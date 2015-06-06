package ru.mars.rps.server.game;

import org.jboss.netty.channel.Channel;
import ru.mars.gameserver.AbstractGameLogic;
import ru.mars.gameserver.MessageFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Date: 01.11.14
 * Time: 21:58
 */
public class GameThread extends AbstractGameLogic implements Runnable {
    private Timer readyTimer;

    private class PlayerNotReadyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (!isAllReady()) {
                if (parameters.isDebug())
                    logger.info("Players are not ready in 1 minute");
                channel1.write(MessageFactory.wrap(MessageType.S_TIME_OUT, ""));
                channel2.write(MessageFactory.wrap(MessageType.S_TIME_OUT, ""));
                synchronized (game) {
                    game = false;
                }
            }
        }
    }


    public GameThread(Channel channel1, Channel channel2, Player player1, Player player2) {
        this.channel1 = channel1;
        this.channel2 = channel2;
        this.player1 = player1;
        this.player2 = player2;
        playerReady.put(channel1, false);
        playerReady.put(channel2, false);
    }

    @Override
    public void run() {
        //ждём в цикле действий
        if (parameters.isDebug())
            logger.info("Starting game thread");
        while (game) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                logger.error("Interrupted while sleep", e);
            }
        }
    }

    @Override
    protected void onPlayerReady(Channel channel) {
        if (parameters.isDebug())
            logger.info("Player " + channel + " is ready, staring 1 minute timer");
        if (readyTimer == null) {
            readyTimer = new Timer();
            readyTimer.schedule(new PlayerNotReadyTimerTask(), 1000 * 60 * 3);
        }
    }


}
