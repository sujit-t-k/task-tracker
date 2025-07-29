package org.ajikhoji.tasktracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Screen;

public class Values {

    public static double SCREEN_WIDTH, SCREEN_HEIGHT;

    public final static String UNDEFINED = "DATA NOT AVAILABLE";

    public final static ObservableList<String> sortOrderChoices = FXCollections.observableArrayList(
            "Earliest Deadline first",
            "Latest Deadline first",
            "Earliest added first",
            "Latest added first",
            "Earliest modified first",
            "Latest modified first"
    );

    static {
        SCREEN_WIDTH = Screen.getPrimary().getBounds().getWidth();
        SCREEN_HEIGHT = Screen.getPrimary().getBounds().getHeight();
    }

}
