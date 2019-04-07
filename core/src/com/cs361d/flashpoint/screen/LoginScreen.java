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
import com.badlogic.gdx.utils.Align;
import com.cs361d.flashpoint.manager.User;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.NetworkManager;
import com.cs361d.flashpoint.networking.ServerCommands;

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

    TextField fdUname, fdSrvIP;
    Label errorMsgLabel;

    private Music BGM = Gdx.audio.newMusic(Gdx.files.internal("playlist/void.mp3"));

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

        createErrorMsgLabel();
        createUsernameTextField();

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
        fdUname.setMessageText("Type firefighter name + [Enter]");
        fdUname.setAlignment(Align.center);
        fdUname.setWidth(300);
        fdUname.setHeight(30);
        fdUname.setMaxLength(15);
        fdUname.setPosition(
                900,
                20);


        fdUname.setTextFieldListener(new TextField.TextFieldListener(){
            @Override
            public void keyTyped(TextField textField, char c){

                if((int)c == 13 || (int)c == 10) {

                    if(fdUname.getText().length() < 5){
                        errorMsgLabel.setText("Name must be at least 5 characters.");
                    } else {
                        String usr = fdUname.getText();
                        User.getInstance().setName(usr);
                        Client.getInstance().sendCommand(ServerCommands.GET_SAVED_GAMES,"");
                    }
                }
            }
        });
        stage.addActor(fdUname);
    }

    private void createErrorMsgLabel() {
        errorMsgLabel = new Label("", skinUI);
        errorMsgLabel.setFontScale(1.2f);
        errorMsgLabel.setColor(Color.RED);

        errorMsgLabel.setPosition(
                900, 70);

        stage.addActor(errorMsgLabel);
    }


//    private void createServerIPTextField() {
//        fdSrvIP = new TextField("", skinUI, "default");
//        fdSrvIP.setMessageText(NetworkManager.DEFAULT_SERVER_IP);
//        fdSrvIP.setWidth(200);
//        fdSrvIP.setHeight(25);
//        fdSrvIP.setPosition(
//                (Gdx.graphics.getWidth() - fdSrvIP.getWidth()) / 2,
//                Gdx.graphics.getHeight() * 1 / 5f - (fdSrvIP.getHeight() / 2));
//        stage.addActor(fdSrvIP);
//    }
}
