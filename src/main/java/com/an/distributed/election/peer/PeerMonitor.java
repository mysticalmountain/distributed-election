package com.an.distributed.election.peer;

import com.an.distributed.election.core.Peer;
import com.an.distributed.election.core.PeerFactory;
import com.an.distributed.election.message.Election;
import com.an.distributed.election.message.Ok;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * @ClassName PeerMonitor
 * @Description PeerMonitor
 * @Author an
 * @Date 2019/7/8 下午10:25
 * @Version 1.0
 */
public class PeerMonitor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    private Context context;

    private static Executor executor = new Executor() {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    };

    public PeerMonitor(Context context) {
        this.context = context;
    }

    //election

    //connection

    //pong

    //leader disconnection

    @Subscribe
    public void startedListener(Context.StartedEvent event) {
        ConnectionFactory connectionFactory = ConnectionFactory.getInstance(context.getPeerFactory());

        int connectionRefusedTimes = 0;
        for (PeerBean peerBean : context.getPeerBeans()) {
            try {
                if (peerBean.getId() != context.getLocalId()) {
                    connectionFactory.connect(String.valueOf(peerBean.getId()), peerBean.getIp(), peerBean.getPort());
                }
            } catch (Exception e) {
                if (e instanceof ConnectException) {
                    connectionRefusedTimes++;
                }
                logger.error("Connection to peer {} {} {} error", peerBean.getId(), peerBean.getIp(), peerBean.getPort(), e);
            }
        }
        if (connectionRefusedTimes + 1 == context.getPeerBeans().size()) {
            logger.info("Connection to all peer is refused, set current peer is leader {} {}", context.getLocalId(), context.getLocalPort());
            context.setLeader(true);
            context.setLeaderId(context.getLocalId());
            context.setLeaderPort(context.getLocalPort());
        }
    }


    @Subscribe
    public void connectionClosedListener(Context.ConnectionClosedEvent event) {
        //Closed connection is leader
        if (context.getLeaderId() == event.id) {
            logger.info("Closed connection {} is leader", event.id);
            for (PeerBean peerBean : context.getPeerBeans()) {
                try {
                    Connection connection = ConnectionFactory.getInstance(context.getPeerFactory()).connect(String.valueOf(peerBean.getId()), peerBean.getIp(), peerBean.getPort());
                    Election election = new Election(context.getLocalId());
                    Ok ok = connection.send(election, context, Ok.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            logger.info("Connection {} is closed, nothing to do", event.id);
            //Nothing to do
        }
    }


    @Subscribe
    public void okEventListener(Ok ok) {


    }

    class OKEvent {

        private Ok ok;

        public OKEvent(Ok ok) {
            this.ok = ok;
        }
    }


}
