import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

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
        Player newPlayer = new Player(Tools.randomString(20), ctx);

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
        }
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


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
         ByteBuf in = (ByteBuf) msg;
        try {
            while (in.isReadable()) { // (1)
                System.out.print((char) in.readByte());
                System.out.flush();
            }
        } finally {
            ReferenceCountUtil.release(msg); // (2)
        }
      /*  String mess = "dddd\n";
        byte[] bittt = mess.getBytes();
        System.out.println(ctx.channel().writeAndFlush(Unpooled.wrappedBuffer(bittt)));*/
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}