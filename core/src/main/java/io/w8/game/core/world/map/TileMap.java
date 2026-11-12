package io.w8.game.core.world.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;

import java.util.Arrays;

public class TileMap {

    public static final int FOREST = 1;
    public static final int WASTELAND = 2;
    public static final int WATER = 3;

    private final int chunkWidth;
    private final int chunkHeight;
    private final int chunkSize;
    private final byte[][] chunks;

    public TileMap(int chunkWidth, int chunkHeight, int chunkSize, String directory) {
        this.chunkWidth = chunkWidth;
        this.chunkHeight = chunkHeight;
        this.chunkSize = chunkSize;
        this.chunks = new byte[chunkWidth * chunkHeight][];

        for (int chunkY = 0; chunkY < chunkHeight; chunkY++) {
            for (int chunkX = 0; chunkX < chunkWidth; chunkX++) {
                String path = directory + "/" + chunkX + "_" + chunkY + ".png";
                FileHandle file = Gdx.files.internal(path);
                int index = chunkY * chunkWidth + chunkX;

                if (file.exists()) {
                    chunks[index] = loadChunk(file, chunkX, chunkY);
                } else {
                    chunks[index] = createWastelandChunk();
                }
            }
        }
    }

    public int getTerrainId(int tileX, int tileY) {
        if (tileX < 0 || tileY < 0
                || tileX >= chunkWidth * chunkSize
                || tileY >= chunkHeight * chunkSize) {
            return WASTELAND;
        }

        int chunkX = tileX / chunkSize;
        int chunkY = tileY / chunkSize;
        int localX = tileX % chunkSize;
        int localY = tileY % chunkSize;
        int chunkIndex = chunkY * chunkWidth + chunkX;

        return chunks[chunkIndex][localY * chunkSize + localX] & 0xFF;
    }

    private byte[] loadChunk(FileHandle file, int chunkX, int chunkY) {
        Pixmap pixmap = new Pixmap(file);

        try {
            if (pixmap.getWidth() != chunkSize || pixmap.getHeight() != chunkSize) {
                throw new IllegalArgumentException(
                        "Invalid chunk size in " + file.path()
                                + ": expected " + chunkSize + "x" + chunkSize
                                + ", got " + pixmap.getWidth() + "x" + pixmap.getHeight()
                );
            }

            byte[] tiles = new byte[chunkSize * chunkSize];

            for (int y = 0; y < chunkSize; y++) {
                for (int x = 0; x < chunkSize; x++) {
                    int sourceX = chunkSize - 1 - y;
                    int sourceY = x;
                    int rgb = pixmap.getPixel(sourceX, sourceY) >>> 8;
                    tiles[y * chunkSize + x] = (byte) getTerrainIdFromColor(
                            rgb,
                            chunkX * chunkSize + x,
                            chunkY * chunkSize + y,
                            file
                    );
                }
            }

            return tiles;
        } finally {
            pixmap.dispose();
        }
    }

    private int getTerrainIdFromColor(int rgb, int tileX, int tileY, FileHandle file) {
        if (rgb == 0x008000) return FOREST;
        if (rgb == 0x0000FF) return WATER;
        if (rgb == 0xFFFF00) return WASTELAND;

        throw new IllegalArgumentException(
                "Unknown map color #" + String.format("%06X", rgb)
                        + " at tile (" + tileX + ", " + tileY + ") in " + file.path()
        );
    }

    private byte[] createWastelandChunk() {
        byte[] tiles = new byte[chunkSize * chunkSize];
        Arrays.fill(tiles, (byte) WASTELAND);
        return tiles;
    }
}
