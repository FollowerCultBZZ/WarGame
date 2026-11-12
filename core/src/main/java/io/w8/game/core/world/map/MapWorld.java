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

    public int getTerrainAt(float worldX, float worldY) {
        int tileX = (int) Math.floor(worldX / tileSize);
        int tileY = (int) Math.floor(worldY / tileSize);
        return tileMap.getTerrainId(tileX, tileY);
    }

    public float getMovementSpeedMultiplier(float worldX, float worldY) {
        return getTerrainAt(worldX, worldY) == TileMap.FOREST ? 0.55f : 1f;
    }

    public boolean isBlocked(float centerX, float centerY, float radius) {
        if (centerX - radius < 0f || centerY - radius < 0f
                || centerX + radius > getWorldWidth()
                || centerY + radius > getWorldHeight()) {
            return true;
        }

        int minTileX = (int) Math.floor((centerX - radius) / tileSize);
        int maxTileX = (int) Math.floor((centerX + radius) / tileSize);
        int minTileY = (int) Math.floor((centerY - radius) / tileSize);
        int maxTileY = (int) Math.floor((centerY + radius) / tileSize);
        float radiusSquared = radius * radius;

        for (int tileY = minTileY; tileY <= maxTileY; tileY++) {
            for (int tileX = minTileX; tileX <= maxTileX; tileX++) {
                if (getTerrainId(tileX, tileY) != TileMap.WATER) continue;

                float left = tileX * tileSize;
                float top = tileY * tileSize;
                float closestX = Math.max(left, Math.min(centerX, left + tileSize));
                float closestY = Math.max(top, Math.min(centerY, top + tileSize));
                float dx = centerX - closestX;
                float dy = centerY - closestY;

                if (dx * dx + dy * dy < radiusSquared) return true;
            }
        }

        return false;
    }
}
