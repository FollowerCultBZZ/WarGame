package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import io.w8.game.core.ecs.components.PickupComponent;
import io.w8.game.core.ecs.components.PlayerComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.ShapeComponent;
import io.w8.game.core.ecs.components.VelocityComponent;
import io.w8.game.core.ecs.factories.EntityFactory;

public class PickupSystem extends EntitySystem {
    private Engine engine;
    private EntityFactory factory;
    private ImmutableArray<Entity> players;
    private ImmutableArray<Entity> pickups;

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
        factory = new EntityFactory(engine);
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class, VelocityComponent.class).get());
        pickups = engine.getEntitiesFor(Family.all(PickupComponent.class, TransformComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (int i = pickups.size() - 1; i >= 0; i--) {
            Entity pickup = pickups.get(i);
            if (pickup.getComponent(TransformComponent.class).y < -30f) {
                engine.removeEntity(pickup);
            }
        }

        for (int i = 0; i < players.size(); i++) {
            Entity playerEntity = players.get(i);
            PlayerComponent player = playerEntity.getComponent(PlayerComponent.class);
            if (player.dead) continue;

            TransformComponent playerTransform = playerEntity.getComponent(TransformComponent.class);

            for (int j = pickups.size() - 1; j >= 0; j--) {
                Entity pickupEntity = pickups.get(j);
                PickupComponent pickup = pickupEntity.getComponent(PickupComponent.class);
                if (pickup == null) continue;

                TransformComponent pickupTransform = pickupEntity.getComponent(TransformComponent.class);
                float dx = playerTransform.x - pickupTransform.x;
                float dy = playerTransform.y - pickupTransform.y;
                ShapeComponent playerShape = playerEntity.getComponent(ShapeComponent.class);
                ShapeComponent pickupShape = pickupEntity.getComponent(ShapeComponent.class);
                float hitRadius = (playerShape == null ? 28f : playerShape.radius)
                        + (pickupShape == null ? 13f : pickupShape.radius);
                if (dx * dx + dy * dy > hitRadius * hitRadius) continue;

                switch (pickup.type) {
                    case PickupComponent.SPEED:
                        player.speedMultiplier = Math.min(2.15f, player.speedMultiplier + 0.15f);
                        break;
                    case PickupComponent.MULTI_SHOT:
                        if (player.extraProjectiles < 5) {
                            player.extraProjectiles++;
                        } else {
                            player.multiShotOverdriveTimer = 8f;
                        }
                        break;
                    case PickupComponent.FIRE_RATE:
                        if (player.fireRateMultiplier < 3.5f) {
                            player.fireRateMultiplier = Math.min(3.5f, player.fireRateMultiplier + 0.35f);
                        } else {
                            player.fireRateOverdriveTimer = 8f;
                        }
                        break;
                    case PickupComponent.HEAL:
                        player.health = Math.min(player.maxHealth, player.health + 28f);
                        player.shieldHealth = player.maxShieldHealth;
                        player.shieldTimer = 3f;
                        break;
                    case PickupComponent.HOMING:
                        if (player.rocketCount < 4) {
                            player.rocketCount++;
                        } else {
                            player.rocketFireRateMultiplier = Math.min(6f,
                                    player.rocketFireRateMultiplier + 0.25f);
                            player.rocketTimer = Math.min(player.rocketTimer,
                                    Math.max(0.32f, 2.8f / player.rocketFireRateMultiplier));
                        }
                        player.homingLevels = Math.min(4, player.homingLevels + 1);
                        break;
                    case PickupComponent.PIERCING:
                        player.piercingLevels = Math.min(3, player.piercingLevels + 1);
                        break;
                    default:
                        continue;
                }

                player.score += 25;
                factory.createEffect(pickupTransform.x, pickupTransform.y, 0.3f, 1f, 0.8f, 24f);
                engine.removeEntity(pickupEntity);
            }
        }
    }
}
