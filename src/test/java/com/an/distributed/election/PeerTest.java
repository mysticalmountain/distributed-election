package com.an.distributed.election;

import com.an.distributed.election.core.Peer;
import com.an.distributed.election.core.PeerFactory;
import com.an.distributed.election.network.PeerServer;
import com.an.distributed.election.peer.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

import static java.lang.System.out;

/**
 * @ClassName PeerTest
 * @Description PeerTest
 * @Author an
 * @Date 2019/7/4 下午10:38
 * @Version 1.0
 */
public class PeerTest {

    private PeerServer peer;
    private PeerFactory peerFactory;

    public void startPeer(int id, int port) {
        Context context = new Context();
        context.addPeerBean(new PeerBean(5001, "127.0.0.1", 5001));
        context.addPeerBean(new PeerBean(5002, "127.0.0.1", 5002));
        context.addPeerBean(new PeerBean(5003, "127.0.0.1", 5003));
        context.setLocalId(id);
        context.setLocalPort(port);
         peerFactory = new PeerFactory() {
            @Override
            public Peer get(InetAddress address, int prot) {
                BullyPeer peer = new BullyPeer(context);
                return peer;
            }
        };
        context.setPeerFactory(peerFactory);
        peer = new PeerServer(peerFactory, context);
        peer.startAsync();
        peer.awaitRunning();
    }

    public void read() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                String command = reader.readLine();
                if (command != null) {
                    if (command.startsWith("connect")) {
                        String[] datas = command.split(" ");
                        if (datas.length != 4) {
                            out.println("Input pattern is connect {hostname} {ip} {port}");
                        } else {
                            Connection connection = ConnectionFactory.getInstance(peerFactory).connect(datas[1], datas[2], Integer.parseInt(datas[3]));
//                            connection.connect(datas[1], datas[2], Integer.parseInt(datas[3]));
                        }
                    } else if (command.startsWith("disconnect")) {
                        String[] datas = command.split(" ");
//                        disconnect(datas[1]);
                    } else if (command.startsWith("send")) {
                        String[] datas = command.split(" ");
//                        send(datas[1], datas[2]);
                    } else if (command.startsWith("stop")) {
                        stop();
                    } else if (command.equals("help")) {
                        out.println("connect : Connect to peer");
                        out.println("stop : Stop current server");
                    }
                }
            } catch (IOException e) {
                System.exit(-1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void stop() {
        peer.stop();
    }
}
