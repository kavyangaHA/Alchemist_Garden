package com.mygdx.game;

public class VinePlant extends Plant{
    public VinePlant(int x, int y) {
        super(x, y);
    }

    @Override
    public void grow() {
        if (age < MAX_AGE){
            age ++;
        }
    }
}
