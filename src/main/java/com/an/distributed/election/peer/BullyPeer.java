package com.an.distributed.election.peer;

import com.an.distributed.election.core.Message;
import com.an.distributed.election.core.MessageWrite;
import com.an.distributed.election.core.Peer;
import com.an.distributed.election.core.ProtocolException;
import com.an.distributed.election.message.*;
import com.google.common.util.concurrent.SettableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Random;

/**
 * @ClassName BullyPeer
 * @Description BullyPeer
 * @Author an
 * @Date 2019/7/4 上午9:23
 * @Version 1.0
 */
public class BullyPeer implements Peer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Context context;
    private MessageWrite messageWrite;
    private Random random = new Random();
    private InetSocketAddress otherSidePeerAddress;

    public BullyPeer(Context context) {
        this.context = context;
    }

    @Override
    public void connectionOpened() {
        logger.info("Peer connction is opened");
        Ping ping = new Ping(random.nextInt(9999));
        Message.Header header = new Message.Header(Message.magic, ping.getCommand(), System.currentTimeMillis(), ping.getLength());
        ping.setHeader(header);
        messageWrite.writeMessage(ping);
    }

    @Override
    public void connectionClosed() {
        context.getEventBus().post(new Context.ConnectionClosedEvent(context.getLocalId()));
    }

    @Override
    public void receiveMessage(Message message) {
        if (Ping.COMMAND.toUpperCase().equals(message.getCommand().toUpperCase())) {
            ping((Ping) message);
        } else if (Pong.COMMAND.toUpperCase().equals(message.getCommand().toUpperCase())) {
            pong((Pong) message);
        } else if (Election.COMMAND.toUpperCase().equals(message.getCommand().toUpperCase())) {
            election((Election) message);
        } else if (Coordinator.COMMAND.toUpperCase().equals(message.getCommand().toUpperCase())) {
            coordinator((Coordinator) message);
        } else if (Ok.COMMAND.toUpperCase().equals(message.getCommand().toUpperCase())) {
            ok((Ok) message);
        } else {
            throw new ProtocolException(String.format("Receive message error, command %s not support", message.getCommand()));
        }
        SettableFuture<Message> messageSettableFuture = SettableFuture.create();
        messageSettableFuture.set(message);
        context.getMessageResponseFutureMap().put(String.valueOf(message.getHeader().serialNo), messageSettableFuture);
    }

    @Override
    public void setMessageWriteTarget(MessageWrite messageWrite) {
        this.messageWrite = messageWrite;
    }

    @Override
    public void setPeerAddress(InetSocketAddress socketAddress) {
        this.otherSidePeerAddress = socketAddress;
    }

    public InetSocketAddress getOtherSidePeerAddress() {
        return otherSidePeerAddress;
    }

    public void ping(Ping ping) {
        Pong pong = null;
        if (context.isLeader()) {
            pong = new Pong(context.getLocalId(), context.getLeaderId(), context.getLeaderPort());
        } else {
            pong = new Pong();
            pong.setLocalId(context.getLocalId());
        }
        Message.Header header = new Message.Header(Message.magic, pong.getCommand(), ping.getHeader().serialNo, pong.getLength());
        pong.setHeader(header);
        messageWrite.writeMessage(pong);
    }

    public void pong(Pong message) {
        //First startup, leader is discovered
        if (message.getId() != 0 && context.getLeaderId() == 0 && context.isLeader() == false) {
            context.setLeader(true);
            context.setLeaderId(message.getId());
            context.setLeaderPort(message.getPort());
            //The peer of other side is leader
            if (message.getLocalId() == message.getId()) {
                context.setConnectedToLeader(true);
            } else {
                //Connect to leader
                try {
                    ConnectionFactory.getInstance(context.getPeerFactory()).connect(String.valueOf(message.getId()), "127.0.0.1", message.getPort());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        //This peer is leader
        if (context.isLeader()) {
            //Nothing to do
        }

        //Leader is founded
        if (context.getLeaderId() != 0) {
            //Nothing to do
        }
        //The peer of other side isn't leader, close the connection
        if (message.getLocalId() != message.getId()) {
            ConnectionFactory.getInstance(context.getPeerFactory()).disConnect(String.valueOf(message.getLocalId()));
        }
    }

    public void election(Election election) {
        if (election.getId() < context.getLocalId()) {
            Ok ok = new Ok(election.getId());
            Message.Header header = new Message.Header(Message.magic, ok.getCommand(), election.getHeader().serialNo, ok.getLength());
            ok.setHeader(header);
            messageWrite.writeMessage(ok);
        }
    }

    public void coordinator(Coordinator coordinator) {
        context.setLeaderId(coordinator.getId());
        context.setLeaderPort(coordinator.getPort());
    }

    public void ok(Ok message) {
        SettableFuture<Message> messageSettableFuture = SettableFuture.create();
        messageSettableFuture.set(message);
        context.getMessageResponseFutureMap().put(String.valueOf(message.getHeader().serialNo), messageSettableFuture);
    }


}
