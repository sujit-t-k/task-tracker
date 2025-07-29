package org.ajikhoji.tasktracker.controls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import org.ajikhoji.tasktracker.model.Task;
import org.ajikhoji.tasktracker.model.ViewPriority;

import static org.ajikhoji.tasktracker.model.ViewPriority.*;

public class TaskList extends ListView<TaskInfoViewer> {

    private final ObservableList<TaskInfoViewer> items;
    private ViewPriority vpCurr;

    public TaskList() {
        this.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(final TaskInfoViewer task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setGraphic(null);
                } else {
                    setGraphic(task);
                }
            }
        });

        this.items = FXCollections.observableArrayList();
        this.setItems(this.items);
        this.vpCurr = LATEST_ADDED;
        this.apply();
    }

    public void add(final Task t) {
        final TaskInfoViewer tiv = new TaskInfoViewer(t);
        this.items.add(tiv);
        tiv.layout();
        this.apply();
        this.refresh();
    }

    public void remove(final Task t) {
        TaskInfoViewer toRemove = null;
        for(final TaskInfoViewer tiv : this.items) {
            if(tiv.getTaskInfo().getId() == t.getId()) {
                toRemove = tiv;
                break;
            }
        }
        if(toRemove != null) {
            this.items.remove(toRemove);
            this.refresh();
        }
    }

    public void setViewPriority(final ViewPriority vp) {
        if(!this.vpCurr.equals(vp)) {
            this.vpCurr = vp;
            this.apply();
            this.refresh();
        }
    }

    public void filterByTag(final String tag) {
        if(tag.equals("Show all the tags")) {
            this.setItems(this.items);
            return;
        }
        this.setItems(FXCollections.observableArrayList(this.items.filtered(t -> t.getTaskInfo().getTags().contains(tag))));
    }

    public void apply() {
        this.items.sort(this.vpCurr.getComparator());
    }

}
