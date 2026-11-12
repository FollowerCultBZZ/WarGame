package io.w8.game.core.world.time;

public class GameTime {
    private static final float GAME_SECONDS_PER_REAL_SECOND = 60f;
    private static final int SECONDS_PER_DAY = 24 * 60 * 60;

    private float totalSeconds = 8.5f * 60f * 60f;

    public void update(float delta) {
        if (delta <= 0f) return;
        totalSeconds += delta * GAME_SECONDS_PER_REAL_SECOND;
    }

    public int getDay() {
        return (int) (totalSeconds / SECONDS_PER_DAY) + 1;
    }

    public int getHour() {
        return ((int) totalSeconds % SECONDS_PER_DAY) / 3600;
    }

    public int getMinute() {
        return (((int) totalSeconds % SECONDS_PER_DAY) % 3600) / 60;
    }

    public String format() {
        return String.format(
                java.util.Locale.ROOT,
                "DAY %d  |  %02d:%02d",
                getDay(),
                getHour(),
                getMinute()
        );
    }
}
