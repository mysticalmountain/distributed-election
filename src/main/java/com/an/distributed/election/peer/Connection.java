package com.an.distributed.election.peer;

import com.an.distributed.election.core.Message;
import com.an.distributed.election.network.PeerHandler;
import com.google.common.util.concurrent.SettableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName Connection
 * @Description Connection
 * @Author an
 * @Date 2019/7/8 下午11:02
 * @Version 1.0
 */
public class Connection {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private PeerHandler peerHandler;


    public Connection(PeerHandler peerHandler) {
        this.peerHandler = peerHandler;
    }

    public <T extends Message> T send(Message message, Context context, Class<T> type) throws ExecutionException, InterruptedException {
        peerHandler.writeMessage(message);
        SettableFuture<Message> messageSettableFuture = null;
        while (messageSettableFuture == null) {
            messageSettableFuture = context.getMessageResponseFutureMap().get(String.valueOf(message.getHeader().serialNo));
            if (messageSettableFuture != null) {
                context.getMessageResponseFutureMap().remove(String.valueOf(message.getHeader().serialNo));
            } else {
                TimeUnit.MILLISECONDS.sleep(3);
            }
        }
        return (T) messageSettableFuture.get();
    }

}
