package com.cs361d.flashpoint.Entities.FireFighterRoles;

import com.cs361d.flashpoint.Entities.Card;

class ImagingTechnician implements Card {

  private String name = "ImagingTechnician";

  // create an object of SingleObject
  private static ImagingTechnician instance = new ImagingTechnician();

  // make the constructor private so that this class cannot be instantiated
  private ImagingTechnician() {
    // TODO
  }

  // Get the only object available
  public static ImagingTechnician getInstance() {
    return instance;
  }

  //    @Override
  //    public void specialAction() {
  //        //TODO
  //        //Print No special action except extra AP pts
  //        return;
  //    }
}
