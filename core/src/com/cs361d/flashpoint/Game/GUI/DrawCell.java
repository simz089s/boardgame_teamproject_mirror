package com.cs361d.flashpoint.Game.GUI;

import com.cs361d.flashpoint.Game.Logic.ActivePawn;
import com.cs361d.flashpoint.Game.Logic.Player;
import com.cs361d.flashpoint.Utilities.BoardPosition;
import com.cs361d.flashpoint.Utilities.TextureLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class DrawCell extends Image {
	private final BoardPosition boardPos;
	private final Player player;
	private final ActivePawn activePawn;
	
	public DrawCell(Player player, ActivePawn activePawn, int cellType, int posX, int posY) {
		boardPos = new BoardPosition(posX, posY);
		this.player = player;
		this.activePawn = activePawn;
		
//		if(cellType == 0) this.setDrawable(TextureLoader.getDrawable("boardBright"));
//		else {
//			this.setDrawable(TextureLoader.getDrawable("boardDark"));
//
//			this.addListener(new InputListener() {
//		        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) { return touched(); }
//			});
//		}

		this.setDrawable(TextureLoader.getDrawable("boardBright"));
		this.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) { return touched(); }
		});
	}
	
	private boolean touched() {
		if(activePawn.isSelected()) {
//			if(activePawn.canMove(this)) {
//				System.out.println("Moved pawn to position: " + boardPos);
//				activePawn.move(getPosition(), boardPos);
//				activePawn.unselect();
//				player.change();
//			}

			System.out.println("Moved pawn to position: " + boardPos);
			activePawn.move(getPosition(), boardPos);
			activePawn.unselect();

//			else if(activePawn.canCapturePawn(boardPos)) {
//				System.out.println("Moved pawn to position: " + boardPos + " - PAWN CAPTURED");
//				activePawn.captureAndMove(getPosition(), boardPos);
//
//				if(!activePawn.anyCapturesLeft()) {
//					activePawn.unselect();
//					player.change();
//				}
//			}
		}	
		return false;
	}
	
	public Vector2 getPosition() { return this.localToParentCoordinates(new Vector2(0,0)); }	
	public BoardPosition getBoardPosition() { return boardPos; }
}
