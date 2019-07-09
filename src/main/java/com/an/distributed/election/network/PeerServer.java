package com.an.distributed.election.network;

import com.an.distributed.election.core.PeerFactory;
import com.an.distributed.election.peer.ConnectionFactory;
import com.an.distributed.election.peer.Context;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @ClassName PeerServer
 * @Description PeerServer
 * @Author an
 * @Date 2019/4/22 上午9:53
 * @Version 1.0
 */
public class PeerServer extends AbstractExecutionThreadService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private PeerFactory peerFactory;
    private Context context;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    public PeerServer(PeerFactory peerFactory, Context context) {
        this.context = context;
        this.peerFactory = peerFactory;
    }


    @Override
    protected void run() throws Exception {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("MessageDecoder", new MessageDecoder());
                            pipeline.addLast("MessageEncoder", new MessageEncoder());
                            pipeline.addLast("PeerHandler", new PeerHandler(peerFactory));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(context.getLocalPort()).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        logger.info("Server start at {} port", context.getLocalPort());
                        context.getEventBus().post(new Context.StartedEvent());
                    } else {
                        logger.info("Server start failed");
                    }
                }
            });
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
