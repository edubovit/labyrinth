package net.edubovit.labyrinth.config;

import java.time.Duration;

public interface Defaults {

    int DEFAULT_WIDTH = 20;

    int DEFAULT_HEIGHT = 20;

    int VIEW_DISTANCE = 4;

    int SESSIONS_CLEANUP_THRESHOLD = 100;

    Duration SESSION_STORAGE_TIME = Duration.ofDays(1);

    int STALE_SESSION_TURNS = 10;

    Duration STALE_SESSION_STORAGE_TIME = Duration.ofMinutes(10);

}
