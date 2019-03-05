package com.cs361d.flashpoint.manager;

public class CreateNewGameManager {

    public static void createNewGame(String gameName, int numPlayers, MapKind map, Difficulty diff) {
        switch (diff) {
            case FAMILLY:
                createFamilyGame(gameName, numPlayers, map);
                break;

        }
    }

    private static void createFamilyGame(String gameName, int numPlayer, MapKind map) {
        BoardManager.getInstance().reset();
        BoardManager.getInstance().setGameName(gameName);
        switch (map) {
            case ORIGINAL1:
                DBHandler.createBoardDBFamilyVersion();
                break;
            default:
        }
        BoardManager.getInstance().setFireFighterNumber(numPlayer);
    }
}

