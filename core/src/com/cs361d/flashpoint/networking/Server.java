package com.cs361d.flashpoint.networking;

import com.cs361d.flashpoint.manager.*;
import com.cs361d.flashpoint.model.BoardElements.*;
import com.cs361d.flashpoint.model.FireFighterSpecialities.FireFighterAdvanceSpecialties;
import com.cs361d.flashpoint.model.FireFighterSpecialities.FireFighterAdvanced;
import com.cs361d.flashpoint.screen.Actions;
import com.cs361d.flashpoint.screen.FlashPointServerGame;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Logger;

public class Server implements Runnable {

  private static final List<FireFighterColor> notYetAssigned = new ArrayList<FireFighterColor>();
  private static String savedIp = "";
  // Vector to store server to client threads
  private static final HashMap<String, ServerToClientRunnable> clientObservers =
      new HashMap<String, ServerToClientRunnable>();

  // Map between IP and client
  private static final HashMap<String, Client> clientList = new HashMap<String, Client>();

  // Hash Map to map the users wirth the Firefighter colors
  private static final HashMap<FireFighterColor, String> colorsToClient =
      new HashMap<FireFighterColor, String>();

  private static Server instance;
  private static boolean gameLoaded = false;
  private static List<String> chatMessages = new ArrayList<String>();
  public static boolean gameHasEnded = false;
  public static String messageToEnd = "";
  public static boolean saveIp = false;

  // counter for clientObservers
  static int i = 0;

  static ServerSocket ss; // Server Socket
  Socket s; // Client socket
  Thread startServer; // DON'T SEND TO SRC CLIENT TWICE

  private static final Logger LOGGER = Logger.getLogger(NetworkLogger.class.getPackage().getName());

  // A client has an instance of the game
  public static FlashPointServerGame serverFPGame = new FlashPointServerGame();

  @Override
  public void run() {
    // running infinite loop for getting client request
    while (true) {
      // Accept the incoming request
      try {
        s = ss.accept(); // s is the client socket
        //        System.out.println("New client request received : " + s);
        LOGGER.info("New client request received : " + s);

        // obtain input and output streams or the client
        DataInputStream din = new DataInputStream(s.getInputStream());
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());

        //        System.out.println("Creating a new handler for this client...");
        LOGGER.info("Creating a new handler for this client...");

        String ip = s.getInetAddress().toString().replace("/", "");

        // Create a new handler object for handling this request.
        ServerToClientRunnable clientObserver =
            new ServerToClientRunnable(s, "client " + i, din, dout, ip);

        // Create a new Thread with this client.
        Thread t = new Thread(clientObserver);
        //        System.out.println("Adding this client to active client list");
        LOGGER.info("Adding this client to active client list");

        // add this client to active clientObservers list
        clientObservers.put(ip, clientObserver);

        //        System.out.println("Client Ip is: " + ip);
        //        System.out.println();
        LOGGER.info("Client Ip is: " + ip + "\n");

        t.start(); // start the thread for the client

        i++; // increment i for new client

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  // Constructor
  private Server(int serverPort) {
    try {
      ss = new ServerSocket(serverPort);
      startServer = new Thread(this);
      startServer.start();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Server createServer() {
    instance = new Server(NetworkManager.DEFAULT_SERVER_PORT);
    //    System.out.println("Server is starting...");
    LOGGER.info("Server is starting...");
    return instance;
  }

  public static Server getServer() {
    if (instance == null) {
      throw new IllegalArgumentException("You are not the server you should not ask to access it");
    }
    return instance;
  }

  // public static ArrayList<Thread> getClientThreads() { return clientThreads; }

  public static boolean isEmpty() {
    return notYetAssigned.isEmpty();
  }

  public static boolean joinCompleted() {
    return notYetAssigned.isEmpty();
  }

  public static synchronized boolean assignFireFighterToClient(String IP) {
    if (notYetAssigned.isEmpty()) {
      return false;
    }
    FireFighterColor color = notYetAssigned.remove(0);
    colorsToClient.put(color, IP);
    sendCommandToSpecificClient(ClientCommands.ASSIGN_FIREFIGHTER, color.toString(), IP);
    return true;
  }

  public static void setFireFighterAssignArray() {
    notYetAssigned.clear();
    savedIp = "";
    Iterator<FireFighter> it = FireFighterTurnManager.getInstance().iterator();
    if (!it.hasNext()) {
      throw new IllegalArgumentException(
          "Cannot call this function if the game board has not been initialized");
    }
    while (it.hasNext()) {
      FireFighter f = it.next();
      notYetAssigned.add(f.getColor());
    }
  }

  /* Send a message to from server */
  private static synchronized void sendMsgToAllClients(String msg) {
    try {
      for (ServerToClientRunnable mc : Server.clientObservers.values()) {
        mc.dout.writeUTF(msg);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static synchronized void sendMsgSpecificClient(String message, String IP) {
    try {
      ServerToClientRunnable client = clientObservers.get(IP);
      client.dout.writeUTF(message);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /* Get info about the server's machine */
  public static String getMyHostName() {
    String hostname = null;
    try {
      InetAddress addr = InetAddress.getLocalHost();
      hostname = addr.getHostName();
      //      System.out.println("Host Name = " + hostname);
      LOGGER.info("Host Name = " + hostname);

    } catch (UnknownHostException e) {
      e.printStackTrace();
    }

    return hostname;
  }

  // Remove Client from Server's and Network's List
  public static void closeClient(String clientIP) {
    // Remove clientHandler from Hashmap
    clientObservers.remove(clientIP);

    // Remove Client from Network
    clientList.remove(clientIP);
    FireFighterColor color = FireFighterColor.NOT_ASSIGNED;
    for (FireFighterColor c : colorsToClient.keySet()) {
      if (clientIP.equals(colorsToClient.get(c))) {
        color = c;
        break;
      }
    }
    if (color != FireFighterColor.NOT_ASSIGNED) {
      JSONObject object = new JSONObject();
      object.put("title", "The player with color " + color + " closed its window!");
      object.put("message", "A player left the game!");
      colorsToClient.clear();
      savedIp = "";
      notYetAssigned.clear();
      chatMessages.clear();
      gameLoaded = false;
      JSONArray array = new JSONArray();
      for (String game : DBHandler.listFilesOfSavedGames()) {
        array.add(game);
      }
      Server.sendCommandToAllClients(ClientCommands.EXIT_GAME, array.toJSONString());
      Server.sendCommandToAllClients(ClientCommands.SHOW_MESSAGE_ON_SCREEN, object.toJSONString());
    }
    //    System.out.println("Client with IP: " + clientIP + " is removed from the Network
    // successfully");
    //    System.out.println("Number of Clients Remaining on Network: " + clientList.size());
    LOGGER.info("Client with IP: " + clientIP + " is removed from the Network successfully");
    LOGGER.info("Number of Clients Remaining on Network: " + clientList.size());
  }

  // Now all server commands handled in the server
  public static void serverExecuteCommand(String msg) {
    try {
      JSONParser parser = new JSONParser();

      JSONObject jsonObject = (JSONObject) parser.parse(msg);
      ServerCommands c = ServerCommands.fromString(jsonObject.get("command").toString());
      String message = jsonObject.get("message").toString();
      String ip = jsonObject.get("IP").toString();
      //      System.out.println(message);
      LOGGER.info(message);
      boolean mustSendAndRefresh = false;

      switch (c) {
        case ADD_CHAT_MESSAGE:
          synchronized (Server.class) {
            chatMessages.add(message);
            Server.sendCommandToAllClients(ClientCommands.ADD_CHAT_MESSAGE, message);
          }
          break;

        case GET_CHAT_MESSAGES:
          synchronized (Server.class) {
            Server.sendCommandToSpecificClient(
                ClientCommands.SEND_CHAT_MESSAGES, getChatAsJsonString(), ip);
          }
          break;

        case SAVE:
          DBHandler.saveBoardToDB(BoardManager.getInstance().getGameName());
          JSONObject obj = new JSONObject();
          obj.put("title", "Saved Game");
          obj.put("message", "Game successfully saved!");
          Server.sendCommandToSpecificClient(
              ClientCommands.SHOW_MESSAGE_ON_SCREEN, obj.toJSONString(), ip);
          Server.sendCommandToAllClients(ClientCommands.REFRESH_LOBBY_SCREEN, "");
          break;

        case EXIT_GAME:
          JSONObject object = new JSONObject();
          object.put("title", "The player: " + message + " left the game");
          object.put("message", "A player left the game!");
          gameLoaded = false;
          savedIp = "";
          notYetAssigned.clear();
          colorsToClient.clear();
          chatMessages.clear();
          JSONArray array = new JSONArray();
          for (String game : DBHandler.listFilesOfSavedGames()) {
            array.add(game);
          }
          Server.sendCommandToAllClients(ClientCommands.EXIT_GAME, array.toJSONString());
          Server.sendCommandToAllClients(
              ClientCommands.SHOW_MESSAGE_ON_SCREEN, object.toJSONString());
          break;

        case LOAD_GAME:
          synchronized (Server.class) {
            if (!gameLoaded) {
              CreateNewGameManager.loadSavedGame(message);
              Server.setFireFighterAssignArray();
              gameLoaded = true;
              assignFireFighterToClient(ip);
              sendCommandToSpecificClient(
                  ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString(), ip);
              sendCommandToSpecificClient(ClientCommands.SET_BOARD_SCREEN, "", ip);
              Server.sendCommandToAllClients(ClientCommands.REFRESH_LOBBY_SCREEN, "");
            } else {
              JSONObject obj1 = new JSONObject();
              obj1.put("title", "A game is already Loaded");
              obj1.put("message", "Wait or try to join the game");
              sendCommandToSpecificClient(
                  ClientCommands.SHOW_MESSAGE_ON_SCREEN, obj1.toJSONString(), ip);
            }
          }
          break;

        case JOIN:
          synchronized (Server.class) {
            if (gameLoaded) {
              if (assignFireFighterToClient(ip)) {
                sendCommandToSpecificClient(
                    ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString(), ip);
                sendCommandToSpecificClient(ClientCommands.SET_BOARD_SCREEN, "", ip);
                Server.sendCommandToAllClients(ClientCommands.REFRESH_LOBBY_SCREEN, "");
                JSONObject obj1 = new JSONObject();
                String rightSide =
                    isEmpty()
                        ? "game is now full!"
                        : notYetAssigned.size() + " players left to join";
                obj1.put("title", "A new player joined the game");
                obj1.put("message", "The player: " + message + " joined the game, " + rightSide);
                sendToClientsInGame(ClientCommands.SHOW_MESSAGE_ON_SCREEN, obj1.toJSONString());

              } else {
                JSONObject obj1 = new JSONObject();
                obj1.put("title", "Game currently full");
                obj1.put("message", "The game is full try later");
                sendCommandToSpecificClient(
                    ClientCommands.SHOW_MESSAGE_ON_SCREEN, obj1.toJSONString(), ip);
              }
            } else {
              JSONObject obj1 = new JSONObject();
              obj1.put("title", "No game Loaded");
              obj1.put("message", "There are no game loaded feel free to create or load one");
              sendCommandToSpecificClient(
                  ClientCommands.SHOW_MESSAGE_ON_SCREEN, obj1.toJSONString(), ip);
            }
          }
          break;

        case CREATE_GAME:
          synchronized (Server.class) {
            if (!gameLoaded) {
              gameLoaded = true;
              jsonObject = (JSONObject) parser.parse(message);
              int numPlayers = Integer.parseInt(jsonObject.get("numPlayers").toString());
              MapKind mapKind = MapKind.fromString(jsonObject.get("mapKind").toString());
              String name = jsonObject.get("name").toString();
              Difficulty difficulty =
                  Difficulty.fromString(jsonObject.get("Difficulty").toString());
              CreateNewGameManager.createNewGame(name, numPlayers, mapKind, difficulty);
              Server.setFireFighterAssignArray();
              assignFireFighterToClient(ip);
              sendCommandToSpecificClient(
                  ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString(), ip);
              sendCommandToSpecificClient(ClientCommands.SET_BOARD_SCREEN, "", ip);
              Server.sendCommandToAllClients(ClientCommands.REFRESH_LOBBY_SCREEN, "");
            } else {
              JSONObject obj1 = new JSONObject();
              obj1.put("title", "Game already Loaded");
              obj1.put("message", "There is a game already loaded");
              sendCommandToSpecificClient(
                  ClientCommands.SHOW_MESSAGE_ON_SCREEN, obj1.toJSONString(), ip);
            }
          }
          break;

        case SET_INITIAL_SPECIALTY:
          synchronized (Server.class) {
            if (FireFighterTurnManagerAdvance.getInstance()
                .setInitialSpecialty(FireFighterAdvanceSpecialties.fromString(message))) {
              mustSendAndRefresh = true;
            }
          }
          break;

        case CHOOSE_INITIAL_POSITION:
          jsonObject = (JSONObject) parser.parse(message);
          int i = Integer.parseInt(jsonObject.get("i").toString());
          int j = Integer.parseInt(jsonObject.get("j").toString());
          Tile t = BoardManager.getInstance().getTileAt(i, j);
          FireFighterTurnManager.getInstance().chooseInitialPosition(t);
          mustSendAndRefresh = true;
          break;

        case SET_AMBULANCE:
          jsonObject = (JSONObject) parser.parse(message);
          i = Integer.parseInt(jsonObject.get("i").toString());
          j = Integer.parseInt(jsonObject.get("j").toString());
          t = BoardManager.getInstance().getTileAt(i, j);
          (BoardManagerAdvanced.getInstance()).addAmbulance(i, j);
          mustSendAndRefresh = true;
          break;

        case SET_FIRETRUCK:
          jsonObject = (JSONObject) parser.parse(message);
          i = Integer.parseInt(jsonObject.get("i").toString());
          j = Integer.parseInt(jsonObject.get("j").toString());
          (BoardManagerAdvanced.getInstance()).addFireTruck(i, j);
          mustSendAndRefresh = true;
          break;

        case ACCEPT_MOVE_BY_CAPTAIN:
          FireFighterTurnManagerAdvance.getInstance().setAccept(Boolean.parseBoolean(message));
          FireFighterTurnManagerAdvance.getInstance().stopWaiting();
          break;

        case ASK_DRIVER_MSG:
          UserResponse response = UserResponse.fromString(message);
          FireFighterTurnManagerAdvance.getInstance().setUserResponse(response);
          FireFighterTurnManagerAdvance.getInstance().stopWaiting();
          break;

        case REPLY_KNOCKED_DOWN_CHOICE:
          jsonObject = (JSONObject) parser.parse(message);
          boolean letKnockDown = (Boolean) jsonObject.get("value");
          Direction d = Direction.fromString(jsonObject.get("direction").toString());
          if (!letKnockDown) {
            FireFighterColor color = getColorFromIP(ip);
            FireFighterAdvanced f =
                FireFighterTurnManagerAdvance.getInstance().getFireFighter(color);
            BoardManagerAdvanced.getInstance().setLetKnockedDown(false);
            BoardManagerAdvanced.getInstance().moveForKnockDown(f, d);
          }
          BoardManagerAdvanced.getInstance().stopWaiting();
          break;

        case REPLY_MOVE_WITH_VEHICLE:
          FireFighterTurnManagerAdvance.getInstance()
              .setUserResponse(UserResponse.fromString(message));
          FireFighterTurnManagerAdvance.getInstance().stopWaiting();
          break;

        case GET_SAVED_GAMES:
          synchronized (Server.class) {
            array = new JSONArray();
            for (String game : DBHandler.listFilesOfSavedGames()) {
              array.add(game);
            }
            obj = new JSONObject();
            if (gameLoaded) {
              obj = new JSONObject();
              obj.put("name", BoardManager.getInstance().getGameName());
              obj.put("numPlayer", notYetAssigned.size());
            }
            else {
              obj.put("name", "");
              obj.put("numPlayer", -1);
            }
            obj.put("games",array);
            Server.sendCommandToSpecificClient(
                ClientCommands.LOAD_SAVED_GAMES, obj.toJSONString(), ip);
          }
          break;
        default:
          // the command must be an action command
          serverExecuteGameCommand(jsonObject.get("command").toString(), message, ip);
      }
      if (mustSendAndRefresh) {
        Server.sendToClientsInGame(ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString());
        Server.sendToClientsInGame(ClientCommands.REFRESH_BOARD_SCREEN, "");
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void sendCommandToAllClients(ClientCommands command, String msg) {
    String jsonMsg = NetworkManager.createJSON(command.toString(), msg);
    sendMsgToAllClients(jsonMsg);
  }

  public static void sendCommandToSpecificClient(ClientCommands command, String msg, String IP) {
    String jsonMsg = NetworkManager.createJSON(command.toString(), msg);
    sendMsgSpecificClient(jsonMsg, IP);
  }

  public void addNewClient(String ip, Client c) {
    this.clientList.put(ip, c);
  }

  public static synchronized void serverExecuteGameCommand(
      String gameCommand, String message, String ip) {
    if (savedIp.equals(ip)) {
      return;
    }
    Actions action = Actions.fromString(gameCommand);
    JSONParser parser = new JSONParser();
    try {
      Direction direction;
      boolean mustSendAndRefresh = false;
      switch (action) {
        case MOVE:
          direction = Direction.fromString(message);
          mustSendAndRefresh = FireFighterTurnManager.getInstance().move(direction);
          break;

        case MOVE_WITH_VICTIM:
          direction = Direction.fromString(message);
          mustSendAndRefresh = FireFighterTurnManager.getInstance().moveWithVictim(direction);

          break;

        case CHOP:
          direction = Direction.fromString(message);
          mustSendAndRefresh = FireFighterTurnManager.getInstance().chopWall(direction);
          break;
        case EXTINGUISH:
          direction = Direction.fromString(message);
          mustSendAndRefresh = FireFighterTurnManager.getInstance().extinguishFire(direction);
          break;
        case INTERACT_WITH_DOOR:
          direction = Direction.fromString(message);
          mustSendAndRefresh = FireFighterTurnManager.getInstance().interactWithDoor(direction);
          break;

        case SAVE:
          DBHandler.saveBoardToDB(BoardManager.getInstance().getGameName());
          break;

        case END_TURN:
          FireFighterTurnManager.getInstance().endTurn();
          mustSendAndRefresh = saveIp;
          if (saveIp) {
            savedIp = ip;
            saveIp = false;
          }
          break;

        case FIRE_DECK_GUN:
          mustSendAndRefresh = FireFighterTurnManagerAdvance.getInstance().fireDeckGun();
          break;

        case MOVE_WITH_HAZMAT:
          direction = Direction.fromString(message);
          mustSendAndRefresh =
              FireFighterTurnManagerAdvance.getInstance().moveWithHazmat(direction);
          break;

        case DRIVE_AMBULANCE:
          direction = Direction.fromString(message);
          mustSendAndRefresh =
              FireFighterTurnManagerAdvance.getInstance().driveAmbulance(direction);
          break;
        case DRIVE_FIRETRUCK:
          direction = Direction.fromString(message);
          mustSendAndRefresh =
              FireFighterTurnManagerAdvance.getInstance().driveFireTruck(direction);
          break;

        case REMOVE_HAZMAT:
          mustSendAndRefresh = FireFighterTurnManagerAdvance.getInstance().disposeHazmat();
          break;

        case FLIP_POI:
          JSONObject jsonObject = (JSONObject) parser.parse(message);
          int i = Integer.parseInt(jsonObject.get("i").toString());
          int j = Integer.parseInt(jsonObject.get("j").toString());
          Tile t = BoardManager.getInstance().getTileAt(i, j);
          mustSendAndRefresh = FireFighterTurnManagerAdvance.getInstance().flipPOI(t);
          break;

        case CURE_VICTIM:
          mustSendAndRefresh = FireFighterTurnManagerAdvance.getInstance().treatVictim();
          break;

        case CREW_CHANGE:
          if (FireFighterTurnManagerAdvance.getInstance()
              .crewChange(FireFighterAdvanceSpecialties.fromString(message))) {
            mustSendAndRefresh = true;
          }
          break;

        case COMMAND_FIREMEN:
          jsonObject = (JSONObject) parser.parse(message);
          FireFighterColor color = FireFighterColor.fromString(jsonObject.get("color").toString());
          action = Actions.fromString(jsonObject.get("action").toString());
          direction = Direction.fromString(jsonObject.get("direction").toString());
          mustSendAndRefresh =
              FireFighterTurnManagerAdvance.getInstance()
                  .fireCaptainCommand(color, action, direction);
          break;

        case REPAIR_WALL:
          direction = Direction.fromString(message);
          mustSendAndRefresh = FireFighterTurnManagerAdvance.getInstance().repairWall(direction);
          break;

        case CLEAR_HOTSPOT:
          mustSendAndRefresh = FireFighterTurnManagerAdvance.getInstance().clearHotSpot();
          break;

        case SPREAD_FIRE:
          mustSendAndRefresh = FireFighterTurnManagerAdvance.getInstance().spreadFire(Direction.fromString(message));
          break;

        default:
      }
      if (mustSendAndRefresh) {
        Server.sendToClientsInGame(ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString());
        Server.sendToClientsInGame(ClientCommands.REFRESH_BOARD_SCREEN, "");
        if (gameHasEnded) {
          gameHasEnded = false;
          gameLoaded = false;
          Server.sendToClientsInGame(ClientCommands.GAME_HAS_ENDED, messageToEnd);
        }
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  public static String getClientIP(FireFighterColor color) {
    return colorsToClient.get(color);
  }

  public static String getChatAsJsonString() {
    JSONArray array = new JSONArray();
    for (String message : chatMessages) {
      array.add(message);
    }
    return array.toJSONString();
  }

  public static void sendToClientsInGame(ClientCommands command, String message) {
    for (String ip : colorsToClient.values()) {
      sendCommandToSpecificClient(command, message, ip);
    }
  }

  public static FireFighterColor getColorFromIP(String ip) {
    for (FireFighterColor color : colorsToClient.keySet()) {
      if (colorsToClient.get(color).equals(ip)) {
        return color;
      }
    }
    return FireFighterColor.NOT_ASSIGNED;
  }
}
