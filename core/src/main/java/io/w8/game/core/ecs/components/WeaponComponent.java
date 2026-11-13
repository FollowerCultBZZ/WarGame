package io.w8.game.core.ecs.components;

import com.badlogic.ashley.core.Component;

public class WeaponComponent implements Component {
    public float fireInterval = 0.24f;
    public float fireTimer;
    public float projectileSpeed = 900f;
}
