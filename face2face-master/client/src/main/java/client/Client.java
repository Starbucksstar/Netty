package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.ParseRegistryMap;
import protobuf.Utils;
import protobuf.code.PacketDecoder;
import protobuf.code.PacketEncoder;
import protobuf.generate.cli2srv.chat.Chat;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * Created by Dell on 2016/2/15.
 * Simple client for module test
 */
public class Client {
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "9099"));

    public static final int clientNum = Integer.parseInt(System.getProperty("size", "10"));

    public static final int frequency = 3000;  //ms

    private static final int userId = 8888;


    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws Exception {
        beginPressTest();
    }

    public static void beginPressTest() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();

                        p.addLast("MessageDecoder", new PacketDecoder());
                        p.addLast("MessageEncoder", new PacketEncoder());
                        p.addLast(new ClientHandler());
                    }
                });
        ChannelFuture channelFuture = b.connect(new InetSocketAddress(HOST, PORT));
        startConnection(channelFuture, userId);
    }


    /**
     * 启动客户端通道监听
     *
     * @param channelFuture--通道操作
     * @param index--客户端id
     */
    private static void startConnection(ChannelFuture channelFuture, int index) {
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future)
                    throws Exception {
                if (future.isSuccess()) {
                    //init registry
                    ParseRegistryMap.initRegistry();
                    logger.info("Client[{}] connected Gate Successed...", index);
                } else {
                    logger.error("Client[{}] connected Gate Failed", index);
                }
            }
        });
    }
}

