package io.w8.game.core.world;

public class Arena {
    private float width;
    private float height;
    private float margin = 28f;

    public void resize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getMinX(float radius) {
        return margin + radius;
    }

    public float getMaxX(float radius) {
        return Math.max(getMinX(radius), width - margin - radius);
    }

    public float getMinY(float radius) {
        return margin + radius;
    }

    public float getMaxY(float radius) {
        return Math.max(getMinY(radius), height - margin - radius);
    }

    public float clampX(float x, float radius) {
        return Math.max(getMinX(radius), Math.min(getMaxX(radius), x));
    }

    public float clampY(float y, float radius) {
        return Math.max(getMinY(radius), Math.min(getMaxY(radius), y));
    }
}
