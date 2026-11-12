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
import io.w8.game.core.world.time.GameTime;
import io.w8.game.core.world.search.SearchSystem;

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
        return y >= height - 94f || y <= 116f;
    }

    public boolean isSearchButton(int screenX, int screenY) {
        float y = height - screenY;
        return screenX >= width * 0.5f && screenX < width * 0.75f
                && y >= 0f && y <= 112f;
    }

    public void render(Player player, MapWorld world, GameTime gameTime, SearchSystem searchSystem) {
        if (width <= 0 || height <= 0) return;

        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.10f, 0.09f, 0.07f, 0.96f);
        shapes.rect(0, height - 112f, width, 88f);
        shapes.setColor(0.24f, 0.20f, 0.14f, 1f);
        shapes.rect(0, height - 90f, width, 2f);
        shapes.setColor(0.10f, 0.09f, 0.07f, 0.97f);
        shapes.rect(0, 0, width, 112f);
        shapes.setColor(0.24f, 0.20f, 0.14f, 1f);
        shapes.rect(0, 112f, width, 2f);

        float section = width / 4f;
        for (int i = 0; i < 4; i++) {
            float x = i * section + 7f;
            shapes.setColor(i == 0 ? 0.35f : 0.18f, i == 0 ? 0.29f : 0.17f, 0.12f, 1f);
            shapes.rect(x, 10f, section - 14f, 88f);
            shapes.setColor(0.48f, 0.39f, 0.22f, 1f);
            shapes.rect(x, 10f, section - 14f, 3f);
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
        font.getData().setScale(Math.max(1.45f, height / 440f));
        font.draw(batch, gameTime.format(), 20f, height - 36f);
        font.draw(batch, "HEALTH 100%", width * 0.28f, height - 27f);
        font.draw(batch, "ENERGY 85%", width * 0.48f, height - 27f);
        font.draw(batch, "FOOD 12%", width * 0.68f, height - 27f);
        font.draw(batch, "SAFE", width - 75f, height - 27f);

        font.getData().setScale(Math.max(1.35f, height / 470f));
        font.draw(batch, "MAP", section * 0.5f - 20f, 62f);
        font.draw(batch, "INVENTORY", section * 1.5f - 58f, 62f);
        font.draw(batch, "SEARCH", section * 2.5f - 43f, 62f);
        font.draw(batch, "CHARACTER", section * 3.5f - 61f, 62f);

        font.setColor(Color.WHITE);
        font.draw(batch, "CELL " + tileX + ", " + tileY + "   " + terrainName,
                18f, height - 88f);
        if (player.isMoving()) {
            font.draw(batch, "MOVING", width - 100f, height - 88f);
        }
        if (searchSystem.isSearching()) {
            font.draw(batch, "SEARCHING  " + String.format(java.util.Locale.ROOT, "%.1f", searchSystem.getRemainingGameMinutes()) + " MIN",
                    18f, height - 142f);
        } else if (!searchSystem.getResultsText().isEmpty()) {
            font.draw(batch, searchSystem.getStatus() + ": " + searchSystem.getResultsText(),
                    18f, height - 142f);
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
