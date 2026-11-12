package io.w8.game.core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import io.w8.game.core.world.camera.CameraController;
import io.w8.game.core.world.map.MapWorld;
import io.w8.game.core.world.player.Player;
import io.w8.game.core.world.render.WorldRenderer;

public class GameScreen extends ScreenAdapter {

    private static final long DOUBLE_TAP_TIME = 320;
    private static final float DOUBLE_TAP_DISTANCE = 48f;
    private static final float DRAG_THRESHOLD = 9f;

    private final OrthographicCamera camera = new OrthographicCamera();
    private final SpriteBatch batch = new SpriteBatch();
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("build/game.atlas"));
    private final MapWorld world = new MapWorld(10, 10, 128, 64);
    private final Player player = new Player(4096f, 4096f);
    private final CameraController cameraController = new CameraController(camera, world);
    private final WorldRenderer worldRenderer = new WorldRenderer(batch, atlas, world);

    private float downX;
    private float downY;
    private float lastX;
    private float lastY;
    private boolean dragging;
    private boolean moved;
    private long lastTapTime;
    private float lastTapX;
    private float lastTapY;

    public GameScreen() {
        cameraController.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraController.snapTo(player.getX(), player.getY());
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (pointer != 0) return false;
                downX = lastX = screenX;
                downY = lastY = screenY;
                dragging = false;
                moved = false;
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (pointer != 0) return false;
                float totalX = screenX - downX;
                float totalY = screenY - downY;
                if (!dragging && totalX * totalX + totalY * totalY > DRAG_THRESHOLD * DRAG_THRESHOLD) {
                    dragging = true;
                    lastTapTime = 0;
                    cameraController.setFollowing(false);
                }
                if (dragging) {
                    cameraController.pan(screenX - lastX, screenY - lastY);
                    moved = true;
                }
                lastX = screenX;
                lastY = screenY;
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (pointer != 0) return false;
                if (!dragging && !moved) {
                    long now = TimeUtils.millis();
                    float dx = screenX - lastTapX;
                    float dy = screenY - lastTapY;
                    if (lastTapTime != 0 && now - lastTapTime <= DOUBLE_TAP_TIME
                            && dx * dx + dy * dy <= DOUBLE_TAP_DISTANCE * DOUBLE_TAP_DISTANCE) {
                        Vector2 destination = camera.unproject(new Vector2(screenX, screenY));
                        player.moveTo(destination.x, destination.y);
                        cameraController.setFollowing(true);
                        lastTapTime = 0;
                    } else {
                        lastTapTime = now;
                        lastTapX = screenX;
                        lastTapY = screenY;
                    }
                }
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
                    Gdx.app.exit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void render(float delta) {
        float dt = Math.min(delta, 0.05f);
        player.update(dt, world.getWorldWidth(), world.getWorldHeight());
        cameraController.update(dt, player);
        Gdx.gl.glClearColor(0.07f, 0.09f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        worldRenderer.render(camera);

        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(1f, 0.72f, 0.18f, 1f);
        shapes.circle(player.getX(), player.getY(), 17f, 24);
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(1f, 1f, 1f, 0.9f);
        shapes.circle(player.getX(), player.getY(), 22f, 24);
        if (player.isMoving()) {
            shapes.setColor(1f, 0.9f, 0.25f, 0.8f);
            shapes.circle(player.getTargetX(), player.getTargetY(), 12f, 20);
        }
        shapes.end();
    }

    @Override
    public void resize(int width, int height) {
        cameraController.resize(width, height);
    }

    @Override
    public void dispose() {
        worldRenderer.dispose();
        atlas.dispose();
        batch.dispose();
        shapes.dispose();
        Gdx.input.setInputProcessor(null);
    }
}
