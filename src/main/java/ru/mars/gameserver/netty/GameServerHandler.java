package ru.mars.gameserver.netty;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.*;
import ru.mars.gameserver.Parameters;
import ru.mars.rps.server.game.GameWorker;

/**
 * Date: 01.11.14
 * Time: 13:58
 */
public class GameServerHandler extends SimpleChannelUpstreamHandler {
    private Logger logger = Logger.getLogger(this.getClass());

//    private class Greeter implements ChannelFutureListener {
//        @Override
//        public void operationComplete(ChannelFuture future) throws Exception {
//            if (future.isSuccess()) {
//                Channel channel = future.getChannel();
//                channel.write("Greet!"); //TODO: что посылать при логине
//                ChannelsHolder.getInstance().getChannels().add(channel);
//            } else {
//                future.getChannel().close();
//            }
//        }
//    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) {
        if (e instanceof ChannelStateEvent)
            if (Parameters.getInstance().isDebug())
                logger.info("Handle upstream : " + e.toString());
        try {
            super.handleUpstream(ctx, e);
        } catch (Exception e1) {
            logger.error("Unable to handle upstream", e1);
        }
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
//        e.getFuture().addListener(new Greeter());
        GameWorker.getInstance().addPlayer(e.getChannel());
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        if (e instanceof ChannelStateEvent) if (Parameters.getInstance().isDebug())
            logger.info("channel disconnected : " + e.toString());
        try {
            GameWorker.getInstance().removePlayer(e.getChannel());
        } catch (Exception e1) {
            logger.error("Unable to handle disconnect", e1);
        }
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        try {
            GameWorker.getInstance().handlePlayerCommand(e.getChannel(), e.getMessage().toString());
        } catch (Exception e1) {
            logger.error("Unable to handle received message", e1);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        logger.warn("Unexcepted exception from downstream.", e.getCause());
        try {
            GameWorker.getInstance().removePlayer(e.getChannel());
            e.getChannel().close();
        } catch (Exception e1) {
            logger.error("Unable to handle exception", e1);
        }
    }
}
