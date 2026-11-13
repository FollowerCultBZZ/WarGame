package io.w8.game.core.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import io.w8.game.core.ecs.components.PickupComponent;
import io.w8.game.core.ecs.components.ShapeComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.VelocityComponent;

public class PickupFactory {
    private final Engine engine;

    public PickupFactory(Engine engine) {
        this.engine = engine;
    }

    public Entity createPickup(float x, float y, int type) {
        Entity entity = engine.createEntity();
        ShapeComponent shape = new ShapeComponent();
        shape.type = type <= PickupComponent.HEAL
                ? ShapeComponent.CIRCLE : ShapeComponent.SQUARE;
        shape.radius = 13f;
        shape.hollow = true;
        shape.rotationSpeed = type <= PickupComponent.HEAL ? 95f : 115f;

        if (type == PickupComponent.SPEED) {
            shape.red = 0.2f; shape.green = 0.9f; shape.blue = 1f;
        } else if (type == PickupComponent.MULTI_SHOT) {
            shape.red = 0.3f; shape.green = 1f; shape.blue = 0.35f;
        } else if (type == PickupComponent.FIRE_RATE) {
            shape.red = 1f; shape.green = 0.85f; shape.blue = 0.15f;
        } else if (type == PickupComponent.HEAL) {
            shape.red = 1f; shape.green = 0.25f; shape.blue = 0.45f;
        } else if (type == PickupComponent.HOMING) {
            shape.red = 0.85f; shape.green = 0.35f; shape.blue = 1f;
        } else {
            shape.red = 1f; shape.green = 0.55f; shape.blue = 0.12f;
        }

        TransformComponent transform = new TransformComponent(x, y);
        transform.rotation = (float) (Math.random() * 360f);
        VelocityComponent velocity = new VelocityComponent();
        velocity.y = -48f;
        entity.add(transform);
        entity.add(velocity);
        entity.add(new PickupComponent(type));
        entity.add(shape);
        engine.addEntity(entity);
        return entity;
    }
}
