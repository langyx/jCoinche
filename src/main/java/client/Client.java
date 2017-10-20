package client;

import com.sun.org.apache.bcel.internal.generic.RET;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import server.CommandType;
import server.Serializer;

public class Client extends Thread{

    String host;
    int port;
    static Channel serverFlux = null;

    public Client(String _host, String _port)
    {
        host = _host;
        port = Integer.parseInt(_port);
    }

    public void run()
    {
        try
        {
            this.connect();
        }catch (Exception e)
        {
            System.out.println("Unable to connect");
        }

    }

    public static Channel getServerFlux()
    {
        return Client.serverFlux;
    }

    public void connect() throws Exception
    {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ClientHandler());
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            Client.serverFlux = f.channel();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }

    }

    public static void writeMessage(Channel client, String message)
    {
        if (client == null)
        {
            System.out.println("Wait For Connection Please!");
            return;
        }
        else
        {
            client.writeAndFlush(Unpooled.wrappedBuffer(Serializer.serialize(message, CommandType.Info)));
        }
    }

    public static void main(String[] args)  {
        if (args.length < 2 || !Client.isParsable(args[1]))
        {
            System.out.println("Usage : <host> <port> !");
            return;
        }
        Client mainClient = new Client(args[0], args[1]);
        mainClient.start();

        UserInput mainInput = new UserInput();
        mainInput.start();
    }

    public static boolean isParsable(String input){
        boolean parsable = true;
        try{
            Integer.parseInt(input);
        }catch(NumberFormatException e){
            parsable = false;
        }
        return parsable;
    }
}
