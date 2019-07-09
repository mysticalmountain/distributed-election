package com.an.distributed.election.message;

import com.an.distributed.election.core.Message;
import com.an.distributed.election.core.ProtocolException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @ClassName Ping
 * @Description Ping
 * @Author an
 * @Date 2019/7/4 上午10:00
 * @Version 1.0
 */
public class Ping extends Message {

    private int nonce;
    public static String COMMAND = "ping";


    public Ping(byte[] payload) {
        super(payload);
    }

    public Ping(int nonce) {
        this.nonce = nonce;
    }

    @Override
    public void parse() throws ProtocolException {
        nonce = (int) readUint32();
    }

    @Override
    protected byte[] doSerialize() throws IOException, ProtocolException {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            uint32ToByteStream(nonce, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            throw new ProtocolException(e);
        }
    }

    @Override
    public String getCommand() {
        return Ping.COMMAND;
    }

    @Override
    public int getLength() {
        return 4;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    @Override
    public String toString() {
        return "Ping{" +
                "nonce=" + nonce +
                ", header=" + header +
                '}';
    }
}
