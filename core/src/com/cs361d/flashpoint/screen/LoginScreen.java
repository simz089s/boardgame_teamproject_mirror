package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
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
import com.cs361d.flashpoint.manager.User;
import com.cs361d.flashpoint.networking.NetworkManager;

public class LoginScreen extends FlashPointScreen {

    static final String FLASHPOINT = "FLASH POINT";

    Stage stage;

    SpriteBatch batch;

    Texture txtrBG;
    Sprite spriteBG;

    FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    BitmapFont fontCaptureIt;
    GlyphLayout gl;

    TextField fdUname;
    TextField fdPwd;
    TextField fdSrvIP;
    CheckBox signUpCheck;
    TextButton btnLogin;
    Label errorMsgLabel;

    private Music BGM = Gdx.audio.newMusic(Gdx.files.internal("playlist/matrix.mp3"));

    LoginScreen(Game pGame) {
        super(pGame);
        BGM.setLooping(true);
    }

    @Override
    public void show() {

        BGM.play();

        stage = new Stage();
        batch = new SpriteBatch();


        txtrBG = new Texture("login.png");
        spriteBG = new Sprite(txtrBG);
        //spriteBG.setScale(0.6f);
        spriteBG.setPosition(
                0, 0);

        generator = new FreeTypeFontGenerator(Gdx.files.internal("data/Capture_it.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 72;
        fontCaptureIt = generator.generateFont(parameter);
        fontCaptureIt.setColor(Color.CORAL);
        gl = new GlyphLayout(fontCaptureIt, FLASHPOINT);


        createUsernameTextField();

        createPasswordTextField();

        createServerIPTextField();

        createSignUpCheckbox();

        createLoginBtn();

        createErrorMsgLabel();


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
    public void hide() {
        BGM.stop();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        fontCaptureIt.dispose();
        generator.dispose();
        stage.dispose();
    }




    // text fields


    public void createUsernameTextField(){
        fdUname = new TextField("", skinUI, "default");
        fdUname.setMessageText("Username");
        fdUname.setWidth(200);
        fdUname.setHeight(25);
        fdUname.setPosition(
                (Gdx.graphics.getWidth() - fdUname.getWidth()) / 2,
                Gdx.graphics.getHeight() * 3 / 5f - (fdUname.getHeight() / 2));

        stage.addActor(fdUname);
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

        stage.addActor(fdPwd);
    }

    private void createServerIPTextField() {
        fdSrvIP = new TextField("", skinUI, "default");
        fdSrvIP.setMessageText(NetworkManager.DEFAULT_SERVER_IP);
        fdSrvIP.setWidth(200);
        fdSrvIP.setHeight(25);
        fdSrvIP.setPosition(
                (Gdx.graphics.getWidth() - fdSrvIP.getWidth()) / 2,
                Gdx.graphics.getHeight() * 1 / 5f - (fdSrvIP.getHeight() / 2));
//        NetworkManager.getInstance().setServerIP(fdSrvIP.getText());
        stage.addActor(fdSrvIP);
    }



    // sign up check box




    public void createSignUpCheckbox(){
        signUpCheck = new CheckBox(" Sign up", skinUI);
        signUpCheck.setPosition(
                Gdx.graphics.getWidth() / 2 - signUpCheck.getWidth() / 2,
                Gdx.graphics.getHeight() / 2f - (fdUname.getHeight() / 2) - 20);

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

        stage.addActor(signUpCheck);
    }



    // button



    public void createLoginBtn(){
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
                        String usr = fdUname.getText();
                        String pwd = fdPwd.getText();

                        User.getInstance().setName(usr);
                        game.setScreen(game.lobbyScreen);

//                        if (searchDB(usr, pwd)) {
//                            User.getInstance().setName(usr);
//                            game.setScreen(game.lobbyScreen);
//                        } else {
//                            errorMsgLabel.setText("Invalid credentials, try again.");
//                        }
                    }
                });

        stage.addActor(btnLogin);
    }



    // error msg label



    public void createErrorMsgLabel(){
        errorMsgLabel = new Label("", skinUI);
        errorMsgLabel.setPosition(
                Gdx.graphics.getWidth() / 2 - 80,
                Gdx.graphics.getHeight() / 2 - 60);
        errorMsgLabel.setColor(Color.RED);

        stage.addActor(errorMsgLabel);
    }
}
