package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import io.w8.game.core.ecs.components.EnemyComponent;
import io.w8.game.core.ecs.components.PlayerComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.VelocityComponent;
import io.w8.game.core.ecs.factories.EntityFactory;
import io.w8.game.core.world.Arena;

public class EnemySystem extends EntitySystem {
    private final Arena arena;
    private Engine engine;
    private EntityFactory factory;
    private ImmutableArray<Entity> players;
    private ImmutableArray<Entity> enemies;
    private float spawnTimer = 0.8f;
    private float elapsed;

    public EnemySystem(Arena arena) {
        this.arena = arena;
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
        factory = new EntityFactory(engine);
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class, TransformComponent.class).get());
        enemies = engine.getEntitiesFor(Family.all(EnemyComponent.class, TransformComponent.class, VelocityComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        float delta = Math.min(deltaTime, 0.05f);
        elapsed += delta;
        spawnTimer -= delta;

        if (spawnTimer <= 0f && players.size() > 0) {
            int kind = MathUtils.random(0, 9) < 6 ? 0 : (MathUtils.randomBoolean() ? 1 : 2);
            float x = MathUtils.random(45f, Math.max(46f, arena.getWidth() - 45f));
            factory.createEnemy(x, arena.getHeight() - 45f, kind, Math.min(12f, elapsed / 35f));
            spawnTimer = Math.max(0.38f, 1.45f - elapsed * 0.008f);
        }

        if (players.size() == 0) return;
        TransformComponent target = players.first().getComponent(TransformComponent.class);

        for (int i = 0; i < enemies.size(); i++) {
            Entity entity = enemies.get(i);
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            VelocityComponent velocity = entity.getComponent(VelocityComponent.class);
            EnemyComponent enemy = entity.getComponent(EnemyComponent.class);

            float dx = target.x - transform.x;
            float dy = target.y - transform.y;
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            if (length > 0.001f) {
                velocity.x = dx / length * enemy.speed;
                velocity.y = dy / length * enemy.speed;
            }
        }
    }
}
