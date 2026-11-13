package io.w8.game.core.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import io.w8.game.core.ecs.components.EffectComponent;
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
        VelocityComponent velocity = new VelocityComponent();
        velocity.maxSpeed = 790f;
        velocity.acceleration = 2700f;

        ShapeComponent shape = new ShapeComponent();
        shape.type = ShapeComponent.TRIANGLE;
        shape.radius = 24f;
        shape.red = 0.2f;
        shape.green = 0.85f;
        shape.blue = 1f;

        entity.add(new TransformComponent(x, y));
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
            enemy.health = 7f + difficulty * 1.4f;
            enemy.speed = 62f + difficulty * 2.3f;
            enemy.contactDamage = 22f;
            enemy.radius = 25f;
            enemy.fireInterval = 2.2f;
            shape.type = ShapeComponent.RECTANGLE;
            shape.radius = 25f;
            shape.red = 0.75f;
            shape.green = 0.22f;
            shape.blue = 0.35f;
        } else if (kind == 2) {
            enemy.health = 2f + difficulty * 0.45f;
            enemy.speed = 155f + difficulty * 4f;
            enemy.contactDamage = 9f;
            enemy.radius = 15f;
            enemy.fireInterval = 3.5f;
            shape.type = ShapeComponent.TRIANGLE;
            shape.radius = 15f;
            shape.red = 1f;
            shape.green = 0.55f;
            shape.blue = 0.18f;
        } else {
            enemy.health = 3f + difficulty * 0.75f;
            enemy.speed = 94f + difficulty * 2.5f;
            enemy.contactDamage = 13f;
            enemy.radius = 18f;
            enemy.fireInterval = 2.8f;
            shape.type = ShapeComponent.CIRCLE;
            shape.radius = 18f;
            shape.red = 1f;
            shape.green = 0.2f;
            shape.blue = 0.28f;
        }

        enemy.fireTimer = enemy.fireInterval * 0.55f;
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
        shape.radius = 13f;
        shape.hollow = true;
        shape.rotationSpeed = type <= PickupComponent.HEAL ? 95f : 115f;

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

        TransformComponent transform = new TransformComponent(x, y);
        transform.rotation = (float) (Math.random() * 360f);
        VelocityComponent velocity = new VelocityComponent();
        velocity.y = -48f;
        entity.add(transform);
        entity.add(velocity);
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
        shape.radius = homing ? 10f : 6f;
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

    public Entity createRocket(float x, float y) {
        Entity entity = createProjectile(x, y, 0f, 640f, true, 0);
        ProjectileComponent projectile = entity.getComponent(ProjectileComponent.class);
        projectile.rocket = true;
        projectile.damage = 3f;
        projectile.remainingLife = 4f;
        ShapeComponent shape = entity.getComponent(ShapeComponent.class);
        shape.type = ShapeComponent.RECTANGLE;
        shape.radius = 13f;
        shape.red = 0.95f;
        shape.green = 0.35f;
        shape.blue = 1f;
        return entity;
    }

    public Entity createEnemyProjectile(float x, float y, float targetX, float targetY, float speed, float damage) {
        Entity entity = engine.createEntity();
        float dx = targetX - x;
        float dy = targetY - y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        VelocityComponent velocity = new VelocityComponent();
        if (length > 0.001f) {
            velocity.x = dx / length * speed;
            velocity.y = dy / length * speed;
        }
        velocity.maxSpeed = speed;
        velocity.acceleration = 0f;

        ShapeComponent shape = new ShapeComponent();
        shape.type = ShapeComponent.CIRCLE;
        shape.radius = 7f;
        shape.red = 1f;
        shape.green = 0.25f;
        shape.blue = 0.12f;

        ProjectileComponent projectile = new ProjectileComponent();
        projectile.enemyOwned = true;
        projectile.damage = damage;
        projectile.remainingLife = 5f;
        entity.add(new TransformComponent(x, y));
        entity.add(velocity);
        entity.add(shape);
        entity.add(projectile);
        engine.addEntity(entity);
        return entity;
    }

    public void createEffect(float x, float y, float red, float green, float blue, float radius) {
        Entity entity = engine.createEntity();
        EffectComponent effect = new EffectComponent();
        effect.red = red;
        effect.green = green;
        effect.blue = blue;
        effect.radius = radius;
        entity.add(new TransformComponent(x, y));
        entity.add(effect);
        engine.addEntity(entity);
    }
}
