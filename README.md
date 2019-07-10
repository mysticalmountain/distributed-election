# 分布式系统选举Bully算法实现

算法逻辑参考这个地址：https://www.ques10.com/p/2215/short-note-on-election-algorithms-1/

Class tree
```
src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── an
    │   │           └── distributed
    │   │               └── election
    │   │                   ├── core
    │   │                   │   ├── Message.java
    │   │                   │   ├── MessageWrite.java
    │   │                   │   ├── Peer.java
    │   │                   │   ├── PeerClient.java
    │   │                   │   ├── PeerFactory.java
    │   │                   │   └── ProtocolException.java
    │   │                   ├── message
    │   │                   │   ├── Coordinator.java
    │   │                   │   ├── Election.java
    │   │                   │   ├── Ok.java
    │   │                   │   ├── Ping.java
    │   │                   │   └── Pong.java
    │   │                   ├── network
    │   │                   │   ├── MessageDecoder.java
    │   │                   │   ├── MessageEncoder.java
    │   │                   │   ├── PeerHandler.java
    │   │                   │   └── PeerServer.java
    │   │                   └── peer
    │   │                       ├── BullyPeer.java
    │   │                       ├── Connection.java
    │   │                       ├── ConnectionFactory.java
    │   │                       ├── ConnectionManager.java
    │   │                       ├── Context.java
    │   │                       ├── PeerBean.java
    │   │                       └── PeerMonitor.java
    │   └── resources
    └── test
        ├── java
        │   └── com
        │       └── an
        │           └── distributed
        │               └── election
        │                   ├── ClientTest.java
        │                   ├── Peer5001.java
        │                   ├── Peer5002.java
        │                   ├── Peer5003.java
        │                   └── PeerTest.java
```
