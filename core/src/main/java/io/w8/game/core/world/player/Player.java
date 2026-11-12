package io.w8.game.core.world.player;

public class Player {

    private static final float MOVE_SPEED = 220f;

    private float x;
    private float y;
    private float targetX;
    private float targetY;
    private boolean moving;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        targetX = x;
        targetY = y;
    }

    public void moveTo(float x, float y) {
        targetX = x;
        targetY = y;
        moving = true;
    }

    public void update(float delta, float worldWidth, float worldHeight) {
        if (!moving) return;

        float dx = targetX - x;
        float dy = targetY - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float step = MOVE_SPEED * delta;

        if (distance <= step || distance < 0.5f) {
            x = targetX;
            y = targetY;
            moving = false;
            return;
        }

        x += dx / distance * step;
        y += dy / distance * step;
        x = Math.max(0f, Math.min(worldWidth, x));
        y = Math.max(0f, Math.min(worldHeight, y));
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getTargetX() { return targetX; }
    public float getTargetY() { return targetY; }
    public boolean isMoving() { return moving; }
}
