package io.w8.game.core.world.player;

import io.w8.game.core.world.map.MapWorld;

public class Player {

    private static final float MOVE_SPEED = 220f;
    private static final float COLLISION_RADIUS = 17f;
    private static final float MAX_STEP = 8f;

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

    public void update(float delta, MapWorld world) {
        if (!moving) return;

        float dx = targetX - x;
        float dy = targetY - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < 0.5f) {
            moving = false;
            return;
        }

        float speed = MOVE_SPEED * world.getMovementSpeedMultiplier(x, y);
        float remaining = Math.min(speed * delta, distance);
        int steps = Math.max(1, (int) Math.ceil(remaining / MAX_STEP));
        float stepX = dx / distance * remaining / steps;
        float stepY = dy / distance * remaining / steps;

        for (int i = 0; i < steps; i++) {
            boolean movedX = false;
            boolean movedY = false;

            if (!world.isBlocked(x + stepX, y, COLLISION_RADIUS)) {
                x += stepX;
                movedX = true;
            }

            if (!world.isBlocked(x, y + stepY, COLLISION_RADIUS)) {
                y += stepY;
                movedY = true;
            }

            if (!movedX && !movedY) {
                moving = false;
                return;
            }
        }

        if (Math.abs(targetX - x) < 0.5f && Math.abs(targetY - y) < 0.5f) {
            x = targetX;
            y = targetY;
            moving = false;
        }
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getTargetX() { return targetX; }
    public float getTargetY() { return targetY; }
    public boolean isMoving() { return moving; }
}
