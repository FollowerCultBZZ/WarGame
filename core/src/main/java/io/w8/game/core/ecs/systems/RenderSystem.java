package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.w8.game.core.ecs.components.PlayerComponent;
import io.w8.game.core.ecs.components.ShapeComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.world.Arena;

public class RenderSystem extends EntitySystem {
    private final ShapeRenderer renderer = new ShapeRenderer();
    private final OrthographicCamera camera;
    private final Arena arena;
    private ImmutableArray<Entity> entities;
    private ImmutableArray<Entity> players;

    public RenderSystem(OrthographicCamera camera, Arena arena) {
        this.camera = camera;
        this.arena = arena;
    }

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        entities = engine.getEntitiesFor(Family.all(TransformComponent.class, ShapeComponent.class).get());
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
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
                renderer.triangle(transform.x, transform.y + r * 1.35f,
                        transform.x - r, transform.y - r,
                        transform.x + r, transform.y - r);
            } else if (shape.type == ShapeComponent.CIRCLE) {
                renderer.circle(transform.x, transform.y, shape.radius, 20);
            } else if (shape.type == ShapeComponent.RECTANGLE) {
                renderer.rect(transform.x - shape.radius * 0.35f,
                        transform.y - shape.radius, shape.radius * 0.7f, shape.radius * 2f);
            }
        }

        if (players.size() > 0) {
            PlayerComponent player = players.first().getComponent(PlayerComponent.class);
            float ratio = Math.max(0f, Math.min(1f, player.health / player.maxHealth));
            float x = 20f;
            float y = arena.getHeight() - 34f;
            float width = 190f;
            float height = 13f;
            renderer.setColor(0.08f, 0.1f, 0.16f, 0.95f);
            renderer.rect(x, y, width, height);
            renderer.setColor(ratio > 0.55f ? 0.18f : ratio > 0.25f ? 1f : 1f,
                    ratio > 0.55f ? 0.85f : ratio > 0.25f ? 0.68f : 0.2f,
                    ratio > 0.55f ? 0.35f : 0.12f, 1f);
            renderer.rect(x + 2f, y + 2f, (width - 4f) * ratio, height - 4f);
            renderer.setColor(0.7f, 0.85f, 1f, 1f);
            renderer.rect(x, y - 4f, width, 1f);
        }
        renderer.end();
    }

    public void dispose() {
        renderer.dispose();
    }
}
