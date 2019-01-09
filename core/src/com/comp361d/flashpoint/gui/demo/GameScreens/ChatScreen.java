package com.comp361d.flashpoint.gui.demo.GameScreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatScreen extends FlashPointScreen {

    Stage stage;
    SpriteBatch batch;
    Texture txtrBG;
    Sprite spriteBG;

    FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    BitmapFont fontCaptureIt;

    // info about chat text view(ex: number of active user)
    Label labelInfo;

    // chat message list
    List<String> lstMsg;
    ListStyle listStyle;
    ScrollPane scrollPaneMsg;
    ScrollPane.ScrollPaneStyle scrollStyle;

    // input message field
    TextField textFieldMsg;

    TextButton btnChangePage;
    TextButton btnExit;

    ChatScreen(Game pGame) {
        super(pGame);
    }

    @Override
    public void show() {

        batch = new SpriteBatch();

        txtrBG = new Texture("chat.png");
        spriteBG = new Sprite(txtrBG);
        spriteBG.setScale(0.6f);
        spriteBG.setPosition(
                -(Gdx.graphics.getWidth() / 2f) - 125, -(Gdx.graphics.getHeight() / 2f) + 30);

        generator =
                new FreeTypeFontGenerator(Gdx.files.internal("data/Capture_it.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 72;
        fontCaptureIt = generator.generateFont(parameter);
        fontCaptureIt.setColor(Color.CORAL);

        // info label
        createInfoLabel("Chat: 3 Active Users");

        // messages list
        String[] messages = {"Jacques:  Ready guys?", "Simon:  let's start!", "Elvric: wait, just a sec"};
        final ArrayList<String> msgs = new ArrayList<String>(Arrays.asList(messages));
        createMessageList(messages);

        // exit button
        createExitButton();

        // send message button
        createChangePageButton();

        // message input text field
        createMessageInputText("Message + [Enter]");

        // listeners
        btnExit.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(game.lobbyScreen);
                    }
                });

        btnChangePage.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(game.statsScreen);
                    }
                });

        textFieldMsg.setTextFieldListener(new TextField.TextFieldListener(){
            @Override
            public void keyTyped(TextField textField, char c){

                if((int)c == 13 || (int)c == 10) {
                    String messageInputed = textFieldMsg.getText();

                    if(!messageInputed.equals("") && !messageInputed.equals(" ")){
                        textFieldMsg.setText("");
                        msgs.add("Jacques:  " + messageInputed);
                        String[] newMsg = msgs.toArray(new String[msgs.size()]);
                        lstMsg.setItems(newMsg);

                        scrollPaneMsg.setActor(lstMsg);
                        scrollPaneMsg.layout();
                        scrollPaneMsg.scrollTo(0, 0, 0, 0);
                    }
                }
            }
        });

        stage = new Stage();
        stage.addActor(scrollPaneMsg);
        stage.addActor(textFieldMsg);
        stage.addActor(btnExit);
        stage.addActor(btnChangePage);
        // stage.addActor(fontCaptureIt);
        stage.addActor(labelInfo);

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
        stage.act(); // scroll bar activation
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

    // UI elements creation
    private void createInfoLabel(String message) {
        labelInfo = new Label(message, skinUI);
        labelInfo.setColor(Color.NAVY);
        labelInfo.setPosition(10, Gdx.graphics.getHeight() - labelInfo.getHeight() - 15);
    }

    private void createMessageList(String[] messages) {
        // list style
        listStyle = new ListStyle();
        listStyle.font = Font.get(25); // font size
        listStyle.fontColorUnselected = Color.BLACK;
        listStyle.fontColorSelected = Color.BLACK;
        listStyle.selection = TextureLoader.getDrawable(100, 100, Color.CLEAR );

        lstMsg = new List<String>(listStyle);
        lstMsg.setItems(messages);

        // scrollPane style
        scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
        scrollStyle.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

        scrollPaneMsg = new ScrollPane(lstMsg, scrollStyle);
        scrollPaneMsg.setOverscroll(false, false);
        scrollPaneMsg.setFadeScrollBars(false);
        scrollPaneMsg.setScrollingDisabled(true, false);
        scrollPaneMsg.setTransform(true);
        scrollPaneMsg.setScale(1.0f);
        scrollPaneMsg.setWidth(Gdx.graphics.getWidth() - 20);
        scrollPaneMsg.setHeight(Gdx.graphics.getHeight() - 100);
        //scrollMessage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 100);
        scrollPaneMsg.setPosition(
                10,
                Gdx.graphics.getHeight() - scrollPaneMsg.getHeight() - 45);
    }

    private void createExitButton() {
        btnExit = new TextButton("EXIT", skinUI, "default");
        btnExit.setWidth(100);
        btnExit.setHeight(25);
        btnExit.setPosition(
                (Gdx.graphics.getWidth() - btnExit.getWidth() - 8),
                (Gdx.graphics.getHeight() - btnExit.getHeight() - 8));
    }

    private void createChangePageButton() {
        btnChangePage = new TextButton("Go to game", skinUI, "default");
        btnChangePage.setWidth(125);
        btnChangePage.setHeight(25);
        btnChangePage.setPosition(
                (Gdx.graphics.getWidth() - btnChangePage.getWidth() - 8),
                5);
    }

    private void createMessageInputText(String hint) {
        textFieldMsg = new TextField("", skinUI, "default");
        textFieldMsg.setMessageText(hint);
        textFieldMsg.setWidth(475);
        textFieldMsg.setHeight(25);
        textFieldMsg.setPosition(
                5,
                5);
    }
}
