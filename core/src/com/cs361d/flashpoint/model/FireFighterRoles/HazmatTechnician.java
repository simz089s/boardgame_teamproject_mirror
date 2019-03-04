package com.cs361d.flashpoint.model.FireFighterRoles;

class HazmatTechnician implements Card {

  String name = "Generalist";

  // create an object of SingleObject
  private static HazmatTechnician instance = new HazmatTechnician();

  // make the constructor private so that this class cannot be instantiated
  private HazmatTechnician() {
    // TODO
  }

  // Get the only object available
  public static HazmatTechnician getInstance() {
    return instance;
  }

  //    @Override
  //    public void specialAction() {
  //        //TODO
  //        //Print No special action except extra AP pts
  //        return;
  //    }
}
