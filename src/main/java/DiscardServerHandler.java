import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.UnsupportedEncodingException;

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

    public void manageNewPlayer(Channel ctx)
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

       // System.out.println("=" + command[0] + "=");
        switch (command[0].toLowerCase())
        {
            case "name":
                if (command.length == 2)
                    Server.getPlayerByChannel(channel).setName(command[1]);
                else
                    Server.writeMessage(channel, "[Server] Command Bad Arguments\n");
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

                                        break;

                                    case "surcoinche":

                                        break;

                                    case "passe":
                                        Server.writeMessageForAllPlayer("[Bet] [" + currPlayer.getName() + "] Passe\n");
                                        Server.gameEngigne.GoNextPlayerTurn();
                                        break;

                                    default:
                                        break;
                                }
                                break;
                            case 3: /* case of betting on color Form : <family> <amount> */

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

            default:
                Server.writeMessage(channel, "[Server] Command Unkown\n");
                break;
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
         ByteBuf in = (ByteBuf) msg;
         byte[] msg_rcv =  ByteBufUtil.getBytes(in);

         try {
             String msg_txt = new String(msg_rcv, "UTF-8");
             this.manageCommand(ctx.channel(), msg_txt.replace("\n", ""));
         }catch (UnsupportedEncodingException e) {
             e.printStackTrace();
         }

        ReferenceCountUtil.release(msg);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}