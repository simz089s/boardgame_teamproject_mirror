package com.cs361d.flashpoint.networking;

import com.cs361d.flashpoint.manager.FireFighterTurnManager;
import com.cs361d.flashpoint.model.BoardElements.FireFighter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

public class NetworkManager {

    private static NetworkManager instance = new NetworkManager();
//    final public String SERVER_IP = getMyIPAddress(); //hardcoded ip for server ******
    final public static String SERVER_IP = "142.157.149.34"; //public ip address
    final public static int SERVER_PORT = 54590;

    // variable of type String
    public Server server;
    public ArrayList<Client> clientList = new ArrayList<Client>();

    // private constructor restricted to this class itself
    private NetworkManager() {
    }

    // static method to create instance of Singleton class
    public static NetworkManager getInstance() {
        return instance;
    }

    public void addServer(Server s) {
        this.server = s;
    }

    public void addNewClient(Client c) {
        this.clientList.add(c);
    }

    public void sendCommand(String command, String msg) {
        String jsonMsg = createJSON(command, msg);
        if(getMyPublicIP().equals(SERVER_IP))
            server.sendMsg(jsonMsg);

        else
            for (Client c : clientList) {
                c.sendMsg(jsonMsg);
            }

    }


    private String createJSON(String command, String msg){

        JSONObject message = new JSONObject();
        message.put("command", command);
        message.put("message", msg);
        return message.toString();

    }

    public String getMyPublicIP() {
        String systemipaddress = "";
        try
        {
            URL url_name = new URL("http://bot.whatismyipaddress.com");

            BufferedReader sc;
            sc = new BufferedReader(new InputStreamReader(url_name.openStream()));

            // reads system IPAddress
            systemipaddress = sc.readLine().trim();
        }
        catch (Exception e) { e.printStackTrace(); }

        System.out.println("Public IP Address: " + systemipaddress +"\n");
        return systemipaddress;
    }

    public String getMyIPAddress() {
        String ipAddress = null;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            ipAddress = addr.getHostAddress();
            System.out.println("LOCAL ONE IP Address = " + ipAddress);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return ipAddress;
    }

    public String getIPByAddress(String address) {
        String ipAddress = null;
        try {
            InetAddress addr = InetAddress.getByName(address);
            ipAddress = addr.getHostAddress();
            System.out.println("IP Address = " + ipAddress);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return ipAddress;
    }

    public String getHostNameByAdress(String address) {
        String hostname = null;
        try {
            InetAddress addr = InetAddress.getByName(address);
            hostname = addr.getHostName();
            System.out.println("Host Name = " + hostname);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return hostname;
    }

}
