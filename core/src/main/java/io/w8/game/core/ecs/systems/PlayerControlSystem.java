package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import io.w8.game.core.ecs.components.PlayerComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.VelocityComponent;
import io.w8.game.core.input.GameInput;
import io.w8.game.core.world.Arena;

public class PlayerControlSystem extends EntitySystem {
    private final GameInput input;
    private final Arena arena;
    private ImmutableArray<Entity> players;

    public PlayerControlSystem(GameInput input, Arena arena) {
        this.input = input;
        this.arena = arena;
    }

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class, VelocityComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        float delta = Math.min(deltaTime, 0.05f);
        for (int i = 0; i < players.size(); i++) {
            Entity entity = players.get(i);
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            VelocityComponent velocity = entity.getComponent(VelocityComponent.class);
            PlayerComponent player = entity.getComponent(PlayerComponent.class);

            float desiredX = 0f;
            float desiredY = 0f;
            if (input.isPointerDown()) {
                float dx = input.getPointerX() - transform.x;
                float dy = input.getPointerY() - transform.y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                if (distance > 5f) {
                    float speed = Math.min(velocity.maxSpeed * player.speedMultiplier, distance * 4f);
                    desiredX = dx / distance * speed;
                    desiredY = dy / distance * speed;
                }
            }

            float maxChange = velocity.acceleration * delta;
            velocity.x = approach(velocity.x, desiredX, maxChange);
            velocity.y = approach(velocity.y, desiredY, maxChange);
            if (!input.isPointerDown() && Math.abs(velocity.x) + Math.abs(velocity.y) < 8f) {
                velocity.x = 0f;
                velocity.y = 0f;
            }
        }
    }

    private float approach(float value, float target, float amount) {
        if (value < target) return Math.min(value + amount, target);
        return Math.max(value - amount, target);
    }
}
