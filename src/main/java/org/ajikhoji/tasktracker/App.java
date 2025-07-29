package org.ajikhoji.tasktracker;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.ajikhoji.tasktracker.controller.TaskManager;
import org.ajikhoji.tasktracker.view.TaskViewerPage;

import java.util.Objects;

public class App extends Application {

    public static Stage stage;
    public static HostServices hs;

    @Override
    public void start(Stage stage) {
        App.stage = stage;
        App.stage.setScene(new Scene(new Pane(), Values.SCREEN_WIDTH * 0.6D, Values.SCREEN_HEIGHT * 0.6D));
        App.stage.getScene().getStylesheets().add(Objects.requireNonNull(Launcher.class.getResource("style/dark_theme.css")).toExternalForm());
        hs = getHostServices();

        TaskManager.getInstance();
        TaskViewerPage.show();
        stage.setTitle("Task Tracker");
        stage.setOnCloseRequest(e -> {
            TaskManager.getInstance().settle();
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}