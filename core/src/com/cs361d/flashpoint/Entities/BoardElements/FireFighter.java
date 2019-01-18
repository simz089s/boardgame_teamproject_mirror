package com.cs361d.flashpoint.Entities.BoardElements;

import com.cs361d.flashpoint.Entities.Card;

class FireFighter {

  // firefighter static attributes

  // Texture texture ;
  // Sprite sprite

  private int PlayerNumber;
  private FireFighterColor color;

  // firefighter dynamic attributes

  private Card role;
  private int maxActionPoints;
  private int actionPointsLeft;
  private Tile currentTile;

  FireFighter(Card pCard) {

    this.role = pCard;

    if (this.role.name == "Generalist") {
      maxActionPoints = 6;
    }
  }
}
