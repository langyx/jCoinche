package server;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

public class Server {

    public static Table mainTable;
    public static List<Player> playerQueue;

    protected static GameEngine gameEngigne;

    public static GameGraphic gameGraphic;

    public Server()
    {
        this.mainTable = new Table();
        this.playerQueue = new ArrayList<>();
        this.gameEngigne = new GameEngine();
        this.gameGraphic = new GameGraphic();

    }


    public static void main(String[] args) throws Exception {

        Server ServerMain = new Server();

        DiscardServer handlerThread =  new DiscardServer(4444);
        handlerThread.start();

        ServerMain.gameEngigne.start();

        ServerMain.gameGraphic.start();

    }

    public static GameEngine getGameEngigne() {
        return gameEngigne;
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

    public static void writeMessageForAllPlayer(String message)
    {
        byte[] bytes = Serializer.serialize(message, CommandType.Info);

        Team tableTeams[] = Server.mainTable.getTeams();

        for (int i = 0; i < tableTeams[0].getPlayers().length; i += 1)
        {
            if (tableTeams[0].getPlayers()[i] != null)
                tableTeams[0].getPlayers()[i].getChannel().writeAndFlush(Unpooled.wrappedBuffer(bytes));
        }

        for (int i = 0; i < tableTeams[1].getPlayers().length; i += 1)
        {
            if (tableTeams[1].getPlayers()[i] != null)
                tableTeams[1].getPlayers()[i].getChannel().writeAndFlush(Unpooled.wrappedBuffer(bytes));
        }

        for (int i = 0; i < Server.playerQueue.size(); i += 1)
        {
            Player tempPlayer = Server.playerQueue.get(i);
            tempPlayer.getChannel().writeAndFlush(Unpooled.wrappedBuffer(bytes));
        }

    }

    public static void writeMessage(Channel client, String message)
    {
       client.writeAndFlush(Unpooled.wrappedBuffer(Serializer.serialize(message, CommandType.Info)));
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

    public static Player getPlayerByChannel(Channel channel)
    {
        Team teamOne = Server.mainTable.getTeams()[0];
        Team teamTwo = Server.mainTable.getTeams()[1];


        for (int i = 0; i < teamOne.getPlayers().length; i += 1)
        {
            if (teamOne.getPlayers()[i] != null
                    && teamOne.getPlayers()[i].getChannel().remoteAddress() == channel.remoteAddress())
                return teamOne.getPlayers()[i];
        }

        for (int i = 0; i < teamTwo.getPlayers().length; i += 1)
        {
            if (teamTwo.getPlayers()[i] != null
                    && teamTwo.getPlayers()[i].getChannel().remoteAddress() == channel.remoteAddress())
                return teamTwo.getPlayers()[i];
        }

        for (int i = 0; i < Server.playerQueue.size(); i += 1)
        {
            Player waitingPlayer = Server.playerQueue.get(i);
            if (waitingPlayer.getChannel().remoteAddress() == channel.remoteAddress())
                return waitingPlayer;
        }

        return null;
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
                System.out.println("server.Player : [" + teamOne.getPlayers()[i].getName() +
                        "] on :" + teamOne.getPlayers()[i].getChannel().remoteAddress());
            }
            else
                System.out.println("server.Player : No");
        }

        System.out.println("TeamTwo :::");
        for (int i = 0; i < teamTwo.getPlayers().length; i += 1)
        {
            if (teamTwo.getPlayers()[i] != null)
            {
                System.out.println("server.Player : [" + teamTwo.getPlayers()[i].getName() +
                        "] on :" + teamTwo.getPlayers()[i].getChannel().remoteAddress());
            }
            else
                System.out.println("server.Player : No");
        }


        System.out.println("\nserver.Player Queue :::");
        for (int i = 0; i < Server.playerQueue.size(); i += 1)
        {
            Player waitingPlayer = Server.playerQueue.get(i);
            System.out.println("Waiter : " + waitingPlayer.getChannel().remoteAddress());
        }
    }
}
