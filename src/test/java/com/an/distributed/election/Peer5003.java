package com.an.distributed.election;

/**
 * @ClassName Peer5001
 * @Description Peer5001
 * @Author an
 * @Date 2019/7/4 下午10:45
 * @Version 1.0
 */
public class Peer5003 extends PeerTest {


    public static void main(String[] args) {
        Peer5003 peer = new Peer5003();
//        PeerManager peerManager = new PeerManager();
//        peerManager.addPeer(Integer.valueOf(5001), Integer.valueOf(5001));
//        peerManager.addPeer(Integer.valueOf(5002), Integer.valueOf(5002));
//        peerManager.addPeer(Integer.valueOf(5003), Integer.valueOf(5003));
        peer.startPeer(5003, 5003);
        peer.read();

    }
}
