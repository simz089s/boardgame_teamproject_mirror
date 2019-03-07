package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.FireStatus;
import com.cs361d.flashpoint.model.BoardElements.Tile;
import com.cs361d.flashpoint.view.BoardScreen;

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

  private void decrementHotspots() {
    numHotSpotLeft--;
  }

  @Override
  public void endTurnFireSpread() throws IllegalAccessException {
    verifyRemoveHazmat();
    Tile hitLocation;
    int numExec = -1;
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
    if (numExec > 0 && numHotSpotLeft > 0) {
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

  private void verifyRemoveHazmat() {
    List<Tile> hazMatTile = getHazMatTile();
    for (Tile t : hazMatTile) {
      if (t.getI() == 0 || t.getI() == COLUMNS - 1 || t.getJ() == 0 || t.getJ() == ROWS - 1) {
        t.setHasHazmat(false);
      }
    }
  }

}
