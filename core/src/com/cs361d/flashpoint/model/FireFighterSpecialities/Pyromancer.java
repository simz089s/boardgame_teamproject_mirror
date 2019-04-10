package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.Direction;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.model.BoardElements.Tile;
import com.cs361d.flashpoint.screen.Actions;

import java.util.List;

import static com.cs361d.flashpoint.model.FireFighterSpecialities.FireFighterAdvanceSpecialties.PYROMANCER;

public class Pyromancer extends FireFighterAdvanced {

    protected Pyromancer(FireFighterColor color) {
        super(color, 4, 3, PYROMANCER, true);
        maxSpecialAp = 3;
    }

    public boolean spreadFireAP() {
        if (specialActionPoints > 0) {
            firstMoveDone();
            specialActionPoints--;
            return true;
        }
        else if (actionPoints > 0) {
            firstMoveDone();
            actionPoints--;
            return true;
        }
        return false;
    }

    @Override
    public boolean moveAP(Direction d) {
        Tile t = currentTile.getAdjacentTile(d);
        if (t.hasFire()) {
            return true;
        }
        return super.moveAP(d);
    }

    @Override
    public boolean moveWithVictimAP() {
        return false;
    }

    @Override
    public boolean extinguishAP() {
        return false;
    }

    @Override
    public boolean fireTheDeckGunAp() {
        return false;
    }

    @Override
    public List<Actions> getActions() {
        List<Actions> actions = super.getActions();
        actions.remove(Actions.MOVE_WITH_VICTIM);
        actions.remove(Actions.EXTINGUISH);
        actions.remove(Actions.FIRE_DECK_GUN);
        actions.remove(Actions.SAVE);
        actions.add(actions.size()-2,Actions.SPREAD_FIRE);
        if (!isFirstMove()) {
            actions.remove(Actions.CREW_CHANGE);
        }
        actionsFilter(actions);
        return actions;
    }
}
