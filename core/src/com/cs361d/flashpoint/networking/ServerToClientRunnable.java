package com.cs361d.flashpoint.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

class ServerToClientRunnable implements Runnable {
    private String name;
    private String ip;
    final DataInputStream din;      // server-from-client input stream
    final DataOutputStream dout;    // server-to-client output stream

    private Socket s;
    boolean isloggedin;
    private boolean notStopped = true;

    // constructor
    public ServerToClientRunnable(Socket s, String name, DataInputStream din, DataOutputStream dout, String ip) {
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

        while (notStopped) {
            try {
                final String messageToSend = din.readUTF();
                System.out.println(messageToSend);
                Thread runThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Server.serverExecuteCommand(messageToSend);
                    }
                });
                runThread.start();

            } catch (Exception clientKilled) {
                try {
                    // Closing resources
                    this.din.close();
                    this.dout.close();
                    this.s.close();
                    System.out.println("Streams and Socket closed for Client with IP: " + ip);

                    // Close Reader Thread
                    stopClientReadFromServerThread();
                    System.out.println("Reader Thread terminated or Client with IP: " + ip);

                    // Remove Client from Server's and Network's List
                    Server.closeClient(ip);
                    System.out.println();

                } catch (IOException e) {
                    System.out.println("Unable to close Streams for Client with IP: " + ip);
                    e.printStackTrace();
                }
            }
        }
    }
}