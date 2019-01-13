package com.cs361d.flashpoint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public abstract class AbstractScreen implements Screen
{
    protected OrthographicCamera camera;
    protected Stage stage;
    protected SpriteBatch spriteBatch;
    protected FlashPointGame game;

    public AbstractScreen(FlashPointGame game) {
        this.game = game;
        createCamera();

        stage = new Stage(new StretchViewport(FlashPointGame.WIDTH, FlashPointGame.HEIGHT, camera));
        spriteBatch = new SpriteBatch();
    }

    private void createCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, FlashPointGame.WIDTH, FlashPointGame.HEIGHT);
        camera.update();
    }

    protected void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }
    @Override public void resize(int width, int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
        spriteBatch.dispose();
    }
}
