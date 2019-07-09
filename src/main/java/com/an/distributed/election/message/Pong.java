package com.an.distributed.election.message;

import com.an.distributed.election.core.Message;
import com.an.distributed.election.core.ProtocolException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @ClassName Pong
 * @Description Pong
 * @Author an
 * @Date 2019/7/4 上午10:05
 * @Version 1.0
 */
public class Pong extends Message {

    public static String COMMAND = "pong";

    private int localId;
    private int id;
    private int port;

    public Pong () {}

    public Pong(byte[] payload) {
        super(payload);
    }

    public Pong(int localId, int id, int port) {
        this.localId = localId;
        this.id = id;
        this.port = port;
    }


    @Override
    public void parse() throws ProtocolException {
        localId = (int) readUint32();
        id = (int) readUint32();
        port = (int) readUint32();
    }


    @Override
    protected byte[] doSerialize() throws IOException, ProtocolException {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            uint32ToByteStream(localId, stream);
            uint32ToByteStream(id, stream);
            uint32ToByteStream(port, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            throw new ProtocolException(e);
        }
    }

    @Override
    public String getCommand() {
        return Pong.COMMAND;
    }

    @Override
    public int getLength() {
        return 12;
    }

    public int getId() {
        return id;
    }

    public int getPort() {
        return port;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    @Override
    public String toString() {
        return "Pong{" +
                "localId=" + localId +
                ", id=" + id +
                ", port=" + port +
                ", header=" + header +
                '}';
    }
}
