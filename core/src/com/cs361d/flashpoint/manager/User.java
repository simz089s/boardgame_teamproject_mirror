package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.FireFighter;


public class User {
    private FireFighter myFireFigheter;
    private static User instance = new User();

    public static User getInstance() {
        return instance;
    }

    public void assignFireFighter(FireFighter f) {
        myFireFigheter = f;
    }

    public boolean isMyTurn() {
        if (myFireFigheter == null) {
            return false;
        }
        else {
            return myFireFigheter.equals(FireFighterTurnManager.getInstance().getCurrentFireFighter());
        }
    }
}
