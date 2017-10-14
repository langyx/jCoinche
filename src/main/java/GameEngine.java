import io.netty.channel.Channel;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

import java.net.SocketAddress;


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

        /*
            Cas ou on commence le pli ou viens de lancer le server
            On passe en état de paris (BET)
         */

        switch (Server.mainTable.getState())
        {
            case Init: //Distribution des cards aux players

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

                if (distribState)
                     Server.mainTable.setState(GameState.Bet);
                break;

            case Bet:
                break;

            case Waitting:
                break;

            case Pli:
                break;

            default:
                break;
        }

        //System.out.println("Game is runnig");

    }

    public void GoNextPlayerTurn()
    {
        if (this.playerTurn == 3)
            this.playerTurn = 0;
        else
            this.playerTurn += 1;
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
}
