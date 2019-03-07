package com.cs361d.flashpoint.networking;


import com.cs361d.flashpoint.view.BoardChatFragment;
import com.cs361d.flashpoint.view.ChatScreen;
import com.cs361d.flashpoint.view.FlashPointGame;
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


public class Server implements Runnable{

    // Server has an instance of the game
    public FlashPointGame serverFPGame = new FlashPointGame();

    // Vector to store active clients
    static Vector<ClientHandler> clients = new Vector<ClientHandler>();

    // counter for clients
    static int i = 0;

    ServerSocket ss;    //Server Socket
    Socket s;           //Client socket
    Thread startServer; // DON'T SEND TO SRC CLIENT TWICE

    public Server() {}

    public Server(int serverPort) {
        // server is listening on port 1234
        try
        {
            ss = new ServerSocket(serverPort);
            startServer = new Thread(this);
            startServer.start();

        } catch (IOException e) { e.printStackTrace(); }

    }

    @Override
    public void run()
    {
        // running infinite loop for getting client request
        while (true)
        {
            // Accept the incoming request
            try
            {
                s = ss.accept(); // s is the client socket
                System.out.println("New client request received : " + s);

                // obtain input and output streams or the client
                DataInputStream din = new DataInputStream(s.getInputStream());
                DataOutputStream dout = new DataOutputStream(s.getOutputStream());

                System.out.println("Creating a new handler for this client...");

                // Create a new handler object for handling this request.
                ClientHandler clientObserver = new ClientHandler(s,"client " + i, din, dout, serverFPGame);

                // Create a new Thread with this client.
                Thread t = new Thread(clientObserver);

                System.out.println("Adding this client to active client list");

                // add this client to active clients list
                clients.add(clientObserver);

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
        try
        {
            for (ClientHandler mc : Server.clients) {
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
        try
        {
            JSONParser parser = new JSONParser();

            JSONObject jsonObject = (JSONObject) parser.parse(msg);
            Commands c = Commands.fromString(jsonObject.get("command").toString());
            String message = jsonObject.get("message").toString();
            switch (c)
            {
                case CHATWAIT:
                    if (!msg.equals(""))
                        ChatScreen.addMessageToGui(message);
                    break;
                case CHATGAME:
                    if (!msg.equals(""))
                        BoardChatFragment.addMessageToGui(message);
                    break;
                case GAMESTATE:
                    //TODO
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        catch (Exception e) {
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




//    static ServerSocket s;
//    static DataInputStream din;  // input from client
//    static DataOutputStream dout;// output to client
//    // Thread to read input from queue and writes to all clients
//    Thread startServer = new Thread(); // DON'T SEND TO SRC CLIENT TWICE
//
//    private static LinkedList<String> msgQueue = new LinkedList<String>();
//    private static ArrayList<Observer> clients = new ArrayList<Observer>();
//
//
//    @Override
//    public void run()
//    {
//        try {
//            while (true)
//            {
//                if(!msgQueue.isEmpty()) {
//                    dout.writeUTF(msgQueue.getFirst());
//                    dout.flush();
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public Server(){ //Start the Server
//
//        try
//        {
//            s=new ServerSocket(3333);
//            Socket newClientConnected = s.accept();
//            clients.add((Observer) newClientConnected);
//            startServer.start(); // Start reader thread
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//
//    }
//
//    //add client to the server's registered list
//    public void register(Observer o) { clients.add(o); }
//    //remove client from server's list
//    public void unregister(Observer o) { clients.remove(o); }
//
//    // Server notifies all the registered clients
//    private synchronized void notifyAllObservers(String msg) {
//        for (Observer obs : clients) {
//            obs.update(new Observable(), msg);
//        }
//    }
//
//    public void sendMsg(String msg) { notifyAllObservers(msg); }
//
//

//    public synchronized void  sendMsg(String msg)throws Exception {
//
//
//        for (Client user: clients) {
////            din=new DataInputStream(s.getInputStream());
//            dout=new DataOutputStream(s.getOutputStream());
//            dout.writeUTF(msg);
//            dout.flush();
//        }
//
//    }

//    public String sendMsg2(String msg)throws Exception{
////       BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
//
//        dout.writeUTF(msg);
//        dout.flush();
//        return din.readUTF();
//    }

//    public String receiveMsg()
//    {
//        final String[] received = {""};
//
//        Runnable _runnable = new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                try
//                {
//                    received[0] =din.readUTF();
//                    msgQueue.add(received[0]);
//                } catch (IOException e)
//                {
//                    e.printStackTrace();
//                }
//
//            }
//        };
//        return received[0];
////        long start = System.currentTimeMillis();
////        long _timeoutMs = 100;
////        String received = "";
////        while (System.currentTimeMillis() < (start + _timeoutMs)) {
////            try
////            {
////                if (din != null)
////                    received=din.readUTF();
////            } catch (IOException e)
////            {
////                e.printStackTrace();
////            }
////        }
////
////        return received;
//
//    }

//    public void closeClient(){
//        try
//        {
//            dout.close();
//            s.close();
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//
//    }





}
