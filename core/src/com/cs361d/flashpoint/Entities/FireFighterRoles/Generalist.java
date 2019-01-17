package com.cs361d.flashpoint.Entities.FireFighterRoles;

import com.cs361d.flashpoint.Entities.Card;

public class Generalist implements Card {

    String name = "Generalist";

    //create an object of SingleObject
    private static Generalist instance = new Generalist();

    //make the constructor private so that this class cannot be instantiated
    private Generalist(){}

    //Get the only object available
    public static Generalist getInstance(){
        return instance;
    }

//    @Override
//    public void specialAction() {
//        //TODO
//        //Print No special action except extra AP pts
//        return;
//    }
}
