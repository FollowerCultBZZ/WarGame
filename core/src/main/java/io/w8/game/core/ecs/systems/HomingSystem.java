package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import io.w8.game.core.ecs.components.EnemyComponent;
import io.w8.game.core.ecs.components.ProjectileComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.VelocityComponent;

public class HomingSystem extends EntitySystem {
    private ImmutableArray<Entity> projectiles;
    private ImmutableArray<Entity> enemies;

    @Override
    public void addedToEngine(Engine engine) {
        projectiles = engine.getEntitiesFor(Family.all(ProjectileComponent.class, TransformComponent.class, VelocityComponent.class).get());
        enemies = engine.getEntitiesFor(Family.all(EnemyComponent.class, TransformComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (int i = 0; i < projectiles.size(); i++) {
            Entity projectile = projectiles.get(i);
            if (!projectile.getComponent(ProjectileComponent.class).homing || enemies.size() == 0) continue;

            TransformComponent transform = projectile.getComponent(TransformComponent.class);
            VelocityComponent velocity = projectile.getComponent(VelocityComponent.class);
            Entity nearest = null;
            float best = Float.MAX_VALUE;

            for (int j = 0; j < enemies.size(); j++) {
                Entity enemy = enemies.get(j);
                TransformComponent target = enemy.getComponent(TransformComponent.class);
                float dx = target.x - transform.x;
                float dy = target.y - transform.y;
                float distance = dx * dx + dy * dy;
                if (distance < best) {
                    best = distance;
                    nearest = enemy;
                }
            }

            if (nearest != null) {
                TransformComponent target = nearest.getComponent(TransformComponent.class);
                float dx = target.x - transform.x;
                float dy = target.y - transform.y;
                float length = (float) Math.sqrt(dx * dx + dy * dy);
                if (length > 0.001f) {
                    float speed = 900f;
                    float desiredX = dx / length * speed;
                    float desiredY = dy / length * speed;
                    velocity.x += (desiredX - velocity.x) * Math.min(1f, deltaTime * 4.5f);
                    velocity.y += (desiredY - velocity.y) * Math.min(1f, deltaTime * 4.5f);
                }
            }
        }
    }
}
