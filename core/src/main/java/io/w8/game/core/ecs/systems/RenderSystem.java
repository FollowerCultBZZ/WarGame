package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.w8.game.core.ecs.components.ShapeComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.world.Arena;

public class RenderSystem extends EntitySystem {
    private final ShapeRenderer renderer = new ShapeRenderer();
    private final OrthographicCamera camera;
    private final Arena arena;
    private ImmutableArray<Entity> entities;

    public RenderSystem(OrthographicCamera camera, Arena arena) {
        this.camera = camera;
        this.arena = arena;
    }

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        entities = engine.getEntitiesFor(
                Family.all(TransformComponent.class, ShapeComponent.class).get()
        );
    }

    public void render() {
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(0.2f, 0.45f, 0.65f, 1f);
        renderer.rect(0f, 0f, arena.getWidth(), arena.getHeight());
        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            ShapeComponent shape = entity.getComponent(ShapeComponent.class);

            renderer.setColor(shape.red, shape.green, shape.blue, 1f);

            if (shape.type == ShapeComponent.TRIANGLE) {
                float r = shape.radius;
                renderer.triangle(
                        transform.x, transform.y + r * 1.35f,
                        transform.x - r, transform.y - r,
                        transform.x + r, transform.y - r
                );
            } else if (shape.type == ShapeComponent.CIRCLE) {
                renderer.circle(transform.x, transform.y, shape.radius, 20);
            } else if (shape.type == ShapeComponent.RECTANGLE) {
                renderer.rect(
                        transform.x - shape.radius * 0.35f,
                        transform.y - shape.radius,
                        shape.radius * 0.7f,
                        shape.radius * 2f
                );
            }
        }
        renderer.end();
    }

    public void dispose() {
        renderer.dispose();
    }
}
