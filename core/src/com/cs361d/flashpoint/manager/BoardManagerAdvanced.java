package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.*;
import com.cs361d.flashpoint.model.FireFighterSpecialities.FireFighterAdvanceSpecialities;
import com.cs361d.flashpoint.model.FireFighterSpecialities.FireFighterAdvanced;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BoardManagerAdvanced extends BoardManager {
  protected BoardManagerAdvanced() {
    super();
  }

  private int numHotSpotLeft = 6;

  public void addHazmat(int i, int j) {
    TILE_MAP[i][j].setHasHazmat(true);
  }

  public void addHotspot(int i, int j) {
    TILE_MAP[i][j].addHotSpot();
  }

  public void setNumHotSpotLeft(int num) {
    this.numHotSpotLeft = num;
  }

  @Override
  public void endTurnFireSpread() throws IllegalAccessException {
    verifyRemoveHazmatOutside();
    Tile hitLocation;
    int numExec = 0;
    do {
      numExec++;
      int i = 1 + (int) (Math.random() * (ROWS - 2));
      int j = 1 + (int) (Math.random() * (COLUMNS - 2));
      hitLocation = TILE_MAP[i][j];
      if (hitLocation.hasNoFireAndNoSmoke()) {
        hitLocation.setFireStatus(FireStatus.SMOKE);
      } else if (hitLocation.hasSmoke()) {
        hitLocation.setFireStatus(FireStatus.FIRE);
      } else {
        explosion(i, j);
      }
      updateSmoke();
      hazMatExplosion();
      List<Tile> tiles = getTilesWithFire();
      // we clear the edge tiles with fire as knocked down player must be able to respawn
      removeEdgeFire();
      updateVictimAndFireFighter(tiles);
    } while (hitLocation.hasHotSpot());
    if (numExec > 1 && numHotSpotLeft > 0) {
      hitLocation.addHotSpot();
      numHotSpotLeft--;
    }
  }

  private List<Tile> getHazMatTile() {
    List<Tile> hazMatTiles = new ArrayList<Tile>();
    Iterator<Tile> it = this.iterator();
    while (it.hasNext()) {
      Tile t = it.next();
      if (t.hasHazmat()) {
        hazMatTiles.add(t);
      }
    }
    return hazMatTiles;
  }

  private void hazMatExplosion() {
    List<Tile> hazMatTile = getHazMatTile();
    for (Tile t : hazMatTile) {
      if (t.hasFire()) {
        explosion(t.getI(), t.getJ());
        t.setHasHazmat(false);
        t.addHotSpot();
        hazMatExplosion();
      }
    }
  }

  private void verifyRemoveHazmatOutside() {
    List<Tile> hazMatTile = getHazMatTile();
    for (Tile t : hazMatTile) {
      if (t.getI() == 0 || t.getI() == COLUMNS - 1 || t.getJ() == 0 || t.getJ() == ROWS - 1) {
        t.setHasHazmat(false);
      }
    }
  }

  @Override
  public void setFireFighterNumber(int count) {
    if (count > 6) {
      throw new IllegalArgumentException("Max num of player is 6");
    }
    for (int i = 0; i < count; i++) {
      FireFighterAdvanced f =
          FireFighterAdvanced.createFireFighter(
              colorList.removeFirst(), FireFighterAdvanceSpecialities.NO_ROLE);
      FireFighterTurnManager.getInstance().addFireFighter(f);
    }
  }

  public void addFireFighter(
      int i,
      int j,
      FireFighterColor color,
      int actionPoints,
      int specialApPoints,
      FireFighterAdvanceSpecialities role) {
    if (role == FireFighterAdvanceSpecialities.NO_ROLE) {
      throw new IllegalArgumentException("A fireFighter on the board must have a role");
    }
    FireFighterAdvanced f =
        FireFighterAdvanced.createFireFighter(color, role);
    f.setActionPoint(actionPoints);
    f.setSpecialActionPoints(specialApPoints);
    if (f.getTile() != null) {
      throw new IllegalArgumentException();
    }
    f.setTile(TILE_MAP[i][j]);
    FireFighterTurnManager.getInstance().addFireFighter(f);
  }

  @Override
  protected void addNewPointInterest() {
    if (victims.isEmpty()) {
      //      throw new IllegalStateException("we cannot add a new point of interest if the list is
      // empty");
      return;
    }
    AbstractVictim v = victims.remove(0);
    int width;
    int height;
    do {
      width = 1 + (int) (Math.random() * (ROWS - 2));
      height = 1 + (int) (Math.random() * (COLUMNS - 2));

    } while (TILE_MAP[width][height].hasPointOfInterest() || TILE_MAP[width][height].hasFire());
    TILE_MAP[width][height].setVictim(v);
    if (TILE_MAP[width][height].hasFireFighters()) {
      // here we are on a firefighter and the point of interest is not
      if (v.isFalseAlarm()) {
        TILE_MAP[width][height].setNullVictim();
        addNewPointInterest();
      } else {
        v.reveal();
      }
    }
  }
}
