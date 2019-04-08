package com.cs361d.flashpoint.networking;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class NetworkManager {

  private static NetworkManager instance;
  //    final public String DEFAULT_SERVER_IP = getMyIPAddress(); //CHANGE THIS TO WORK OUTSIDE
  // MCGILL WORLD
  // public static final String DEFAULT_SERVER_IP = "142.157.74.18"; // Simon public ip address
//  public static final String DEFAULT_SERVER_IP = "142.157.67.193"; // Elvric public ip address
   final public static String DEFAULT_SERVER_IP = "142.157.66.229"; // DC public ip
//  final public static String DEFAULT_SERVER_IP = "142.157.66.229"; // JZ public ip
  //final public static String DEFAULT_SERVER_IP = "142.157.66.15"; // Matty V IP
  public static final int DEFAULT_SERVER_PORT = 54590;

  static final NetworkLogger networkLogger = new NetworkLogger(); // Calls logger setup(), otherwise static (this line may be useless in fact)

  // In the controller, because both Server and Client need this class
  public static String createJSON(String command, String msg) {

    JSONObject message = new JSONObject();
    message.put("command", command);
    message.put("message", msg);
    message.put("IP", getMyPublicIP());
    return message.toString();
  }

  public static String getMyPublicIP() {
    String systemIPAddress = "";
    try {
      URL url_name = new URL("http://bot.whatismyipaddress.com");

      BufferedReader sc;
      sc = new BufferedReader(new InputStreamReader(url_name.openStream()));

      // reads system IPAddress
      systemIPAddress = sc.readLine().trim();
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Public IP Address: " + systemIPAddress + "\n");
    return systemIPAddress;
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
