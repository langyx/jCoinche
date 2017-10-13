import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

public class GameEngine extends Thread
{
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
            Server.writeMessageForAllPlayer("[Server] Waiting for full team\n");
            sleep(1000);
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
}
