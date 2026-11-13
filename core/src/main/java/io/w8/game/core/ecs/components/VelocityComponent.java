package io.w8.game.core.ecs.components;

import com.badlogic.ashley.core.Component;

public class VelocityComponent implements Component {
    public float x;
    public float y;
    public float maxSpeed = 520f;
    public float acceleration = 1800f;
}
