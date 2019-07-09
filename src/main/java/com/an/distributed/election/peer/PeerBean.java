package com.an.distributed.election.peer;

/**
 * @ClassName PeerBean
 * @Description PeerBean
 * @Author an
 * @Date 2019/7/4 下午3:41
 * @Version 1.0
 */
public class PeerBean {

    private int id;
    private String ip;
    private int port;

    public PeerBean(int id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
