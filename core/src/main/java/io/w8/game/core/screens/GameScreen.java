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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import io.w8.game.core.world.camera.CameraController;
import io.w8.game.core.world.map.MapWorld;
import io.w8.game.core.world.player.Player;
import io.w8.game.core.world.render.WorldRenderer;
import io.w8.game.core.ui.GameHud;
import io.w8.game.core.world.time.GameTime;
import io.w8.game.core.world.rules.GameRules;
import io.w8.game.core.world.search.SearchSystem;

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
    private final GameHud hud = new GameHud();
    private final GameTime gameTime = new GameTime();
    private final GameRules gameRules = new GameRules();
    private final SearchSystem searchSystem = new SearchSystem();
    private boolean hudTouch;

    private float downX;
    private float downY;
    private float lastX;
    private float lastY;
    private boolean dragging;
    private boolean moved;
    private long lastTapTime;
    private float lastTapX;
    private float lastTapY;

    private boolean pointer0Down;
    private boolean pointer1Down;
    private boolean pinching;
    private float pointer0X;
    private float pointer0Y;
    private float pointer1X;
    private float pointer1Y;
    private float pinchDistance;

    public GameScreen() {
        cameraController.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hud.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraController.snapTo(player.getX(), player.getY());
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (pointer == 0 && hud.isHudTouch(screenY)) {
                    hudTouch = true;
                    return true;
                }
                if (pointer == 0) {
                    pointer0Down = true;
                    pointer0X = screenX;
                    pointer0Y = screenY;
                    downX = lastX = screenX;
                    downY = lastY = screenY;
                    dragging = false;
                    moved = false;
                } else if (pointer == 1) {
                    pointer1Down = true;
                    pointer1X = screenX;
                    pointer1Y = screenY;
                    if (pointer0Down) {
                        pinching = true;
                        lastTapTime = 0;
                        cameraController.setFollowing(false);
                        pinchDistance = distance(pointer0X, pointer0Y, pointer1X, pointer1Y);
                    }
                }
                return pointer == 0 || pointer == 1;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (hudTouch) return true;
                if (pointer == 0) {
                    pointer0X = screenX;
                    pointer0Y = screenY;
                } else if (pointer == 1) {
                    pointer1X = screenX;
                    pointer1Y = screenY;
                } else {
                    return false;
                }

                if (pointer0Down && pointer1Down) {
                    float currentDistance = distance(pointer0X, pointer0Y, pointer1X, pointer1Y);
                    if (pinchDistance > 0f && currentDistance > 0f) {
                        cameraController.setZoom(
                                cameraController.getZoom() * pinchDistance / currentDistance
                        );
                    }
                    pinchDistance = currentDistance;
                    pinching = true;
                    cameraController.setFollowing(false);
                    return true;
                }

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
                if (hudTouch) {
                    hudTouch = false;
                    return true;
                }
                if (pointer == 0) {
                    pointer0Down = false;
                } else if (pointer == 1) {
                    pointer1Down = false;
                } else {
                    return false;
                }

                if (pinching) {
                    if (!pointer0Down && !pointer1Down) {
                        pinching = false;
                        pinchDistance = 0f;
                    }
                    return true;
                }

                if (pointer == 0 && !dragging && !moved) {
                    long now = TimeUtils.millis();
                    float dx = screenX - lastTapX;
                    float dy = screenY - lastTapY;
                    if (lastTapTime != 0 && now - lastTapTime <= DOUBLE_TAP_TIME
                            && dx * dx + dy * dy <= DOUBLE_TAP_DISTANCE * DOUBLE_TAP_DISTANCE) {
                        Vector3 destination = camera.unproject(new Vector3(screenX, screenY, 0f));
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

    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public void render(float delta) {
        float dt = Math.min(delta, 0.05f);
        gameTime.update(dt);
        player.update(dt, gameRules);
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

        hud.render(player, world, gameTime, searchSystem);
    }

    @Override
    public void resize(int width, int height) {
        cameraController.resize(width, height);
        hud.resize(width, height);
    }

    @Override
    public void dispose() {
        worldRenderer.dispose();
        hud.dispose();
        atlas.dispose();
        batch.dispose();
        shapes.dispose();
        Gdx.input.setInputProcessor(null);
    }
}
