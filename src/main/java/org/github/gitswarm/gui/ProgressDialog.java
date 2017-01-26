package org.github.gitswarm.gui;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProgressDialog {
   
   private final Stage dialogStage;
   private final ProgressBar progressBar = new ProgressBar();
   
   public ProgressDialog(String title) {
      dialogStage = new Stage();
      dialogStage.initStyle(StageStyle.UTILITY);
      dialogStage.setResizable(false);
      dialogStage.initModality(Modality.APPLICATION_MODAL);
      dialogStage.setTitle(title);
      
      progressBar.setProgress(1);  
      progressBar.setPrefSize(200d, 15d);
      
      final HBox hb = new HBox();
      hb.setSpacing(5);
      hb.setAlignment(Pos.CENTER);
      hb.getChildren().addAll(progressBar);      
      
      Scene scene = new Scene(hb, 200, 15);
      dialogStage.setScene(scene);
   }
   
   public void activateProgressBar(final Task<?> task) {
      progressBar.progressProperty().bind(task.progressProperty());
   }
   
   public Stage getDialogStage() {
      return dialogStage;
   }
}
