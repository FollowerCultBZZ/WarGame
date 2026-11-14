package io.w8.game.core.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.w8.game.core.ecs.effects.ParticleRenderer;
import io.w8.game.core.GameFonts;
import io.w8.game.core.ecs.components.EffectComponent;
import io.w8.game.core.ecs.components.EnemyComponent;
import io.w8.game.core.ecs.components.PlayerComponent;
import io.w8.game.core.ecs.components.ProjectileComponent;
import io.w8.game.core.ecs.components.PickupComponent;
import io.w8.game.core.ecs.components.ShapeComponent;
import io.w8.game.core.ecs.components.TransformComponent;
import io.w8.game.core.ecs.components.VelocityComponent;
import io.w8.game.core.world.Arena;

public class RenderSystem extends EntitySystem {
    private final ShapeRenderer renderer = new ShapeRenderer();
    private final ParticleRenderer particleRenderer = new ParticleRenderer(renderer);
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font;
    private final TextureAtlas atlas;
    private final TextureRegion playerRegion;
    private final TextureRegion bulletRegion;
    private final TextureRegion missileRegion;
    private final TextureRegion healthIcon;
    private final TextureRegion missileIcon;
    private final TextureRegion reloadIcon;
    private final TextureRegion countIcon;
    private final Animation<TextureRegion> fireAnimation;
    private final TextureRegion shieldRegion;
    private final TextureRegion buttonUp;
    private final Texture backgroundTexture;
    private final ShaderProgram shieldShader;
    private final GlyphLayout layout = new GlyphLayout();
    private float animationTime;
    private final OrthographicCamera camera;
    private final Arena arena;
    private ImmutableArray<Entity> entities;
    private ImmutableArray<Entity> players;
    private ImmutableArray<Entity> effects;
    private ImmutableArray<Entity> enemies;

    public RenderSystem(OrthographicCamera camera, Arena arena) {
        this.camera = camera;
        this.arena = arena;
        atlas = new TextureAtlas(Gdx.files.internal("atlases/game.atlas"));
        playerRegion = atlas.findRegion("player");
        bulletRegion = atlas.findRegion("bullet");
        missileRegion = atlas.findRegion("missile");
        healthIcon = atlas.findRegion("health-icon");
        missileIcon = atlas.findRegion("missile-icon");
        reloadIcon = atlas.findRegion("reload-icon");
        countIcon = atlas.findRegion("count-icon");
        shieldRegion = atlas.findRegion("shield");
        buttonUp = atlas.findRegion("button-up");
        backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));
        backgroundTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        font = GameFonts.create(24);

        String vertexShader =
                "attribute vec4 a_position;\n" +
                "attribute vec4 a_color;\n" +
                "attribute vec2 a_texCoord0;\n" +
                "uniform mat4 u_projTrans;\n" +
                "varying vec4 v_color;\n" +
                "varying vec2 v_texCoords;\n" +
                "void main(){ v_color=a_color; v_color.a*=255.0/254.0; " +
                "v_texCoords=a_texCoord0; gl_Position=u_projTrans*a_position; }";
        String fragmentShader =
                "#ifdef GL_ES\nprecision mediump float;\n#endif\n" +
                "varying vec4 v_color;\n" +
                "varying vec2 v_texCoords;\n" +
                "uniform sampler2D u_texture;\n" +
                "uniform float u_time;\n" +
                "void main(){\n" +
                " vec4 tex=texture2D(u_texture,v_texCoords);\n" +
                " float wave=0.5+0.5*sin(u_time*3.2+v_texCoords.y*17.0+v_texCoords.x*9.0);\n" +
                " float sweep=1.0-smoothstep(0.0,0.16,abs(fract((v_texCoords.x+v_texCoords.y)*1.35-u_time*0.22)-0.5));\n" +
                " vec3 tint=mix(vec3(0.18,0.68,1.0),vec3(0.72,0.96,1.0),wave);\n" +
                " vec3 color=tex.rgb*tint*(0.82+wave*0.28+sweep*0.42);\n" +
                " gl_FragColor=vec4(color,tex.a*v_color.a*0.84);\n" +
                "}";
        ShaderProgram compiledShieldShader = new ShaderProgram(vertexShader, fragmentShader);
        if (compiledShieldShader.isCompiled()) {
            shieldShader = compiledShieldShader;
        } else {
            Gdx.app.error("RenderSystem", "Shield shader compilation failed: " + compiledShieldShader.getLog());
            compiledShieldShader.dispose();
            shieldShader = null;
        }

        Array<TextureRegion> fireFrames = new Array<>();
        fireFrames.addAll(atlas.findRegions("fire"));
        fireAnimation = new Animation<>(0.075f, fireFrames, Animation.PlayMode.LOOP);
    }

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        entities = engine.getEntitiesFor(Family.all(TransformComponent.class, ShapeComponent.class).get());
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
        effects = engine.getEntitiesFor(Family.all(EffectComponent.class, TransformComponent.class).get());
        enemies = engine.getEntitiesFor(Family.all(EnemyComponent.class, TransformComponent.class).get());
    }

    public void render() {
        animationTime += Math.min(Gdx.graphics.getDeltaTime(), 0.05f);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        float bgScale = Math.max(arena.getWidth() / backgroundTexture.getWidth(),
                arena.getHeight() / backgroundTexture.getHeight());
        float bgWidth = backgroundTexture.getWidth() * bgScale;
        float bgHeight = backgroundTexture.getHeight() * bgScale;
        batch.draw(backgroundTexture, (arena.getWidth() - bgWidth) * 0.5f,
                (arena.getHeight() - bgHeight) * 0.5f, bgWidth, bgHeight);
        batch.end();

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

        renderer.end();

        particleRenderer.render(effects);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            ProjectileComponent projectile = entity.getComponent(ProjectileComponent.class);
            if (entity.getComponent(PickupComponent.class) != null
                    || entity.getComponent(PlayerComponent.class) != null
                    || (projectile != null && !projectile.enemyOwned)) continue;
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
            } else {
                renderer.rect(transform.x - shape.radius * 0.35f,
                        transform.y - shape.radius, shape.radius * 0.7f, shape.radius * 2f,
                        shape.radius * 0.35f, shape.radius, 1f, 1f, transform.rotation);
            }
        }

        if (players.size() > 0) {
            PlayerComponent player = players.first().getComponent(PlayerComponent.class);
            float ratio = Math.max(0f, Math.min(1f, player.health / player.maxHealth));
            float x = 44f;
            float y = arena.getHeight() - 52f;
            float width = 190f;
            float height = 13f;
            renderer.setColor(0.08f, 0.1f, 0.16f, 0.95f);
            renderer.rect(x, y, width, height);
            renderer.setColor(ratio > 0.55f ? 0.18f : 1f,
                    ratio > 0.55f ? 0.85f : ratio > 0.25f ? 0.68f : 0.12f,
                    ratio > 0.55f ? 0.35f : 0.12f, 1f);
            renderer.rect(x + 2f, y + 2f, (width - 4f) * ratio, height - 4f);

            float shieldRatio = player.maxShieldHealth <= 0f ? 0f
                    : Math.max(0f, Math.min(1f, player.shieldHealth / player.maxShieldHealth));
            if (player.shieldTimer > 0f && player.shieldHealth > 0f) {
                float shieldY = y - 19f;
                renderer.setColor(0.05f, 0.12f, 0.2f, 0.95f);
                renderer.rect(x, shieldY, width, height);
                renderer.setColor(0.15f, 0.72f, 1f, 1f);
                renderer.rect(x + 2f, shieldY + 2f, (width - 4f) * shieldRatio, height - 4f);
            }
        }

        for (int i = 0; i < enemies.size(); i++) {
            EnemyComponent enemy = enemies.get(i).getComponent(EnemyComponent.class);
            if (enemy.kind != 3) continue;
            float ratio = Math.max(0f, Math.min(1f, enemy.health / enemy.maxHealth));
            float x = arena.getWidth() * 0.18f;
            float y = arena.getHeight() - 112f;
            float width = arena.getWidth() * 0.64f;
            renderer.setColor(0.12f, 0.04f, 0.06f, 1f);
            renderer.rect(x, y, width, 18f);
            renderer.setColor(0.95f, 0.08f, 0.16f, 1f);
            renderer.rect(x + 2f, y + 2f, (width - 4f) * ratio, 14f);
        }
        renderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (int i = 0; i < players.size(); i++) {
            Entity entity = players.get(i);
            PlayerComponent player = entity.getComponent(PlayerComponent.class);
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            VelocityComponent velocity = entity.getComponent(VelocityComponent.class);
            float rotation = transform.rotation;
            float radians = (float) Math.toRadians(rotation);

            if (!player.dead) {
                TextureRegion flame = fireAnimation.getKeyFrame(animationTime);
                float flameX = transform.x + (float) Math.sin(radians) * 30f;
                float flameY = transform.y - (float) Math.cos(radians) * 30f;
                drawCentered(flame, flameX, flameY, 20f, 28f, rotation);
            }

            float playerHeight = 64f;
            float playerWidth = playerHeight * playerRegion.getRegionWidth()
                    / (float) playerRegion.getRegionHeight();
            drawCentered(playerRegion, transform.x, transform.y,
                    playerWidth, playerHeight, rotation);

            if (player.shieldTimer > 0f && player.shieldHealth > 0f && shieldRegion != null) {
                if (shieldShader != null) {
                    batch.setShader(shieldShader);
                    shieldShader.bind();
                    shieldShader.setUniformf("u_time", animationTime);
                }
                drawCentered(shieldRegion, transform.x, transform.y, 88f, 88f, rotation);
                if (shieldShader != null) batch.setShader(null);
            }
        }

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            ProjectileComponent projectile = entity.getComponent(ProjectileComponent.class);
            if (projectile == null || projectile.enemyOwned) continue;
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            TextureRegion region = projectile.rocket ? missileRegion : bulletRegion;
            float height = projectile.rocket ? 32f : 19f;
            float width = height * region.getRegionWidth() / (float) region.getRegionHeight();
            drawCentered(region, transform.x, transform.y, width, height, transform.rotation);
        }

        font.setColor(1f, 1f, 1f, 1f);
        font.getData().setScale(1f);
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            PickupComponent pickup = entity.getComponent(PickupComponent.class);
            if (pickup == null) continue;
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            TextureRegion icon = getPickupIcon(pickup.type);
            if (icon != null) {
                drawCentered(icon, transform.x, transform.y, 30f, 30f, 0f);
            } else {
                String label = pickup.type == PickupComponent.SPEED ? "S"
                        : pickup.type == PickupComponent.PIERCING ? "P" : "?";
                font.draw(batch, label, transform.x - 5f, transform.y + 7f);
            }
        }

        if (players.size() > 0) {
            PlayerComponent player = players.first().getComponent(PlayerComponent.class);
            font.getData().setScale(1.1f);
            String scoreText = "SCORE " + player.score;
            layout.setText(font, scoreText);
            font.draw(batch, scoreText, arena.getWidth() - layout.width - 32f,
                    arena.getHeight() - 38f);
            font.draw(batch, "LIVES " + player.lives, 44f, arena.getHeight() - 82f);
            float statusY = arena.getHeight() - 116f;
            if (player.shieldTimer > 0f && player.shieldHealth > 0f) {
                font.draw(batch, "SHIELD " + (int) Math.ceil(player.shieldHealth)
                        + "/" + (int) player.maxShieldHealth + "  "
                        + (int) Math.ceil(player.shieldTimer) + "s", 44f, statusY);
                statusY -= 30f;
            }
            if (player.invulnerabilityTimer > 0f) {
                font.draw(batch, "INVULNERABLE " + (int) Math.ceil(player.invulnerabilityTimer) + "s",
                        44f, statusY);
                statusY -= 30f;
            }
            if (player.fireRateOverdriveTimer > 0f) {
                font.draw(batch, "FIRE RATE OVERDRIVE x3.5  " + (int) Math.ceil(player.fireRateOverdriveTimer) + "s",
                        44f, statusY);
                statusY -= 30f;
            }
            if (player.multiShotOverdriveTimer > 0f) {
                font.draw(batch, "DOUBLE SHOTS  " + (int) Math.ceil(player.multiShotOverdriveTimer) + "s",
                        44f, statusY);
                statusY -= 30f;
            }
            if (player.homingLevels > 0) {
                font.draw(batch, "ROCKET RATE x" + String.format(java.util.Locale.US, "%.2f",
                        player.rocketFireRateMultiplier), 44f, statusY);
            }
            if (player.dead) {
                font.getData().setScale(3f);
                String gameOver = "GAME OVER";
                layout.setText(font, gameOver);
                font.draw(batch, gameOver, (arena.getWidth() - layout.width) * 0.5f,
                        arena.getHeight() * 0.5f + 115f);
                font.getData().setScale(1.5f);
                String finalScore = "FINAL SCORE " + player.score;
                layout.setText(font, finalScore);
                font.draw(batch, finalScore, (arena.getWidth() - layout.width) * 0.5f,
                        arena.getHeight() * 0.5f + 48f);

                float buttonWidth = 360f;
                float buttonHeight = 100f;
                float buttonX = (arena.getWidth() - buttonWidth) * 0.5f;
                float buttonY = arena.getHeight() * 0.5f - 170f;
                batch.draw(buttonUp, buttonX, buttonY, buttonWidth, buttonHeight);
                font.getData().setScale(1.6f);
                layout.setText(font, "RESTART");
                font.draw(batch, "RESTART",
                        (arena.getWidth() - layout.width) * 0.5f,
                        buttonY + (buttonHeight + layout.height) * 0.5f);
            }
        }
        for (int i = 0; i < enemies.size(); i++) {
            EnemyComponent enemy = enemies.get(i).getComponent(EnemyComponent.class);
            if (enemy.kind == 3) {
                font.getData().setScale(1.05f);
                String bossText = "BOSS " + enemy.bossNumber + "  STAGE " + enemy.bossStage;
                layout.setText(font, bossText);
                font.draw(batch, bossText, (arena.getWidth() - layout.width) * 0.5f,
                        arena.getHeight() - 88f);
            }
        }
        batch.end();
    }

    private TextureRegion getPickupIcon(int type) {
        switch (type) {
            case PickupComponent.HEAL: return healthIcon;
            case PickupComponent.HOMING: return missileIcon;
            case PickupComponent.FIRE_RATE: return reloadIcon;
            case PickupComponent.MULTI_SHOT: return countIcon;
            default: return null;
        }
    }

    private void drawCentered(TextureRegion region, float centerX, float centerY,
                              float width, float height, float rotation) {
        batch.draw(region,
                centerX - width * 0.5f, centerY - height * 0.5f,
                width * 0.5f, height * 0.5f,
                width, height, 1f, 1f, rotation);
    }

    public void dispose() {
        atlas.dispose();
        backgroundTexture.dispose();
        if (shieldShader != null) shieldShader.dispose();
        renderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
