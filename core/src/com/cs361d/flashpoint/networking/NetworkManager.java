package com.cs361d.flashpoint.networking;

import com.badlogic.gdx.Gdx;
import com.cs361d.flashpoint.manager.BoardManager;
import com.cs361d.flashpoint.manager.CreateNewGameManager;
import com.cs361d.flashpoint.manager.DBHandler;
import com.cs361d.flashpoint.manager.FireFighterTurnManager;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.view.BoardChatFragment;
import com.cs361d.flashpoint.view.BoardScreen;
import com.cs361d.flashpoint.view.ChatScreen;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NetworkManager {

    private static NetworkManager instance;
//    final public String DEFAULT_SERVER_IP = getMyIPAddress(); //CHANGE THIS TO WORK OUTSIDE MCGILL WORLD
    final public static String DEFAULT_SERVER_IP = "142.157.74.18"; //Simon public ip address
//    final public static String DEFAULT_SERVER_IP = "142.157.67.193"; //Elvric public ip address
    //final public static String DEFAULT_SERVER_IP = "142.157.149.34"; // DC public ip
    final public static int DEFAULT_SERVER_PORT = 54590;

    public String serverIP;

    // variable of type String
    public Server server;
    public ArrayList<Client> clientList = new ArrayList<Client>();

    // private constructor restricted to this class itself
    private NetworkManager(String pServerIP) {
        serverIP = pServerIP;
    }

    // static method to create instance of Singleton class
    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager(DEFAULT_SERVER_IP);
        }
        return instance;
    }

    public void setServerIP(String pServerIP) {
        serverIP = pServerIP;
    }

    public void addServer(Server s) {
        this.server = s;
    }

    public void addNewClient(Client c) {
        this.clientList.add(c);
    }

    public void sendCommand(Commands command, String msg) {
        String jsonMsg = createJSON(command.toString(), msg);
        if(getMyPublicIP().equals(serverIP))
            server.sendMsg(jsonMsg);

        else
            for (Client c : clientList) {
                c.sendMsg(jsonMsg);
            }

    }


    private String createJSON(String command, String msg){

        JSONObject message = new JSONObject();
        message.put("command", command);
        message.put("message", msg);
        message.put("IP", getMyPublicIP());
        return message.toString();

    }

    public String getMyPublicIP() {
        String systemipaddress = "";
        try
        {
            URL url_name = new URL("http://bot.whatismyipaddress.com");

            BufferedReader sc;
            sc = new BufferedReader(new InputStreamReader(url_name.openStream()));

            // reads system IPAddress
            systemipaddress = sc.readLine().trim();
        }
        catch (Exception e) { e.printStackTrace(); }

        System.out.println("Public IP Address: " + systemipaddress +"\n");
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

    public static void ExecuteCommand(String msg) {
        try {
            /* Get the command from the string read
             * CHATWAIT: waiting screen chat changes
             * CHATGAME: in-game chat changes
             * GAMESTATE: gameState changes
             * */

            JSONParser parser = new JSONParser();

            JSONObject jsonObject = (JSONObject) parser.parse(msg);
            Commands c = Commands.fromString(jsonObject.get("command").toString());
            String message = jsonObject.get("message").toString();
            System.out.println(message);
            switch (c) {
                case CHATWAIT:
                    if (!msg.equals("")) ChatScreen.addMessageToGui(message);
                    break;
                case CHATGAME:
                    if (!msg.equals("")) BoardChatFragment.addMessageToGui(message);
                    break;
                case GAMESTATE:
                    // Transfer the redraw call to the main thread (that has openGL and GDX)
                    CreateNewGameManager.loadGameFromString(message);
                    Gdx.app.postRunnable(
                            new Runnable() {
                                @Override
                                public void run() {
                                    BoardScreen.redrawBoardEntirely();
                                }
                            });
                    break;
                case SAVE:
                    CreateNewGameManager.loadGameFromString(message);
                    DBHandler.saveBoardToDB(BoardManager.getInstance().getGameName());
                    break;
                case GAMECREATED:
                    break;

                case JOINGAME:
                    JSONArray tilesArr = (JSONArray) parser.parse(message);
                    Iterator<String> iterator = tilesArr.iterator();
                    List<FireFighterColor> colorList = new ArrayList<FireFighterColor>();
                    while (iterator.hasNext()) {
                        colorList.add(FireFighterColor.fromString(iterator.next()));
                    }
                    FireFighterTurnManager.getInstance().setNotYetAssigned(colorList);
                    break;
                case LOADGAME:
                    CreateNewGameManager.loadGameFromString(message);
                    break;
                case DISCONNECT:
                    if(instance.getMyPublicIP().equals(instance.serverIP))
                        instance.server.closeServer(); // disconnect all the clients
                    break;
                case GETGAME:
                    break;
                    
                default:
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
