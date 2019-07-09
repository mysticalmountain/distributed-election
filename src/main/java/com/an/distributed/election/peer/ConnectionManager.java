package com.an.distributed.election.peer;

import com.an.distributed.election.core.PeerFactory;
import com.an.distributed.election.network.MessageDecoder;
import com.an.distributed.election.network.MessageEncoder;
import com.an.distributed.election.network.PeerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ConnectionManager
 * @Description ConnectionManager
 * @Author an
 * @Date 2019/7/8 下午5:54
 * @Version 1.0
 */
public class ConnectionManager {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private PeerFactory peerFactory;
    private Map<String, ChannelFuture> channelFutures = new HashMap<>();


    public ConnectionManager(PeerFactory peerFactory) {
        this.peerFactory = peerFactory;
    }

    public void connect(String id, String ip, int port) throws InterruptedException {
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
                        pipeline.addLast("PeerHandler", new PeerHandler(peerFactory));
                    }
                });

        ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.info("Connect to {} port {}", ip, port);
                    channelFutures.put(id, channelFuture);
                } else {
                    logger.error("Connect to {} port {} error", ip, port);
                }
            }
        });
    }

    public void disConnect(String id) {
        ChannelFuture channelFuture = channelFutures.get(id);
        logger.info("{} {} ", channelFuture.channel().isOpen(), channelFuture.channel().isActive());
        channelFuture.channel().disconnect();
        logger.info("{} {} ", channelFuture.channel().isOpen(), channelFuture.channel().isActive());
    }
}
