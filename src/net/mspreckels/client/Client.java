package net.mspreckels.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.mspreckels.client.config.ClientConfig;
import net.mspreckels.logger.Logger;
import net.mspreckels.logger.Logger.Level;
import net.mspreckels.client.message.ClientMessage;
import net.mspreckels.client.message.ClientMessageType;
import net.mspreckels.server.message.ServerMessage;
import net.mspreckels.client.enums.ClientState;

public class Client {

  private static Logger LOG = new Logger(Client.class);

  private final String[] args;
  private final long retryMilliseconds = 5000;
  private final ClientConfig config;

  private ClientState clientState;

  private Socket socket;
  private ObjectInputStream objectInputStream;
  private ObjectOutputStream objectOutputStream;
  private List<Integer> cards;

  public Client(String[] args, ClientConfig config) {
    this.args = args;
    this.config = config;
    this.clientState = ClientState.STARTUP;
    this.cards = new ArrayList<>();
  }

  public void start() {
    while (!clientState.equals(ClientState.CLOSE)) {
      try {
        run();
      } catch (IOException | InterruptedException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  public void run() throws IOException, InterruptedException, ClassNotFoundException {
    switch (this.clientState) {
      case STARTUP -> handleStartup();
      case CONNECTING -> handleConnecting();
      case CONNECTED -> handleConnected();
      case SHUTDOWN -> handleShutdown();
    }
  }

  private void handleStartup() {
    LOG.log(Level.SUCCESS, "Client started.");

    cards = IntStream.range(1, 11).boxed().collect(Collectors.toList());
    Collections.shuffle(cards);

    changeState(ClientState.CONNECTING);
  }

  private void handleConnecting() throws IOException, InterruptedException {
    try {
      this.socket = new Socket(this.config.getIp(), this.config.getPort());

      this.objectInputStream = new ObjectInputStream(socket.getInputStream());
      this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

      LOG.log(Level.SUCCESS, String.format("Connection successful! Connected to %s",
        this.socket.getRemoteSocketAddress()));

      changeState(ClientState.CONNECTED);
    } catch (ConnectException e) {
      LOG.log(Level.WARNING,
        String.format("Connection couldn't be established. Retry in %s second.",
          (retryMilliseconds / 1000)));
      Thread.sleep(retryMilliseconds);
    }
  }

  private void handleConnected() throws IOException, ClassNotFoundException {
    LOG.log(Level.INFO, "Waiting for server to tell me what to do.");
    ServerMessage message = readInputStream(ServerMessage.class);

    handleServerMessage(message);
  }

  private void handleServerMessage(ServerMessage message) throws IOException {
    switch (message.getType()) {
      case SHUTDOWN -> {
        sendResponse(ClientMessageType.OK, "Shutting down!");
        changeState(ClientState.SHUTDOWN);
      }
      case GET_CARDS -> {
        LOG.log(Level.INFO, "Sending cards (%s) to server", cards.stream()
          .map(String::valueOf)
          .collect(Collectors.joining(" ")));

        sendResponse(ClientMessageType.OK, cards);
      }
      case PRINT -> {
        LOG.log(Level.INFO, "Server sent: %s", message.getPayload());
        sendResponse(ClientMessageType.OK, "Done!");

      }
    }
  }

  private void sendResponse(ClientMessageType type, Object payload) throws IOException {
    ClientMessage message = new ClientMessage();
    message.setDescription("Response");
    message.setType(type);
    message.setPayload(payload);

    objectOutputStream.writeObject(message);
  }

  private void handleShutdown() {
    LOG.log(Level.WARNING, "Client shutting down. Goodbye!");
    changeState(ClientState.CLOSE);
  }

  private <T> T readInputStream(Class<T> clazz) throws IOException, ClassNotFoundException {
    return (T) this.objectInputStream.readObject();
  }

  private void changeState(ClientState newState) {
    LOG.log(Level.INFO, String.format("Changing state from %s to %s", this.clientState, newState));
    this.clientState = newState;
  }

  public ClientState getClientState() {
    return this.clientState;
  }


}
