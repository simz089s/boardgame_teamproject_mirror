package com.cs361d.flashpoint.networking;

import com.badlogic.gdx.Gdx;
import com.cs361d.flashpoint.manager.*;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.screen.BoardChatFragment;
import com.cs361d.flashpoint.screen.BoardScreen;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class NetworkManager {

  private static NetworkManager instance;
  //    final public String DEFAULT_SERVER_IP = getMyIPAddress(); //CHANGE THIS TO WORK OUTSIDE
  // MCGILL WORLD
  // public static final String DEFAULT_SERVER_IP = "142.157.74.18"; // Simon public ip address
//  public static final String DEFAULT_SERVER_IP = "142.157.67.193"; // Elvric public ip address
   //final public static String DEFAULT_SERVER_IP = "104.222.125.177"; // DC public ip
  final public static String DEFAULT_SERVER_IP = "142.157.75.165"; // Matty V IP
  public static final int DEFAULT_SERVER_PORT = 54590;

  private String serverIP;

  // variable of type String
  public Server server;
//  public ArrayList<Client> clientList = new ArrayList<Client>();
private static HashMap<String, Client> clientList = new HashMap<String, Client>();

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

  public void addNewClient(String ip, Client c) { this.clientList.put(ip, c); }

  public HashMap getClientList() { return clientList; }

  public void sendCommand(ServerCommands command, String msg) {
    String jsonMsg = createJSON(command.toString(), msg);
    for (Client c: clientList.values()) {
      if (getMyPublicIP().equals(c.getClientIP()))
          c.sendMsgToServer(jsonMsg);
    }
  }

  public String createJSON(String command, String msg) {

    JSONObject message = new JSONObject();
    message.put("command", command);
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

  public static void serverExecuteCommand(String msg) {
    try {
      JSONParser parser = new JSONParser();

      JSONObject jsonObject = (JSONObject) parser.parse(msg);
      ServerCommands c = ServerCommands.fromString(jsonObject.get("command").toString());
      String message = jsonObject.get("message").toString();
      String ip = jsonObject.get("IP").toString();
      System.out.println(message);

      switch (c) {
        case ADD_CHAT_MESSAGE:
          if (!message.equals("")) {
            Server.addMessage(message);
//            if (BoardScreen.isChatFragment()) {
//              BoardChatFragment.addMessageToChat(message);
//            }
            Server.sendMsgToAllClients(msg);
          }
          else if (!message.equals("")) {
            Server.addMessage(message);
//            if (BoardScreen.isChatFragment()) {
//              BoardChatFragment.addMessageToChat(message);
//            }
          }
//          else if (!message.equals("")) {
//            Gdx.app.postRunnable(
//                    new Runnable() {
//                      @Override
//                      public void run() {
//                        if (BoardScreen.isChatFragment()) {
//                          BoardChatFragment.addMessageToChat(message);
//                        }
//                      }
//                    });
//          }
          break;

        case GET_CHAT_MESSAGES:
          JSONArray jsa = new JSONArray();
          Iterator<String> it = Server.iteratorForChat();
          while (it.hasNext()) {
            jsa.add(it.next());
          }
          Server.getServer()
                  .sendMsgSpecificClient(ip, ClientCommands.SEND_CHAT_MESSAGES, jsa.toJSONString());
//            BoardScreen.setSideFragment(Fragment.CHAT);
//            Iterator<String> it = Server.iteratorForChat();
//            while (it.hasNext()) {
//              BoardChatFragment.addMessageToChat(it.next());
//            }
//          }
          break;

        case GAMESTATE:
          Server.sendMsgToAllClients(msg);
          break;

        case SAVE:
          CreateNewGameManager.loadGameFromString(message);
          DBHandler.saveBoardToDB(BoardManager.getInstance().getGameName());
          break;

        case SEND_NEWLY_CREATED_BOARD:
          System.out.println(!Server.getServer().getLoadedOrCreatedStatus());
          if (!Server.getServer().getLoadedOrCreatedStatus()) {
            System.out.println(msg);
            Server.getServer().changeLoadedStatus(true);
            CreateNewGameManager.loadGameFromString(message);
            Server.getServer().setFireFighterAssignArray();
            Server.getServer().assignFireFighterToClient(ip);
            Server.getServer().sendMsgSpecificClient(ip, ClientCommands.SETBOARDSCREEN, "");
          }
          break;

        case DISCONNECTSERVER:
            instance.server.closeServer(); // disconnect all the clients
          break;

        case DISCONNECTCLIENT:
//            instance.server.closeClient(); // disconnect clients
          break;

        case ASK_TO_GET_ASSIGN_FIREFIGHTER:
          Server.getServer().assignFireFighterToClient(ip);
          break;

        case EXITGAME:
          Server.getServer().changeLoadedStatus(false);
          for (ClientHandler mc : Server.getClientObservers().values()) {
            //TODO: Only if client is in the game let him exit
            mc.dout.writeUTF(msg);
          }
          break;

        case LOADGAME:
          break;

        case JOIN:
          if (Server.getServer().getLoadedOrCreatedStatus()) {
            if (!Server.getServer().noMorePlayer()
                && Server.getServer().getLoadedOrCreatedStatus()) {
              Server.getServer().assignFireFighterToClient(ip);
              Server.getServer()
                  .sendMsgSpecificClient(ip, ClientCommands.GAMESTATE, DBHandler.getBoardAsString());
              Server.getServer().sendMsgSpecificClient(ip, ClientCommands.SETBOARDSCREEN, "");
            }
//          } else if (Server.getServer().getLoadedOrCreatedStatus() && !Server.getServer().isEmpty()) {
//              Server.getServer().assignFireFighterToClient(ip);
//              BoardScreen.setBoardScreen();
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
      ClientCommands c = ClientCommands.fromString(jsonObject.get("command").toString());
      final String message = jsonObject.get("message").toString();
      String ip = jsonObject.get("IP").toString();
      System.out.println(message);
      switch (c) {
//        case CHATGAME:
//          if (!message.equals("")) BoardChatFragment.addMessageToGui(message);
//          break;

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

        default:
      }


    } catch (Exception e) {
      e.printStackTrace();
    }
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
//
//  public String getIPByAddress(String address) {
//    String ipAddress = null;
//    try {
//      InetAddress addr = InetAddress.getByName(address);
//      ipAddress = addr.getHostAddress();
//      System.out.println("IP Address = " + ipAddress);
//
//    } catch (UnknownHostException e) {
//      e.printStackTrace();
//    }
//
//    return ipAddress;
//  }
//
//  public String getHostNameByAdress(String address) {
//    String hostname = null;
//    try {
//      InetAddress addr = InetAddress.getByName(address);
//      hostname = addr.getHostName();
//      System.out.println("Host Name = " + hostname);
//
//    } catch (UnknownHostException e) {
//      e.printStackTrace();
//    }
//
//    return hostname;
//  }







}
