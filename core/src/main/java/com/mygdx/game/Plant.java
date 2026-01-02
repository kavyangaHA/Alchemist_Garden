package com.mygdx.game;

//abstract class means we can't create a "Plant" directly,
//only specific types of plant that extend it.
public abstract class Plant {

    //position of the grid
    public int x;
    public int y;

    //internal state of the plant
    protected int age;
    protected static final int MAX_AGE = 300;//it takes 300 "frames" to mature (~5 seconds at 60 fps)
    //The "Family Only" Rule
    //The protected keyword means that the variable is private to the outside world,
    // but accessible to its children (subclasses).
    //
    //If it were private: A Sunflower class extending Plant wouldn't be able to see or change the age variable.
    // It would be like a parent having a secret the children aren't allowed to know.
    //
    //If it were public: Any other class in your entire game (like ScoreBoard or Enemy) could reach in and change the plant's age.
    // This is dangerous because it leads to "spaghetti code."
    //2. Why it’s used for age
    //Since Plant is an abstract class, it is designed specifically to be inherited.
    //
    //When you create a specific plant, like a TomatoPlant, you will likely want to
    // write logic inside its grow() method that modifies the age:



    public Plant(int x, int y){
        this.x = x;
        this.y = y;
        this.age = 0 ;
    }
    //this method will be called every frame to update the plant
    public abstract void grow();
    //With abstract: If you create a Sunflower class and forget to write the grow() method,
    // the code will not compile. It catches your mistake immediately.
    //
    //Without abstract: If you use a regular empty method and forget to override it in Sunflower,
    // the game will run, but your sunflowers will just sit there and do nothing forever. This makes bugs much harder to find.

    public boolean isMature(){
        return age >= MAX_AGE;
    }
}
