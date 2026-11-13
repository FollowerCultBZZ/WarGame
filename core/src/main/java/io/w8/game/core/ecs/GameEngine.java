package io.w8.game.core.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.w8.game.core.ecs.systems.MovementSystem;
import io.w8.game.core.ecs.systems.PlayerControlSystem;
import io.w8.game.core.ecs.systems.ProjectileSystem;
import io.w8.game.core.ecs.systems.RenderSystem;
import io.w8.game.core.ecs.systems.WeaponSystem;
import io.w8.game.core.input.GameInput;
import io.w8.game.core.world.Arena;

public class GameEngine {
    private final Engine engine;
    private final RenderSystem renderSystem;

    public GameEngine(OrthographicCamera camera, GameInput input, Arena arena) {
        engine = new Engine();
        renderSystem = new RenderSystem(camera, arena);

        engine.addSystem(new PlayerControlSystem(input, arena));
        engine.addSystem(new WeaponSystem(engine));
        engine.addSystem(new MovementSystem(arena));
        engine.addSystem(new ProjectileSystem(arena));
        engine.addSystem(renderSystem);
    }

    public Engine getEngine() {
        return engine;
    }

    public void update(float deltaTime) {
        engine.update(Math.min(deltaTime, 0.05f));
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
