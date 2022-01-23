package net.mspreckels.server.thread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import net.mspreckels.logger.Logger;
import net.mspreckels.logger.Logger.Level;
import net.mspreckels.server.Server;
import net.mspreckels.server.thread.ServerClientThread;

public class Session extends Thread {

  private static final Logger LOG = new Logger(Session.class);

  private final ArrayList<ServerClientThread> clients;

  public Session(List<ServerClientThread> threads) {
    this.clients = new ArrayList<>(threads);
  }

  @Override
  public void run() {

    try {
      HashMap<UUID, int[]> cardSelection = getCardsFromClients();

      cardSelection.forEach((uuid, ints) -> LOG.log(Level.INFO, "Player %s has chosen %s.", uuid,
        Arrays.stream(ints)
          .mapToObj(String::valueOf)
          .collect(Collectors.joining(" "))));
      
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

  }

  private HashMap<UUID, int[]> getCardsFromClients() throws IOException, ClassNotFoundException {
    HashMap<UUID, int[]> cardSelection = new HashMap<>();

    for (ServerClientThread client : clients) {
      int[] cards = client.getCards();

      cardSelection.put(client.getUUID(), cards);
    }

    return cardSelection;
  }
}
