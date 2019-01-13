package com.cs361d.flashpoint;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FlashPointGame extends Game
{
	public enum ScreenMode {MAIN_MENU, NEW_GAME, GAME}
	public static String GAME_TITLE = "Flash Point: Fire Rescue";
	public static int WIDTH = 800;
	public static int HEIGHT = 600;

//	SpriteBatch batch;
//	Texture img;

	private GameScreen game;
	private GameScreen menu;
	private GameScreen newGame;
	@Override
	public void create () {
//		batch = new SpriteBatch();
//		img = new Texture("badlogic.jpg");
		game = new GameScreen(this);
		this.setScreen(game);
		this.setScreen(ScreenMode.GAME);
	}

	public void setScreen(ScreenMode mode) {
		if(mode == ScreenMode.MAIN_MENU) this.setScreen(menu);
		else if(mode == ScreenMode.GAME) {
			this.setScreen(game);
			//game.newGame(newGame.getPlayerNames(), newGame.getBoardName());
			game.newGame(new String[]{"Player1","Player2"}, "Board Name");
		}
		else if(mode == ScreenMode.NEW_GAME) this.setScreen(newGame);
	}

//	@Override
//	public void render () {
//		Gdx.gl.glClearColor(1, 0, 0, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		batch.begin();
//		batch.draw(img, 0, 0);
//		batch.end();
//	}

//	@Override
//	public void dispose () {
//		batch.dispose();
//		img.dispose();
//	}
}
