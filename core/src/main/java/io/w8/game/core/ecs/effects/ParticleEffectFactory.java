package io.w8.game.core.ecs.effects;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import io.w8.game.core.ecs.components.EffectComponent;
import io.w8.game.core.ecs.components.TransformComponent;

public class ParticleEffectFactory {
    private final Engine engine;

    public ParticleEffectFactory(Engine engine) {
        this.engine = engine;
    }

    public void createShotSparks(float x, float y, float velocityX, float velocityY) {
        EffectComponent effect = createEffect();
        setWarmToAshGradient(effect, 1f, 0.98f, 0.68f);

        float baseAngle = MathUtils.atan2Deg(velocityY, velocityX) + 180f;
        int count = MathUtils.random(3, 5);

        for (int i = 0; i < count; i++) {
            float angle = baseAngle + MathUtils.random(-58f, 58f);
            float speed = MathUtils.random(95f, 230f);
            EffectComponent.Particle particle = createParticle(
                    x + MathUtils.random(-3f, 3f),
                    y + MathUtils.random(-3f, 3f),
                    angle, speed, MathUtils.random(6f, 12f),
                    MathUtils.random(0.16f, 0.28f)
            );
            particle.shape = EffectComponent.STREAK;
            particle.thickness = MathUtils.random(1.1f, 2.1f);
            particle.endSizeScale = 0.18f;
            particle.drag = 3.5f;
            effect.particles.add(particle);
        }

        add(effect);
    }

    public void createRocketTrail(float x, float y, float velocityX, float velocityY) {
        EffectComponent effect = createEffect();
        setWarmToAshGradient(effect, 1f, 0.38f, 0.06f);
        effect.middleRed = 1f;
        effect.middleGreen = 0.24f;
        effect.middleBlue = 0.025f;

        float baseAngle = MathUtils.atan2Deg(velocityY, velocityX) + 180f;
        int count = MathUtils.random(2, 3);

        for (int i = 0; i < count; i++) {
            float angle = baseAngle + MathUtils.random(-22f, 22f);
            float speed = MathUtils.random(25f, 85f);
            EffectComponent.Particle particle = createParticle(
                    x + MathUtils.random(-3f, 3f),
                    y + MathUtils.random(-3f, 3f),
                    angle, speed, MathUtils.random(3f, 6f),
                    MathUtils.random(0.24f, 0.42f)
            );
            particle.velocityX += velocityX * 0.16f;
            particle.velocityY += velocityY * 0.16f;
            particle.endSizeScale = 0.08f;
            particle.drag = 2.8f;
            effect.particles.add(particle);
        }

        add(effect);
    }

    public void createExplosion(float x, float y, float radius,
                                float red, float green, float blue) {
        EffectComponent effect = createEffect();
        setWarmToAshGradient(effect, red, green, blue);
        int count = MathUtils.clamp((int) (radius * 0.45f), 14, 30);
        float speedScale = MathUtils.clamp(radius / 34f, 0.7f, 2.2f);

        for (int i = 0; i < count; i++) {
            float angle = MathUtils.random(360f);
            float speed = MathUtils.random(85f, 245f) * speedScale;
            EffectComponent.Particle particle = createParticle(
                    x, y, angle, speed,
                    MathUtils.random(2.5f, 6.5f) * MathUtils.clamp(speedScale, 0.8f, 1.35f),
                    MathUtils.random(0.58f, 0.95f)
            );
            particle.endSizeScale = 0.12f;
            effect.particles.add(particle);
        }

        add(effect);
    }

    private EffectComponent createEffect() {
        return new EffectComponent();
    }

    private EffectComponent.Particle createParticle(
            float x, float y, float angle, float speed, float size, float lifetime) {
        EffectComponent.Particle particle = new EffectComponent.Particle();
        particle.x = x;
        particle.y = y;
        particle.velocityX = MathUtils.cosDeg(angle) * speed;
        particle.velocityY = MathUtils.sinDeg(angle) * speed;
        particle.size = size;
        particle.lifetime = lifetime;
        return particle;
    }

    private void setWarmToAshGradient(EffectComponent effect, float red, float green, float blue) {
        effect.startRed = red;
        effect.startGreen = green;
        effect.startBlue = blue;
        effect.middleRed = 1f;
        effect.middleGreen = 0.34f;
        effect.middleBlue = 0.035f;
        effect.endRed = 0.42f;
        effect.endGreen = 0.45f;
        effect.endBlue = 0.48f;
    }

    private void add(EffectComponent effect) {
        Entity entity = engine.createEntity();
        entity.add(new TransformComponent());
        entity.add(effect);
        engine.addEntity(entity);
    }
}
