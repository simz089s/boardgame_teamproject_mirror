package com.cs361d.flashpoint.networking;


import com.cs361d.flashpoint.manager.FireFighterTurnManager;
import com.cs361d.flashpoint.manager.User;
import com.cs361d.flashpoint.model.BoardElements.FireFighter;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;

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
    private static HashMap<String, ClientHandler> clientObservers = new HashMap<String, ClientHandler>();


    // Arraylist of client Threads
    static ArrayList<Thread> clientThreads = new ArrayList<Thread>();

    public void stopServerWriteToClientThread() { notStopped = false; }


    private static Server instance;
    private boolean gameAlreadyLoadedorCreated = false;
    private static List<String> messages = new ArrayList<String>();
    private boolean notStopped = true;

    // counter for clientObservers
    static int i = 0;

    ServerSocket ss;    //Server Socket
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

                // Create a new handler object for handling this request.
                ClientHandler clientObserver = new ClientHandler(s, "client " + i, din, dout);

                // Create a new Thread with this client.
                Thread t = new Thread(clientObserver);

                System.out.println("Adding this client to active client list");

                // add this client to active clientObservers list
                String ip = s.getInetAddress().toString().replace("/","");
                clientObservers.put(ip, clientObserver);

                System.out.println("Client Ip is: " + s.getInetAddress().toString());

                // start the thread for the client.
                t.start();

                // increment i for new client.
                // i is used for naming only, and can be replaced by any naming scheme
                i++;

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

    public static HashMap<String, ClientHandler> getClientObservers() { return clientObservers; }

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
            for (ClientHandler mc : Server.clientObservers.values()) {
                mc.dout.writeUTF(msg);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public synchronized void sendMsgSpecificClient(String ip, ClientCommands command, String message){
        try {
            ClientHandler client = clientObservers.get(ip);
            String msg = NetworkManager.getInstance().createJSON(command.toString(), message);
            client.dout.writeUTF(msg);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Close server process
    public void closeServer(){
        try {
            // Close every client connection
            for (final String clientIP : Server.clientObservers.keySet()) {
                // redirect client to login page
                this.sendMsgSpecificClient(clientIP,Commands.SETLOGINSCREEN,"");
                ClientHandler mc = Server.clientObservers.get(clientIP);
                mc.stopServerWriteToClientThread(); // Stop server-to-client writer thread (stop Client Handler)
                mc.din.close(); //close server-from-client input stream
                mc.dout.close();//close server-to-client output stream
                mc.s.close();   //close client socket
            }

            // Main thread waits for all other threads to finish
            for (Thread clientThread: Server.clientThreads){
                clientThread.join();
            }

            // Stop all client's reader threads
            for (Client client: NetworkManager.getInstance().clientList) {
                client.stopClientReadingThread();
                client.din.close(); //close client-from-server input stream
                client.dout.close();//close client-to-server output stream
            }
            ss.close();         //close server socket

            //Stop Server Thread
            stopServerThread();

        } catch (IOException e) { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

    }

    private void stopServerThread() { notStopped = false;  }

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






}