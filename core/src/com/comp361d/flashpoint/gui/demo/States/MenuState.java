package com.comp361d.flashpoint.gui.demo.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.comp361d.flashpoint.gui.demo.GameScreens.FlashPointGame;

public class MenuState extends State {
  private Texture backGround;
  public MenuState(GameStateManager gsm) {
    super(gsm);
    backGround = new Texture("map/flmap.png");
  }

  @Override
  public void handleInput() {
    if (Gdx.input.justTouched()) {
      gsm.set(new PlayState(gsm));
      //dispose();
    }
  }

  @Override
  public void update(float dt) {
    handleInput();
  }

  @Override
  public void render(SpriteBatch sb) {
    sb.begin();
    sb.draw(backGround, 0,0, FlashPointGame.WIDTH, FlashPointGame.HEIGHT);
    sb.end();
  }

  @Override
  public void dispose() {
    backGround.dispose();
  }
}
