package io.w8.game.core.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import io.w8.game.core.ecs.components.HealthComponent;
import io.w8.game.core.ecs.components.MovementComponent;
import io.w8.game.core.ecs.components.TeamComponent;
import io.w8.game.core.ecs.components.TransformComponent;

public class EntityFactory {
    private final Engine engine;

    public EntityFactory(Engine engine) {
        this.engine = engine;
    }

    public Entity createPlayer(float x, float y) {
        Entity entity = engine.createEntity();

        TransformComponent transform = new TransformComponent(x, y);
        MovementComponent movement = new MovementComponent();
        movement.speed = 220f;

        entity.add(transform);
        entity.add(movement);
        entity.add(new HealthComponent(100f));
        entity.add(new TeamComponent(TeamComponent.Team.BLUE));

        engine.addEntity(entity);
        return entity;
    }
}
