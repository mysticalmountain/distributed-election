package com.an.distributed.election.peer;

import com.an.distributed.election.core.PeerFactory;
import com.an.distributed.election.network.MessageDecoder;
import com.an.distributed.election.network.MessageEncoder;
import com.an.distributed.election.network.PeerHandler;
import com.google.common.util.concurrent.SettableFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName ConnectionFactory
 * @Description ConnectionFactory
 * @Author an
 * @Date 2019/7/9 下午2:39
 * @Version 1.0
 */
public class ConnectionFactory {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private PeerFactory peerFactory;
    private static Map<String, ChannelFuture> channelFutures = new HashMap<>();
    private SettableFuture<Connection> connectionSettableFuture = SettableFuture.create();


    private ConnectionFactory(PeerFactory peerFactory) {
        this.peerFactory = peerFactory;
    }

    public static ConnectionFactory getInstance(PeerFactory peerFactory) {
        return new ConnectionFactory(peerFactory);
    }

    public Connection connect(String id, String ip, int port) throws InterruptedException, ExecutionException {
        PeerHandler peerHandler = new PeerHandler(peerFactory);
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("MessageDecoder", new MessageDecoder());
                        pipeline.addLast("MessageEncoder", new MessageEncoder());
                        pipeline.addLast("PeerHandler", peerHandler);
                    }
                });

        ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();

        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.info("Connect to {} port {}", ip, port);
                    connectionSettableFuture.set(new Connection(peerHandler));
                    channelFutures.put(id, channelFuture);

                } else {
                    logger.error("Connect to {} port {} error", ip, port);
                    connectionSettableFuture.set(null);
                }
            }
        });
        return connectionSettableFuture.get();
    }

    public void disConnect(String id) {
        ChannelFuture channelFuture = channelFutures.get(id);
        if (channelFuture == null) {
            logger.info("The connection of {} not found", id);
        } else if (channelFuture.channel().isOpen()) {
            channelFuture.channel().disconnect();
            logger.info("The connection of {} is closed", id);
        } else {
            logger.info("The connection of {} had closed", id);
        }
    }

}
