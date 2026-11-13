package io.w8.game.core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.w8.game.core.Core;
import io.w8.game.core.ecs.GameEngine;
import io.w8.game.core.ecs.factories.EntityFactory;
import io.w8.game.core.input.GameInput;
import io.w8.game.core.world.Arena;

public class GameScreen extends ScreenAdapter {
    private static final float VIEW_WIDTH = 720f;
    private static final float VIEW_HEIGHT = 1280f;

    private final Core game;
    private final OrthographicCamera camera;
    private final ExtendViewport viewport;
    private final Arena arena;
    private final GameInput input;
    private final GameEngine gameEngine;

    public GameScreen(Core game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(VIEW_WIDTH, VIEW_HEIGHT, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        arena = new Arena();
        arena.resize(viewport.getWorldWidth(), viewport.getWorldHeight());

        input = new GameInput(viewport);
        gameEngine = new GameEngine(camera, input, arena);

        EntityFactory factory = new EntityFactory(gameEngine.getEngine());
        factory.createPlayer(arena.getWidth() * 0.5f, arena.getHeight() * 0.28f);

        Gdx.input.setInputProcessor(input);
    }

    @Override
    public void render(float delta) {
        float dt = Math.min(delta, 0.05f);

        Gdx.gl.glClearColor(0.015f, 0.025f, 0.07f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameEngine.update(dt);
        boolean gameOver = gameEngine.isPlayerDead();
        input.setGameOver(gameOver);
        if (gameOver && input.consumeRestartPressed()) {
            dispose();
            game.setScreen(new GameScreen(game));
            return;
        }
        camera.update();
        gameEngine.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        arena.resize(viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    @Override
    public void dispose() {
        gameEngine.dispose();
        Gdx.input.setInputProcessor(null);
    }
}
