package com.cs361d.flashpoint.Game.Logic;

import com.cs361d.flashpoint.Game.GUI.*;
import com.cs361d.flashpoint.Game.Logic.Player.Players;
import com.cs361d.flashpoint.Utilities.BoardPosition;
import com.cs361d.flashpoint.GameScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class ActivePawn {
	private static final float pawnMovementSpeed = 0.15f;
	
	private final GameScreen screen;
	private final Board board;
	private DrawPawn selected;
	private DrawCell selectedCell;
	
	public ActivePawn(GameScreen scr, Board brd) {
		screen = scr;
		board = brd;
	}
	
	public void select(DrawPawn pawn) {
		selected = pawn;
		selectedCell = screen.getCell(selected.getBoardPosition());
		selectedCell.setColor(Color.GREEN);
	}
	
	public void unselect() {
		if(selectedCell != null) selectedCell.setColor(Color.WHITE);
		selected = null;
		selectedCell = null;
	}
	
	public boolean canMove(DrawCell cell) {
		BoardPosition distance = BoardPosition.getDistance(cell.getBoardPosition(), selected.getBoardPosition());
		
		if(selected.getType() == DrawPawn.PawnType.STANDARD) return (distance.x == 1) && (distance.y == 1);
		else {
			if(distance.x == distance.y) return canMoveKing(selected.getBoardPosition(), cell);
		}
		return false;
	}
	
	private boolean canMoveKing(BoardPosition checkPos, DrawCell destination) {
		if(destination.getBoardPosition().isEqual(checkPos)) return true;
		else if(board.getValue(checkPos) != 1 && !selected.getBoardPosition().isEqual(checkPos)) return false;
		
		BoardPosition dir = BoardPosition.getDirection(destination.getBoardPosition(), selected.getBoardPosition());
		BoardPosition newCheckPos = new BoardPosition();
		
		if(dir.x < 0 && dir.y < 0) newCheckPos =  new BoardPosition(checkPos.x - 1, checkPos.y - 1); //Top left
		else if(dir.x > 0 && dir.y < 0) newCheckPos =  new BoardPosition(checkPos.x + 1, checkPos.y - 1); //Top right
		else if(dir.x < 0 && dir.y > 0) newCheckPos =  new BoardPosition(checkPos.x - 1, checkPos.y + 1); //Bottom left
		else if(dir.x > 0 && dir.y > 0) newCheckPos =  new BoardPosition(checkPos.x + 1, checkPos.y + 1); //Bottom right
		
		return canMoveKing(newCheckPos, destination);
	}
	
	public void move(Vector2 ScreenPos, BoardPosition boardPos) {
		board.setValue(selected.getBoardPosition(), 1);
		board.setValue(boardPos, selected.getPlayerInt());
		selected.setBoardPosition(boardPos);
		selected.addAction(Actions.moveTo(ScreenPos.x, ScreenPos.y, pawnMovementSpeed));
		if(canChangeToKing(boardPos.y)) selected.setAsKing();
		screen.countPawns();
	}
	
	private boolean canChangeToKing(int posY) {
		if(selected.getPlayer() == Players.BRIGHT && posY == board.getHeight() - 1) return true;
		else if(selected.getPlayer() == Players.DARK && posY == 0) return true;
		else return false;
	}
	
	public boolean canCapturePawn(BoardPosition cellPos) {
		BoardPosition direction = BoardPosition.getDirection(cellPos, selected.getBoardPosition());	
		int ChangePosX, changePosY;
		
		if(direction.x > 0) ChangePosX = -1;
		else ChangePosX = 1;
		
		if(direction.y > 0) changePosY = -1;
		else changePosY = 1;
		
		BoardPosition pawnToCapture = new BoardPosition(cellPos.x + ChangePosX, cellPos.y + changePosY);
		BoardPosition cellToMove = new BoardPosition(cellPos.x + ChangePosX*2, cellPos.y + changePosY*2);
		
		if(canCapture(pawnToCapture, cellToMove)) {
			BoardPosition dist = BoardPosition.getDistance(cellPos, selected.getBoardPosition());
			
			if(selected.getType() == DrawPawn.PawnType.STANDARD && dist.x == 2 && dist.y == 2) return true;
			else if(selected.getType() == DrawPawn.PawnType.KING && dist.x == dist.y) return true;
		}		
		return false;
	}
		
	public boolean canCapture(BoardPosition pawnToCapture, BoardPosition cellToMove) {
		if(board.getValue(pawnToCapture) == 0 || board.getValue(pawnToCapture) == 1) return false;
		
		Players activePlayer = selected.getPlayer();
		Players pawnToCapturePlayer = board.getPawnPlayer(pawnToCapture);
		
		if(activePlayer == pawnToCapturePlayer) return false;
		else {
			if(board.getValue(cellToMove) == 1 || board.getValue(cellToMove) == selected.getPlayerInt()) return true;
			else return false;
		}
	}

	public void captureAndMove(Vector2 ScreenPos, BoardPosition boardPos) {
		BoardPosition direction = BoardPosition.getDirection(boardPos, selected.getBoardPosition());
		int toRemoveX, toRemoveY;
		
		if(direction.x > 0) toRemoveX = boardPos.x - 1; 
		else toRemoveX = boardPos.x + 1; 
		
		if(direction.y > 0) toRemoveY = boardPos.y - 1;
		else toRemoveY = boardPos.y + 1;
		
		removePawn(new BoardPosition(toRemoveX, toRemoveY));
		move(ScreenPos, boardPos);
	}
	
	public boolean anyCapturesLeft() {
		BoardPosition pos = selected.getBoardPosition();
		
		if(canCapture(new BoardPosition(pos.x - 1, pos.y - 1), new BoardPosition(pos.x - 2, pos.y - 2))) return true; //Top left
		if(canCapture(new BoardPosition(pos.x + 1, pos.y - 1), new BoardPosition(pos.x + 2, pos.y - 2))) return true; //Top right
		if(canCapture(new BoardPosition(pos.x - 1, pos.y + 1), new BoardPosition(pos.x - 2, pos.y + 2))) return true; //Bottom left
		if(canCapture(new BoardPosition(pos.x + 1, pos.y + 1), new BoardPosition(pos.x + 2, pos.y + 2))) return true; //Bottom right
		return false;
	}
	
	public void removePawn(BoardPosition pos) {
		board.setValue(pos.x, pos.y, 1);
		screen.removePawn(pos.x, pos.y);
	}
	
	public DrawPawn get() { return selected; }
	
	public boolean isSelected() {
		if(selected == null) return false;
		else return true;
	}
}
