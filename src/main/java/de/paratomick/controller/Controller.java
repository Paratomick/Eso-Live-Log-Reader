package de.paratomick.controller;

import de.paratomick.App;
import de.paratomick.controller.relequen.TimerController;
import de.paratomick.controller.unitStats.UnitsController;
import de.paratomick.model.Model;
import de.paratomick.util.Unloadable;
import de.paratomick.view.View;

import java.util.ArrayList;

public class Controller {

    App app;

    Model model;
    View view;

    UpdateTimeController updateTimeController;
    LeftController leftController;

    ArrayList<Unloadable> centerList;

    public Controller(App app) {
        this.app = app;

        model = new Model();
        view = new View();

        updateTimeController = new UpdateTimeController();
        view.setUpdateTimeView(updateTimeController.getView());

        leftController = new LeftController(this);
        view.setLeftView(leftController.getView());

        centerList = new ArrayList<>();

        showFileOptions();
    }

    public Model getModel() {
        return model;
    }

    public View getView() {
        return view;
    }

    private void clear() {
        for(Unloadable d: centerList) {
            d.unload();
        }
        centerList.clear();
        updateTimeController.unload();
    }

    public void showFileOptions() {
        clear();

        FileSelectorController fsc = new FileSelectorController(this, model);
        centerList.add(fsc);
        view.showFileOptions(fsc.getView());
    }

    public void showIngame() {
        clear();

        updateTimeController.init(model);

        UnitsController unitsController = new UnitsController(model);
        centerList.add(unitsController);

        TimerController timerController = new TimerController(model);
        centerList.add(timerController);

        view.showIngame(unitsController.getView(), timerController.getView());
    }

    public void startReaderThread(Model model) {
        Thread analizerThread = new Thread(model);
        analizerThread.setDaemon(true);
        analizerThread.start();
    }

    public void stopReaderThread(Model model) {
        model.stop();
    }
}
