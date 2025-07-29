package org.ajikhoji.tasktracker.view;

import javafx.scene.control.Alert;
import org.ajikhoji.tasktracker.controller.TaskManager;
import org.ajikhoji.tasktracker.model.Task;

public class EditTaskPage {

    private final TaskEditor te;

    private EditTaskPage(final Task t) {
        final Task tOld = new Task();
        tOld.setId(t.getId());
        tOld.setTitle(t.getTitle());
        tOld.setStatus(t.getStatus());
        tOld.setDescription(t.getDescription());
        tOld.setRemarks(t.getRemarks());
        tOld.setDeadlineDateTime(t.getDeadlineDateTime());
        tOld.setAddedDateTime(t.getAddedDateTime());
        tOld.setModifiedDateTime(t.getModifiedDateTime());
        for(final String tag : t.getTags()) {
            tOld.addTag(tag);
        }

        this.te = new TaskEditor(
            t,
            "Edit Task",
            (redirectTo) -> TaskViewerPage.show(),
            "Save",
            (editedTask) -> TaskManager.getInstance().modifyTask(tOld, editedTask, (isSuccess) -> {
                if(editedTask.getTitle().equals(tOld.getTitle())
                    && editedTask.getDescription().equals(tOld.getDescription())
                    && editedTask.getRemarks().equals(tOld.getRemarks())
                    && editedTask.getStatus().equals(tOld.getStatus())
                    && editedTask.getDeadlineDateTime().equals(tOld.getDeadlineDateTime())
                    && editedTask.getTags().containsAll(tOld.getTags())
                    && tOld.getTags().containsAll(editedTask.getTags())) {
                        final Alert a = new Alert(Alert.AlertType.INFORMATION);
                        a.setTitle("Operation aborted");
                        a.setContentText("No modifications has been made");
                        a.show();
                    return;
                }
                if (isSuccess) {
                    TaskViewerPage.getInstance().remove(tOld);
                    TaskViewerPage.getInstance().add(editedTask);
                    TaskViewerPage.show();
                } else {
                    final Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Failed: Commit modification");
                    a.setContentText("Try again after sometime");
                    a.show();
                }
            })
        );
    }

    public static void show(final Task taskToEdit) {
        final EditTaskPage etp = new EditTaskPage(taskToEdit);
        etp.te.set();
    }

}
