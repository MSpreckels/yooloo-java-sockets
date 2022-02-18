package de.mspreckels.client;

import de.mspreckels.client.config.ClientConfig;

public class Main {

  public static void main(String[] args) {
    ClientConfig config = new ClientConfig();
    Client client = new Client(args, config);
    client.start();
  }
}
