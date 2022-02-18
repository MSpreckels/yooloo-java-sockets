package de.mspreckels.client.config;

public class ClientConfig {

  private String ip;
  private int port;

  /**
   * Creates a client config. default ip is "localhost" default port is 3333
   */
  public ClientConfig() {
    this.ip = "localhost";
    this.port = 3333;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }
}
