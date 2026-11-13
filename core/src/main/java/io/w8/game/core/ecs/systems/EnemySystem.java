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
    private float spawnTimer = 0.65f;
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
        float difficulty = Math.min(20f, elapsed / 24f);
        spawnTimer -= delta;

        if (spawnTimer <= 0f && players.size() > 0) {
            int roll = MathUtils.random(0, 99);
            int kind = roll < 28 ? 0 : roll < 55 ? 1 : 2;
            float x = MathUtils.random(45f, Math.max(46f, arena.getWidth() - 45f));
            factory.createEnemy(x, arena.getHeight() - 42f, kind, difficulty);
            spawnTimer = Math.max(0.22f, 1.15f - elapsed * 0.014f);
        }

        if (players.size() == 0) return;
        TransformComponent target = players.first().getComponent(TransformComponent.class);
        float shooterFloor = arena.getHeight() * 0.62f;

        for (int i = 0; i < enemies.size(); i++) {
            Entity entity = enemies.get(i);
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            VelocityComponent velocity = entity.getComponent(VelocityComponent.class);
            EnemyComponent enemy = entity.getComponent(EnemyComponent.class);
            float dx = target.x - transform.x;
            float dy = target.y - transform.y;
            float length = (float) Math.sqrt(dx * dx + dy * dy);

            if (enemy.kind == 0) {
                float sway = MathUtils.sin(elapsed * 1.8f + enemy.strafePhase);
                velocity.x = sway * enemy.speed * 1.8f;
                velocity.y = transform.y > shooterFloor ? -enemy.speed * 0.25f : 0f;
                if (transform.y < shooterFloor) {
                    transform.y = shooterFloor;
                    velocity.y = 0f;
                }
            } else if (length > 0.001f) {
                float nx = dx / length;
                float ny = dy / length;
                if (enemy.kind == 1) {
                    velocity.x = nx * enemy.speed;
                    velocity.y = ny * enemy.speed;
                } else {
                    float strafe = MathUtils.sin(elapsed * 5.2f + enemy.strafePhase) * enemy.speed * 0.78f;
                    velocity.x = nx * enemy.speed - ny * strafe;
                    velocity.y = ny * enemy.speed + nx * strafe;
                }
            }

            if (enemy.kind == 0) {
                enemy.fireTimer -= delta;
                if (enemy.fireTimer <= 0f && transform.y >= shooterFloor) {
                    float fireSpeed = 300f + difficulty * 11f;
                    factory.createEnemyProjectile(transform.x, transform.y - enemy.radius,
                            target.x, target.y, fireSpeed, 11f + difficulty * 0.7f);
                    enemy.fireTimer = Math.max(0.72f, enemy.fireInterval - difficulty * 0.035f);
                }
            }
        }
    }
}
