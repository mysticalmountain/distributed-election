package com.an.distributed.election.peer;

import com.an.distributed.election.core.Message;
import com.an.distributed.election.core.PeerFactory;
import com.an.distributed.election.message.Election;
import com.an.distributed.election.message.Ok;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName Context
 * @Description Context
 * @Author an
 * @Date 2019/7/8 下午5:06
 * @Version 1.0
 */
public class Context {

    private int localId;
    private int localPort;
    private int leaderId;
    private int leaderPort;
    private boolean isLeader;
    private boolean isConnectedToLeader;

    private PeerFactory peerFactory;
    private EventBus eventBus = new EventBus("peerEventBus");


    private List<PeerBean> peerBeans = new ArrayList<>();
    private Map<String, SettableFuture<Message>> messageResponseFutureMap = new HashMap<>();




    private ConnectionFactory connectionFactory;

    public Context() {
        PeerMonitor peerMonitor = new PeerMonitor(this);
        eventBus.register(peerMonitor);
    }


    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public int getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }

    public int getLeaderPort() {
        return leaderPort;
    }

    public void setLeaderPort(int leaderPort) {
        this.leaderPort = leaderPort;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }

    public boolean isConnectedToLeader() {
        return isConnectedToLeader;
    }

    public void setConnectedToLeader(boolean connectedToLeader) {
        isConnectedToLeader = connectedToLeader;
    }

    public Map<String, SettableFuture<Message>> getMessageResponseFutureMap() {
        return messageResponseFutureMap;
    }

    public void setMessageResponseFutureMap(Map<String, SettableFuture<Message>> messageResponseFutureMap) {
        this.messageResponseFutureMap = messageResponseFutureMap;
    }

    public List<PeerBean> getPeerBeans() {
        return peerBeans;
    }

    public void addPeerBean(PeerBean peerBean) {
        peerBeans.add(peerBean);
    }

    public void removePeerBean(PeerBean peerBean) {
        peerBeans.remove(peerBean);
    }


    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public PeerFactory getPeerFactory() {
        return peerFactory;
    }

    public void setPeerFactory(PeerFactory peerFactory) {
        this.peerFactory = peerFactory;
    }

    static class ConnectionClosedEvent {
        public int id;

        public ConnectionClosedEvent(int id) {
            this.id = id;
        }
    }

    public static class StartedEvent {

    }
}
