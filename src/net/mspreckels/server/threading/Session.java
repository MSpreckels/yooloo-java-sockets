package net.mspreckels.server.threading;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.mspreckels.logger.Logger;
import net.mspreckels.logger.Logger.Level;
import net.mspreckels.server.yooloo.YoolooGame;

public class Session extends Thread {

  private static final Logger LOG = new Logger(Session.class);

  private final ArrayList<ServerClientThread> clients;
  private final int sessonId;
  private HashMap<UUID, List<Integer>> clientCards;

  public Session(int id, List<ServerClientThread> threads) {
    this.sessonId = id;
    this.clients = new ArrayList<>(threads);
  }

  @Override
  public void run() {

    //get cards from clients
    try {
      clientCards = getCardsFromClients();

      clientCards.forEach(
        (uuid, cards) -> LOG.log(Level.INFO, "(%s) Player %s has chosen %s.", sessonId, uuid,
          cards.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(" "))));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    broadcast("Starting yooloo game!");

    YoolooGame yoolooGame = new YoolooGame(this);
    yoolooGame.play();

    //shutdown clients
    clients.forEach(serverClientThread -> {
      try {
        serverClientThread.shutdown();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    });

  }

  private HashMap<UUID, List<Integer>> getCardsFromClients()
    throws IOException, ClassNotFoundException {
    HashMap<UUID, List<Integer>> cardSelection = new HashMap<>();

    for (ServerClientThread client : clients) {
      List<Integer> cards = client.getCards();

      cardSelection.put(client.getUUID(), cards);
    }

    return cardSelection;
  }

  public Set<UUID> getClientUUIDs() {
    return clientCards.keySet();
  }

  public void sendMessageTo(UUID client, String s) {
    ServerClientThread clientThread = clients.stream()
      .filter(serverClientThread -> serverClientThread.getUUID().equals(client))
      .findFirst().orElseThrow();

    try {
      clientThread.sendMessage(s);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void broadcast(String s) {
    clients.forEach(serverClientThread -> {
      try {
        serverClientThread.sendMessage(s);
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    });
  }

  public Integer getCardOfClient(UUID client, int i) {
    return clientCards.get(client).get(i);
  }

  public UUID getClientByCard(int card, int roundIndex) {

    HashMap<UUID, Integer> clientsPlayedCardOfRound = new HashMap<>();

    clientCards.forEach((uuid, integers) ->
      clientsPlayedCardOfRound.put(uuid, integers.get(roundIndex)));

    for (UUID client : clientsPlayedCardOfRound.keySet()) {
      if (clientsPlayedCardOfRound.get(client) == card) {
        return client;
      }
    }

    return null;
  }
}
