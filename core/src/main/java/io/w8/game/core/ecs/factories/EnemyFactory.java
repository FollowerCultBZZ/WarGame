package io.w8.game.core.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import io.w8.game.core.ecs.components.EnemyComponent;
import io.w8.game.core.ecs.components.ShapeComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.VelocityComponent;

public class EnemyFactory {
    private final Engine engine;

    public EnemyFactory(Engine engine) {
        this.engine = engine;
    }

    public Entity createEnemy(float x, float y, int kind, float difficulty) {
        Entity entity = engine.createEntity();
        EnemyComponent enemy = new EnemyComponent();
        enemy.kind = kind;
        ShapeComponent shape = new ShapeComponent();

        if (kind == 0) {
            enemy.health = 6.5f + difficulty * 1.25f;
            enemy.speed = 58f + difficulty * 2.2f;
            enemy.contactDamage = 24f + difficulty * 1.0f;
            enemy.radius = 23f;
            enemy.fireInterval = Math.max(0.48f, 1.35f - difficulty * 0.045f);
            shape.type = ShapeComponent.CIRCLE;
            shape.radius = 23f;
            shape.red = 0.35f;
            shape.green = 0.72f;
            shape.blue = 1f;
        } else if (kind == 1) {
            enemy.health = 15f + difficulty * 2.2f;
            enemy.speed = 118f + difficulty * 4.8f;
            enemy.contactDamage = 42f + difficulty * 1.8f;
            enemy.radius = 31f;
            enemy.fireInterval = 999f;
            shape.type = ShapeComponent.RECTANGLE;
            shape.radius = 31f;
            shape.red = 0.72f;
            shape.green = 0.24f;
            shape.blue = 0.18f;
        } else {
            enemy.health = 4.5f + difficulty * 0.8f;
            enemy.speed = 235f + difficulty * 9f;
            enemy.contactDamage = 20f + difficulty * 0.9f;
            enemy.radius = 17f;
            enemy.fireInterval = 999f;
            shape.type = ShapeComponent.TRIANGLE;
            shape.radius = 17f;
            shape.red = 1f;
            shape.green = 0.62f;
            shape.blue = 0.16f;
        }
        enemy.maxHealth = enemy.health;
        enemy.strafePhase = (float) (Math.random() * Math.PI * 2f);
        enemy.fireTimer = enemy.kind == 0 ? enemy.fireInterval * 0.45f : 999f;
        entity.add(new TransformComponent(x, y));
        entity.add(new VelocityComponent());
        entity.add(enemy);
        entity.add(shape);
        engine.addEntity(entity);
        return entity;
    }

    public Entity createBoss(float x, float y, int scoreMilestone) {
        Entity entity = engine.createEntity();
        EnemyComponent enemy = new EnemyComponent();
        enemy.kind = 3;
        enemy.bossNumber = Math.max(1, scoreMilestone / 10000);
        enemy.maxHealth = 650f + (scoreMilestone / 10000) * 260f;
        enemy.health = enemy.maxHealth;
        enemy.speed = 125f + (scoreMilestone / 10000) * 8f;
        enemy.contactDamage = 65f;
        enemy.radius = 66f;
        enemy.fireInterval = Math.max(0.55f, 1.25f - (scoreMilestone / 10000) * 0.06f);
        enemy.bossAttackTimer = 1.2f;
        enemy.strafePhase = (float) (Math.random() * Math.PI * 2f);

        ShapeComponent shape = new ShapeComponent();
        shape.type = ShapeComponent.CIRCLE;
        shape.radius = enemy.radius;
        shape.red = 0.82f;
        shape.green = 0.08f;
        shape.blue = 0.16f;

        entity.add(new TransformComponent(x, y));
        entity.add(new VelocityComponent());
        entity.add(enemy);
        entity.add(shape);
        engine.addEntity(entity);
        return entity;
    }
}
