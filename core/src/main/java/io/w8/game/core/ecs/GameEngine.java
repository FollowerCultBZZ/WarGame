package io.w8.game.core.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.w8.game.core.ecs.components.PlayerComponent;
import io.w8.game.core.ecs.systems.CombatSystem;
import io.w8.game.core.ecs.systems.EffectSystem;
import io.w8.game.core.ecs.systems.EnemySystem;
import io.w8.game.core.ecs.systems.HomingSystem;
import io.w8.game.core.ecs.systems.MovementSystem;
import io.w8.game.core.ecs.systems.PickupSystem;
import io.w8.game.core.ecs.systems.PlayerControlSystem;
import io.w8.game.core.ecs.systems.ProjectileSystem;
import io.w8.game.core.ecs.systems.RenderSystem;
import io.w8.game.core.ecs.systems.WeaponSystem;
import io.w8.game.core.input.GameInput;
import io.w8.game.core.world.Arena;

public class GameEngine {
    private final Engine engine;
    private final RenderSystem renderSystem;
    private ImmutableArray<Entity> players;

    public GameEngine(OrthographicCamera camera, GameInput input, Arena arena) {
        engine = new Engine();
        renderSystem = new RenderSystem(camera, arena);
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());

        engine.addSystem(new PlayerControlSystem(input, arena));
        engine.addSystem(new EnemySystem(arena));
        engine.addSystem(new WeaponSystem(engine));
        engine.addSystem(new HomingSystem());
        engine.addSystem(new MovementSystem(arena));
        engine.addSystem(new CombatSystem(arena));
        engine.addSystem(new PickupSystem());
        engine.addSystem(new ProjectileSystem(arena));
        engine.addSystem(new EffectSystem());
        engine.addSystem(renderSystem);
    }

    public Engine getEngine() {
        return engine;
    }

    public void update(float deltaTime) {
        engine.update(Math.min(deltaTime, 0.05f));
    }

    public boolean isPlayerDead() {
        return players.size() > 0 && players.first().getComponent(PlayerComponent.class).dead;
    }

    public void render() {
        renderSystem.render();
    }

    public void dispose() {
        engine.removeAllEntities();
        engine.removeAllSystems();
        renderSystem.dispose();
    }
}
