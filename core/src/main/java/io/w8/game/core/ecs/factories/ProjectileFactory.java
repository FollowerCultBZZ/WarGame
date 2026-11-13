package io.w8.game.core.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import io.w8.game.core.ecs.components.ProjectileComponent;
import io.w8.game.core.ecs.components.ShapeComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.VelocityComponent;

public class ProjectileFactory {
    private final Engine engine;

    public ProjectileFactory(Engine engine) {
        this.engine = engine;
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
        projectile.damage = 4f;
        projectile.splashRadius = 78f;
        projectile.remainingLife = 5f;
        ShapeComponent shape = entity.getComponent(ShapeComponent.class);
        shape.type = ShapeComponent.RECTANGLE;
        shape.radius = 13f;
        shape.red = 0.95f;
        shape.green = 0.35f;
        shape.blue = 1f;
        return entity;
    }

    public Entity createUltimateRocket(float x, float y) {
        Entity entity = createRocket(x, y);
        ProjectileComponent projectile = entity.getComponent(ProjectileComponent.class);
        projectile.damage = 6f;
        projectile.splashRadius = 112f;
        VelocityComponent velocity = entity.getComponent(VelocityComponent.class);
        velocity.x = 0f;
        velocity.y = 820f;
        velocity.maxSpeed = 820f;
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
}
