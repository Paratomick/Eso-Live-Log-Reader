package de.paratomick.controller;

import de.paratomick.App;
import de.paratomick.model.Model;
import de.paratomick.view.FileSelectorView;
import de.paratomick.util.Unloadable;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.nio.file.Path;

public class FileSelectorController implements Unloadable {

    FileSelectorView view;

    public FileSelectorController(Controller controller, Model model) {
        view = new FileSelectorView();

        view.getLabelPath().setText(model.getFilePath());

        DirectoryChooser fc = new DirectoryChooser();
        view.getBtnChooseFile().setOnAction(actionEvent -> {

            File prevFile = new File(view.getLabelPath().getText());
            if(prevFile.exists() && prevFile.isFile()) {
                fc.setInitialDirectory(prevFile.toPath().getParent().toFile());
            } else {
                Path prevPath = prevFile.toPath();
                if(prevPath.toFile().exists()) {
                    fc.setInitialDirectory(prevPath.toFile());
                }
            }

            File file = fc.showDialog(App.getPrimaryStage());

            if(file == null) return;

            view.getLabelPath().setText(file.getPath() + "/Encounter.log");
        });
        view.getBtnLoadFile().setOnAction(actionEvent -> {

            if(!fileExists(view.getLabelPath().getText())) {
                System.err.println("File does not exist! (" + view.getLabelPath().getText() + ")");
                return;
            }
            model.setFilePath(view.getLabelPath().getText());

            model.init();
            controller.showIngame();
            controller.startReaderThread(model);
        });
    }

    public boolean fileExists(String path) {
        File fileToWatch = new File(path);
        return fileToWatch.exists() && fileToWatch.isFile();
    }

    @Override
    public void unload() {

    }

    public FileSelectorView getView() {
        return view;
    }
}
