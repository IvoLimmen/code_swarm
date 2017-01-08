package org.github.codeswarm.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EditDialog {

   private ColorAssignerProperties model;
   private final Stage owner;
   private Stage dialog;

   public EditDialog(Stage owner) {
      this.owner = owner;
   }

   public Stage createDialog(ColorAssignerProperties model) {
      // initialize the confirmation dialog
      this.dialog = new Stage(StageStyle.DECORATED);
      this.model = model;

      dialog.initModality(Modality.WINDOW_MODAL);
      dialog.initOwner(owner);
      if ("".equals(model.getLabel().getValue())) {
         dialog.setTitle("Create a new color rule");
      } else {
         dialog.setTitle("Edit " + model.getLabel().getValue());
      }
      dialog.setScene(mainScene());

      return dialog;
   }

   private Scene mainScene() {
      BorderPane borderPane = new BorderPane();

      borderPane.setBottom(createButtons());
      borderPane.setCenter(createMainView());

      StackPane root = new StackPane();
      root.getChildren().add(borderPane);

      return new Scene(root, 600, 400);
   }

   private GridPane createButtons() {
      GridPane gridPane = new GridPane();
      gridPane.setPadding(new Insets(5));
      gridPane.setHgap(10);
      gridPane.setVgap(10);

      Button saveButton = new Button("Save");
      saveButton.setOnAction(a -> {
         this.model.getLabel().set(this.labelField.getText());
         this.model.getExpression().set(this.expressionField.getText());
         this.model.getColor().set(this.color.getValue());
         dialog.close();
      });
      gridPane.add(saveButton, 0, 0);

      Button cancelButton = new Button("Cancel");
      cancelButton.setOnAction(a -> {
         dialog.close();
      });
      gridPane.add(cancelButton, 1, 0);

      return gridPane;
   }

   private TextField labelField;

   private TextField expressionField;

   private ColorPicker color;   

   private GridPane createMainView() {
      GridPane gridPane = new GridPane();
      gridPane.setPadding(new Insets(5));
      gridPane.setHgap(10);
      gridPane.setVgap(10);

      ColumnConstraints labelColumn = new ColumnConstraints();
      labelColumn.setMinWidth(100d);
      gridPane.getColumnConstraints().add(labelColumn);

      gridPane.add(new Label("Label:"), 0, 1);
      labelField = new TextField(this.model.getLabel().getValue());
      gridPane.add(labelField, 1, 1);

      gridPane.add(new Label("Expression:"), 0, 2);
      expressionField = new TextField(this.model.getExpression().getValue());
      gridPane.add(expressionField, 1, 2);

      gridPane.add(new Label("Color:"), 0, 3);
      color = new ColorPicker(this.model.getColor().getValue());
      gridPane.add(color, 1, 3);

      return gridPane;
   }

}
