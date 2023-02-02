package de.paratomick.controller.relequen;

import de.paratomick.model.Model;
import de.paratomick.model.Unit;
import de.paratomick.view.relequen.TimeView;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;

public class TimeController {

    private TimeView view;

    public TimeController(Model model, Unit unit) {
        view = new TimeView();

        view.getLabelStacks().textProperty().bind(unit.relequenProperty().asString());

        var relequenTime = Bindings.divide(unit.relequenTimecodeProperty()
                .subtract(model.estimatedTimcodeProperty()), 1000d).add(5);
        view.getLabelTimer().textProperty().bind(Bindings.format("%2.1f", Bindings.max(0,relequenTime)));
        view.styleProperty().bind(
                new When(relequenTime.lessThan(0)).then(
                        "-fx-text-fill: red; -fx-background-color: rgba(95, 0, 0, 0.5);"
                ).otherwise(
                        ""
                ));
        view.visibleProperty().bind(relequenTime.greaterThan(-20));
        view.managedProperty().bind(relequenTime.greaterThan(-20));
    }

    public void unbind() {
        view.visibleProperty().unbind();
        view.managedProperty().unbind();
        view.styleProperty().unbind();
        view.getLabelTimer().textProperty().unbind();
        view.getLabelStacks().textProperty().unbind();
    }

    public TimeView getView() {
        return view;
    }
}
