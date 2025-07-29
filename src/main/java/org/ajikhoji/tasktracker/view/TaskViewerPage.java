package org.ajikhoji.tasktracker.view;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;

import static org.ajikhoji.tasktracker.Values.*;
import static org.ajikhoji.tasktracker.App.stage;
import static org.ajikhoji.tasktracker.App.hs;

import javafx.stage.Modality;
import javafx.stage.Stage;
import org.ajikhoji.tasktracker.Launcher;
import org.ajikhoji.tasktracker.controller.TaskManager;
import org.ajikhoji.tasktracker.controls.TaskList;
import org.ajikhoji.tasktracker.model.Task;
import org.ajikhoji.tasktracker.model.ViewPriority;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.BiConsumer;

public class TaskViewerPage {

    private Pane paneBase;
    public static TaskViewerPage tvp;
    public final TaskList tlAccomplished;
    private final TaskList tlOngoing;
    private final TaskList tlNotStarted;
    private final TaskList tlMissed;
    private final Clipboard cb;

    /*
     *   WARNING : Do not hardcode sensitive information here. Find alternatives to ensure security.
     */
    private final static String
        APP_NAME = "Name of the application",
        APP_VERSION = "App version",
        DEV_NAME = "Name of the Developer",
        DEV_CONTACT_MAIL = "Email address of developer/owner",
        OPEN_SOURCE_PROJ_LINK = "Github project link";

    private TaskViewerPage() {
        this.cb = Clipboard.getSystemClipboard();

        this.tlAccomplished = new TaskList();
        this.tlOngoing = new TaskList();
        this.tlNotStarted = new TaskList();
        this.tlMissed = new TaskList();
        this.init();
    }

    public static void show() {
        getInstance().set();
    }

    public static TaskViewerPage getInstance() {
        if(tvp == null) {
            tvp = new TaskViewerPage();
        }
        return tvp;
    }

    public void add(final Task t) {
        switch (t.getStatus()) {
            case ACCOMPLISHED -> this.tlAccomplished.add(t);
            case ONGOING -> this.tlOngoing.add(t);
            case NOT_STARTED -> this.tlNotStarted.add(t);
        }
        if(t.getDeadlineDateTime().isBefore(LocalDateTime.now())) {
            this.tlMissed.add(t);
        }
    }

    public void remove(final Task t) {
        switch (t.getStatus()) {
            case ACCOMPLISHED -> this.tlAccomplished.remove(t);
            case ONGOING -> this.tlOngoing.remove(t);
            case NOT_STARTED -> this.tlNotStarted.remove(t);
        }
        this.tlMissed.remove(t);
    }

    private void init() {
        final Pane paneBase = new Pane();
        paneBase.getStyleClass().add("pane-primary");

        final BorderPane bpBase = new BorderPane();
        bpBase.prefWidthProperty().bind(paneBase.widthProperty());
        bpBase.prefHeightProperty().bind(paneBase.heightProperty());
        paneBase.getChildren().add(bpBase);

        final Tab t1 = new Tab("Ongoing", this.tlOngoing);
        final Tab t2 = new Tab("Not Started", this.tlNotStarted);
        final Tab t3 = new Tab("Accomplished", this.tlAccomplished);
        final Tab t4 = new Tab("Missed", this.tlMissed);
        final TabPane tp = new TabPane(t1, t2, t3, t4);
        tp.getStyleClass().add("tab-pane-primary");
        tp.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tp.setLayoutX(140.0D);
        tp.setLayoutY(140.0D);
        bpBase.setCenter(tp);

        final BorderPane bpBottom = new BorderPane();
        bpBottom.setStyle("-fx-background-color: #191919; -fx-border-color: #565656; -fx-border-width: 0.75px 0 0 0; -fx-padding: 2px 10px 2px 10px;");
        bpBottom.setPrefHeight(50.0D);
        bpBase.setBottom(bpBottom);

        //bottom control pane component
        final Label lblSortOrder = new Label("Sort order");
        final ChoiceBox<String> cbxSortOrder = new ChoiceBox<>();
        cbxSortOrder.setItems(sortOrderChoices);
        cbxSortOrder.getSelectionModel().selectedItemProperty().addListener((ol, ov, nv) -> {
            this.tlOngoing.setViewPriority(ViewPriority.getEnum(nv));
            this.tlAccomplished.setViewPriority(ViewPriority.getEnum(nv));
            this.tlNotStarted.setViewPriority(ViewPriority.getEnum(nv));
            this.tlMissed.setViewPriority(ViewPriority.getEnum(nv));
        });
        cbxSortOrder.getSelectionModel().select(3);
        final HBox hbxSortOrder = new HBox(5.0D, lblSortOrder, cbxSortOrder);
        hbxSortOrder.setAlignment(Pos.CENTER);

        final Label lblViewTag = new Label("View tag");
        final ChoiceBox<String> cbxViewTag = new ChoiceBox<>();
        cbxViewTag.setItems(TaskManager.getAvailableTagsList());
        cbxViewTag.getSelectionModel().selectedItemProperty().addListener((ol, ov, nv) -> {
            this.tlOngoing.filterByTag(nv);
            this.tlAccomplished.filterByTag(nv);
            this.tlMissed.filterByTag(nv);
            this.tlNotStarted.filterByTag(nv);
        });
        cbxViewTag.getSelectionModel().selectFirst();
        final HBox hbxViewTag = new HBox(5.0D, lblViewTag, cbxViewTag);
        hbxViewTag.setAlignment(Pos.CENTER);

        final HBox hbxViewControls = new HBox(12.0D, hbxSortOrder, hbxViewTag);
        hbxViewControls.setAlignment(Pos.CENTER_LEFT);
        bpBottom.setLeft(hbxViewControls);

        final Button btnAbout = new Button("About");
        btnAbout.setOnAction(e -> {
            final GridPane gpInfo = new GridPane();
            gpInfo.setStyle("-fx-background-color: #262626; -fx-padding: 12.0D");
            gpInfo.setHgap(6.0D);
            gpInfo.setVgap(6.0D);

            final ColumnConstraints ccFieldName = new ColumnConstraints();
            ccFieldName.setHalignment(HPos.RIGHT);
            ccFieldName.setHgrow(Priority.SOMETIMES);
            gpInfo.getColumnConstraints().add(ccFieldName);

            final ColumnConstraints ccValue = new ColumnConstraints();
            ccValue.setHalignment(HPos.LEFT);
            ccValue.setHgrow(Priority.ALWAYS);
            gpInfo.getColumnConstraints().add(ccValue);

            final BiConsumer<String, Labeled> rowInfo = (fieldName, fieldNode) -> {
                final Label lblField = new Label(fieldName);
                lblField.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                fieldNode.setMaxWidth(Double.MAX_VALUE);
                GridPane.setHgrow(fieldNode, Priority.ALWAYS);
                fieldNode.setOnMousePressed(eh -> {
                    final ClipboardContent content = new ClipboardContent();
                    content.putString(fieldNode.getText());
                    this.cb.setContent(content);
                });
                fieldNode.getStyleClass().add("about-value-holder");

                gpInfo.add(lblField, 0, gpInfo.getRowCount());
                gpInfo.add(fieldNode, 1, gpInfo.getRowCount() - 1);
            };

            final Hyperlink mailLink = new Hyperlink(DEV_CONTACT_MAIL);
            mailLink.setOnAction(eh -> hs.showDocument(String.format("mailto:%s", DEV_CONTACT_MAIL)));

            final Hyperlink projLink = new Hyperlink(OPEN_SOURCE_PROJ_LINK);
            projLink.setOnAction(eh -> hs.showDocument(OPEN_SOURCE_PROJ_LINK));

            rowInfo.accept("App Name", new Label(APP_NAME));
            rowInfo.accept("App Version", new Label(APP_VERSION));
            rowInfo.accept("Developed By", new Label(DEV_NAME));
            rowInfo.accept("Mail", mailLink);
            rowInfo.accept("Project Source Code", projLink);

            final Label lblInfo = new Label("Press on any field value to copy");
            lblInfo.setAlignment(Pos.CENTER);
            lblInfo.setMaxWidth(Double.MAX_VALUE);
            lblInfo.setStyle("-fx-padding: 12px 0px 0px 0px;");
            GridPane.setHgrow(lblInfo, Priority.ALWAYS);
            gpInfo.add(lblInfo, 0, gpInfo.getRowCount(), 2, 1);

            final Stage stAbout = new Stage();
            final Button btnClose = new Button("Close");
            btnClose.setOnAction(eh -> stAbout.close());
            final HBox hbxClose = new HBox(btnClose);
            hbxClose.setStyle("-fx-padding: 12px 0px 0px 0px;");
            hbxClose.setMaxWidth(Double.MAX_VALUE);
            hbxClose.setAlignment(Pos.CENTER);
            gpInfo.add(hbxClose, 0, gpInfo.getRowCount(), 2, 1);

            final Scene sc = new Scene(gpInfo);
            sc.getStylesheets().add(Objects.requireNonNull(Launcher.class.getResource("style/dark_theme.css")).toExternalForm());
            stAbout.setTitle("About");
            stAbout.setResizable(false);
            stAbout.setScene(sc);
            stAbout.initModality(Modality.APPLICATION_MODAL);
            stAbout.initOwner(stage);
            stAbout.show();
        });

        final Button btnAddTask = new Button("Add new task");
        btnAddTask.setOnAction(e -> AddTaskPage.show());

        final HBox hbxAddTask = new HBox(12.0D, btnAbout, btnAddTask);
        hbxAddTask.setAlignment(Pos.CENTER);
        bpBottom.setRight(hbxAddTask);

        this.paneBase = paneBase;
    }

    private void set() {
        stage.getScene().setRoot(this.paneBase);
    }

}
