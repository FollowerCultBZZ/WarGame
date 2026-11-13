package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import io.w8.game.core.ecs.components.PlayerComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.WeaponComponent;
import io.w8.game.core.ecs.factories.EntityFactory;

public class WeaponSystem extends EntitySystem {
    private final Engine engine;
    private EntityFactory factory;
    private ImmutableArray<Entity> players;

    public WeaponSystem(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void addedToEngine(Engine engine) {
        factory = new EntityFactory(engine);
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class, WeaponComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        float delta = Math.min(deltaTime, 0.05f);
        for (int i = 0; i < players.size(); i++) {
            Entity player = players.get(i);
            TransformComponent transform = player.getComponent(TransformComponent.class);
            PlayerComponent stats = player.getComponent(PlayerComponent.class);
            WeaponComponent weapon = player.getComponent(WeaponComponent.class);

            weapon.fireTimer -= delta;
            if (weapon.fireTimer <= 0f) {
                weapon.fireTimer = weapon.fireInterval / stats.fireRateMultiplier;
                int count = 1 + stats.extraProjectiles;
                for (int shot = 0; shot < count; shot++) {
                    float offset = (shot - (count - 1) * 0.5f) * 13f;
                    factory.createProjectile(transform.x, transform.y + 32f, offset,
                            weapon.projectileSpeed, false, stats.piercingLevels);
                }
            }

            if (stats.homingLevels > 0) {
                stats.rocketTimer -= delta;
                if (stats.rocketTimer <= 0f) {
                    for (int rocket = 0; rocket < stats.rocketCount; rocket++) {
                        float offset = (rocket - (stats.rocketCount - 1) * 0.5f) * 18f;
                        factory.createRocket(transform.x + offset, transform.y + 4f);
                    }
                    stats.rocketTimer = Math.max(0.32f, 2.8f / stats.rocketFireRateMultiplier);
                }
            }
        }
    }
}
