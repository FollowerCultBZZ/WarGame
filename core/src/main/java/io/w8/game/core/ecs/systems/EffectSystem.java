package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import io.w8.game.core.ecs.components.EffectComponent;

public class EffectSystem extends EntitySystem {
    private Engine engine;
    private ImmutableArray<Entity> effects;
    private final Array<Entity> expired = new Array<>();

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
        effects = engine.getEntitiesFor(Family.all(EffectComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        float delta = Math.min(deltaTime, 0.05f);
        expired.clear();

        for (int i = 0; i < effects.size(); i++) {
            Entity entity = effects.get(i);
            EffectComponent effect = entity.getComponent(EffectComponent.class);

            for (int p = effect.particles.size - 1; p >= 0; p--) {
                EffectComponent.Particle particle = effect.particles.get(p);
                particle.age += delta;
                particle.x += particle.velocityX * delta;
                particle.y += particle.velocityY * delta;

                float damping = Math.max(0f, 1f - particle.drag * delta);
                particle.velocityX *= damping;
                particle.velocityY *= damping;

                if (particle.age >= particle.lifetime) {
                    effect.particles.removeIndex(p);
                }
            }

            if (effect.particles.size == 0) expired.add(entity);
        }

        for (int i = 0; i < expired.size; i++) {
            engine.removeEntity(expired.get(i));
        }
    }
}
