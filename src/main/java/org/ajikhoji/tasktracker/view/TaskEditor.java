package org.ajikhoji.tasktracker.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;

import static org.ajikhoji.tasktracker.App.stage;
import org.ajikhoji.tasktracker.controls.TagEditor;
import org.ajikhoji.tasktracker.model.Status;
import org.ajikhoji.tasktracker.model.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.function.*;

public class TaskEditor {

    private Pane base;

    public TaskEditor(final Task tEdit, final String strTitle, final Consumer<Void> onRedirection, final String strOption, final Consumer<Task> onSuccessfulValidation) {
        assert tEdit != null;
        this.init(tEdit, strTitle, onRedirection, strOption, onSuccessfulValidation);
    }

    public TaskEditor(final String strTitle, final Consumer<Void> onRedirection, final String strOption, final Consumer<Task> onSuccessfulValidation) {
        this.init(null, strTitle, onRedirection, strOption, onSuccessfulValidation);
    }

    private void init(final Task tDefault, final String strTitle, final Consumer<Void> redirect, final String strButtonText, final Consumer<Task> perform) {
        final BorderPane bpBase = new BorderPane();
        bpBase.setStyle("-fx-background-color: #262626;");

        //top title display
        final Label lblHeader = new Label(strTitle);
        lblHeader.setAlignment(Pos.CENTER);
        lblHeader.setTextAlignment(TextAlignment.CENTER);
        lblHeader.prefWidthProperty().bind(bpBase.widthProperty());
        lblHeader.setStyle("-fx-background-color: #242424; -fx-padding: 8px 0px 6px 0px; -fx-font-size: 16px; -fx-font-weight: bold;");
        bpBase.setTop(lblHeader);

        //bottom controls pane
        final Button btnAdd = new Button(strButtonText);
        final Button btnClear = new Button("Clear");
        final Button btnBack = new Button("Back");
        final FlowPane foControls = new FlowPane(btnClear, btnBack, btnAdd);
        foControls.setHgap(12.0D);
        foControls.setAlignment(Pos.CENTER);
        foControls.setStyle("-fx-background-color: #202020; -fx-padding: 8px 0px 8px 0px;");
        bpBase.setBottom(foControls);
        btnBack.setOnAction(e -> redirect.accept(null));

        //middle area for showing input controls
        final Label lblTitle = getLabel("Title");
        final TextField tfTitle = new TextField();
        tfTitle.textProperty().addListener((ol, ov, nv) -> {
            if(nv == null) {
                return;
            }
            if(nv.length() > 50) {
                tfTitle.setText(ov);
            }
        });
        tfTitle.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(tfTitle, Priority.ALWAYS);

        final HBox hbxTitle = new HBox(8.0D, lblTitle, tfTitle);
        hbxTitle.setAlignment(Pos.CENTER_LEFT);

        final Label lblStatus = getLabel("Status");
        final ChoiceBox<String> cbxStatus = new ChoiceBox<>(FXCollections.observableArrayList("Not started", "Ongoing", "Accomplished"));
        cbxStatus.getSelectionModel().selectFirst();
        final HBox hbxStatus = new HBox(8.0D, lblStatus, cbxStatus);
        hbxStatus.setAlignment(Pos.CENTER_LEFT);

        final Label lblDeadline = getLabel("Deadline");
        lblDeadline.setMinWidth(Region.USE_PREF_SIZE);

        final DatePicker dp = new DatePicker();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        final UnaryOperator<TextFormatter.Change> dateFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.isBlank() || newText.matches("^[0-9/]+$")) {
                return change;
            }
            return null;
        };
        final TextFormatter<String> tfDate = new TextFormatter<>(dateFilter);
        dp.getEditor().setTextFormatter(tfDate);

        dp.setConverter(new StringConverter<>() {
            @Override
            public String toString(final LocalDate date) {
                return (date != null) ? formatter.format(date) : "";
            }
            @Override
            public LocalDate fromString(final String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) {
                    return null;
                }
                try {
                    return LocalDate.parse(dateString.trim(), formatter);
                } catch (final Exception e) {
                    final Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Validation Error : Invalid Date");
                    a.setContentText(String.format("%s is not a valid date", dateString));
                    a.show();
                    return null;
                }
            }
        });
        dp.setDayCellFactory((final DatePicker picker) -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) return;

                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                }
            }
        });

        final Function<Integer, Spinner<Integer>> SpinnerCreator = (maxValue) -> {
            final Spinner<Integer> sp = new Spinner<>(0, maxValue, maxValue, 1);
            sp.setEditable(true);

            final UnaryOperator<TextFormatter.Change> filter = change -> {
                String newText = change.getControlNewText();
                if(newText.isBlank()) {
                    return change;
                }

                if (newText.matches("\\d*")) {
                    try {
                        final int val = Integer.parseInt(newText);
                        if(val < 0 || val > maxValue) {
                            return null;
                        }
                    } catch (final Exception e) {
                        return null;
                    }
                    return change;
                }

                return null;
            };
            final TextFormatter<String> textFormatter = new TextFormatter<>(filter);
            sp.getEditor().setTextFormatter(textFormatter);

            sp.getEditor().textProperty().addListener((ol, ov, nv) -> {
                if(nv == null || nv.isEmpty()) {
                    sp.getEditor().setText("0");
                    return;
                }
                if(nv.length() == 2 && nv.charAt(0) == '0') {
                    if(nv.charAt(1) == '0') {
                        sp.getEditor().setText("0");
                    } else {
                        sp.getEditor().setText(String.valueOf(nv.charAt(1)));
                    }
                }
            });

            HBox.setHgrow(sp, Priority.NEVER);
            return sp;
        };

        final Spinner<Integer> spHours = SpinnerCreator.apply(23);
        final Label lblDeadlineHours = new Label("hr");
        lblDeadlineHours.setStyle("-fx-padding: 0px 10px 0px 0px;");
        lblDeadlineHours.setMinWidth(Region.USE_PREF_SIZE);

        final Spinner<Integer> spMinutes = SpinnerCreator.apply(59);
        final Label lblDeadlineMinutes = new Label("min");
        lblDeadlineMinutes.setMinWidth(Region.USE_PREF_SIZE);

        final HBox hbxDeadline = new HBox(8.0D, lblDeadline, dp, spHours, lblDeadlineHours, spMinutes, lblDeadlineMinutes);
        hbxDeadline.setAlignment(Pos.CENTER_LEFT);

        final Function<String, VBox> fieldAdder = (final String displayText) -> {
            final Label lblRemarks = getLabel(displayText);
            final Label lblRemarksCharCount = new Label("0 / 400");
            final TextArea taRemarks = new TextArea();
            taRemarks.textProperty().addListener((ol, ov, nv) -> {
                if(nv == null) {
                    lblRemarksCharCount.setText("0 / 400");
                    return;
                }
                if(nv.length() > 400) {
                    taRemarks.setText(ov);
                    return;
                }
                lblRemarksCharCount.setText(nv.length() + " / 400");
            });
            return new VBox(2.5D, lblRemarks, taRemarks, lblRemarksCharCount);
        };

        final VBox vbxDescription = fieldAdder.apply("Description");
        final TextArea taDescription = (TextArea) vbxDescription.getChildren().get(1);
        final VBox vbxRemarks = fieldAdder.apply("Remarks");
        final TextArea taRemarks = (TextArea) vbxRemarks.getChildren().get(1);
        btnClear.setOnAction(e -> {
            tfTitle.clear();
            taDescription.clear();
            taRemarks.clear();
        });

        final TagEditor te = new TagEditor(tDefault != null ? tDefault.getTags() : new HashSet<>());

        final VBox vbx = new VBox(12.0D, hbxTitle, te, hbxStatus, hbxDeadline, vbxDescription, vbxRemarks);
        vbx.setPadding(new Insets(16.0D));
        vbx.setStyle("-fx-background-color: #292929;");
        final ScrollPane spDetail = new ScrollPane(vbx);
        spDetail.setFitToWidth(true);
        bpBase.setCenter(spDetail);

        if(tDefault != null) {
            cbxStatus.getSelectionModel().select(Status.get(tDefault.getStatus()));
            tfTitle.setText(tDefault.getTitle());
            taDescription.setText(tDefault.getDescription());
            taRemarks.setText(taRemarks.getText());
            dp.setValue(tDefault.getDeadlineDateTime().toLocalDate());
            spHours.getValueFactory().setValue(tDefault.getDeadlineDateTime().getHour());
            spMinutes.getValueFactory().setValue(tDefault.getDeadlineDateTime().getMinute());
        } else {
            cbxStatus.getSelectionModel().selectFirst();
            dp.setValue(LocalDate.now());
        }

        btnAdd.setOnAction(e -> {
            if(tfTitle.getText() == null || tfTitle.getText().isBlank()) {
                final Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Validation Error");
                a.setContentText("Title shall not be blank");
                a.show();
                return;
            }
            if(!dp.getEditor().getText().matches("^\\d{1,2}/\\d{1,2}/\\d{4}$")) {
                final Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Validation Error : Invalid Date");
                a.setContentText("Deadline date should be of format: DD/MM/YYYY");
                a.show();
                return;
            }
            LocalDateTime ldtDeadline;
            try {
                ldtDeadline = dp.getValue().atTime(spHours.getValue(), spMinutes.getValue(), 1);
                if (ldtDeadline.isBefore(LocalDateTime.now())) {
                    final Alert a = new Alert(Alert.AlertType.WARNING);
                    a.setTitle("Validation Error");
                    a.setContentText("Deadline should be a future timestamp");
                    a.show();
                    return;
                }
            } catch (final Exception ex) {
                final Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Validation Error : Invalid Date");
                a.setContentText("Enter valid date");
                a.show();
                return;
            }

            final Task tNew = new Task();
            if(tDefault != null) {
                tNew.setId(tDefault.getId());
                tNew.setAddedDateTime(tDefault.getAddedDateTime());
                tNew.setModifiedDateTime(LocalDateTime.now());
            }
            tNew.setStatus(Status.get(cbxStatus.getValue()));
            tNew.setTitle(tfTitle.getText());
            tNew.setDescription(taDescription.getText());
            tNew.setDeadlineDateTime(ldtDeadline);
            tNew.setRemarks(taRemarks.getText());
            for(final String tag : te.getTags()) {
                tNew.addTag(tag);
            }
            perform.accept(tNew);
        });
        this.base = bpBase;
    }

    void set() {
        stage.getScene().setRoot(this.base);
    }

    private Label getLabel(final String strContent) {
        final Label lbl = new Label(strContent);
        return lbl;
    }

}
