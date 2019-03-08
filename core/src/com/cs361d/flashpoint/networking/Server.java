package com.cs361d.flashpoint.networking;


import com.cs361d.flashpoint.manager.FireFighterTurnManager;
import com.cs361d.flashpoint.manager.User;
import com.cs361d.flashpoint.model.BoardElements.FireFighter;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.screen.FlashPointGame;

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
    // Server has an instance of the game
    public FlashPointGame serverFPGame = new FlashPointGame();

    // Vector to store active client threads
    static HashMap<String, ClientHandler> clientThreads = new HashMap<String, ClientHandler>();
    private static Server instance;
    private boolean gameAlreadyLoadedorCreated = false;
    // counter for clientThreads
    static int i = 0;

    ServerSocket ss;    //Server Socket
    Socket s;           //Client socket
    Thread startServer; // DON'T SEND TO SRC CLIENT TWICE

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

                // Create a new handler object for handling this request.
                ClientHandler clientObserver = new ClientHandler(s, "client " + i, din, dout, serverFPGame);

                // Create a new Thread with this client.
                Thread t = new Thread(clientObserver);

                System.out.println("Adding this client to active client list");

                // add this client to active clientThreads list
                String ip = s.getInetAddress().toString().replace("/","");
                clientThreads.put(ip, clientObserver);

                System.out.println("Client Ip is: " + s.getInetAddress().toString());

                // start the thread for the client.
                t.start();

                // increment i for new client.
                // i is used for naming only, and can be replaced by any naming scheme
                i++;

            } catch (IOException e) { e.printStackTrace(); }
        }
    }


    /* Send a message to from server */
    public synchronized void sendMsg(String msg) {
        try {
            for (ClientHandler mc : Server.clientThreads.values()) {
                mc.dout.writeUTF(msg);
            }
            updateServerGui(msg); //update your own Gui

        } catch (IOException e) { e.printStackTrace(); }
    }

    public synchronized void sendMsgSpecificClient(String ip, Commands command, String message){
        try {
            // Get the specific client handler
            if (ip.equals(NetworkManager.getInstance().getMyPublicIP())) {
                return;
            }
            ClientHandler client = clientThreads.get(ip);

            String msg = NetworkManager.getInstance().createJSON(command, message);
            client.dout.writeUTF(msg);

            // updateServerGui(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Update GUI of the Server based on sent String*/
    public void updateServerGui(String msg) {
        /* Get the command from the string read
         * CHATWAIT: waiting screen chat changes
         * CHATGAME: in-game chat changes
         * GAMESTATE: gameState changes
         * */
        try { NetworkManager.executeCommand(msg); }
        catch (Exception e) { e.printStackTrace(); }
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

    public void closeServer(){
        try {
            // Close every client and redirect to login page
            for (ClientHandler mc : Server.clientThreads.values()) {
                mc.din.close(); //close client input stream
                mc.dout.close();//close client output stream
                mc.s.close();   //close client socket
            }
            ss.close();         //close server socket

        } catch (IOException e) { e.printStackTrace(); }

    }

    public synchronized void  assignFireFighterToClient(String IP) {
        if (notYetAssigned.isEmpty()) {
            return;
        }
        FireFighterColor color = notYetAssigned.remove(0);
        if (IP.equals(NetworkManager.getInstance().getMyPublicIP())) {
            User.getInstance().assignFireFighter(color);
        }
        else {
           sendMsgSpecificClient(IP, Commands.ASSIGN_FIREFIGHTER, color.toString());
        }
    }
    public static boolean amIServer() {
        return instance != null;
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
    public boolean isEmpty() {
        return notYetAssigned.isEmpty();
    }
    public void changeLoadedStatus(boolean status) {
        gameAlreadyLoadedorCreated = status;
    }
    public boolean getLoadedOrCreatedStatus() {
        return gameAlreadyLoadedorCreated;
    }
    public boolean noMorePlayer() {
        return notYetAssigned.isEmpty();
    }

}