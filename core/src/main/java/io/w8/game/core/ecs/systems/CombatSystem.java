package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import io.w8.game.core.ecs.components.EnemyComponent;
import io.w8.game.core.ecs.components.PlayerComponent;
import io.w8.game.core.ecs.components.ProjectileComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.factories.EntityFactory;

public class CombatSystem extends EntitySystem {
    private Engine engine;
    private EntityFactory factory;
    private ImmutableArray<Entity> players;
    private ImmutableArray<Entity> enemies;
    private ImmutableArray<Entity> projectiles;
    private final Array<Entity> remove = new Array<>();

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
        factory = new EntityFactory(engine);
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class).get());
        enemies = engine.getEntitiesFor(Family.all(EnemyComponent.class, TransformComponent.class).get());
        projectiles = engine.getEntitiesFor(Family.all(ProjectileComponent.class, TransformComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        remove.clear();

        for (int i = 0; i < projectiles.size(); i++) {
            Entity shot = projectiles.get(i);
            if (remove.contains(shot, true)) continue;
            TransformComponent shotTransform = shot.getComponent(TransformComponent.class);
            ProjectileComponent projectile = shot.getComponent(ProjectileComponent.class);

            for (int j = 0; j < enemies.size(); j++) {
                Entity enemyEntity = enemies.get(j);
                if (remove.contains(enemyEntity, true)) continue;
                TransformComponent enemyTransform = enemyEntity.getComponent(TransformComponent.class);
                EnemyComponent enemy = enemyEntity.getComponent(EnemyComponent.class);
                float dx = shotTransform.x - enemyTransform.x;
                float dy = shotTransform.y - enemyTransform.y;
                float radius = enemy.radius + 8f;

                if (dx * dx + dy * dy <= radius * radius) {
                    enemy.health -= projectile.damage;
                    if (enemy.health <= 0f) {
                        remove.add(enemyEntity);
                        if (MathUtils.randomBoolean(0.42f)) {
                            int type = MathUtils.random(0, 5);
                            factory.createPickup(enemyTransform.x, enemyTransform.y, type);
                        }
                    }
                    if (projectile.pierceRemaining > 0) {
                        projectile.pierceRemaining--;
                    } else {
                        remove.add(shot);
                    }
                    break;
                }
            }
        }

        for (int i = 0; i < players.size(); i++) {
            Entity playerEntity = players.get(i);
            PlayerComponent player = playerEntity.getComponent(PlayerComponent.class);
            TransformComponent playerTransform = playerEntity.getComponent(TransformComponent.class);
            player.damageCooldown = Math.max(0f, player.damageCooldown - deltaTime);

            for (int j = 0; j < enemies.size(); j++) {
                Entity enemyEntity = enemies.get(j);
                if (remove.contains(enemyEntity, true)) continue;
                TransformComponent enemyTransform = enemyEntity.getComponent(TransformComponent.class);
                EnemyComponent enemy = enemyEntity.getComponent(EnemyComponent.class);
                float dx = playerTransform.x - enemyTransform.x;
                float dy = playerTransform.y - enemyTransform.y;
                float radius = enemy.radius + 18f;

                if (dx * dx + dy * dy <= radius * radius && player.damageCooldown <= 0f) {
                    player.health = Math.max(0f, player.health - enemy.contactDamage);
                    player.damageCooldown = 0.65f;
                    enemy.health -= 1f;
                    if (enemy.health <= 0f) remove.add(enemyEntity);
                    break;
                }
            }
        }

        for (int i = 0; i < remove.size; i++) engine.removeEntity(remove.get(i));
    }
}
