package com.an.distributed.election;

/**
 * @ClassName Peer5001
 * @Description Peer5001
 * @Author an
 * @Date 2019/7/4 下午10:45
 * @Version 1.0
 */
public class Peer5001 extends PeerTest {


    public static void main(String[] args) {
        Peer5001 peer = new Peer5001();
//        PeerManager peerManager = new PeerManager();
//        peerManager.addPeer(Integer.valueOf(5001), Integer.valueOf(5001));
//        peerManager.addPeer(Integer.valueOf(5002), Integer.valueOf(5002));
//        peerManager.addPeer(Integer.valueOf(5003), Integer.valueOf(5003));
        peer.startPeer(5001, 5001);
        peer.read();

    }
}
