package com.comp361d.flashpoint.gui.demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class FlashpointGUIDemo extends ApplicationAdapter {
    static final String FLASHPOINT = "Flashpoint";

    SpriteBatch batch;

    Skin skinUI;

    Label debugLbl;

    Texture txtrBG;
    Sprite spriteBG;

    FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    BitmapFont fontCaptureIt;
    GlyphLayout gl;

    TextField fdUname;
    TextField fdPwd;
    TextButton btnLogin;

    Stage stage;

    // Image img;
    // Table tbl;
    // Stack stack;

    @Override
    public void create() {
        batch = new SpriteBatch();

        skinUI = new Skin(Gdx.files.internal("core/assets/data/uiskin.json"));

        debugLbl = new Label("", skinUI);
        debugLbl.setPosition(10, 10);
        debugLbl.setColor(Color.PURPLE);
        debugLbl.setText(Gdx.graphics.getWidth() + " " + Gdx.graphics.getHeight());

        txtrBG = new Texture("core/assets/fire_rescue.png");
        spriteBG = new Sprite(txtrBG);
        spriteBG.setScale(0.6f);
        spriteBG.setPosition(-(Gdx.graphics.getWidth() / 2f) - 125, -(Gdx.graphics.getHeight() / 2f) + 30);

        generator =
                new FreeTypeFontGenerator(Gdx.files.internal("core/assets/data/Capture_it.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 72;
        fontCaptureIt = generator.generateFont(parameter);
        fontCaptureIt.setColor(Color.CORAL);
        gl = new GlyphLayout(fontCaptureIt, FLASHPOINT);

        btnLogin = new TextButton("Login", skinUI, "default");
        btnLogin.setWidth(100);
        btnLogin.setHeight(25);
        btnLogin.setPosition(
                (Gdx.graphics.getWidth() - btnLogin.getWidth()) / 2,
                Gdx.graphics.getHeight() / 3f - (btnLogin.getHeight() / 2));
        btnLogin.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (btnLogin.getText().toString().equals("Login")) btnLogin.setText("Logging in...");
                        else btnLogin.setText("Login");
                    }

                    @Override
                    public void enter(
                            InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        btnLogin.setText("Sent");
                    }

                    @Override
                    public void exit(
                            InputEvent event, float x, float y, int pointer, Actor toActor) {
                        btnLogin.setText("Send");
                    }
                });

        fdUname = new TextField("", skinUI, "default");
        fdUname.setMessageText("Username");
        fdUname.setWidth(200);
        fdUname.setHeight(25);
        fdUname.setPosition(
                (Gdx.graphics.getWidth() - fdUname.getWidth()) / 2,
                Gdx.graphics.getHeight() * 3 / 5f - (fdUname.getHeight() / 2));

        fdPwd = new TextField("", skinUI, "default");
        fdPwd.setPasswordMode(true);
        fdPwd.setPasswordCharacter('*');
        fdPwd.setMessageText("Password");
        fdPwd.setWidth(200);
        fdPwd.setHeight(25);
        fdPwd.setPosition(
                (Gdx.graphics.getWidth() - fdUname.getWidth()) / 2,
                Gdx.graphics.getHeight() / 2f - (fdUname.getHeight() / 2));

        stage = new Stage();
        stage.addActor(fdUname);
        stage.addActor(fdPwd);
        stage.addActor(btnLogin);
        // stage.addActor(fontCaptureIt);
        stage.addActor(debugLbl);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void dispose() {
        batch.dispose();
        fontCaptureIt.dispose();
        generator.dispose();
        skinUI.dispose();
        stage.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.disableBlending();
        spriteBG.draw(batch);
        batch.enableBlending();
        batch.end();

        batch.begin();
        stage.draw();
        batch.end();

        batch.begin();
        fontCaptureIt.draw(
                batch,
                FLASHPOINT,
                (Gdx.graphics.getWidth() - gl.width) / 2,
                Gdx.graphics.getHeight() * 9 / 10f - (gl.height / 2));
        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
