package io.w8.game.core.ecs.components;

import com.badlogic.ashley.core.Component;

public class ShapeComponent implements Component {
    public static final int TRIANGLE = 0;
    public static final int CIRCLE = 1;
    public static final int RECTANGLE = 2;
    public static final int SQUARE = 3;

    public int type = TRIANGLE;
    public float radius = 18f;
    public float red = 0.2f;
    public float green = 0.85f;
    public float blue = 1f;
    public boolean hollow;
    public float rotationSpeed;
}
