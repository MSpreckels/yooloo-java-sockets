package net.mspreckels.server.config;

public class ServerConfig {
  private int port;
  private int maxPlayersInSession;

  /**
   * Creates a server config.
   * default port is 3333
   */
  public ServerConfig() {
    this.port = 3333;
    this.maxPlayersInSession = 2;
  }

  public void setMaxPlayersInSession(int maxPlayersInSession) {
    this.maxPlayersInSession = maxPlayersInSession;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getMaxPlayersInSession() {
    return this.maxPlayersInSession;
  }
}
