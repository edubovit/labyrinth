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

    private Labyrinth.Way way;

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
        while (digTunnel());
        view.drawOuterBorders(labyrinth.getEnter(), labyrinth.getExit());
    }

    public void digOne() {
        if (way == null) {
            way = labyrinth.chooseRandomWay();
        }
        if (way == null) {
            return;
        }
        way = labyrinth.digOne(way, view::drawCell);
    }

    private boolean digTunnel() {
        var chosenWay = labyrinth.chooseRandomWay();
        if (chosenWay == null) {
            return false;
        } else {
            labyrinth.digTunnel(chosenWay, view::drawCell);
            return true;
        }
    }

}
