package io.w8.game.core.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

public class EffectComponent implements Component {
    public static final int CIRCLE = 0;
    public static final int STREAK = 1;

    public float startRed = 1f;
    public float startGreen = 0.94f;
    public float startBlue = 0.12f;
    public float middleRed = 1f;
    public float middleGreen = 0.34f;
    public float middleBlue = 0.035f;
    public float endRed = 0.42f;
    public float endGreen = 0.45f;
    public float endBlue = 0.48f;

    public final Array<Particle> particles = new Array<>();

    public static class Particle {
        public int shape = CIRCLE;
        public float x;
        public float y;
        public float velocityX;
        public float velocityY;
        public float size;
        public float thickness = 2f;
        public float age;
        public float lifetime = 0.8f;
        public float drag = 1.8f;
        public float endSizeScale = 0.25f;
    }
}
