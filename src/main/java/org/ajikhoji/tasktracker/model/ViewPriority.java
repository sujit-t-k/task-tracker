package org.ajikhoji.tasktracker.model;

import org.ajikhoji.tasktracker.controls.TaskInfoViewer;

import java.time.LocalDateTime;
import java.util.Comparator;

public enum ViewPriority {

    EARLIEST_ADDED("Earliest added", (a, b) -> {
        final Task x = a.getTaskInfo();
        final Task y = b.getTaskInfo();

        final LocalDateTime ldtX = x.getAddedDateTime();
        final LocalDateTime ldtY = y.getAddedDateTime();

        if(ldtX == null) {
            return 1;
        }

        if(ldtY == null) {
            return -1;
        }

        return ldtX.compareTo(ldtY);
    }),

    LATEST_ADDED("Latest added", (a, b) -> {
        final Task x = a.getTaskInfo();
        final Task y = b.getTaskInfo();

        final LocalDateTime ldtX = x.getAddedDateTime();
        final LocalDateTime ldtY = y.getAddedDateTime();

        if(ldtX == null) {
            return 1;
        }

        if(ldtY == null) {
            return -1;
        }

        return -ldtX.compareTo(ldtY);
    }),

    EARLIEST_MODIFIED("Earliest modified",  (a, b) -> {
        final Task x = a.getTaskInfo();
        final Task y = b.getTaskInfo();

        final LocalDateTime ldtX = x.getModifiedDateTime();
        final LocalDateTime ldtY = y.getModifiedDateTime();

        if(ldtX == null) {
            return 1;
        }

        if(ldtY == null) {
            return -1;
        }

        return ldtX.compareTo(ldtY);
    }),

    LATEST_MODIFIED("Latest modified",  (a, b) -> {
        final Task x = a.getTaskInfo();
        final Task y = b.getTaskInfo();

        final LocalDateTime ldtX = x.getModifiedDateTime();
        final LocalDateTime ldtY = y.getModifiedDateTime();

        if(ldtX == null) {
            return 1;
        }

        if(ldtY == null) {
            return -1;
        }

        return -ldtX.compareTo(ldtY);
    }),

    EARLIEST_DEADLINE("Earliest deadline", (a, b) -> {
        final Task x = a.getTaskInfo();
        final Task y = b.getTaskInfo();

        final LocalDateTime ldtX = x.getDeadlineDateTime();
        final LocalDateTime ldtY = y.getDeadlineDateTime();

        if(ldtX == null) {
            return 1;
        }

        if(ldtY == null) {
            return -1;
        }

        return ldtX.compareTo(ldtY);
    }),

    LATEST_DEADLINE("Latest deadline", (a, b) -> {
        final Task x = a.getTaskInfo();
        final Task y = b.getTaskInfo();

        final LocalDateTime ldtX = x.getDeadlineDateTime();
        final LocalDateTime ldtY = y.getDeadlineDateTime();

        if(ldtX == null) {
            return 1;
        }

        if(ldtY == null) {
            return -1;
        }

        return -ldtX.compareTo(ldtY);
    });

    private final String content;
    private final Comparator<TaskInfoViewer> comp;

    ViewPriority(final String str, final Comparator<TaskInfoViewer> comp) {
        this.content = str;
        this.comp = comp;
    }

    public String get() {
        return this.content;
    }

    public Comparator<TaskInfoViewer> getComparator() {
        return this.comp;
    }

    public static ViewPriority getEnum(final String str) {
        return switch (str) {
            case "Earliest Deadline first" -> EARLIEST_DEADLINE;
            case "Latest Deadline first" -> LATEST_DEADLINE;
            case "Earliest modified first" -> EARLIEST_MODIFIED;
            case "Latest modified first" -> LATEST_MODIFIED;
            case "Earliest added first" -> EARLIEST_ADDED;
            default ->  LATEST_ADDED;
        };
    }

}
