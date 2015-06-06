package ru.mars.gameserver;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import ru.mars.rps.server.game.GameWorker;
import ru.mars.rps.server.game.Player;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Date: 04.11.14
 * Time: 18:34
 */
public abstract class AbstractGameLogic {
    protected Channel channel1, channel2;
    protected Player player1, player2;
    protected int player1element = -1, player2element = -1;
    protected Map<Channel, Boolean> playerReady = new WeakHashMap<>();
    protected Logger logger = Logger.getLogger(this.getClass());
    protected volatile Boolean game = true;
    protected Parameters parameters = Parameters.getInstance();

    public synchronized void setPlayerReady(Channel playerChannel) {
        playerReady.put(playerChannel, true);
        onPlayerReady(playerChannel);
    }

    public synchronized boolean isAllReady() {
        return playerReady.get(channel1) && playerReady.get(channel2);
    }

    protected abstract void onPlayerReady(Channel channel);

    protected abstract void onPlayerSelectedElement(Channel channel, int element);

    protected abstract void playerCancelGame(Channel channel);

    protected void sendGameOverMessage(Integer deadPlayer) {
        channel1.write(MessageFactory.createGameOverMessage(deadPlayer));
        channel2.write(MessageFactory.createGameOverMessage(deadPlayer));

    }

    public synchronized void playerDisconnect(Channel channel) {
        if (channel.equals(channel1)) {
            try {
                channel2.write(MessageFactory.createGameOverMessage(2));
                GameWorker.getInstance().setPlayerState(channel2, GameState.LOGIN);
            } catch (Exception e) {
                logger.error("Unable to send player disconnection message to channel2", e);
            }

        } else {
            try {
                channel1.write(MessageFactory.createGameOverMessage(1));
                GameWorker.getInstance().setPlayerState(channel1, GameState.LOGIN);
            } catch (Exception e) {
                logger.error("Unable to send player disconnection message to channel1", e);
            }
        }
        game = false;
    }
}
