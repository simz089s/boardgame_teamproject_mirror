package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.*;
import jdk.internal.dynalink.support.LinkerServicesImpl;

import java.awt.*;
import java.util.*;
import java.util.List;

public class BoardManager {
  private final LinkedList<FireFighterColor> colorList = new LinkedList<FireFighterColor>();
  public final static int NUM_VICTIM_SAVED_TO_WIN = 7;
  public final static int MAX_WALL_DAMAGE_POSSIBLE = 24;
  public static final int WIDTH = 10;
  public static final int HEIGHT = 8;
  private final Tile[][] TILE_MAP = new Tile[HEIGHT][WIDTH];
  private int totalWallDamageLeft = MAX_WALL_DAMAGE_POSSIBLE;
  private int numVictimSaved = 0;
  private int numVictimDead = 0;
  private int numFalseAlarmRemoved = 0;
  private List<AbstractVictim> victims = new ArrayList<AbstractVictim>(18);

  // create an object of SingleObject
  private static BoardManager instance = new BoardManager();

  // Get the only object available
  public static BoardManager getInstance() {
    return instance;
  }

  // make the constructor private so that this class cannot be instantiated
  private BoardManager() {
    for (int i = 0; i < HEIGHT; i++) {
      for (int j = 0; j < WIDTH; j++) {
        Tile newTile = new Tile(FireStatus.EMPTY, i, j);
        TILE_MAP[i][j] = newTile;
        Obstacle top = new Obstacle(-1);
        Obstacle left = new Obstacle(-1);
        newTile.addObstacle(Direction.TOP, top);
        newTile.addObstacle(Direction.LEFT, left);

        /*
        Add wall to the bottom and right of adjacent tiles
         */
        if (i > 0) {
          TILE_MAP[i - 1][j].addObstacle(Direction.BOTTOM, top);
        }
        if (j > 0) {
          TILE_MAP[i][j - 1].addObstacle(Direction.RIGHT, left);
        }
        /*
         Add wall to the right of the final tile or the bottom of the final tiles
        */
        if (j == WIDTH - 1) {
          Obstacle right = new Obstacle(-1);
          newTile.addObstacle(Direction.RIGHT, right);
        }
        if (i == HEIGHT - 1) {
          Obstacle bottom = new Obstacle(-1);
          newTile.addObstacle(Direction.BOTTOM, bottom);
        }
      }
    }
    for (int i = 0; i < 6; i++) {
      Victim v = new Victim( true);
      victims.add(v);
    }
    for (int i = 6; i < 18; i++) {
      Victim v = new Victim(false);
      victims.add(v);
    }
    Collections.shuffle(victims);
    colorList.add(FireFighterColor.BLUE);
    colorList.add(FireFighterColor.GREEN);
    colorList.add(FireFighterColor.RED);
    colorList.add(FireFighterColor.ORANGE);
    colorList.add(FireFighterColor.WHITE);
    colorList.add(FireFighterColor.YELLOW);

  }

  public void addWall(int i, int j, Direction d, int health) {
    /*
     if the health is less than 1 then no need to update the walls
    */
    if (health == -1) {
      return;
    } else {
      TILE_MAP[i][j].getObstacle(d).setHealth(health);
    }
  }
  public void setCarrierStatus(int i, int j, CarrierStatus s) {
    TILE_MAP[i][j].setCarrierStatus(s);
  }

  public void setFireFighterNumber(int count) {
    if (count > 6) {
      throw new IllegalArgumentException("Max num of player is 6");
    }
    for (int i = 0; i < count; i++) {
      FireFighter f = FireFighter.createFireFighter(colorList.removeFirst(), 0, 4);
      FireFighterTurnManager.getInstance().addFireFighter(f);
    }
  }
  public void addDoor(int i, int j, Direction d, int health, boolean isOpen) {
    Obstacle obstacle = TILE_MAP[i][j].getObstacle(d);
    /*
    set the boolean isDoor to true
     */
    obstacle.makeDoor();
    obstacle.setHealth(health);
    obstacle.setOpen(isOpen);
  }

  public void addFireFighter(
      int i, int j, FireFighterColor color, int numVictimsSaved, int actionPoints) {
    FireFighter f = FireFighter.createFireFighter(color, numVictimsSaved, actionPoints);
    if (f.getTile() != null) {
      throw new IllegalArgumentException();
    }
    f.setTile(TILE_MAP[i][j]);
    FireFighterTurnManager.getInstance().addFireFighter(f);
  }

  public void addFireStatus(int i, int j, FireStatus f) {
    TILE_MAP[i][j].setFireStatus(f);
  }

  public void setVictims(int numFalseAlarmRemoved, int numVictimsDead, int numVictimSaved) throws IllegalAccessException {
    this.numFalseAlarmRemoved = numFalseAlarmRemoved;
    this.numVictimSaved = numVictimSaved;
    this.numVictimDead = numVictimsDead;
    if (victims.size() != 18) {
      throw new IllegalAccessException("The method setVictims can only be called when the game is created");
    }
    for(int i = 0; i < victims.size(); i++) {
      AbstractVictim v = victims.get(i);
      if (v.isFalseAlarm()) {
        if (numFalseAlarmRemoved > 0) {
          victims.remove(v);
          numFalseAlarmRemoved--;
        }
        }
      else {
        if (numVictimsDead > 0) {
          victims.remove(v);
          numVictimsDead--;
        }
      }
    }
    Collections.shuffle(victims);
  }

  public void addVictim(int i, int j, boolean isRevealed, boolean isCured, boolean isFalseAlarm) {

    for (int k = 0; k < victims.size(); k++) {
      AbstractVictim v = victims.get(k);
      if (v.isFalseAlarm() == isFalseAlarm) {
        if (isRevealed) {
          v.reveal();
        }
        if (isCured) {
          v.cure();
        }
        TILE_MAP[i][j].setVictim(v);
        victims.remove(v);
        break;
      }
    }
    Collections.shuffle(victims);
  }

  public Tile[][] getTiles() {
    return TILE_MAP;
  }

  // Spread the fire at the end of the turn
  public void endTurnFireSpread() throws IllegalAccessException {
    int i = 1+(int) (Math.random()*(HEIGHT-3));
    int j = 1+(int) (Math.random()*(WIDTH-3));
    Tile hitLocation = TILE_MAP[i][j];
    if (hitLocation.hasNoFireAndNoSmoke()) {
      hitLocation.setFireStatus(FireStatus.SMOKE);
    } else if (hitLocation.hasSmoke()) {
      hitLocation.setFireStatus(FireStatus.FIRE);
    } else {
      explosion(i, j);
    }
    updateSmoke();
    List<Tile> tiles = getTilesWithFire();
    // we clear the edge tiles with fire as knocked down player must be able to respawn
    removeEdgeFire();
    updateVictimAndFireFighter(tiles);
  }

  public void reset() {
    instance = new BoardManager();
    FireFighterTurnManager.getInstance().reset();
    FireFighter.reset();
  }
  // Spread the fire in a specific direction after an explosion
  private void explosionFireSpread(int i, int j, Direction d) {
    Tile hitLocation = TILE_MAP[i][j];
    if (!hitLocation.hasFire()) {
      hitLocation.setFireStatus(FireStatus.FIRE);
    } else {
      Obstacle obs = hitLocation.getObstacle(d);
      if (obs.isDestroyed() || (obs.isDoor() && obs.isOpen())) {
        switch (d) {
          case TOP:
            if (i < 1) {
              return;
            }
            explosionFireSpread(i - 1, j, d);
            break;
          case BOTTOM:
            if (i > HEIGHT - 2) {
              return;
            }
            explosionFireSpread(i + 1, j, d);
            break;
          case LEFT:
            if (j < 1) {
              return;
            }
            explosionFireSpread(i, j - 1, d);
            break;
          case RIGHT:
            if (j > WIDTH - 2) {
              return;
            }
            explosionFireSpread(i, j + 1, d);
            break;
          case NODIRECTION:
            throw new IllegalArgumentException(d + " is an invalid Direction.");
          default:
            throw new IllegalArgumentException("The Direction " + d + " does not exit.");
        }
      } else {
        obs.applyDamage();
      }
    }
  }

  // Create an explosion at the specified location and propagates fire
  private void explosion(int i, int j) {
    Tile hitLocation = TILE_MAP[i][j];
    if (i > 0) {
      Obstacle top = hitLocation.getObstacle(Direction.TOP);
      if (top.isDestroyed() || (top.isDoor() && top.isOpen())) {
        explosionFireSpread(i - 1, j, Direction.TOP);
      } else {
        top.applyDamage();
      }
    }
    if (i < HEIGHT - 1) {
      Obstacle bottom = hitLocation.getObstacle(Direction.BOTTOM);
      if (bottom.isDestroyed() || (bottom.isDoor() && bottom.isOpen())) {
        explosionFireSpread(i + 1, j, Direction.BOTTOM);
      } else {
        bottom.applyDamage();
      }
    }
    if (j > 0) {
      Obstacle left = hitLocation.getObstacle(Direction.LEFT);
      if (left.isDestroyed() || (left.isDoor() && left.isOpen())) {
        explosionFireSpread(i, j - 1, Direction.LEFT);
      } else {
        left.applyDamage();
      }
    }
    if (j < WIDTH - 1) {
      Obstacle right = hitLocation.getObstacle(Direction.RIGHT);
      if (right.isDestroyed() || (right.isDoor() && right.isOpen())) {
        explosionFireSpread(i, j + 1, Direction.RIGHT);
      } else {
        right.applyDamage();
      }
    }
  }

  // Ensure that all the tiles with smoke catch fire at the end of turn
  private void updateSmoke() {
    boolean repeat = false;
    for (int i = 0; i < HEIGHT; i++) {
      for (int j = 0; j < WIDTH; j++) {
        if (TILE_MAP[i][j].hasSmoke() && hasFireNextToTile(i, j)) {
          TILE_MAP[i][j].setFireStatus(FireStatus.FIRE);
          repeat = true;
        }
      }
    }
    // Not optimal but we repeat the method until no more changes are seen
    if (repeat) {
      updateSmoke();
    }
  }

  private boolean hasFireNextToTile(int i, int j) {
    if (i > 0 && TILE_MAP[i - 1][j].hasFire()) {
      Obstacle obs = TILE_MAP[i][j].getObstacle(Direction.TOP);
      if (obs.isDestroyed() || (obs.isDoor() && obs.isOpen())) {
        return true;
      }
    }

    if (i < HEIGHT - 1 && TILE_MAP[i + 1][j].hasFire()) {
      Obstacle obs = TILE_MAP[i][j].getObstacle(Direction.BOTTOM);
      if (obs.isDestroyed() || (obs.isDoor() && obs.isOpen())) {
        return true;
      }
    }

    if (j > 0 && TILE_MAP[i][j - 1].hasFire()) {
      Obstacle obs = TILE_MAP[i][j].getObstacle(Direction.LEFT);
      if (obs.isDestroyed() || (obs.isDoor() && obs.isOpen())) {
        return true;
      }
    }

    if (i < HEIGHT - 1 && TILE_MAP[i][j + 1].hasFire()) {
      Obstacle obs = TILE_MAP[i][j].getObstacle(Direction.RIGHT);
      if (obs.isDestroyed() || (obs.isDoor() && obs.isOpen())) {
        return true;
      }
    }

    return false;
  }

  // removes victims that are in a place with fire at the end of turn as well as knockdown player
  private void updateVictimAndFireFighter(List<Tile> tiles) throws IllegalAccessException {
    for (Tile t : tiles) {
      if (t.hasFireFighters()) {
        knockedDown(t.getI(), t.getJ());
      }
      if (t.hasPointOfInterest()) {
        if (t.hasRealVictim()) {
          numVictimDead++;
          if (numVictimDead > 3) {
            // TODO
            /*
            we lost the game as too many victims died
             */
            reset();
          }
          else {
            addNewPointInterest();
          }
        }
        else {
          numVictimSaved++;
        }
        t.setNullVictim();
      }
    }
  }

  protected void addNewPointInterest() {
    AbstractVictim v = victims.remove(0);
    int width;
    int height;
    do
    {
      width = new Random().nextInt(WIDTH - 1) + 1;
      height = new Random().nextInt(HEIGHT - 1) + 1;

    }
      while (TILE_MAP[width][height].hasPointOfInterest());
      TILE_MAP[width][height].setFireStatus(FireStatus.EMPTY);
      if (TILE_MAP[width][height].hasFireFighters()) {
        // here we are on a firefighter and the point of interest is not
        if (v.isFalseAlarm()) {
          addNewPointInterest();
          //TODO
          // message to explain the situation
        }
        else {
          v.reveal();
        }
      }
      else {
        TILE_MAP[width][height].setVictim(v);
      }
  }
  // removes the fire outside the building
  private void removeEdgeFire() {
    for (int i = 0; i < HEIGHT; i++) {
      TILE_MAP[i][0].setFireStatus(FireStatus.EMPTY);
      TILE_MAP[i][WIDTH - 1].setFireStatus(FireStatus.EMPTY);
    }
    for (int j = 0; j < WIDTH; j++) {
      TILE_MAP[0][j].setFireStatus(FireStatus.EMPTY);
      TILE_MAP[HEIGHT - 1][j].setFireStatus(FireStatus.EMPTY);
    }
  }

  public Tile getAdjacentTile(Tile t, Direction d) {
    int i = t.getI();
    int j = t.getJ();
    switch (d) {
      case TOP:
        if (i > 0) {
          return TILE_MAP[i - 1][j];
        }
        break;
      case BOTTOM:
        if (i < BoardManager.HEIGHT - 1) {
          return TILE_MAP[i + 1][j];
        }
        break;
      case LEFT:
        if (j > 0) {
          return TILE_MAP[i][j - 1];
        }
        break;
      case RIGHT:
        if (j < BoardManager.WIDTH - 1) {
          return TILE_MAP[i][j + 1];
        }
        break;
        // if there is no direction return the tile given
      case NODIRECTION:
        return t;
      default:
        throw new IllegalArgumentException();
    }
    return null;
  }

  public List<Tile> getClosestAmbulanceTile(int i, int j) {
    List<Tile> tiles = new ArrayList<Tile>();
    double currentSmallestDistance = Double.MAX_VALUE;
    for (int k = 0; k < HEIGHT; k++) {
      for (int l = 0; l < WIDTH; l++) {
        if (TILE_MAP[k][l].canContainAmbulance()) {
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

  private void knockedDown(int i, int j) throws IllegalAccessException {
    List<Tile> tiles = getClosestAmbulanceTile(i, j);
    for (FireFighter f : TILE_MAP[i][j].getFirefighters()) {
      if (tiles.size() == 1) {
        f.setTile(tiles.get(0));
      } else if (tiles.size() > 1) {
        f.removeFromBoard();

        // TODO
        /*
        We must then ask the fireFighter to what tile he wishes to go to
         */
      } else {
        throw new IllegalStateException();
      }
    }
  }

  public List<Tile> getTilesWithFire() {
    List<Tile> tiles = new ArrayList<Tile>();
    for (Tile[] rows : TILE_MAP) {
      for (Tile t : rows) {
        if (t.hasFire()) {
          tiles.add(t);
        }
      }
    }
    return tiles;
  }

  public void setNumVictimSaved(int s) {
    this.numVictimSaved = s;
  }

  public void setTotalWallDamageLeft(int w) {
    this.totalWallDamageLeft = w;
  }

  public void verifyVictimRescueStatus(Tile t) {
    int i = t.getI();
    int j = t.getJ();
    if (i == 0 || i == HEIGHT-1 || j == 0 || j == WIDTH-1 ) {
      if (t.hasRealVictim()) {
        numVictimSaved++;
        t.setNullVictim();
        addNewPointInterest();
      }
    }
    if (numVictimSaved >= NUM_VICTIM_SAVED_TO_WIN) {
      // TODO
      /*
      the game ends we won
       */
      reset();
    }
  }

  public void useDamageMarker() {
    this.totalWallDamageLeft--;
    if (this.totalWallDamageLeft <= 0) {
      // TODO
      /*
      building collapsed we lost the game
       */
      reset();
    }
  }

  public void chooseInitialPosition() {

  }
}
