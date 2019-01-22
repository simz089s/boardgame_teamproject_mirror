package com.cs361d.flashpoint.model.BoardElements;

public abstract class AbstractVictim {

    public boolean isRevealed()
    {

        return false;
    }

    public boolean reveal()
    {
        return false;
    }

    public boolean isCured()
    {
        return false;
    }

    public boolean cure()
    {
       return false;
    }

    public boolean isFalseAlarm() { return true; }

    public boolean isNull() {return true;}
}
