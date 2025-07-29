module org.ajikhoji.tasktracker {
    requires javafx.controls;
    requires java.desktop;
    requires java.sql;
    requires org.hsqldb;

    opens org.ajikhoji.tasktracker;
    exports org.ajikhoji.tasktracker;
    exports org.ajikhoji.tasktracker.model;
    opens org.ajikhoji.tasktracker.model;
}