package com.cs361d.flashpoint.Networking;

import java.util.ArrayList;

public class NetworkManager {

    private static NetworkManager instance = null;

    // variable of type String
    public Server server;
    public ArrayList<Client> clientList = new ArrayList<Client>();

    // private constructor restricted to this class itself
    private NetworkManager() {
    }

    // static method to create instance of Singleton class
    public static NetworkManager getInstance() {
        if (instance == null)
            instance = new NetworkManager();

        return instance;
    }

    public void addServer(Server s) {
        this.server = s;
    }

    public void addNewClient(Client c) {
        this.clientList.add(c);
    }

    public void sendChatMessage(String msg){
        if(Server.getMyIPAddress().equals(Server.SERVER_IP)) {
            server.sendMsg("chat-"+msg);
        }
        else clientList.get(0).sendMsg("chat-"+msg);

    }

    public void sendUpdatedGameState(String msg){
        if(Server.getMyIPAddress().equals(Server.SERVER_IP)) {
            server.sendMsg("game-"+msg);
        }
        else clientList.get(0).sendMsg("game-"+msg);
    }


    public void sendUpdatedStats(String msg){
        if(Server.getMyIPAddress().equals(Server.SERVER_IP)) {
            server.sendMsg("stat-"+msg);
        }
        else clientList.get(0).sendMsg("stat-"+msg);

    }

}
