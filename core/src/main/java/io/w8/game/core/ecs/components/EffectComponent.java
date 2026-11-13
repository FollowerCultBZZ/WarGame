package io.w8.game.core.ecs.components;

import com.badlogic.ashley.core.Component;

public class EffectComponent implements Component {
    public float age;
    public float duration = 0.32f;
    public float radius = 10f;
    public float red = 1f;
    public float green = 0.65f;
    public float blue = 0.2f;
}
