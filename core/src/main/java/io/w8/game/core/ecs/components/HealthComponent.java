package io.w8.game.core.ecs.components;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {
    public float current;
    public float maximum;

    public HealthComponent() {
    }

    public HealthComponent(float maximum) {
        this.maximum = maximum;
        this.current = maximum;
    }

    public void damage(float amount) {
        current = Math.max(0f, current - Math.max(0f, amount));
    }

    public void heal(float amount) {
        current = Math.min(maximum, current + Math.max(0f, amount));
    }

    public boolean isDead() {
        return current <= 0f;
    }
}
