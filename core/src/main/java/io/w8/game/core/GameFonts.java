package io.w8.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public final class GameFonts {
    private GameFonts() {
    }

    public static BitmapFont create(int size) {
        FreeTypeFontGenerator generator =
                new FreeTypeFontGenerator(Gdx.files.internal("font/RussoOne-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = Color.WHITE;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.borderWidth = 0f;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }
}
