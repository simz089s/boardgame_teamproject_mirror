package com.comp361d.flashpoint.gui.demo.GameScreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class LoginScreen extends FlashPointScreen {

    static final String FLASHPOINT = "Flash Point";

    SpriteBatch batch;

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

    private static final String[][] ACCOUNTS = {
        {"Username", "Password"},
        {"Simon", "notunderrated"},
        {"Elvric", "BGod"},
        {"Jacques", "corp"},
        {"David", "notdaniel"},
        {"Daniel", "notdavid"},
        {"Mat", "hematics"}
    };

    private static boolean searchDB(String usr, String pwd) {
        for (int i = 0; i < ACCOUNTS.length; i++)
            if (ACCOUNTS[i][0].equals(usr) && ACCOUNTS[i][1].equals(pwd)) return true;
        return false;
    }

    LoginScreen(Game pGame) {
        super(pGame);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        debugLbl.setPosition(10, 10);
        debugLbl.setColor(Color.PURPLE);
        debugLbl.setText(Gdx.graphics.getWidth() + " " + Gdx.graphics.getHeight());

        txtrBG = new Texture("core/assets/fire_rescue.png");
        spriteBG = new Sprite(txtrBG);
        spriteBG.setScale(0.6f);
        spriteBG.setPosition(
                -(Gdx.graphics.getWidth() / 2f) - 125, -(Gdx.graphics.getHeight() / 2f) + 30);

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
                        btnLogin.setText("Logging in...");

                        String usr = fdUname.getText().toString();
                        String pwd = fdPwd.getText().toString();
                        if (searchDB(usr, pwd)) {
                            game.setScreen(game.lobbyScreen);
                        } else {
                            btnLogin.setText("Wrong");
                        }
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
    public void render(float delta) {
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

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        fontCaptureIt.dispose();
        generator.dispose();
        skinUI.dispose();
        stage.dispose();
    }
}