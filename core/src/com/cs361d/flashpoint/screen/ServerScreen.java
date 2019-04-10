package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.NetworkLogger;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ServerScreen implements Screen {

    private static final Logger LOGGER = Logger.getLogger(NetworkLogger.class.getPackage().getName());

    FlashPointServerGame game;

    private Stage stage = new Stage();
    private SpriteBatch batch = new SpriteBatch();

    static Skin skinUI = new Skin(Gdx.files.internal("data/uiskin.json"));

    Label infoTitle = new Label("SERVER | Public IP: " + Client.serverIP + " | Close this window to close the server", skinUI);
    Button testBtn = new Button(skinUI);

    ScrollPane scrollPaneMsg;
    ScrollPaneStyle scrollStyle;

    // messages list
    ListStyle listStyle;
    private static ArrayList<String> messages = new ArrayList();
    private static com.badlogic.gdx.scenes.scene2d.ui.List<String> lstMsg;

    ArrayList<ScrollPane> msgListSP = new ArrayList();

    ServerScreen(Game pGame) {
        game = (FlashPointServerGame) pGame;
    }

    public static void logMessage(String msg) {
        messages.add(msg);
        lstMsg.clearItems();
        String[] msgArr = messages.toArray(new String[messages.size()]);
        lstMsg.setItems(msgArr);
    }

    @Override
    public void show() {

        infoTitle.setPosition(0, Gdx.graphics.getHeight() - 20);
        infoTitle.setColor(Color.SKY);
        stage.addActor(infoTitle);

        testBtn.setSize(25, 20);
        testBtn.setPosition(1, 1);
        testBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent even, float x, float y) {
                LOGGER.info("Logger call test");
            }
        });
        stage.addActor(testBtn);

        // list style
        listStyle = new ListStyle();
        listStyle.font = Font.get(12); // font size
        listStyle.fontColorUnselected = Color.GREEN;
        listStyle.fontColorSelected = Color.GREEN;
        listStyle.selection = TextureLoader.getDrawable(100, 100, Color.CLEAR);

        lstMsg = new com.badlogic.gdx.scenes.scene2d.ui.List<String>(listStyle);
        messages.clear();
        String[] messagesStrArr = messages.toArray(new String[messages.size()]);
        lstMsg.setItems(messagesStrArr);

        // scrollPane style
        scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = TextureLoader.getDrawable(10, 10, Color.DARK_GRAY);
        scrollStyle.vScroll = TextureLoader.getDrawable(10, 10, Color.LIGHT_GRAY);

        scrollPaneMsg = new ScrollPane(lstMsg, scrollStyle);
        scrollPaneMsg.setOverscroll(false, false);
        scrollPaneMsg.setFadeScrollBars(false);
        scrollPaneMsg.setTransform(true);
        scrollPaneMsg.setWidth(600);
        scrollPaneMsg.setHeight(300);
        scrollPaneMsg.setPosition(5, 20);

        msgListSP.add(scrollPaneMsg);

        stage.addActor(scrollPaneMsg);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        stage.draw();
        stage.act();
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
        stage.dispose();
        skinUI.dispose();
    }
}
