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
import io.w8.game.core.ecs.factories.EntityFactory;

public class HomingSystem extends EntitySystem {
    private Engine engine;
    private EntityFactory factory;
    private ImmutableArray<Entity> projectiles;
    private ImmutableArray<Entity> enemies;

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
        factory = new EntityFactory(engine);
        projectiles = engine.getEntitiesFor(Family.all(ProjectileComponent.class, TransformComponent.class, VelocityComponent.class).get());
        enemies = engine.getEntitiesFor(Family.all(EnemyComponent.class, TransformComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (int i = 0; i < projectiles.size(); i++) {
            Entity entity = projectiles.get(i);
            ProjectileComponent projectile = entity.getComponent(ProjectileComponent.class);
            if (!projectile.homing) continue;

            TransformComponent transform = entity.getComponent(TransformComponent.class);
            VelocityComponent velocity = entity.getComponent(VelocityComponent.class);

            if (projectile.rocket) {
                projectile.trailTimer -= deltaTime;
                if (projectile.trailTimer <= 0f) {
                    factory.createEffect(transform.x, transform.y, 0.7f, 0.35f, 1f, 8f);
                    projectile.trailTimer = 0.075f;
                }
            }

            if (enemies.size() == 0) continue;
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
                    float speed = projectile.rocket ? 780f : 900f;
                    float desiredX = dx / length * speed;
                    float desiredY = dy / length * speed;
                    float turn = Math.min(1f, deltaTime * (projectile.rocket ? 3.8f : 4.5f));
                    velocity.x += (desiredX - velocity.x) * turn;
                    velocity.y += (desiredY - velocity.y) * turn;
                }
            }
        }
    }
}
