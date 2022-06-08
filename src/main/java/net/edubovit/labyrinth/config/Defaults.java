package net.edubovit.labyrinth.config;

import java.time.Duration;

public interface Defaults {

    int DEFAULT_WIDTH = 20;

    int DEFAULT_HEIGHT = 20;

    int DEFAULT_CELL_SIZE = 20;

    int DEFAULT_CELL_BORDER = 1;

    int DEFAULT_OUTER_BORDER = 6;

    int VIEW_DISTANCE = 4;

    int STORE_LAST_IMAGES = 50;

    int IMAGES_CLEANUP_THRESHOLD = 1000;

    int SESSIONS_CLEANUP_THRESHOLD = 100;

    Duration SESSION_STORAGE_TIME = Duration.ofDays(1);

}
