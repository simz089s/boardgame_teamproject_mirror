package com.cs361d.flashpoint.Entities.FireFighterRoles;

import com.cs361d.flashpoint.Entities.Card;

class RescueSpecialist implements Card {

  private String name = "RescueSpecialist";

  // create an object of SingleObject
  private static RescueSpecialist instance = new RescueSpecialist();

  // make the constructor private so that this class cannot be instantiated
  private RescueSpecialist() {
    // TODO
  }

  // Get the only object available
  public static RescueSpecialist getInstance() {
    return instance;
  }

  //    @Override
  //    public void specialAction() {
  //        //TODO
  //        //Print No special action except extra AP pts
  //        return;
  //    }
}
