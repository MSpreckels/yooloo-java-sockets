package net.mspreckels.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import net.mspreckels.enums.AppState;
import net.mspreckels.logger.Logger;
import net.mspreckels.logger.Logger.Level;
import net.mspreckels.server.config.ServerConfig;
import net.mspreckels.server.state.ServerState;
import net.mspreckels.server.thread.ServerClientThread;

public class Server {

  private static final Logger LOG = new Logger(Server.class);

  private final String[] args;
  private final ServerConfig config;
  private AppState appState;
  private ServerState serverState;

  private ServerSocket serverSocket;

  public Server(String[] args, ServerConfig config) {
    this.args = args;
    serverSocket = null;
    appState = AppState.STARTUP;
    this.config = config;
  }

  /**
   * main server method, runs in a loop
   *
   * @throws IOException
   */
  public void run() throws IOException, InterruptedException, ClassNotFoundException {
    switch (this.appState) {
      case STARTUP -> handleStartup();
      case ACCEPTING -> handleAccepting();
      case SHUTDOWN -> handleShutdown();
    }
  }

  private void handleStartup() throws IOException {
    LOG.log(Level.SUCCESS, "Server started.");

    //Setup server socket
    this.serverSocket = new ServerSocket(config.getPort());

    changeState(AppState.ACCEPTING);
  }

  private void handleAccepting() throws IOException, InterruptedException, ClassNotFoundException {
    LOG.log(Level.INFO, "Waiting for connections...");
    Socket incomingClient = this.serverSocket.accept();

    handleClient(incomingClient);
  }

  private void handleClient(Socket incomingClient)
    throws IOException, InterruptedException, ClassNotFoundException {
    LOG.log(Level.INFO,
      String.format("Client %s has been connected.", incomingClient.getRemoteSocketAddress()));

    ServerClientThread serverClientThread = new ServerClientThread(incomingClient);
    serverClientThread.start();
  }

  private void handleShutdown() {
    LOG.log(Level.INFO, "Server shutting down. Goodbye!");
    changeState(AppState.CLOSE);
  }

  private void changeState(AppState newState) {
    LOG.log(Level.INFO, String.format("Changing state from %s to %s", this.appState, newState));
    this.appState = newState;
  }

  public AppState getAppState() {
    return this.appState;
  }
}
