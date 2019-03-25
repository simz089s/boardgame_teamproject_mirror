package com.cs361d.flashpoint.networking;


import com.cs361d.flashpoint.manager.*;
import com.cs361d.flashpoint.model.BoardElements.FireFighter;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;


public class Server implements Runnable
{

    private static List<FireFighterColor> notYetAssigned = new ArrayList<FireFighterColor>();

    // Vector to store server to client threads
    private final static HashMap<String, ServerToClientRunnable> clientObservers = new HashMap<String, ServerToClientRunnable>();

    // Map between IP and client
    private final static HashMap<String, Client> clientList = new HashMap<String, Client>();

    // Hash Map to map the users wirth the Firefighter colors
    private final static HashMap<FireFighterColor, String> colorsToClient = new HashMap<FireFighterColor, String>();

    // Arraylist of client Threads
    //static ArrayList<Thread> clientThreads = new ArrayList<Thread>();


    private static Server instance;
    private static boolean gameLoaded = false;
    private static List<String> messages = new ArrayList<String>();

    // counter for clientObservers
    static int i = 0;

    static ServerSocket ss;    //Server Socket
    Socket s;           //Client socket
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

                String ip = s.getInetAddress().toString().replace("/","");

                // Create a new handler object for handling this request.
                ServerToClientRunnable clientObserver = new ServerToClientRunnable(s, "client " + i, din, dout, ip);

                // Create a new Thread with this client.
                Thread t = new Thread(clientObserver);
                System.out.println("Adding this client to active client list");

                // add this client to active clientObservers list
                clientObservers.put(ip, clientObserver);

                System.out.println("Client Ip is: " + s.getInetAddress().toString());
                System.out.println();

                t.start();  // start the thread for the client

                i++; // increment i for new client

            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    //Constructor
    private Server(int serverPort) {
        try {
            ss = new ServerSocket(serverPort);
            startServer = new Thread(this);
            startServer.start();

        } catch (IOException e) { e.printStackTrace(); }

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

    //public static ArrayList<Thread> getClientThreads() { return clientThreads; }

    public boolean isEmpty() { return notYetAssigned.isEmpty(); }

    // To iterate through the chat messages
    public static Iterator<String> iteratorForChat(){ return messages.iterator(); }

    // Function to add messages
    public static synchronized void addMessage(String message){ messages.add(message);}

    public void changeLoadedStatus(boolean status) { gameLoaded = status; }
    public boolean getLoadedOrCreatedStatus() { return gameLoaded; }
    public static boolean noMorePlayer() { return notYetAssigned.isEmpty(); }


    public static synchronized void  assignFireFighterToClient(String IP) {
        if (notYetAssigned.isEmpty()) {
            return;
        }
        FireFighterColor color = notYetAssigned.remove(0);
        colorsToClient.put(color, IP);
        sendCommandToSpecificClient(ClientCommands.ASSIGN_FIREFIGHTER, color.toString(), IP);
    }

    public void setFireFighterAssignArray() {
        notYetAssigned.clear();
        Iterator<FireFighter> it = FireFighterTurnManager.getInstance().iterator();
        if (!it.hasNext()) {
            throw new IllegalArgumentException("Cannot call this function if the game board has not been initialized");
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
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static synchronized void sendMsgSpecificClient(String message, String IP){
        try {
            ServerToClientRunnable client = clientObservers.get(IP);
            client.dout.writeUTF(message);
        } catch (IOException e) { e.printStackTrace(); }
    }

    /* Get info about the server's machine */
    public static String getMyHostName() {
        String hostname = null;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
            System.out.println("Host Name = " + hostname);

        } catch (UnknownHostException e) { e.printStackTrace(); }

        return hostname;
    }

    // Remove Client from Server's and Network's List
    public void closeClient(String clientIP) {
        //Remove clientHandler from Hashmap
        clientObservers.remove(clientIP);

        //Remove Client from Network
        clientList.remove(clientIP);
        System.out.println("Client with IP: "+clientIP+" is removed from the Network successfully");
        System.out.println("Number of Clients Remaining on Network: "+clientList.size());
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

            switch (c) {
                case ADD_CHAT_MESSAGE:
                    if (!message.equals("")) {
                        Server.addMessage(message);
//            if (BoardScreen.isChatFragment()) {
//              BoardChatFragment.addMessageToChat(message);
//            }
                        Server.sendMsgToAllClients(msg);
                    } else if (!message.equals("")) {
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
//                    JSONArray jsa = new JSONArray();
//                    Iterator<String> it = Server.iteratorForChat();
//                    while (it.hasNext()) {
//                        jsa.add(it.next());
//                    }
//                    Server.getServer()
//                            .sendMsgSpecificClient(ip, ClientCommands.SEND_CHAT_MESSAGES, jsa.toJSONString());
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


                case EXITGAME:
//                    Server.getServer().changeLoadedStatus(false);
//                    for (ServerToClientRunnable mc : Server.getClientObservers().values()) {
//                        //TODO: Only if client is in the game let him exit
//                        mc.dout.writeUTF(msg);
//                    }
                    break;

                case LOADGAME:

                    if (!gameLoaded) {
                        DBHandler.loadBoardFromDB(message);
                        //TODO set false when client leaves game
                        gameLoaded = true;
                        assignFireFighterToClient(ip);
                        sendCommandToSpecificClient(ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString(), ip);
                        sendCommandToSpecificClient(ClientCommands.SETBOARDSCREEN, "", ip);
                    }
                    break;

                case JOIN:
                    if (!gameLoaded) {
                        assignFireFighterToClient(ip);
                        sendCommandToSpecificClient(ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString(), ip);
                        sendCommandToSpecificClient(ClientCommands.SETBOARDSCREEN, "", ip);
                    }
                    break;

                default:
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

    public void addNewClient(String ip, Client c) { this.clientList.put(ip, c); }

}