package com.cs361d.flashpoint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.cs361d.flashpoint.Game.GUI.*;
import com.cs361d.flashpoint.Game.GUI.DrawPawn.PawnType;
import com.cs361d.flashpoint.Game.Logic.Board;
import com.cs361d.flashpoint.Game.Logic.ActivePawn;
import com.cs361d.flashpoint.Game.Logic.Player;
import com.cs361d.flashpoint.Game.Logic.Player.Players;
import com.cs361d.flashpoint.Utilities.*;

import java.util.ArrayList;

public class GameScreen extends AbstractScreen {
    private final int maxBoardSize = 520;
    private Table boardCellContainer;
    private Board board;
    private DrawCell[][] boardCells;
    ArrayList<DrawPawn> pawns;
    private PlayerInfo playerDark, playerBright;
    private GameEnd end;
    private int boardCellSize = 65;
    private Player player;
    private ActivePawn activePawn;

    public GameScreen(FlashPointGame game) {
        super(game);
        board = new Board();
        player = new Player(this);
        activePawn = new ActivePawn(this, board);
    }

    @Override
    public void render(float delta) {
        clearScreen();
//        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) game.setScreen(FlashPointGame.ScreenMode.MAIN_MENU);

        stage.draw();
    }

    public void newGame(String[] playerNames, String boardName) {
        System.out.println("\nNew Game\n");
        for(Actor act : stage.getActors()) act.remove();
        if(pawns != null) for(DrawPawn pw : pawns) pw.remove();

        boardCellContainer = new Table();
        boardCellContainer.setFillParent(true);

        board.loadFromFile(boardName);
        countBoardCellSize();
        loadBoard();

        pawns = new ArrayList<DrawPawn>();
        stage.addActor(boardCellContainer);
        boardCellContainer.validate();

        loadPawnsGroups();
        loadPlayerInfo(playerNames);
        countPawns();
        player.set(Players.BRIGHT);
        updateActivePlayer();
    }

    public DrawCell getCell(BoardPosition pos) {
        return boardCells[pos.x][pos.y];
    }

    public void removePawn(int x, int y) {
        for(int i = 0; i < pawns.size(); ++i) {
            if(pawns.get(i).getBoardPosition().isEqual(x, y)) {
                pawns.get(i).remove();
                pawns.remove(i);
                break;
            }
        }
        countPawns();
        checkEndGame();
    }

    public void countPawns() {
        int pawnsBright = 0, pawnsBrightKings = 0, pawnsDark = 0, pawnsDarkKings = 0;

        for(DrawPawn pw : pawns) {
            if(pw.getPlayer() == Players.BRIGHT) {
                if(pw.getType() == PawnType.STANDARD) ++pawnsBright;
                else ++pawnsBrightKings;
            }
            else {
                if(pw.getType() == PawnType.STANDARD) ++pawnsDark;
                else ++pawnsDarkKings;
            }
        }

        playerBright.setValue(pawnsBright, pawnsBrightKings);
        playerDark.setValue(pawnsDark, pawnsDarkKings);
    }

    private void checkEndGame() {
        if(board.countPawns(2) == 0 && board.countPawns(4) == 0) {
            end = new GameEnd(game, playerDark.getName());
            stage.addActor(end);
        }
        else if(board.countPawns(3) == 0 && board.countPawns(5) == 0) {
            end = new GameEnd(game, playerBright.getName());
            stage.addActor(end);
        }
    }

    public void updateActivePlayer() {
        playerBright.setColor(Color.WHITE);
        playerDark.setColor(Color.WHITE);

        if(player.getActive() == Players.BRIGHT) playerBright.setColor(Color.GREEN);
        else playerDark.setColor(Color.GREEN);
    }

    private void countBoardCellSize() {
//        boardCellSize = 65;
//        while(board.getWidth() * boardCellSize > maxBoardSize) { boardCellSize -= 1; }
//        while(board.getHeight() * boardCellSize > maxBoardSize) { boardCellSize -= 1; }
//        System.out.println(boardCellSize);
        boardCellSize = 80;
    }

    public int getBoardCellSize() { return boardCellSize; }

    private void loadBoard() {
        int width = board.getWidth();
        int height = board.getHeight();
        boardCells = new DrawCell[width][height];

        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                boardCells[x][y] = new DrawCell(player, activePawn, board.getValue(x, y), x, y);
                boardCellContainer.add(boardCells[x][y]).size(boardCellSize);
            }
            boardCellContainer.row();
        }
    }

    private void loadPawnsGroups() {
//        for(int y = 0; y < board.getHeight(); ++y) {
//            for(int x = 0; x < board.getWidth(); ++x) {
//                int boardVal = board.getValue(x, y);
//
//                if(boardVal != 0 && boardVal != 1) {
//                    DrawPawn buffer = new DrawPawn(player, activePawn, boardVal, x, y);
//                    buffer.setPosition(boardCells[x][y].getPosition().x, boardCells[x][y].getPosition().y);
//                    buffer.setSize(boardCellSize, boardCellSize);
//                    pawns.add(buffer);
//                    stage.addActor(buffer);
//                }
//            }
//        }

        DrawPawn buffer = new DrawPawn(player, activePawn, 2, 5, 5);
        buffer.setPosition(boardCells[5][5].getPosition().x, boardCells[5][5].getPosition().y);
        buffer.setSize(boardCellSize, boardCellSize);
        pawns.add(buffer);
        stage.addActor(buffer);

    }

    private void loadPlayerInfo(String[] playerNames) {
        if(playerBright != null) playerBright.remove();
        playerBright = new PlayerInfo(playerNames[0], "pawnBright");
        playerBright.setPosition(670, 460);
        stage.addActor(playerBright);
        playerBright.validate();

        if(playerDark != null) playerDark.remove();
        playerDark = new PlayerInfo(playerNames[1], "pawnDark");
        playerDark.setPosition(10, 40);
        stage.addActor(playerDark);
        playerDark.validate();
    }
}

