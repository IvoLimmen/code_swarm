package org.github.gitswarm.gui;

import java.util.Optional;
import javafx.scene.control.ChoiceBox;

public class EnhancedChoiceBox extends ChoiceBox<String> {

   public EnhancedChoiceBox() {
      setOnKeyTyped((event) -> {
         String key = event.getCharacter().toLowerCase();
         int currentIndex = getSelectionModel().getSelectedIndex();
         Optional<String> next = getItems().stream().skip(currentIndex + 1).filter(p -> p.toLowerCase().startsWith(key)).findFirst();

         if (next.isPresent()) {
            setValue(next.get());
         } else {
            // letter not found, is probably the last one try from top
            next = getItems().stream().filter(p -> p.toLowerCase().startsWith(key)).findFirst();

            if (next.isPresent()) {
               setValue(next.get());
            } // no else; key not found
         }
      });
   }
}
