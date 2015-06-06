package ru.mars.gameserver;

import com.beust.jcommander.Parameter;

/**
 * Date: 15.01.15
 * Time: 11:35
 */
public class Parameters {
    @Parameter(names = {"-g"}, description = "Game server port")
    private Integer gameServerPort = 56777;
    @Parameter(names = {"-p"}, description = "Policy server port")
    private Integer policyServerPort = 1008;
    @Parameter(names = "--help", help = true)
    private boolean help;
    @Parameter(names = {"-d","--debug"}, description = "Debug messages")
    private boolean debug = false;
    @Parameter(names = {"-ps","--policy-server"}, description = "Start policy server")
    private boolean startPolicy = false;

    private static Parameters instance = new Parameters();

    public static Parameters getInstance() {
        return instance;
    }

    private Parameters() {
    }

    public Integer getGameServerPort() {
        return gameServerPort;
    }

    public void setGameServerPort(Integer gameServerPort) {
        this.gameServerPort = gameServerPort;
    }

    public Integer getPolicyServerPort() {
        return policyServerPort;
    }

    public void setPolicyServerPort(Integer policyServerPort) {
        this.policyServerPort = policyServerPort;
    }

    public Boolean isDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public boolean isStartPolicy() {
        return startPolicy;
    }

    public void setStartPolicy(boolean startPolicy) {
        this.startPolicy = startPolicy;
    }
}