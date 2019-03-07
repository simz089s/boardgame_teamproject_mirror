package com.cs361d.flashpoint.networking;

import com.cs361d.flashpoint.view.FlashPointGame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client
{

    // A client has an instance of the game
    public static FlashPointGame clientFPGame = new FlashPointGame();

    Socket s;
    DataInputStream din;      // input stream
    DataOutputStream dout;    // output stream
    String ip;                // getting Server's ip

    public Client(String serverIP, int serverPort) {
        try {
            ip = serverIP;

            // establish the connection
            s = new Socket(ip, serverPort);

            // obtaining input and out streams
            din = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());

            // readMessage thread constantly listening
            Thread readMessage =
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            while (true)
                            {
                                String msg;
                                try
                                {
                                    // read the message sent to this client
                                    msg = din.readUTF();
                                    NetworkManager.ExecuteCommand(msg);
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

            readMessage.start();

        } catch (UnknownHostException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }
    }

    /* Send a message to a server */
    public synchronized void sendMsg(String msg) {
        try {
            // write on the output stream
            dout.writeUTF(msg);
            dout.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
