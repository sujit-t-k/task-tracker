package org.ajikhoji.tasktracker.model;

import org.ajikhoji.tasktracker.Values;

import java.time.LocalDateTime;
import static java.util.Collections.unmodifiableSet;

import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.HashSet;

public class Task {

    public final static int NOT_ASSIGNED = -17;

    private int id = NOT_ASSIGNED;
    private String title;
    private String description;
    private String remarks;
    private final Set<String> tags;

    private LocalDateTime ldtDeadline;
    private LocalDateTime ldtAdded;
    private LocalDateTime ldtLastModified;
    private Status status;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public Task() {
        this.tags = new HashSet<>();
        this.status = Status.NOT_STARTED;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getDeadlineDateTime() {
        return ldtDeadline;
    }

    public void setDeadlineDateTime(LocalDateTime ldtDeadline) {
        this.ldtDeadline = ldtDeadline;
    }

    public LocalDateTime getModifiedDateTime() {
        return ldtLastModified;
    }

    public void setModifiedDateTime(LocalDateTime ldtDeadline) {
        this.ldtLastModified = ldtDeadline;
    }

    public LocalDateTime getAddedDateTime() {
        return ldtAdded;
    }

    public void setAddedDateTime(LocalDateTime ldtDeadline) {
        this.ldtAdded = ldtDeadline;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(final Status s) {
        this.status = s;
    }

    public void addTag(final String tag) {
        assert tag != null;
        this.tags.add(tag);
    }

    public void removeTag(final String tag) {
        assert tag != null;
        this.tags.remove(tag);
    }

    public boolean holdsTag(final String tag) {
        assert tag != null;
        return this.tags.contains(tag);
    }

    public Set<String> getTags() {
        return unmodifiableSet(this.tags);
    }

    @Override
    public String toString() {
        return String.format("TASK - Id : %d, Title : %s | Added : %s | Status = %s | Deadline : %s | Tags : %s | " +
                "Description : %s | Remarks : %s", this.id, this.title, getFormattedDate(this.ldtAdded), getStatus(this.status),
                getFormattedDate(this.ldtLastModified), this.tags.toString(), get(this.description), get(this.remarks));
    }

    public static String getFormattedDate(final LocalDateTime ldt) {
        if(ldt == null) {
            return Values.UNDEFINED;
        }
        return FORMATTER.format(ldt);
    }

    public static String get(final String data) {
        if(data == null) {
            return Values.UNDEFINED;
        }
        return data;
    }

    public String getStatus(final Status currStatus) {
        return currStatus.name();
    }

}
