package io.w8.game.core.world.map;

public class MapWorld {

    private final int chunkWidth;
    private final int chunkHeight;
    private final int chunkSize;
    private final int tileSize;
    private final TileMap tileMap;

    public MapWorld(int chunkWidth, int chunkHeight, int chunkSize, int tileSize) {
        this.chunkWidth = chunkWidth;
        this.chunkHeight = chunkHeight;
        this.chunkSize = chunkSize;
        this.tileSize = tileSize;
        this.tileMap = new TileMap(chunkWidth, chunkHeight, chunkSize, "map");
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getTilesWide() {
        return chunkWidth * chunkSize;
    }

    public int getTilesHigh() {
        return chunkHeight * chunkSize;
    }

    public float getWorldWidth() {
        return getTilesWide() * tileSize;
    }

    public float getWorldHeight() {
        return getTilesHigh() * tileSize;
    }

    public int getTerrainId(int tileX, int tileY) {
        return tileMap.getTerrainId(tileX, tileY);
    }
}
