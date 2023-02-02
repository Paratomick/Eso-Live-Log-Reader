package de.paratomick.view;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class UpdateTimeView extends HBox {
    Label timeLabel;

    public UpdateTimeView() {
        timeLabel = new Label();
        timeLabel.getStyleClass().add("time_label");
        getChildren().add(timeLabel);
    }

    public Label getTimeLabel() {
        return timeLabel;
    }
}
