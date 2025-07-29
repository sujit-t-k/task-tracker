package org.ajikhoji.tasktracker.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.ajikhoji.tasktracker.model.Task;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.function.Consumer;

public final class TaskManager {

    private final static ObservableList<String> availableTagsList = FXCollections.observableArrayList();
    private final static HashMap<String, Integer> tagsFreq;
    public static TaskManager tm;
    private final DbHandler handler;

    static {
        availableTagsList.add("Show all the tags");
        tagsFreq = new HashMap<>();
    }

    private TaskManager() {
        this.handler = new DbHandler();
        this.handler.load();
    }

    public static TaskManager getInstance() {
        if(tm == null) {
            tm = new TaskManager();
        }
        return tm;
    }

    public void addNewTask(final Task t, final Consumer<Boolean> postFunction) {
        t.setAddedDateTime(LocalDateTime.now());
        final int id = this.handler.addNewTask(t);
        if(id == Task.NOT_ASSIGNED) {
            postFunction.accept(false);
            return;
        }
        t.setId(id);

        for(final String tag : t.getTags()) {
            incrementTagCount(tag);
        }
        postFunction.accept(true);
    }

    public void deleteTask(final Task t, final Consumer<Boolean> postFunction) {
        if(!this.handler.deleteTask(t)) {
            postFunction.accept(false);
            return;
        }
        for(final String tag : t.getTags()) {
            decrementTagCount(tag);
        }
        postFunction.accept(true);
    }

    public void modifyTask(final Task tOld, final Task tNew, final Consumer<Boolean> postFunction) {
        if(!this.handler.updateTask(tOld, tNew)) {
            postFunction.accept(false);
            return;
        }
        for(final String tag : tOld.getTags()) {
            decrementTagCount(tag);
        }
        for(final String tag : tNew.getTags()) {
            incrementTagCount(tag);
        }
        postFunction.accept(true);
    }

    public static void incrementTagCount(final String tag) {
        final int updatedCount = tagsFreq.getOrDefault(tag, 0) + 1;
        if(updatedCount == 1) {
            availableTagsList.add(tag);
        }
        tagsFreq.put(tag, updatedCount);
    }

    private static void decrementTagCount(final String tag) {
        final int updatedCount = tagsFreq.getOrDefault(tag, 1) - 1;
        if(updatedCount < 1) {
            availableTagsList.remove(tag);
            tagsFreq.remove(tag);
            return;
        }
        tagsFreq.put(tag, updatedCount);
    }

    public static ObservableList<String> getAvailableTagsList() {
        return availableTagsList;
    }

    public void settle() {
        this.handler.close();
    }

}
