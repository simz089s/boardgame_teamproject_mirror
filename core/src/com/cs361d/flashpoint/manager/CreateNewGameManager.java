package com.cs361d.flashpoint.manager;

public class CreateNewGameManager {

    public void createNewGame(int numPlayers, MapKind map, Difficulty diff) {
        switch (diff) {
            case FAMILLY:
                createFamilyGame(numPlayers, map);
                break;

        }
    }

    private void createFamilyGame(int numPlayer, MapKind map) {
        BoardManager.getInstance().reset();
        switch (map) {
            case ORIGINAL1:
                DBHandler.createBoardDBFamilyVersion();
                break;
            default:
        }
        BoardManager.getInstance().setFireFighterNumber(numPlayer);
    }
}
