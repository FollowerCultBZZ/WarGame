package io.w8.game.core.world.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import io.w8.game.core.ui.GameHud;
import io.w8.game.core.world.camera.CameraController;
import io.w8.game.core.world.map.MapWorld;
import io.w8.game.core.world.player.Player;
import io.w8.game.core.world.rules.GameRules;
import io.w8.game.core.world.search.SearchSystem;

public class WorldInputController extends InputAdapter {

    private static final long DOUBLE_TAP_TIME = 320;
    private static final float DOUBLE_TAP_DISTANCE = 48f;
    private static final float DRAG_THRESHOLD = 9f;

    private final OrthographicCamera camera;
    private final CameraController cameraController;
    private final Player player;
    private final MapWorld world;
    private final GameHud hud;
    private final SearchSystem searchSystem;
    private final GameRules gameRules;

    private float downX;
    private float downY;
    private float lastX;
    private float lastY;
    private boolean dragging;
    private boolean moved;
    private long lastTapTime;
    private float lastTapX;
    private float lastTapY;

    private boolean hudTouch;
    private boolean pointer0Down;
    private boolean pointer1Down;
    private boolean pinching;
    private float pointer0X;
    private float pointer0Y;
    private float pointer1X;
    private float pointer1Y;
    private float pinchDistance;

    public WorldInputController(
            OrthographicCamera camera,
            CameraController cameraController,
            Player player,
            MapWorld world,
            GameHud hud,
            SearchSystem searchSystem,
            GameRules gameRules
    ) {
        this.camera = camera;
        this.cameraController = cameraController;
        this.player = player;
        this.world = world;
        this.hud = hud;
        this.searchSystem = searchSystem;
        this.gameRules = gameRules;

        Gdx.input.setInputProcessor(this);
    }

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

        if (!dragging && totalX * totalX + totalY * totalY
                > DRAG_THRESHOLD * DRAG_THRESHOLD) {
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
            if (hud.isSearchButton(screenX, screenY) && !player.isMoving()) {
                searchSystem.start(
                        world.getTerrainAt(player.getX(), player.getY()),
                        gameRules
                );
            }

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
            handleTap(screenX, screenY);
        }

        return true;
    }

    private void handleTap(int screenX, int screenY) {
        long now = TimeUtils.millis();
        float dx = screenX - lastTapX;
        float dy = screenY - lastTapY;

        if (lastTapTime != 0 && now - lastTapTime <= DOUBLE_TAP_TIME
                && dx * dx + dy * dy <= DOUBLE_TAP_DISTANCE * DOUBLE_TAP_DISTANCE) {
            Vector3 destination = camera.unproject(new Vector3(screenX, screenY, 0f));

            if (!searchSystem.isSearching()) {
                player.moveTo(destination.x, destination.y);
            }

            cameraController.setFollowing(true);
            lastTapTime = 0;
        } else {
            lastTapTime = now;
            lastTapX = screenX;
            lastTapY = screenY;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
            Gdx.app.exit();
            return true;
        }

        return false;
    }

    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
