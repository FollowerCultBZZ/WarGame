package io.w8.game.core.world.rules;

public class GameRules {
    private float movementSpeedMultiplier = 1f;
    private float searchSpeedMultiplier = 1f;
    private float resourceAmountMultiplier = 1f;
    private float resourceChanceMultiplier = 1f;

    public float getMovementSpeedMultiplier() {
        return movementSpeedMultiplier;
    }

    public void setMovementSpeedMultiplier(float value) {
        movementSpeedMultiplier = Math.max(0.1f, value);
    }

    public float getSearchSpeedMultiplier() {
        return searchSpeedMultiplier;
    }

    public void setSearchSpeedMultiplier(float value) {
        searchSpeedMultiplier = Math.max(0.1f, value);
    }

    public float getResourceAmountMultiplier() {
        return resourceAmountMultiplier;
    }

    public void setResourceAmountMultiplier(float value) {
        resourceAmountMultiplier = Math.max(0f, value);
    }

    public float getResourceChanceMultiplier() {
        return resourceChanceMultiplier;
    }

    public void setResourceChanceMultiplier(float value) {
        resourceChanceMultiplier = Math.max(0f, value);
    }
}
