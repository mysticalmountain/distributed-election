package com.an.distributed.election.message;

import com.an.distributed.election.core.Message;
import com.an.distributed.election.core.ProtocolException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @ClassName Ok
 * @Description Ok
 * @Author an
 * @Date 2019/7/4 上午9:20
 * @Version 1.0
 */
public class Ok extends Message {

    public static String COMMAND = "ok";

    private int id;

    public Ok(byte[] payload) {
        super(payload);
    }

    public Ok(int id) {
        this.id = id;
    }

    @Override
    public void parse() throws ProtocolException {
        id = (int) readUint32();
    }

    @Override
    protected byte[] doSerialize() throws IOException, ProtocolException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        uint32ToByteStream(id, stream);
        return stream.toByteArray();
    }

    @Override
    public String getCommand() {
        return Ok.COMMAND;
    }

    @Override
    public int getLength() {
        return 4;
    }

    @Override
    public String toString() {
        return "Ok{" +
                "id=" + id +
                ", header=" + header +
                '}';
    }
}
