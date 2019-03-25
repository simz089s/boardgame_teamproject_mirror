package com.cs361d.flashpoint.networking;

import com.badlogic.gdx.Gdx;
import com.cs361d.flashpoint.manager.*;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.screen.BoardChatFragment;
import com.cs361d.flashpoint.screen.BoardScreen;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class NetworkManager {

  private static NetworkManager instance;
  //    final public String DEFAULT_SERVER_IP = getMyIPAddress(); //CHANGE THIS TO WORK OUTSIDE
  // MCGILL WORLD
  // public static final String DEFAULT_SERVER_IP = "142.157.74.18"; // Simon public ip address
//  public static final String DEFAULT_SERVER_IP = "142.157.67.193"; // Elvric public ip address
   //final public static String DEFAULT_SERVER_IP = "142.157.149.154"; // DC public ip
  final public static String DEFAULT_SERVER_IP = "142.157.75.165"; // Matty V IP
  public static final int DEFAULT_SERVER_PORT = 54590;



  // In the controller, because both Server and Client need this class
  public static String createJSON(String command, String msg) {

    JSONObject message = new JSONObject();
    message.put("command", command);
    message.put("message", msg);
    message.put("IP", getMyPublicIP());
    return message.toString();
  }

  public static String getMyPublicIP() {
    String systemipaddress = "";
    try {
      URL url_name = new URL("http://bot.whatismyipaddress.com");

      BufferedReader sc;
      sc = new BufferedReader(new InputStreamReader(url_name.openStream()));

      // reads system IPAddress
      systemipaddress = sc.readLine().trim();
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Public IP Address: " + systemipaddress + "\n");
    return systemipaddress;
  }

  public static String getMyIPAddress() {
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
//
//  public String getIPByAddress(String address) {
//    String ipAddress = null;
//    try {
//      InetAddress addr = InetAddress.getByName(address);
//      ipAddress = addr.getHostAddress();
//      System.out.println("IP Address = " + ipAddress);
//
//    } catch (UnknownHostException e) {
//      e.printStackTrace();
//    }
//
//    return ipAddress;
//  }
//
//  public String getHostNameByAdress(String address) {
//    String hostname = null;
//    try {
//      InetAddress addr = InetAddress.getByName(address);
//      hostname = addr.getHostName();
//      System.out.println("Host Name = " + hostname);
//
//    } catch (UnknownHostException e) {
//      e.printStackTrace();
//    }
//
//    return hostname;
//  }







}
