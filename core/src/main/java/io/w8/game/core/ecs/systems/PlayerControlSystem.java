package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import io.w8.game.core.ecs.components.PlayerComponent;
import io.w8.game.core.ecs.components.ShapeComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.VelocityComponent;
import io.w8.game.core.ecs.factories.EntityFactory;
import io.w8.game.core.input.GameInput;
import io.w8.game.core.world.Arena;

public class PlayerControlSystem extends EntitySystem {
    private static final float FOLLOW_GAIN = 10f;
    private static final float ARRIVAL_DISTANCE = 2.5f;

    private final GameInput input;
    private final Arena arena;
    private EntityFactory factory;
    private ImmutableArray<Entity> players;
    private float targetX;
    private float targetY;
    private boolean targetInitialized;
    private boolean wasPointerDown;

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
        boolean pointerDown = input.isPointerDown();

        for (int i = 0; i < players.size(); i++) {
            Entity entity = players.get(i);
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            VelocityComponent velocity = entity.getComponent(VelocityComponent.class);
            PlayerComponent player = entity.getComponent(PlayerComponent.class);

            if (player.dead) {
                velocity.x = 0f;
                velocity.y = 0f;
                targetInitialized = false;
                wasPointerDown = false;
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

            if (!pointerDown) {
                velocity.x = 0f;
                velocity.y = 0f;
                wasPointerDown = false;
                continue;
            }

            if (!wasPointerDown || !targetInitialized) {
                targetX = transform.x;
                targetY = transform.y;
                targetInitialized = true;
            }

            targetX += dragX;
            targetY += dragY;

            ShapeComponent shape = entity.getComponent(ShapeComponent.class);
            float radius = shape == null ? 0f : shape.radius;
            targetX = arena.clampX(targetX, radius);
            targetY = arena.clampY(targetY, radius);

            float offsetX = targetX - transform.x;
            float offsetY = targetY - transform.y;
            float distance = (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY);

            if (distance <= ARRIVAL_DISTANCE) {
                velocity.x = 0f;
                velocity.y = 0f;
            } else {
                float speed = Math.min(velocity.maxSpeed * player.speedMultiplier, distance * FOLLOW_GAIN);
                velocity.x = offsetX / distance * speed;
                velocity.y = offsetY / distance * speed;
            }

            wasPointerDown = true;
        }
    }
}
