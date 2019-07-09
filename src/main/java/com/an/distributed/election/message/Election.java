package com.an.distributed.election.message;

import com.an.distributed.election.core.Message;
import com.an.distributed.election.core.ProtocolException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @ClassName Election
 * @Description Election
 * @Author an
 * @Date 2019/7/4 上午9:11
 * @Version 1.0
 */
public class Election extends Message {

    public static String COMMAND = "election";

    private int id;

    public Election(byte[] payload) {
        super(payload);
    }
    public Election(int id) {
        this.id = id;
    }

    @Override
    public void parse() throws ProtocolException {
        id = (int) readUint32();
    }

    @Override
    protected byte[] doSerialize() throws IOException, ProtocolException {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            uint32ToByteStream(id, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            throw new ProtocolException(e);
        }
    }

    @Override
    public String getCommand() {
        return Election.COMMAND;
    }

    @Override
    public int getLength() {
        return 4;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Election{" +
                "id=" + id +
                ", header=" + header +
                '}';
    }
}
