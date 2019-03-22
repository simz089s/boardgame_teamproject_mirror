package com.cs361d.flashpoint.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

class ClientHandler implements Runnable {
    private String name;
    private String ip;
    final DataInputStream din;      // server-from-client input stream
    final DataOutputStream dout;    // server-to-client output stream

    private Socket s;
    boolean isloggedin;
    private boolean notStopped = true;

    // constructor
    public ClientHandler(Socket s, String name, DataInputStream din, DataOutputStream dout, String ip) {
        this.din = din;
        this.dout = dout;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
        this.ip = ip;
    }

    public void stopClientReadFromServerThread() { notStopped = false; }

    public String getName() { return name; }
    public Socket getSocket() { return s; }

    @Override
    // Constantly ready to read a new msg
    public void run() {

        String messageToSend = "";
        while (notStopped)
        {
            try
            {
                String newMsg = din.readUTF();
                // Don`t send the same string more than once
                if (messageToSend.equals(newMsg))
                {
                    continue;
                }
                messageToSend = newMsg;
                System.out.println(messageToSend);

                NetworkManager.serverExecuteCommand(messageToSend);
            } catch (EOFException clientKilled) {
                try {
                    // Closing resources
                    this.din.close();
                    this.dout.close();
                    this.s.close();
                    System.out.println("Streams and Socket closed for Client with IP: "+ip);

                    // Close Reader Thread
                    stopClientReadFromServerThread();
                    System.out.println("Reader Thread terminated or Client with IP: "+ip);

                    // Remove Client from Server's and Network's List
                    Server.getServer().closeClient(ip);
                    System.out.println();

                } catch (IOException e) {
                    System.out.println("Unable to close Streams for Client with IP: "+ip);
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
                try {
                    this.din.close();
                    this.dout.close();
                    System.out.println("Unable to close Streams for Client with IP: "+ip);

                } catch(IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }
}