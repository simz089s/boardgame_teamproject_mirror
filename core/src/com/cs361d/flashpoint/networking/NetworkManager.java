package com.cs361d.flashpoint.networking;

import com.badlogic.gdx.Gdx;
import com.cs361d.flashpoint.manager.*;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.screen.BoardChatFragment;
import com.cs361d.flashpoint.screen.BoardScreen;
import com.cs361d.flashpoint.screen.ChatScreen;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class NetworkManager {

  private static NetworkManager instance;
  //    final public String DEFAULT_SERVER_IP = getMyIPAddress(); //CHANGE THIS TO WORK OUTSIDE
  // MCGILL WORLD
  // public static final String DEFAULT_SERVER_IP = "142.157.74.18"; // Simon public ip address
//  public static final String DEFAULT_SERVER_IP = "142.157.67.193"; // Elvric public ip address
   final public static String DEFAULT_SERVER_IP = "142.157.149.16"; // DC public ip
  public static final int DEFAULT_SERVER_PORT = 54590;

  private String serverIP;

  // variable of type String
  public Server server;
  public ArrayList<Client> clientList = new ArrayList<Client>();

  // private constructor restricted to this class itself
  private NetworkManager(String pServerIP, int pServerPort) {
    serverIP = pServerIP;
  }

  // static method to create instance of Singleton class
  public static NetworkManager getInstance() {
    if (instance == null) {
      instance =
          new NetworkManager(NetworkManager.DEFAULT_SERVER_IP, NetworkManager.DEFAULT_SERVER_PORT);
    }
    return instance;
  }

  public void addServer(Server s) {
    this.server = s;
  }

  public void addNewClient(Client c) { this.clientList.add(c); }

  public void sendCommand(ClientCommands command, String msg) {
    String jsonMsg = createJSON(command, msg);
//    if (getMyPublicIP().equals(DEFAULT_SERVER_IP)) server.sendMsg(jsonMsg);
//    else
//      for (Client c : clientList) {
//        c.sendMsg(jsonMsg);
//      }
    // Find the right client and send the message from him to server
    for (Client c: clientList) {
      if (getMyPublicIP().equals(c.getClientIP()))
        c.sendMsg(jsonMsg);
    }
  }

  public String createJSON(ClientCommands command, String msg) {

    JSONObject message = new JSONObject();
    message.put("command", command.toString());
    message.put("message", msg);
    message.put("IP", getMyPublicIP());
    return message.toString();
  }

  public String getMyPublicIP() {
    String systemipaddress = "";
    try {
      URL url_name = new URL("http://bot.whatismyipaddress.com");

      BufferedReader sc;
      sc = new BufferedReader(new InputStreamReader(url_name.openStream()));

      // reads system IPAddress
      systemipaddress = sc.readLine().trim();
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Public IP Address: " + systemipaddress + "\n");
    return systemipaddress;
  }

  public String getMyIPAddress() {
    String ipAddress = null;
    try {
      InetAddress addr = InetAddress.getLocalHost();
      ipAddress = addr.getHostAddress();
      System.out.println("LOCAL ONE IP Address = " + ipAddress);

    } catch (UnknownHostException e) {
      e.printStackTrace();
    }

    return ipAddress;
  }

  public String getIPByAddress(String address) {
    String ipAddress = null;
    try {
      InetAddress addr = InetAddress.getByName(address);
      ipAddress = addr.getHostAddress();
      System.out.println("IP Address = " + ipAddress);

    } catch (UnknownHostException e) {
      e.printStackTrace();
    }

    return ipAddress;
  }

  public String getHostNameByAdress(String address) {
    String hostname = null;
    try {
      InetAddress addr = InetAddress.getByName(address);
      hostname = addr.getHostName();
      System.out.println("Host Name = " + hostname);

    } catch (UnknownHostException e) {
      e.printStackTrace();
    }

    return hostname;
  }

  public static void serverExecuteCommand(String msg) {
    try {
      JSONParser parser = new JSONParser();

      JSONObject jsonObject = (JSONObject) parser.parse(msg);
      ServerCommands c = ServerCommands.fromString(jsonObject.get("command").toString());
      String message = jsonObject.get("message").toString();
      String ip = jsonObject.get("IP").toString();
      System.out.println(message);
      switch (c) {
//        case CHATWAIT:
//          if (!msg.equals("")) ChatScreen.addMessageToGui(message);
//          if (Server.amIServer()) {
//
//            }
//          }
//          break;

        case CHATGAME:
        case GAMESTATE:
        case SAVE:
          for (ClientHandler mc : Server.getClientThreads().values()) {
            mc.dout.writeUTF(msg);
          }
          break;

        case SEND_NEWLY_CREATED_BOARD:
          if (!Server.getServer().getLoadedOrCreatedStatus()) {
            Server.getServer().changeLoadedStatus(true);
            CreateNewGameManager.loadGameFromString(message);
            Server.getServer().setFireFighterAssignArray();
            Server.getServer().assignFireFighterToClient(ip);
//            if (ip.equals(NetworkManager.getInstance().getMyPublicIP())) {
//              BoardScreen.setBoardScreen();
//            } else {
              Server.getServer().sendMsgSpecificClient(ip, ServerCommands.SETBOARDSCREEN, "");
//            }
          }
          break;

        case DISCONNECTSERVER:
          if (instance.getMyPublicIP().equals(DEFAULT_SERVER_IP))
            instance.server.closeServer(); // disconnect all the clients
          break;

        case DISCONNECTCLIENT:
          if (instance.getMyPublicIP().equals(DEFAULT_SERVER_IP))
            instance.server.closeClient(); // disconnect clients
          break;

        case ASK_TO_GET_ASSIGN_FIREFIGHTER:
          Server.getServer().assignFireFighterToClient(ip);
          break;

        case ASSIGN_FIREFIGHTER:
          User.getInstance().assignFireFighter(FireFighterColor.fromString(message));
          break;

        case EXITGAME:
          Server.getServer().changeLoadedStatus(false);
          for (ClientHandler mc : Server.getClientThreads().values()) {
            mc.dout.writeUTF(msg);
          }
          break;

        case JOIN:
          if (!ip.equals(DEFAULT_SERVER_IP) && Server.getServer().getLoadedOrCreatedStatus()) {
            if (!Server.getServer().noMorePlayer()
                && Server.getServer().getLoadedOrCreatedStatus()) {
              Server.getServer().assignFireFighterToClient(ip);
              Server.getServer()
                  .sendMsgSpecificClient(ip, ServerCommands.GAMESTATE, DBHandler.getBoardAsString());
              Server.getServer().sendMsgSpecificClient(ip, ServerCommands.SETBOARDSCREEN, "");
            }
          } else if (Server.getServer().getLoadedOrCreatedStatus() && !Server.getServer().isEmpty()) {
              Server.getServer().assignFireFighterToClient(ip);
              BoardScreen.setBoardScreen();
            }
          break;

        default:
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void clientExecuteCommand(String msg) {
    try {
      JSONParser parser = new JSONParser();

      JSONObject jsonObject = (JSONObject) parser.parse(msg);
      ServerCommands c = ServerCommands.fromString(jsonObject.get("command").toString());
      String message = jsonObject.get("message").toString();
      String ip = jsonObject.get("IP").toString();
      System.out.println(message);
      switch (c) {
        case CHATWAIT:
          if (!msg.equals("")) ChatScreen.addMessageToGui(message);
          break;

        case CHATGAME:
          if (!message.equals("")) BoardChatFragment.addMessageToGui(message);
          break;

        case GAMESTATE:
          // Transfer the redraw call to the main thread (that has openGL and GDX)
          CreateNewGameManager.loadGameFromString(message);
          Gdx.app.postRunnable(
                  new Runnable() {
                    @Override
                    public void run() {
                      BoardScreen.redrawBoard();
                    }
                  });
          break;

        case SAVE:
          CreateNewGameManager.loadGameFromString(message);
          DBHandler.saveBoardToDB(BoardManager.getInstance().getGameName());
          break;

        case ASK_TO_GET_ASSIGN_FIREFIGHTER:
          Server.getServer().assignFireFighterToClient(ip);
          break;

        case ASSIGN_FIREFIGHTER:
          User.getInstance().assignFireFighter(FireFighterColor.fromString(message));
          break;

        case EXITGAME:
          Gdx.app.postRunnable(
                  new Runnable() {
                    @Override
                    public void run() {
                      BoardScreen.setLobbyPage();
                    }
                  });
          break;

        case SETBOARDSCREEN:
          Gdx.app.postRunnable(
                  new Runnable() {
                    @Override
                    public void run() {
                      BoardScreen.setBoardScreen();
                    }
                  });

        default:
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
