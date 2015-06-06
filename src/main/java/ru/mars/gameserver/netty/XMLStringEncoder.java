package ru.mars.gameserver.netty;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import ru.mars.gameserver.Parameters;

import java.nio.charset.Charset;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;

/**
 * Date: 05.11.14
 * Time: 17:43
 */
public class XMLStringEncoder extends OneToOneEncoder {
    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        ChannelBuffer buffer = copiedBuffer((String) msg, Charset.defaultCharset());
        buffer.writeByte((byte) 0x00);
        if (Parameters.getInstance().isDebug())
            logger.info("Writing message: " + ((String) msg));
        return buffer;
    }
}
