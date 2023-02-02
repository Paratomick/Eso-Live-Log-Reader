package de.paratomick;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import de.paratomick.controller.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class App extends Application {

    private final static String windowTitle = "Live Log Visualizer";
    private static Stage stage;

    private Controller controller;

    @Override
    public void start(Stage stage) throws Exception {
        App.stage = stage;

        controller = new Controller(this);

        // Create Scene
        Scene scene = new Scene(controller.getView(), 800, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(null);
        stage.setX(100);
        stage.setY(0);
        stage.setTitle(windowTitle);
        stage.show();

        // Link Listeners
        stage.focusedProperty().addListener((observableValue, o, n) -> {
            if(!o && n) {
                unsetTranspatent();
                controller.getView().setBackground(true);
            } else if(o && !n) {
                setTranspatent();
                controller.getView().setBackground(false);
            }
        });

        double[] mousePos = new double[2];
        scene.setOnMousePressed(mouseEvent -> {
            if(mouseEvent.isPrimaryButtonDown()) {
                mousePos[0] = mouseEvent.getScreenX();
                mousePos[1] = mouseEvent.getScreenY();
            }
        });
        scene.setOnMouseDragged(mouseEvent -> {
            if(mouseEvent.isPrimaryButtonDown()) {
                stage.setX(stage.getX() + (mouseEvent.getScreenX() - mousePos[0]));
                stage.setY(stage.getY() + (mouseEvent.getScreenY() - mousePos[1]));
                mousePos[0] = mouseEvent.getScreenX();
                mousePos[1] = mouseEvent.getScreenY();
            }
        });
    }

    private void setTranspatent() {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, windowTitle);
        int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
        //wl = wl | WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
        wl = wl | WinUser.WS_EX_TRANSPARENT;
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
    }

    private void unsetTranspatent() {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, windowTitle);
        int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
        wl = wl & ~WinUser.WS_EX_TRANSPARENT;
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
    }

    public static Stage getPrimaryStage() {
        return App.stage;
    }

    public static void main(String[] args) {
        App.launch(args);
    }
}
