package net.mspreckels.message;

import java.io.Serializable;
import net.mspreckels.client.Client;

public class ClientMessage implements Serializable {
  private String description;
  private ClientMessageType type;
  private Object payload;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ClientMessageType getType() {
    return type;
  }

  public void setType(ClientMessageType type) {
    this.type = type;
  }

  public Object getPayload() {
    return payload;
  }

  public void setPayload(Object payload) {
    this.payload = payload;
  }
}
