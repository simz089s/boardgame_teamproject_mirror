package com.cs361d.flashpoint.manager;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

public class ServerNetworkManager {

  public static final int PORT_TCP = 54555;
  public static final int PORT_UDP = 54777;

  private Server server;
  private int tcpPort;
  private int udpPort;

  public ServerNetworkManager() {
    this(PORT_TCP, PORT_UDP);
  }

  public ServerNetworkManager(int tcpPort, int udpPort) {
    server = new Server();
    server.start();
    try {
      server.bind(tcpPort, udpPort);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Server getServer() {
    return server;
  }

  public void connectClient(
      int timeoutClient, String hostClient, int tcpPortClient, int udpPortClient) {
    Client client = new Client();
    client.start();
    try {
      client.connect(timeoutClient, hostClient, tcpPortClient, udpPortClient);
    } catch (IOException e) {
      e.printStackTrace();
    }

    String request = new String();
    request = "Here is the request";
    client.sendTCP(request);

    client.addListener(
        new Listener() {
          public void received(Connection connection, Object object) {
            if (object instanceof String) {
              String response = (String) object;
              System.out.println(response);
            }
          }
        });
  }

  public void test() {

    server.addListener(
        new Listener() {
          public void received(Connection connection, Object object) {
            if (object instanceof String) {
              String request = (String) object;
              System.out.println(request);

              String response = new String();
              response = "Thanks";
              connection.sendTCP(response);
            }
          }
        });

    this.connectClient(5000, "cs.mcgill.ca", 80, PORT_UDP);
  }
}
