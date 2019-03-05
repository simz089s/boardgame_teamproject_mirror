package com.cs361d.flashpoint.Networking;


//import com.cs361d.flashpoint.view.ChatServerScreen;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Server implements Runnable{

    // Vector to store active clients
    static Vector<ClientHandler> clients = new Vector<ClientHandler>();

    // counter for clients
    static int i = 0;

    ServerSocket ss; //Server Socket
    Socket s; //Client socket
    Thread startServer; // DON'T SEND TO SRC CLIENT TWICE
    Thread chatServer;
    ChatServerScreen css;


    public Server(ChatServerScreen css) {
        // server is listening on port 1234
        try
        {
            ss = new ServerSocket(1234);
            this.css = css;
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
                s = ss.accept();
                System.out.println("New client request received : " + s);

                // obtain input and output streams
                DataInputStream din = new DataInputStream(s.getInputStream());
                DataOutputStream dout = new DataOutputStream(s.getOutputStream());

                System.out.println("Creating a new handler for this client...");

                // Create a new handler object for handling this request.
                ClientHandler clientObserver = new ClientHandler(s,"client " + i, din, dout, css);

                // Create a new Thread with this object.
                Thread t = new Thread(clientObserver);

                System.out.println("Adding this client to active client list");

                // add this client to active clients list
                clients.add(clientObserver);

                // start the thread.
                t.start();

                // increment i for new client.
                // i is used for naming only, and can be replaced by any naming scheme
                i++;

            } catch (IOException e) { e.printStackTrace(); }
        }
    }


    /* Send a message to a server */
    public synchronized void sendMsg(String msg) {
        try
        {
            for (ClientHandler mc : Server.clients) {
                mc.dout.writeUTF(msg);
            }
            //update the chat for yourself
            css.msgs.add(msg);
            String[] newMsg = css.msgs.toArray(new String[css.msgs.size()]);
//            css.lstMsg.setItems(newMsg);

        } catch (IOException e) { e.printStackTrace(); }
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

    public static String getMyIPAddress() {
        String ipAddress = null;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String ipAddr = addr.getHostAddress();
            System.out.println("IP Address = " + ipAddr.toString());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return ipAddress;
    }

    public static String getIPByAddress(String address) {
        String ipAddress = null;
        try {
            InetAddress addr = InetAddress.getByName(address);
            String ipAddr = addr.getHostAddress();
            System.out.println("IP Address = " + ipAddr.toString());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return ipAddress;
    }

    public static String getHostNameByAdress(String address) {
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
