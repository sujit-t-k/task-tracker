package org.ajikhoji.tasktracker.controls;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.ajikhoji.tasktracker.Values;
import org.ajikhoji.tasktracker.controller.TaskManager;
import org.ajikhoji.tasktracker.model.Task;
import org.ajikhoji.tasktracker.view.EditTaskPage;
import org.ajikhoji.tasktracker.view.TaskViewerPage;

public class TaskInfoViewer extends BorderPane {

    private final Task task;

    public TaskInfoViewer(final Task t) {
        this.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        this.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        this.task = t;
        this.initUI();
    }

    private void initUI() {
        VBox leftBox = new VBox(5);
        leftBox.setPadding(new Insets(10));

        Label titleLabel = new Label(this.task.getTitle());
        titleLabel.getStyleClass().add("title-label");

        final String deadline = Task.getFormattedDate(this.task.getDeadlineDateTime());
        final Label deadlineLabel = new Label("Deadline: " + (deadline.equals(Values.UNDEFINED) ? "Not Fixed" : deadline));

        final String added = Task.getFormattedDate(this.task.getAddedDateTime());
        final Label addedLabel = new Label("Added: " + (added.equals(Values.UNDEFINED) ? "Unknown" : added));

        final String modified = Task.getFormattedDate(this.task.getModifiedDateTime());
        final Label modifiedLabel = new Label(modified.equals(Values.UNDEFINED) ? "Never modified" : String.format("Modified: %s", modified));

        VBox topLine = new VBox(titleLabel, new HBox(30.0D, deadlineLabel, addedLabel, modifiedLabel));
        titleLabel.maxWidthProperty().bind(topLine.widthProperty().subtract(10.0D));
        titleLabel.setWrapText(true);

        final FlowPane tagPane = new FlowPane(5, 5);
        for (String tag : this.task.getTags()) {
            Label tagLabel = new Label(tag);
            tagLabel.getStyleClass().add("tag-label");
            tagPane.getChildren().add(tagLabel);
        }

        leftBox.getChildren().addAll(topLine, tagPane);

        HBox rightBox = new HBox(10);
        rightBox.setAlignment(Pos.CENTER);
        rightBox.setPadding(new Insets(10));

        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");

        editButton.setOnAction(e -> EditTaskPage.show(this.task));
        deleteButton.setOnAction(e -> TaskManager.getInstance().deleteTask(this.task, (isSuccess) -> {
            if(isSuccess) {
                TaskViewerPage.getInstance().remove(this.task);
                TaskViewerPage.show();
            } else {
                final Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Failed: Task Deletion");
                a.setContentText("Please try again");
                a.show();
            }
        }));

        rightBox.getChildren().addAll(editButton, deleteButton);

        setCenter(leftBox);
        setRight(rightBox);

        getStyleClass().add("task-info-card");
    }

    public Task getTaskInfo() {
        return this.task;
    }

}
