package com.mygdx.game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class MyGdxGame extends Game {
    public BitmapFont font;
    @Override
    public void create() {
        // Set the screen to our new TitleScreen
        font = new BitmapFont();
        setScreen(new GameScreen(this));
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render(); // This is crucial! It calls the render method of the current screen
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
