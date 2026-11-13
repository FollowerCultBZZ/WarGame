package io.w8.game.core.ecs.components;

import com.badlogic.ashley.core.Component;

public class PickupComponent implements Component {
    public static final int SPEED = 0;
    public static final int MULTI_SHOT = 1;
    public static final int FIRE_RATE = 2;
    public static final int HEAL = 3;
    public static final int HOMING = 4;
    public static final int PIERCING = 5;

    public int type;

    public PickupComponent() {
    }

    public PickupComponent(int type) {
        this.type = type;
    }
}
