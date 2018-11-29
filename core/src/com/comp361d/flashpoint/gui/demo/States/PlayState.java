package com.comp361d.flashpoint.gui.demo.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.comp361d.flashpoint.gui.demo.Sprites.Player;

public class PlayState extends State {
    private Player player;
    Texture backGround = new Texture("map/flmap.png");

    public PlayState(GameStateManager gsm) {
        super(gsm);
        player = new Player(-1, -1);
        player.getSprite().setScale(0.5f);
    }

    @Override
    public void handleInput() {
        if (Gdx.input.justTouched()) {
            player.move();
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(backGround, 0, 0);
        // sb.draw(player.getSprite(), player.getPosition().x, player.getPosition().y);
        player.getSprite().setPosition(player.getPosition().x, player.getPosition().y);
        player.getSprite().draw(sb);
        sb.end();
    }

    @Override
    public void dispose() {}
}
