package io.w8.game.core.ecs.components;

import com.badlogic.ashley.core.Component;

public class TeamComponent implements Component {
    public enum Team {
        NEUTRAL,
        BLUE,
        RED
    }

    public Team team = Team.NEUTRAL;

    public TeamComponent() {
    }

    public TeamComponent(Team team) {
        this.team = team;
    }
}
