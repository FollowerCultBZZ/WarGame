package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import io.w8.game.core.ecs.components.HealthComponent;

public class HealthSystem extends EntitySystem {
    private Engine engine;
    private ImmutableArray<Entity> entities;

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
        entities = engine.getEntitiesFor(Family.all(HealthComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (int i = entities.size() - 1; i >= 0; i--) {
            Entity entity = entities.get(i);
            HealthComponent health = entity.getComponent(HealthComponent.class);
            if (health.isDead()) {
                engine.removeEntity(entity);
            }
        }
    }
}
