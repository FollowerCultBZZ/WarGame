package io.w8.game.core.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteComponent implements Component {
    public TextureRegion region;
    public float width;
    public float height;
    public int layer;
}
