package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.domain.Labyrinth;

import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LabyrinthProcessor {

    private final Labyrinth labyrinth;

    private final LabyrinthView view;

    public void init(Stage stage) {
        view.drawOuterBorders(labyrinth.getEnter(), labyrinth.getExit());
        stage.setScene(new Scene(view.getPane()));
        stage.show();
    }

    public void generate() {
        while (digOne());
        view.drawOuterBorders(labyrinth.getEnter(), labyrinth.getExit());
    }

    public WritableImage printMap() {
        return view.snapshot();
    }

    private boolean digOne() {
        var chosenWay = labyrinth.chooseRandomWay();
        if (chosenWay == null) {
            return false;
        } else {
            labyrinth.digTunnel(chosenWay, view::drawCell);
            return true;
        }
    }

}
