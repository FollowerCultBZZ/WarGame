package io.w8.game.core.ecs;

import com.badlogic.ashley.core.Engine;
import io.w8.game.core.ecs.systems.HealthSystem;
import io.w8.game.core.ecs.systems.MovementSystem;

public class GameEngine {
    private final Engine engine;

    public GameEngine() {
        engine = new Engine();
        engine.addSystem(new MovementSystem());
        engine.addSystem(new HealthSystem());
    }

    public Engine getEngine() {
        return engine;
    }

    public void update(float deltaTime) {
        engine.update(Math.min(deltaTime, 0.05f));
    }

    public void dispose() {
        engine.removeAllEntities();
        engine.removeAllSystems();
    }
}
