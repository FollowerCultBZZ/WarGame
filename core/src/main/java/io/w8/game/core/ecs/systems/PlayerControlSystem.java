package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import io.w8.game.core.ecs.components.PlayerComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.VelocityComponent;
import io.w8.game.core.ecs.factories.EntityFactory;
import io.w8.game.core.input.GameInput;
import io.w8.game.core.world.Arena;

public class PlayerControlSystem extends EntitySystem {
    private final GameInput input;
    private final Arena arena;
    private EntityFactory factory;
    private ImmutableArray<Entity> players;

    public PlayerControlSystem(GameInput input, Arena arena) {
        this.input = input;
        this.arena = arena;
    }

    @Override
    public void addedToEngine(Engine engine) {
        factory = new EntityFactory(engine);
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

            player.ultimateCooldown = Math.max(0f, player.ultimateCooldown - delta);
            if (input.consumeUltimatePressed() && player.ultimateCooldown <= 0f) {
                for (int rocket = 0; rocket < 30; rocket++) {
                    float x = (rocket + 0.5f) * arena.getWidth() / 30f;
                    factory.createUltimateRocket(x, 8f);
                }
                player.ultimateCooldown = 22f;
            }

            float desiredX = 0f;
            float desiredY = 0f;
            if (input.isPointerDown()) {
                float dx = input.getPointerX() - transform.x;
                float dy = input.getPointerY() - transform.y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                if (distance > 3f) {
                    float speed = Math.min(velocity.maxSpeed * player.speedMultiplier, distance * 18f);
                    desiredX = dx / distance * speed;
                    desiredY = dy / distance * speed;
                }
            }

            float maxChange = velocity.acceleration * delta * (input.isPointerDown() ? 2.2f : 3.2f);
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
