package de.paratomick.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FileSelectorView extends VBox{

    TextField labelPath;
    Button btnChooseFile;
    Button btnLoadFile;

    public FileSelectorView() {

        labelPath = new TextField("File Path");

        btnChooseFile = new Button("File Chooser");
        btnLoadFile = new Button("Load");
        getChildren().addAll(labelPath, new HBox(btnChooseFile, btnLoadFile));
    }

    public TextField getLabelPath() {
        return labelPath;
    }

    public Button getBtnChooseFile() {
        return btnChooseFile;
    }

    public Button getBtnLoadFile() {
        return btnLoadFile;
    }
}
