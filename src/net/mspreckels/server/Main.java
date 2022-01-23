package net.mspreckels.server;

import java.io.IOException;
import net.mspreckels.enums.AppState;
import net.mspreckels.server.config.ServerConfig;

public class Main {

    public static void main(String[] args) {
        ServerConfig config = new ServerConfig();

        Server server = new Server(args, config);

        while(!server.getAppState().equals(AppState.CLOSE)) {
            try {
                server.run();
            } catch (IOException | InterruptedException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
