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
        if (players.size() == 0) return;

        PlayerComponent player = players.first().getComponent(PlayerComponent.class);
        if (player.dead) return;

        float difficulty = Math.min(30f, Math.max(elapsed / 18f, player.score / 1800f));
        boolean bossAlive = false;
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).getComponent(EnemyComponent.class).kind == 3) {
                bossAlive = true;
                break;
            }
        }

        if (!bossAlive && player.score >= player.nextBossScore) {
            factory.createBoss(arena.getWidth() * 0.5f, arena.getHeight() * 0.88f, player.nextBossScore);
            player.nextBossScore += 10000;
            bossAlive = true;
        }

        int scoreTier = player.score / 10000;
        int maxEnemies = Math.min(72, 12 + scoreTier * 12);
        spawnTimer -= delta;
        if (!bossAlive && enemies.size() < maxEnemies && spawnTimer <= 0f) {
            int roll = MathUtils.random(0, 99);
            int kind = roll < 30 ? 0 : roll < 62 ? 1 : 2;
            float x = MathUtils.random(38f, Math.max(39f, arena.getWidth() - 38f));
            factory.createEnemy(x, arena.getHeight() - 38f, kind, difficulty);
            float baseInterval = 1.85f - difficulty * 0.052f;
            spawnTimer = Math.max(0.32f, baseInterval);
        } else if (enemies.size() >= maxEnemies) {
            spawnTimer = Math.min(spawnTimer, 0.35f);
        }

        TransformComponent target = players.first().getComponent(TransformComponent.class);
        float shooterFloor = arena.getHeight() * 0.70f;
        float shooterCeiling = arena.getHeight() * 0.94f;

        for (int i = 0; i < enemies.size(); i++) {
            Entity entity = enemies.get(i);
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            VelocityComponent velocity = entity.getComponent(VelocityComponent.class);
            EnemyComponent enemy = entity.getComponent(EnemyComponent.class);
            if (enemy.kind == 3) {
                float laneY = arena.getHeight() * 0.86f;
                velocity.x = MathUtils.sin(elapsed * 0.85f + enemy.strafePhase) * enemy.speed * 1.5f;
                velocity.y = MathUtils.clamp((laneY - transform.y) * 2f, -enemy.speed * 0.4f, enemy.speed * 0.4f);

                if (enemy.bossStage == 1 && enemy.health <= enemy.maxHealth * 0.5f) {
                    enemy.bossStage = 2;
                    enemy.bossAttackTimer = 1.1f;
                    enemy.bossRadialVolleyCount = 0;
                    factory.createEffect(transform.x, transform.y, 1f, 0.12f, 0.25f, enemy.radius * 2.8f);
                }

                if (enemy.bossStage == 1) {
                    if (enemy.bossPatternActiveTimer > 0f) {
                        enemy.bossPatternActiveTimer -= delta;
                        enemy.bossShotTimer -= delta;
                        if (enemy.bossShotTimer <= 0f) {
                            float angle = enemy.bossSweepAngle;
                            float targetX = transform.x + MathUtils.cosDeg(angle) * 180f;
                            float targetY = transform.y + MathUtils.sinDeg(angle) * 180f;
                            factory.createEnemyProjectile(transform.x, transform.y - enemy.radius,
                                    targetX, targetY, 560f, 13f + difficulty * 0.35f);
                            enemy.bossShotTimer = 0.105f;
                            enemy.bossSweepAngle += enemy.bossSweepDirection * 12f;
                            if (enemy.bossSweepAngle >= 320f) {
                                enemy.bossSweepAngle = 320f;
                                enemy.bossSweepDirection = -1;
                            } else if (enemy.bossSweepAngle <= 220f) {
                                enemy.bossSweepAngle = 220f;
                                enemy.bossSweepDirection = 1;
                            }
                        }
                        if (enemy.bossPatternActiveTimer <= 0f) {
                            enemy.bossPatternActiveTimer = 0f;
                            enemy.bossAttackTimer = 1.65f;
                        }
                    } else {
                        enemy.bossAttackTimer -= delta;
                        if (enemy.bossAttackTimer <= 0f) {
                            enemy.bossPatternActiveTimer = 2.1f;
                            enemy.bossShotTimer = 0f;
                        }
                    }
                } else {
                    enemy.bossAttackTimer -= delta;
                    if (enemy.bossAttackTimer <= 0f) {
                        int bulletCount = 18;
                        for (int bullet = 0; bullet < bulletCount; bullet++) {
                            float angle = bullet * 360f / bulletCount;
                            float targetX = transform.x + MathUtils.cosDeg(angle) * 160f;
                            float targetY = transform.y + MathUtils.sinDeg(angle) * 160f;
                            factory.createEnemyProjectile(transform.x, transform.y,
                                    targetX, targetY, 430f, 15f + difficulty * 0.3f);
                        }
                        enemy.bossRadialVolleyCount++;
                        if (enemy.bossRadialVolleyCount < 2) {
                            enemy.bossAttackTimer = 0.62f;
                        } else {
                            enemy.bossRadialVolleyCount = 0;
                            enemy.bossAttackTimer = 2.75f;
                        }
                    }
                }

                if (transform.x < enemy.radius || transform.x > arena.getWidth() - enemy.radius) {
                    velocity.x *= -1f;
                }
                continue;
            }

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
