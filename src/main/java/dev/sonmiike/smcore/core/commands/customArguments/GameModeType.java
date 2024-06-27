package dev.sonmiike.smcore.core.commands.customArguments;

import lombok.Getter;

@Getter
public enum GameModeType {
    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    SPECTATOR(3)
    ;

    private final int id;

    GameModeType(int id) {
        this.id = id;
    }

    public static GameModeType fromId(int id) {
        for (GameModeType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid game mode ID: " + id);
    }
}
