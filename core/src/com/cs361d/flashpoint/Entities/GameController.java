package com.cs361d.flashpoint.Entities;

public class GameController
{
    private String gameName;
    private GameMode gameMode;
    private GamePhase currentPhase;
    private int numPlayers;
    private int numWallsDestroyed;
    private int numVictimsSaved;
    private int numVictimsDead;

    public GameController(String gameName, GameMode gameMode, GamePhase currentPhase, int numPlayers,
                          int numWallsDestroyed, int numVictimsSaved, int numVictimsDead)
    {
        this.gameName = gameName;
        this.gameMode = gameMode;
        this.currentPhase = currentPhase;
        this.numPlayers = numPlayers;
        this.numWallsDestroyed = numWallsDestroyed;
        this.numVictimsSaved = numVictimsSaved;
        this.numVictimsDead = numVictimsDead;
    }
}
