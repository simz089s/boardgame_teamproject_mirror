package com.cs361d.flashpoint.model.FireFighterRoles;

import com.cs361d.flashpoint.model.Card;

class FireCaptain implements Card {

  private String name = "FireCaptain";

  // create an object of SingleObject
  private static FireCaptain instance = new FireCaptain();

  // make the constructor private so that this class cannot be instantiated
  private FireCaptain() {
    // TODO
  }

  // Get the only object available
  public static FireCaptain getInstance() {
    return instance;
  }

  //    @Override
  //    public void specialAction() {
  //        //TODO
  //        //Print No special action except extra AP pts
  //        return;
  //    }
}
