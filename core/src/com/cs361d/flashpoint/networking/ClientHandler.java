package com.cs361d.flashpoint.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class ClientHandler implements Runnable {
    private String name;
    private String ip;
    final DataInputStream din;
    final DataOutputStream dout;
    Socket s;
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

    public void stopServerWriteToClientThread() { notStopped = false; }

    public String getName() { return name; }

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
                if (messageToSend.equals(newMsg)) {
                    continue;
                }
                messageToSend = newMsg;
                System.out.println(messageToSend);

                NetworkManager.serverExecuteCommand(messageToSend);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    // closing resources
                    this.din.close();
                    this.dout.close();

                } catch(IOException ee) {
                    ee.printStackTrace();
                    Server.getServer().closeClient(ip);
                }
            }
        }

    }
}