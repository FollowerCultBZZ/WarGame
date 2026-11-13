package io.w8.game.core.ecs.components;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
    public float health = 100f;
    public float maxHealth = 100f;
    public float damageCooldown;
    public int extraProjectiles;
    public int homingLevels;
    public int piercingLevels;
    public float fireRateMultiplier = 1f;
    public float speedMultiplier = 1f;
    public float rocketTimer = 1.2f;
    public int score;
}
