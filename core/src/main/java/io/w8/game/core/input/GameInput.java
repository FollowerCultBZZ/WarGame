package io.w8.game.core.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameInput extends InputAdapter {
    private final Viewport viewport;
    private final Vector2 pointerWorld = new Vector2();
    private final Vector2 lastPointerWorld = new Vector2();
    private boolean pointerDown;
    private boolean ultimatePressed;
    private boolean gameOver;
    private boolean restartPressed;
    private float pendingDragX;
    private float pendingDragY;
    private long lastTapTime;
    private int lastTapX;
    private int lastTapY;

    public GameInput(Viewport viewport) {
        this.viewport = viewport;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (pointer != 0) return false;
        updatePointer(screenX, screenY);
        lastPointerWorld.set(pointerWorld);
        pendingDragX = 0f;
        pendingDragY = 0f;

        if (gameOver) {
            restartPressed = isInsideRestartButton(pointerWorld.x, pointerWorld.y);
            pointerDown = false;
            return true;
        }

        long now = System.currentTimeMillis();
        int tapDx = screenX - lastTapX;
        int tapDy = screenY - lastTapY;
        if (lastTapTime != 0L && now - lastTapTime <= 330L
                && tapDx * tapDx + tapDy * tapDy <= 90 * 90) {
            ultimatePressed = true;
            lastTapTime = 0L;
        } else {
            lastTapTime = now;
            lastTapX = screenX;
            lastTapY = screenY;
        }

        pointerDown = true;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (pointer != 0 || !pointerDown) return false;
        accumulateDrag(screenX, screenY);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer != 0) return false;
        if (!gameOver && pointerDown) accumulateDrag(screenX, screenY);
        pointerDown = false;
        return true;
    }

    private void accumulateDrag(int screenX, int screenY) {
        updatePointer(screenX, screenY);
        pendingDragX += pointerWorld.x - lastPointerWorld.x;
        pendingDragY += pointerWorld.y - lastPointerWorld.y;
        lastPointerWorld.set(pointerWorld);
    }

    private void updatePointer(int screenX, int screenY) {
        pointerWorld.set(screenX, screenY);
        viewport.unproject(pointerWorld);
    }

    public float consumeDragDeltaX() {
        float value = pendingDragX;
        pendingDragX = 0f;
        return value;
    }

    public float consumeDragDeltaY() {
        float value = pendingDragY;
        pendingDragY = 0f;
        return value;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        if (!gameOver) restartPressed = false;
    }

    public boolean consumeRestartPressed() {
        boolean pressed = restartPressed;
        restartPressed = false;
        return pressed;
    }

    private boolean isInsideRestartButton(float x, float y) {
        float width = 360f;
        float height = 100f;
        float left = (viewport.getWorldWidth() - width) * 0.5f;
        float bottom = viewport.getWorldHeight() * 0.5f - 170f;
        return x >= left && x <= left + width
                && y >= bottom && y <= bottom + height;
    }

    public boolean consumeUltimatePressed() {
        boolean pressed = ultimatePressed;
        ultimatePressed = false;
        return pressed;
    }

    public boolean isPointerDown() {
        return pointerDown;
    }

    public float getPointerX() {
        return pointerWorld.x;
    }

    public float getPointerY() {
        return pointerWorld.y;
    }
}
