package ru.mars.gameserver;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import ru.mars.rps.server.game.GameWorker;
import ru.mars.rps.server.game.Player;

import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

/**
 * Date: 04.11.14
 * Time: 18:34
 */
public abstract class AbstractGameLogic {
    protected Channel channel1, channel2;
    protected Player player1, player2;
    protected Map<Channel, Boolean> playerReady = new WeakHashMap<>();
    protected Logger logger = Logger.getLogger(this.getClass());
    protected volatile Boolean game = true;

    public synchronized void setPlayerReady(Channel playerChannel) {
        playerReady.put(playerChannel, true);
        onPlayerReady(playerChannel);
    }

    public synchronized boolean isAllReady() {
        return playerReady.get(channel1) && playerReady.get(channel2);
    }

    protected abstract void onPlayerReady(Channel channel);

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


    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
