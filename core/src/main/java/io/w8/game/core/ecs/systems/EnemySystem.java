package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import io.w8.game.core.ecs.components.EnemyComponent;
import io.w8.game.core.ecs.components.PlayerComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.VelocityComponent;
import io.w8.game.core.ecs.factories.EntityFactory;
import io.w8.game.core.world.Arena;

public class EnemySystem extends EntitySystem {
    private final Arena arena;
    private EntityFactory factory;
    private ImmutableArray<Entity> players;
    private ImmutableArray<Entity> enemies;
    private float spawnTimer = 2.2f;
    private float elapsed;

    public EnemySystem(Arena arena) {
        this.arena = arena;
    }

    @Override
    public void addedToEngine(Engine engine) {
        factory = new EntityFactory(engine);
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class).get());
        enemies = engine.getEntitiesFor(Family.all(EnemyComponent.class, TransformComponent.class, VelocityComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        float delta = Math.min(deltaTime, 0.05f);
        elapsed += delta;
        float difficulty = Math.min(20f, elapsed / 18f);
        spawnTimer -= delta;

        if (spawnTimer <= 0f && players.size() > 0) {
            int roll = MathUtils.random(0, 99);
            int kind = roll < 30 ? 0 : roll < 62 ? 1 : 2;
            float x = MathUtils.random(38f, Math.max(39f, arena.getWidth() - 38f));
            factory.createEnemy(x, arena.getHeight() - 38f, kind, difficulty);
            float baseInterval = 1.85f - difficulty * 0.065f;
            spawnTimer = Math.max(0.24f, baseInterval);
        }

        if (players.size() == 0) return;
        TransformComponent target = players.first().getComponent(TransformComponent.class);
        float shooterFloor = arena.getHeight() * 0.70f;
        float shooterCeiling = arena.getHeight() * 0.94f;

        for (int i = 0; i < enemies.size(); i++) {
            Entity entity = enemies.get(i);
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            VelocityComponent velocity = entity.getComponent(VelocityComponent.class);
            EnemyComponent enemy = entity.getComponent(EnemyComponent.class);
            float dx = target.x - transform.x;
            float dy = target.y - transform.y;
            float length = (float) Math.sqrt(dx * dx + dy * dy);

            if (enemy.kind == 0) {
                float laneY = shooterFloor + (shooterCeiling - shooterFloor) * 0.58f
                        + MathUtils.sin(elapsed * 0.7f + enemy.strafePhase) * arena.getHeight() * 0.035f;
                float sway = MathUtils.sin(elapsed * 1.45f + enemy.strafePhase);
                velocity.x = sway * enemy.speed * 1.45f;
                velocity.y = MathUtils.clamp((laneY - transform.y) * 2.4f, -enemy.speed * 0.55f, enemy.speed * 0.55f);
                if (transform.y < shooterFloor) {
                    transform.y = shooterFloor;
                    velocity.y = Math.abs(velocity.y);
                }
                if (transform.y > shooterCeiling) {
                    transform.y = shooterCeiling;
                    velocity.y = -Math.abs(velocity.y);
                }

                enemy.fireTimer -= delta;
                if (enemy.fireTimer <= 0f && transform.y >= shooterFloor) {
                    float fireSpeed = 360f + difficulty * 18f;
                    factory.createEnemyProjectile(transform.x, transform.y - enemy.radius,
                            target.x, target.y, fireSpeed, 16f + difficulty * 1.1f);
                    enemy.fireTimer = Math.max(0.48f, enemy.fireInterval - difficulty * 0.045f);
                }
            } else if (length > 0.001f) {
                float nx = dx / length;
                float ny = dy / length;
                if (enemy.kind == 1) {
                    velocity.x = nx * enemy.speed;
                    velocity.y = ny * enemy.speed;
                } else {
                    float weave = MathUtils.sin(elapsed * (6.5f + difficulty * 0.12f) + enemy.strafePhase);
                    float strafe = weave * enemy.speed * 1.35f;
                    float forward = enemy.speed * 0.92f;
                    velocity.x = nx * forward - ny * strafe;
                    velocity.y = ny * forward + nx * strafe;
                }
            }
        }
    }
}
