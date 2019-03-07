package com.cs361d.flashpoint.networking;


import com.cs361d.flashpoint.view.FlashPointGame;

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

    // Server has an instance of the game
    public FlashPointGame serverFPGame = new FlashPointGame();

    // Vector to store active client threads
    static HashMap<String, ClientHandler> clientThreads = new HashMap<String, ClientHandler>();

    // counter for clientThreads
    static int i = 0;

    ServerSocket ss;    //Server Socket
    Socket s;           //Client socket
    Thread startServer; // DON'T SEND TO SRC CLIENT TWICE

    public Server() {}

    public Server(int serverPort) {

        try {
            ss = new ServerSocket(serverPort);
            startServer = new Thread(this);
            startServer.start();

        } catch (IOException e) { e.printStackTrace(); }

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
                clientThreads.put(s.getInetAddress().toString(), clientObserver);

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

    /* Update GUI of the Server based on sent String*/
    public void updateServerGui(String msg) {
        /* Get the command from the string read
         * CHATWAIT: waiting screen chat changes
         * CHATGAME: in-game chat changes
         * GAMESTATE: gameState changes
         * */
        try { NetworkManager.ExecuteCommand(msg); }
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
                mc.s.close();
                mc.din.close();
                mc.dout.close();
                mc.fpg.setScreen(mc.fpg.getLoginScreen());
            }

        } catch (IOException e) { e.printStackTrace(); }

    }


}