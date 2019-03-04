package com.cs361d.flashpoint.model.BoardElements;

public class Victim extends AbstractVictim
{
    private boolean isRevealed;
    private boolean isCured;
    private final boolean IS_FALSE_ALARM;
    public Victim(boolean isFalseAlarm)
      {
        this.isCured = false;
        this.isRevealed = false;
        this.IS_FALSE_ALARM = isFalseAlarm;
    }


    public boolean isRevealed()
    {

        return isRevealed;
    }

    public boolean reveal()
    {
        if (this.isRevealed) {
            return false;
    } else {
      this.isRevealed = true;
      return true;
        }
    }

    public boolean isCured()
    {
        return isCured;
    }

    public boolean cure()
    {
        if (isCured) {
            return false;
        }
        else {
        this.isCured = true;
        return true;
        }
    }

    public boolean isFalseAlarm()
    {
        return IS_FALSE_ALARM;
    }

    public boolean isNull() {
        return false;
    }
}
