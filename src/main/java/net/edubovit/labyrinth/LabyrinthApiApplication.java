package net.edubovit.labyrinth;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LabyrinthApiApplication {

    public static void main(String[] args) {
//        SpringApplication.run(LabyrinthApiApplication.class, args);
        Application.launch(JavaFXRunner.class, args);
    }

}
