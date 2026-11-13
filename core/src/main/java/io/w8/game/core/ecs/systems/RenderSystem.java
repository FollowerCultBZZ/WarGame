package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.w8.game.core.ecs.components.EffectComponent;
import io.w8.game.core.ecs.components.EnemyComponent;
import io.w8.game.core.ecs.components.PlayerComponent;
import io.w8.game.core.ecs.components.PickupComponent;
import io.w8.game.core.ecs.components.ShapeComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.world.Arena;

public class RenderSystem extends EntitySystem {
    private final ShapeRenderer renderer = new ShapeRenderer();
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font = new BitmapFont();
    private final OrthographicCamera camera;
    private final Arena arena;
    private ImmutableArray<Entity> entities;
    private ImmutableArray<Entity> players;
    private ImmutableArray<Entity> effects;
    private ImmutableArray<Entity> enemies;

    public RenderSystem(OrthographicCamera camera, Arena arena) {
        this.camera = camera;
        this.arena = arena;
    }

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        entities = engine.getEntitiesFor(Family.all(TransformComponent.class, ShapeComponent.class).get());
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
        effects = engine.getEntitiesFor(Family.all(EffectComponent.class, TransformComponent.class).get());
        enemies = engine.getEntitiesFor(Family.all(EnemyComponent.class, TransformComponent.class).get());
    }

    public void render() {
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(0.2f, 0.45f, 0.65f, 1f);
        renderer.rect(0f, 0f, arena.getWidth(), arena.getHeight());
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (entity.getComponent(PickupComponent.class) == null) continue;
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            ShapeComponent shape = entity.getComponent(ShapeComponent.class);
            renderer.setColor(shape.red, shape.green, shape.blue, 1f);
            float r = shape.radius;
            if (shape.type == ShapeComponent.CIRCLE) {
                renderer.circle(transform.x, transform.y, r, 24);
                renderer.circle(transform.x, transform.y, r * 0.55f, 16);
                float angle = (float) Math.toRadians(transform.rotation);
                renderer.line(
                        transform.x + (float) Math.cos(angle) * r * 0.55f,
                        transform.y + (float) Math.sin(angle) * r * 0.55f,
                        transform.x + (float) Math.cos(angle) * r,
                        transform.y + (float) Math.sin(angle) * r
                );
            } else {
                renderer.rect(transform.x - r, transform.y - r, r, r,
                        r, r, 1f, 1f, transform.rotation);
            }
        }

        for (int i = 0; i < effects.size(); i++) {
            Entity entity = effects.get(i);
            EffectComponent effect = entity.getComponent(EffectComponent.class);
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            float progress = Math.min(1f, effect.age / effect.duration);
            float radius = effect.radius * (0.35f + progress);
            renderer.setColor(effect.red, effect.green, effect.blue, 1f - progress);
            renderer.circle(transform.x, transform.y, radius, 24);
        }
        for (int i = 0; i < players.size(); i++) {
            Entity playerEntity = players.get(i);
            PlayerComponent player = playerEntity.getComponent(PlayerComponent.class);
            if (player.invulnerabilityTimer > 0f) {
                TransformComponent transform = playerEntity.getComponent(TransformComponent.class);
                renderer.setColor(0.25f, 0.9f, 1f, 1f);
                renderer.circle(transform.x, transform.y, 39f, 32);
            }
        }
        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (entity.getComponent(PickupComponent.class) != null) continue;
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            ShapeComponent shape = entity.getComponent(ShapeComponent.class);
            renderer.setColor(shape.red, shape.green, shape.blue, 1f);

            if (shape.type == ShapeComponent.TRIANGLE) {
                float r = shape.radius;
                if (entity.getComponent(PlayerComponent.class) != null) {
                    renderer.triangle(transform.x, transform.y + r * 1.35f,
                            transform.x - r, transform.y - r,
                            transform.x + r, transform.y - r);
                    renderer.setColor(0.65f, 0.95f, 1f, 1f);
                    renderer.triangle(transform.x, transform.y + r * 0.75f,
                            transform.x - r * 0.38f, transform.y - r * 0.55f,
                            transform.x + r * 0.38f, transform.y - r * 0.55f);
                    renderer.setColor(0.15f, 0.65f, 1f, 1f);
                    renderer.triangle(transform.x, transform.y - r * 0.72f,
                            transform.x - r * 0.28f, transform.y - r * 1.45f,
                            transform.x + r * 0.28f, transform.y - r * 1.45f);
                } else {
                    renderer.triangle(transform.x, transform.y + r * 1.35f,
                            transform.x - r, transform.y - r,
                            transform.x + r, transform.y - r);
                }
            } else if (shape.type == ShapeComponent.CIRCLE) {
                renderer.circle(transform.x, transform.y, shape.radius, 20);
            } else {
                renderer.rect(transform.x - shape.radius * 0.35f,
                        transform.y - shape.radius, shape.radius * 0.7f, shape.radius * 2f,
                        shape.radius * 0.35f, shape.radius, 1f, 1f, transform.rotation);
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
            renderer.setColor(ratio > 0.55f ? 0.18f : 1f,
                    ratio > 0.55f ? 0.85f : ratio > 0.25f ? 0.68f : 0.2f,
                    ratio > 0.55f ? 0.35f : 0.12f, 1f);
            renderer.rect(x + 2f, y + 2f, (width - 4f) * ratio, height - 4f);
        }

        for (int i = 0; i < enemies.size(); i++) {
            EnemyComponent enemy = enemies.get(i).getComponent(EnemyComponent.class);
            if (enemy.kind != 3) continue;
            float ratio = Math.max(0f, Math.min(1f, enemy.health / enemy.maxHealth));
            float x = arena.getWidth() * 0.18f;
            float y = arena.getHeight() - 70f;
            float width = arena.getWidth() * 0.64f;
            renderer.setColor(0.12f, 0.04f, 0.06f, 1f);
            renderer.rect(x, y, width, 18f);
            renderer.setColor(0.95f, 0.08f, 0.16f, 1f);
            renderer.rect(x + 2f, y + 2f, (width - 4f) * ratio, 14f);
        }
        renderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.setColor(1f, 1f, 1f, 1f);
        font.getData().setScale(1.15f);
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            PickupComponent pickup = entity.getComponent(PickupComponent.class);
            if (pickup == null) continue;
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            String label;
            switch (pickup.type) {
                case PickupComponent.HEAL: label = "H"; break;
                case PickupComponent.HOMING: label = "M"; break;
                case PickupComponent.FIRE_RATE: label = "R"; break;
                case PickupComponent.MULTI_SHOT: label = "C"; break;
                case PickupComponent.SPEED: label = "S"; break;
                default: label = "P"; break;
            }
            font.draw(batch, label, transform.x - 4.5f, transform.y + 4.5f);
        }
        if (players.size() > 0) {
            PlayerComponent player = players.first().getComponent(PlayerComponent.class);
            font.getData().setScale(1.35f);
            font.draw(batch, "SCORE " + player.score, arena.getWidth() - 175f, arena.getHeight() - 22f);
            font.getData().setScale(1.15f);
            font.draw(batch, "LIVES " + player.lives, 20f, arena.getHeight() - 58f);
            if (player.invulnerabilityTimer > 0f) {
                font.draw(batch, "SHIELD " + (int) Math.ceil(player.invulnerabilityTimer) + "s",
                        20f, arena.getHeight() - 82f);
            }
            if (player.fireRateOverdriveTimer > 0f) {
                font.draw(batch, "FIRE RATE +250%  " + (int) Math.ceil(player.fireRateOverdriveTimer) + "s",
                        20f, arena.getHeight() - 106f);
            }
            if (player.multiShotOverdriveTimer > 0f) {
                font.draw(batch, "DOUBLE SHOTS  " + (int) Math.ceil(player.multiShotOverdriveTimer) + "s",
                        20f, arena.getHeight() - 130f);
            }
            if (player.homingLevels > 0) {
                font.draw(batch, "ROCKET RATE x" + String.format(java.util.Locale.US, "%.2f",
                        player.rocketFireRateMultiplier), 20f, arena.getHeight() - 154f);
            }
            if (player.dead) {
                font.getData().setScale(3.2f);
                font.draw(batch, "GAME OVER", arena.getWidth() * 0.5f - 100f, arena.getHeight() * 0.5f + 18f);
                font.getData().setScale(1.5f);
                font.draw(batch, "FINAL SCORE " + player.score, arena.getWidth() * 0.5f - 95f,
                        arena.getHeight() * 0.5f - 25f);
            }
        }
        for (int i = 0; i < enemies.size(); i++) {
            EnemyComponent enemy = enemies.get(i).getComponent(EnemyComponent.class);
            if (enemy.kind == 3) {
                font.getData().setScale(1.1f);
                font.draw(batch, "BOSS " + enemy.bossNumber + "  STAGE " + enemy.bossStage,
                        arena.getWidth() * 0.18f, arena.getHeight() - 47f);
            }
        }
        batch.end();
    }

    public void dispose() {
        renderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
