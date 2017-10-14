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
        if (Server.mainTable.getState() == GameState.Init || Server.mainTable.getState() == GameState.Bet)
        {
            Server.mainTable.setState(GameState.Bet);


        }

        System.out.println("Game is runnig");

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
            return 1;
        else if (Server.mainTable.getTeams()[1].getPlayers()[0].getChannel().remoteAddress() == playerSock)
            return 2;
        else if (Server.mainTable.getTeams()[1].getPlayers()[1].getChannel().remoteAddress() == playerSock)
            return 3;
        else
            return -1;
    }
}
