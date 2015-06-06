package ru.mars.rps.server;

import com.beust.jcommander.JCommander;
import ru.mars.gameserver.Parameters;
import ru.mars.gameserver.netty.GameServer;
import ru.mars.policyserver.PolicyServer;

import java.io.IOException;

public class Main {

    public static void main(String args[]) throws IOException, InterruptedException {
        final Parameters parameters = Parameters.getInstance();
        new JCommander(parameters, args);
        System.out.println("Debug is " + parameters.isDebug());
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (parameters.isStartPolicy())
                    new PolicyServer(parameters.getPolicyServerPort(), new String[]{"*:" + parameters.getGameServerPort()}).start();
                new GameServer(parameters.getGameServerPort()).start();
            }
        }).start();
    }
}
