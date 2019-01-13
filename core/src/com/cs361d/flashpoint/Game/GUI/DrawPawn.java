package com.cs361d.flashpoint.Game.GUI;

import com.cs361d.flashpoint.Game.Logic.ActivePawn;
import com.cs361d.flashpoint.Game.Logic.Player;
import com.cs361d.flashpoint.Game.Logic.Player.Players;
import com.cs361d.flashpoint.Utilities.BoardPosition;
import com.cs361d.flashpoint.Utilities.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class DrawPawn extends Image {
	public enum PawnType {STANDARD, KING}
	
	private BoardPosition pos;
	private Players player;
	private PawnType type;
	private final Player activePlayer;
	private final ActivePawn activePawn;
	
	public DrawPawn(Player player, ActivePawn activePawn, int pawnType, int posX, int posY) {
		activePlayer = player;
		this.activePawn = activePawn;
		pos = new BoardPosition(posX, posY);
		
		if(pawnType == 2 || pawnType == 4) {
			this.player = Players.BRIGHT;
			
			if(pawnType == 2) {
				this.setDrawable(TextureLoader.getDrawable("pawnBright"));
				type = PawnType.STANDARD;
			}
			else setAsKing();
		}
		else if(pawnType == 3 || pawnType == 5) {
			this.player = Players.DARK;
			
			if(pawnType == 3) {
				this.setDrawable(TextureLoader.getDrawable("pawnDark"));
				type = PawnType.STANDARD;
			}
			else setAsKing();
		}
		
		this.addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) { return touched(); }
		});
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if(this.getActions().size > 0) this.act(Gdx.graphics.getDeltaTime());
	}
		
	private boolean touched() {
		if(activePlayer.getActive() == player) {
			System.out.println("Pressed pawn on position: " + pos);
			if(activePawn.get() == this) activePawn.unselect();
			else {
				activePawn.unselect();
				activePawn.select(this);
			}
		}
		return false;
	}
	
	public void setAsKing() {
		type = PawnType.KING;
		if(player == Players.BRIGHT) this.setDrawable(TextureLoader.getDrawable("pawnBrightKing"));
		else this.setDrawable(TextureLoader.getDrawable("pawnDarkKing"));
	}
	
	public void setBoardPosition(BoardPosition pos) { setBoardPosition(pos.x, pos.y); }
	public void setBoardPosition(int x, int y) { pos.setPosition(x, y); }
	public BoardPosition getBoardPosition() { return pos; }
	public Players getPlayer() { return player; }
	public PawnType getType() { return type; }
	
	public int getPlayerInt() {
		if(player == Players.BRIGHT) return 2;
		else return 3;
	}
}
