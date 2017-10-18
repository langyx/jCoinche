import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;

import static sun.swing.MenuItemLayoutHelper.max;

/**
 * Handles a server-side channel.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter { // (1)

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.manageNewPlayer(ctx.channel());

        Server.showTablePlayers();
    }

    private void manageNewPlayer(Channel ctx)
    {
        Player newPlayer = new Player(Tools.randomPlayerName(), ctx);

        int countPlayerTeamOne = Server.countArray(Server.mainTable.getTeams()[0].getPlayers());
        int countPlayerTeamTwo = Server.countArray(Server.mainTable.getTeams()[1].getPlayers());

        if (countPlayerTeamOne < 2)
            Server.mainTable.getTeams()[0].setNewPlayer(newPlayer, Server.mainTable.getTeams()[0].getFirstFreePlayerIndex());
        else if (countPlayerTeamTwo < 2)
            Server.mainTable.getTeams()[1].setNewPlayer(newPlayer, Server.mainTable.getTeams()[1].getFirstFreePlayerIndex());
        else if (Server.playerQueue.size() < 2)
            Server.playerQueue.add(newPlayer);
        else
        {
            Server.writeMessage(ctx, "Server & Queue Full");
            ctx.close();
            return;
        }
        Server.writeMessage(ctx, "[Server] Welcome " + newPlayer.getName() + "\n");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.write("An error of conenxion occured... Closing connexion...").addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (Server.mainTable.getTeams()[0].removePlayer(ctx.channel())
                || Server.mainTable.getTeams()[1].removePlayer(ctx.channel())) {
            if (Server.playerQueue.size() > 0) {
                Player subPlayerFromQueue = Server.playerQueue.get(0);
                manageNewPlayer(subPlayerFromQueue.getChannel());
                Server.playerQueue.remove(0);
            }
            System.out.println("Client : " + ctx.channel().remoteAddress() + " has left !");
        } else {
            int qeueIndex = Server.getQueueIndex(ctx.channel());
            if (qeueIndex != -1)
                Server.playerQueue.remove(qeueIndex);
        }

        Server.showTablePlayers();
    }


    public void manageCommand(Channel channel, String message)
    {
        String[] command = message.split(" ");
        Player currPlayer = Server.getPlayerByChannel(channel);
        Team playerTeam = Server.mainTable.getTeamOfPlayer(currPlayer.getChannel());


        // System.out.println("=" + command[0] + "=");
        switch (command[0].toLowerCase())
        {
            case "name":
                if (command.length == 2)
                    Server.getPlayerByChannel(channel).setName(command[1]);
                else
                    Server.writeMessage(channel, "[Server] Command Bad Arguments\n");
                break;

            case "hand":
                Server.writeMessage(channel, currPlayer.getFormatedDeck());
                break;

            case "table":
                Server.writeMessage(channel, Server.mainTable.getFormattedMidDeck());
                break;

            case "bet" :
                if (Server.mainTable.getState() == GameState.Bet && Server.getGameEngigne().isTheGamePlayable())
                {
                    if (Server.getGameEngigne().isThePlayerCanPlay(channel))
                    {
                        switch (command.length)
                        {
                            case 2: /* case of simple action */
                                switch (command[1].toLowerCase())
                                {
                                    case "coinche":
                                        if (playerTeam.isCoinched())
                                        {
                                            Server.writeMessage(channel, "[Bet] You're coinched : Passe Or Surcoinche\n");
                                            break;
                                        }
                                        int currentTeamOneBet = Server.mainTable.getTeams()[0].getBet();
                                        int currentTeamTwoBet = Server.mainTable.getTeams()[1].getBet();
                                        int currentBestBet = max(currentTeamOneBet, currentTeamTwoBet);
                                        Team bestBetTeam = null;

                                        /* On vérifie qu'une des team à bet */
                                        if (currentBestBet == 0)
                                        {
                                            Server.writeMessage(channel, "[Bet] No bet to coinche\n");
                                            break;
                                        }

                                        if (currentBestBet == currentTeamOneBet)
                                            bestBetTeam = Server.mainTable.getTeams()[0];
                                        else
                                            bestBetTeam = Server.mainTable.getTeams()[1];

                                        /* On vérifie que le meilleur paris ne soit pas l'équipe du joueur voulant coincher */
                                        SocketAddress playerAdress = currPlayer.getChannel().remoteAddress();
                                        if (playerAdress == bestBetTeam.getPlayers()[0].getChannel().remoteAddress()
                                                || playerAdress == bestBetTeam.getPlayers()[1].getChannel().remoteAddress())
                                        {
                                            Server.writeMessage(channel, "[Bet] No bet to coinche (You team are highest Bet)\n");
                                            break;
                                        }

                                        bestBetTeam.setCoinche(2);
                                        Server.gameEngigne.GoBackPlayerTurn();

                                        Player lastPlayer = Server.gameEngigne.getPlayerByMapPosition(Server.gameEngigne.getPlayerTurn());
                                        Server.writeMessageForAllPlayer("[Bet] [" + currPlayer.getName() + "] Coinche\n");
                                        Server.writeMessageForAllPlayer("[Bet] " + lastPlayer.getName() + " have to answer (Surcoinche or Passe) !\n");
                                        break;

                                    case "surcoinche":
                                       if (playerTeam.getCoinche() <= 0)
                                        {
                                            Server.writeMessage(channel, "[Bet] You can't surcoinche because nobody coinche you\n");
                                            break;
                                        }

                                        playerTeam.setCoinche(3);

                                        Server.gameEngigne.setTableCycle(0);
                                        Server.gameEngigne.setPlayerTurn(0);
                                        Server.mainTable.setState(GameState.BetTraitement);

                                        break;

                                    case "passe":

                                        Server.writeMessageForAllPlayer("[Bet] [" + currPlayer.getName() + "] Passe\n");
                                        Server.gameEngigne.GoNextPlayerTurn();

                                        /* Redistribution des cartes dans le cas ou tous les joueurs ont passé pendant le tour de bet sans aucune bet placée */
                                        if (Server.gameEngigne.getPlayerTurn() == 0 && Server.gameEngigne.getBestBet() <= 0)
                                        {
                                            Server.mainTable.initMainDeck();
                                            if (Server.gameEngigne.DistributeCardsForPlayers())
                                                Server.gameEngigne.setTableCycle(0);

                                        }
                                        /* Passage au jeu (pli) dans le cas ou le dernier passe alors qu'une bet plus élevé que celui de sa team est placée */
                                        else if (Server.gameEngigne.getPlayerTurn() == 0 && Server.gameEngigne.getBestBet() > playerTeam.getBet())
                                        {
                                            Server.mainTable.setState(GameState.BetTraitement);
                                        }
                                        /* Si le joueur Passe alors que sa Team est coinché (il ne surcoinche pas) */
                                        else if (playerTeam.isCoinched())
                                        {
                                            Server.mainTable.setState(GameState.BetTraitement);
                                        }
                                        break;

                                    default:
                                        Server.writeMessage(channel, "[Bet] Bad argument\n");
                                        break;
                                }
                                break;
                            case 3: /* case of betting on color Form : <family> <amount> */

                                /* Si le player est coinché il ne peut pas remiser */
                                if (playerTeam.isCoinched())
                                {
                                    Server.writeMessage(channel, "[Bet] You're coinched : Passe Or Surcoinche\n");
                                    break;
                                }

                                /* check de la famille de la carte choisie */
                                CardFamily cardFamily = CardFamily.getCardFamilyFromString(command[1]);
                                if (cardFamily == null)
                                {
                                    Server.writeMessage(channel, "[Bet] Bad family arg\n");
                                    break;
                                }

                                /* Check du format du montant de paris */
                                int amount = Tools.tryParse(command[2]);
                                if (amount == -1 || amount % 10 != 0 || amount < 80 || amount > 160)
                                {
                                    Server.writeMessage(channel, "[Bet] Bad amount arg\n");
                                    break;
                                }

                                /* check que le bet proposé est plus grand que celui des deux team */
                                int currentTeamOneBet = Server.mainTable.getTeams()[0].getBet();
                                int currentTeamTwoBet = Server.mainTable.getTeams()[1].getBet();
                                int currentBestBet = max(currentTeamOneBet, currentTeamTwoBet);
                                if (amount <= currentBestBet)
                                {
                                    Server.writeMessage(channel, "[Bet] amount have to be biggest than current best Bet (" + currentBestBet + ")\n");
                                    break;
                                }

                                /* Push du nouveau hight bet */
                                Server.mainTable.getTeamOfPlayer(channel).setBet(amount);
                                Server.mainTable.getTeamOfPlayer(channel).setBetFamily(cardFamily);

                                Server.writeMessageForAllPlayer("[Bet] [" + currPlayer.getName() + "] New hight bet : " + amount + "\n");

                                /* passage au joueur suivant */
                                Server.gameEngigne.GoNextPlayerTurn();

                                break;

                            default:
                                Server.writeMessage(channel, "[Bet] Bad argument\n");
                                break;
                        }
                    }
                    else
                        Server.writeMessage(channel, "[Bet] Is not you turn\n");
                }
                else
                    Server.writeMessage(channel, "[Server] Is no the bet round !\n");
                break;

            case "drop":

                //game.getPlayerMapPostion(channel);

                if (Server.getGameEngigne().isTheGamePlayable() && Server.mainTable.getState() == GameState.Pli) {

                    if (Server.getGameEngigne().isThePlayerCanPlay(channel)) {

                        if (!((command.length == 3) && ((command[1].equalsIgnoreCase(CardName.AS.toString())) ||
                                command[1].equalsIgnoreCase(CardName.Sept.toString()) ||
                                command[1].equalsIgnoreCase(CardName.Huit.toString()) ||
                                command[1].equalsIgnoreCase(CardName.Neuf.toString()) ||
                                command[1].equalsIgnoreCase(CardName.Dix.toString()) ||
                                command[1].equalsIgnoreCase(CardName.Valet.toString()) ||
                                command[1].equalsIgnoreCase(CardName.Dame.toString()) ||
                                command[1].equalsIgnoreCase(CardName.Roi.toString())) &&
                                (command[2].equalsIgnoreCase(CardFamily.Coeur.toString()) ||
                                        command[2].equalsIgnoreCase(CardFamily.Pique.toString()) ||
                                        command[2].equalsIgnoreCase(CardFamily.Trefle.toString()) ||
                                        command[2].equalsIgnoreCase(CardFamily.Carreau.toString())))) {

                            //System.out.print("command1 == "   command[1]   "command 2 === "   command[2]);

                            Server.writeMessage(channel, "[Server] Please enter drop   Cardname   CardFamily\n");

                        } else {

                            //String prout = Server.getPlayerByChannel(channel).getFormatedDeck();

                            //System.out.println("AVANTTTTTTTTT"   prout)

                            if (Server.mainTable.getMidDeck()[0] == null) {
                                if (Server.getPlayerByChannel(channel).searchOnDeck(command[1], command[2])) {
                                    Server.getPlayerByChannel(channel).removeOnDeck(command[1], command[2]);
                                    Server.writeMessage(channel, "[Server] Card Dropped\n");
                                    Server.mainTable.setFirstCardFamily(CardFamily.getCardFamilyFromString(command[2]));
                                    Server.mainTable.setWinningCard(Server.mainTable.getMidDeck()[0]); // CHECK LA MEILLEUR DES ATOUT ET LA DONNER A WINNINGCARD
                                    Server.gameEngigne.GoNextPlayerTurn();
                                    // PLAYER OBLIGE DE METTRE LE MEME TYPE QUE LA CARTE DU MILIEU
                                } else {
                                    System.out.println("You don't have this card or already used, try again !");
                                }

                            } else {
                                // si il a trouvé la carte dans le deck
                                if (Server.getPlayerByChannel(channel).searchOnDeck(command[1], command[2])) {
                                    // Check si la carte que on veut poser est égale à la famille de la premiere
                                    if (Server.mainTable.getFirstCardFamily().toString().equalsIgnoreCase(command[2])) {
                                        //Check si la first card et celle qu'on veut poser sont de la famille de l'atout
                                        if (Server.mainTable.getFirstCardFamily().toString().equalsIgnoreCase(Server.mainTable.getAtout().toString())) {
                                            //si famille de Atout condition de check de valeur
                                            Server.getPlayerByChannel(channel).removeOnDeck(command[1], command[2]);
                                            Server.writeMessage(channel, "[Server] Card Dropped\n");
                                            if (Server.gameEngigne.getPlayerTurn() < 4) {
                                                //Server.mainTable.setWinningCard();
                                                if (Server.mainTable.checkValueAtout(Server.mainTable.getWinningCard().getFamilyName().toString(), Server.mainTable.getMidDeck()[Server.gameEngigne.getPlayerTurn()].getFamilyName().toString()) == Server.mainTable.getMidDeck()[Server.gameEngigne.getPlayerTurn()].getFamilyName().toString())
                                                    Server.mainTable.setWinningCard(Server.mainTable.getMidDeck()[Server.gameEngigne.getPlayerTurn()]);
                                            }
                                            if (Server.gameEngigne.getPlayerTurn() == 3) {
                                                Server.writeMessage(channel, "[Server] Fin de premiere manche\n");
                                                //Server.gameEngigne.setPlayerTurn(0);
                                                Server.mainTable.initMidDeck();
                                                Server.mainTable.setWinningCard(null);
                                            }
                                        } else {
                                            Server.getPlayerByChannel(channel).removeOnDeck(command[1], command[2]);
                                            Server.writeMessage(channel, "[Server] Card Dropped\n");
                                            if (Server.gameEngigne.getPlayerTurn() < 4) {
                                                //Server.mainTable.setWinningCard();
                                                System.out.println(Server.gameEngigne.getPlayerTurn());
                                                if (Server.mainTable.checkValueNonAtout(Server.mainTable.getWinningCard().getFamilyName().toString(), Server.mainTable.getMidDeck()[Server.gameEngigne.getPlayerTurn()].getFamilyName().toString()) == Server.mainTable.getMidDeck()[Server.gameEngigne.getPlayerTurn()].getFamilyName().toString())
                                                    Server.mainTable.setWinningCard(Server.mainTable.getMidDeck()[Server.gameEngigne.getPlayerTurn()]);
                                            }
                                            if (Server.gameEngigne.getPlayerTurn() == 3) {
                                                Server.writeMessage(channel, "[Server] Fin de premiere manche\n");
                                                //Server.gameEngigne.setPlayerTurn(0);
                                                Server.mainTable.initMidDeck();
                                                Server.mainTable.setWinningCard(null);
                                            }
                                        }
                                        Server.gameEngigne.GoNextPlayerTurn();
                                    } else // si la carte qu'on veut poser est pas égale a la famille de la premiere donc peut etre pas égale a l'atout
                                    // SI egal a atout et egal a family ca va pas rentrer dedans
                                    //si egal a atout mais pas égal a family , rentre ici !
                                    {
                                        // Si il a trouvé une carte de la famille de la 1ere card
                                        if (Server.getPlayerByChannel(channel).searchFirstcardFamily(Server.mainTable.getFirstCardFamily().toString())) {
                                            System.out.println("You have a " +  Server.mainTable.getWinningCard().getFamilyCard().toString()  + " in your deck, play it");
                                            Server.writeMessage(channel, "You have a "  +  Server.mainTable.getWinningCard().getFamilyCard().toString()  +  " in your deck, play it\n");
                                        }
                                        else {
                                            //check si il pisse de l'atout ou pas ( SI C PAS DE LA MEME FAMILLE MAIS ATOUT )
                                            if (command[2].equalsIgnoreCase(Server.mainTable.getAtout().toString()) && (Server.getPlayerByChannel(channel).searchFirstcardFamily(Server.mainTable.getFirstCardFamily().toString()) == false)) {
                                                // if winning card est atout check value des deux
                                                // if winning card != atout
                                                //wining card == command[2]
                                                // SI LA WINNING =
                                                if (Server.mainTable.getWinningCard().getFamilyCard().toString().equalsIgnoreCase(Server.mainTable.getAtout().toString())) {
                                                    System.out.println("command1 = "  + command[1]  + " command 2" +  command[2]);
                                                    Server.getPlayerByChannel(channel).removeOnDeck(command[1], command[2]);
                                                    if (Server.gameEngigne.getPlayerTurn() < 4) {
                                                        //Server.mainTable.setWinningCard();
                                                        if (Server.mainTable.checkValueAtout(Server.mainTable.getWinningCard().getFamilyName().toString(), Server.mainTable.getMidDeck()[Server.gameEngigne.getPlayerTurn()].getFamilyName().toString()) == Server.mainTable.getMidDeck()[Server.gameEngigne.getPlayerTurn()].getFamilyName().toString())
                                                            Server.mainTable.setWinningCard(Server.mainTable.getMidDeck()[Server.gameEngigne.getPlayerTurn()]);
                                                    }
                                                    if (Server.gameEngigne.getPlayerTurn() == 3) {
                                                        System.out.println("FIN DE LA MANCHE");
                                                        Server.writeMessage(channel, "[Server] Fin de la manche\n");
                                                        //Server.gameEngigne.setPlayerTurn(0);
                                                        Server.mainTable.initMidDeck();
                                                        Server.mainTable.setWinningCard(null);
                                                    }
                                                }
                                                else { // LE CAS OU JE COUPE AVEC DE LATOUT
                                                    Server.getPlayerByChannel(channel).removeOnDeck(command[1], command[2]);
                                                    Server.mainTable.setWinningCard(Server.mainTable.getMidDeck()[Server.gameEngigne.getPlayerTurn()]);
                                                }
                                            }
                                            else // LE CAS OU JE JETTE JUSTE DE LA MERDE SANS ATOUT SANS RIEN
                                            {
                                                Server.getPlayerByChannel(channel).removeOnDeck(command[1], command[2]);
                                            }
                                            Server.gameEngigne.GoNextPlayerTurn();
                                        }
                                    }
                                    //Server.gameEngigne.GoNextPlayerTurn();
                                }
                                else {
                                    Server.writeMessage(channel,"[Server] You don't have this card or already used, try again !\n");
                                }

                            }

                        }

                    } else {

                        Server.writeMessage(channel,"[Server] You can't drop ! Not your turn\n");

                    }
                }

                break;


            default:
                Server.writeMessage(channel, "[Server] Command Unkown\n");
                break;
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
         ByteBuf in = (ByteBuf) msg;
         byte[] msg_rcv =  ByteBufUtil.getBytes(in);

         String msg_txt = Serializer.deserialize(msg_rcv);
        this.manageCommand(ctx.channel(), msg_txt.replace("\n", ""));


        ReferenceCountUtil.release(msg);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}