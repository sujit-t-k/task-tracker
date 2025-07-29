package org.ajikhoji.tasktracker.view;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.ajikhoji.tasktracker.controller.TaskManager;

import static org.ajikhoji.tasktracker.App.stage;

public class AddTaskPage {

    public static AddTaskPage atp;
    private final TaskEditor te;

    private AddTaskPage(final Stage stage) {
        this.te = new TaskEditor(
     "Add New Task",
            (redirectTo) -> {
                TaskViewerPage.show();
            },
            "Add",
            (newTask) -> {
                TaskManager.getInstance().addNewTask(newTask, (isSuccess) -> {
                    if (isSuccess) {
                        atp = null;
                        TaskViewerPage.getInstance().add(newTask);
                        TaskViewerPage.show();
                    } else {
                        final Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setTitle("Failed: New Task Addition");
                        a.setContentText("Please try again");
                        a.show();
                    }
                });
            }
        );
    }

    public static void show() {
        if(atp == null) {
            System.gc();
            atp = new AddTaskPage(stage);
        }
        atp.te.set();
    }

}
