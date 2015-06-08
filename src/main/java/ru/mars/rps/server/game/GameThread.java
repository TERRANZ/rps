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
//        while (!isAllReady()) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                logger.error("Unable to wait", e);
//            }
//        }
    }

    @Override
    protected void onPlayerSelectedElement(Channel channel, int element) {
        if (channel1.equals(channel))
            player1element = element;
        else
            player2element = element;
        if (player1element > -1 && player2element > -1) {
            //TODO: calculate players won
            //1 - r
            //2 - p
            //3 - s
            int playerwon = 0;
            boolean player1won = false;
            if (player1element == player2element)
                playerwon = 3;
            else {
                switch (player1element) {
                    case 1: {
                        if (player2element == 3) {
                            //r + p => p
                            player1won = false;
                        } else {
                            //r + s => r
                            player1won = true;
                        }
                    }
                    break;
                    case 2: {
                        if (player2element == 3) {
                            //p + s => s
                            player1won = false;
                        } else {
                            //p + r => p
                            player1won = true;
                        }
                    }
                    break;
                    case 3: {
                        if (player2element == 1) {
                            //s + r => r
                            player1won = false;
                        } else {
                            //s + p => s;
                            player1won = true;
                        }
                    }
                    break;
                }
                playerwon = player1won ? 0 : 1;
            }

            channel1.write(MessageFactory.createPlayerSelectionMessage(player2element, playerwon != 3 ? player1won ? 1 : 0 : 3));
            channel2.write(MessageFactory.createPlayerSelectionMessage(player1element, playerwon != 3 ? player1won ? 0 : 1 : 3));
            rounds++;
            if (rounds == 2) {
                channel1.write(MessageFactory.createGameResultMessage(1));
                channel2.write(MessageFactory.createGameResultMessage(1));
            }
        }
    }

    @Override
    protected void playerCancelGame(Channel channel) {
        channel1.write(MessageFactory.createGameResultMessage(1));
        channel2.write(MessageFactory.createGameResultMessage(1));
    }
}
