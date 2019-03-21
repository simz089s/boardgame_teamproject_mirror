package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.*;
import com.cs361d.flashpoint.model.FireFighterSpecialities.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FireFighterTurnManagerAdvance extends FireFighterTurnManager {
  protected final ArrayList<FireFighterAdvanceSpecialities> FREESPECIALITIES =
      new ArrayList<FireFighterAdvanceSpecialities>(9);

  protected FireFighterTurnManagerAdvance() {
    super();
    FREESPECIALITIES.clear();
    FireFighterAdvanced.reset();
    for (FireFighterAdvanceSpecialities s : FireFighterAdvanceSpecialities.values()) {
      FREESPECIALITIES.add(s);
    }
  }

  @Override
  public void addFireFighter(FireFighter f) {
    if (FIREFIGHTERS.contains(f) || !(f instanceof FireFighterAdvanced)) {
      throw new IllegalArgumentException();
    }
    FIREFIGHTERS.add(((FireFighterAdvanced) f));
    if (FIREFIGHTERS.size() > MAX_NUMBER_OF_PLAYERS) {
      throw new IllegalStateException();
    }
  }

  public boolean setInitialSpeciality(FireFighterAdvanceSpecialities speciality) {
    if (FREESPECIALITIES.remove(speciality)) {
      FireFighter f = FIREFIGHTERS.removeFirst();
      if (((FireFighterAdvanced) f).getSpeciality() != FireFighterAdvanceSpecialities.NO_SPECIALITY) {
        throw new IllegalArgumentException("This fireFighter already has a speciality");
      }
      FIREFIGHTERS.addFirst(FireFighterAdvanced.createFireFighter(f.getColor(), speciality));
      return true;
    } else {
      return false;
    }
  }

  // Covers the veteran bonusRemovove
  @Override
  public void endTurn() throws IllegalAccessException {
    FireFighter fireFighter = getCurrentFireFighter();
    if (!fireFighter.getTile().hasFire()) {
      FireFighterAdvanced last = (FireFighterAdvanced) FIREFIGHTERS.removeFirst();
      last.removeVeteranBonus();
      FIREFIGHTERS.addLast(last);
      last.firstTurnDone();
      BoardManager.getInstance().endTurnFireSpread();
      if (!getCurrentFireFighter().isFirstTurn()) {
        getCurrentFireFighter().resetActionPoints();
        verifyVeteranVacinityToAddAp();
      }
      sendChangeToNetwork();
    } else {
      sendMessageToGui("You cannot end turn as you are currently on a tile with fire");
    }
  }

  public void crewChange(FireFighterAdvanceSpecialities speciality) {
    FireFighterAdvanced f = getCurrentFireFighter();
    if (FREESPECIALITIES.contains(speciality) && f.getTile().hasFireTruck() && f.crewChangeAP()) {
      FREESPECIALITIES.remove(speciality);
      FREESPECIALITIES.add(f.getSpeciality());
      FireFighterAdvanced newF = FireFighterAdvanced.createFireFighter(f.getColor(), speciality);
      newF.setActionPoint(f.getActionPointsLeft());
      newF.setHadVeteranBonus(f.getHadVeteranBonus());
      FIREFIGHTERS.removeFirst();
      FIREFIGHTERS.addFirst(newF);
    } else {

      sendMessageToGui(
          "Either you are not located on the fire truck"
              + " or the speciality is not available or you have less than 2 ap");
    }
  }

  @Override
  public FireFighterAdvanced getCurrentFireFighter() {
    return (FireFighterAdvanced) FIREFIGHTERS.peek();
  }

  public void treatVictim() {
    Paramedic f = (Paramedic) getCurrentFireFighter();
    if (f == null) {
      sendMessageToGui("You are not a paramedic");
    } else if (!f.getTile().hasRealVictim()) {
      sendMessageToGui("The fireFighter is on a tile with no vicitim");
    } else if (f.getTile().getVictim().isCured()) {
      sendMessageToGui("The victim is alreadyCured");
    }
    if (f.treatVictimAP()) {
      f.getTile().getVictim().cure();
    }
    else {
      sendMessageToGui("You do not have enough AP to treat the victim need at least 1");
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
        validTiles.add(f.getTile());
        for (Direction d : Direction.values()) {
          if (d != Direction.NODIRECTION && d != Direction.NULLDIRECTION) {
            int dist = 1;
            Tile inRange = f.getTile().getAdjacentTile(d);
            if (inRange != null) {
              validTiles.add(inRange);
              }
            if (!f.getTile().hasObstacle(d)) {
              while (inRange != null && !inRange.hasObstacle(d) && dist < 4) {
                inRange = inRange.getAdjacentTile(d);
                if (inRange != null && !inRange.hasNoFireAndNoSmoke()) {
                  break;
                }
                dist++;
                if (inRange != null) {
                  validTiles.add(inRange);
                }
              }
            }
          }
        }
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
  public void move(Direction d) {
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
        sendChangeToNetwork();
      } else {
        sendMessageToGui(
                "You do not have enough AP to move to that spot you need 1 ap to move to a tile with smoke or nothing"
                        + " and 2 to move on a tile with fire");
      }
    }
  }

  @Override
  public void moveWithVictim(Direction d) {
    super.moveWithVictim(d);
    verifyVeteranVacinityToAddAp();
    flipAdjacentPOIForRescueDog();
  }

  public String[] getAvailableSpecialities() {
        ArrayList<String> sList = new ArrayList<String>();
        for (FireFighterAdvanceSpecialities spec: FREESPECIALITIES ) {
          if (spec != FireFighterAdvanceSpecialities.NO_SPECIALITY)
            sList.add(spec.toString().replace("_"," "));
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
      }
      else if (currentTile.hasObstacle(d)) {
        sendMessageToGui("You cannot move to that direction as there is an obstacle in the way");
        return false;
      }
      else if (currentTile.getAdjacentTile(d).hasFire()) {
        sendMessageToGui("You cannot move a hazmat into fire");
        return false;
      }
      else if (currentTile.getAdjacentTile(d).hasHazmat()) {
        sendMessageToGui("The tile you want to move the hazmat to already has a hazmat");
        return false;
      }
      else {
        return true;
      }

    }

    public void disposeHazmat() {
      Tile currentTile = getCurrentFireFighter().getTile();
      HazmatTechnician h = (HazmatTechnician) getCurrentFireFighter();
      if (h == null) {
        sendMessageToGui("You are not the Hazmat technician");
      }
      else if (!currentTile.hasHazmat()) {
        sendMessageToGui("The current tile does not have a hazmat");
      }
      else if(h.removeHazmatAp()) {
        currentTile.setHasHazmat(false);
      }
      else {
        sendMessageToGui("You do not have enough AP require 2 for that task");
      }
    }

    public void flipPOI(Tile t) {
      ImagingTechnician i = (ImagingTechnician) getCurrentFireFighter();
      if (i == null) {
        sendMessageToGui("You are not the imaging technician");
      }
      else if (!t.hasPointOfInterest() || t.getVictim().isRevealed()) {
        sendMessageToGui("The victim is already revealed");
      }
      else if (i.flipPOIAP()) {
        if (t.getVictim().isFalseAlarm()) {
          t.setNullVictim();
        }
        else {
          t.getVictim().reveal();
        }
      }
      else {
        sendMessageToGui("You need 1 AP to perform that action");
      }
    }

    public void moveWithHazmat(Direction d) {
      if (canMoveWithHazmat(d)) {
        if (getCurrentFireFighter().moveWithHazmatAp()) {
          Tile currentTile =getCurrentFireFighter().getTile();
          currentTile.setHasHazmat(false);
          Tile adjacent = currentTile.getAdjacentTile(d);
          adjacent.setHasHazmat(true);
          getCurrentFireFighter().setTile(adjacent);
          verifyHazmatRemovalStatus(adjacent);
          verifyVeteranVacinityToAddAp();
        }
        else {
          sendMessageToGui("You do not have enough AP to move the Hazmat 2 AP required");
        }
      }
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
            if (adjacent != null && adjacent.hasPointOfInterest() && adjacent.getVictim().isFalseAlarm()) {
              adjacent.setNullVictim();
            }
            else if (adjacent != null || adjacent.hasPointOfInterest()) {
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
    FireFighter f = FIREFIGHTERS.removeFirst();
    FIREFIGHTERS.addLast(f);
    if (f.getTile() != null) {
      throw new IllegalAccessException("The FireFighter has already been assigned a tile");
    }
    f.setTile(t);
    flipAdjacentPOIForRescueDog();
    for (FireFighter fi : FIREFIGHTERS) {
        if(verifyVeteranVacinity(((FireFighterAdvanced)fi))) {
          ((FireFighterAdvanced) fi).veteranBonus();
        }
    }
    sendChangeToNetwork();
    return FIREFIGHTERS.getFirst().getTile() != null;
  }

  //TODO
  // verify the veteran vacinity at the begining of each turn;
  // Flip the markes in ajacentSpace
}
