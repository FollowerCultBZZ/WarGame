package io.w8.game.core;

import com.badlogic.gdx.Game;
import io.w8.game.core.screens.GameScreen;

public class Core extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
