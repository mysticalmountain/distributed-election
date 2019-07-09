package com.an.distributed.election.message;

import com.an.distributed.election.core.Message;
import com.an.distributed.election.core.ProtocolException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @ClassName Coordinator
 * @Description Coordinator
 * @Author an
 * @Date 2019/7/4 上午9:20
 * @Version 1.0
 */
public class Coordinator extends Message {

    public static String COMMAND = "coordinator";

    private int id;
    private int port;

    public Coordinator(byte[] payload) {
        super(payload);
    }

    @Override
    public void parse() throws ProtocolException {
        id = (int) readUint32();
        port = (int) readUint32();
    }

    @Override
    protected byte[] doSerialize() throws IOException, ProtocolException {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            uint32ToByteStream(id, stream);
            uint32ToByteStream(port, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            throw new ProtocolException(e);
        }
    }

    @Override
    public String getCommand() {
        return Coordinator.COMMAND;
    }

    @Override
    public int getLength() {
        return 8;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Coordinator{" +
                "id=" + id +
                ", port=" + port +
                '}';
    }
}
