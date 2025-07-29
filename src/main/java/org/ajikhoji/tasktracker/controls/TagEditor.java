package org.ajikhoji.tasktracker.controls;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.ajikhoji.tasktracker.Launcher;

import static org.ajikhoji.tasktracker.Values.SCREEN_WIDTH;
import static org.ajikhoji.tasktracker.App.stage;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class TagEditor extends VBox {

    private final Set<String> tagSet;
    private Label lblSelected;

    public TagEditor(final Set<String> tags) {
        final FlowPane fpTags = new FlowPane(5, 5);
        final Label lblNoTagsDefault = new Label("No tags available");
        if(tags.isEmpty()) {
            fpTags.getChildren().add(lblNoTagsDefault);
        }
        this.tagSet = new HashSet<>();
        this.tagSet.addAll(tags);

        final Label lblTag = new Label("Tags");
        final Button btnModify = new Button("Edit");
        final Button btnDelete = new Button("Delete");
        final Button btnAdd = new Button("Add");
        final Button btnClear = new Button("Clear");
        btnClear.setDisable(this.tagSet.isEmpty());
        final BorderPane bpHeader = new BorderPane();
        bpHeader.setLeft(lblTag);
        bpHeader.setRight(new HBox(8.0D, btnModify, btnDelete, btnAdd, btnClear));
        bpHeader.setMaxWidth(Double.MAX_VALUE);

        btnModify.setVisible(false);
        btnDelete.setVisible(false);

        final Consumer<String> tagGenerator = strContent -> {
            final Label tagLabel = new Label(strContent);
            tagLabel.getStyleClass().add("tag-label-selectable");
            tagLabel.setOnMouseClicked(e -> {
                if(this.lblSelected != null) {
                    this.lblSelected.getStyleClass().clear();
                    this.lblSelected.getStyleClass().add("tag-label-selectable");
                }
                this.lblSelected = tagLabel;
                tagLabel.getStyleClass().add("selected-tag-label");

                btnModify.setVisible(true);
                btnDelete.setVisible(true);
            });
            fpTags.getChildren().remove(lblNoTagsDefault);
            fpTags.getChildren().add(tagLabel);
        };

        btnDelete.setOnAction(e -> {
            fpTags.getChildren().remove(this.lblSelected);
            this.tagSet.remove(this.lblSelected.getText());
            btnModify.setVisible(false);
            btnDelete.setVisible(false);
            if(this.tagSet.isEmpty()) {
                fpTags.getChildren().add(lblNoTagsDefault);
                btnClear.setDisable(true);
            }
        });

        btnModify.setOnAction(e -> {
            this.showWindow("Edit Tag: " + this.lblSelected.getText(), this.lblSelected.getText(), "Apply", newValue -> {
                this.tagSet.remove(this.lblSelected.getText());
                this.lblSelected.setText(newValue);
                this.tagSet.add(newValue);
            });
        });

        btnAdd.setOnAction(e -> {
            this.showWindow("Add New Tag", "", "Add", newValue -> {
                if(this.tagSet.isEmpty()) {
                    fpTags.getChildren().remove(lblNoTagsDefault);
                    btnClear.setDisable(false);
                }
                this.tagSet.add(newValue);
                tagGenerator.accept(newValue);
            });
        });

        btnClear.setOnAction(e -> {
            this.tagSet.clear();
            fpTags.getChildren().clear();
            fpTags.getChildren().add(lblNoTagsDefault);
            btnClear.setDisable(true);
        });

        for(final String s : tagSet) {
            tagGenerator.accept(s);
        }

        this.getChildren().addAll(bpHeader, fpTags);
    }

    public Set<String> getTags() {
        return this.tagSet;
    }

    private void showWindow(final String strWindowTitle, final String strDefaultValue, final String strButtonText, final Consumer<String> action) {
        final Stage st = new Stage();
        final TextField tf = new TextField(strDefaultValue);
        final Label lblInfo = new Label();
        lblInfo.getStyleClass().add("error-label");
        lblInfo.setWrapText(true);
        lblInfo.setMaxWidth(Double.MAX_VALUE);
        lblInfo.setAlignment(Pos.CENTER);

        final Button btn = new Button(strButtonText);
        btn.setOnAction(e -> {
            action.accept(tf.getText());
            st.close();
        });
        btn.setDisable(strDefaultValue == null || strDefaultValue.isBlank());

        final Consumer<String> disableOperation = textPrompt -> {
            lblInfo.setText(textPrompt);
            btn.setDisable(true);
        };

        tf.textProperty().addListener((ol, ov, nv) -> {
            lblInfo.setText("");
            btn.setDisable(false);
            if(nv == null) {
                disableOperation.accept("Tag name should not be empty.");
                return;
            }
            if(nv.isBlank()) {
                disableOperation.accept("Tag name should not be blank.");
                tf.setText("");
            }
            if(nv.length() > 20) {
                tf.setText(ov);
            }
            if(this.tagSet.contains(nv)) {
                disableOperation.accept("Tag name '" + nv + "' is present already.");
            }
            if(nv.length() == 20) {
                lblInfo.setText("Maximum length of 20 reached");
            }
        });

        final VBox base = new VBox(8.0D, tf, lblInfo, btn);
        base.setPrefWidth(SCREEN_WIDTH * 0.25D);
        base.setAlignment(Pos.CENTER_RIGHT);
        base.setStyle("-fx-background-color: #262626; -fx-padding: 8px;");

        final Scene sc = new Scene(base);
        sc.getStylesheets().add(Objects.requireNonNull(Launcher.class.getResource("style/dark_theme.css")).toExternalForm());
        st.setTitle(strWindowTitle);
        st.setResizable(false);
        st.setScene(sc);
        st.initModality(Modality.APPLICATION_MODAL);
        st.initOwner(stage);
        st.show();
    }

}
