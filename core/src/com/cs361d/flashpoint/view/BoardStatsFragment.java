package com.cs361d.flashpoint.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.cs361d.flashpoint.manager.BoardManager;
import com.cs361d.flashpoint.manager.FireFighterTurnManager;
import com.cs361d.flashpoint.model.BoardElements.FireFighter;

import java.util.ArrayList;
import java.util.Iterator;

public class BoardStatsFragment {

    Stage stage;

    // stats scroll pane
    List<String> lstStats;
    List.ListStyle listStyle;
    ScrollPane scrollPaneStats;
    ScrollPane.ScrollPaneStyle scrollStyle;

    ArrayList<ScrollPane> statsListSP = new ArrayList<ScrollPane>();

    // constructor
    public BoardStatsFragment(Stage stage){
        this.stage = stage;
    }

    // TODO : pass in an updated Stats object to be converted to a string (GameManager)
    public void createStatsFragment() {

        String[] gamesStatsArr = createStats();

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

    private String[] createStats(){

        ArrayList<String> gamesStatsArrList = new ArrayList<String>();

        gamesStatsArrList.add(BoardManager.getInstance().getGameName());
        gamesStatsArrList.add("");
        gamesStatsArrList.add("[Team stats]");
        gamesStatsArrList.add("Walls left: " + BoardManager.getInstance().getTotalWallDamageLeft());
        gamesStatsArrList.add("Victims saved: " + BoardManager.getInstance().getNumVictimSaved());
        gamesStatsArrList.add("Victims lost: " + BoardManager.getInstance().getNumVictimDead());
        gamesStatsArrList.add("");

        Iterator<FireFighter> it = FireFighterTurnManager.getInstance().iterator();
        while(it.hasNext()) {
            FireFighter f = it.next();
            gamesStatsArrList.add("" + f.getColor());
            gamesStatsArrList.add("Accumulated AP: " + f.getActionPointsLeft());
            gamesStatsArrList.add("Special AP: 0");
            gamesStatsArrList.add("Specialist: Fire Captain");
            gamesStatsArrList.add("");
        }

        String[] gamesStatsArr = gamesStatsArrList.toArray(new String[0]);

        return gamesStatsArr;
    }

    public void removeStatsFragment() {
        for (int i = 0; i < statsListSP.size(); i++) {
            statsListSP.get(i).remove();
        }
        statsListSP.clear();
    }
}
