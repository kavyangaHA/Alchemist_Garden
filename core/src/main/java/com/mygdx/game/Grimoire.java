package com.mygdx.game;

import java.util.ArrayList;

public class Grimoire {
    //this method handle the combination logic
    //it takes the players inventory and modifies it.

    public static void combine(ArrayList<String>seeds,ArrayList<String>essences){
        boolean hasVineSeed = seeds.contains("Vine");
        boolean hasSunEssence = essences.contains("SunEssence");

        //check if the player has the required items for the combination
        if(hasVineSeed && hasSunEssence){
            System.out.println("Combining VineSeed and SunEssence....");

            //after combining we need to remove the old items from the inventory
            seeds.remove("Vine");
            essences.remove("SunEssence");

            //now we need to add the new(combined one)seed to the inventory
            seeds.add("SunKissedVineSeed");

            System.out.println("You creates a Sun-Kissed Vine Seed!");

        }
        else {
            System.out.println("Cannot combine.You need a VineSeed and a SunEssence.");
        }
    }
}
