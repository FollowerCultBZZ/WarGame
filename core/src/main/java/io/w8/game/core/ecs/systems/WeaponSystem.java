package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import io.w8.game.core.ecs.components.PlayerComponent;
import io.w8.game.core.ecs.components.ProjectileComponent;
import io.w8.game.core.ecs.components.ShapeComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.VelocityComponent;
import io.w8.game.core.ecs.components.WeaponComponent;

public class WeaponSystem extends EntitySystem {
    private final Engine engine;
    private ImmutableArray<Entity> players;

    public WeaponSystem(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void addedToEngine(Engine engine) {
        players = engine.getEntitiesFor(
                Family.all(PlayerComponent.class, TransformComponent.class, WeaponComponent.class).get()
        );
    }

    @Override
    public void update(float deltaTime) {
        float delta = Math.min(deltaTime, 0.05f);

        for (int i = 0; i < players.size(); i++) {
            Entity player = players.get(i);
            TransformComponent playerTransform = player.getComponent(TransformComponent.class);
            WeaponComponent weapon = player.getComponent(WeaponComponent.class);

            weapon.fireTimer -= delta;
            if (weapon.fireTimer > 0f) continue;

            weapon.fireTimer += weapon.fireInterval;

            Entity projectile = engine.createEntity();

            TransformComponent transform = new TransformComponent(
                    playerTransform.x,
                    playerTransform.y + 30f
            );

            VelocityComponent velocity = new VelocityComponent();
            velocity.x = 0f;
            velocity.y = weapon.projectileSpeed;

            ShapeComponent shape = new ShapeComponent();
            shape.type = ShapeComponent.RECTANGLE;
            shape.radius = 6f;
            shape.red = 0.25f;
            shape.green = 0.9f;
            shape.blue = 1f;

            projectile.add(transform);
            projectile.add(velocity);
            projectile.add(shape);
            projectile.add(new ProjectileComponent());

            engine.addEntity(projectile);
        }
    }
}
