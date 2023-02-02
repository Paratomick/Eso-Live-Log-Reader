module thl.editor_wiencke {
    requires javafx.controls;
    requires javafx.web;
    requires commons.math3;
    requires java.desktop;
    requires com.sun.jna;
    requires com.sun.jna.platform;

    exports de.paratomick;
    exports de.paratomick.view;
    exports de.paratomick.model;
    exports de.paratomick.view.relequen;
    exports de.paratomick.view.unitStats;
    exports de.paratomick.util;
}