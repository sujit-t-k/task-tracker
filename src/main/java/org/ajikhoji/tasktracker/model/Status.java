package org.ajikhoji.tasktracker.model;

public enum Status {
    ACCOMPLISHED,
    ONGOING,
    NOT_STARTED;

    public static Status get(final String str) {
        return switch (str) {
            case "Not started" -> NOT_STARTED;
            case "Ongoing" -> ONGOING;
            default -> ACCOMPLISHED;
        };
    }

    public static String get(final Status st) {
        return switch (st) {
            case NOT_STARTED -> "Not started";
            case ONGOING -> "Ongoing";
            default -> "Accomplished";
        };
    }

}
