package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class MyGdxGame extends Game {
    // We will hold our current screen here
    private GameScreen gameScreen;

    @Override
    public void create() {
        // Create an instance of our game screen
        gameScreen = new GameScreen();
        // Set it as the current screen for the game
        setScreen(gameScreen);
    }

    @Override
    public void render() {
        // Clear the screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // This is crucial! It calls the render method of our current screen
        super.render();
    }

    @Override
    public void dispose() {
        // Dispose of the screen when the game closes
        gameScreen.dispose();
    }
}
