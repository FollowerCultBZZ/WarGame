package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import io.w8.game.core.ecs.components.MovementComponent;
import io.w8.game.core.ecs.components.TransformComponent;

public class MovementSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        entities = engine.getEntitiesFor(
                Family.all(TransformComponent.class, MovementComponent.class).get()
        );
    }

    @Override
    public void update(float deltaTime) {
        float delta = Math.min(deltaTime, 0.05f);

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            MovementComponent movement = entity.getComponent(MovementComponent.class);

            if (!movement.moving) continue;

            float dx = movement.targetX - transform.x;
            float dy = movement.targetY - transform.y;
            float distanceSquared = dx * dx + dy * dy;

            if (distanceSquared <= 1f) {
                transform.x = movement.targetX;
                transform.y = movement.targetY;
                movement.stop();
                continue;
            }

            float distance = (float) Math.sqrt(distanceSquared);
            float step = Math.min(movement.speed * delta, distance);
            transform.x += dx / distance * step;
            transform.y += dy / distance * step;
        }
    }
}
