import io.netty.channel.Channel;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

import java.net.SocketAddress;

import static sun.swing.MenuItemLayoutHelper.max;


/*

MAP is design like this :
                    3      2      1      0
END PLI <<<<==== [P2T2] [P2T1] [P1T2] [P1T1] <<<==== START PLI (1 tableCycle)

 */

public class GameEngine extends Thread
{
    protected int playerTurn = 0;
    protected int tableCycle = 0;

    public void run()
    {

        while (true)
        {

            try {
                this.gamingCore();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public GameEngine()  {

    }

    private boolean initDeckPlayerWithMainDeck(Player player)
    {
        for (int i = 0; i < player.getDeck().length; i +=  1)
        {
            player.getDeck()[i] = null;
            if (player.addCard(Server.mainTable.PickRandomCardInMainDeck()) == false)
                return false;
        }
        return  true;
    }

    protected void gamingCore() throws InterruptedException {
        while (!this.isTheGamePlayable())
        {
            //Server.writeMessageForAllPlayer("[Server] Waiting for full team\n");
            sleep(1000);
        }

        switch (Server.mainTable.getState())
        {
            case Init: //Distribution des cards aux players

                boolean distribState = this.DistributeCardsForPlayers();

                if (distribState)
                     Server.mainTable.setState(GameState.Bet);
                break;

            case Bet:
                /* All manage in ServerHandler */

                /* Si les paris s'éternise sur plus de 3 tours */
                if (this.getTableCycle() == 3)
                    Server.mainTable.setState(GameState.BetTraitement);
                break;

            case BetTraitement:
                Team attackingTeam = this.getBestBetTeam(false);
                Team defenseTeam = this.getBestBetTeam(true);

                defenseTeam.setBet(0);
                defenseTeam.setCoinche(0);

                Server.mainTable.setAtout(attackingTeam.getBetFamily());

                Server.writeMessageForAllPlayer("[Server] Start Game ! Atout is [" + attackingTeam.getBetFamily().toString() + "]\n");

                Server.mainTable.setState(GameState.Pli);
                break;

            case Waitting:
                break;

            case Pli:
                if (Server.gameEngigne.getPlayerTurn() == 3) {
                    while (Server.gameEngigne.getPlayerTurn() != 0)
                    {
                        try {
                            sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    int sumRoundPoint = Server.mainTable.getSumCardDroppedPli();
                    Server.writeMessageForAllPlayer("[Server] Round ended by " + sumRoundPoint + " points \n");
                    Server.mainTable.getTeamOfPlayer(Server.mainTable.getWinningCardPlayer().getChannel()).addScore(sumRoundPoint);
                    Server.mainTable.setSumCardDroppedPli(0);
                    Server.mainTable.initMidDeck();
                    Server.mainTable.setWinningCard(null);
                    Server.mainTable.setWinningCardPlayer(null);

                    if (Server.countArray(Server.mainTable.getTeams()[0].getPlayers()[0].getDeck()) == 0)
                    {
                        Team attTeam = this.getBestBetTeam(false);
                        Team defTeam = this.getBestBetTeam(true);
                        Server.mainTable.setState(GameState.Ended);

                        int scoreAtt = attTeam.getScore();
                        int scoreDef = defTeam.getScore();

                        int finalAttScore = 0;
                        int finalDefScore = 0;

                        if (scoreAtt >= attTeam.getBet())
                        {
                            Server.writeMessageForAllPlayer("Attacking team succeeded!\n");
                            finalAttScore = scoreAtt + (attTeam.getBet() * attTeam.getCoinche());
                            finalDefScore = scoreDef;
                        }
                        else
                        {
                            Server.writeMessageForAllPlayer("Attacking team failed!\n");
                            finalDefScore = scoreDef + (attTeam.getBet() * attTeam.getCoinche());
                        }

                        Server.writeMessageForAllPlayer("Round Score : [Att] + " + finalAttScore + " - " + finalDefScore + " [Def]\n");

                        this.getBestBetTeam(false).setGameScore(finalAttScore);
                        this.getBestBetTeam(true).setGameScore(finalDefScore);

                        Server.writeMessageForAllPlayer("Game Score : [Att] + "
                                + this.getBestBetTeam(false).getGameScore() + " - "
                                + this.getBestBetTeam(true).getGameScore() + " [Def]\n");

                        Server.mainTable.setState(GameState.Ended);
                    }
                }
                break;

            case Ended:

                break;

            default:
                break;
        }

        //System.out.println("Game is runnig");

    }

    public int getBestBet()
    {
        int currentTeamOneBet = Server.mainTable.getTeams()[0].getBet();
        int currentTeamTwoBet = Server.mainTable.getTeams()[1].getBet();
        int currentBestBet = max(currentTeamOneBet, currentTeamTwoBet);
        return currentBestBet;
    }

    public Team getBestBetTeam(boolean inversed)
    {
        if (this.getBestBet() == Server.mainTable.getTeams()[0].getBet())
            return inversed ? Server.mainTable.getTeams()[1] :  Server.mainTable.getTeams()[0];
        else
            return inversed ? Server.mainTable.getTeams()[0] :  Server.mainTable.getTeams()[1];

    }

    public boolean DistributeCardsForPlayers()
    {
        boolean distribState = true;

        if (!initDeckPlayerWithMainDeck(Server.mainTable.getTeams()[0].getPlayers()[0])) distribState = false;
        if (!initDeckPlayerWithMainDeck(Server.mainTable.getTeams()[0].getPlayers()[1])) distribState = false;
        if (!initDeckPlayerWithMainDeck(Server.mainTable.getTeams()[1].getPlayers()[0])) distribState = false;
        if (!initDeckPlayerWithMainDeck(Server.mainTable.getTeams()[1].getPlayers()[1])) distribState = false;

        String distribStateMessage = "a";
        if (distribState == false)
            distribStateMessage = "[Server] Cards distribution failed !\n";
        else
            distribStateMessage = "[Server] Cards distribution done !\n";

        System.out.println(distribStateMessage);
        Server.writeMessageForAllPlayer(distribStateMessage);
        return distribState;
    }

    public void GoNextPlayerTurn()
    {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (this.playerTurn == 3)
        {
            this.playerTurn = 0;
            this.tableCycle += 1;
        }
        else
            this.playerTurn += 1;
    }

    public void GoBackPlayerTurn()
    {
        if (this.playerTurn == 0)
            this.playerTurn = 3;
        else
            this.playerTurn -= 1;
    }

    public boolean isThePlayerCanPlay(Channel player)
    {
        if (this.getPlayerMapPostion(player) == this.playerTurn)
            return true;
        else
            return false;
    }

    public boolean isTheGamePlayable()
    {
        Team tableTeams[] = Server.mainTable.getTeams();

        if (Server.countArray(tableTeams[0].getPlayers()) == 2
                && Server.countArray(tableTeams[1].getPlayers()) == 2)
            return true;
        else
            return false;
    }

    public int getPlayerMapPostion(Channel playerChan)
    {
        SocketAddress playerSock =  playerChan.remoteAddress();
        if (Server.mainTable.getTeams()[0].getPlayers()[0].getChannel().remoteAddress() == playerSock)
            return 0;
        else if (Server.mainTable.getTeams()[0].getPlayers()[1].getChannel().remoteAddress() == playerSock)
            return 2;
        else if (Server.mainTable.getTeams()[1].getPlayers()[0].getChannel().remoteAddress() == playerSock)
            return 1;
        else if (Server.mainTable.getTeams()[1].getPlayers()[1].getChannel().remoteAddress() == playerSock)
            return 3;
        else
            return -1;
    }

    public Player getPlayerByMapPosition(int mapPos)
    {
        switch (mapPos)
        {
            case 0:
                return Server.mainTable.getTeams()[0].getPlayers()[0];
            case 1:
                return Server.mainTable.getTeams()[1].getPlayers()[0];
            case 2:
                return Server.mainTable.getTeams()[0].getPlayers()[1];
            case 3:
                return Server.mainTable.getTeams()[1].getPlayers()[1];
            default:
                return null;

        }
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public int getTableCycle() {
        return tableCycle;
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }

    public void setTableCycle(int tableCycle) {
        this.tableCycle = tableCycle;
    }
}
