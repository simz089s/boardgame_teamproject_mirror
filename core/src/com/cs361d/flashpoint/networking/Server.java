package com.cs361d.flashpoint.networking;

import com.cs361d.flashpoint.manager.*;
import com.cs361d.flashpoint.model.BoardElements.Direction;
import com.cs361d.flashpoint.model.BoardElements.FireFighter;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.model.BoardElements.Tile;
import com.cs361d.flashpoint.model.FireFighterSpecialities.FireFighterAdvanceSpecialities;
import com.cs361d.flashpoint.screen.Actions;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class Server implements Runnable {

  private static final List<FireFighterColor> notYetAssigned = new ArrayList<FireFighterColor>();

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

  // counter for clientObservers
  static int i = 0;

  static ServerSocket ss; // Server Socket
  Socket s; // Client socket
  Thread startServer; // DON'T SEND TO SRC CLIENT TWICE

  @Override
  public void run() {
    // running infinite loop for getting client request
    while (true) {
      // Accept the incoming request
      try {
        s = ss.accept(); // s is the client socket
        System.out.println("New client request received : " + s);

        // obtain input and output streams or the client
        DataInputStream din = new DataInputStream(s.getInputStream());
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());

        System.out.println("Creating a new handler for this client...");

        String ip = s.getInetAddress().toString().replace("/", "");

        // Create a new handler object for handling this request.
        ServerToClientRunnable clientObserver =
            new ServerToClientRunnable(s, "client " + i, din, dout, ip);

        // Create a new Thread with this client.
        Thread t = new Thread(clientObserver);
        System.out.println("Adding this client to active client list");

        // add this client to active clientObservers list
        clientObservers.put(ip, clientObserver);

        System.out.println("Client Ip is: " + ip);
        System.out.println();

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
    return instance;
  }

  public static Server getServer() {
    if (instance == null) {
      throw new IllegalArgumentException("You are not the server you should not ask to access it");
    }
    return instance;
  }

  // public static ArrayList<Thread> getClientThreads() { return clientThreads; }

  public boolean isEmpty() {
    return notYetAssigned.isEmpty();
  }

  public static boolean noMorePlayer() {
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
      System.out.println("Host Name = " + hostname);

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
      object.put("title","The player with color " + color + " closed its window!");
      object.put("message","Welcome back to the lobby!");
      colorsToClient.clear();
      notYetAssigned.clear();
      chatMessages.clear();
      gameLoaded = false;
      Server.sendCommandToAllClients(ClientCommands.EXIT_GAME,"");
      Server.sendCommandToAllClients(ClientCommands.SHOW_MESSAGE_ON_SCREEN,object.toJSONString());
    }
    System.out.println("Client with IP: " + clientIP + " is removed from the Network successfully");
    System.out.println("Number of Clients Remaining on Network: " + clientList.size());
  }

  // Now all server commands handled in the server
  public static void serverExecuteCommand(String msg) {
    try {
      JSONParser parser = new JSONParser();

      JSONObject jsonObject = (JSONObject) parser.parse(msg);
      ServerCommands c = ServerCommands.fromString(jsonObject.get("command").toString());
      String message = jsonObject.get("message").toString();
      String ip = jsonObject.get("IP").toString();
      System.out.println(message);
      boolean mustSendAndRefresh = false;
      boolean refreshCrewChangePanel = false;

      switch (c) {
        case ADD_CHAT_MESSAGE:
          chatMessages.add(message);
          Server.sendCommandToAllClients(ClientCommands.ADD_CHAT_MESSAGE,message);
          break;

        case GET_CHAT_MESSAGES:
          Server.sendCommandToSpecificClient(ClientCommands.SEND_CHAT_MESSAGES,getChatAsJsonString(),ip);
          break;

        case SAVE:
          DBHandler.saveBoardToDB(BoardManager.getInstance().getGameName());
          JSONObject obj = new JSONObject();
          obj.put("title", "Saved Game");
          obj.put("message", "Game successfully saved!");
          Server.sendCommandToSpecificClient(
              ClientCommands.SHOW_MESSAGE_ON_SCREEN, obj.toJSONString(), ip);
          break;

        case EXIT_GAME:
          JSONObject object = new JSONObject();
          object.put("title","The player: " + message + " left the game");
          object.put("message","Welcome back to the lobby!");
          gameLoaded = false;
          notYetAssigned.clear();
          colorsToClient.clear();
          chatMessages.clear();
          Server.sendCommandToAllClients(ClientCommands.EXIT_GAME, "");
          Server.sendCommandToAllClients(ClientCommands.SHOW_MESSAGE_ON_SCREEN,object.toJSONString());
          break;

        case LOAD_GAME:
          if (!gameLoaded) {
            CreateNewGameManager.loadSavedGame(message);
            Server.setFireFighterAssignArray();
            gameLoaded = true;
            assignFireFighterToClient(ip);
            sendCommandToSpecificClient(
                ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString(), ip);
            sendCommandToSpecificClient(ClientCommands.SET_BOARD_SCREEN, "", ip);
          } else {
            JSONObject obj1 = new JSONObject();
            obj1.put("title", "A game is already Loaded");
            obj1.put("message", "Wait or try to join the game");
            sendCommandToSpecificClient(
                ClientCommands.SHOW_MESSAGE_ON_SCREEN, obj1.toJSONString(), ip);
          }
          break;

        case JOIN:
          if (gameLoaded) {
            if (assignFireFighterToClient(ip)) {
              sendCommandToSpecificClient(
                  ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString(), ip);
              sendCommandToSpecificClient(ClientCommands.SET_BOARD_SCREEN, "", ip);
            }
            else {
              JSONObject obj1 = new JSONObject();
              obj1.put("title", "Game currently full");
              obj1.put("message", "The game is full try latter");
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
          break;

        case CREATE_GAME:
          if (!gameLoaded) {
            gameLoaded = true;
            jsonObject = (JSONObject) parser.parse(message);
            int numPlayers = Integer.parseInt(jsonObject.get("numPlayers").toString());
            MapKind mapKind = MapKind.fromString(jsonObject.get("mapKind").toString());
            String name = jsonObject.get("name").toString();
            Difficulty difficulty = Difficulty.fromString(jsonObject.get("Difficulty").toString());
            CreateNewGameManager.createNewGame(name, numPlayers, mapKind, difficulty);
            Server.setFireFighterAssignArray();
            assignFireFighterToClient(ip);
            sendCommandToSpecificClient(
                ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString(), ip);
            sendCommandToSpecificClient(ClientCommands.SET_BOARD_SCREEN, "", ip);
          }
          else {
            JSONObject obj1 = new JSONObject();
            obj1.put("title", "Game already Loaded");
            obj1.put("message", "There is a game already loaded");
            sendCommandToSpecificClient(
                    ClientCommands.SHOW_MESSAGE_ON_SCREEN, obj1.toJSONString(), ip);
          }
          break;

        case SET_INITIAL_SPECIALITY:
          if (FireFighterTurnManagerAdvance.getInstance().setInitialSpeciality(FireFighterAdvanceSpecialities.fromString(message))) {
            mustSendAndRefresh = true;
            refreshCrewChangePanel = true;
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

        default:
          // the command must be an action command
          serverExecuteGameCommand(jsonObject.get("command").toString(), message, ip);
      }
      if (mustSendAndRefresh) {
        Server.sendCommandToAllClients(ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString());
        Server.sendCommandToAllClients(ClientCommands.REFRESH_BOARD_SCREEN, "");
      }
      if (refreshCrewChangePanel) {
        Server.sendCommandToSpecificClient(ClientCommands.END_OF_SPECIALITY_CHANGE,"",ip);
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

  public static synchronized void serverExecuteGameCommand(String gameCommand, String message, String ip) {

    Actions action = Actions.fromString(gameCommand);
    JSONParser parser = new JSONParser();
    try {
      Direction direction;
      boolean mustSendAndRefresh = false;
      boolean refreshCrewChangePanel = false;
      switch (action) {
        case MOVE:
          JSONObject jsonObject = (JSONObject) parser.parse(message);
          direction = Direction.fromString(jsonObject.get("direction").toString());
          mustSendAndRefresh = FireFighterTurnManager.getInstance().move(direction);
          break;

        case MOVE_WITH_VICTIM:
          jsonObject = (JSONObject) parser.parse(message);
          direction = Direction.fromString(jsonObject.get("direction").toString());
          mustSendAndRefresh = FireFighterTurnManager.getInstance().moveWithVictim(direction);

          break;

        case CHOP:
          jsonObject = (JSONObject) parser.parse(message);
          direction = Direction.fromString(jsonObject.get("direction").toString());
          mustSendAndRefresh = FireFighterTurnManager.getInstance().chopWall(direction);
          break;
        case EXTINGUISH:
          jsonObject = (JSONObject) parser.parse(message);
          direction = Direction.fromString(jsonObject.get("direction").toString());
          mustSendAndRefresh = FireFighterTurnManager.getInstance().extinguishFire(direction);
          break;
        case INTERACT_WITH_DOOR:
          jsonObject = (JSONObject) parser.parse(message);
          direction = Direction.fromString(jsonObject.get("direction").toString());
          mustSendAndRefresh = FireFighterTurnManager.getInstance().interactWithDoor(direction);
          break;

        case SAVE:
          DBHandler.saveBoardToDB(BoardManager.getInstance().getGameName());
          break;

        case END_TURN:
          FireFighterTurnManager.getInstance().endTurn();
          mustSendAndRefresh = true;
          break;

        case FIRE_DECK_GUN:
          mustSendAndRefresh = FireFighterTurnManagerAdvance.getInstance().fireDeckGun();
          break;

        case MOVE_WITH_HAZMAT:
          jsonObject = (JSONObject) parser.parse(message);
          direction = Direction.fromString(jsonObject.get("direction").toString());
          mustSendAndRefresh = FireFighterTurnManagerAdvance.getInstance().moveWithHazmat(direction);
          break;
        case DRIVE_AMBULANCE:
          jsonObject = (JSONObject) parser.parse(message);
          direction = Direction.fromString(jsonObject.get("direction").toString());
          mustSendAndRefresh = FireFighterTurnManagerAdvance.getInstance().driveAmbulance(direction);
          break;
        case DRIVE_FIRETRUCK:
          jsonObject = (JSONObject) parser.parse(message);
          direction = Direction.fromString(jsonObject.get("direction").toString());
          mustSendAndRefresh = FireFighterTurnManagerAdvance.getInstance().driveFireTruck(direction);
          break;
        case REMOVE_HAZMAT:
          mustSendAndRefresh = FireFighterTurnManagerAdvance.getInstance().disposeHazmat();
          break;

        case FLIP_POI:
          jsonObject = (JSONObject) parser.parse(message);
          int i = Integer.parseInt(jsonObject.get("i").toString());
          int j = Integer.parseInt(jsonObject.get("j").toString());
          Tile t = BoardManager.getInstance().getTileAt(i,j);
          mustSendAndRefresh = FireFighterTurnManagerAdvance.getInstance().flipPOI(t);
          break;
        case CURE_VICTIM:
          mustSendAndRefresh = FireFighterTurnManagerAdvance.getInstance().treatVictim();
          break;
        case CREW_CHANGE:
          if (FireFighterTurnManagerAdvance.getInstance().crewChange(FireFighterAdvanceSpecialities.fromString(message))) {
            mustSendAndRefresh = true;
            refreshCrewChangePanel = true;
          }
          break;

        default:
      }
      if (mustSendAndRefresh) {
        Server.sendCommandToAllClients(ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString());
        Server.sendCommandToAllClients(ClientCommands.REFRESH_BOARD_SCREEN, "");
      }
      if (refreshCrewChangePanel) {
        Server.sendCommandToSpecificClient(ClientCommands.END_OF_SPECIALITY_CHANGE,"",ip);
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
}
