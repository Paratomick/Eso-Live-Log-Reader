package de.paratomick.view;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class LeftView extends VBox {

    Button btnClose, btnBack;

    public LeftView() {
        btnClose = new Button("X");
        btnBack = new Button("<");
        getChildren().addAll(btnClose, btnBack);

        btnClose.getStyleClass().addAll("leftButton", "btnClose");
        btnBack.getStyleClass().addAll("leftButton", "btnBack");
    }

    public Button getBtnClose() {
        return btnClose;
    }

    public Button getBtnBack() {
        return btnBack;
    }

    public void setVisibility(boolean visibility) {
        setVisible(visibility);
        setDisable(visibility);
    }
}
