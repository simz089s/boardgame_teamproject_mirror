package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;

import java.util.ArrayList;

import static com.cs361d.flashpoint.screen.BoardScreen.removeAllPrevFragments;
import static com.cs361d.flashpoint.screen.FlashPointScreen.skinUI;

public class BoardCheatSFragment {

    String imagesFileName[] = {"1.png", "2.png", "cafs firefighter.png", "driver.png", "fire captain.png", "generalist.png",
            "hazmat technician.png", "imaging technician.png", "paramedic.png", "rescue specialist.png",
            "veteran.png", "rescue dog.png", "structural engineer.png", "pyromancer.png"};

    Image[] cardImages = new Image[imagesFileName.length];

    Stage stage;

    // specialist cards list
    List<String> lstCards;
    List.ListStyle listStyle;
    ScrollPane scrollPaneCard;
    ScrollPane.ScrollPaneStyle scrollStyle;

    ArrayList<ScrollPane> cardsListSP = new ArrayList<ScrollPane>();

    // constructor
    public BoardCheatSFragment(Stage stage){
        this.stage = stage;
        populateCardImgArr();

    }

    public void drawCheatSFragment() {

        if(BoardScreen.isCallForActionFragment()){
            return;
        }

        removeAllPrevFragments();

        // scrollPane style
        scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
        scrollStyle.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

        scrollPaneCard = new ScrollPane(createCardTable(), scrollStyle);
        scrollPaneCard.setOverscroll(false, false);
        scrollPaneCard.setFadeScrollBars(false);
        scrollPaneCard.setScrollingDisabled(true, false);
        scrollPaneCard.setTransform(true);
        scrollPaneCard.setScale(1.0f);
        scrollPaneCard.setWidth(360);
        scrollPaneCard.setHeight(450);
        //scrollMessage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 100);
        scrollPaneCard.setPosition(
                845,
                Gdx.graphics.getHeight() - scrollPaneCard.getHeight() - 190);


        cardsListSP.add(scrollPaneCard);
        stage.addActor(scrollPaneCard);
    }

    private void populateCardImgArr() {
        int i = 0;
        for (String str: imagesFileName){
            cardImages[i] = new Image(new Texture("cheat_sheet/" + str));
            i++;
        }
    }

    private Table createCardTable() {
        Table table = new Table(skinUI);

        for (Image img: cardImages) {
            table.add(img);
            table.row();
        }

        return table;
    }

    public void removeCheatSFragment() {
        for (int i = 0; i < cardsListSP.size(); i++) {
            cardsListSP.get(i).remove();
        }
        cardsListSP.clear();
    }

}
