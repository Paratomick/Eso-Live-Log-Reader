package de.paratomick.controller.unitStats;

import de.paratomick.model.Model;
import de.paratomick.model.Unit;
import de.paratomick.util.Unloadable;
import de.paratomick.view.unitStats.UnitsView;
import javafx.collections.ListChangeListener;

import java.util.TreeMap;

public class UnitsController implements Unloadable {

    UnitsView view;

    TreeMap<Integer, UnitController> unitControllerMap;

    Model model;
    ListChangeListener<Unit> listener;

    public UnitsController(Model model) {
        this.model = model;
        view = new UnitsView();

        unitControllerMap = new TreeMap<>();

        // Add and remove units from view on model change.
        listener = change -> {
            while (change.next()) {
                change.getAddedSubList().forEach(unit -> {
                    UnitController unitController = new UnitController(model, unit);
                    unitController.setPrefWidth(view.getBox().widthProperty());
                    unitControllerMap.put(unit.getId(), unitController);
                    view.addUnit(unitController.getView());
                });
                change.getRemoved().forEach(unit -> {
                    var uc = unitControllerMap.get(unit.getId());
                    uc.unbind();
                    view.removeUnit(uc.getView());
                    unitControllerMap.remove(unit.getId());
                });
            }
        };
        model.getVisibleSortedUnits().addListener(listener);
    }

    public UnitsView getView() {
        return view;
    }

    @Override
    public void unload() {
        model.getVisibleSortedUnits().removeListener(listener);
    }
}
