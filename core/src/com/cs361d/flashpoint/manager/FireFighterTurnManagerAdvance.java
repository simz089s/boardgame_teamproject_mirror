package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.*;
import com.cs361d.flashpoint.model.FireFighterSpecialities.*;
import com.cs361d.flashpoint.networking.ClientCommands;
import com.cs361d.flashpoint.networking.DriverResponse;
import com.cs361d.flashpoint.networking.Server;
import com.cs361d.flashpoint.screen.Actions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FireFighterTurnManagerAdvance extends FireFighterTurnManager {
  protected final ArrayList<FireFighterAdvanceSpecialities> FREESPECIALITIES =
      new ArrayList<FireFighterAdvanceSpecialities>(9);

  private final AtomicBoolean wait = new AtomicBoolean(true);
  private boolean accept = false;
  private DriverResponse response = DriverResponse.ACCEPT;

  public void setAccept(boolean val) {
    this.accept = val;
  }

  public void setDriverResponse(DriverResponse response) {
    this.response = response;
  }

  public static FireFighterTurnManagerAdvance getInstance() {
    return (FireFighterTurnManagerAdvance) instance;
  }

  protected FireFighterTurnManagerAdvance() {
    super();
    FREESPECIALITIES.clear();
    FireFighterAdvanced.reset();
    for (FireFighterAdvanceSpecialities s : FireFighterAdvanceSpecialities.values()) {
      FREESPECIALITIES.add(s);
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

  public boolean setInitialSpeciality(FireFighterAdvanceSpecialities speciality) {
    if (FREESPECIALITIES.remove(speciality)) {
      FireFighter f = FIREFIGHTERS.removeFirst();
      if (((FireFighterAdvanced) f).getSpeciality()
          != FireFighterAdvanceSpecialities.NO_SPECIALITY) {
        throw new IllegalArgumentException("This fireFighter already has a speciality");
      }
      FireFighterAdvanced newF = FireFighterAdvanced.createFireFighter(f.getColor(), speciality);
      FIREFIGHTERS.addLast(newF);
      Tile currentTile = f.getTile();
      f.removeFromBoard();
      newF.setTile(currentTile);

      return true;
    } else {
      sendMessageToGui("That speciality is not available");
      return false;
    }
  }

  // Covers the veteran bonusRemovove
  @Override
  public void endTurn() {
    // TODO do not end turn if all users are not yet present
    FireFighter fireFighter = getCurrentFireFighter();
    if (!fireFighter.getTile().hasFire()) {
      FireFighterAdvanced last = (FireFighterAdvanced) FIREFIGHTERS.removeFirst();
      last.removeVeteranBonus();
      FIREFIGHTERS.addLast(last);
      last.firstTurnDone();
      BoardManagerAdvanced.getInstance().endTurnFireSpread();
      if (!getCurrentFireFighter().isFirstTurn()) {
        getCurrentFireFighter().resetActionPoints();
        verifyVeteranVacinityToAddAp();
      }
    } else {
      sendMessageToGui("You cannot end turn as you are currently on a tile with fire");
    }
  }

  public boolean crewChange(FireFighterAdvanceSpecialities speciality) {
    FireFighterAdvanced f = getCurrentFireFighter();
    if (FREESPECIALITIES.contains(speciality) && f.getTile().hasFireTruck() && f.crewChangeAP()) {
      FREESPECIALITIES.remove(speciality);
      FREESPECIALITIES.add(f.getSpeciality());
      FireFighterAdvanced newF = FireFighterAdvanced.createFireFighter(f.getColor(), speciality);
      newF.setActionPoint(f.getActionPointsLeft());
      newF.setHadVeteranBonus(f.getHadVeteranBonus());
      Tile currentTile = f.getTile();
      f.removeFromBoard();
      newF.setTile(currentTile);
      FIREFIGHTERS.removeFirst();
      FIREFIGHTERS.addFirst(newF);
      return true;
    } else {
      sendMessageToGui(
          "You must be located on the fire truck"
              + " and must have at least 2 ap in order to change the of Specialty");
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
      sendMessageToGui("You are not a paramedic");
      return false;
    } else if (!f.getTile().hasRealVictim()) {
      sendMessageToGui("The fireFighter is on a tile with no vicitim");
      return false;
    } else if (f.getTile().getVictim().isCured()) {
      sendMessageToGui("The victim is alreadyCured");
      return false;
    }
    if (f.treatVictimAP()) {
      f.getTile().getVictim().cure();
      return true;
    } else {
      sendMessageToGui("You do not have enough AP to treat the victim need at least 1");
      return false;
    }
  }

  @Override
  protected boolean canMove(Direction d) {
    if (getCurrentFireFighter() instanceof RescueDog) {
      // Don't allow a move if ap < 3 and moving into fire
      Tile t = getCurrentFireFighter().getTile().getAdjacentTile(d);
      if (t != null && t.hasFire()) {
        sendMessageToGui("You cannot move on a tile with fire as you are the rescue dog");
        return false;
      }

      Tile currentTile = getCurrentFireFighter().getTile();
      // verifies that there is a tile in the direction
      if (currentTile.getAdjacentTile(d) == null) {
        sendMessageToGui("No tile at all at that location");
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

  public boolean verifyVeteranVacinity(FireFighterAdvanced targeted) {
    if (targeted instanceof Veteran) {
      return false;
    }
    List<Tile> validTiles = new ArrayList<Tile>();
    for (FireFighter f : FIREFIGHTERS) {
      if (f instanceof Veteran) {
        Tile currentTile = f.getTile();
        validTiles.add(currentTile);
        for (Direction d : Direction.values()) {
          int dist = 2;
          if (d != Direction.NODIRECTION && d != Direction.NULLDIRECTION) {
            Tile adjacentTile = currentTile.getAdjacentTile(d);
            if (adjacentTile == null) {
              continue;
            }
            validTiles.add(adjacentTile);
            while (dist < 4 && !adjacentTile.hasObstacle(d)) {
              adjacentTile = adjacentTile.getAdjacentTile(d);
              if (adjacentTile != null && adjacentTile.hasNoFireAndNoSmoke()) {
                validTiles.add(adjacentTile);
                dist++;
              } else {
                break;
              }
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

  private void verifyVeteranVacinityToAddAp() {
    if (verifyVeteranVacinity(getCurrentFireFighter())) {
      getCurrentFireFighter().veteranBonus();
    }
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
        verifyVeteranVacinityToAddAp();
        flipAdjacentPOIForRescueDog();
        return true;
      } else {
        sendMessageToGui(
            "You do not have enough AP to move to that spot you need 1 ap to move to a tile with smoke or nothing"
                + " and 2 to move on a tile with fire");
        return false;
      }
    }
    return false;
  }

  @Override
  public boolean moveWithVictim(Direction d) {
    if (super.moveWithVictim(d)) {
      verifyVeteranVacinityToAddAp();
      flipAdjacentPOIForRescueDog();
      return true;
    }
    return false;
  }

  public String[] getAvailableSpecialities() {
    ArrayList<String> sList = new ArrayList<String>();
    for (FireFighterAdvanceSpecialities spec : FREESPECIALITIES) {
      if (spec != FireFighterAdvanceSpecialities.NO_SPECIALITY)
        sList.add(spec.toString().replace("_", " "));
    }
    String[] list = new String[sList.size()];
    sList.toArray(list);
    return list;
  }

  public boolean currentHasSpeciality() {
    return !getCurrentFireFighter().hasSpeciality(FireFighterAdvanceSpecialities.NO_SPECIALITY);
  }

  private boolean canMoveWithHazmat(Direction d) {
    Tile currentTile = getCurrentFireFighter().getTile();
    if (!currentTile.hasHazmat()) {
      sendMessageToGui("The tile does not contain a hazmat");
      return false;
    } else if (currentTile.hasObstacle(d)) {
      sendMessageToGui("You cannot move to that direction as there is an obstacle in the way");
      return false;
    } else if (currentTile.getAdjacentTile(d).hasFire()) {
      sendMessageToGui("You cannot move a hazmat into fire");
      return false;
    } else if (currentTile.getAdjacentTile(d).hasHazmat()) {
      sendMessageToGui("The tile you want to move the hazmat to already has a hazmat");
      return false;
    } else {
      return true;
    }
  }

  public boolean disposeHazmat() {
    Tile currentTile = getCurrentFireFighter().getTile();
    HazmatTechnician h = (HazmatTechnician) getCurrentFireFighter();
    if (h == null) {
      sendMessageToGui("You are not the Hazmat technician");
      return false;
    } else if (!currentTile.hasHazmat()) {
      sendMessageToGui("The current tile does not have a hazmat");
      return false;
    } else if (h.removeHazmatAp()) {
      currentTile.setHasHazmat(false);
      return true;
    } else {
      sendMessageToGui("You do not have enough AP require 2 for that task");
      return false;
    }
  }

  public boolean flipPOI(Tile t) {
    ImagingTechnician i = (ImagingTechnician) getCurrentFireFighter();
    if (i == null) {
      sendMessageToGui("You are not the imaging technician");
      return false;
    } else if (!t.hasPointOfInterest() || t.getVictim().isRevealed()) {
      sendMessageToGui("The victim is already revealed");
      return false;
    } else if (i.flipPOIAP()) {
      if (t.getVictim().isFalseAlarm()) {
        t.setNullVictim();
      } else {
        t.getVictim().reveal();
      }
      return true;
    } else {
      sendMessageToGui("You need 1 AP to perform that action");
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
        verifyVeteranVacinityToAddAp();
        return true;
      } else {
        sendMessageToGui("You do not have enough AP to move the Hazmat 2 AP required");
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
      Tile currentTile = getCurrentFireFighter().getTile();
      for (Direction d : Direction.values()) {
        if (d != Direction.NODIRECTION && d != Direction.NULLDIRECTION) {
          Tile adjacent = currentTile.getAdjacentTile(d);
          if (adjacent != null
              && adjacent.hasPointOfInterest()
              && adjacent.getVictim().isFalseAlarm()) {
            adjacent.setNullVictim();
          } else if (adjacent != null && adjacent.hasPointOfInterest()) {
            adjacent.getVictim().reveal();
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
      throw new IllegalAccessException("The FireFighter has already been assigned a tile");
    }
    f.setTile(t);
    flipAdjacentPOIForRescueDog();
    for (FireFighter fi : FIREFIGHTERS) {
      if (verifyVeteranVacinity(((FireFighterAdvanced) fi))) {
        ((FireFighterAdvanced) fi).veteranBonus();
      }
    }
    return FIREFIGHTERS.getFirst().getTile() != null;
  }

  public void removeSpecilty(FireFighterAdvanceSpecialities speciality) {
    FREESPECIALITIES.remove(speciality);
  }

  public boolean driveAmbulance(Direction d) {
    if (getCurrentFireFighter().driveAp()) {
      if (d == Direction.NODIRECTION || d == Direction.NULLDIRECTION) {
        throw new IllegalArgumentException("The direction cannot be " + d.toString());
      } else {
        List<Tile> newLocation =
            BoardManagerAdvanced.getInstance().getTilesThatCanContainAmbulance(d);
        List<Tile> list = BoardManagerAdvanced.getInstance().getTilesThatContainAmbulance();
        for (Tile t : list) {
          t.setCarrierStatus(CarrierStatus.CANHAVEAMBULANCE);
        }
        for (Tile t : newLocation) {
          t.setCarrierStatus(CarrierStatus.HASAMBULANCE);
        }
        BoardManagerAdvanced.getInstance().verifyVictimRescueStatus(newLocation.get(0));
        BoardManagerAdvanced.getInstance().verifyVictimRescueStatus(newLocation.get(1));
        verifyVeteranVacinityToAddAp();
        return true;
      }
    }
    sendMessageToGui("You need at least 2 AP to move the ambulance.");
    return false;
  }

  public boolean driveFireTruck(Direction d) {
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
        verifyVeteranVacinityToAddAp();
        return true;
      }
    }
    sendMessageToGui("You must be standing on the fire truck and have at least 2 AP.");
    return false;
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

  public boolean fireDeckGun() {
    if (!getCurrentFireFighter().getTile().hasFireTruck()) {
      sendMessageToGui("You cannot use the deck gun as you are not on the FireTruck");
      return false;
    }
    Tile[][] tiles = BoardManagerAdvanced.getInstance().getTilesToUseGunOn();
    for (int i = 0; i < tiles.length; i++) {
      for (int j = 0; j < tiles[0].length; j++) {
        if (tiles[i][j].hasFireFighters()) {
          sendMessageToGui(
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
      sendMessageToGui(
          "You need more AP to fire the deck gun 4 if you are not the Driver 2 otherwise");
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
      sendMessageToGui("You are not the fire captain");
      return false;
    }
    FireFighterAdvanced fadv = null;
    for (FireFighter f : FIREFIGHTERS) {
      if (f.getColor() == color) {
        fadv = (FireFighterAdvanced) f;
        break;
      }
    }
    if (fadv == null) {
      throw new IllegalArgumentException("The color" + color + " is not in the list");
    }
    this.sendToCaptain = true;
    String ip = Server.getClientIP(fadv.getColor());
    if (ip == null) {
      sendMessageToGui(
          "The current player you are trying to move is not yet assigned to a User please wait");
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
      sendMessageToGui("The user of the " + color + "fireFighter rejected the move");
    }
    this.sendToCaptain = false;
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
      throw new IllegalArgumentException("The color" + color + " is not in the list");
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
}
