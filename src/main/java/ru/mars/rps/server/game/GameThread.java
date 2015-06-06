package ru.mars.rps.server.game;

import org.jboss.netty.channel.Channel;
import ru.mars.gameserver.AbstractGameLogic;
import ru.mars.gameserver.MessageFactory;

/**
 * Date: 01.11.14
 * Time: 21:58
 */
public class GameThread extends AbstractGameLogic implements Runnable {
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
            logger.info("Player " + channel + " is ready");
        while (!isAllReady()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                logger.error("Unable to wait", e);
            }
        }
    }

    @Override
    protected void onPlayerSelectedElement(Channel channel, int element) {
        if (channel1.equals(channel))
            player1element = element;
        else
            player2element = element;
        if (player1element > -1 && player2element > -1) {
            //TODO: calculate players won
            int playerwon = 0;
            channel1.write(MessageFactory.createPlayerSelectionMessage(player2element, playerwon));
            channel2.write(MessageFactory.createPlayerSelectionMessage(player1element, playerwon));
            channel1.write(MessageFactory.createGameResultMessage(1));
            channel2.write(MessageFactory.createGameResultMessage(1));
        }
    }

    @Override
    protected void playerCancelGame(Channel channel) {
        channel1.write(MessageFactory.createGameResultMessage(1));
        channel2.write(MessageFactory.createGameResultMessage(1));
    }
}
