package com.cs361d.flashpoint.Game.Logic;

import com.cs361d.flashpoint.Game.Logic.Player.Players;
import com.cs361d.flashpoint.Utilities.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Board {
	private int board[][];
	private int boardWidth, boardHeight;
	
	public Board() { }
	
	public void loadFromFile(String boardName) {
//		FileHandle file = Gdx.files.internal("Boards/" + boardName);
//		String lines[] = file.readString().split(" #END");
//		for(int i = 0; i < lines.length; ++i) lines[i] = lines[i].replaceAll("\\s", ""); //Remove white chars
//
//		boardHeight = lines.length;
//		boardWidth = lines[0].length();
//
//		board = new int[boardWidth][boardHeight];
//
//		for(int y = 0; y < boardHeight; ++y) {
//			for(int x = 0; x < boardWidth; ++x) {
//				board[x][y] = Character.getNumericValue(lines[y].charAt(x));
//			}
//		}
		boardHeight = 8;
		boardWidth = 10;
		board = new int[boardWidth][boardHeight];
	}
	
	public int getWidth() { return boardWidth; }
	public int getHeight() { return boardHeight; }
	public int getValue(BoardPosition pos) { return getValue(pos.x, pos.y); }
	public Players getPawnPlayer(BoardPosition pos) { return getPawnPlayer(pos.x, pos.y); }
	
	public void setValue(BoardPosition pos, int val) { setValue(pos.x, pos.y, val); }
	
	public int getValue(int x, int y) { 
		if(x >= 0 && x < getWidth() && y >= 0 && y < getHeight()) return board[x][y];
		else return -1;
	}
	
	public Players getPawnPlayer(int x, int y) {
		if(getValue(x,y) == 2 || getValue(x,y) == 4) return Players.BRIGHT;
		else return Players.DARK;
	}

	public void setValue(int x, int y, int val) {
		if(getValue(x, y) != -1) board[x][y] = val;
	}
	
	public int countPawns(int val) {
		int counter = 0;
		
		for(int y = 0; y < boardHeight; ++y){
			for(int x = 0; x < boardWidth; ++x){
				if(board[x][y] == val) ++counter;
			}
		}
		
		return counter;
	}
}
