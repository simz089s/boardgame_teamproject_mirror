package com.comp361d.flashpoint.gui.demo.States;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class State {
  protected OrthographicCamera cam;
  protected Vector2 mouse;
  protected GameStateManager gsm;

  protected State(GameStateManager gsm) {
    this.cam = new OrthographicCamera();
    this.mouse = new Vector2();
    this.gsm = gsm;
  }

  public abstract void handleInput();
  public abstract void update(float dt);
  public abstract void render(SpriteBatch sb);
  public abstract void dispose();
}
