package io.w8.game.core.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
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
        velocity.maxSpeed = 920f;
        velocity.acceleration = 4200f;

        ShapeComponent shape = new ShapeComponent();
        shape.type = ShapeComponent.TRIANGLE;
        shape.radius = 28f;
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

    public void createEffect(float x, float y, float red, float green, float blue, float radius) {
        Entity entity = engine.createEntity();
        EffectComponent effect = new EffectComponent();
        effect.startRed = red;
        effect.startGreen = green;
        effect.startBlue = blue;
        int particleCount = MathUtils.random(12, MathUtils.clamp((int) (radius * 0.45f), 16, 30));
        float speedScale = MathUtils.clamp(radius / 34f, 0.7f, 2.2f);

        for (int i = 0; i < particleCount; i++) {
            float angle = MathUtils.random(0f, 360f);
            float speed = MathUtils.random(85f, 245f) * speedScale;
            EffectComponent.Particle particle = new EffectComponent.Particle();
            particle.x = x;
            particle.y = y;
            particle.velocityX = MathUtils.cosDeg(angle) * speed;
            particle.velocityY = MathUtils.sinDeg(angle) * speed;
            particle.size = MathUtils.random(2.5f, 6.5f) * MathUtils.clamp(speedScale, 0.8f, 1.35f);
            particle.lifetime = MathUtils.random(0.58f, 0.95f);
            effect.particles.add(particle);
        }

        entity.add(new TransformComponent(x, y));
        entity.add(effect);
        engine.addEntity(entity);
    }
}
