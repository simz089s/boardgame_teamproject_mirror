package com.cs361d.flashpoint.manager;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.*;

public class ChatClientManager implements Observer {

    private int tcpPort;
    private int udpPort;

    private List<String> messages = new LinkedList<String>();

    public ChatClientManager() throws IOException {
        this("localhost", NetworkManager.PORT_TCP);
    }

    public ChatClientManager(String host, int tcpPort) throws IOException {
        this.tcpPort = tcpPort;

        final Socket socket = new Socket(host, tcpPort);

        // Writer in new thread to separate writing message and showing messages. Other than
        // concurrency
        // problem in
        // fitting both in one terminal screen, should not be a problem when using two separate
        // "boxes"
        // in GUI (change
        // System.in etc.) or add locks (TODO)
        new Thread("Writer") {
            public void run() {
                //                try (Scanner sc = new Scanner(System.in);
                //                     PrintStream ps = new PrintStream(socket.getOutputStream())) {
                Scanner sc = new Scanner(System.in);
                try {
                    PrintStream ps = new PrintStream(socket.getOutputStream());
                    try {
                        System.out.print("Client> ");
                        do {
                            // Get user input
                            String msg = sc.nextLine();
                            if (msg.equals("exit")) break;

                            // Send over socket
                            ps.println(msg);

                            System.out.print("Client> ");
                        } while (sc.hasNextLine());
                    } finally {
                        ps.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    sc.close();
                }
            }
        }.start();

        Scanner socketInputStreamScanner = new Scanner(socket.getInputStream());
        try {
            do {
                // Get response
                String response = socketInputStreamScanner.nextLine();
                System.out.println("Server> " + response);
                if (response.equals("exit")) break;
            } while (socketInputStreamScanner.hasNextLine());
        } finally {
            socketInputStreamScanner.close();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Server> " + arg);
    }

    public static void main(String[] args) {
        //        new Thread("Server") {
        //            public void run() {
        //                try {
        //                    ChatServerManager srv = new ChatServerManager();
        //                } catch (IOException ex) {
        //                    ex.printStackTrace();
        //                    System.exit(1);
        //                }
        //            }
        //        }.start();

        try {
            ChatClientManager cli = new ChatClientManager();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
