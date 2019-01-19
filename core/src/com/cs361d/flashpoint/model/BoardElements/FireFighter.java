package com.cs361d.flashpoint.model.BoardElements;

import com.cs361d.flashpoint.model.Card;

public class FireFighter {

  // firefighter static attributes

  // Texture texture ;
  // Sprite sprite

  private int PlayerNumber;
  private String color;

  // firefighter dynamic attributes

  private Card role;
  private int maxActionPoints;
  private int actionPointsLeft;
  private Tile currentTile;


  public FireFighter(String color) {
    this.color = color;
  }

  public FireFighter(Card pCard) {

    this.role = pCard;

    if (this.role.name == "Generalist") {
      maxActionPoints = 6;
    }
  }

  public int getPlayerNumber() {
    return PlayerNumber;
  }

  public void setPlayerNumber(int playerNumber) {
    PlayerNumber = playerNumber;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public Card getRole() {
    return role;
  }

  public void setRole(Card role) {
    this.role = role;
  }

  public int getMaxActionPoints() {
    return maxActionPoints;
  }

  public void setMaxActionPoints(int maxActionPoints) {
    this.maxActionPoints = maxActionPoints;
  }

  public int getActionPointsLeft() {
    return actionPointsLeft;
  }

  public void setActionPointsLeft(int actionPointsLeft) {
    this.actionPointsLeft = actionPointsLeft;
  }

  public Tile getCurrentTile() {
    return currentTile;
  }

  public void setCurrentTile(Tile currentTile) {
    this.currentTile = currentTile;
  }
}
