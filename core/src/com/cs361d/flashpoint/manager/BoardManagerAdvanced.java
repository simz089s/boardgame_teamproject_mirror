package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.*;
import com.cs361d.flashpoint.model.FireFighterSpecialities.FireFighterAdvanceSpecialities;
import com.cs361d.flashpoint.model.FireFighterSpecialities.FireFighterAdvanced;
import com.cs361d.flashpoint.networking.ClientCommands;
import com.cs361d.flashpoint.networking.Server;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BoardManagerAdvanced extends BoardManager {

  protected BoardManagerAdvanced() {
    super();
  }

  public void addHazmat(int i, int j) {
    TILE_MAP[i][j].setHasHazmat(true);
  }

  public void addHotspot(int i, int j) {
    TILE_MAP[i][j].addHotSpot();
  }

  public void setNumHotSpotLeft(int num) {
    this.numHotSpotLeft = num;
  }

  public int getNumHotSpotLeft() {
    return this.numHotSpotLeft;
  }

  @Override
  public void endTurnFireSpread() {
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
    checkVictimsAndAdd();
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
        if (numHotSpotLeft > 0) {
          t.addHotSpot();
          numHotSpotLeft--;
        }
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
              colorList.removeFirst(), FireFighterAdvanceSpecialities.NO_SPECIALITY);
      FireFighterTurnManager.getInstance().addFireFighter(f);
    }
  }

  public void addFireFighter(
      int i,
      int j,
      FireFighterColor color,
      int actionPoints,
      int specialApPoints,
      boolean veteranBonus,
      FireFighterAdvanceSpecialities role,
      boolean firstTurn) {
    FireFighterAdvanced f = FireFighterAdvanced.createFireFighter(color, role);
    FireFighterTurnManagerAdvance fta =
        (FireFighterTurnManagerAdvance) (FireFighterTurnManager.getInstance());
    fta.removeSpecilty(role);
    f.setActionPoint(actionPoints);
    f.setSpecialActionPoints(specialApPoints);
    f.setHadVeteranBonus(veteranBonus);
    f.setFirstTurn(firstTurn);
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

  @Override
  public boolean verifyVictimRescueStatus(Tile t) {
    int i = t.getI();
    int j = t.getJ();
    if (i == 0 || i == ROWS - 1 || j == 0 || j == COLUMNS - 1) {
      if (t.hasRealVictim() && t.hasAmbulance()) {
        numVictimSaved++;
        t.setNullVictim();
        Server.sendToClientsInGame(ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString());
        Server.sendToClientsInGame(ClientCommands.REFRESH_BOARD_SCREEN,"");
        sendMessageToGUI("Victim Saved", "Congratulations, you saved one victim!");
        return true;
      }
    }
    if (numVictimSaved >= NUM_VICTIM_SAVED_TO_WIN) {
      endGameMessage("GAME OVER", "Congratulations, you won the game saving 7 victims!");
      return true;
    }
    return false;
  }

  public void addAmbulance(int i, int j) {
    Tile currentTile = TILE_MAP[i][j];
    if (!currentTile.canContainAmbulance()) {
      throw new IllegalArgumentException("This tile cannot contain ambulance");
    }

    Tile neibourTile = null;
    for (Direction d : Direction.values()) {
      if (d != Direction.NODIRECTION) {
        neibourTile = currentTile.getAdjacentTile(d);
        if (neibourTile != null && neibourTile.canContainAmbulance()) {
          break;
        }
        neibourTile = null;
      }
    }
    if (neibourTile == null) {
      throw new IllegalArgumentException("There is no tile adjacent that is an ambulance");
    }
    currentTile.setCarrierStatus(CarrierStatus.HASAMBULANCE);
    neibourTile.setCarrierStatus(CarrierStatus.HASAMBULANCE);
  }

  public void addFireTruck(int i, int j) {
    Tile currentTile = TILE_MAP[i][j];
    if (!currentTile.canContainFireTruck()) {
      throw new IllegalArgumentException("This tile cannot contain fireTruck");
    }

    Tile neibourTile = null;
    for (Direction d : Direction.values()) {
      if (d != Direction.NODIRECTION) {
        neibourTile = currentTile.getAdjacentTile(d);
        if (neibourTile != null && neibourTile.canContainFireTruck()) {
          break;
        }
        neibourTile = null;
      }
    }
    if (neibourTile == null) {
      throw new IllegalArgumentException("There is no tile adjacent that is a fireTrcuh");
    }
    currentTile.setCarrierStatus(CarrierStatus.HASFIRETRUCK);
    neibourTile.setCarrierStatus(CarrierStatus.HASFIRETRUCK);
  }

  @Override
  public ArrayList<Tile> getClosestAmbulanceTile(int i, int j) {
    ArrayList<Tile> tiles = new ArrayList<Tile>();
    double currentSmallestDistance = Double.MAX_VALUE;
    for (int k = 0; k < ROWS; k++) {
      for (int l = 0; l < COLUMNS; l++) {
        if (TILE_MAP[k][l].hasAmbulance()) {
          double squareDistance = Math.pow(i - k, 2) + Math.pow(j - l, 2);
          if (squareDistance < currentSmallestDistance) {
            currentSmallestDistance = squareDistance;
            tiles.clear();
            tiles.add(TILE_MAP[k][l]);
          } else if (squareDistance == currentSmallestDistance) {
            tiles.add(TILE_MAP[k][l]);
          }
        }
      }
    }
    return tiles;
  }

  public static void reset() {
    instance = new BoardManagerAdvanced();
    FireFighterAdvanced.reset();
    FireFighterAdvanced.reset();
  }

  public boolean hasAmbulancePlaced() {
    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLUMNS; j++) {
        if (TILE_MAP[i][j].hasAmbulance()) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean hasFireTruckPlaced() {
    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLUMNS; j++) {
        if (TILE_MAP[i][j].hasFireTruck()) {
          return true;
        }
      }
    }
    return false;
  }

  public List<Tile> getTilesThatCanContainAmbulance(Direction d) {
    List<Tile> list = new ArrayList<Tile>(2);
    switch (d) {
      case TOP:
        for (int j = 0; j < COLUMNS; j++) {
          if (TILE_MAP[0][j].canContainAmbulance()) {
            list.add(TILE_MAP[0][j]);
          }
        }
        break;
      case BOTTOM:
        for (int j = 0; j < COLUMNS; j++) {
          if (TILE_MAP[ROWS - 1][j].canContainAmbulance()) {
            list.add(TILE_MAP[ROWS - 1][j]);
          }
        }
        break;
      case LEFT:
        for (int i = 0; i < ROWS; i++) {
          if (TILE_MAP[i][0].canContainAmbulance()) {
            list.add(TILE_MAP[i][0]);
          }
        }
        break;
      case RIGHT:
        for (int i = 0; i < ROWS; i++) {
          if (TILE_MAP[i][COLUMNS - 1].canContainAmbulance()) {
            list.add(TILE_MAP[i][COLUMNS - 1]);
          }
        }
        break;
      default:
        throw new IllegalArgumentException("This direction is invalid here: " + d);
    }
    if (list.size() < 2) {
      throw new IllegalArgumentException("The list must be of a size greater than 1");
    }
    return list;
  }

  public List<Tile> getTilesThatCanContainFireTruck(Direction d) {
    List<Tile> list = new ArrayList<Tile>(2);
    switch (d) {
      case TOP:
        for (int j = 0; j < COLUMNS; j++) {
          if (TILE_MAP[0][j].canContainFireTruck()) {
            list.add(TILE_MAP[0][j]);
          }
        }
        break;
      case BOTTOM:
        for (int j = 0; j < COLUMNS; j++) {
          if (TILE_MAP[ROWS - 1][j].canContainFireTruck()) {
            list.add(TILE_MAP[ROWS - 1][j]);
          }
        }
        break;
      case LEFT:
        for (int i = 0; i < ROWS; i++) {
          if (TILE_MAP[i][0].canContainFireTruck()) {
            list.add(TILE_MAP[i][0]);
          }
        }
        break;
      case RIGHT:
        for (int i = 0; i < ROWS; i++) {
          if (TILE_MAP[i][COLUMNS - 1].canContainFireTruck()) {
            list.add(TILE_MAP[i][COLUMNS - 1]);
          }
        }
        break;
      default:
        throw new IllegalArgumentException("This direction is invalid here: " + d);
    }
    if (list.size() < 2) {
      throw new IllegalArgumentException("The list must be of a size greater than 1");
    }
    return list;
  }

  public List<Tile> getTilesThatContainFireTruck() {
    List<Tile> list = new ArrayList<Tile>(2);
    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLUMNS; j++) {
        if (TILE_MAP[i][j].hasFireTruck()) {
          list.add(TILE_MAP[i][j]);
        }
      }
    }
    if (list.size() != 2) {
      throw new IllegalArgumentException(
          "The list must be exactly equal to 2 size found: " + list.size());
    }
    return list;
  }

  public List<Tile> getTilesThatContainAmbulance() {
    List<Tile> list = new ArrayList<Tile>(2);
    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLUMNS; j++) {
        if (TILE_MAP[i][j].hasAmbulance()) {
          list.add(TILE_MAP[i][j]);
        }
      }
    }
    if (list.size() != 2) {
      throw new IllegalArgumentException(
          "The list must be exactly equal to 2 size found: " + list.size());
    }
    return list;
  }

  public Direction ambulanceLocationSide() {
    List<Tile> list = getTilesThatContainAmbulance();
    int i = list.get(0).getI();
    int j = list.get(0).getJ();
    if (i == 0) {
      return Direction.TOP;
    } else if (i == ROWS - 1) {
      return Direction.BOTTOM;
    } else if (j == 0) {
      return Direction.LEFT;
    } else {
      return Direction.RIGHT;
    }
  }

  public Direction fireTruckLocationSide() {
    List<Tile> list = getTilesThatContainFireTruck();
    int i = list.get(0).getI();
    int j = list.get(0).getJ();
    if (i == 0) {
      return Direction.TOP;
    } else if (i == ROWS - 1) {
      return Direction.BOTTOM;
    } else if (j == 0) {
      return Direction.LEFT;
    } else {
      return Direction.RIGHT;
    }
  }

  public static BoardManagerAdvanced getInstance() {
    return (BoardManagerAdvanced) instance;
  }

  public Tile[][] getTilesToUseGunOn() {
    Tile[][] tiles = null;
    List<Tile> fireTruckTile = getTilesThatContainFireTruck();
    Tile givenTile = fireTruckTile.get(0);
    int i = givenTile.getI();
    int j = givenTile.getJ();
    if (i == 0) {
      if (j < 5) {
        tiles = getQuadrant(Quadrants.TOP_LEFT);
      } else {
        tiles = getQuadrant(Quadrants.TOP_RIGHT);
      }
    } else if (i == ROWS - 1) {
      if (j < 5) {
        tiles = getQuadrant(Quadrants.BOTTOM_LEFT);
      } else {
        tiles = getQuadrant(Quadrants.BOTTOM_RIGHT);
      }
    } else if (j == 0) {
      if (i < 5) {
        tiles = getQuadrant(Quadrants.TOP_LEFT);
      } else {
        tiles = getQuadrant(Quadrants.BOTTOM_LEFT);
      }

    } else if (j == COLUMNS - 1) {
      if (i < 5) {
        tiles = getQuadrant(Quadrants.TOP_RIGHT);
      } else {
        tiles = getQuadrant(Quadrants.BOTTOM_RIGHT);
      }
    }
    if (tiles == null) {
      throw new IllegalArgumentException("The tiles must exist in the quadrant");
    }

    return tiles;
  }

  public List<Tile> tilesWithPOI() {
    List<Tile> list = new ArrayList<Tile>(3);
    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLUMNS; j++) {
        if (TILE_MAP[i][j].hasPointOfInterest() && !TILE_MAP[i][j].getVictim().isRevealed()) {
          list.add(TILE_MAP[i][j]);
        }
      }
    }
    return list;
  }

  private Tile[][] getQuadrant(Quadrants q) {
    Tile[][] tiles = new Tile[3][4];
    switch (q) {
      case TOP_LEFT:
        for (int i = 1; i < 4; i++) {
          for (int j = 1; j < 5; j++) {
            tiles[i - 1][j - 1] = TILE_MAP[i][j];
          }
          break;
        }
      case TOP_RIGHT:
        for (int i = 1; i < 4; i++) {
          for (int j = 5; j < 9; j++) {
            tiles[i - 1][j - 5] = TILE_MAP[i][j];
          }
        }
        break;
      case BOTTOM_LEFT:
        for (int i = 4; i < 7; i++) {
          for (int j = 1; j < 5; j++) {
            tiles[i - 4][j - 1] = TILE_MAP[i][j];
          }
        }
        break;
      case BOTTOM_RIGHT:
        for (int i = 4; i < 7; i++) {
          for (int j = 5; j < 9; j++) {
            tiles[i - 4][j - 5] = TILE_MAP[i][j];
          }
        }
        break;
      default:
        throw new IllegalArgumentException(
            "The enum for the quadrant does not exits" + q.toString());
    }
    return tiles;
  }

  public void fireDeckGunOnTile(Tile t) {
    List<Tile> list = getTilesReachedByDeckGun(t);
    for (Tile tile : list) {
      tile.setFireStatus(FireStatus.EMPTY);
    }
  }

  public List<Tile> getTilesReachedByDeckGun(Tile t) {
    List<Tile> tiles = new ArrayList<Tile>(5);
    tiles.add(t);
    for (Direction d : Direction.values()) {
      if (d != Direction.NODIRECTION && d != Direction.NULLDIRECTION) {
        if (!t.hasObstacle(d)) {
          Tile next = t.getAdjacentTile(d);
          if (next != null) {
            tiles.add(next);
          }
        }
      }
    }
    return tiles;
  }

  public void incrementHotSpots() {
    this.numHotSpotLeft++;
  }

  public void incrementNumberOfWallsLeft() {
    this.totalWallDamageLeft++;
  }

}
