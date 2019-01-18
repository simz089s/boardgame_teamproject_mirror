package com.cs361d.flashpoint.model.FireFighterRoles;

import com.cs361d.flashpoint.model.Card;

class CAFSFirefighter implements Card {

  private String name = "CAFSFirefighter";

  // create an object of SingleObject
  private static CAFSFirefighter instance = new CAFSFirefighter();

  // make the constructor private so that this class cannot be instantiated
  private CAFSFirefighter() {
    // TODO
  }

  // Get the only object available
  public static CAFSFirefighter getInstance() {
    return instance;
  }

  //    @Override
  //    public void specialAction() {
  //        //TODO
  //        //Print No special action except extra AP pts
  //        return;
  //    }
}
