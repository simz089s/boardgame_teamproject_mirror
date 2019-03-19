package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.*;
import com.cs361d.flashpoint.model.FireFighterSpecialities.*;

import java.util.ArrayList;
import java.util.List;

public class FireFighterTurnManagerAdvance extends FireFighterTurnManager {
  protected final ArrayList<FireFighterAdvanceSpecialities> FREESPECIALITIES =
      new ArrayList<FireFighterAdvanceSpecialities>(9);

  protected FireFighterTurnManagerAdvance() {
    super();
    FREESPECIALITIES.clear();
    for (FireFighterAdvanceSpecialities s : FireFighterAdvanceSpecialities.values()) {
      FREESPECIALITIES.add(s);
    }
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

  public boolean assignSpecialityForTheFirstTime(FireFighterAdvanceSpecialities speciality) {
    if (FREESPECIALITIES.remove(speciality)) {
      FireFighter f = FIREFIGHTERS.removeFirst();
      if (((FireFighterAdvanced) f).getSpeciality() == FireFighterAdvanceSpecialities.NO_SPECIALITY) {
        throw new IllegalArgumentException("This fireFighter already has a speciality");
      }
      FIREFIGHTERS.addLast(FireFighterAdvanced.createFireFighter(f.getColor(), speciality));
      return true;
    } else {
      sendMessageToGui("The speciality Asked is not available");
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
      BoardManager.getInstance().endTurnFireSpread();
      last.resetActionPoints();
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
    return (FireFighterAdvanced) super.getCurrentFireFighter();
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
    List<Tile> validTiles = new ArrayList<Tile>();
    for (FireFighter f : FIREFIGHTERS) {
      if (f instanceof Veteran) {
        validTiles.add(f.getTile());
        for (Direction d : Direction.values()) {
          if (d != Direction.NODIRECTION) {
            int dist = 1;
            Tile inRange = f.getTile().getAdjacentTile(d);
            validTiles.add(inRange);
            if (!f.getTile().hasObstacle(d)) {
              while (!inRange.hasObstacle(d) && dist < 4) {
                inRange = inRange.getAdjacentTile(d);
                if (!inRange.hasNoFireAndNoSmoke()) {
                  break;
                }
                dist++;
                validTiles.add(inRange);
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

  private void verifyVeteranVacintyToAddAp() {
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
            BoardManager.getInstance().addNewPointInterest();
          }
        }
        verifyVeteranVacintyToAddAp();
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
    if (canMoveWithVictim(d)) {
      FireFighter f = getCurrentFireFighter();
      if (f.moveWithVictimAP()) {
        AbstractVictim v = f.getTile().getVictim();
        Tile oldTile = f.getTile();
        Tile newTile = oldTile.getAdjacentTile(d);
        oldTile.setNullVictim();
        newTile.setVictim(v);
        f.setTile(newTile);
        verifyVeteranVacintyToAddAp();
        if (!BoardManager.getInstance().verifyVictimRescueStatus(newTile)) {
          sendChangeToNetwork();
        }
      } else {
        sendMessageToGui("You need 2 AP to move with a victim");
      }
    }
  }

  public String[] getAvailableSpecialities() {
        List<String> sList = new ArrayList<String>();
        for (FireFighterAdvanceSpecialities spec: FREESPECIALITIES ) {
            sList.add(spec.toString().replace("_"," "));
        }
        return (String[]) sList.toArray();
    }
  //TODO
  // verify the veteran vacinity at the begining of each turn;
  // Flip the markes in ajacentSpace
}
