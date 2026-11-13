package io.w8.game.core.ecs.components;

import com.badlogic.ashley.core.Component;

public class ProjectileComponent implements Component {
    public float remainingLife = 1.8f;
    public float damage = 1f;
    public int pierceRemaining;
    public boolean homing;
}
