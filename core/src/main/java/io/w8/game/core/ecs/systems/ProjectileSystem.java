package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import io.w8.game.core.ecs.components.ProjectileComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.world.Arena;

public class ProjectileSystem extends EntitySystem {
    private final Arena arena;
    private Engine engine;
    private ImmutableArray<Entity> projectiles;
    private final Array<Entity> expired = new Array<>();

    public ProjectileSystem(Arena arena) {
        this.arena = arena;
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
        projectiles = engine.getEntitiesFor(Family.all(ProjectileComponent.class, TransformComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        float delta = Math.min(deltaTime, 0.05f);
        expired.clear();
        for (int i = 0; i < projectiles.size(); i++) {
            Entity entity = projectiles.get(i);
            ProjectileComponent projectile = entity.getComponent(ProjectileComponent.class);
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            projectile.remainingLife -= delta;
            if (projectile.remainingLife <= 0f
                    || transform.y > arena.getHeight() + 60f
                    || transform.x < -60f
                    || transform.x > arena.getWidth() + 60f
                    || transform.y < -60f) {
                expired.add(entity);
            }
        }
        for (int i = 0; i < expired.size; i++) engine.removeEntity(expired.get(i));
    }
}
