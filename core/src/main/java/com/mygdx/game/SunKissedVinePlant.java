package com.mygdx.game;

public class SunKissedVinePlant extends VinePlant {
    //override the MAX_AGE to make it grow faster
    private static final int MAX_AGE = 150; //Grow in ~2.5s

    public SunKissedVinePlant(int x,int y){
        super(x,y);
    }

    @Override
    public boolean isMature() {
        return age>=MAX_AGE;
    }
}
