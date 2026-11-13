package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import io.w8.game.core.ecs.components.PickupComponent;
import io.w8.game.core.ecs.components.PlayerComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.VelocityComponent;
import io.w8.game.core.ecs.components.WeaponComponent;

public class PickupSystem extends EntitySystem {
    private Engine engine;
    private ImmutableArray<Entity> players;
    private ImmutableArray<Entity> pickups;

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class, VelocityComponent.class, WeaponComponent.class).get());
        pickups = engine.getEntitiesFor(Family.all(PickupComponent.class, TransformComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (int i = 0; i < players.size(); i++) {
            Entity playerEntity = players.get(i);
            PlayerComponent player = playerEntity.getComponent(PlayerComponent.class);
            TransformComponent playerTransform = playerEntity.getComponent(TransformComponent.class);
            VelocityComponent velocity = playerEntity.getComponent(VelocityComponent.class);
            WeaponComponent weapon = playerEntity.getComponent(WeaponComponent.class);

            for (int j = 0; j < pickups.size(); j++) {
                Entity pickupEntity = pickups.get(j);
                if (pickupEntity.getComponent(PickupComponent.class) == null) continue;
                TransformComponent pickupTransform = pickupEntity.getComponent(TransformComponent.class);
                float dx = playerTransform.x - pickupTransform.x;
                float dy = playerTransform.y - pickupTransform.y;
                if (dx * dx + dy * dy > 34f * 34f) continue;

                int type = pickupEntity.getComponent(PickupComponent.class).type;
                if (type == PickupComponent.SPEED) {
                    player.speedMultiplier = Math.min(1.8f, player.speedMultiplier + 0.12f);
                    velocity.maxSpeed = Math.min(velocity.maxSpeed, 620f);
                } else if (type == PickupComponent.MULTI_SHOT) {
                    player.extraProjectiles = Math.min(4, player.extraProjectiles + 1);
                } else if (type == PickupComponent.FIRE_RATE) {
                    player.fireRateMultiplier = Math.min(2.2f, player.fireRateMultiplier + 0.15f);
                } else if (type == PickupComponent.HEAL) {
                    player.health = Math.min(player.maxHealth, player.health + 28f);
                } else if (type == PickupComponent.HOMING) {
                    player.homingLevels = Math.min(3, player.homingLevels + 1);
                } else if (type == PickupComponent.PIERCING) {
                    player.piercingLevels = Math.min(3, player.piercingLevels + 1);
                }
                engine.removeEntity(pickupEntity);
            }
        }
    }
}
