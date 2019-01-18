package com.cs361d.flashpoint.model.FireFighterRoles;

import com.cs361d.flashpoint.model.Card;

class Driver implements Card {

  private String name = "Driver";

  // create an object of SingleObject
  private static Driver instance = new Driver();

  // make the constructor private so that this class cannot be instantiated
  private Driver() {
    // TODO
  }

  // Get the only object available
  public static Driver getInstance() {
    return instance;
  }

  //    @Override
  //    public void specialAction() {
  //        //TODO
  //        //Print No special action except extra AP pts
  //        return;
  //    }
}
