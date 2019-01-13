package com.cs361d.flashpoint.Game.Logic;

import com.cs361d.flashpoint.GameScreen;

public class Player {
	public enum Players {BRIGHT, DARK}
	
	private GameScreen screen;
	private Players active;
	
	public Player(GameScreen scr) { screen = scr; }
	
	public Players getActive() { return active; }
	public void set(Players player) { active = player; }
	
	public void change() {
		if(active == Players.BRIGHT) active = Players.DARK;
		else active = Players.BRIGHT;
		if(screen != null) screen.updateActivePlayer();
	}
}
