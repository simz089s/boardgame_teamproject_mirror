package com.cs361d.flashpoint.GameScreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.ui.*;
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

    CheckBox signUpCheck;

    TextButton btnLogin;

    Label errorMsgLabel;

    Stage stage;

    LoginScreen(Game pGame) {
        super(pGame);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        debugLbl.setPosition(10, 10);
        debugLbl.setColor(Color.PURPLE);
        debugLbl.setText(Gdx.graphics.getWidth() + " " + Gdx.graphics.getHeight());

        txtrBG = new Texture("login.png");
        spriteBG = new Sprite(txtrBG);
        //spriteBG.setScale(0.6f);
        spriteBG.setPosition(
                0, 0);

        generator =
                new FreeTypeFontGenerator(Gdx.files.internal("data/Capture_it.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 72;
        fontCaptureIt = generator.generateFont(parameter);
        fontCaptureIt.setColor(Color.CORAL);
        gl = new GlyphLayout(fontCaptureIt, FLASHPOINT);

        createUsernameTextField();

        createPasswordTextField();

        createSignUpCheckbox();

        createLoginBtn();

        createErrorMsgLabel();

        signUpCheck.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (signUpCheck.isChecked()) {
                    btnLogin.setText("Sign up");
                } else {
                    btnLogin.setText("Login");
                }
            }
        });

        btnLogin.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        String usr = fdUname.getText();
                        String pwd = fdPwd.getText();

                        if (usr.isEmpty() || pwd.isEmpty()){
                            errorMsgLabel.setText("Error: empty fields");
                        } else {
                            //if (searchDB(usr, pwd)) {
                                game.setScreen(game.lobbyScreen);
                            /*} else {
                                btnLogin.setText("Wrong");
                            }*/
                        }
                    }
                });

        stage = new Stage();
        stage.addActor(fdUname);
        stage.addActor(fdPwd);
        stage.addActor(signUpCheck);
        stage.addActor(btnLogin);
        stage.addActor(errorMsgLabel);
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
        super.dispose();
        batch.dispose();
        fontCaptureIt.dispose();
        generator.dispose();
        stage.dispose();
    }

    public void createUsernameTextField(){
        fdUname = new TextField("", skinUI, "default");
        fdUname.setMessageText("Username");
        fdUname.setWidth(200);
        fdUname.setHeight(25);
        fdUname.setPosition(
                (Gdx.graphics.getWidth() - fdUname.getWidth()) / 2,
                Gdx.graphics.getHeight() * 3 / 5f - (fdUname.getHeight() / 2));
    }

    public void createPasswordTextField(){
        fdPwd = new TextField("", skinUI, "default");
        fdPwd.setPasswordMode(true);
        fdPwd.setPasswordCharacter('*');
        fdPwd.setMessageText("Password");
        fdPwd.setWidth(200);
        fdPwd.setHeight(25);
        fdPwd.setPosition(
                (Gdx.graphics.getWidth() - fdUname.getWidth()) / 2,
                Gdx.graphics.getHeight() / 2f - (fdUname.getHeight() / 2) + 10);
    }


    public void createSignUpCheckbox(){
        signUpCheck = new CheckBox(" Sign up", skinUI);
        signUpCheck.setPosition(
                Gdx.graphics.getWidth() / 2 - signUpCheck.getWidth() / 2,
                Gdx.graphics.getHeight() / 2f - (fdUname.getHeight() / 2) - 20);
    }

    public void createLoginBtn(){
        btnLogin = new TextButton("Login", skinUI, "default");
        btnLogin.setWidth(100);
        btnLogin.setHeight(25);
        btnLogin.setPosition(
                (Gdx.graphics.getWidth() - btnLogin.getWidth()) / 2,
                Gdx.graphics.getHeight() / 3f - (btnLogin.getHeight() / 2));
    }

    public void createErrorMsgLabel(){
        errorMsgLabel = new Label("", skinUI);
        errorMsgLabel.setPosition(
                Gdx.graphics.getWidth() / 2 - 70,
                (Gdx.graphics.getHeight() / 3f - (btnLogin.getHeight() / 2)) / 2);
        errorMsgLabel.setColor(Color.RED);
    }
}
