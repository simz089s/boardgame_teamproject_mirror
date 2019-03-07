package com.cs361d.flashpoint.networking;


import com.cs361d.flashpoint.view.FlashPointGame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class ClientHandler extends Server implements Runnable {
    private String name;
    final DataInputStream din;
    final DataOutputStream dout;
    Socket s;
    boolean isloggedin;
    FlashPointGame fpg;

    // constructor
    public ClientHandler(Socket s, String name,
                         DataInputStream din, DataOutputStream dout, FlashPointGame fpg) {
        this.din = din;
        this.dout = dout;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
        this.fpg = fpg;
    }



    @Override
    // Constantly ready to read a new msg
    public void run() {

        String messageToSend = "";
        while (true)
        {
            try
            {
                String newMsg = din.readUTF();
                // Don`r send the same string more than once
                if (messageToSend.equals(newMsg)) {
                    continue;
                }
                messageToSend = newMsg;
                System.out.println(messageToSend);

                this.updateServerGui(messageToSend);

                // Send string to every client
                for (ClientHandler mc : Server.clientThreads.values()) {
                    mc.dout.writeUTF(messageToSend);
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    // closing resources
                    this.din.close();
                    this.dout.close();

                } catch(IOException ee) { ee.printStackTrace();  }
            }
        }

    }
}