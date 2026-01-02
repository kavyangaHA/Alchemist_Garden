package com.mygdx.game;

//using Simple Factory
public class PlantFactory {
    //This is our "Factory Method".It is static,so we can call it
    //directly from the class without needing to create a factory object.

    public static Plant createPlant(String plantType,int x,int y){
        //we use a switch statement to decide which plant to create
        //based on the string that is given
        switch (plantType){
            case "Vine":
                return new VinePlant(x,y);
                //In future,we can add more plants(cases)
                //ex- case "Bomb" this will give us bombPlant(x,y);

            default:
                //if unknown type is requested we return null
                // To avoid crashes
                return null;
        }



    }


}
