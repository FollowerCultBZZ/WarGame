package io.w8.game.core.ecs.components;

import com.badlogic.ashley.core.Component;

public class EnemyComponent implements Component {
    public float health = 2f;
    public float speed = 90f;
    public float contactDamage = 12f;
    public float radius = 16f;
    public int kind;
}
