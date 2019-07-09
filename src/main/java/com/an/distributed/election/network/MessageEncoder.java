package com.an.distributed.election.network;

import com.an.distributed.election.core.Message;
import com.google.common.io.BaseEncoding;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

/**
 * @ClassName MessageEncoder
 * @Description MessageEncoder
 * @Author an
 * @Date 2019/4/24 上午11:25
 * @Version 1.0
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final BaseEncoding HEX = BaseEncoding.base16().lowerCase();

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(msg.getHeader().serialize());
        stream.write(msg.serialize());
        byte [] dataBytes = stream.toByteArray();
        logger.info("Write HEX message {}", HEX.encode(dataBytes));
        out.writeBytes(stream.toByteArray());
    }
}
