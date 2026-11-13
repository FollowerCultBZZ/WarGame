package io.w8.game.core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.w8.game.core.Core;
import io.w8.game.core.GameFonts;

public class MenuScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 720f;
    private static final float WORLD_HEIGHT = 1280f;
    private static final float BUTTON_WIDTH = 440f;
    private static final float BUTTON_HEIGHT = 112f;
    private static final float BUTTON_X = (WORLD_WIDTH - BUTTON_WIDTH) * 0.5f;
    private static final float PLAY_Y = 610f;
    private static final float EXIT_Y = 455f;

    private final Core game;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final ExtendViewport viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
    private final SpriteBatch batch = new SpriteBatch();
    private final TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("atlases/game.atlas"));
    private final BitmapFont titleFont = GameFonts.create(64);
    private final BitmapFont buttonFont = GameFonts.create(36);
    private final GlyphLayout layout = new GlyphLayout();
    private final TextureRegion buttonUp = atlas.findRegion("button-up");
    private final TextureRegion buttonDown = atlas.findRegion("button-down");
    private int pressedButton = -1;

    public MenuScreen(Core game) {
        this.game = game;
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        Gdx.input.setInputProcessor(new InputAdapter() {
            private final Vector2 point = new Vector2();

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (pointer != 0) return false;
                point.set(screenX, screenY);
                viewport.unproject(point);
                pressedButton = hitButton(point.x, point.y);
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (pointer != 0) return false;
                point.set(screenX, screenY);
                viewport.unproject(point);
                int releasedButton = hitButton(point.x, point.y);
                if (pressedButton != -1 && pressedButton == releasedButton) {
                    if (pressedButton == 0) {
                        game.setScreen(new GameScreen());
                    } else if (pressedButton == 1) {
                        Gdx.app.exit();
                    }
                }
                pressedButton = -1;
                return true;
            }
        });
    }

    private int hitButton(float x, float y) {
        if (inside(x, y, PLAY_Y)) return 0;
        if (inside(x, y, EXIT_Y)) return 1;
        return -1;
    }

    private boolean inside(float x, float y, float buttonY) {
        return x >= BUTTON_X && x <= BUTTON_X + BUTTON_WIDTH
                && y >= buttonY && y <= buttonY + BUTTON_HEIGHT;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.015f, 0.025f, 0.07f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        layout.setText(titleFont, "SPACE WAR");
        titleFont.draw(batch, layout, (WORLD_WIDTH - layout.width) * 0.5f, 900f);

        drawButton(0, PLAY_Y, "PLAY");
        drawButton(1, EXIT_Y, "EXIT");

        layout.setText(buttonFont, "TAP TO MOVE  •  DOUBLE TAP: ULTIMATE");
        buttonFont.getData().setScale(0.65f);
        layout.setText(buttonFont, "TAP TO MOVE  •  DOUBLE TAP: ULTIMATE");
        buttonFont.draw(batch, layout, (WORLD_WIDTH - layout.width) * 0.5f, 300f);
        buttonFont.getData().setScale(1f);

        batch.end();
    }

    private void drawButton(int id, float y, String text) {
        TextureRegion region = pressedButton == id ? buttonDown : buttonUp;
        batch.draw(region, BUTTON_X, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        layout.setText(buttonFont, text);
        buttonFont.draw(batch, text,
                WORLD_WIDTH * 0.5f - layout.width * 0.5f,
                y + (BUTTON_HEIGHT + layout.height) * 0.5f);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        atlas.dispose();
        titleFont.dispose();
        buttonFont.dispose();
        Gdx.input.setInputProcessor(null);
    }
}
