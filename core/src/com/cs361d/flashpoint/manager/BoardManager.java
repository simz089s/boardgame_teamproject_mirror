package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.*;
import com.cs361d.flashpoint.networking.ClientCommands;
import com.cs361d.flashpoint.networking.Server;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.List;

public class BoardManager implements Iterable<Tile> {
  protected final LinkedList<FireFighterColor> colorList = new LinkedList<FireFighterColor>();
  public static final int NUM_VICTIM_SAVED_TO_WIN = 7;
  public static final int MAX_WALL_DAMAGE_POSSIBLE = 24;
  public static final int COLUMNS = 10;
  public static final int ROWS = 8;
  protected final Tile[][] TILE_MAP = new Tile[ROWS][COLUMNS];
  protected int totalWallDamageLeft = MAX_WALL_DAMAGE_POSSIBLE;
  protected int numVictimSaved = 0;
  protected int numVictimDead = 0;
  protected int numFalseAlarmRemoved = 0;
  protected int numHotSpotLeft = 0;
  protected String gameName = "defaultName";
  protected List<AbstractVictim> victims = new ArrayList<AbstractVictim>(18);
  protected boolean gameEnded = false;

  // create an object of SingleObject

  public void setGameEnded() {
    gameEnded = true;
  }

  protected static BoardManager instance = new BoardManager();

  // Get the only object available
  public static BoardManager getInstance() {
    return instance;
  }

  // make the constructor private so that this class cannot be instantiated
  protected BoardManager() {
    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLUMNS; j++) {
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
        if (j == COLUMNS - 1) {
          Obstacle right = new Obstacle(-1);
          newTile.addObstacle(Direction.RIGHT, right);
        }
        if (i == ROWS - 1) {
          Obstacle bottom = new Obstacle(-1);
          newTile.addObstacle(Direction.BOTTOM, bottom);
        }
      }
    }
    for (int i = 0; i < 5; i++) {
      Victim v = new Victim(true);
      victims.add(v);
    }
    for (int i = 0; i < 11; i++) {
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

  public void setGameName(String name) {
    this.gameName = name;
  }

  public String getGameName() {
    return this.gameName;
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
      FireFighter f = FireFighter.createFireFighter(colorList.removeFirst(), 4);
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

  public void addFireFighter(int i, int j, FireFighterColor color, int actionPoints) {
    FireFighter f = FireFighter.createFireFighter(color, actionPoints);
    if (f.getTile() != null) {
      throw new IllegalArgumentException();
    }
    f.setTile(TILE_MAP[i][j]);
    FireFighterTurnManager.getInstance().addFireFighter(f);
  }

  public void addFireStatus(int i, int j, FireStatus f) {
    TILE_MAP[i][j].setFireStatus(f);
  }

  /**
   * Used to set the game parameters when loading or creating the game that are only held by the
   * BoardManager
   *
   * @param numFalseAlarmRemoved
   * @param numVictimsDead
   * @param numVictimSaved
   * @param totalWallDamageLeft
   */
  public void setGameAtStart(
      int numFalseAlarmRemoved, int numVictimsDead, int numVictimSaved, int totalWallDamageLeft) {
    this.numFalseAlarmRemoved = numFalseAlarmRemoved;
    this.numVictimSaved = numVictimSaved;
    this.numVictimDead = numVictimsDead;
    this.totalWallDamageLeft = totalWallDamageLeft;
    victims = new ArrayList<AbstractVictim>();
    for (int i = 0; i < 12 - numVictimsDead - numVictimSaved; i++) {
      Victim v = new Victim(false);
      victims.add(v);
    }
    for (int i = 0; i < 6 - numFalseAlarmRemoved; i++) {
      Victim v = new Victim(true);
      victims.add(v);
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

  /**
   * Spreads the fire at the end of a turn
   *
   * @see #explosion(int, int)
   * @see #updateSmoke()
   * @see #getTilesWithFire()
   * @see #updateVictimAndFireFighter(List)
   * @see #replenishPOI()
   */
  public void endTurnFireSpread() {
    /* Generate the coordinate of the explosion excluding
     * the edges of the map
     */
    int i = 1 + (int) (Math.random() * (ROWS - 2));
    int j = 1 + (int) (Math.random() * (COLUMNS - 2));
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
    replenishPOI();
  }

  /**
   * Iterate through the board tile counting the number of point of interests left in the game if
   * less than 3 then it spawns new POI if there are some left
   *
   * @see #addNewPointInterest()
   */
  protected void replenishPOI() {
    int numVictimToAdd = 3;
    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLUMNS; j++) {
        if (TILE_MAP[i][j].hasPointOfInterest()) {
          numVictimToAdd--;
        }
      }
    }
    while (numVictimToAdd > 0) {
      addNewPointInterest();
      numVictimToAdd--;
    }
  }

  public static void reset() {
    instance = new BoardManager();
    FireFighterTurnManager.reset();
    FireFighter.reset();
  }

  /**
   * Called when there is no obstacle between the explostion location and the adjecent tile in the
   * direction d responsible for spreading the fire in one direction.
   *
   * @param i adjacent tile i location
   * @param j adjacent tile j location
   * @param d direction of the spread
   */
  protected void explosionFireSpread(int i, int j, Direction d) {
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
            if (i > ROWS - 2) {
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
            if (j > COLUMNS - 2) {
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

  /**
   * Called when a fire spawns on top of a fire we resolve an explosion at the location passed
   * Explosing spreads fire in all adjacent tiles if there is an obstacle in that direction the
   * obstacle gets 1 damage else the fire spread to the adgecent tile if that tile already had fire
   * then we carry on the spread of the fire in the same direction.
   *
   * @param i
   * @param j
   */
  protected void explosion(int i, int j) {
    Tile hitLocation = TILE_MAP[i][j];
    hitLocation.setFireStatus(FireStatus.FIRE);
    if (i > 0) {
      Obstacle top = hitLocation.getObstacle(Direction.TOP);
      if (top.isDestroyed() || (top.isDoor() && top.isOpen())) {
        explosionFireSpread(i - 1, j, Direction.TOP);
      } else {
        top.applyDamage();
      }
    }
    if (i < ROWS - 1) {
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
    if (j < COLUMNS - 1) {
      Obstacle right = hitLocation.getObstacle(Direction.RIGHT);
      if (right.isDestroyed() || (right.isDoor() && right.isOpen())) {
        explosionFireSpread(i, j + 1, Direction.RIGHT);
      } else {
        right.applyDamage();
      }
    }
  }

  /** Ensure that all the tiles with smoke catch fire at the end of turn */
  protected void updateSmoke() {
    boolean repeat = false;
    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLUMNS; j++) {
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

  protected boolean hasFireNextToTile(int i, int j) {
    if (i > 0 && TILE_MAP[i - 1][j].hasFire()) {
      Obstacle obs = TILE_MAP[i][j].getObstacle(Direction.TOP);
      if (obs.isDestroyed() || (obs.isDoor() && obs.isOpen())) {
        return true;
      }
    }

    if (i < ROWS - 1 && TILE_MAP[i + 1][j].hasFire()) {
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

    if (i < ROWS - 1 && TILE_MAP[i][j + 1].hasFire()) {
      Obstacle obs = TILE_MAP[i][j].getObstacle(Direction.RIGHT);
      if (obs.isDestroyed() || (obs.isDoor() && obs.isOpen())) {
        return true;
      }
    }

    return false;
  }

  /**
   * This method is responsible for knocking down players in fire and removing victims standing on a
   * space with fire
   *
   * @param tiles these are the tile that had fire at the end of the firespread before clearing the
   *     map edge
   */
  protected void updateVictimAndFireFighter(List<Tile> tiles) {
    for (Tile t : tiles) {
      if (t.hasFireFighters()) {
        knockedDown(t.getI(), t.getJ());
      }
      if (t.hasPointOfInterest()) {
        if (t.hasRealVictim()) {
          this.numVictimDead++;
          if (numVictimDead > 3) {
            endGameMessage("You lost the game more than 3 victims died");
          }
        } else {
          numFalseAlarmRemoved++;
        }
        t.setNullVictim();
      }
    }
  }

  /**
   * Responsible for adding new POI onto the board, it generates a random numbe for the i and j
   * location until it finds a location with no POI. Then it gets the next victim from the victim
   * array stored by BoardManager. If there is a fireFighter at that location then it reveals the
   * victim if it is a false alarm it then calls the method again else it reveals it. and add that
   * POI at the location wanted.
   */
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

    } while (TILE_MAP[width][height].hasPointOfInterest());
    TILE_MAP[width][height].setFireStatus(FireStatus.EMPTY);
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

  /**
   * Adds a point of interest at the desired location
   *
   * @param i
   * @param j
   */
  protected void addNewPointInterest(int i, int j) {
    AbstractVictim v = victims.get(0);
    TILE_MAP[i][j].setVictim(v);
  }

  /** removes the fire outside the building */
  protected void removeEdgeFire() {
    for (int i = 0; i < ROWS; i++) {
      TILE_MAP[i][0].setFireStatus(FireStatus.EMPTY);
      TILE_MAP[i][COLUMNS - 1].setFireStatus(FireStatus.EMPTY);
    }
    for (int j = 0; j < COLUMNS; j++) {
      TILE_MAP[0][j].setFireStatus(FireStatus.EMPTY);
      TILE_MAP[ROWS - 1][j].setFireStatus(FireStatus.EMPTY);
    }
  }

  /**
   * Get the tile adjacent to t in direction d if there is not tile at that location it returns
   * null.
   *
   * @param t the tile where we will get the adjacent tile from
   * @param d the direction we want to get the adjacent tile at
   * @return
   */
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
        if (i < BoardManager.ROWS - 1) {
          return TILE_MAP[i + 1][j];
        }
        break;
      case LEFT:
        if (j > 0) {
          return TILE_MAP[i][j - 1];
        }
        break;
      case RIGHT:
        if (j < BoardManager.COLUMNS - 1) {
          return TILE_MAP[i][j + 1];
        }
        break;
        // if there is no direction return the tile given
      case NODIRECTION:
        return t;
      default:
        throw new IllegalArgumentException(d.toString());
    }
    return null;
  }

  /**
   * Used basic distance algorithm to retrieve the tiles that canContanAmbulance that are closest to
   * fireFighters located at the i and j param.
   *
   * @param i the i location of the fireFighter
   * @param j the j location of the fireFighter
   * @return List of tile that are closest to the fireFighter and canContainAnAmbulance
   * @see Tile#canContainAmbulance()
   */
  public ArrayList<Tile> getClosestAmbulanceTile(int i, int j) {
    ArrayList<Tile> tiles = new ArrayList<Tile>();
    double currentSmallestDistance = Double.MAX_VALUE;
    for (int k = 0; k < ROWS; k++) {
      for (int l = 0; l < COLUMNS; l++) {
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

  /**
   * If the tile has no fire or has no fireFighters there is no knock down so it returns else it
   * retrieves the closest ambulance tile and while the tile still contains fireFighters it sets
   * them to the new safe ambulance tile
   * @param i i location of the tile we want to verify if there is a knockdown
   * @param j j location of the tile we want to verify if there is a knockdown
   */
  protected void knockedDown(int i, int j) {
    if (!TILE_MAP[i][j].hasFire()) {
      return;
    }
    ArrayList<Tile> tiles = getClosestAmbulanceTile(i, j);
    while (TILE_MAP[i][j].hasFireFighters()) {
      FireFighter f = TILE_MAP[i][j].getFirefighters().get(0);
      if (tiles.size() == 1) {
        if (tiles.get(0).hasFire()) {
          throw new IllegalArgumentException(
              "Issue with tile at location " + i + " " + j + "It should not have fire");
        }
        f.setTile(tiles.get(0));
      } else if (tiles.size() > 1) {
        f.setTile(tiles.get(0));
      } else {
        throw new IllegalStateException();
      }
    }
  }

  /**
   * Method not used was created for familly game where fireFighter is at equidistance of two tiles that CAN_CONTAIN_AMBULANCE
   * but was not finished
   * @param t
   * @param f
   */
  public void chooseForKnockedDown(Tile t, FireFighter f) {
    if (!t.canContainAmbulance()) {
      throw new IllegalArgumentException(
          "Cannot place firefighter outside of ambulance tile after knocked down");
    }
    f.setTile(t);
  }

  /**
   * Used to retrived the tile that have fire on them as we clear the edges of fire before looking
   * at possible knocked down fireFighters or dead victim to avoid infinit knock downs if the
   * ambulance tile is on fire (Elvric: I made a modification to the method look at tiles that makes
   * that code not that useful but I kept the current logic just in case feel free to change that)
   *
   * @return the list of tile that have fire on them
   * @see Tile
   */
  public List<Tile> getTilesWithFire() {
    List<Tile> tiles = new ArrayList<Tile>();
    for (Tile[] rows : TILE_MAP) {
      for (Tile t : rows) {
        if (t.hasFire()) {
          if (t.hasAmbulance()) {
            t.setFireStatus(FireStatus.EMPTY);
            continue;
          }
          tiles.add(t);
        }
      }
    }
    return tiles;
  }

  /**
   * If the tile is an edge tile we verify if the tile contains a real victim
   * if it does we set the tile to have no victim and increment the victim save count by 1
   * @param t the tile where the victim is on
   * @return true if the game has ended because enough victim have been rescued false otherwise
   */
  public boolean verifyVictimRescueStatus(Tile t) {
    int i = t.getI();
    int j = t.getJ();
    if (i == 0 || i == ROWS - 1 || j == 0 || j == COLUMNS - 1) {
      if (t.hasRealVictim()) {
        t.setNullVictim();
        numVictimSaved++;
        if (numVictimSaved >= NUM_VICTIM_SAVED_TO_WIN) {
          endGameMessage("Congratulations, you won the game saving 7 victims!");
          return false;
        }
        Server.sendToClientsInGame(ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString());
        Server.sendToClientsInGame(ClientCommands.REFRESH_BOARD_SCREEN, "");
        sendMessageToAllPlayers("Victim Saved", "Congratulations, a victim was saved!");
        return true;
      }
    }
    return false;
  }

  /**
   * Responsible to decrement the damage markers of the game.
   *
   * @return true if there still exits some damage marker false otherwise as the building collapses
   *     if no more damage markers are left
   */
  public boolean useDamageMarker() {
    this.totalWallDamageLeft--;
    if (this.totalWallDamageLeft <= 0) {
      BoardManager.getInstance().setGameEnded();
      endGameMessage("You lost the game. The building collapsed.");
      return false;
    }
    return true;
  }

  public int getTotalWallDamageLeft() {
    return totalWallDamageLeft;
  }

  public int getNumVictimSaved() {
    return numVictimSaved;
  }

  public int getNumVictimDead() {
    return numVictimDead;
  }

  public int getNumFalseAlarmRemoved() {
    return numFalseAlarmRemoved;
  }

  public static void useExperienceGameManager() {
    instance = new BoardManagerAdvanced();
    FireFighterTurnManager.useFireFighterGameManagerAdvanced();
  }

  public static void useFamilyGameManager() {
    instance = new BoardManager();
  }

  @NotNull
  @Override
  public Iterator<Tile> iterator() {
    List<Tile> tiles = new ArrayList<Tile>();
    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLUMNS; j++) {
        tiles.add(TILE_MAP[i][j]);
      }
    }
    return tiles.iterator();
  }

  /**
   *  Set the gameHasEnded boolean to true of the server so after refreshing the game and only after that it sends
   *  the end game command to all clients for the proper display of the end game message.
   * @param msg message to be displayed to clients when the game ends
   * @see Server#serverExecuteGameCommand(String, String, String)
   */
  public void endGameMessage(String msg) {
    Server.gameHasEnded = true;
    Server.messageToEnd = msg;
  }

  /**
   * Used to retrieve all adgecent tiles that it adjacent tile that are not null and can be accessed with no obstacle
   * blocking the way
   * @param t the tile we want to get all adjacent tiles of
   * @return Tile list of all the adjacent tiles.
   */
  public List<Tile> getAllAdjacentTile(Tile t) {
    List<Tile> list = new ArrayList<Tile>();
    for (Direction direction : Direction.values()) {
      if (direction != Direction.NULLDIRECTION && direction != Direction.NODIRECTION) {
        if (t.hasObstacle(direction)) {
          continue;
        }
        Tile adjacentTile = t.getAdjacentTile(direction);
        if (adjacentTile != null) {
          list.add(adjacentTile);
        }
      }
    }
    return list;
  }

  public Tile getTileAt(int i, int j) {
    return TILE_MAP[i][j];
  }

  public static boolean isAdvanced() {
    return instance instanceof BoardManagerAdvanced;
  }


  protected void sendMessageToAllPlayers(String title, String message) {
    JSONObject obj = new JSONObject();
    obj.put("title", title);
    obj.put("message", message);
    Server.sendToClientsInGame(ClientCommands.SHOW_MESSAGE_ON_SCREEN, obj.toJSONString());
  }
}
