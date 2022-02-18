package de.mspreckels.server;

import de.mspreckels.logger.Logger;
import de.mspreckels.logger.Logger.Level;
import de.mspreckels.server.config.ServerConfig;
import de.mspreckels.server.enums.ServerState;
import de.mspreckels.server.threading.ServerClientThread;
import de.mspreckels.server.threading.Session;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

  private static final Logger LOG = new Logger(Server.class);

  private final String[] args;
  private final ServerConfig config;
  private ServerState serverState;

  private ServerSocket serverSocket;
  private List<ServerClientThread> serverClientThreadList;

  private int numSessionsCreated = 0;

  public Server(String[] args, ServerConfig config) {
    this.args = args;
    serverSocket = null;
    serverState = ServerState.STARTUP;
    this.config = config;
    serverClientThreadList = new ArrayList<>();
  }

  public void start() {
    while (!serverState.equals(ServerState.CLOSE)) {
      try {
        run();
      } catch (IOException | InterruptedException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * main server method, runs in a loop
   *
   * @throws IOException
   */
  public void run() throws IOException, InterruptedException, ClassNotFoundException {
    switch (this.serverState) {
      case STARTUP -> handleStartup();
      case ACCEPTING -> handleAccepting();
      case SESSION -> handleSession();
      case SHUTDOWN -> handleShutdown();
    }
  }

  private void handleStartup() throws IOException {
    LOG.log(Level.SUCCESS, "Server started.");

    //Setup server socket
    this.serverSocket = new ServerSocket(config.getPort());

    changeState(ServerState.ACCEPTING);
  }

  private void handleAccepting() throws IOException, InterruptedException, ClassNotFoundException {
    LOG.log(Level.INFO, "Waiting for connections... (%s/%s)",
      this.serverClientThreadList.size(),
      config.getMaxPlayersInSession());
    Socket incomingClient = this.serverSocket.accept();

    handleClient(incomingClient);

    if (serverClientThreadList.size() == config.getMaxPlayersInSession()) {
      changeState(ServerState.SESSION);
    }
  }

  private void handleClient(Socket incomingClient)
    throws IOException, InterruptedException, ClassNotFoundException {
    LOG.log(Level.INFO,
      String.format("Client %s has been connected.", incomingClient.getRemoteSocketAddress()));

    ServerClientThread serverClientThread = new ServerClientThread(incomingClient);
    serverClientThread.start();

    this.serverClientThreadList.add(serverClientThread);
  }

  private void handleSession() throws IOException, ClassNotFoundException {
    LOG.log(Level.INFO, "Enough clients connected! Creating session...");

    Session session = new Session(numSessionsCreated++, serverClientThreadList);
    session.start();
    serverClientThreadList.clear();
    changeState(ServerState.ACCEPTING);
  }

  private void handleShutdown() {
    LOG.log(Level.INFO, "Server shutting down. Goodbye!");
    changeState(ServerState.CLOSE);
  }

  private void changeState(ServerState newState) {
    LOG.log(Level.INFO, String.format("Changing state from %s to %s", this.serverState, newState));
    this.serverState = newState;
  }

  public ServerState getServerState() {
    return this.serverState;
  }


}
