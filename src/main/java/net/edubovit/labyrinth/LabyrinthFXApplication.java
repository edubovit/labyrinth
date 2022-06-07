package net.edubovit.labyrinth;

import net.edubovit.labyrinth.domain.Labyrinth;
import net.edubovit.labyrinth.service.LabyrinthProcessor;
import net.edubovit.labyrinth.service.LabyrinthView;

import javafx.application.Application;
import javafx.stage.Stage;

import static net.edubovit.labyrinth.config.Defaults.DEFAULT_CELL_BORDER;
import static net.edubovit.labyrinth.config.Defaults.DEFAULT_CELL_SIZE;
import static net.edubovit.labyrinth.config.Defaults.DEFAULT_HEIGHT;
import static net.edubovit.labyrinth.config.Defaults.DEFAULT_OUTER_BORDER;
import static net.edubovit.labyrinth.config.Defaults.DEFAULT_WIDTH;

public class LabyrinthFXApplication extends Application {

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
        var service = new LabyrinthProcessor(
                new Labyrinth(width, height, readSeed()),
                new LabyrinthView(width, height, cellSize, cellBorder, outerBorder));
        service.init(primaryStage);
        service.generate();
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
