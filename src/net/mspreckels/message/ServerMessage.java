package net.mspreckels.message;

import java.io.Serializable;

public class ServerMessage implements Serializable {
  private String description;
  private ServerMessageType type;
  private Object payload;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ServerMessageType getType() {
    return type;
  }

  public void setType(ServerMessageType type) {
    this.type = type;
  }

  public Object getPayload() {
    return payload;
  }

  public void setPayload(Object payload) {
    this.payload = payload;
  }
}
