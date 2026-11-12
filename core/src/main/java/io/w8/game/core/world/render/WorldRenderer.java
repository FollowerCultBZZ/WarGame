package io.w8.game.core.world.render;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import io.w8.game.core.world.map.MapWorld;
import io.w8.game.core.world.map.TileMap;

public class WorldRenderer {

    private final SpriteBatch batch;
    private final MapWorld world;
    private final Array<AtlasRegion> forest;
    private final Array<AtlasRegion> wasteland;
    private final Array<AtlasRegion> water;

    public WorldRenderer(SpriteBatch batch, TextureAtlas atlas, MapWorld world) {
        this.batch = batch;
        this.world = world;
        forest = atlas.findRegions("forest");
        wasteland = atlas.findRegions("grass");
        water = atlas.findRegions("water");

        if (forest.size == 0 || wasteland.size == 0 || water.size == 0) {
            throw new IllegalStateException(
                    "Atlas must contain forest, grass (wasteland) and water regions"
            );
        }
    }

    public void render(OrthographicCamera camera) {
        int tileSize = world.getTileSize();
        float visibleWidth = camera.viewportWidth * camera.zoom;
        float visibleHeight = camera.viewportHeight * camera.zoom;
        int minX = Math.max(0, (int) Math.floor((camera.position.x - visibleWidth * 0.5f) / tileSize));
        int minY = Math.max(0, (int) Math.floor((camera.position.y - visibleHeight * 0.5f) / tileSize));
        int maxX = Math.min(world.getTilesWide() - 1,
                (int) Math.ceil((camera.position.x + visibleWidth * 0.5f) / tileSize));
        int maxY = Math.min(world.getTilesHigh() - 1,
                (int) Math.ceil((camera.position.y + visibleHeight * 0.5f) / tileSize));

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (int tileY = minY; tileY <= maxY; tileY++) {
            for (int tileX = minX; tileX <= maxX; tileX++) {
                Array<AtlasRegion> variants = variantsFor(world.getTerrainId(tileX, tileY));
                int variant = Math.floorMod(hash(tileX, tileY), variants.size);
                batch.draw(variants.get(variant), tileX * tileSize, tileY * tileSize, tileSize, tileSize);
            }
        }
        batch.end();
    }

    private Array<AtlasRegion> variantsFor(int terrainId) {
        if (terrainId == TileMap.FOREST) return forest;
        if (terrainId == TileMap.WATER) return water;
        return wasteland;
    }

    private int hash(int x, int y) {
        int value = x * 0x45d9f3b ^ y * 0x119de1f3;
        value = (value ^ (value >>> 16)) * 0x45d9f3b;
        return value ^ (value >>> 16);
    }

    public void dispose() {
    }
}
