package de.paratomick.controller;

import de.paratomick.App;
import de.paratomick.view.LeftView;
import javafx.application.Platform;

public class LeftController {

    private LeftView view;

    public LeftController(Controller controller) {

        view = new LeftView();

        view.getBtnClose().setOnAction(actionEvent -> Platform.exit());
        view.getBtnBack().setOnAction(actionEvent -> controller.showFileOptions());

        App.getPrimaryStage().focusedProperty().addListener((observable, oldValue, newValue) -> {
            view.setVisible(newValue);
        });
    }

    public LeftView getView() {
        return view;
    }
}
