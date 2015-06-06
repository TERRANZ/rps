package ru.mars.gameserver;

/**
 * Date: 23.11.14
 * Time: 20:06
 */
public class Statistic {
    private int online;
    private int games;

    public Statistic(int online, int games) {
        this.online = online;
        this.games = games;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getGames() {
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }
}
