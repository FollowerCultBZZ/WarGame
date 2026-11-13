package io.w8.game.core.ecs.effects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import io.w8.game.core.ecs.components.EffectComponent;

public class ParticleRenderer {
    private final ShapeRenderer shapes;

    public ParticleRenderer(ShapeRenderer shapes) {
        this.shapes = shapes;
    }

    public void render(ImmutableArray<Entity> entities) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < entities.size(); i++) {
            EffectComponent effect = entities.get(i).getComponent(EffectComponent.class);
            for (int p = 0; p < effect.particles.size; p++) {
                renderParticle(effect, effect.particles.get(p));
            }
        }
        shapes.end();
    }

    private void renderParticle(EffectComponent effect, EffectComponent.Particle particle) {
        float progress = MathUtils.clamp(particle.age / particle.lifetime, 0f, 1f);
        float red;
        float green;
        float blue;

        if (progress < 0.48f) {
            float t = progress / 0.48f;
            red = MathUtils.lerp(effect.startRed, effect.middleRed, t);
            green = MathUtils.lerp(effect.startGreen, effect.middleGreen, t);
            blue = MathUtils.lerp(effect.startBlue, effect.middleBlue, t);
        } else {
            float t = (progress - 0.48f) / 0.52f;
            red = MathUtils.lerp(effect.middleRed, effect.endRed, t);
            green = MathUtils.lerp(effect.middleGreen, effect.endGreen, t);
            blue = MathUtils.lerp(effect.middleBlue, effect.endBlue, t);
        }

        float alpha = 1f - progress;
        float size = particle.size * MathUtils.lerp(1f, particle.endSizeScale, progress);
        shapes.setColor(red, green, blue, alpha);

        if (particle.shape == EffectComponent.STREAK) {
            float angle = MathUtils.atan2Deg(particle.velocityY, particle.velocityX);
            shapes.rect(
                    particle.x, particle.y - particle.thickness * 0.5f,
                    0f, particle.thickness * 0.5f,
                    size, particle.thickness, 1f, 1f, angle
            );
        } else {
            shapes.circle(particle.x, particle.y, size, 8);
        }
    }
}
