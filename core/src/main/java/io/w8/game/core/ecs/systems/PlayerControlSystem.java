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
    private static final float FINGER_SPEED_FOR_MAX_INPUT = 380f;
    private static final float PLAYER_ACCELERATION = 900f;
    private static final float PLAYER_BRAKING = 1100f;

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
        float dragX = input.consumeDragDeltaX();
        float dragY = input.consumeDragDeltaY();

        for (int i = 0; i < players.size(); i++) {
            Entity entity = players.get(i);
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            VelocityComponent velocity = entity.getComponent(VelocityComponent.class);
            PlayerComponent player = entity.getComponent(PlayerComponent.class);
            if (player.dead) {
                velocity.x = 0f;
                velocity.y = 0f;
                continue;
            }

            player.ultimateCooldown = Math.max(0f, player.ultimateCooldown - delta);
            if (input.consumeUltimatePressed() && player.ultimateCooldown <= 0f) {
                for (int rocket = 0; rocket < 30; rocket++) {
                    float x = (rocket + 0.5f) * arena.getWidth() / 30f;
                    factory.createUltimateRocket(x, 8f);
                }
                player.ultimateCooldown = 22f;
            }

            float moveX = dragX / delta / FINGER_SPEED_FOR_MAX_INPUT;
            float moveY = dragY / delta / FINGER_SPEED_FOR_MAX_INPUT;
            float inputLength = (float) Math.sqrt(moveX * moveX + moveY * moveY);
            if (inputLength > 1f) {
                moveX /= inputLength;
                moveY /= inputLength;
                inputLength = 1f;
            }

            float maxSpeed = velocity.maxSpeed * player.speedMultiplier;
            float desiredX = moveX * maxSpeed;
            float desiredY = moveY * maxSpeed;
            float maxChange = (inputLength > 0.015f ? PLAYER_ACCELERATION : PLAYER_BRAKING) * delta;
            velocity.x = approach(velocity.x, desiredX, maxChange);
            velocity.y = approach(velocity.y, desiredY, maxChange);
            if (inputLength <= 0.015f && Math.abs(velocity.x) + Math.abs(velocity.y) < 5f) {
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
