package io.w8.game.core.world.search;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import io.w8.game.core.world.map.TileMap;
import io.w8.game.core.world.rules.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class SearchSystem {
    private static final float BASE_SEARCH_TIME = 10f * 60f;

    private final JsonValue tables;
    private final Random random = new Random();
    private final List<String> results = new ArrayList<>();

    private boolean searching;
    private float elapsedGameSeconds;
    private float requiredGameSeconds;
    private String status = "Ready to search";

    public SearchSystem() {
        tables = new JsonReader().parse(Gdx.files.internal("data/search_tables.json"));
    }

    public void start(int terrainId, GameRules rules) {
        if (searching) {
            status = "Search already in progress";
            return;
        }

        if (terrainId != TileMap.FOREST
                && terrainId != TileMap.WASTELAND
                && terrainId != TileMap.WATER) {
            status = "Nothing to search here";
            return;
        }

        results.clear();
        elapsedGameSeconds = 0f;
        requiredGameSeconds = BASE_SEARCH_TIME / rules.getSearchSpeedMultiplier();
        searching = true;
        status = "Searching...";
    }

    public void update(float delta, GameRules rules) {
        if (!searching || delta <= 0f) return;

        elapsedGameSeconds += delta * 60f * rules.getSearchSpeedMultiplier();
        if (elapsedGameSeconds >= requiredGameSeconds) {
            finishSearch(terrainIdForCurrentSearch, rules);
        }
    }

    private int terrainIdForCurrentSearch;

    public void start(int terrainId, GameRules rules, boolean ignored) {
        terrainIdForCurrentSearch = terrainId;
        start(terrainId, rules);
    }

    private void finishSearch(int terrainId, GameRules rules) {
        JsonValue resources = tables.get(Integer.toString(terrainId));
        if (resources != null) {
            for (JsonValue item = resources.child; item != null; item = item.next) {
                float chance = Math.min(100f,
                        item.getFloat("chance") * rules.getResourceChanceMultiplier());
                if (random.nextFloat() * 100f >= chance) continue;

                int min = item.getInt("min");
                int max = item.getInt("max");
                int amount = min + random.nextInt(max - min + 1);
                amount = Math.round(amount * rules.getResourceAmountMultiplier());
                if (amount <= 0) continue;

                results.add(item.getString("name") + " x" + amount);
            }
        }

        if (results.isEmpty()) {
            results.add("Nothing found");
        }

        searching = false;
        status = "Search complete";
    }

    public boolean isSearching() {
        return searching;
    }

    public float getProgress() {
        if (!searching || requiredGameSeconds <= 0f) return 0f;
        return Math.min(1f, elapsedGameSeconds / requiredGameSeconds);
    }

    public float getRemainingGameMinutes() {
        return Math.max(0f, requiredGameSeconds - elapsedGameSeconds) / 60f;
    }

    public String getStatus() {
        return status;
    }

    public String getResultsText() {
        if (results.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();
        for (String result : results) {
            if (builder.length() > 0) builder.append("   |   ");
            builder.append(result);
        }
        return builder.toString();
    }
}
