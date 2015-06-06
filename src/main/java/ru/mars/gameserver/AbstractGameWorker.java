package ru.mars.gameserver;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import ru.mars.rps.server.game.GameThread;
import ru.mars.rps.server.game.MessageType;
import ru.mars.rps.server.game.PairFinder;
import ru.mars.rps.server.game.Player;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Date: 07.01.15
 * Time: 15:10
 */
public abstract class AbstractGameWorker {
    protected Logger logger = Logger.getLogger(this.getClass());
    protected Map<Channel, GameState> gameStateMap = new WeakHashMap<>();
    protected Map<Channel, Player> playerMap = new WeakHashMap<>();
    protected Map<Channel, GameThread> gameThreadMap = new WeakHashMap<>();
    protected Parameters parameters = Parameters.getInstance();

    protected void startPairFinder() {
        new Thread(new PairFinder()).start();
    }

    public synchronized void addPlayer(Channel channel) {
        gameStateMap.put(channel, GameState.LOGIN);
        playerMap.put(channel, new Player());
    }

    public synchronized void removePlayer(Channel channel) {
        if (playerMap.containsKey(channel)) {
            if (gameThreadMap.get(channel) != null)
                gameThreadMap.get(channel).playerDisconnect(channel);
            cleanupPlayer(channel);
        }
    }

    public synchronized void handlePlayerCommand(Channel channel, String xml) {
        if (parameters.isDebug())
            logger.info("Received xml = " + xml + " from channel " + channel.toString());
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        Document doc = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();
        } catch (Exception e) {
            logger.error("Unable to parse xml", e);
        }

        if (doc != null) {
            Element root = doc.getDocumentElement();
            Integer command = Integer.parseInt(root.getElementsByTagName("id").item(0).getTextContent());

            if (command == MessageType.C_PLAYER_CANCEL_WAIT) {
                if (parameters.isDebug())
                    logger.info("Player " + channel + " cancelled waiting");
                channel.write(MessageFactory.wrap(MessageType.S_GAME_CANCELLED, ""));
                gameStateMap.put(channel, GameState.LOGIN);
            } else
                processCommand(command, root, channel);

        }
    }

    protected abstract void processCommand(Integer command, Element root, Channel channel);

    public Map<Channel, Player> getPlayerMap() {
        synchronized (playerMap) {
            return playerMap;
        }
    }

    public void addGameThreadForChannel(Channel channel, GameThread gameThread) {
        synchronized (gameThreadMap) {
            gameThreadMap.put(channel, gameThread);
        }
    }

    public void setPlayerState(Channel channel, GameState gameState) {
        synchronized (gameStateMap) {
            gameStateMap.put(channel, gameState);
        }
    }

    public GameState getPlayerState(Channel channel) {
        synchronized (gameStateMap) {
            return gameStateMap.get(channel);
        }
    }

    public Player getPlayer(Channel channel) {
        synchronized (playerMap) {
            return playerMap.get(channel);
        }
    }

    public synchronized void startGameThread(GameThread gameThread) {
        new Thread(gameThread).start();
    }

    public void cleanupPlayer(Channel channel) {
        synchronized (playerMap) {
            playerMap.remove(channel);
        }
        synchronized (gameStateMap) {
            gameStateMap.remove(channel);
        }
        synchronized (gameThreadMap) {
            gameThreadMap.remove(channel);
        }
    }
}
