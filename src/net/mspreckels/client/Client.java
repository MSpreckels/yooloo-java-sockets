package net.mspreckels.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import net.mspreckels.client.config.ClientConfig;
import net.mspreckels.client.state.ClientState;
import net.mspreckels.logger.Logger;
import net.mspreckels.logger.Logger.Level;
import net.mspreckels.client.message.ClientMessage;
import net.mspreckels.client.message.ClientMessageType;
import net.mspreckels.server.message.ServerMessage;
import net.mspreckels.enums.AppState;

public class Client {

//  private static Logger LOG = Logger.getLogger("CLIENT_LOGGER");
  private static Logger LOG = new Logger(Client.class);

  private final String[] args;
  private final long retryMilliseconds = 5000;
  private final ClientConfig config;

  private AppState appState;
  private ClientState clientState;

  private Socket socket;
  private ObjectInputStream objectInputStream;
  private ObjectOutputStream objectOutputStream;

  public Client(String[] args, ClientConfig config) {
    this.args = args;
    this.config = config;
    this.appState = AppState.STARTUP;
    this.clientState = ClientState.WAITING;
  }

  public void run() throws IOException, InterruptedException, ClassNotFoundException {
    switch (this.appState) {
      case STARTUP -> handleStartup();
      case CONNECTING -> handleConnecting();
      case CONNECTED -> handleConnected();
      case SHUTDOWN -> handleShutdown();
    }
  }

  private void handleStartup() {
    LOG.log(Level.SUCCESS, "Client started.");

    changeState(AppState.CONNECTING);
  }

  private void handleConnecting() throws IOException, InterruptedException {
    try {
      this.socket = new Socket(this.config.getIp(), this.config.getPort());

      this.objectInputStream = new ObjectInputStream(socket.getInputStream());
      this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

      LOG.log(Level.SUCCESS, String.format("Connection successful! Connected to %s",
        this.socket.getRemoteSocketAddress()));

      changeState(AppState.CONNECTED);
    } catch (ConnectException e) {
      LOG.log(Level.WARNING,
        String.format("Connection couldn't be established. Retry in %s second.",
          (retryMilliseconds / 1000)));
      Thread.sleep(retryMilliseconds);
    }
  }

  private void handleConnected() throws IOException, ClassNotFoundException {
    switch (clientState) {
      case REGISTER -> {
      }
      case LOGIN -> {
      }
      case PLAYING -> {
      }
      case WAITING -> {
        LOG.log(Level.INFO, "Waiting for server to tell me what to do.");
        ServerMessage message = readInputStream(ServerMessage.class);

        handleServerMessage(message);
      }
      case QUIT -> {
      }
    }
//        changeState(AppState.BEFORE_SHUTDOWN);
  }

  private void handleServerMessage(ServerMessage message) throws IOException {
    switch (message.getType()) {
      case CHANGE_APPSTATE -> {
        sendResponse(ClientMessageType.CONFIRM, "Shutting down!");
        changeState((AppState) message.getPayload());
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
    changeState(AppState.CLOSE);
  }

  private <T> T readInputStream(Class<T> clazz) throws IOException, ClassNotFoundException {
    return (T) this.objectInputStream.readObject();
  }

  private void changeState(AppState newState) {
    LOG.log(Level.INFO, String.format("Changing state from %s to %s", this.appState, newState));
    this.appState = newState;
  }

  public AppState getAppState() {
    return this.appState;
  }
}
