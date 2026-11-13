package io.w8.game.core.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameInput extends InputAdapter {
    private final Viewport viewport;
    private final Vector2 pointerWorld = new Vector2();
    private boolean pointerDown;

    public GameInput(Viewport viewport) {
        this.viewport = viewport;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (pointer != 0) return false;
        updatePointer(screenX, screenY);
        pointerDown = true;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (pointer != 0 || !pointerDown) return false;
        updatePointer(screenX, screenY);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer != 0) return false;
        updatePointer(screenX, screenY);
        pointerDown = false;
        return true;
    }

    private void updatePointer(int screenX, int screenY) {
        pointerWorld.set(screenX, screenY);
        viewport.unproject(pointerWorld);
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
