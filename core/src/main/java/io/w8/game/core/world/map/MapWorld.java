package io.w8.game.core.world.map;

public class MapWorld {

    private final int chunkWidth;
    private final int chunkHeight;
    private final int chunkSize;
    private final int tileSize;

    public MapWorld(int chunkWidth, int chunkHeight, int chunkSize, int tileSize) {
        this.chunkWidth = chunkWidth;
        this.chunkHeight = chunkHeight;
        this.chunkSize = chunkSize;
        this.tileSize = tileSize;
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
        int patchX = Math.floorDiv(tileX, 5);
        int patchY = Math.floorDiv(tileY, 5);
        int patch = hash(patchX, patchY);
        if (Math.floorMod(patch, 19) == 0) return 3;
        if (Math.floorMod(patch, 4) == 0) return 1;
        return 2;
    }

    private int hash(int x, int y) {
        int value = x * 0x45d9f3b ^ y * 0x119de1f3;
        value = (value ^ (value >>> 16)) * 0x45d9f3b;
        return value ^ (value >>> 16);
    }
}
