package io.w8.game.core.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import io.w8.game.core.ecs.components.PlayerComponent;
import io.w8.game.core.ecs.components.ShapeComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.VelocityComponent;
import io.w8.game.core.ecs.components.WeaponComponent;

public class PlayerFactory {
    private final Engine engine;

    public PlayerFactory(Engine engine) {
        this.engine = engine;
    }

    public Entity createPlayer(float x, float y) {
        Entity entity = engine.createEntity();
        VelocityComponent velocity = new VelocityComponent();
        velocity.maxSpeed = 920f;
        velocity.acceleration = 4200f;

        ShapeComponent shape = new ShapeComponent();
        shape.type = ShapeComponent.TRIANGLE;
        shape.radius = 28f;
        shape.red = 0.2f;
        shape.green = 0.85f;
        shape.blue = 1f;

        entity.add(new TransformComponent(x, y));
        entity.add(velocity);
        entity.add(new PlayerComponent());
        entity.add(new WeaponComponent());
        entity.add(shape);
        engine.addEntity(entity);
        return entity;
    }
}
