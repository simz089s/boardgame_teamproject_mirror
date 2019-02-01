package com.cs361d.flashpoint.manager;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

public class ChatClientManager {
    public static void main(String[] args) {

    ChatServerManager chatMan = new ChatServerManager();
    Server srv = chatMan.server;

    /*
     * TODO:
     * (remove this) test exchange
     */
    srv.addListener(
        new Listener() {
          public void received(Connection connection, Object object) {
            if (object instanceof NetworkManager.ChatMessage) {
              NetworkManager.ChatMessage request = (NetworkManager.ChatMessage) object;
              System.out.println("In client: " + request.text);

              NetworkManager.ChatMessage response = new NetworkManager.ChatMessage();
              response.text = "From client: Thanks";
              connection.sendTCP(response);
            }
            if (object instanceof String) {
              String request = (String) object;
              System.out.println("In client: " + request);

              String response = "From client: Thanks";
              connection.sendTCP(response);
            }
          }
        });

    chatMan.connectClient(5000, "localhost", NetworkManager.PORT_TCP, NetworkManager.PORT_UDP);

//        Log.set(Log.LEVEL_DEBUG);
//        new ChatClientManager();
    }
}
