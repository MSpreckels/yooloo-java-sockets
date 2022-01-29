package net.mspreckels.client;

import java.io.IOException;
import net.mspreckels.client.config.ClientConfig;
import net.mspreckels.enums.AppState;

public class Main {

  public static void main(String[] args) {
    ClientConfig config = new ClientConfig();
    Client client = new Client(args, config);
    client.start();
  }
}
