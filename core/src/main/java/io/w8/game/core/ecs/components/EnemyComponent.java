package io.w8.game.core.ecs.components;

import com.badlogic.ashley.core.Component;

public class EnemyComponent implements Component {
    public float health = 2f;
    public float maxHealth = 2f;
    public float shieldHealth;
    public float maxShieldHealth;
    public float speed = 90f;
    public float contactDamage = 12f;
    public float radius = 16f;
    public float fireTimer;
    public float fireInterval = 2.8f;
    public int kind;
    public float strafePhase;
    public int bossStage = 1;
    public float bossAttackTimer = 1.2f;
    public float bossPatternActiveTimer;
    public float bossShotTimer;
    public float bossSweepAngle = 220f;
    public int bossSweepDirection = 1;
    public int bossRadialVolleyCount;
    public int bossNumber = 1;
    public float bossWaveRotation;
}
