package io.w8.game.core.ecs.components;

import com.badlogic.ashley.core.Component;

public class TransformComponent implements Component {
    public float x;
    public float y;
    public float rotation;
    public float scaleX = 1f;
    public float scaleY = 1f;

    public TransformComponent() {
    }

    public TransformComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
