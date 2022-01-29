package net.mspreckels.server.yooloo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.mspreckels.server.threading.ServerClientThread;
import net.mspreckels.server.threading.Session;

public class YoolooGame {

  private static final int MAX_ROUNDS = 10;
  private final Session session;
  private HashMap<UUID, Integer> points;

  public YoolooGame(Session session) {
    this.session = session;
    this.points = new HashMap<>();
  }

  public void play() {

    for (UUID client : session.getClientUUIDs()) {
      points.put(client, 0);
    }

    for (int i = 0; i < MAX_ROUNDS; i++) {
      List<Integer> currentCards = new ArrayList<>();

      for (UUID client : session.getClientUUIDs()) {
        Integer cardOfClient = session.getCardOfClient(client, i);
        currentCards.add(cardOfClient);

        this.session.sendMessageTo(client, "Played Round " + (i + 1));
      }

      int[] cardsCounter = new int[10];
      currentCards.forEach(integer -> cardsCounter[integer - 1]++);

      int bestCard = 0;
      for (int j = cardsCounter.length - 1; j >= 0; j--) {
        if (cardsCounter[j] == 1) {
          bestCard = j + 1;
          break;
        }
      }

      UUID roundWinner = session.getClientByCard(bestCard, i);

      if (roundWinner == null) {
        this.session.broadcast("Round " + (i + 1) + " tied!");
      } else {
        Integer points = this.points.get(roundWinner);
        this.points.put(roundWinner, points + (i+1));

        this.session.broadcast("Winner of round " + (i + 1) + " is " + roundWinner);
      }

    }

    Entry<UUID, Integer> winnerEntry = points.entrySet().stream()
      .sorted((uuidIntegerEntry, uuidIntegerEntry2) ->
        uuidIntegerEntry.getValue() > uuidIntegerEntry2.getValue() ? -1 : 0).iterator().next();
    this.session.broadcast("Winner of game is " + winnerEntry.getKey());

  }
}
