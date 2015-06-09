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

        logger.debug("GameThread ending");
    }

    @Override
    protected void onPlayerReady(Channel channel) {
        if (parameters.isDebug())
            logger.info("Player " + channel + " is ready");
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
            logger.debug("Player1 element = " + player1element + " player2 element = " + player2element);
            if (player1element == player2element)
                playerwon = 3;
            else {

                switch (player1element) {
                    case 1: {
                        if (player2element == 2) {
                            //r + p => p
                            player1won = false;
                            logger.debug("r + p => p");
                        } else {
                            //r + s => r
                            player1won = true;
                            logger.debug("r + s => r");
                        }
                    }
                    break;
                    case 2: {
                        if (player2element == 3) {
                            //p + s => s
                            player1won = false;
                            logger.debug("p + s => s");
                        } else {
                            //p + r => p
                            player1won = true;
                            logger.debug("p + r => p");
                        }
                    }
                    break;
                    case 3: {
                        if (player2element == 1) {
                            //s + r => r
                            player1won = false;
                            logger.debug("s + r => r");
                        } else {
                            //s + p => s;
                            player1won = true;
                            logger.debug("s + p => s;");
                        }
                    }
                    break;
                }
                wins[player1won ? 0 : 1]++;
                playerwon = player1won ? 0 : 1;
            }

            channel1.write(MessageFactory.createPlayerSelectionMessage(player2element, playerwon != 3 ? player1won ? 1 : 0 : 3));
            channel2.write(MessageFactory.createPlayerSelectionMessage(player1element, playerwon != 3 ? player1won ? 0 : 1 : 3));
            player1element = -1;
            player2element = -1;
            if (wins[0] == 3) {
                channel1.write(MessageFactory.createGameResultMessage(1));
                channel2.write(MessageFactory.createGameResultMessage(0));
                game = false;
            } else if (wins[1] == 3) {
                channel1.write(MessageFactory.createGameResultMessage(0));
                channel2.write(MessageFactory.createGameResultMessage(1));
                game = false;
            }
        }
    }

    @Override
    protected void playerCancelGame(Channel channel) {
        channel1.write(MessageFactory.createGameResultMessage(channel == channel1 ? 0 : 1));
        channel2.write(MessageFactory.createGameResultMessage(channel == channel2 ? 0 : 1));
    }
}
