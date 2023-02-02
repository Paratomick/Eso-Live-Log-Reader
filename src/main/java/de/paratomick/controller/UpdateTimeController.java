package de.paratomick.controller;

import de.paratomick.model.Model;
import de.paratomick.util.Unloadable;
import de.paratomick.view.UpdateTimeView;
import javafx.beans.binding.Bindings;

public class UpdateTimeController implements Unloadable {

    private UpdateTimeView updateTimeView;

    public UpdateTimeController() {
        updateTimeView = new UpdateTimeView();
    }

    public void init(Model model) {
        updateTimeView.getTimeLabel().textProperty().bind(
                Bindings.format("%5.1fs", model.timeSinceLastUpdateProperty().divide(1e9d))
        );
    }

    @Override
    public void unload() {
        updateTimeView.getTimeLabel().textProperty().unbind();
        updateTimeView.getTimeLabel().textProperty().set("");
    }

    public UpdateTimeView getView() {
        return updateTimeView;
    }
}
