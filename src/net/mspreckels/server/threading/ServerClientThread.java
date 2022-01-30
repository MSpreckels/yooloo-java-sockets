package net.mspreckels.server.threading;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import net.mspreckels.client.enums.ClientState;
import net.mspreckels.logger.Logger;
import net.mspreckels.logger.Logger.Level;
import net.mspreckels.client.message.ClientMessage;
import net.mspreckels.server.message.ServerMessage;
import net.mspreckels.server.message.ServerMessageType;

public class ServerClientThread extends Thread {

  private static final Logger LOG = new Logger(ServerClientThread.class);

  private final ObjectOutputStream objectOutputStream;
  private final ObjectInputStream objectInputStream;

  private final Socket client;
  private final UUID uuid;

  public ServerClientThread(Socket client) throws IOException, ClassNotFoundException {

    this.client = client;
    this.uuid = UUID.randomUUID();

    objectOutputStream = new ObjectOutputStream(client.getOutputStream());
    objectInputStream = new ObjectInputStream(client.getInputStream());
    objectOutputStream.flush();

    LOG.log(Level.INFO, "(%s) Sending print command to %s", uuid, client.getRemoteSocketAddress());
    ServerMessage message = new ServerMessage();
    message.setDescription("Testing.");
    message.setType(ServerMessageType.IDENTITY);
    message.setPayload(uuid);
    objectOutputStream.writeObject(message);

    //wait for response
    ClientMessage response = readInputStream(ClientMessage.class);
    LOG.log(Level.INFO, "(%s) Client response: %s %s", uuid, response.getType(), response.getPayload());
  }

  public void shutdown() throws IOException, ClassNotFoundException {
    LOG.log(Level.INFO, "(%s) Sending shutdown command to %s", uuid, client.getRemoteSocketAddress());
    ServerMessage message = new ServerMessage();
    message.setDescription("Shutting down.");
    message.setType(ServerMessageType.SHUTDOWN);
    objectOutputStream.writeObject(message);

    //wait for response
    ClientMessage response = readInputStream(ClientMessage.class);
    LOG.log(Level.INFO, "(%s) Client response: %s %s", uuid, response.getType(), response.getPayload());

  }

  private <T> T readInputStream(Class<T> clazz) throws IOException, ClassNotFoundException {
    return (T) this.objectInputStream.readObject();
  }

  public List<Integer> getCards() throws IOException, ClassNotFoundException {
    LOG.log(Level.INFO, "(%s) Asking client for their card selection", uuid);
    ServerMessage message = new ServerMessage();
    message.setDescription("Fetching cards");
    message.setType(ServerMessageType.GET_CARDS);
    objectOutputStream.writeObject(message);

    ClientMessage response = readInputStream(ClientMessage.class);
    LOG.log(Level.INFO, "(%s) Client response: %s %s", uuid, response.getType(), response.getPayload());

    return (List<Integer>) response.getPayload();
  }

  public UUID getUUID() {
    return uuid;
  }

  public void sendMessage(String s) throws IOException, ClassNotFoundException {
    ServerMessage message = new ServerMessage();
    message.setDescription("Message");
    message.setType(ServerMessageType.PRINT);
    message.setPayload(s);
    objectOutputStream.writeObject(message);

    ClientMessage response = readInputStream(ClientMessage.class);
//    LOG.log(Level.INFO, "(%s) Client response: %s", uuid, response.getType(), response.getPayload());

  }
}
