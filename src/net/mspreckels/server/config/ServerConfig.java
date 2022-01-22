package net.mspreckels.server.config;

public class ServerConfig {
  private int port;

  /**
   * Creates a server config.
   * default port is 3333
   */
  public ServerConfig() {
    this.port = 3333;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }
}
