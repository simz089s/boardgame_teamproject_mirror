package com.cs361d.flashpoint.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

import java.util.ArrayList;

public class BoardStatsFragment {

    Stage stage;

    // specialist cards list
    List<String> lstStats;
    List.ListStyle listStyle;
    ScrollPane scrollPaneStats;
    ScrollPane.ScrollPaneStyle scrollStyle;

    ArrayList<ScrollPane> statsListSP = new ArrayList<ScrollPane>();

    // constructor
    public BoardStatsFragment(Stage stage){
        this.stage = stage;
    }

    // TO DO : pass in an updated Stats object to be converted to a string (GameManager)
    public void createStatsFragment() {

        //create available games list (TO JOIN)
        String[] gamesStatsArr = {"\t\t[Team stats]", "Walls left: X/20", "Victims saved: Y/7", "Victims lost: Z/5", "",
                "[My info]", "Accumulated AP: 1", "Special AP: 0", "Specialist: Fire Captain", "# victims saved: 2", "",
                "[BLUE]", "Accumulated AP: 2", "Special AP: 1", "Specialist: Rescue specialist", "# victims saved: 0", "",
                "[YELLOW]", "Accumulated AP: 0", "Special AP: 1", "Specialist: Paramedic", "# victims saved: 0"
        };

        // list style
        listStyle = new List.ListStyle();
        listStyle.font = Font.get(20); // font size
        listStyle.fontColorUnselected = Color.BLACK;
        listStyle.fontColorSelected = Color.BLACK;
        listStyle.selection = TextureLoader.getDrawable(100, 100, Color.CLEAR );

        lstStats = new List<String>(listStyle);
        lstStats.setItems(gamesStatsArr);

        // scrollPane style
        scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
        scrollStyle.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

        scrollPaneStats = new ScrollPane(lstStats, scrollStyle);
        scrollPaneStats.setOverscroll(false, false);
        scrollPaneStats.setFadeScrollBars(false);
        scrollPaneStats.setScrollingDisabled(true, false);
        scrollPaneStats.setTransform(true);
        scrollPaneStats.setScale(1.0f);
        scrollPaneStats.setWidth(360);
        scrollPaneStats.setHeight(450);
        //scrollMessage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 100);
        scrollPaneStats.setPosition(
                845,
                Gdx.graphics.getHeight() - scrollPaneStats.getHeight() - 150);


        statsListSP.add(scrollPaneStats);
        stage.addActor(scrollPaneStats);
    }

    public void removeStatsFragment() {
        for (int i = 0; i < statsListSP.size(); i++) {
            statsListSP.get(i).remove();
        }
        statsListSP.clear();
    }
}
