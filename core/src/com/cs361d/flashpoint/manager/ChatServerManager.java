package com.cs361d.flashpoint.manager;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.cs361d.flashpoint.manager.NetworkManager.*;

import javax.swing.*;

public class ChatServerManager {

    // TODO: make private
    public Server server;

    private int tcpPort;
    private int udpPort;

    public ChatServerManager() {
        this(NetworkManager.PORT_TCP, NetworkManager.PORT_UDP);
    }

    public ChatServerManager(int tcpPort, int udpPort) {
        server =
                new Server() {
                    protected Connection newConnection() {
                        return new ChatConnection();
                    }
                };

        NetworkManager.register(server);

        server.addListener(new Listener() {
            public void received (Connection c, Object object) {
                ChatConnection connection = (ChatConnection)c;

                if (object instanceof RegisterName) {
                    if (connection.name != null) return;
                    String name = ((RegisterName)object).name;
                    if (name == null) return;
                    name = name.trim();
                    if (name.length() == 0) return;

                    connection.name = name;

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.text = name + " connected.";
                    server.sendToAllExceptTCP(connection.getID(), chatMessage);

                    updateNames();

                    return;
                }

                if (object instanceof ChatMessage) {
                    if (connection.name == null) return;
                    ChatMessage chatMessage = (ChatMessage)object;

                    String message = chatMessage.text;
                    if (message == null) return;
                    message = message.trim();
                    if (message.length() == 0) return;

                    chatMessage.text = connection.name + ": " + message;
                    server.sendToAllTCP(chatMessage);
                    return;
                }
            }

            public void disconnected (Connection c) {
                ChatConnection connection = (ChatConnection)c;
                if (connection.name != null) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.text = connection.name + " disconnected.";
                    server.sendToAllTCP(chatMessage);
                    updateNames();
                }
            }
        });

        try {
            server.bind(tcpPort, udpPort);
        } catch (IOException e) {
            System.out.println("Error: Could not bind to port(s): " + tcpPort + " , " + udpPort);
            e.printStackTrace();
        }

        server.start();

        /*
         * TODO:
         *  Refactor into libGDX
         */
        JFrame frame = new JFrame("Chat Server");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosed (WindowEvent evt) {
                server.stop();
            }
        });
        frame.getContentPane().add(new JLabel("Close to stop the chat server."));
        frame.setSize(320, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private class ChatConnection extends Connection {
        private String name;

        public String getName() {
            return name;
        }
    }

    void updateNames() {
        Connection[] connections = server.getConnections();
        List names = new ArrayList(connections.length);

        for (int i = connections.length - 1; i >= 0; i--) {
            ChatConnection connection = (ChatConnection) connections[i];
            names.add(connection.name);
        }

        NetworkManager.UpdateNames updateNames = new NetworkManager.UpdateNames();
        updateNames.names = (String[]) names.toArray(new String[names.size()]);
        server.sendToAllTCP(updateNames);
    }

    /*
     * TODO:
     * remove this test
     */
    public void connectClient(
            int timeoutClient, String hostClient, int tcpPortClient, int udpPortClient) {

        Client client = new Client();
        client.start();
        try {
            client.connect(timeoutClient, hostClient, tcpPortClient, udpPortClient);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        ChatMessage request = new ChatMessage();
//        request.text = "Here is the request";
//        client.sendTCP(request);
        client.sendTCP("From server: Here is the request");

        client.addListener(
                new Listener() {
                    public void received(Connection connection, Object object) {
                        if (object instanceof ChatMessage) {
                            ChatMessage response = (ChatMessage) object;
                            System.out.println("In server: " + response.text);
                        }
                        if (object instanceof String) {
                            String response = (String) object;
                            System.out.println("In server: " + response);
                        }
                    }
                });
    }
}
