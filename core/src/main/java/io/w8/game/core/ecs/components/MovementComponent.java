package io.w8.game.core.ecs.components;

import com.badlogic.ashley.core.Component;

public class MovementComponent implements Component {
    public float targetX;
    public float targetY;
    public float speed = 220f;
    public boolean moving;

    public void moveTo(float x, float y) {
        targetX = x;
        targetY = y;
        moving = true;
    }

    public void stop() {
        moving = false;
    }
}
