package com.cs361d.flashpoint.Entities.BoardElements;


import com.cs361d.flashpoint.Entities.Card;

public class FireFighter{

    //firefighter static attributes

    //Texture texture ;
    //Sprite sprite

    int PlayerNumber ;
    FireFighterColor color ;

    //firefighter dynamic attributes

    Card role ;
    int maxActionPoints;
    int actionPointsLeft ;
    Tile currentTile ;


    public FireFighter(Card pCard ){

        this.role = pCard ;

        if(this.role.name == "Generalist" )
        {
            maxActionPoints = 6 ;
        }




    }


}
