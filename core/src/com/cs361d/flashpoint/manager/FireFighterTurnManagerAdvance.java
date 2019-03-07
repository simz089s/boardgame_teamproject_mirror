package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.FireFighter;
import com.cs361d.flashpoint.model.BoardElements.FireFighterAdvanced;

public class FireFighterTurnManagerAdvance extends FireFighterTurnManager {
    protected FireFighterTurnManagerAdvance() {
        super();
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
}
