package com.cs361d.flashpoint.model.BoardElements;

import com.cs361d.flashpoint.model.Board;

import java.util.ArrayList;
import java.util.HashMap;

public class Tile {

    private final ArrayList<FireFighter> FIREFIGHTERS = new ArrayList<FireFighter>();
    private FireStatus fireStatus;
    private AbstractVictim victim;
    private boolean has_hotSpot;
    private final HashMap<Direction, Obstacle> OBSTACLES = new HashMap<Direction, Obstacle>();
    private final int I;
    private final int J;

    public Tile(FireStatus fireStatus, boolean has_hotSpot, int i, int j){
        this.fireStatus = fireStatus;
        this.victim = NullVictim.getInstance();
        this.has_hotSpot = has_hotSpot;
        this.I = i;
        this.J = j;
    }

    public int getI() {
        return I;
    }

    public int getJ() {
        return J;
    }

    public void addObstacle(Direction d, Obstacle o) {
        if (OBSTACLES.containsKey(d)) {
            throw new UnsupportedOperationException("You cannot overwrite a Obstacle that already exits");
        }
        else {
            OBSTACLES.put(d, o);
        }
    }

    public Obstacle getObstacle(Direction d) {
        return OBSTACLES.get(d);
    }

    public boolean hasObstacle(Direction d) {
        Obstacle o;
        switch (d) {
            case RIGHT:
                o = OBSTACLES.get(Direction.RIGHT);
                break;

            case LEFT:
                o = OBSTACLES.get(Direction.LEFT);
                break;

            case TOP:
                o = OBSTACLES.get(Direction.TOP);
                break;

            case BOTTOM:
                o = OBSTACLES.get(Direction.BOTTOM);
                break;

            default:
                throw new IllegalArgumentException();
        }
        if (o.isDestroyed()) {
            return false;
        }
        if (o.isDoor() && o.isOpen()) {
            return false;
        }
        return true;
    }

    public ArrayList<FireFighter> getFirefighters() {
        return FIREFIGHTERS;
    }

    public void addFirefighter(FireFighter firefighter) {

        FIREFIGHTERS.add(firefighter);
    }

    public void removeFirefighter(FireFighter firefighter) {

        FIREFIGHTERS.remove(firefighter);
    }

    public boolean containsPointOfInterest() {
        return !victim.isNull();
    }
    public AbstractVictim getVictim() {
        return victim;
    }

    /*
    verifies that the Victim is not a FALSE_ALARM
     */
    public boolean containsVictim() {
        if (victim.isNull()) {
            return false;
        }
        return !victim.isFalseAlarm();
    }

    public Tile getAdjacentTile(Direction d) {
        return Board.getInstance().getAdjacentTile(this, d);
    }
    public void setVictim(AbstractVictim victim) {
        this.victim = victim;
    }

    /*
    Makes the victim null
     */
    public void setNullVictim() {
        this.victim = NullVictim.getInstance();
    }

    public void setFireStatus(FireStatus f) {
        this.fireStatus = f;
    }

    public boolean hasFire() {
        return this.fireStatus == FireStatus.FIRE;
    }

    public boolean hasSmoke() {
        return this.fireStatus == FireStatus.SMOKE;
    }

    public boolean hasNoFireAndNoSmoke() {
        return this.fireStatus == FireStatus.EMPTY;
    }
}
