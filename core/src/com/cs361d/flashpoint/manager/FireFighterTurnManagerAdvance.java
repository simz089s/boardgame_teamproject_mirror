package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.FireFighter;
import com.cs361d.flashpoint.model.FireFighterSpecialities.*;
import com.cs361d.flashpoint.screen.BoardScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireFighterTurnManagerAdvance extends FireFighterTurnManager {
  protected final ArrayList<FireFighterAdvanceSpecialities> FREESPECIALITIES = new ArrayList<FireFighterAdvanceSpecialities>(9);

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
      FIREFIGHTERS.addLast(FireFighterAdvanced.createFireFighter(f.getColor(),speciality));
      return true;
    } else {
      BoardScreen.createDialog("The speciality Asked is not available", "");
      return false;
    }
  }

  public void crewChange(FireFighterAdvanceSpecialities speciality) {
    FireFighterAdvanced f = getCurrentFireFighter();
    if (FREESPECIALITIES.contains(speciality) && f.getTile().hasFireTruck() && f.crewChangeAP()) {
      FREESPECIALITIES.remove(speciality);
      FREESPECIALITIES.add(f.getSpeciality());
      FireFighterAdvanced newF = FireFighterAdvanced.createFireFighter(f.getColor(), speciality);
      newF.setActionPoint(f.getActionPointsLeft());
      FIREFIGHTERS.removeFirst();
      FIREFIGHTERS.addFirst(newF);
    } else {
      BoardScreen.createDialog("Cannot getSpeciality", "Either you are not located on the fire truck" +
              " or the speciality is not available or you have less than 2 ap");
    }
  }

  @Override
  public FireFighterAdvanced getCurrentFireFighter() {
    return (FireFighterAdvanced) super.getCurrentFireFighter();
  }
}
