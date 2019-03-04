package com.cs361d.flashpoint.manager;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import com.cs361d.flashpoint.manager.NetworkManager.*;

import javax.swing.*;

public class ChatServerManager {

    private int tcpPort;
    private int udpPort;

    private List<Observer> users = new ArrayList<Observer>(6);

    private List<String> messages = new ArrayList<String>();

    public ChatServerManager() throws IOException {
        this(NetworkManager.PORT_TCP);
    }

    public ChatServerManager(int tcpPort) throws IOException {
        this.tcpPort = tcpPort;

        ServerSocket srvSocket = new ServerSocket(tcpPort);
        Socket socket = srvSocket.accept();

        System.out.println("Client connected. Server ready.");

        Scanner socketInputStreamScanner = new Scanner(socket.getInputStream());
        PrintStream ps = new PrintStream(socket.getOutputStream());
        try {
            /*
             * TODO:
             *  Refactor into libGDX (button or something)
             */
            JFrame frame = new JFrame("Chat Server");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.addWindowListener(
                    new WindowAdapter() {
                        public void windowClosed(WindowEvent evt) {
                            //                            ps.println("exit");
                            System.exit(0);
                        }
                    });
            frame.getContentPane().add(new JLabel("Close to stop the chat server."));
            frame.setSize(320, 200);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            while (socketInputStreamScanner.hasNextLine()) {
                // Receive message
                String msg = socketInputStreamScanner.nextLine();
                System.out.println("LOG: " + msg);

                // Send response
                ps.println("Your current line is: " + "'" + msg + "'");
            }
        } finally{
            socketInputStreamScanner.close();
            ps.close();
        }
    }

    // TODO: Use Command pattern + Observer pattern + Visitor pattern ???
    private void pushMessage(String msg) {
        for (Observer obs : users) {
            obs.update(new Observable(), msg);
        }
    }

    public static void main(String[] args) {
        try {
            ChatServerManager srv = new ChatServerManager();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
