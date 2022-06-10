package net.edubovit.labyrinth.dto;

import net.edubovit.labyrinth.config.Defaults;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateGameRequestDTO(int width, int height, long seed) {

    @JsonCreator
    public CreateGameRequestDTO(@JsonProperty("width") Integer width,
                                @JsonProperty("height") Integer height,
                                @JsonProperty("seed") Long seed) {
        this(
                readPositiveIntOrDefault(width, Defaults.DEFAULT_WIDTH),
                readPositiveIntOrDefault(height, Defaults.DEFAULT_HEIGHT),
                readSeed(seed)
        );
    }

    public static CreateGameRequestDTO defaultGame() {
        return new CreateGameRequestDTO(
                Defaults.DEFAULT_WIDTH,
                Defaults.DEFAULT_HEIGHT,
                System.currentTimeMillis()
        );
    }

    private static int readPositiveIntOrDefault(Integer value, int defaultValue) {
        return value == null || value <= 0 ? defaultValue : value;
    }

    private static long readSeed(Long seed) {
        return seed == null ? System.currentTimeMillis() : seed;
    }

}
