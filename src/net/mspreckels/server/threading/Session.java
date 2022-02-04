package net.mspreckels.server.threading;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.mspreckels.logger.Logger;
import net.mspreckels.logger.Logger.Level;
import net.mspreckels.server.yooloo.YoolooGame;

//TODO: create singlesession and tournament session
public class Session extends Thread {

  private static final Logger LOG = new Logger(Session.class);

  private final ArrayList<ServerClientThread> clientThreads;
  private final int sessonId;
  private HashMap<UUID, List<Integer>> clientCards;

  public Session(int id, List<ServerClientThread> threads) {
    this.sessonId = id;
    this.clientThreads = new ArrayList<>(threads);
  }

  @Override
  public void run() {

    //get cards from clients
    try {
      clientCards = getCardsFromClients();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    clientCards.forEach(
      (uuid, cards) -> LOG.log(Level.INFO, "(%s) Player %s has chosen %s.", sessonId, uuid,
        cards.stream()
          .map(String::valueOf)
          .collect(Collectors.joining(" "))));

    broadcast("Starting yooloo game!");

    YoolooGame yoolooGame = new YoolooGame(this);
    yoolooGame.play();

    //shutdown clients
    clientThreads.forEach(clientThread -> {
      try {
        clientThread.shutdown();
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

    for (ServerClientThread client : clientThreads) {
      List<Integer> cards = client.getCards();

      cardSelection.put(client.getUUID(), cards);
    }

    return cardSelection;
  }

  public Set<UUID> getClientUUIDs() {
    return clientCards.keySet();
  }

  public void sendMessageTo(UUID client, String s) {
    ServerClientThread clientThread = clientThreads.stream()
      .filter(serverClientThread -> serverClientThread.getUUID().equals(client))
      .findFirst().orElseThrow();

    LOG.log(Level.INFO, "(%s) Sending message %s", client, s);

    try {
      clientThread.sendMessage(s);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void broadcast(String s) {
    LOG.log(Level.INFO, "(%s) Broadcasting: '%s'", sessonId, s);

    clientThreads.forEach(serverClientThread -> {
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
