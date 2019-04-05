package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.Direction;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.model.BoardElements.Tile;
import com.cs361d.flashpoint.screen.Actions;
import com.cs361d.flashpoint.screen.BoardDialog;
import com.cs361d.flashpoint.screen.BoardGameInfoLabel;
import com.cs361d.flashpoint.screen.BoardScreen;

import java.util.List;

public class RescueDog extends FireFighterAdvanced {

    public RescueDog(FireFighterColor color) {
        super(color, 12, 0, FireFighterAdvanceSpecialities.RESCUE_DOG, true);
        this.maxActionPoint = 18;
        this.actionsPointPerTurn = 12;
    }

    @Override
    public void resetActionPoints() {
        if (this.actionPoints > 6) {
            this.actionPoints = 6;
        }
        super.resetActionPoints();
    }

    @Override
    public boolean moveAP(Direction d) {
        if(currentTile.getAdjacentTile(d).hasFire()) {
            BoardScreen.getDialog().drawDialog("Action rejected", "The rescue dog cannot go on a space with fire");
            return false;
        }
        else if (currentTile.hasObstacle(d)) {
            if (actionPoints < 2) {
                return false;
            }
            else {
                actionPoints -= 2;
                firstMoveDone();
                return true;
            }
        }
        else {
            if (actionPoints < 1) {
                return false;
            }
            else {
                firstMoveDone();
                actionPoints--;
                return true;
            }
        }
    }

    @Override
    public boolean moveWithVictimAP() {
        if (currentTile.getVictim().isCured()) {
            return super.moveWithVictimAP();
        }
        else {
            if (actionPoints < 4) {
                return false;
            }
            else {
                actionPoints -= 4;
                firstMoveDone();
                return true;
            }
        }
    }

    @Override
    public boolean extinguishAP() {
        return false;
    }

    @Override
    public boolean openCloseDoorAP() {
        return false;
    }

    @Override
    public boolean chopAP() {
        return false;
    }

    @Override
    public boolean driveAp() {
        return false;
    }

    @Override
    public boolean dodgeAp() {
        return false;
    }

    @Override
    public List<Actions> getActions() {
        List<Actions> actions = Actions.rescueDogActions();
        if (!isFirstMove()) {
            actions.remove(Actions.CREW_CHANGE);
        }
        actionsFilter(actions);
        return actions;
    }
}
