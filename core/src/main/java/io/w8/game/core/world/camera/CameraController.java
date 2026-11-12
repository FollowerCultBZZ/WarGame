package io.w8.game.core.world.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.w8.game.core.world.map.MapWorld;
import io.w8.game.core.world.player.Player;

public class CameraController {

    private static final float FOLLOW_SPEED = 7f;
    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 5f;
    private static final float INITIAL_ZOOM = 2f;

    private final OrthographicCamera camera;
    private final MapWorld world;
    private boolean following = true;

    public CameraController(OrthographicCamera camera, MapWorld world) {
        this.camera = camera;
        this.world = world;
        camera.zoom = INITIAL_ZOOM;
    }

    public void resize(int width, int height) {
        camera.setToOrtho(true, Math.max(width, 1), Math.max(height, 1));
        camera.zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, camera.zoom));
        clamp();
        camera.update();
    }

    public void snapTo(float x, float y) {
        camera.position.set(x, y, 0f);
        clamp();
        camera.update();
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public void setZoom(float zoom) {
        camera.zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom));
        clamp();
        camera.update();
    }

    public float getZoom() {
        return camera.zoom;
    }

    public void pan(float screenDeltaX, float screenDeltaY) {
        float unitsPerPixelX = camera.viewportWidth * camera.zoom
                / Math.max(1f, Gdx.graphics.getWidth());
        float unitsPerPixelY = camera.viewportHeight * camera.zoom
                / Math.max(1f, Gdx.graphics.getHeight());
        camera.position.add(-screenDeltaX * unitsPerPixelX, -screenDeltaY * unitsPerPixelY, 0f);
        clamp();
        camera.update();
    }

    public void update(float delta, Player player) {
        if (following) {
            float alpha = 1f - (float) Math.exp(-FOLLOW_SPEED * delta);
            camera.position.x += (player.getX() - camera.position.x) * alpha;
            camera.position.y += (player.getY() - camera.position.y) * alpha;
        }
        clamp();
        camera.update();
    }

    private void clamp() {
        float visibleWidth = camera.viewportWidth * camera.zoom;
        float visibleHeight = camera.viewportHeight * camera.zoom;
        float halfWidth = visibleWidth * 0.5f;
        float halfHeight = visibleHeight * 0.5f;
        float worldWidth = world.getWorldWidth();
        float worldHeight = world.getWorldHeight();

        camera.position.x = worldWidth <= visibleWidth
                ? worldWidth * 0.5f
                : Math.max(halfWidth, Math.min(worldWidth - halfWidth, camera.position.x));
        camera.position.y = worldHeight <= visibleHeight
                ? worldHeight * 0.5f
                : Math.max(halfHeight, Math.min(worldHeight - halfHeight, camera.position.y));
    }
}
