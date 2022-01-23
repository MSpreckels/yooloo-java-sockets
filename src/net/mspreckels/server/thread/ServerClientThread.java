package net.mspreckels.server.thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import net.mspreckels.enums.AppState;
import net.mspreckels.logger.Logger;
import net.mspreckels.logger.Logger.Level;
import net.mspreckels.client.message.ClientMessage;
import net.mspreckels.server.message.ServerMessage;
import net.mspreckels.server.message.ServerMessageType;

public class ServerClientThread extends Thread {

  private static final Logger LOG = new Logger(ServerClientThread.class);

  private ObjectOutputStream objectOutputStream;
  private ObjectInputStream objectInputStream;

  public ServerClientThread(Socket client) throws IOException, ClassNotFoundException {

    objectOutputStream = new ObjectOutputStream(client.getOutputStream());
    objectInputStream = new ObjectInputStream(client.getInputStream());
    objectOutputStream.flush();

    LOG.log(Level.INFO, "Sending print command to %s", client.getRemoteSocketAddress());
    ServerMessage message = new ServerMessage();
    message.setDescription("Testing.");
    message.setType(ServerMessageType.PRINT);
    message.setPayload("Hello from Server");
    objectOutputStream.writeObject(message);

    //wait for response
    ClientMessage response = readInputStream(ClientMessage.class);
    LOG.log(Level.INFO, "Client response: %s %s", response.getType(), response.getPayload());

    LOG.log(Level.INFO, "Sending shutdown command to %s", client.getRemoteSocketAddress());
    message = new ServerMessage();
    message.setDescription("Shutting down .");
    message.setType(ServerMessageType.CHANGE_APPSTATE);
    message.setPayload(AppState.SHUTDOWN);
    objectOutputStream.writeObject(message);

    //wait for response
    response = readInputStream(ClientMessage.class);
    LOG.log(Level.INFO, "Client response: %s %s", response.getType(), response.getPayload());

  }

  private <T> T readInputStream(Class<T> clazz) throws IOException, ClassNotFoundException {
    return (T) this.objectInputStream.readObject();
  }
}
