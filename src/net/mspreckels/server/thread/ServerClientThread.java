package net.mspreckels.server.thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import net.mspreckels.enums.AppState;
import net.mspreckels.message.ServerMessage;
import net.mspreckels.message.ServerMessageType;

public class ServerClientThread extends Thread {

  private ObjectOutputStream objectOutputStream;
  private ObjectInputStream objectInputStream;

  public ServerClientThread(Socket client) throws IOException {

    objectOutputStream = new ObjectOutputStream(client.getOutputStream());
    objectInputStream = new ObjectInputStream(client.getInputStream());
    objectOutputStream.flush();

    ServerMessage message = new ServerMessage();
    message.setDescription("Testing.");
    message.setType(ServerMessageType.PRINT);
    message.setPayload("Hello from Server");
    objectOutputStream.writeObject(message);

    //get client reponse

    message = new ServerMessage();
    message.setDescription("Shutting down .");
    message.setType(ServerMessageType.CHANGE_APPSTATE);
    message.setPayload(AppState.SHUTDOWN);
    objectOutputStream.writeObject(message);
  }
}
