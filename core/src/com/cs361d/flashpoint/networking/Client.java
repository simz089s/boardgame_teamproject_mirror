package com.cs361d.flashpoint.networking;

import com.badlogic.gdx.Gdx;
import com.cs361d.flashpoint.manager.CreateNewGameManager;
import com.cs361d.flashpoint.manager.DBHandler;
import com.cs361d.flashpoint.manager.User;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.screen.Actions;
import com.cs361d.flashpoint.screen.BoardChatFragment;
import com.cs361d.flashpoint.screen.BoardScreen;
import com.cs361d.flashpoint.screen.FlashPointGame;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

  // A client has an instance of the game
  public static FlashPointGame clientFPGame = new FlashPointGame();

  private Socket s;
  private DataInputStream din; // client-from-server input stream
  private DataOutputStream dout; // client-to-server output stream
  private String clientIP;
  private boolean notStopped = true;

  private static Client instance;

  public static Client createClient() {
    try {
      instance = new Client(NetworkManager.DEFAULT_SERVER_IP, NetworkManager.DEFAULT_SERVER_PORT);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return instance;
  }

  public static Client getInstance() {
    return instance;
  }

  private Client(String serverIP, int serverPort) throws IOException {
    try {

      // Attempt to connect to server
      s = new Socket(serverIP, serverPort);

      // obtaining input and out streams
      din = new DataInputStream(s.getInputStream());
      dout = new DataOutputStream(s.getOutputStream());

      // obtain client IP address
      clientIP = s.getInetAddress().toString().replace("/", "");

      // readMessage thread constantly listening
      Thread readMessage =
          new Thread(
              new Runnable() {
                @Override
                public void run() {
                  while (notStopped) {
                    String msg;
                    try {
                      // read the message sent to this client
                      msg = din.readUTF();
                      clientExecuteCommand(msg);
                    } catch (Exception connectionLost) {
                      try {
                        System.out.println("Server disconnected: closing client...");
                        // Closing resources
                        din.close();
                        dout.close();
                        s.close();
                        System.out.println(
                            "Streams and Socket closed for Client with IP: " + clientIP);

                        // Close Reader Thread
                        stopClientReadFromClientHandlerThread();
                        System.out.println(
                            "Reader Thread terminated or Client with IP: " + clientIP);

                      } catch (IOException e) {
                        System.out.println(
                            "Unable to close Streams for Client with IP: " + clientIP);
                        e.printStackTrace();
                      }
                    }
                  }
                }
              });
      readMessage.start();

    } catch (UnknownHostException e) {
      System.out.println("Server Not Found");
      e.printStackTrace();
    }
  }

  public void stopClientReadFromClientHandlerThread() {
    notStopped = false;
  }

  /* Getters */
  public String getClientIP() {
    return clientIP;
  }

  public DataInputStream getDin() {
    return din;
  }

  public DataOutputStream getDout() {
    return dout;
  }

  /* Send a message to a server */
  private synchronized void sendMsgToServer(String msg) {
    try {
      // write on the output stream
      dout.writeUTF(msg);
      dout.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void stopClientReadingThread() {
    notStopped = false;
  }

  // Now in client
  public static void clientExecuteCommand(String msg) {
    try {
      JSONParser parser = new JSONParser();

      JSONObject jsonObject = (JSONObject) parser.parse(msg);
      ClientCommands c = ClientCommands.fromString(jsonObject.get("command").toString());
      final String message = jsonObject.get("message").toString();
      System.out.println(message);
      switch (c) {
        case EXIT_GAME:
          Gdx.app.postRunnable(
              new Runnable() {
                @Override
                public void run() {
                  BoardScreen.setLobbyPage();
                }
              });
          break;

        case SET_BOARD_SCREEN:
          Gdx.app.postRunnable(
              new Runnable() {
                @Override
                public void run() {
                  BoardScreen.setBoardScreen();
                }
              });
          break;
        case SEND_CHAT_MESSAGES:
          Gdx.app.postRunnable(
              new Runnable() {
                @Override
                public void run() {
                  //                      BoardScreen.setSideFragment(Fragment.CHAT);
                }
              });
          JSONArray jsa = (JSONArray) parser.parse(message);
          for (Object a : jsa) {
            final String newMessage = a.toString();
            Gdx.app.postRunnable(
                new Runnable() {
                  @Override
                  public void run() {
                    final String msg = newMessage;
                    BoardChatFragment.addMessageToChat(msg);
                  }
                });
          }
          break;

        case ADD_CHAT_MESSAGE:
          if (!message.equals("")) {
            Gdx.app.postRunnable(
                new Runnable() {
                  @Override
                  public void run() {
                    //                      if (BoardScreen.isChatFragment())
                    BoardChatFragment.addMessageToChat(message);
                  }
                });
          }
          break;

        case SET_GAME_STATE:
          CreateNewGameManager.loadGameFromString(message);
          break;

        case ASSIGN_FIREFIGHTER:
          User.getInstance().assignFireFighter(FireFighterColor.fromString(message));
          break;

        case REFRESH_BOARD_SCREEN:
          Gdx.app.postRunnable(
              new Runnable() {
                @Override
                public void run() {
                  BoardScreen.redrawAfterMove();
                }
              });
          break;

        case SHOW_MESSAGE_ON_GAME_SCREEN:
          jsonObject = (JSONObject) parser.parse(message);
          final String title = jsonObject.get("title").toString();
          final String message1 = jsonObject.get("message").toString();
          Gdx.app.postRunnable(
              new Runnable() {
                @Override
                public void run() {
                  BoardScreen.getDialog().drawDialog(title, message1);
                }
              });
          break;
        default:
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void sendCommand(ServerCommands command, String msg) {
    String jsonMsg = NetworkManager.createJSON(command.toString(), msg);
    this.sendMsgToServer(jsonMsg);
  }

  public void sendCommand(Actions command, String msg) {
    String jsonMsg = NetworkManager.createJSON(command.toString(), msg);
    this.sendMsgToServer(jsonMsg);
  }
}
