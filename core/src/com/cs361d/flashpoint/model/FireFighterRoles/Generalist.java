package com.cs361d.flashpoint.model.FireFighterRoles;

class Generalist implements Card {

  private String name = "Generalist";

  // create an object of SingleObject
  private static Generalist instance = new Generalist();

  // make the constructor private so that this class cannot be instantiated
  private Generalist() {
    // TODO
  }

  // Get the only object available
  public static Generalist getInstance() {
    return instance;
  }

  //    @Override
  //    public void specialAction() {
  //        //TODO
  //        //Print No special action except extra AP pts
  //        return;
  //    }
}
