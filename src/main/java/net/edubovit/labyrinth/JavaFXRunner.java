package net.edubovit.labyrinth;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFXRunner extends Application {

    private ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    }

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(LabyrinthApiApplication.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

}
