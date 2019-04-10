package com.cs361d.flashpoint.networking;

import com.badlogic.gdx.Gdx;
import com.cs361d.flashpoint.manager.CreateNewGameManager;
import com.cs361d.flashpoint.manager.User;
import com.cs361d.flashpoint.model.BoardElements.CarrierStatus;
import com.cs361d.flashpoint.model.BoardElements.Direction;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.screen.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Client {

  // A client has an instance of the game
  public static FlashPointGame clientFPGame = new FlashPointGame();

  private Socket s;
  private DataInputStream din; // client-from-server input stream
  private DataOutputStream dout; // client-to-server output stream
  private String clientIP;
  private boolean notStopped = true;
  public static String serverIP = NetworkManager.DEFAULT_SERVER_IP;

  private static Client instance;

  private static final Logger LOGGER = Logger.getLogger(NetworkLogger.class.getPackage().getName());

  public static Client createClient() {
//    System.out.println(
//        "If the server IP changed please enter it here (otherwise enter an invalid IP address string) :");
//    Scanner sc = new Scanner(System.in);
    try {
//      String newIP = sc.next().trim();
//      if (newIP.matches(
//          "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")) {
//        serverIP = newIP;
//      }
      System.out.println("Using server IP : " + serverIP);
      instance = new Client(serverIP, NetworkManager.DEFAULT_SERVER_PORT);
      //      System.out.println("Client is starting...");
      LOGGER.info("Client is starting...");
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
//      sc.close();
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
                    try {
                      // read the message sent to this client
                      final String msg = din.readUTF();
                      Thread newThread =
                          new Thread(
                              new Runnable() {
                                @Override
                                public void run() {
                                  clientExecuteCommand(msg);
                                }
                              });
                      newThread.start();
                    } catch (Exception connectionLost) {
                      try {
                        //                        System.out.println("Server disconnected: closing
                        // client...");
                        LOGGER.info("Server disconnected: closing client...");
                        // Closing resources
                        din.close();
                        dout.close();
                        s.close();
                        //                        System.out.println(
                        //                            "Streams and Socket closed for Client with IP:
                        // " + clientIP);
                        LOGGER.info("Streams and Socket closed for Client with IP: " + clientIP);

                        // Close Reader Thread
                        stopClientReadFromClientHandlerThread();
                        //                        System.out.println(
                        //                            "Reader Thread terminated or Client with IP: "
                        // + clientIP);
                        LOGGER.info("Reader Thread terminated or Client with IP: " + clientIP);

                      } catch (IOException e) {
                        //                        System.out.println(
                        //                            "Unable to close Streams for Client with IP: "
                        // + clientIP);
                        LOGGER.info("Unable to close Streams for Client with IP: " + clientIP);
                        e.printStackTrace();
                      }
                    }
                  }
                }
              });
      readMessage.start();

    } catch (UnknownHostException e) {
      //      System.out.println("Server Not Found");
      LOGGER.info("Server Not Found");
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
  public static synchronized void clientExecuteCommand(String msg) {
    try {
      JSONParser parser = new JSONParser();

      JSONObject jsonObject = (JSONObject) parser.parse(msg);
      ClientCommands c = ClientCommands.fromString(jsonObject.get("command").toString());
      final String message = jsonObject.get("message").toString();
      System.out.println(message); // Do not log
      LOGGER.info("A client sent the command : " + c.toString());
      switch (c) {
        case EXIT_GAME:
          JSONArray array = (JSONArray) parser.parse(message);
          final ArrayList<String> g = new ArrayList<String>();
          for (Object a : array) {
            g.add(a.toString());
          }
          Gdx.app.postRunnable(
              new Runnable() {
                @Override
                public void run() {
                  BoardScreen.setLobbyPageOutOfGame(g);
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
          final JSONArray jsa = (JSONArray) parser.parse(message);
          Gdx.app.postRunnable(
              new Runnable() {
                @Override
                public void run() {
                  BoardScreen.setSideFragment(Fragment.CHAT);
                  for (Object a : jsa) {
                    BoardChatFragment.addMessageToChat(a.toString());
                  }
                }
              });
          break;

        case ADD_CHAT_MESSAGE:
          if (!message.equals("")) {
            Gdx.app.postRunnable(
                new Runnable() {
                  @Override
                  public void run() {
                    if (BoardScreen.isChatFragment()) {
                      BoardChatFragment.addMessageToChat(message);
                    }
                  }
                });
          }
          break;
        case SET_CURRENT_GAME_STATS:
          jsonObject = (JSONObject) parser.parse(message);
          final String name = jsonObject.get("name").toString();
          final int numPlayer = Integer.parseInt(jsonObject.get("numPlayer").toString());
          Gdx.app.postRunnable(
              new Runnable() {
                @Override
                public void run() {
                  LobbyScreen.setLoadedGameName(name);
                  LobbyScreen.setNumPlayersLeftToJoin(numPlayer);
                }
              });
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
                  BoardScreen.redrawBoard();
                }
              });
          break;

        case ASK_TO_ACCEPT_MOVE:
          jsonObject = (JSONObject) parser.parse(message);
          final Actions actions = Actions.fromString(jsonObject.get("action").toString());
          final Direction d = Direction.fromString(jsonObject.get("direction").toString());
          Gdx.app.postRunnable(
              new Runnable() {
                @Override
                public void run() {
                  BoardAcceptCaptainCmdFragment.drawAcceptCaptainCmdPanel(actions, d);
                }
              });
          break;

        case ASK_DRIVER_MSG:
          final JSONObject jsob = (JSONObject) parser.parse(message);
          final JSONArray iArray = (JSONArray) parser.parse(jsob.get("i").toString());
          final JSONArray jArray = (JSONArray) parser.parse(jsob.get("j").toString());
          Gdx.app.postRunnable(
              new Runnable() {
                @Override
                public void run() {
                  for (int i = 0; i < iArray.size(); i++) {
                    BoardScreen.addFilterOnFireDeckGun(
                        Integer.parseInt(iArray.get(i).toString()),
                        Integer.parseInt(jArray.get(i).toString()));
                  }
                  BoardFireDeckGunPanel.drawFireDeckGunPanel();
                }
              });
          break;

        case SHOW_MESSAGE_ON_SCREEN:
          jsonObject = (JSONObject) parser.parse(message);
          final String title = jsonObject.get("title").toString();
          final String message1 = jsonObject.get("message").toString();
          Gdx.app.postRunnable(
              new Runnable() {
                @Override
                public void run() {
                  BoardScreen.displayMessage(title, message1);
                }
              });
          break;

        case ASK_WISH_ABOUT_KNOCK_DOWN:
          array = (JSONArray) parser.parse(message);
          final List<Direction> directions = new ArrayList<Direction>();
          for (Object o : array) {
            directions.add(Direction.fromString(o.toString()));
          }
          Gdx.app.postRunnable(
              new Runnable() {
                @Override
                public void run() {
                  BoardOnKnockDownPanel.drawOnKnockDownPanel(directions);
                }
              });
          break;

        case ASK_DRIVE_WITH_ENGINE:
          final CarrierStatus status = CarrierStatus.fromString(message);
          Gdx.app.postRunnable(
              new Runnable() {
                @Override
                public void run() {
                  BoardRideVehicleFragment.drawRideVehicleFragment(status);
                }
              });
          break;

        case GAME_HAS_ENDED:
          Gdx.app.postRunnable(
              new Runnable() {
                @Override
                public void run() {
                  BoardScreen.getDialog().drawEndGameDialog("GAME OVER", message);
                }
              });
          break;

        case LOAD_SAVED_GAMES:
          final ArrayList<String> games = new ArrayList<String>();
          JSONObject obj = (JSONObject) parser.parse(message);
          for (Object o : (JSONArray) parser.parse(obj.get("games").toString())) {
            games.add(o.toString());
          }
          final String nameL = obj.get("name").toString();
          final int numPlayerL = Integer.parseInt(obj.get("numPlayer").toString());
          Gdx.app.postRunnable(
              new Runnable() {
                @Override
                public void run() {
                  LobbyScreen.setSavedGames(games);
                  LobbyScreen.setLoadedGameName(nameL);
                  LobbyScreen.setNumPlayersLeftToJoin(numPlayerL);
                  BoardScreen.setLobbyPage();
                }
              });
          break;

        case REFRESH_LOBBY_SCREEN:
          Gdx.app.postRunnable(
              new Runnable() {
                @Override
                public void run() {
                  BoardScreen.refreshLobbyScreen();
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
