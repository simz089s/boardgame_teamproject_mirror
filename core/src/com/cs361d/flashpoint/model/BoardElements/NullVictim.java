package com.cs361d.flashpoint.model.BoardElements;

public class NullVictim extends AbstractVictim {


    private static NullVictim nullVictim = new NullVictim();
    private NullVictim() {
        super();
    }
    public static NullVictim getInstance() {
        return nullVictim;
    }

}
