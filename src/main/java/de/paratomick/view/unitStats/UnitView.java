package de.paratomick.view.unitStats;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class UnitView extends HBox {

    Label nameLabel;
    Label healthLabel;
    Label magickaLabel;
    Label staminaLabel;
    Label ultLabel;
    Label damageLabel;

    ProgressBar healthBar;
    ProgressBar absorbBar;
    ProgressBar magickaBar;
    ProgressBar staminaBar;
    ProgressBar ultBar;
    ProgressBar damageBar;
    ProgressBar damageBar2;

    VBox nameBox;
    StackPane healthStack, magickaStack, staminaStack, ultStack, damageStack;

    public UnitView() {
        nameLabel = new Label("Charactername");
        nameBox = new VBox(nameLabel);
        nameLabel.getStyleClass().add("player_label");

        healthBar = new ProgressBar();
        absorbBar = new ProgressBar();
        healthLabel = new Label("21/42 50%");
        healthStack = new StackPane(healthBar, absorbBar, healthLabel);
        StackPane.setAlignment(absorbBar, Pos.BOTTOM_CENTER);

        magickaBar = new ProgressBar();
        magickaLabel = new Label("21/42 50%");
        magickaStack = new StackPane(magickaBar, magickaLabel);

        staminaBar = new ProgressBar();
        staminaLabel = new Label("21/42 50%");
        staminaStack = new StackPane(staminaBar, staminaLabel);

        ultBar = new ProgressBar();
        ultLabel = new Label("21/42 50%");
        ultStack = new StackPane(ultBar, ultLabel);

        damageBar = new ProgressBar();
        damageBar2 = new ProgressBar();
        damageLabel = new Label("103598");
        damageStack = new StackPane(damageBar, damageBar2, damageLabel);


        getChildren().addAll(nameLabel, healthStack, magickaStack, staminaStack, ultStack, damageStack);


        // Style
        healthBar.getStyleClass().add("health_bar");
        absorbBar.getStyleClass().add("absorb_bar");
        magickaBar.getStyleClass().add("magicka_bar");
        staminaBar.getStyleClass().add("stamina_bar");
        ultBar.getStyleClass().add("ult_bar");
        damageBar.getStyleClass().add("health_bar");
        damageBar2.getStyleClass().add("absorb_bar");

        nameBox.prefWidthProperty().bind(widthProperty().divide(11d).multiply(2d));
        healthStack.prefWidthProperty().bind(widthProperty().divide(11d).multiply(2d));
        magickaStack.prefWidthProperty().bind(widthProperty().divide(11d).multiply(2d));
        staminaStack.prefWidthProperty().bind(widthProperty().divide(11d).multiply(2d));
        ultStack.prefWidthProperty().bind(widthProperty().divide(11d));
        damageStack.prefWidthProperty().bind(widthProperty().divide(11d).multiply(2d));
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public Label getHealthLabel() {
        return healthLabel;
    }

    public Label getMagickaLabel() {
        return magickaLabel;
    }

    public Label getStaminaLabel() {
        return staminaLabel;
    }

    public Label getUltLabel() {
        return ultLabel;
    }

    public Label getDamageLabel() {
        return damageLabel;
    }

    public ProgressBar getHealthBar() {
        return healthBar;
    }

    public ProgressBar getAbsorbBar() {
        return absorbBar;
    }

    public ProgressBar getMagickaBar() {
        return magickaBar;
    }

    public ProgressBar getStaminaBar() {
        return staminaBar;
    }

    public ProgressBar getUltBar() {
        return ultBar;
    }

    public ProgressBar getDamageBar() {
        return damageBar;
    }

    public ProgressBar getDamageBar2() {
        return damageBar2;
    }
}
