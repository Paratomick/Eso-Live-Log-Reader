package de.paratomick.controller.relequen;

import de.paratomick.model.Model;
import de.paratomick.model.Unit;
import de.paratomick.util.Unloadable;
import de.paratomick.view.relequen.TimerView;
import javafx.collections.ListChangeListener;

import java.util.TreeMap;

public class TimerController implements Unloadable {

    Model model;

    private TimerView view;

    ListChangeListener<Unit> listener;
    TreeMap<Integer, TimeController> timeControllerMap;

    public TimerController(Model model) {
        this.model = model;
        view = new TimerView();
        timeControllerMap = new TreeMap<>();

        listener = change -> {
            while (change.next()) {
                change.getAddedSubList().forEach(unit -> {
                    TimeController timeController = new TimeController(model, unit);
                    timeControllerMap.put(unit.getId(), timeController);
                    view.addTimer(timeController.getView());
                });
                change.getRemoved().forEach(unit -> {
                    var tc = timeControllerMap.get(unit.getId());
                    tc.unbind();
                    view.removeTimer(tc.getView());
                    timeControllerMap.remove(unit.getId());
                });
            }
        };
        model.getRelequenUnits().addListener(listener);
    }

    @Override
    public void unload() {
        model.getRelequenUnits().removeListener(listener);
    }

    public TimerView getView() {
        return view;
    }

}
