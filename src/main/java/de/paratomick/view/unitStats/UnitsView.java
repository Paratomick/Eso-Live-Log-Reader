package de.paratomick.view.unitStats;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class UnitsView extends ScrollPane {

    VBox box;

    public UnitsView() {

        box = new VBox();
        getChildren().add(box);
        setContent(box);

        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        box.prefWidthProperty().bind(widthProperty().subtract(10));
    }

    public void addUnit(UnitView unitView) {
        box.getChildren().addAll(unitView);
    }

    public void removeUnit(UnitView unitView) {
        box.getChildren().remove(unitView);
    }

    public VBox getBox() {
        return box;
    }
}
