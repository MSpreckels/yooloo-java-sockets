package net.mspreckels.server;

import net.mspreckels.server.config.ServerConfig;

public class Main {

    public static void main(String[] args) {
        ServerConfig config = new ServerConfig();
        Server server = new Server(args, config);
        server.start();
    }
}
