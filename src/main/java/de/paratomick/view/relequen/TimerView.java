package de.paratomick.view.relequen;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;

public class TimerView extends ScrollPane {

    HBox box;

    public TimerView() {

        box = new HBox();
        setContent(box);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        box.prefWidthProperty().bind(widthProperty().subtract(10));
        getStyleClass().add("relequenBox");
        box.getStyleClass().add("relequenBox");
    }

    public void addTimer(TimeView timeView) {
        box.getChildren().add(timeView);
    }

    public void removeTimer(TimeView timeView) {
        box.getChildren().remove(timeView);
    }
}
