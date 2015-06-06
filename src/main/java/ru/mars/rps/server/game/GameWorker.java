package ru.mars.rps.server.game;

import org.jboss.netty.channel.Channel;
import org.w3c.dom.Element;
import ru.mars.gameserver.AbstractGameWorker;
import ru.mars.gameserver.GameState;
import ru.mars.gameserver.MessageFactory;

/**
 * Date: 01.11.14
 * Time: 14:32
 */
public class GameWorker extends AbstractGameWorker {
    private static GameWorker instance = new GameWorker();

    private GameWorker() {
        startPairFinder();
    }

    public static GameWorker getInstance() {
        return instance;
    }

    @Override
    protected void processCommand(Integer command, Element root, Channel channel) {
        switch (command) {
            case MessageType.C_PLAYER_WAITING: {
                gameStateMap.put(channel, GameState.LOGGED_IN);
                channel.write(MessageFactory.wrap(MessageType.S_WAIT, ""));
                if (parameters.isDebug())
                    logger.info("Player " + playerMap.get(channel).toString() + " waiting for pair");
            }
            break;
            case MessageType.C_READY_TO_PLAY: {
                GameThread gameThread = gameThreadMap.get(channel);
                gameThread.setPlayerReady(channel);
                gameStateMap.put(channel, GameState.IN_GAME);
                if (parameters.isDebug())
                    logger.info("Player " + playerMap.get(channel).toString() + " is ready to play");
            }
            break;
            case MessageType.C_PLAYER_FIELD: {

            }
            break;
            case MessageType.C_TARGET: {
            }
            break;
            case MessageType.C_OK: {
            }
            break;
        }
    }

}
