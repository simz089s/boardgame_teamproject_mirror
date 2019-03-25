package com.cs361d.flashpoint.networking;

import com.cs361d.flashpoint.screen.FlashPointGame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    // A client has an instance of the game
    public static FlashPointGame clientFPGame = new FlashPointGame();

    private Socket s;
    private DataInputStream din;      // client-from-server input stream
    private DataOutputStream dout;    // client-to-server output stream
    private String clientIP;
    private boolean notStopped = true;

    public Client(String serverIP, int serverPort) throws IOException {
        try {

            // Attempt to connect to server
            s = new Socket(serverIP, serverPort);

            // obtaining input and out streams
            din = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());

            // obtain client IP address
            clientIP = s.getInetAddress().toString().replace("/","");

            // readMessage thread constantly listening
            Thread readMessage =
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            while (notStopped) {
                                String msg;
                                try {
                                    // read the message sent to this client
                                    msg = din.readUTF();
                                    NetworkManager.clientExecuteCommand(msg);
                                } catch (Exception connectionLost) {
                                    try {
                                        System.out.println("Server disconnected: closing client...");
                                        // Closing resources
                                        din.close();
                                        dout.close();
                                        s.close();
                                        System.out.println("Streams and Socket closed for Client with IP: " + clientIP);

                                        // Close Reader Thread
                                        stopClientReadFromClientHandlerThread();
                                        System.out.println("Reader Thread terminated or Client with IP: " + clientIP);

                                    } catch (IOException e) {
                                        System.out.println("Unable to close Streams for Client with IP: " + clientIP);
                                        e.printStackTrace();
                                    }
                                    }
                            }
                        }
                    });
            readMessage.start();

        } catch (UnknownHostException e) {
            System.out.println("Server Not Found");
            e.printStackTrace();
        }

    }

    public void stopClientReadFromClientHandlerThread(){notStopped = false;}

    /* Getters */
    public String getClientIP() { return clientIP; }
    public DataInputStream getDin() { return din; }
    public DataOutputStream getDout() { return dout; }


    /* Send a message to a server */
    public synchronized void sendMsgToServer(String msg) {
        try {
            // write on the output stream
            dout.writeUTF(msg);
            dout.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void stopClientReadingThread() { notStopped = false; }
}
