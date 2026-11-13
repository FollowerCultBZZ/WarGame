package io.w8.game.core.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import io.w8.game.core.ecs.effects.ParticleEffectFactory;

public class EntityFactory {
    private final PlayerFactory players;
    private final EnemyFactory enemies;
    private final PickupFactory pickups;
    private final ProjectileFactory projectiles;
    private final ParticleEffectFactory effects;

    public EntityFactory(Engine engine) {
        players = new PlayerFactory(engine);
        enemies = new EnemyFactory(engine);
        pickups = new PickupFactory(engine);
        projectiles = new ProjectileFactory(engine);
        effects = new ParticleEffectFactory(engine);
    }

    public Entity createPlayer(float x, float y) {
        return players.createPlayer(x, y);
    }

    public Entity createEnemy(float x, float y, int kind, float difficulty) {
        return enemies.createEnemy(x, y, kind, difficulty);
    }

    public Entity createBoss(float x, float y, int scoreMilestone) {
        return enemies.createBoss(x, y, scoreMilestone);
    }

    public Entity createPickup(float x, float y, int type) {
        return pickups.createPickup(x, y, type);
    }

    public Entity createProjectile(float x, float y, float offsetX,
                                   float speed, boolean homing, int piercing) {
        return projectiles.createProjectile(x, y, offsetX, speed, homing, piercing);
    }

    public Entity createRocket(float x, float y) {
        return projectiles.createRocket(x, y);
    }

    public Entity createUltimateRocket(float x, float y) {
        return projectiles.createUltimateRocket(x, y);
    }

    public Entity createEnemyProjectile(float x, float y, float targetX, float targetY,
                                        float speed, float damage) {
        return projectiles.createEnemyProjectile(x, y, targetX, targetY, speed, damage);
    }

    public void createEffect(float x, float y, float red, float green, float blue, float radius) {
        effects.createExplosion(x, y, radius, red, green, blue);
    }
}
