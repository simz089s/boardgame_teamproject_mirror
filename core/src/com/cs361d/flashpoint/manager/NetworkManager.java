package com.cs361d.flashpoint.manager;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class NetworkManager {

    public static final int PORT_TCP = 54555;
    public static final int PORT_UDP = 54777;

    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(String.class);
        kryo.register(String[].class);
    }

    public static class RegisterName {
        public String name;
    }

    public static class UpdateNames {
        public String[] names;
    }

    public static class ChatMessage {
        public String text;
    }

}
