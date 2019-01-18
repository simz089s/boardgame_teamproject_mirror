package com.cs361d.flashpoint.model.BoardElements;

public class Victim
{
    private boolean isRevealed;
    private boolean isCured;
    private boolean isRealVictim;

    public Victim(boolean isRevealed, boolean isCured, boolean isRealVictim)
    {
        this.isRevealed = isRevealed;
        this.isCured = isCured;
        this.isRealVictim = isRealVictim;
    }


    public boolean isRevealed()
    {
        return isRevealed;
    }

    public void setRevealed(boolean revealed)
    {
        isRevealed = revealed;
    }

    public boolean isCured()
    {
        return isCured;
    }

    public void setCured(boolean cured)
    {
        isCured = cured;
    }

    public boolean isRealVictim()
    {
        return isRealVictim;
    }

    public void setRealVictim(boolean realVictim)
    {
        isRealVictim = realVictim;
    }
}
