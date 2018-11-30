package com.comp361d.flashpoint.gui.demo.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Vector2 position;
    private Texture texture;
    private Sprite sprite;

    public Player() {
        texture = new Texture("firefighter/6.png");
        sprite = new Sprite(texture);
        float x = Gdx.input.getX() - sprite.getWidth() / 2;
        float y = Gdx.graphics.getHeight() - (Gdx.input.getY() + sprite.getHeight() / 2);
        position = new Vector2(x, y);
    }

    public void update(float dt) {}

    public Vector2 getPosition() {
        return position;
    }

    public Texture getTexture() {
        return texture;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void move() {
        position.x = Gdx.input.getX() - sprite.getWidth() / 2;
        position.y = Gdx.graphics.getHeight() - (Gdx.input.getY() + sprite.getHeight() / 2);
    }
}
