package com.an.distributed.election;

import com.an.distributed.election.core.Message;
import com.an.distributed.election.core.Peer;
import com.an.distributed.election.core.PeerFactory;
import com.an.distributed.election.message.Election;
import com.an.distributed.election.message.Ok;
import com.an.distributed.election.message.Ping;
import com.an.distributed.election.message.Pong;
import com.an.distributed.election.peer.BullyPeer;
import com.an.distributed.election.peer.Connection;
import com.an.distributed.election.peer.ConnectionFactory;
import com.an.distributed.election.peer.Context;

import java.net.InetAddress;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName ClientTest
 * @Description ClientTest
 * @Author an
 * @Date 2019/7/9 下午2:50
 * @Version 1.0
 */
public class ClientTest {


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int id = 5001;
        int port = 5001;
        Context context = new Context();
        context.setLocalId(id);
        context.setLocalPort(port);
        PeerFactory peerFactory = new PeerFactory() {
            @Override
            public Peer get(InetAddress address, int prot) {
                BullyPeer peer = new BullyPeer(context);
                return peer;
            }
        };

        ConnectionFactory connectionFactory = ConnectionFactory.getInstance(peerFactory);
        Connection connection = connectionFactory.connect(String.valueOf(id), "127.0.0.1", port);


        Random random = new Random();
        Ping ping = new Ping(random.nextInt(9999));
        Message.Header header = new Message.Header(Message.magic, ping.getCommand(), System.currentTimeMillis(), ping.getLength());
        ping.setHeader(header);
        Pong pong = connection.send(ping, context, Pong.class);
        System.out.println("------------------" + pong);

        Election election = new Election(100);
        header = new Message.Header(Message.magic, election.getCommand(), System.currentTimeMillis(), election.getLength());
        election.setHeader(header);
        Ok ok = connection.send(election, context, Ok.class);
        System.out.println("------------------" + ok);

        connectionFactory.disConnect(String.valueOf(id));
    }
}
