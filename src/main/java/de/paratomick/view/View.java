package de.paratomick.view;

import de.paratomick.view.relequen.TimerView;
import de.paratomick.view.unitStats.UnitsView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class View extends BorderPane {

    VBox centerRoot;

    public View() {

        centerRoot = new VBox();
        setCenter(centerRoot);
    }

    public void setUpdateTimeView(UpdateTimeView updateTimeView) {
        setTop(updateTimeView);
    }

    public void setLeftView(LeftView leftView) {
        setLeft(leftView);
    }

    private void clear() {
        centerRoot.getChildren().clear();
    }

    public void showFileOptions(FileSelectorView fsv) {
        clear();

        centerRoot.getChildren().addAll(fsv);
    }

    public void showIngame(UnitsView unitsView, TimerView timerView) {
        clear();

        centerRoot.getChildren().addAll(unitsView, timerView);
    }

    public void setBackground(boolean b) {
        if (b) {
            centerRoot.getStyleClass().setAll("view_background");
        } else {
            centerRoot.getStyleClass().setAll("view_no_background");
        }
    }
}
