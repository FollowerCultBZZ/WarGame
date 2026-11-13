package io.w8.game.core.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import io.w8.game.core.ecs.components.PlayerComponent;
import io.w8.game.core.ecs.components.ShapeComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.VelocityComponent;

public class EntityFactory {
    private final Engine engine;

    public EntityFactory(Engine engine) {
        this.engine = engine;
    }

    public Entity createPlayer(float x, float y) {
        Entity entity = engine.createEntity();

        TransformComponent transform = new TransformComponent(x, y);
        VelocityComponent velocity = new VelocityComponent();
        velocity.maxSpeed = 520f;
        velocity.acceleration = 1800f;

        ShapeComponent shape = new ShapeComponent();
        shape.type = ShapeComponent.TRIANGLE;
        shape.radius = 18f;
        shape.red = 0.2f;
        shape.green = 0.85f;
        shape.blue = 1f;

        entity.add(transform);
        entity.add(velocity);
        entity.add(new PlayerComponent());
        entity.add(shape);

        engine.addEntity(entity);
        return entity;
    }
}
