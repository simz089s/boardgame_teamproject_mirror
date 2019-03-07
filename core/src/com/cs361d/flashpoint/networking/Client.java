package com.cs361d.flashpoint.networking;

import com.badlogic.gdx.Gdx;
import com.cs361d.flashpoint.manager.CreateNewGameManager;
import com.cs361d.flashpoint.manager.DBHandler;
import com.cs361d.flashpoint.view.BoardChatFragment;
import com.cs361d.flashpoint.view.BoardScreen;
import com.cs361d.flashpoint.view.ChatScreen;
import com.cs361d.flashpoint.view.FlashPointGame;
import netscape.javascript.JSObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

  // A client has an instance of the game
  public static FlashPointGame clientFPGame = new FlashPointGame();

  Socket s;
  DataInputStream din; // input stream
  DataOutputStream dout; // output stream
  String ip; // getting Server's ip

  //    ChatClientScreen ccs; // instance of the chat screen so we can modify/update it

  public Client(String serverIP, int serverPort) {
    try {
      ip = serverIP;
      //                ip = "localhost";
      // establish the connection
      s = new Socket(ip, serverPort);

      // obtaining input and out streams
      din = new DataInputStream(s.getInputStream());
      dout = new DataOutputStream(s.getOutputStream());

      //                this.ccs = ccs;
      // readMessage thread constantly listening
      Thread readMessage =
          new Thread(
              new Runnable() {
                @Override
                public void run() {
                  while (true) {
                    try {
                      // read the message sent to this client
                      String msg = din.readUTF();
                      /* Get the command from the string read
                       * CHATWAIT: waiting screen chat changes
                       * CHATGAME: in-game chat changes
                       * GAMESTATE: gameState changes
                       * */

                      JSONParser parser = new JSONParser();

                      JSONObject jsonObject = (JSONObject) parser.parse(msg);
                      Commands c = Commands.fromString(jsonObject.get("command").toString());
                      String message = jsonObject.get("message").toString();
                      switch (c) {
                        case CHATWAIT:
                          if (!msg.equals("")) ChatScreen.addMessageToGui(message);
                          break;
                        case CHATGAME:
                          if (!msg.equals("")) BoardChatFragment.addMessageToGui(message);
                          break;
                        case GAMESTATE:
                          CreateNewGameManager.loadGameFromString(message);
                          Gdx.app.postRunnable(
                              new Runnable() {
                                @Override
                                public void run() {
                                  BoardScreen.redrawBoardEntierly();
                                }
                              });
                          break;
                        default:
                      }

                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  }
                }
              });

      readMessage.start();

    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /* Send a message to a server */
  public synchronized void sendMsg(String msg) {
    try {
      // write on the output stream
      dout.writeUTF(msg);
      dout.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //    public String receiveMsg(){
  //        // readMessage thread
  //        final String[] msg = {""};
  //        Thread readMessage = new Thread(new Runnable()
  //        {
  //            @Override
  //            public void run() {
  //                try {
  //                    // read the message sent to this client
  //                    msg[0] = din.readUTF();
  //                } catch (IOException e) { e.printStackTrace(); }
  //            }
  //        });
  //        return msg[0];
  //    }

  //        Thread sendMessage = new Thread(new Runnable()
  //        {
  //            @Override
  //            public void run() {
  //                while (true) {
  //
  //                    // read the message to deliver.
  //                    String msg = scn.nextLine();
  //
  //                    try {
  //                        // write on the output stream
  //                        dos.writeUTF(msg);
  //                    } catch (IOException e) {
  //                        e.printStackTrace();
  //                    }
  //                }
  //            }
  //        });

  //    static Socket s;
  //    public DataInputStream din;
  //    public DataOutputStream dout;
  //    String received = "";
  //    final static int ServerPort = 3333;
  //
  //    public Client(){
  //        try
  //        {
  //            // getting localhost ip
  //            InetAddress ip = InetAddress.getByName("localhost");
  //
  //            // establish the connection
  //            s=new Socket(ip,ServerPort);
  //
  //            // obtaining input and out streams
  //            din=new DataInputStream(s.getInputStream());
  //            dout=new DataOutputStream(s.getOutputStream());
  //
  //        } catch (IOException e)
  //        {
  //            e.printStackTrace();
  //        }
  //
  //    }

  //    @Override
  //    public void update(Observable observable, Object o) {
  //        received = (String) o;
  //    }

  // sendMessage thread
  //        Thread sendMessage = new Thread(new Runnable()
  //        {
  //            @Override
  //            public void run() {
  //                while (true) {
  //
  //                    try {
  //                        // write on the output stream
  //                        dout.writeUTF(msg);
  //                    } catch (IOException e) {
  //                        e.printStackTrace();
  //                    }
  //                }
  //            }
  //        });

  // readMessage thread
  //    Thread readMessage = new Thread(new Runnable()
  //    {
  //        @Override
  //        public void run() {
  //
  //            while (true) {
  //                try {
  //                    // read the message sent to this client
  //                    String msg = din.readUTF();
  //                    System.out.println(msg);
  //                } catch (IOException e) {
  //
  //                    e.printStackTrace();
  //                }
  //            }
  //        }
  //    });
  //
  //                readMessage.start();

  //    public String sendMsg2(String msg)throws Exception{
  ////       BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
  //
  //        dout.writeUTF(msg);
  //        dout.flush();
  //        return din.readUTF();
  //    }

  //    public String receiveMsg()
  //    {
  ////        final String[] received = {""};
  ////
  ////        Runnable _runnable = new Runnable()
  ////        {
  ////            @Override
  ////            public void run()
  ////            {
  ////                try
  ////                {
  ////                    received[0] =din.readUTF();
  ////                } catch (IOException e)
  ////                {
  ////                    e.printStackTrace();
  ////                }
  ////
  ////            }
  ////        };
  //        long start = System.currentTimeMillis();
  //        long _timeoutMs = 100;
  //        String received = "";
  //        while (System.currentTimeMillis() < (start + _timeoutMs)) {
  ////            _runnable.run();
  //            try
  //            {
  //                if (din != null)
  //                    received=din.readUTF();
  //            } catch (IOException e)
  //            {
  //                e.printStackTrace();
  //            }
  //        }
  //
  //        return received;
  //
  //    }

  //    public void closeClient(){
  //        try
  //        {
  //            dout.close();
  //            s.close();
  //        } catch (IOException e)
  //        {
  //            e.printStackTrace();
  //        }
  //
  //    }

}
