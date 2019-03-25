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

    private List<FireFighterColor> notYetAssigned = new ArrayList<FireFighterColor>();

    // Vector to store active client threads
    private static HashMap<String, ServerToClientRunnable> clientObservers = new HashMap<String, ServerToClientRunnable>();

    // Hash Map to map the users with the Firefighter colors
    private static HashMap<User, FireFighterColor> fireFighterColors = new HashMap<User, FireFighterColor>();

    // Arraylist of client Threads
    static ArrayList<Thread> clientThreads = new ArrayList<Thread>();

    public void stopServerWriteToClientThread() { notStopped = false; }


    private static Server instance;
    private boolean gameAlreadyLoadedorCreated = false;
    private static List<String> messages = new ArrayList<String>();
    private static boolean notStopped = true;

    // counter for clientObservers
    static int i = 0;

    static ServerSocket ss;    //Server Socket
    Socket s;           //Client socket
    Thread startServer; // DON'T SEND TO SRC CLIENT TWICE

    @Override
    public void run() {
        // running infinite loop for getting client request
        while (notStopped) {
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

    public static Server createServer(int serverPort) {
        instance = new Server(serverPort);
        return instance;
    }

    public static Server getServer() {
        if (instance == null) {
            throw new IllegalArgumentException("You are not the server you should not ask to access it");
        }
        return instance;
    }

    public static HashMap<String, ServerToClientRunnable> getClientObservers() { return clientObservers; }

    public static ArrayList<Thread> getClientThreads() { return clientThreads; }

    public boolean isEmpty() { return notYetAssigned.isEmpty(); }

    // To iterate through the chat messages
    public static Iterator<String> iteratorForChat(){ return messages.iterator(); }

    // Function to add messages
    public static synchronized void addMessage(String message){ messages.add(message);}

    public void changeLoadedStatus(boolean status) { gameAlreadyLoadedorCreated = status; }
    public boolean getLoadedOrCreatedStatus() { return gameAlreadyLoadedorCreated; }
    public boolean noMorePlayer() { return notYetAssigned.isEmpty(); }


    public synchronized void  assignFireFighterToClient(String IP) {
        if (notYetAssigned.isEmpty()) {
            return;
        }
        FireFighterColor color = notYetAssigned.remove(0);
        if (IP.equals(NetworkManager.getInstance().getMyPublicIP())) {
            User.getInstance().assignFireFighter(color);
        }
        else {
            sendMsgSpecificClient(IP, ClientCommands.ASSIGN_FIREFIGHTER, color.toString());
        }
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
    public static synchronized void sendMsgToAllClients(String msg) {
        try {
            for (ServerToClientRunnable mc : Server.clientObservers.values()) {
                mc.dout.writeUTF(msg);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public synchronized void sendMsgSpecificClient(String ip, ClientCommands command, String message){
        try {
            ServerToClientRunnable client = clientObservers.get(ip);
            String msg = NetworkManager.getInstance().createJSON(command.toString(), message);
            client.dout.writeUTF(msg);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Close server process
    public static void closeServer(){
        try {
            // Close every client connection
            for (final String clientIP : Server.clientObservers.keySet()) {
                // redirect client to login page
                //this.sendMsgSpecificClient(clientIP,ClientCommands.SETLOGINSCREEN,"");
                ServerToClientRunnable mc = Server.clientObservers.get(clientIP);
                mc.stopClientReadFromServerThread(); // Stop server-to-client writer thread (stop Client Handler)
                mc.din.close(); //close server-from-client input stream
                mc.dout.close();//close server-to-client output stream
                mc.getSocket().close();   //close client socket
            }

            // Main thread waits for all other threads to finish
            for (Thread clientThread: Server.clientThreads){
                clientThread.join();
            }

            // Stop all client's reader threads
            for (Object c: NetworkManager.getInstance().getClientList().values()) {
                Client client = (Client) c;
                client.stopClientReadingThread();
                client.getDin().close(); //close client-from-server input stream
                //close client-to-server output stream
                client.getDout().close();
            }
            ss.close();         //close server socket

            //Stop Server Thread
            stopServerThread();

        } catch (IOException e) { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

    }

    private static void stopServerThread() { notStopped = false;  }

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
        Server.getClientObservers().remove(clientIP);

        //Remove Client from Network
        NetworkManager.getInstance().getClientList().remove(clientIP);
        System.out.println("Client with IP: "+clientIP+" is removed from the Network successfully");
        System.out.println("Number of Clients Remaining on Network: "+NetworkManager.getInstance().getClientList().size());
    }

    public static HashMap<User, FireFighterColor> getFireFighterColors() {
        return fireFighterColors;
    }

    public static void addColorToHashMap(User u, FireFighterColor f){
        fireFighterColors.put(u,f );
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
                    closeServer(); // disconnect all the clients
                    break;

                case DISCONNECTCLIENT:
//            instance.server.closeClient(); // disconnect clients
                    break;

                case ASK_TO_GET_ASSIGN_FIREFIGHTER:
                    Server.getServer().assignFireFighterToClient(ip);
                    break;

                case EXITGAME:
                    Server.getServer().changeLoadedStatus(false);
                    for (ServerToClientRunnable mc : Server.getClientObservers().values()) {
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

}