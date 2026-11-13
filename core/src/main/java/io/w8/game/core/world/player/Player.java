package io.w8.game.core.world.player;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import io.w8.game.core.ecs.components.HealthComponent;
import io.w8.game.core.ecs.components.MovementComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.factories.EntityFactory;
import io.w8.game.core.world.rules.GameRules;

public class Player {
    private static final float MOVE_SPEED = 220f;

    private final Entity entity;
    private final TransformComponent transform;
    private final MovementComponent movement;
    private final HealthComponent health;

    public Player(Engine engine, float x, float y) {
        entity = new EntityFactory(engine).createPlayer(x, y);
        transform = entity.getComponent(TransformComponent.class);
        movement = entity.getComponent(MovementComponent.class);
        health = entity.getComponent(HealthComponent.class);
    }

    public void moveTo(float x, float y) {
        movement.moveTo(x, y);
    }

    public void update(float delta, GameRules rules) {
        movement.speed = MOVE_SPEED * rules.getMovementSpeedMultiplier();
    }

    public float getX() {
        return transform.x;
    }

    public float getY() {
        return transform.y;
    }

    public float getTargetX() {
        return movement.targetX;
    }

    public float getTargetY() {
        return movement.targetY;
    }

    public boolean isMoving() {
        return movement.moving;
    }

    public float getHealth() {
        return health.current;
    }

    public float getMaxHealth() {
        return health.maximum;
    }

    public void damage(float amount) {
        health.damage(amount);
    }

    public Entity getEntity() {
        return entity;
    }
}
