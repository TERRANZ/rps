package ru.mars.rps.server.game;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import ru.mars.gameserver.GameState;
import ru.mars.gameserver.MessageFactory;
import ru.mars.gameserver.Parameters;

/**
 * Date: 03.11.14
 * Time: 14:15
 */
public class PairFinder implements Runnable {

    protected Logger logger = Logger.getLogger(this.getClass());

    @Override
    public void run() {
        while (true) {
            try {
                if (GameWorker.getInstance().getPlayerMap().size() > 1) {
                    Channel chan1 = null, chan2 = null;
                    for (Channel channel : GameWorker.getInstance().getPlayerMap().keySet()) {
                        if (GameWorker.getInstance().getPlayerState(channel).equals(GameState.LOGGED_IN))
                            if (chan1 == null)
                                chan1 = channel;
                            else if (chan2 == null && !chan1.equals(channel))
                                chan2 = channel;
                        if (chan1 != null && chan2 != null) {
                            if (Parameters.getInstance().isDebug())
                                logger.info(chan1 + " and " + chan2 + " are in pair");
                            chan1.write(MessageFactory.wrap(MessageType.S_PAIR_FOUND, ""));
                            chan2.write(MessageFactory.wrap(MessageType.S_PAIR_FOUND, ""));
                            GameThread gameThread = new GameThread(chan1, chan2, GameWorker.getInstance().getPlayer(chan1), GameWorker.getInstance().getPlayer(chan2));
                            GameWorker.getInstance().addGameThreadForChannel(chan1, gameThread);
                            GameWorker.getInstance().addGameThreadForChannel(chan2, gameThread);
                            GameWorker.getInstance().setPlayerState(chan1, GameState.GAME_LOADING);
                            GameWorker.getInstance().setPlayerState(chan2, GameState.GAME_LOADING);
                            GameWorker.getInstance().startGameThread(gameThread);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error while finding pair", e);
            }
            try {
                Thread.sleep(500);//засыпаем на полсекунды, если не найдена пара
            } catch (InterruptedException e) {
                logger.error("Interrupted while sleeping", e);
            }
        }
    }
}
