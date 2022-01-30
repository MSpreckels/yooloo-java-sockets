package net.mspreckels.client;

import net.mspreckels.client.config.ClientConfig;

public class Main {

  public static void main(String[] args) {
    ClientConfig config = new ClientConfig();
    Client client = new Client(args, config);
    client.start();

    while(true) {
      try {
        Thread.sleep(0);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
