package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import io.w8.game.core.ecs.components.ShapeComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.VelocityComponent;
import io.w8.game.core.world.Arena;

public class MovementSystem extends EntitySystem {
    private final Arena arena;
    private ImmutableArray<Entity> entities;

    public MovementSystem(Arena arena) {
        this.arena = arena;
    }

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        entities = engine.getEntitiesFor(
                Family.all(TransformComponent.class, VelocityComponent.class).get()
        );
    }

    @Override
    public void update(float deltaTime) {
        float delta = Math.min(deltaTime, 0.05f);

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            VelocityComponent velocity = entity.getComponent(VelocityComponent.class);
            ShapeComponent shape = entity.getComponent(ShapeComponent.class);
            float radius = shape == null ? 0f : shape.radius;

            transform.x += velocity.x * delta;
            transform.y += velocity.y * delta;

            float clampedX = arena.clampX(transform.x, radius);
            float clampedY = arena.clampY(transform.y, radius);

            if (clampedX != transform.x) velocity.x = 0f;
            if (clampedY != transform.y) velocity.y = 0f;

            transform.x = clampedX;
            transform.y = clampedY;
        }
    }
}
