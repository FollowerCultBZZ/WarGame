package io.w8.game.core.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

public class EffectComponent implements Component {
    public float age;
    public float duration = 0.95f;
    public float startRed = 1f;
    public float startGreen = 0.94f;
    public float startBlue = 0.12f;
    public final Array<Particle> particles = new Array<>();

    public static class Particle {
        public float x;
        public float y;
        public float velocityX;
        public float velocityY;
        public float size;
        public float age;
        public float lifetime;
    }
}
