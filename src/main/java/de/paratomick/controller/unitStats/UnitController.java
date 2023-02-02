package de.paratomick.controller.unitStats;

import de.paratomick.model.Model;
import de.paratomick.model.Unit;
import de.paratomick.view.unitStats.UnitView;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.beans.property.ReadOnlyDoubleProperty;

public class UnitController {

    UnitView view;

    public UnitController(Model model, Unit unit) {

        view = new UnitView();

        view.getNameLabel().textProperty().bind(new When(unit.isPlayer()).then(unit.accountNameProperty()).otherwise(unit.characterNameProperty()));

        view.getHealthLabel().textProperty().bind(Bindings.format("%5d / %5d", unit.healthProperty(), unit.maxHealthProperty()));
        view.getMagickaLabel().textProperty().bind(Bindings.format("%5d / %5d", unit.magickaProperty(), unit.maxMagickaProperty()));
        view.getStaminaLabel().textProperty().bind(Bindings.format("%5d / %5d", unit.staminaProperty(), unit.maxStaminaProperty()));
        view.getUltLabel().textProperty().bind(Bindings.format("%3d / 500", unit.ultProperty()));
        view.getDamageLabel().textProperty().bind(Bindings.format("%6.0f DPS", unit.damageDoneProperty().multiply(model.dpsMultiplierProperty())));

        view.getHealthBar().progressProperty().bind(unit.healthProperty().divide(Bindings.max(1d, unit.maxHealthProperty())));
        view.getAbsorbBar().progressProperty().bind(unit.absorbProperty().divide(Bindings.max(1d, unit.maxHealthProperty())));
        view.getMagickaBar().progressProperty().bind(unit.magickaProperty().divide(Bindings.max(1d, unit.maxMagickaProperty())));
        view.getStaminaBar().progressProperty().bind(unit.staminaProperty().divide(Bindings.max(1d, unit.maxStaminaProperty())));
        view.getUltBar().progressProperty().bind(unit.ultProperty().divide(500d));
        view.getDamageBar().progressProperty().bind(
                unit.damageDoneProperty().multiply(model.dps.maxDmgMult)
        );
        view.getDamageBar2().progressProperty().bind(
                unit.damageDoneBossProperty().multiply(model.dps.maxDmgMult)
        );
    }

    public void setPrefWidth(ReadOnlyDoubleProperty parentWidthProperty) {
        view.prefWidthProperty().bind(parentWidthProperty);
    }

    public UnitView getView() {
        return view;
    }

    public void unbind() {
        view.getNameLabel().textProperty().unbind();
        view.getHealthLabel().textProperty().unbind();
        view.getMagickaLabel().textProperty().unbind();
        view.getStaminaLabel().textProperty().unbind();
        view.getUltLabel().textProperty().unbind();
        view.getDamageLabel().textProperty().unbind();
        view.getHealthBar().progressProperty().unbind();
        view.getAbsorbBar().progressProperty().unbind();
        view.getMagickaBar().progressProperty().unbind();
        view.getStaminaBar().progressProperty().unbind();
        view.getUltBar().progressProperty().unbind();
        view.getDamageBar().progressProperty().unbind();
    }
}
