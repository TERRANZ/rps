package ru.mars.rps.server.game;

/**
 * Date: 01.11.14
 * Time: 14:34
 */
public class Player {
    private boolean inGame = false;
    private int playerSide = 0;

    public Player() {
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public int getPlayerSide() {
        return playerSide;
    }

    public void setPlayerSide(int playerSide) {
        this.playerSide = playerSide;
    }

    @Override
    public String toString() {
        return "Player{" +
                "inGame=" + inGame +
                ", playerSide=" + playerSide +
                '}';
    }
}
