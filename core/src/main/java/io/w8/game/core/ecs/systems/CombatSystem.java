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
import io.w8.game.core.ecs.components.ShapeComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.VelocityComponent;
import io.w8.game.core.ecs.factories.EntityFactory;
import io.w8.game.core.world.Arena;

public class CombatSystem extends EntitySystem {
    private Engine engine;
    private EntityFactory factory;
    private final Arena arena;
    private ImmutableArray<Entity> players;
    private ImmutableArray<Entity> enemies;
    private ImmutableArray<Entity> projectiles;
    private final Array<Entity> remove = new Array<>();

    public CombatSystem(Arena arena) {
        this.arena = arena;
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
        factory = new EntityFactory(engine);
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class, VelocityComponent.class).get());
        enemies = engine.getEntitiesFor(Family.all(EnemyComponent.class, TransformComponent.class).get());
        projectiles = engine.getEntitiesFor(Family.all(ProjectileComponent.class, TransformComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        if (players.size() == 0 || players.first().getComponent(PlayerComponent.class).dead) return;
        remove.clear();

        for (int i = 0; i < players.size(); i++) {
            PlayerComponent player = players.get(i).getComponent(PlayerComponent.class);
            player.damageCooldown = Math.max(0f, player.damageCooldown - deltaTime);
            player.invulnerabilityTimer = Math.max(0f, player.invulnerabilityTimer - deltaTime);
        }

        for (int i = 0; i < projectiles.size(); i++) {
            Entity shot = projectiles.get(i);
            if (remove.contains(shot, true)) continue;
            TransformComponent shotTransform = shot.getComponent(TransformComponent.class);
            ProjectileComponent projectile = shot.getComponent(ProjectileComponent.class);

            if (projectile.enemyOwned) {
                for (int p = 0; p < players.size(); p++) {
                    Entity playerEntity = players.get(p);
                    PlayerComponent player = playerEntity.getComponent(PlayerComponent.class);
                    if (player.dead) continue;
                    TransformComponent playerTransform = playerEntity.getComponent(TransformComponent.class);
                    float dx = shotTransform.x - playerTransform.x;
                    float dy = shotTransform.y - playerTransform.y;
                    float hitRadius = entityRadius(playerEntity, 24f) + entityRadius(shot, 7f);
                    if (dx * dx + dy * dy <= hitRadius * hitRadius) {
                        if (player.invulnerabilityTimer <= 0f && player.damageCooldown <= 0f) {
                            damagePlayer(playerEntity, projectile.damage, 0.35f);
                        }
                        remove.add(shot);
                        break;
                    }
                }
                continue;
            }

            for (int j = 0; j < enemies.size(); j++) {
                Entity enemyEntity = enemies.get(j);
                if (remove.contains(enemyEntity, true)) continue;
                TransformComponent enemyTransform = enemyEntity.getComponent(TransformComponent.class);
                EnemyComponent enemy = enemyEntity.getComponent(EnemyComponent.class);
                float dx = shotTransform.x - enemyTransform.x;
                float dy = shotTransform.y - enemyTransform.y;
                float hitRadius = enemy.radius + entityRadius(shot, projectile.rocket ? 13f : 6f);

                if (dx * dx + dy * dy <= hitRadius * hitRadius) {
                    float splash = projectile.splashRadius;
                    for (int k = 0; k < enemies.size(); k++) {
                        Entity affected = enemies.get(k);
                        if (remove.contains(affected, true)) continue;
                        TransformComponent affectedTransform = affected.getComponent(TransformComponent.class);
                        EnemyComponent affectedEnemy = affected.getComponent(EnemyComponent.class);
                        float sx = affectedTransform.x - shotTransform.x;
                        float sy = affectedTransform.y - shotTransform.y;
                        boolean inBlast = splash > 0f && sx * sx + sy * sy <= splash * splash;
                        if (affected != enemyEntity && !inBlast) continue;
                        affectedEnemy.health -= projectile.damage;
                        if (affectedEnemy.health <= 0f) {
                            remove.add(affected);
                            if (players.size() > 0) {
                                PlayerComponent scorer = players.first().getComponent(PlayerComponent.class);
                                scorer.score += affectedEnemy.kind == 3 ? 2500
                                        : affectedEnemy.kind == 1 ? 400
                                        : affectedEnemy.kind == 0 ? 275 : 200;
                            }
                            factory.createEffect(affectedTransform.x, affectedTransform.y,
                                    1f, affectedEnemy.kind == 3 ? 0.12f : 0.25f,
                                    0.18f, affectedEnemy.radius * (affectedEnemy.kind == 3 ? 2.5f : 1.8f));
                            if (affectedEnemy.kind != 3 && MathUtils.randomBoolean(0.42f)) {
                                int[] pickupTypes = {
                                        io.w8.game.core.ecs.components.PickupComponent.HEAL,
                                        io.w8.game.core.ecs.components.PickupComponent.HOMING,
                                        io.w8.game.core.ecs.components.PickupComponent.FIRE_RATE,
                                        io.w8.game.core.ecs.components.PickupComponent.MULTI_SHOT
                                };
                                factory.createPickup(affectedTransform.x, affectedTransform.y,
                                        pickupTypes[MathUtils.random(pickupTypes.length - 1)]);
                            }
                        }
                    }
                    factory.createEffect(shotTransform.x, shotTransform.y,
                            1f, 0.55f, 0.2f, splash > 0f ? splash : 12f);
                    if (projectile.pierceRemaining > 0) projectile.pierceRemaining--;
                    else remove.add(shot);
                    break;
                }
            }
        }

        for (int i = 0; i < players.size(); i++) {
            Entity playerEntity = players.get(i);
            PlayerComponent player = playerEntity.getComponent(PlayerComponent.class);
            if (player.dead || player.invulnerabilityTimer > 0f || player.damageCooldown > 0f) continue;
            TransformComponent playerTransform = playerEntity.getComponent(TransformComponent.class);

            for (int j = 0; j < enemies.size(); j++) {
                Entity enemyEntity = enemies.get(j);
                if (remove.contains(enemyEntity, true)) continue;
                TransformComponent enemyTransform = enemyEntity.getComponent(TransformComponent.class);
                EnemyComponent enemy = enemyEntity.getComponent(EnemyComponent.class);
                float dx = playerTransform.x - enemyTransform.x;
                float dy = playerTransform.y - enemyTransform.y;
                float radius = enemy.radius + entityRadius(playerEntity, 24f);
                if (dx * dx + dy * dy <= radius * radius) {
                    damagePlayer(playerEntity, enemy.contactDamage, 0.65f);
                    enemy.health -= 1f;
                    factory.createEffect(playerTransform.x, playerTransform.y, 1f, 0.15f, 0.12f, 30f);
                    if (enemy.health <= 0f) {
                        remove.add(enemyEntity);
                        player.score += enemy.kind == 3 ? 2500
                                : enemy.kind == 1 ? 400 : enemy.kind == 0 ? 275 : 200;
                        factory.createEffect(enemyTransform.x, enemyTransform.y,
                                1f, 0.3f, 0.12f, enemy.radius * 1.6f);
                    }
                    break;
                }
            }
        }

        for (int i = 0; i < remove.size; i++) engine.removeEntity(remove.get(i));
    }

    private float entityRadius(Entity entity, float fallback) {
        ShapeComponent shape = entity.getComponent(ShapeComponent.class);
        return shape == null ? fallback : shape.radius;
    }

    private void damagePlayer(Entity entity, float damage, float cooldown) {
        PlayerComponent player = entity.getComponent(PlayerComponent.class);
        if (player.dead || player.invulnerabilityTimer > 0f || player.damageCooldown > 0f) return;

        TransformComponent transform = entity.getComponent(TransformComponent.class);
        VelocityComponent velocity = entity.getComponent(VelocityComponent.class);
        player.health = Math.max(0f, player.health - damage);
        player.damageCooldown = cooldown;
        factory.createEffect(transform.x, transform.y, 1f, 0.2f, 0.12f, 28f);

        if (player.health > 0f) return;

        player.lives = Math.max(0, player.lives - 1);
        if (player.lives == 0) {
            player.dead = true;
            player.health = 0f;
            return;
        }

        player.health = player.maxHealth;
        player.invulnerabilityTimer = 5f;
        player.damageCooldown = 0.6f;
        transform.x = arena.getWidth() * 0.5f;
        transform.y = arena.getHeight() * 0.28f;
        velocity.x = 0f;
        velocity.y = 0f;
        factory.createEffect(transform.x, transform.y, 0.2f, 0.85f, 1f, 100f);
    }
}
