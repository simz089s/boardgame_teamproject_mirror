package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.*;
import com.cs361d.flashpoint.model.FireFighterSpecialities.*;
import com.cs361d.flashpoint.networking.*;
import com.cs361d.flashpoint.screen.Actions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FireFighterTurnManagerAdvance extends FireFighterTurnManager {
  protected final ArrayList<FireFighterAdvanceSpecialties> FREE_SPECIALTIES =
      new ArrayList<FireFighterAdvanceSpecialties>(9);

  private final AtomicBoolean wait = new AtomicBoolean(true);
  private boolean accept = false;
  private UserResponse response = UserResponse.ACCEPT;
  private boolean hasUsedCAF = false;

  public void setAccept(boolean val) {
    this.accept = val;
  }

  public void setUserResponse(UserResponse response) {
    this.response = response;
  }

  public static FireFighterTurnManagerAdvance getInstance() {
    return (FireFighterTurnManagerAdvance) instance;
  }

  protected FireFighterTurnManagerAdvance() {
    super();
    FREE_SPECIALTIES.clear();
    FireFighterAdvanced.reset();
    for (FireFighterAdvanceSpecialties s : FireFighterAdvanceSpecialties.values()) {
      FREE_SPECIALTIES.add(s);
    }
  }

  public void stopWaiting() {
    wait.set(false);
  }

  @Override
  public void addFireFighter(FireFighter f) {
    if (FIREFIGHTERS.contains(f) || !(f instanceof FireFighterAdvanced)) {
      throw new IllegalArgumentException();
    }
    FIREFIGHTERS.add(f);
    if (FIREFIGHTERS.size() > MAX_NUMBER_OF_PLAYERS) {
      throw new IllegalStateException();
    }
  }

  public boolean setInitialSpecialty(FireFighterAdvanceSpecialties specialty) {
    if (FREE_SPECIALTIES.remove(specialty)) {
      FireFighter f = FIREFIGHTERS.removeFirst();
      if (((FireFighterAdvanced) f).getSpecialty()
          != FireFighterAdvanceSpecialties.NO_SPECIALTY) {
        throw new IllegalArgumentException("This fireFighter already has a specialty");
      }
      FireFighterAdvanced newF = FireFighterAdvanced.createFireFighter(f.getColor(), specialty);
      FIREFIGHTERS.addLast(newF);
      Tile currentTile = f.getTile();
      f.removeFromBoard();
      newF.setTile(currentTile);

      return true;
    } else {
      sendActionRejectedMessageToCurrentPlayer("That specialty is not available");
      return false;
    }
  }

  // Covers the veteran bonusRemoval
  @Override
  public boolean endTurn() {
    if (!Server.isEmpty()) {
      sendActionRejectedMessageToCurrentPlayer(
          "You cannot end your turn as the game is not full. More players need to join!");
      return false;
    }
    Server.saveIp = true;
    hasUsedCAF = false;
    FireFighterAdvanced last = (FireFighterAdvanced) FIREFIGHTERS.removeFirst();
    last.removeVeteranBonus();
    FIREFIGHTERS.addLast(last);
    last.firstTurnDone();
    BoardManagerAdvanced.getInstance().endTurnFireSpread();
    if (!getCurrentFireFighter().isFirstTurn()) {
      getCurrentFireFighter().resetActionPoints();
    }
    return verifyVeteranVicinityToAddAp();
  }

  public boolean crewChange(FireFighterAdvanceSpecialties specialty) {
    FireFighterAdvanced f = getCurrentFireFighter();
    if (FREE_SPECIALTIES.contains(specialty) && f.getTile().hasFireTruck() && f.crewChangeAP()) {
      FREE_SPECIALTIES.remove(specialty);
      FREE_SPECIALTIES.add(f.getSpecialty());
      FireFighterAdvanced newF = FireFighterAdvanced.createFireFighter(f.getColor(), specialty);
      newF.setActionPoint(f.getActionPointsLeft());
      newF.setHadVeteranBonus(f.getHadVeteranBonus());
      Tile currentTile = f.getTile();
      f.removeFromBoard();
      newF.setTile(currentTile);
      FIREFIGHTERS.removeFirst();
      FIREFIGHTERS.addFirst(newF);
      newF.setFirstMove(false);
      return true;
    } else {
      sendActionRejectedMessageToCurrentPlayer(
          "You must be located on the fire truck and must have at least 2 AP in order to change your specialty");
      return false;
    }
  }

  @Override
  public FireFighterAdvanced getCurrentFireFighter() {
    return (FireFighterAdvanced) FIREFIGHTERS.peek();
  }

  public boolean treatVictim() {
    Paramedic f = (Paramedic) getCurrentFireFighter();
    if (f == null) {
      sendActionRejectedMessageToCurrentPlayer("You are not a Paramedic");
      return false;
    } else if (!f.getTile().hasRealVictim()) {
      sendActionRejectedMessageToCurrentPlayer("The firefighter is on a tile with no victim");
      return false;
    } else if (f.getTile().getVictim().isCured()) {
      sendActionRejectedMessageToCurrentPlayer("The victim is already cured");
      return false;
    }
    if (f.treatVictimAP()) {
      f.getTile().getVictim().cure();
      return true;
    } else {
      sendActionRejectedMessageToCurrentPlayer(
          "You do not have enough AP to treat the victim, need at least 1");
      return false;
    }
  }

  @Override
  protected boolean canMove(Direction d) {
    if (getCurrentFireFighter() instanceof RescueDog) {
      // Don't allow a move if ap < 3 and moving into fire
      Tile t = getCurrentFireFighter().getTile().getAdjacentTile(d);
      if (t != null && t.hasFire()) {
        sendActionRejectedMessageToCurrentPlayer(
            "You cannot move on a tile with fire as you are the rescue dog");
        return false;
      }

      Tile currentTile = getCurrentFireFighter().getTile();
      // verifies that there is a tile in the direction
      if (currentTile.getAdjacentTile(d) == null) {
        sendActionRejectedMessageToCurrentPlayer("No tile at all at that location");
        return false;
      } else if (currentTile.hasObstacle(d)) {
        Obstacle o = currentTile.getObstacle(d);
        if (o.isDoor()) {
          return false;
        } else if (o.getHealth() < 2) {
          return true;
        } else {
          return false;
        }
      }
      return true;
    } else {
      return super.canMove(d);
    }
  }

  public boolean verifyVeteranVicinity(FireFighterAdvanced targeted) {
    if (targeted instanceof Veteran) {
      return false;
    }
    List<Tile> validTiles = new ArrayList<Tile>();
    for (FireFighter f : FIREFIGHTERS) {
      if (f instanceof Veteran) {
        Tile currentTile = f.getTile();
        validTiles.add(currentTile);
        for (Direction d : Direction.outwardDirections()) {
          int dist = 1;
          if (currentTile.hasObstacle(d)) {
            continue;
          }
          Tile adjacentTile = currentTile.getAdjacentTile(d);
          if (adjacentTile == null) {
            continue;
          }
          validTiles.add(adjacentTile);
          while (dist < 3 && !adjacentTile.hasObstacle(d) && adjacentTile.hasNoFireAndNoSmoke()) {
            adjacentTile = adjacentTile.getAdjacentTile(d);
            if (adjacentTile != null) {
              validTiles.add(adjacentTile);
              dist++;
            } else {
              break;
            }
          }
        }
        break;
      }
    }
    for (Tile t : validTiles) {
      if (t.getFirefighters().contains(targeted)) {
        return true;
      }
    }
    return false;
  }

  private boolean verifyVeteranVicinityToAddAp() {
    if (verifyVeteranVicinity(getCurrentFireFighter()) && getCurrentFireFighter().veteranBonus()) {
      Server.sendToClientsInGame(ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString());
      Server.sendToClientsInGame(ClientCommands.REFRESH_BOARD_SCREEN, "");
      sendMessageToCurrentPlayer(
          "One ExtraAP", "Congratulations, you are in the Veteran's vicinity, you get one extra AP");
      return false;
    }
    return true;
  }

  @Override
  public boolean move(Direction d) {
    FireFighter f = getCurrentFireFighter();
    if (canMove(d)) {
      if (f.moveAP(d)) {
        Tile oldTile = f.getTile();
        Tile newTile = oldTile.getAdjacentTile(d);
        f.setTile(newTile);
        if (newTile.hasPointOfInterest()) {
          if (newTile.hasRealVictim()) {
            newTile.getVictim().reveal();
          } else {
            newTile.setNullVictim();
          }
        }
        flipAdjacentPOIForRescueDog();
        return verifyVeteranVicinityToAddAp();
      } else {
        sendActionRejectedMessageToCurrentPlayer(
            "You do not have enough AP to move to that spot, you need 1 ap to move to a tile with smoke or nothing and 2 to move on a tile with fire");
        return false;
      }
    }
    return false;
  }

  @Override
  public boolean moveWithVictim(Direction d) {
    if (super.moveWithVictim(d)) {
      flipAdjacentPOIForRescueDog();
      return verifyVeteranVicinityToAddAp();
    }
    return false;
  }

  public String[] getAvailableSpecialities() {
    ArrayList<String> sList = new ArrayList<String>();
    for (FireFighterAdvanceSpecialties spec : FREE_SPECIALTIES) {
      if (spec != FireFighterAdvanceSpecialties.NO_SPECIALTY)
        sList.add(spec.toString().replace("_", " "));
    }
    String[] list = new String[sList.size()];
    sList.toArray(list);
    return list;
  }

  public boolean currentHasSpecialty() {
    return !getCurrentFireFighter().hasSpecialty(FireFighterAdvanceSpecialties.NO_SPECIALTY);
  }

  private boolean canMoveWithHazmat(Direction d) {
    Tile currentTile = getCurrentFireFighter().getTile();
    if (!currentTile.hasHazmat()) {
      sendActionRejectedMessageToCurrentPlayer("The tile does not contain a Hazmat");
      return false;
    } else if (currentTile.hasObstacle(d)) {
      sendActionRejectedMessageToCurrentPlayer(
          "You cannot move to that direction as there is an obstacle in the way");
      return false;
    } else if (currentTile.getAdjacentTile(d).hasFire()) {
      sendActionRejectedMessageToCurrentPlayer("You cannot move a Hazmat into fire");
      return false;
    } else if (currentTile.getAdjacentTile(d).hasHazmat()) {
      sendActionRejectedMessageToCurrentPlayer(
          "The tile you want to move the Hazmat to already has a Hazmat");
      return false;
    } else {
      return true;
    }
  }

  public boolean disposeHazmat() {
    Tile currentTile = getCurrentFireFighter().getTile();
    HazmatTechnician h = (HazmatTechnician) getCurrentFireFighter();
    if (h == null) {
      sendActionRejectedMessageToCurrentPlayer("You are not the Hazmat Technician");
      return false;
    } else if (!currentTile.hasHazmat()) {
      sendActionRejectedMessageToCurrentPlayer("The current tile does not have a Hazmat");
      return false;
    } else if (h.removeHazmatAp()) {
      currentTile.setHasHazmat(false);
      return true;
    } else {
      sendActionRejectedMessageToCurrentPlayer("You do not have enough AP, it requires 2 for that task");
      return false;
    }
  }

  public boolean flipPOI(Tile t) {
    ImagingTechnician i = (ImagingTechnician) getCurrentFireFighter();
    if (i == null) {
      sendActionRejectedMessageToCurrentPlayer("You are not the Imaging Technician");
      return false;
    } else if (!t.hasPointOfInterest() || t.getVictim().isRevealed()) {
      sendActionRejectedMessageToCurrentPlayer("The victim is already revealed");
      return false;
    } else if (i.flipPOIAP()) {
      if (t.getVictim().isFalseAlarm()) {
        t.setNullVictim();
      } else {
        t.getVictim().reveal();
      }
      return true;
    } else {
      sendActionRejectedMessageToCurrentPlayer("You need 1 AP to perform that action");
      return false;
    }
  }

  public boolean moveWithHazmat(Direction d) {
    if (canMoveWithHazmat(d)) {
      if (getCurrentFireFighter().moveWithHazmatAp()) {
        Tile currentTile = getCurrentFireFighter().getTile();
        currentTile.setHasHazmat(false);
        Tile adjacent = currentTile.getAdjacentTile(d);
        adjacent.setHasHazmat(true);
        getCurrentFireFighter().setTile(adjacent);
        verifyHazmatRemovalStatus(adjacent);
        return verifyVeteranVicinityToAddAp();
      } else {
        sendActionRejectedMessageToCurrentPlayer(
            "You do not have enough AP to move the Hazmat. 2 AP required");
        return false;
      }
    }
    return false;
  }

  public void verifyHazmatRemovalStatus(Tile t) {
    int i = t.getI();
    int j = t.getJ();
    if (i == 0 || i == 7 || j == 0 || j == 9) {
      t.setHasHazmat(false);
    }
  }

  private void flipAdjacentPOIForRescueDog() {
    if (getCurrentFireFighter() instanceof RescueDog) {
      for (Tile t : getCurrentFireFighter().getTile().getAllAdjacentTile()) {
        if (t.hasPointOfInterest()) {
          if (t.hasRealVictim()) {
            t.getVictim().reveal();
          } else {
            t.setNullVictim();
          }
        }
      }
    }
  }

  @Override
  public void setOrder(List<FireFighterColor> list) {
    LinkedList<FireFighter> newList = new LinkedList<FireFighter>();
    for (FireFighterColor c : list) {
      FireFighter f = FireFighterAdvanced.getFireFighter(c);
      newList.add(f);
    }
    FIREFIGHTERS = newList;
  }

  @Override
  public boolean chooseInitialPosition(Tile t) throws IllegalAccessException {
    FireFighter f = FIREFIGHTERS.getFirst();
    if (f.getTile() != null) {
      throw new IllegalAccessException("The firefighter has already been assigned a tile");
    }
    f.setTile(t);
    flipAdjacentPOIForRescueDog();
    for (FireFighter fi : FIREFIGHTERS) {
      if (verifyVeteranVicinity(((FireFighterAdvanced) fi))) {
        ((FireFighterAdvanced) fi).veteranBonus();
      }
    }
    return FIREFIGHTERS.getFirst().getTile() != null;
  }

  public void removeSpecilty(FireFighterAdvanceSpecialties specialty) {
    FREE_SPECIALTIES.remove(specialty);
  }

  public boolean driveAmbulance(Direction d) {
    if (!Server.isEmpty()) {
      sendActionRejectedMessageToCurrentPlayer(
          "You cannot drive the ambulance as all players are not yet here");
      return false;
    }

    if (getCurrentFireFighter().driveAp()) {
      if (d == Direction.NODIRECTION || d == Direction.NULLDIRECTION) {
        throw new IllegalArgumentException("The direction cannot be " + d.toString());
      } else {
        List<Tile> newLocation =
            BoardManagerAdvanced.getInstance().getTilesThatCanContainAmbulance(d);
        List<Tile> currenLocation =
            BoardManagerAdvanced.getInstance().getTilesThatContainAmbulance();
        for (Tile t : currenLocation) {
          t.setCarrierStatus(CarrierStatus.CANHAVEAMBULANCE);
        }
        for (Tile t : newLocation) {
          t.setCarrierStatus(CarrierStatus.HASAMBULANCE);
        }
        BoardManagerAdvanced.getInstance().verifyVictimRescueStatus(newLocation.get(0));
        BoardManagerAdvanced.getInstance().verifyVictimRescueStatus(newLocation.get(1));
        Server.sendToClientsInGame(ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString());
        Server.sendToClientsInGame(ClientCommands.REFRESH_BOARD_SCREEN, "");
        moveWithAmbulance(currenLocation, newLocation, d);
        return verifyVeteranVicinityToAddAp();
      }
    }
    sendActionRejectedMessageToCurrentPlayer("You need at least 2 AP to move the ambulance.");
    return false;
  }

  private void moveWithAmbulance(List<Tile> oldLocation, List<Tile> newLocation, Direction d) {
    List<FireFighterAdvanced> fireFighterList = new ArrayList<FireFighterAdvanced>();
    for (Tile t : oldLocation) {
      for (FireFighter f : t.getFirefighters()) {
        fireFighterList.add((FireFighterAdvanced) f);
      }
    }

    for (FireFighterAdvanced f : fireFighterList) {
      Server.sendCommandToSpecificClient(
          ClientCommands.ASK_DRIVE_WITH_ENGINE,
          CarrierStatus.HASAMBULANCE.toString(),
          Server.getClientIP(f.getColor()));
      while (wait.get()) ;
      wait.set(true);
      if (response == UserResponse.ACCEPT) {
        repositionFireFighterAfterAmbulanceMove(f, newLocation, d);
        Server.sendToClientsInGame(ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString());
        Server.sendToClientsInGame(ClientCommands.REFRESH_BOARD_SCREEN, "");
      }
    }
  }

  public boolean driveFireTruck(Direction d) {
    if (!Server.isEmpty()) {
      sendActionRejectedMessageToCurrentPlayer(
          "You cannot drive the truck as all players are not yet here");
      return false;
    }
    if (getCurrentFireFighter().getTile().hasFireTruck() && getCurrentFireFighter().driveAp()) {
      if (d == Direction.NODIRECTION || d == Direction.NULLDIRECTION) {
        throw new IllegalArgumentException("The direction cannot be " + d.toString());
      } else {
        List<Tile> newLocation =
            BoardManagerAdvanced.getInstance().getTilesThatCanContainFireTruck(d);
        List<Tile> list = BoardManagerAdvanced.getInstance().getTilesThatContainFireTruck();
        for (int i = 0; i < list.size(); i++) {
          list.get(i).setCarrierStatus(CarrierStatus.CANHAVEFIRETRUCK);
        }
        for (Tile t : newLocation) {
          t.setCarrierStatus(CarrierStatus.HASFIRETRUCK);
        }
        repositionFireFighterAfterFireTruckMove(getCurrentFireFighter(), newLocation, d);
        Server.sendToClientsInGame(ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString());
        Server.sendToClientsInGame(ClientCommands.REFRESH_BOARD_SCREEN, "");
        moveWithFireTruck(list, newLocation, d);
        return verifyVeteranVicinityToAddAp();
      }
    }
    sendActionRejectedMessageToCurrentPlayer(
        "You must be standing on the fire truck and have at least 2 AP.");
    return false;
  }

  private void moveWithFireTruck(List<Tile> oldLocation, List<Tile> newLocation, Direction d) {
    List<FireFighterAdvanced> fireFighterList = new ArrayList<FireFighterAdvanced>();
    for (Tile t : oldLocation) {
      for (FireFighter f : t.getFirefighters()) {
        fireFighterList.add((FireFighterAdvanced) f);
      }
    }

    for (FireFighterAdvanced f : fireFighterList) {
      Server.sendCommandToSpecificClient(
          ClientCommands.ASK_DRIVE_WITH_ENGINE,
          CarrierStatus.HASFIRETRUCK.toString(),
          Server.getClientIP(f.getColor()));
      while (wait.get()) ;
      wait.set(true);
      if (response == UserResponse.ACCEPT) {
        repositionFireFighterAfterFireTruckMove(f, newLocation, d);
        Server.sendToClientsInGame(ClientCommands.SET_GAME_STATE, DBHandler.getBoardAsString());
        Server.sendToClientsInGame(ClientCommands.REFRESH_BOARD_SCREEN, "");
      }
    }
  }

  public void repositionFireFighterAfterFireTruckMove(
      FireFighterAdvanced f, List<Tile> newVehTile, Direction d) {
    Tile currentTile = f.getTile();
    int i = currentTile.getI();
    int j = currentTile.getJ();
    switch (d) {
      case TOP:
        if (j == 0) {
          if (currentTile.getAdjacentTile(Direction.BOTTOM).canContainFireTruck()) {
            f.setTile(newVehTile.get(1));
          } else {
            f.setTile(newVehTile.get(0));
          }
        } else {
          if (currentTile.getAdjacentTile(Direction.BOTTOM).canContainFireTruck()) {
            f.setTile(newVehTile.get(0));
          } else {
            f.setTile(newVehTile.get(1));
          }
        }
        break;
      case BOTTOM:
        if (j == 0) {
          if (currentTile.getAdjacentTile(Direction.BOTTOM).canContainFireTruck()) {
            f.setTile(newVehTile.get(0));
          } else {
            f.setTile(newVehTile.get(1));
          }
        } else {
          if (currentTile.getAdjacentTile(Direction.BOTTOM).canContainFireTruck()) {
            f.setTile(newVehTile.get(1));
          } else {
            f.setTile(newVehTile.get(0));
          }
        }
        break;
      case RIGHT:
        if (i == 0) {
          if (currentTile.getAdjacentTile(Direction.LEFT).canContainFireTruck()) {
            f.setTile(newVehTile.get(1));
          } else {
            f.setTile(newVehTile.get(0));
          }
        } else {
          if (currentTile.getAdjacentTile(Direction.LEFT).canContainFireTruck()) {
            f.setTile(newVehTile.get(0));
          } else {
            f.setTile(newVehTile.get(1));
          }
        }
        break;
      case LEFT:
        if (i == 0) {
          if (currentTile.getAdjacentTile(Direction.LEFT).canContainFireTruck()) {
            f.setTile(newVehTile.get(0));
          } else {
            f.setTile(newVehTile.get(1));
          }
        } else {
          if (currentTile.getAdjacentTile(Direction.LEFT).canContainFireTruck()) {
            f.setTile(newVehTile.get(1));
          } else {
            f.setTile(newVehTile.get(0));
          }
        }
        break;
    }
  }

  public void repositionFireFighterAfterAmbulanceMove(
      FireFighterAdvanced f, List<Tile> newVehTile, Direction d) {
    Tile currentTile = f.getTile();
    int i = currentTile.getI();
    int j = currentTile.getJ();
    switch (d) {
      case TOP:
        if (j == 0) {
          if (currentTile.getAdjacentTile(Direction.BOTTOM).canContainAmbulance()) {
            f.setTile(newVehTile.get(1));
          } else {
            f.setTile(newVehTile.get(0));
          }
        } else {
          if (currentTile.getAdjacentTile(Direction.BOTTOM).canContainAmbulance()) {
            f.setTile(newVehTile.get(0));
          } else {
            f.setTile(newVehTile.get(1));
          }
        }
        break;
      case BOTTOM:
        if (j == 0) {
          if (currentTile.getAdjacentTile(Direction.BOTTOM).canContainAmbulance()) {
            f.setTile(newVehTile.get(0));
          } else {
            f.setTile(newVehTile.get(1));
          }
        } else {
          if (currentTile.getAdjacentTile(Direction.BOTTOM).canContainAmbulance()) {
            f.setTile(newVehTile.get(1));
          } else {
            f.setTile(newVehTile.get(0));
          }
        }
        break;
      case RIGHT:
        if (i == 0) {
          if (currentTile.getAdjacentTile(Direction.LEFT).canContainAmbulance()) {
            f.setTile(newVehTile.get(1));
          } else {
            f.setTile(newVehTile.get(0));
          }
        } else {
          if (currentTile.getAdjacentTile(Direction.LEFT).canContainAmbulance()) {
            f.setTile(newVehTile.get(0));
          } else {
            f.setTile(newVehTile.get(1));
          }
        }
        break;
      case LEFT:
        if (i == 0) {
          if (currentTile.getAdjacentTile(Direction.LEFT).canContainAmbulance()) {
            f.setTile(newVehTile.get(0));
          } else {
            f.setTile(newVehTile.get(1));
          }
        } else {
          if (currentTile.getAdjacentTile(Direction.LEFT).canContainAmbulance()) {
            f.setTile(newVehTile.get(1));
          } else {
            f.setTile(newVehTile.get(0));
          }
        }
        break;
    }
  }

  public boolean fireDeckGun() {
    if (!getCurrentFireFighter().getTile().hasFireTruck()) {
      sendActionRejectedMessageToCurrentPlayer(
          "You cannot use the deck gun as you are not on the FireTruck");
      return false;
    }
    Tile[][] tiles = BoardManagerAdvanced.getInstance().getTilesToUseGunOn();
    for (int i = 0; i < tiles.length; i++) {
      for (int j = 0; j < tiles[0].length; j++) {
        if (tiles[i][j].hasFireFighters()) {
          sendActionRejectedMessageToCurrentPlayer(
              "You cannot fire the deck gun as there is a firefighter in the quadrant");
          return false;
        }
      }
    }
    if (getCurrentFireFighter().fireTheDeckGunAp()) {
      int row = (int) (Math.random() * 3);
      int column = (int) (Math.random() * 4);
      if (getCurrentFireFighter() instanceof Driver) {
        sendDriverMessage(tiles[row][column]);
        switch (response) {
          case THROW_ROW_DIE:
            row = (int) (Math.random() * 3);
            break;
          case THROW_COLUMN_DIE:
            column = (int) (Math.random() * 4);
            break;
          default:
        }
        BoardManagerAdvanced.getInstance().fireDeckGunOnTile(tiles[row][column]);
        return true;
      } else {
        BoardManagerAdvanced.getInstance().fireDeckGunOnTile(tiles[row][column]);
        return true;
      }
    } else {
      sendActionRejectedMessageToCurrentPlayer(
          "You need more AP to fire the deck gun. 4 if you are not the Driver, 2 otherwise");
      return false;
    }
  }

  private void sendDriverMessage(Tile t) {
    List<Tile> list = BoardManagerAdvanced.getInstance().getTilesReachedByDeckGun(t);
    JSONArray iArray = new JSONArray();
    JSONArray jArray = new JSONArray();
    for (Tile tile : list) {
      iArray.add(tile.getI());
      jArray.add(tile.getJ());
    }
    JSONObject all = new JSONObject();
    all.put("i", iArray);
    all.put("j", jArray);
    String ip = Server.getClientIP(getCurrentFireFighter().getColor());
    Server.sendCommandToSpecificClient(ClientCommands.ASK_DRIVER_MSG, all.toJSONString(), ip);
    while (wait.get()) ;
    wait.set(true);
  }

  public List<FireFighterColor> getColorForFireCaptain() {
    List<FireFighterColor> colorList = new ArrayList<FireFighterColor>();
    for (FireFighter f : FIREFIGHTERS) {
      if (!(f instanceof FireCaptain)) {
        colorList.add(f.getColor());
      }
    }
    return colorList;
  }

  public boolean fireCaptainCommand(FireFighterColor color, Actions action, Direction d) {
    if (!(getCurrentFireFighter() instanceof FireCaptain)) {
      sendActionRejectedMessageToCurrentPlayer("You are not the Fire Captain");
      return false;
    }
    FireFighterAdvanced fadv = null;
    for (FireFighter f : FIREFIGHTERS) {
      if (f.getColor() == color) {
        fadv = (FireFighterAdvanced) f;
        break;
      }
    }
    if (fadv instanceof CAFSFirefighter && hasUsedCAF) {
      sendActionRejectedMessageToCurrentPlayer(
          "As the Fire Captain you cannot use more than 1 special AP for the CAFS firefighter");
      return false;
    }
    if (fadv == null) {
      throw new IllegalArgumentException("The color " + color + " is not in the list");
    }
    this.sendToCaptain = true;
    String ip = Server.getClientIP(fadv.getColor());
    if (ip == null) {
      sendActionRejectedMessageToCurrentPlayer(
          "The current player you are trying to move is not yet assigned to a user, please wait");
      this.sendToCaptain = false;
      return false;
    }
    boolean worked = false;
    sendAproval(color, action, d);
    if (accept) {
      if (fadv.setForFireCaptainAction((FireCaptain) getCurrentFireFighter())) {
        FIREFIGHTERS.addFirst(fadv);
        switch (action) {
          case MOVE:
            worked = move(d);
            break;

          case MOVE_WITH_VICTIM:
            worked = moveWithVictim(d);
            break;

          case MOVE_WITH_HAZMAT:
            worked = moveWithHazmat(d);
            break;

          case INTERACT_WITH_DOOR:
            worked = interactWithDoor(d);
            break;
          default:
        }
        FIREFIGHTERS.removeFirst();
        getCurrentFireFighter().setSpecialActionPoints(fadv.getActionPointsLeft());
        fadv.resetSavedActionPoints();
      }
    } else {
      sendActionRejectedMessageToCurrentPlayer(
          "The user of the " + color + " firefighter rejected the move");
    }
    this.sendToCaptain = false;
    if (worked) {
      if (fadv instanceof CAFSFirefighter) {
        hasUsedCAF = true;
      }
      getCurrentFireFighter().firstMoveDone();
    }
    return worked;
  }

  public List<Actions> getFireFighterPossibleActions(FireFighterColor color) {
    FireFighterAdvanced fadv = null;
    List<Actions> actionsList = new ArrayList<Actions>();
    actionsList.add(Actions.MOVE);
    actionsList.add(Actions.MOVE_WITH_VICTIM);
    actionsList.add(Actions.MOVE_WITH_HAZMAT);
    actionsList.add(Actions.INTERACT_WITH_DOOR);

    for (FireFighter f : FIREFIGHTERS) {
      if (f.getColor() == color) {
        fadv = (FireFighterAdvanced) f;
        break;
      }
    }
    if (fadv == null) {
      throw new IllegalArgumentException("The color " + color + " is not in the list");
    }
    if (fadv instanceof RescueDog) {
      actionsList.remove(Actions.MOVE_WITH_HAZMAT);
      actionsList.remove(Actions.INTERACT_WITH_DOOR);
    }

    return actionsList;
  }

  private void sendAproval(FireFighterColor color, Actions action, Direction direction) {
    final JSONObject object = new JSONObject();
    object.put("direction", direction.toString());
    object.put("action", action.toString());
    final String ip = Server.getClientIP(color);
    Server.sendCommandToSpecificClient(
        ClientCommands.ASK_TO_ACCEPT_MOVE, object.toJSONString(), ip);
    while (wait.get()) ;
    wait.set(true);
  }

  public boolean clearHotSpot() {
    StructuralEngineer eng = (StructuralEngineer) getCurrentFireFighter();
    if (eng == null) {
      sendActionRejectedMessageToCurrentPlayer(
          "You are not the engineer, you cannot perform this action");
      return false;
    }
    if (canClear()) {
      if (eng.clearAp()) {
        eng.getTile().removeHotSpot();
        return true;
      }
      sendActionRejectedMessageToCurrentPlayer(
          "You need at least one AP to clear a tile of its hotspot");
      return false;
    }
    return false;
  }

  public boolean repairWall(Direction d) {
    StructuralEngineer eng = (StructuralEngineer) getCurrentFireFighter();
    if (eng == null) {
      sendActionRejectedMessageToCurrentPlayer(
          "You are not the engineer, you cannot perform this action");
      return false;
    }
    if (canRepair(d)) {
      if (eng.repairAp()) {
        eng.getTile().repairObstacle(d);
        return true;
      }
      sendActionRejectedMessageToCurrentPlayer(
          "You need at least two AP to clear a tile of its hotspot");
      return false;
    }
    return false;
  }

  private boolean canClear() {
    for (Tile t : getCurrentFireFighter().getTile().getAllAdjacentTile()) {
      if (t.hasFire()) {
        sendActionRejectedMessageToCurrentPlayer(
            "You cannot clean here as one of the adjacent tile has fire");
        return false;
      }
    }
    if (!getCurrentFireFighter().getTile().hasHotSpot()) {
      sendActionRejectedMessageToCurrentPlayer("The tile you are on does not have a hotspot");
      return false;
    } else {
      return true;
    }
  }

  private boolean canRepair(Direction d) {
    for (Tile t : getCurrentFireFighter().getTile().getAllAdjacentTile()) {
      if (t.hasFire()) {
        sendActionRejectedMessageToCurrentPlayer(
            "You cannot clean here as one of the adjacent tile has fire");
        return false;
      }
    }
    Obstacle o = getCurrentFireFighter().getTile().getObstacle(d);
    if (o.isNull()) {
      sendActionRejectedMessageToCurrentPlayer("You cannot repair the air");
      return false;
    } else if (o.isDoor()) {
      sendActionRejectedMessageToCurrentPlayer("You cannot repair a door");
      return false;
    } else if (o.getHealth() > 1) {
      sendActionRejectedMessageToCurrentPlayer("You cannot fix a perfectly healthy wall");
      return false;
    } else {
      return true;
    }
  }

  public FireFighterAdvanced getFireFighter(FireFighterColor color) {
    for (FireFighter f : FIREFIGHTERS) {
      if (f.getColor() == color) {
        return (FireFighterAdvanced) f;
      }
    }
    return null;
  }

  public boolean spreadFire(Direction d) {
    Pyromancer p = (Pyromancer) getCurrentFireFighter();
    if (p == null) {
      sendActionRejectedMessageToCurrentPlayer("You are not the pyromancer");
      return false;
    }
    Tile t = getCurrentFireFighter().getTile().getAdjacentTile(d);
    if (p.getTile().hasObstacle(d)) {
      sendActionRejectedMessageToCurrentPlayer("The fire spread cannot jump over walls");
      return false;
    } else if (t == null || t.hasAmbulance()) {
      sendActionRejectedMessageToCurrentPlayer(
          "There are either no tile at the location or an ambulance and you cannot spread fire on an ambulance, come on!!!");
      return false;
    } else if (t.hasFire()
        || (t.hasSmoke() && (t.hasPointOfInterest() || t.hasFireFighters() || t.hasHazmat()))) {
      sendActionRejectedMessageToCurrentPlayer(
          "You cannot spread fire on a tile with fire or a tile containing a POI, a firefighter or a Hazmat");
      return false;
    }
    if (!p.spreadFireAP()) {
      sendActionRejectedMessageToCurrentPlayer("You need at least 1 AP to spread the fire!");
      return false;
    }
    if (t.hasNoFireAndNoSmoke()) {
      t.setFireStatus(FireStatus.SMOKE);
    } else {
      t.setFireStatus(FireStatus.FIRE);
    }
    return true;
  }
}
