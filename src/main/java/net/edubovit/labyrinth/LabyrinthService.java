package net.edubovit.labyrinth;

import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LabyrinthService {

    private final Labyrinth labyrinth;

    private final LabyrinthView view;

    private final Scene scene;

    public LabyrinthService(Labyrinth labyrinth, LabyrinthView view) {
        this.labyrinth = labyrinth;
        this.view = view;
        scene = new Scene(view.getPane());
    }

    public void init(Stage stage) {
        view.drawOuterBorders(labyrinth.getEnter(), labyrinth.getExit());
        stage.setScene(scene);
        stage.show();
    }

    public void digUntilReady() {
        while (digOne());
        view.drawOuterBorders(labyrinth.getEnter(), labyrinth.getExit());
    }

    public boolean digOne() {
        var chosenWay = labyrinth.chooseRandomWay();
        if (chosenWay == null) {
            return false;
        } else {
            labyrinth.digTunnel(chosenWay, view::drawCell);
            return true;
        }
    }

}
