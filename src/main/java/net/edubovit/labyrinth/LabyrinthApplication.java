package net.edubovit.labyrinth;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LabyrinthApplication extends Application {

    private static final int DEFAULT_WIDTH = 20;

    private static final int DEFAULT_HEIGHT = 20;

    private static final int DEFAULT_CELL_SIZE = 20;

    private static final int DEFAULT_CELL_BORDER = 1;

    private static final int DEFAULT_OUTER_BORDER = 5;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        int width = readIntParameter("width", DEFAULT_WIDTH);
        int height = readIntParameter("height", DEFAULT_HEIGHT);
        int cellSize = readIntParameter("cell", DEFAULT_CELL_SIZE);
        int cellBorder = readIntParameter("cell-border", DEFAULT_CELL_BORDER);
        int outerBorder = readIntParameter("outer-border", DEFAULT_OUTER_BORDER);
        int frameTime = readIntParameter("time", 50);
        var service = new LabyrinthService(
                new Labyrinth(width, height, readSeed()),
                new LabyrinthView(width, height, cellSize, cellBorder, outerBorder));
        service.init(primaryStage);
        var timeline = new Timeline(new KeyFrame(Duration.ZERO), new KeyFrame(Duration.millis(frameTime), event -> service.digOne()));
        timeline.setCycleCount(-1);
        timeline.play();
    }

    private int readIntParameter(String name, int defaultValue) {
        String parameter = getParameters().getNamed().get(name);
        if (parameter == null || parameter.isBlank()) {
            return defaultValue;
        } else {
            return Integer.parseInt(parameter);
        }
    }

    private long readSeed() {
        String parameter = getParameters().getNamed().get("seed");
        if (parameter == null || parameter.isBlank()) {
            return System.currentTimeMillis();
        } else {
            return Long.parseLong(parameter);
        }
    }

}
