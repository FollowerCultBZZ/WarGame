package io.w8.game.core.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import io.w8.game.core.ecs.components.EnemyComponent;
import io.w8.game.core.ecs.components.PlayerComponent;
import io.w8.game.core.ecs.components.ProjectileComponent;
import io.w8.game.core.ecs.components.PickupComponent;
import io.w8.game.core.ecs.components.ShapeComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.VelocityComponent;
import io.w8.game.core.ecs.components.WeaponComponent;

public class EntityFactory {
    private final Engine engine;

    public EntityFactory(Engine engine) {
        this.engine = engine;
    }

    public Entity createPlayer(float x, float y) {
        Entity entity = engine.createEntity();
        TransformComponent transform = new TransformComponent(x, y);
        VelocityComponent velocity = new VelocityComponent();
        velocity.maxSpeed = 620f;
        velocity.acceleration = 2100f;

        ShapeComponent shape = new ShapeComponent();
        shape.type = ShapeComponent.TRIANGLE;
        shape.radius = 18f;
        shape.red = 0.2f;
        shape.green = 0.85f;
        shape.blue = 1f;

        entity.add(transform);
        entity.add(velocity);
        entity.add(new PlayerComponent());
        entity.add(new WeaponComponent());
        entity.add(shape);
        engine.addEntity(entity);
        return entity;
    }

    public Entity createEnemy(float x, float y, int kind, float difficulty) {
        Entity entity = engine.createEntity();
        EnemyComponent enemy = new EnemyComponent();
        enemy.kind = kind;
        ShapeComponent shape = new ShapeComponent();

        if (kind == 1) {
            enemy.health = 5f + difficulty;
            enemy.speed = 58f + difficulty * 2f;
            enemy.contactDamage = 22f;
            enemy.radius = 23f;
            shape.type = ShapeComponent.RECTANGLE;
            shape.radius = 23f;
            shape.red = 0.75f;
            shape.green = 0.22f;
            shape.blue = 0.35f;
        } else if (kind == 2) {
            enemy.health = 1.5f + difficulty * 0.25f;
            enemy.speed = 145f + difficulty * 3f;
            enemy.contactDamage = 9f;
            enemy.radius = 13f;
            shape.type = ShapeComponent.TRIANGLE;
            shape.radius = 14f;
            shape.red = 1f;
            shape.green = 0.55f;
            shape.blue = 0.18f;
        } else {
            enemy.health = 2.5f + difficulty * 0.5f;
            enemy.speed = 88f + difficulty * 2f;
            enemy.contactDamage = 13f;
            enemy.radius = 17f;
            shape.type = ShapeComponent.CIRCLE;
            shape.radius = 17f;
            shape.red = 1f;
            shape.green = 0.2f;
            shape.blue = 0.28f;
        }

        entity.add(new TransformComponent(x, y));
        entity.add(new VelocityComponent());
        entity.add(enemy);
        entity.add(shape);
        engine.addEntity(entity);
        return entity;
    }

    public Entity createPickup(float x, float y, int type) {
        Entity entity = engine.createEntity();
        ShapeComponent shape = new ShapeComponent();
        shape.type = type <= PickupComponent.HEAL
                ? ShapeComponent.CIRCLE : ShapeComponent.SQUARE;
        shape.radius = 12f;

        if (type == PickupComponent.SPEED) {
            shape.red = 0.2f; shape.green = 0.9f; shape.blue = 1f;
        } else if (type == PickupComponent.MULTI_SHOT) {
            shape.red = 0.3f; shape.green = 1f; shape.blue = 0.35f;
        } else if (type == PickupComponent.FIRE_RATE) {
            shape.red = 1f; shape.green = 0.85f; shape.blue = 0.15f;
        } else if (type == PickupComponent.HEAL) {
            shape.red = 1f; shape.green = 0.25f; shape.blue = 0.45f;
        } else if (type == PickupComponent.HOMING) {
            shape.red = 0.85f; shape.green = 0.35f; shape.blue = 1f;
        } else {
            shape.red = 1f; shape.green = 0.55f; shape.blue = 0.12f;
        }

        entity.add(new TransformComponent(x, y));
        entity.add(new VelocityComponent());
        entity.add(new PickupComponent(type));
        entity.add(shape);
        engine.addEntity(entity);
        return entity;
    }

    public Entity createProjectile(float x, float y, float offsetX,
                                   float speed, boolean homing, int piercing) {
        Entity entity = engine.createEntity();
        TransformComponent transform = new TransformComponent(x + offsetX, y);
        VelocityComponent velocity = new VelocityComponent();
        velocity.x = offsetX * 2.5f;
        velocity.y = speed;
        velocity.maxSpeed = speed;
        velocity.acceleration = 0f;

        ShapeComponent shape = new ShapeComponent();
        shape.type = homing ? ShapeComponent.TRIANGLE : ShapeComponent.RECTANGLE;
        shape.radius = homing ? 9f : 6f;
        shape.red = homing ? 0.95f : 0.25f;
        shape.green = homing ? 0.45f : 0.9f;
        shape.blue = 1f;

        ProjectileComponent projectile = new ProjectileComponent();
        projectile.homing = homing;
        projectile.pierceRemaining = piercing;

        entity.add(transform);
        entity.add(velocity);
        entity.add(shape);
        entity.add(projectile);
        engine.addEntity(entity);
        return entity;
    }
}
