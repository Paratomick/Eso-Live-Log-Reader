package de.paratomick.view.relequen;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TimeView extends VBox {

    Label labelName;
    Label labelStacks;
    Label labelTimer;

    public TimeView() {

        labelName = new Label();
        labelStacks = new Label();
        labelTimer = new Label();

        getChildren().addAll(labelName, labelStacks, labelTimer);
        getStyleClass().add("relequen");

        labelStacks.getStyleClass().add("relequenStacks");
    }

    public Label getLabelName() {
        return labelName;
    }

    public Label getLabelStacks() {
        return labelStacks;
    }

    public Label getLabelTimer() {
        return labelTimer;
    }
}
