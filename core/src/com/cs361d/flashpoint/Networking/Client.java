package com.cs361d.flashpoint.Networking;

//import com.cs361d.flashpoint.view.ChatClientScreen;
import com.cs361d.flashpoint.view.FlashPointGame;
import kotlin.jvm.Synchronized;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {

    // A client has an instance of the game
    public static FlashPointGame clientFPGame = new FlashPointGame();

//    final static int ServerPort = 50000;
    final static int ServerPort = 1234;
    Socket s;
    DataInputStream din;    // input stream
    DataOutputStream dout;  //output stream
    String ip;              // getting Server's ip

//    ChatClientScreen ccs; // instance of the chat screen so we can modify/update it

    public Client() {
            try {

                // ip = "142.157.149.34";
                ip = "localhost";
                // establish the connection
                s = new Socket(ip, ServerPort);

                // obtaining input and out streams
                din = new DataInputStream(s.getInputStream());
                dout = new DataOutputStream(s.getOutputStream());

//                this.ccs = ccs;

                // readMessage thread constantly listening
                Thread readMessage = new Thread(new Runnable()
                {
                    @Override
                    public void run() {

                        while (true) {
                            try {
                                // read the message sent to this client
                                String msg = din.readUTF();
                                /* Get the first 4 chars from the string: wait, play, stat, game
                                * wait: waiting screen chat changes
                                * play: in-game chat changes
                                * stat: stats changes
                                * game: gameState changes
                                * */

                                String type = msg.substring(0,4);
                                String msgToSend = msg.substring(5);

                                // Update stats
                                if (type.equals("stat")) {
                                    //TODO
                                }
                                // Update chat message from waiting area
                                else if(type.equals("wait")) {
                                    // update messages array
                                    if (!msg.equals("")) {
//                                        ccs.msgs.add(msg);
//                                        String[] newMsg = clientFPGame.chatScreen.msgs.toArray(new String[ccs.msgs.size()]);
//                                        ccs.lstMsg.setItems(newMsg);
                                    }
                                }
                                // Update chat message from game area
                                else if(type.equals("play")) {
                                    // update messages array
                                    if (!msg.equals(""))
                                    {
//                                        ccs.msgs.add(msg);
//                                        String[] newMsg = clientFPGame.boardChatFragment.msgs.toArray(new String[ccs.msgs.size()]);
//                                        ccs.lstMsg.setItems(newMsg);
                                    }
                                }

                                // update Game State
                                else {
                                    //TODO
                                }



                            } catch (IOException e) {

                                e.printStackTrace();
                            }
                        }
                    }
                });

                readMessage.start();

            }
            catch (UnknownHostException e) { e.printStackTrace(); }
            catch (IOException e) { e.printStackTrace(); }
        }

        /* Send a message to a server */
        public synchronized void sendMsg(String msg) {
            try
            {
                // write on the output stream
                dout.writeUTF(msg);
                dout.flush();
            } catch (IOException e) { e.printStackTrace(); }
    }




//    public String receiveMsg(){
//        // readMessage thread
//        final String[] msg = {""};
//        Thread readMessage = new Thread(new Runnable()
//        {
//            @Override
//            public void run() {
//                try {
//                    // read the message sent to this client
//                    msg[0] = din.readUTF();
//                } catch (IOException e) { e.printStackTrace(); }
//            }
//        });
//        return msg[0];
//    }


//        Thread sendMessage = new Thread(new Runnable()
//        {
//            @Override
//            public void run() {
//                while (true) {
//
//                    // read the message to deliver.
//                    String msg = scn.nextLine();
//
//                    try {
//                        // write on the output stream
//                        dos.writeUTF(msg);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });




//    static Socket s;
//    public DataInputStream din;
//    public DataOutputStream dout;
//    String received = "";
//    final static int ServerPort = 3333;
//
//    public Client(){
//        try
//        {
//            // getting localhost ip
//            InetAddress ip = InetAddress.getByName("localhost");
//
//            // establish the connection
//            s=new Socket(ip,ServerPort);
//
//            // obtaining input and out streams
//            din=new DataInputStream(s.getInputStream());
//            dout=new DataOutputStream(s.getOutputStream());
//
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//
//    }

//    @Override
//    public void update(Observable observable, Object o) {
//        received = (String) o;
//    }

        // sendMessage thread
//        Thread sendMessage = new Thread(new Runnable()
//        {
//            @Override
//            public void run() {
//                while (true) {
//
//                    try {
//                        // write on the output stream
//                        dout.writeUTF(msg);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

    // readMessage thread
//    Thread readMessage = new Thread(new Runnable()
//    {
//        @Override
//        public void run() {
//
//            while (true) {
//                try {
//                    // read the message sent to this client
//                    String msg = din.readUTF();
//                    System.out.println(msg);
//                } catch (IOException e) {
//
//                    e.printStackTrace();
//                }
//            }
//        }
//    });
//
//                readMessage.start();


//    public String sendMsg2(String msg)throws Exception{
////       BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
//
//        dout.writeUTF(msg);
//        dout.flush();
//        return din.readUTF();
//    }

//    public String receiveMsg()
//    {
////        final String[] received = {""};
////
////        Runnable _runnable = new Runnable()
////        {
////            @Override
////            public void run()
////            {
////                try
////                {
////                    received[0] =din.readUTF();
////                } catch (IOException e)
////                {
////                    e.printStackTrace();
////                }
////
////            }
////        };
//        long start = System.currentTimeMillis();
//        long _timeoutMs = 100;
//        String received = "";
//        while (System.currentTimeMillis() < (start + _timeoutMs)) {
////            _runnable.run();
//            try
//            {
//                if (din != null)
//                    received=din.readUTF();
//            } catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//
//        return received;
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
