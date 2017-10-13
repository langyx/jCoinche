import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static Table mainTable;
    public static List<Player> playerQueue;

    public Server()
    {
        this.mainTable = new Table();
        this.playerQueue = new ArrayList<>();
    }

    public static void main(String[] args) throws Exception {

        Server ServerMain = new Server();

        DiscardServer handlerThread =  new DiscardServer(4444);
        handlerThread.start();

    }

    public static int countArray(Object[] data)
    {
        int count = 0;

        for (int i = 0; i < data.length; i += 1)
        {
            if (data[i] != null)
            {
                count += 1;
            }
        }
     return count;
    }

    public static void writeMessage(Channel client, String message)
    {
        byte[] bytes = message.getBytes();
        System.out.println(client.writeAndFlush(Unpooled.wrappedBuffer(bytes)));
    }

    public static int getQueueIndex(Channel channel)
    {
        for (int i = 0; i < Server.playerQueue.size(); i += 1)
        {
            Player temp = Server.playerQueue.get(i);
            if (temp.getChannel().remoteAddress() == channel.remoteAddress())
            {
                return i;
            }
        }
        return -1;
    }

    public static void showTablePlayers()
    {
        Team teamOne = Server.mainTable.getTeams()[0];
        Team teamTwo = Server.mainTable.getTeams()[1];

        System.out.println("TeamOne :::");
        for (int i = 0; i < teamOne.getPlayers().length; i += 1)
        {
            if (teamOne.getPlayers()[i] != null)
            {
                System.out.println("Player : [" + teamOne.getPlayers()[i].getName() +
                        "] on :" + teamOne.getPlayers()[i].getChannel().remoteAddress());
            }
            else
                System.out.println("Player : No");
        }

        System.out.println("TeamTwo :::");
        for (int i = 0; i < teamTwo.getPlayers().length; i += 1)
        {
            if (teamTwo.getPlayers()[i] != null)
            {
                System.out.println("Player : [" + teamTwo.getPlayers()[i].getName() +
                        "] on :" + teamTwo.getPlayers()[i].getChannel().remoteAddress());
            }
            else
                System.out.println("Player : No");
        }


        System.out.println("\nPlayer Queue :::");
        for (int i = 0; i < Server.playerQueue.size(); i += 1)
        {
            Player waitingPlayer = Server.playerQueue.get(i);
            System.out.println("Waiter : " + waitingPlayer.getChannel().remoteAddress());
        }
    }
}
