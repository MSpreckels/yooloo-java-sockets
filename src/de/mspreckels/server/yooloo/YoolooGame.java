package de.mspreckels.server.yooloo;

import de.mspreckels.server.threading.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

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
        int cardOfClient = session.getCardOfClient(client, i);
        currentCards.add(cardOfClient);
      }
      this.session.broadcast("Played Round " + (i + 1));

      int bestCard = getBestCard(currentCards);

      UUID roundWinner = session.getClientByCard(bestCard, i);

      if (roundWinner == null) {
        this.session.broadcast(
          String.format("Round %s tied! Scoreboard: %s", i + 1, getScoreboard()));
      } else {
        int points = this.points.get(roundWinner);
        this.points.put(roundWinner, points + (i + 1));

        this.session.broadcast(
          String.format("Winner of round %s is %s. Scoreboard: %s", i + 1, roundWinner,
            getScoreboard()));
      }

    }

    UUID gameWinner = getWinnerOfGame();

    this.session.broadcast(
      String.format("Winner of game is %s with %s points! Scoreboard: %s", gameWinner,
        points.get(gameWinner), getScoreboard()));

  }

  private String getScoreboard() {
    return points.entrySet().stream()
      .map(uuidIntegerEntry -> String.format("%s: %s", uuidIntegerEntry.getKey(),
        uuidIntegerEntry.getValue()))
      .collect(Collectors.joining(", "));
  }

  private UUID getWinnerOfGame() {
    Entry<UUID, Integer> winnerEntry = points.entrySet().stream()
      .sorted((uuidIntegerEntry, uuidIntegerEntry2) ->
        uuidIntegerEntry.getValue() > uuidIntegerEntry2.getValue() ? -1 : 0).iterator().next();

    return winnerEntry.getKey();
  }

  private int getBestCard(List<Integer> currentCards) {
    int bestCard = 0;
    int[] cardsCounter = new int[10];
    currentCards.forEach(integer -> cardsCounter[integer - 1]++);

    for (int j = cardsCounter.length - 1; j >= 0; j--) {
      if (cardsCounter[j] == 1) {
        bestCard = j + 1;
        break;
      }
    }
    return bestCard;
  }
}
