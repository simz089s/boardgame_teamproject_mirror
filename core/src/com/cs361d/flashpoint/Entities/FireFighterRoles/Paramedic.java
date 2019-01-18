package com.cs361d.flashpoint.Entities.FireFighterRoles;

import com.cs361d.flashpoint.Entities.Card;

class Paramedic implements Card {

  private String name = "Paramedic";

  // create an object of SingleObject
  private static Paramedic instance = new Paramedic();

  // make the constructor private so that this class cannot be instantiated
  private Paramedic() {
    // TODO
  }

  // Get the only object available
  public static Paramedic getInstance() {
    return instance;
  }

  //    @Override
  //    public void specialAction() {
  //        //TODO
  //        //Print No special action except extra AP pts
  //        return;
  //    }
}
