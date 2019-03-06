package com.cs361d.flashpoint.Networking;

//import com.cs361d.flashpoint.view.ChatServerScreen;

import com.cs361d.flashpoint.view.FlashPointGame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class ClientHandler implements Runnable
{
    private String name;
    final DataInputStream din;
    final DataOutputStream dout;
    Socket s;
    boolean isloggedin;
    FlashPointGame fpg;

    // constructor
    public ClientHandler(Socket s, String name,
                         DataInputStream din, DataOutputStream dout, FlashPointGame fpg) {
        this.din = din;
        this.dout = dout;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
        this.fpg = fpg;
    }



    @Override
    // Constantly ready to read a new msg
    public void run() {

        String MsgToSend;
        while (true)
        {
            try
            {
                // receive the string
                MsgToSend = din.readUTF();
                System.out.println(MsgToSend);
//
//                if(received.equals("logout")){
//                    this.isloggedin=false;
//                    this.s.close();
//                    break;
//                }

                // break the string into message and recipient part
//                StringTokenizer st = new StringTokenizer(received, "#");
//                String MsgToSend = st.nextToken();
//                String recipient = st.nextToken();

                // search for the recipient in the connected devices list.
                // ar is the vector storing client of active users

                // update messages array for server's chat
                if (!MsgToSend.equals("")) {
//                    fpg.chatScreen.msgs.add(MsgToSend);
//                    String[] newMsg = fpg.chatScreen.msgs.toArray(new String[css.msgs.size()]);
//                    fpg.chatScreen.lstMsg.setItems(newMsg);
                }


                for (ClientHandler mc : Server.clients) {
                    // if the recipient is found, write on its
                    // output stream
//                    if (mc.name.equals(recipient) && mc.isloggedin==true)
//                    {
//                        mc.dos.writeUTF(this.name+" : "+MsgToSend);
//                        break;
//                    }
                    mc.dout.writeUTF(MsgToSend);
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    // closing resources
                    this.din.close();
                    this.dout.close();

                } catch(IOException ee) { ee.printStackTrace();  }
            }

        }

    }
}