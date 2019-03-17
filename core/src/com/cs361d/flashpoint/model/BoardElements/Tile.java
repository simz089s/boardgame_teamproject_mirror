package com.cs361d.flashpoint.model.BoardElements;

import com.cs361d.flashpoint.manager.BoardManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tile {

    private final ArrayList<FireFighter> FIREFIGHTERS = new ArrayList<FireFighter>();
    private FireStatus fireStatus;
    private AbstractVictim victim;
    private CarrierStatus carrierStatus;
    private final Map<Direction, Obstacle> OBSTACLES = new HashMap<Direction, Obstacle>();
    private final int I;
    private final int J;
    private boolean hasHazmat = false;
    private boolean hasHotSpot = false;
    public Tile(FireStatus fireStatus, int i, int j){
        this.fireStatus = fireStatus;
        this.victim = NullVictim.getInstance();
        this.I = i;
        this.J = j;
        this.carrierStatus = CarrierStatus.EMPTY;
    }
    public String getCarrierStatusString() {
        return this.carrierStatus.toString();
    }
    public boolean hasHazmat() {
        return this.hasHazmat;
    }

    public boolean hasHotSpot()
    {
        return this.hasHazmat;
    }

    public void setHasHazmat(boolean value) {
        this.hasHazmat = value;
    }

    public void addHotSpot() {
        this.hasHotSpot = true;
    }


    public void setCarrierStatus(CarrierStatus s) {
        carrierStatus = s;
    }


    public int getI() {
        return I;
    }

    public int getJ() {
        return J;
    }

    public void addObstacle(Direction d, Obstacle o) {
        if (OBSTACLES.containsKey(d)) {
            throw new UnsupportedOperationException("You cannot overwrite an Obstacle that already exits");
        }
        else {
            OBSTACLES.put(d, o);
        }
    }

    public Obstacle getObstacle(Direction d) {
        return OBSTACLES.get(d);
    }

    // Return if there is an obstacle in the desired direction
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

            case NODIRECTION:
                return false;
            default:
                throw new IllegalArgumentException(d + " is an invalid Direction.");
        }
        if (o.isDestroyed()) {
            return false;
        }
        if (o.isDoor() && o.isOpen()) {
            return false;
        }
        return true;
    }

    public List<FireFighter> getFirefighters() {
        return FIREFIGHTERS;
    }

    public void addFirefighter(FireFighter firefighter) {

        if (FIREFIGHTERS.contains(firefighter)) {
            throw new IllegalArgumentException("This tile already contains that fireFighter");
        }
        FIREFIGHTERS.add(firefighter);
    }

    public void removeFirefighter(FireFighter firefighter) {
        FIREFIGHTERS.remove(firefighter);
    }

    public void removeAllFireFighters() {
        FIREFIGHTERS.clear();
    }

    public boolean hasPointOfInterest() {
        return !victim.isNull();
    }

    public AbstractVictim getVictim() {
        return victim;
    }

    /*
    verifies that the Victim is not a FALSE_ALARM
     */
    public boolean hasRealVictim() {
        if (victim.isNull()) {
            return false;
        }
        return !victim.isFalseAlarm();
    }

    // return the tile adjacent in the direction wished
    public Tile getAdjacentTile(Direction d) {
        return BoardManager.getInstance().getAdjacentTile(this, d);
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

    public List<Tile> getClosestAmbulanceTile() {
        return BoardManager.getInstance().getClosestAmbulanceTile(this.I, this.J);
    }

    public boolean canContainAmbulance() {
        return this.carrierStatus == CarrierStatus.CANHAVEAMBULANCE;
    }
    public boolean hasAmbulance() {
        return this.carrierStatus == CarrierStatus.HASAMBULANCE;
    }
    public boolean canContainFireTruck() {return this.carrierStatus == CarrierStatus.CANHAVEFIRETRUCK;}
    public boolean hasFireTruck() {return this.carrierStatus == CarrierStatus.HASFIRETRUCK;}

    public boolean hasFireFighters() {
        return FIREFIGHTERS.size() > 0;
    }

    public String getFireStatusString() {
        return this.fireStatus.toString();
    }
}
