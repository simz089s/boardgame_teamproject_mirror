package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;

import java.util.ArrayList;
import java.util.List;

public class ServerScreen implements Screen {

    FlashPointServerGame game;

    private Stage stage = new Stage();
    private SpriteBatch batch = new SpriteBatch();

    ListStyle listStyle;
    ScrollPane scrollPaneMsg;
    ScrollPaneStyle scrollStyle;

    // messages list
    private static List<String> messages = new ArrayList();
    private static com.badlogic.gdx.scenes.scene2d.ui.List<String> lstMsg;

    List<ScrollPane> msgListSP = new ArrayList();

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

        // list style
        listStyle = new com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle();
        listStyle.font = Font.get(18); // font size
        listStyle.fontColorUnselected = Color.BLACK;
        listStyle.fontColorSelected = Color.BLACK;
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
        scrollPaneMsg.setWidth(360);
        scrollPaneMsg.setHeight(450);
        scrollPaneMsg.setPosition(845, Gdx.graphics.getHeight() - scrollPaneMsg.getHeight() - 150);

        //        createMessageInputText();

        msgListSP.add(scrollPaneMsg);

        stage.addActor(scrollPaneMsg);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
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
    }
}
