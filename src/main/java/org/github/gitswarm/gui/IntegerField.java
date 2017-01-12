package org.github.gitswarm.gui;

import javafx.scene.control.TextField;

public class IntegerField extends TextField {

   public IntegerField() {
   }

   public IntegerField(Integer value) {
      super(value.toString());
   }

   public final Integer getInteger() {
      return Integer.parseInt(getText());
   }

   public final void setInteger(Integer value) {
      setText(value.toString());
   }

   @Override
   public void replaceText(int start, int end, String text) {
      if (validate(text)) {
         super.replaceText(start, end, text);
      }
   }

   @Override
   public void replaceSelection(String text) {
      if (validate(text)) {
         super.replaceSelection(text);
      }
   }

   private boolean validate(String text) {
      return text.matches("[0-9]*");
   }
}
