package net.mspreckels.server.session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.mspreckels.server.thread.ServerClientThread;

public class Session extends Thread {

  private final ArrayList<ServerClientThread> clients;

  public Session(List<ServerClientThread> threads) {
    this.clients = new ArrayList<>(threads);
  }

  @Override
  public void run() {
    int counter = 0;

    while (counter < 5) {
      for (ServerClientThread thread : clients) {
        try {
          thread.poke();
        } catch (IOException e) {
          e.printStackTrace();
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }
      }

      try {
        Thread.sleep(1000);
        counter++;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    for (ServerClientThread thread : clients) {
      try {
        thread.shutdown();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }

  }
}
