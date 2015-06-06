package ru.mars.gameserver;


import ru.mars.rps.server.game.MessageType;

/**
 * Date: 01.11.14
 * Time: 22:41
 */
public class MessageFactory {

    protected static String header(int msgId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='utf-8'?>");
        sb.append("<msg>");
        sb.append("<id>");
        sb.append(msgId);
        sb.append("</id>");
        return sb.toString();
    }

    protected static String footer(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append(msg);
        sb.append("</msg>");
        return sb.toString();
    }

    public static String wrap(int msgId, String msg) {
        return footer(header(msgId) + msg);
    }

    public static String createGameOverMessage(Integer state) {
        return wrap(MessageType.S_GAME_OVER, "<state>" + state + "</state>");
    }

    public static String createPlayerSelectionMessage(int element, int playerwon) {
        return wrap(MessageType.S_PLAYER_SELECTED, "<element>" + element + "</element><won>" + playerwon + "</won>");
    }

    public static String createGameResultMessage(int state) {
        return wrap(MessageType.S_GAME_OVER, "<state>" + state + "</state>");
    }
}
