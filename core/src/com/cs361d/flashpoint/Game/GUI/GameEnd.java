package com.cs361d.flashpoint.Game.GUI;

import com.cs361d.flashpoint.FlashPointGame;
//import com.cs361d.flashpoint.FlashPointGame.ScreenMode;
import com.cs361d.flashpoint.Utilities.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class GameEnd extends Table {
	private final float tableWidth = 300;
	private final float tableHeight = 200;
	private final float buttonWidth = 130;
	private final float buttonHeight = 60;
	private final int titleSize = 25;
	private final int winnerSize = 20;
	private final int buttonSize = 16;
	
	private LabelStyle styleTitle, styleWinner;
	private Label title, winner;
	private TextButton backToMenuButton, newGameButton;
	private FlashPointGame game;

	public GameEnd(FlashPointGame game, String winnerName) {
		this.game = game;
		this.setSize(tableWidth, tableHeight);
		setPosition(FlashPointGame.WIDTH/2 - tableWidth/2, FlashPointGame.HEIGHT/2 - tableHeight/2);
		
		styleTitle = new LabelStyle();
		styleWinner = new LabelStyle();
		
		this.setBackground(TextureLoader.getDrawable((int)tableWidth, (int)tableHeight, Color.DARK_GRAY));
		addTitle();
		setWinner(winnerName);
		addButtons();
//		addButtonsListeners();
	}

	private void addTitle() {
		styleTitle.font = Font.get(titleSize);
		styleTitle.fontColor = Color.WHITE;
		
		title = new Label("Game finished", styleTitle);
		this.add(title).expand().colspan(2).row();
	}
	
	private void setWinner(String winnerName) {
		styleWinner.font = Font.get(winnerSize);
		styleWinner.fontColor = Color.WHITE;
		
		winner = new Label(String.format("%s won!", winnerName), styleWinner);
		this.add(winner).expand().colspan(2).row();
	}
	
	private void addButtons() {
		TextButtonStyle style = new TextButtonStyle(TextureLoader.getDrawable("buttonStandard"), TextureLoader.getDrawable("buttonPressed"), TextureLoader.getDrawable("buttonStandard"), Font.get(buttonSize));

		backToMenuButton = new TextButton("Main menu", style);
		newGameButton = new TextButton("Play again", style);

		this.add(backToMenuButton).size(buttonWidth, buttonHeight).expand();
		this.add(newGameButton).size(buttonWidth, buttonHeight).expand();
	}
	
//	private void addButtonsListeners() {
//		backToMenuButton.addListener(new ChangeListener() {
//			@Override public void changed (ChangeEvent event, Actor actor) {
//	           game.setScreen(ScreenMode.MAIN_MENU);
//	        }
//	    });
//
//		newGameButton.addListener(new ChangeListener() {
//			@Override public void changed (ChangeEvent event, Actor actor) {
//				game.setScreen(ScreenMode.GAME);
//	        }
//	    });
//	}
}
