package io.w8.game.core.ecs.components;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
    public float health = 100f;
    public float maxHealth = 100f;
    public float shieldHealth;
    public float maxShieldHealth = 50f;
    public float shieldTimer;
    public float damageCooldown;
    public float invulnerabilityTimer;
    public int lives = 3;
    public int extraProjectiles;
    public int homingLevels;
    public int rocketCount = 1;
    public int piercingLevels;
    public float fireRateMultiplier = 1f;
    public float rocketFireRateMultiplier = 1f;
    public float fireRateOverdriveTimer;
    public float multiShotOverdriveTimer;
    public float speedMultiplier = 1f;
    public float rocketTimer = 1.2f;
    public float ultimateCooldown;
    public int score;
    public int nextBossScore = 10000;
    public boolean dead;
}
