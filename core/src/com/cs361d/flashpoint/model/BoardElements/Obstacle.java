package com.cs361d.flashpoint.model.BoardElements;

public class Obstacle {
  private int health;
  private boolean isDoor;
  private boolean isOpen;
  private final static int MAX_HEALTH = 2;

  // Used when creating obstacles that are not doors
  public Obstacle(int health) {
      if (health < -1 || health > MAX_HEALTH) {
          throw new IllegalArgumentException("Health cannot be " + health);
      }
      else {
          this.health = health;
          isDoor = false;
      }
  }
  public Obstacle(int health, boolean isDoor) {
      if (health < -1 || health > MAX_HEALTH) {
          throw new IllegalArgumentException("Health cannot be " + health);
      }
      else {
          this.health = health;
      }
      this.isDoor = isDoor;
  }

  public boolean isDestroyed() {
    return this.health == 0;
  }

  public boolean damage() {
    if (isDestroyed()) {
      return false;
    } else {
      this.health--;
      return true;
    }
  }

  // obstacle with -1 health means there is no obstacle
  public boolean isNull() {
      return this.health == -1;
  }

    public void setHealth(int health) {
        this.health = health;
    }

    public void openDoor() {
        if (isOpen || !isDoor) {
            throw new IllegalAccessError("You cannot open a wall or open an open door");
        }
        isOpen = true;

    }

    public void closeDoor() {
        if (!isOpen || !isDoor) {
            throw new IllegalAccessError("You cannot open a wall");

        }
        isOpen = false;
    }

    public boolean isOpen() {
      return isOpen;
    }

    public void makeDoor() {
      isDoor = true;
    }

    public void setOpen(boolean isOpen) {
      this.isOpen = isOpen;
    }
}
