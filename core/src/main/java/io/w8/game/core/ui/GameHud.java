package io.w8.game.core.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import io.w8.game.core.world.map.MapWorld;
import io.w8.game.core.world.map.TileMap;
import io.w8.game.core.world.player.Player;

public class GameHud implements Disposable {
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font = new BitmapFont();
    private final OrthographicCamera camera = new OrthographicCamera();
    private float width;
    private float height;

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    public boolean isHudTouch(float screenY) {
        float y = height - screenY;
        return y >= height - 72f || y <= 82f;
    }

    public void render(Player player, MapWorld world) {
        if (width <= 0 || height <= 0) return;

        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.10f, 0.09f, 0.07f, 0.96f);
        shapes.rect(0, height - 68f, width, 68f);
        shapes.setColor(0.24f, 0.20f, 0.14f, 1f);
        shapes.rect(0, height - 70f, width, 2f);
        shapes.setColor(0.10f, 0.09f, 0.07f, 0.97f);
        shapes.rect(0, 0, width, 78f);
        shapes.setColor(0.24f, 0.20f, 0.14f, 1f);
        shapes.rect(0, 78f, width, 2f);

        float section = width / 4f;
        for (int i = 0; i < 4; i++) {
            float x = i * section + 7f;
            shapes.setColor(i == 0 ? 0.35f : 0.18f, i == 0 ? 0.29f : 0.17f, 0.12f, 1f);
            shapes.rect(x, 8f, section - 14f, 60f);
            shapes.setColor(0.48f, 0.39f, 0.22f, 1f);
            shapes.rect(x, 8f, section - 14f, 2f);
        }
        shapes.end();

        int tileX = (int) Math.floor(player.getX() / world.getTileSize());
        int tileY = (int) Math.floor(player.getY() / world.getTileSize());
        int terrain = world.getTerrainId(tileX, tileY);
        String terrainName = terrain == TileMap.FOREST ? "Forest"
                : terrain == TileMap.WATER ? "Water" : "Wasteland";

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.setColor(0.91f, 0.85f, 0.69f, 1f);
        font.getData().setScale(1.15f);
        font.draw(batch, "DAY 1  |  08:30", 20f, height - 27f);
        font.draw(batch, "HEALTH 100%", width * 0.28f, height - 27f);
        font.draw(batch, "ENERGY 85%", width * 0.48f, height - 27f);
        font.draw(batch, "FOOD 12%", width * 0.68f, height - 27f);
        font.draw(batch, "SAFE", width - 75f, height - 27f);

        font.getData().setScale(1f);
        font.draw(batch, "MAP", section * 0.5f - 14f, 43f);
        font.draw(batch, "INVENTORY", section * 1.5f - 35f, 43f);
        font.draw(batch, "SEARCH", section * 2.5f - 28f, 43f);
        font.draw(batch, "CHARACTER", section * 3.5f - 38f, 43f);

        font.setColor(Color.WHITE);
        font.draw(batch, "CELL " + tileX + ", " + tileY + "   " + terrainName,
                18f, height - 88f);
        if (player.isMoving()) {
            font.draw(batch, "MOVING", width - 100f, height - 88f);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        shapes.dispose();
        batch.dispose();
        font.dispose();
    }
}
