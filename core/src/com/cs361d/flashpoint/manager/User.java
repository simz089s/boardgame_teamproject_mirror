package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.FireFighter;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.networking.Server;


public class User {
    private FireFighterColor myColor;
    String name = "defaultName";
    private static User instance = new User();

    public static User getInstance() {
        return instance;
    }

    public void assignFireFighter(FireFighterColor c) {
        myColor = c;
        Server.addColorToHashMap(this,c );
    }

    public boolean isMyTurn() {
        FireFighterColor color = Server.getFireFighterColors().get(this);
        if (color == null) {
            return false;
        }
        else {
            return color == FireFighterTurnManager.getInstance().getCurrentFireFighter().getColor();
        }
    }

    public FireFighterColor getColor() {
        return Server.getFireFighterColors().get(this);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
