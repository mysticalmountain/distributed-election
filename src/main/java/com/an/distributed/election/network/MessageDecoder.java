package com.an.distributed.election.network;

import com.an.distributed.election.core.Message;
import com.an.distributed.election.core.ProtocolException;
import com.an.distributed.election.message.*;
import com.google.common.io.BaseEncoding;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * @ClassName MessageDecoder
 * @Description MessageDecoder
 * @Author an
 * @Date 2019/4/24 上午11:25
 * @Version 1.0
 */
public class MessageDecoder extends ByteToMessageDecoder {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final BaseEncoding HEX = BaseEncoding.base16().lowerCase();


    private int cacheTimes = 0;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        seekPastMagicBytes(in);

        byte[] headerBytes = new byte[24];
        if (in.readableBytes() < headerBytes.length) {
            in.resetReaderIndex();
            return;
        }
        in.readBytes(headerBytes, 0, headerBytes.length);
        Message.Header header = new Message.Header(headerBytes);
        header.magic = Message.magic;
        byte[] commandBytes = new byte[header.getLength()];
        if (in.readableBytes() < commandBytes.length) {
            in.resetReaderIndex();
            return;
        }
        in.readBytes(commandBytes, 0, commandBytes.length);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(headerBytes);
        stream.write(commandBytes);
        logger.info("Receive HEX message {}", HEX.encode(stream.toByteArray()));

        Message message = null;
        if (Election.COMMAND.toUpperCase().equals(header.command.toUpperCase())) {
            message = new Election(commandBytes);
        } else if (Ok.COMMAND.toUpperCase().equals(header.command.toUpperCase())) {
            message = new Ok(commandBytes);
        } else if (Coordinator.COMMAND.toUpperCase().equals(header.command.toUpperCase())) {
            message = new Coordinator(commandBytes);
        } else if (Ping.COMMAND.toUpperCase().equals(header.command.toUpperCase())) {
            message = new Ping(commandBytes);
        } else if (Pong.COMMAND.toUpperCase().equals(header.command.toUpperCase())) {
            message = new Pong(commandBytes);
        } else {
            throw new ProtocolException(String.format("Command %s mapping message not found", header.command));
        }
        message.setHeader(header);
        out.add(message);
    }

    public void seekPastMagicBytes(ByteBuf in) throws Exception {
        int times = 1;
        times = cacheTimes == 0 ? 1 : cacheTimes;
        while (times <= 3) {
            if (in.readableBytes() > 0) {
                int b = in.readByte();
                int tb = 0xFF & (Message.magic >> (times * 8));
                if (b == tb) {
                    times++;
                } else {
                    times = 1;
                }
            } else {
                cacheTimes = times;
            }
        }
    }
}
