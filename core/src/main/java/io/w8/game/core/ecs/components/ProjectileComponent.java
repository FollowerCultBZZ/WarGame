package io.w8.game.core.ecs.components;

import com.badlogic.ashley.core.Component;

public class ProjectileComponent implements Component {
    public float remainingLife = 2.5f;
    public float damage = 1f;
    public float trailTimer;
    public int pierceRemaining;
    public boolean homing;
    public boolean enemyOwned;
    public boolean rocket;
}
