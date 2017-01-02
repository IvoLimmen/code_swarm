package org.github.codeswarm.gui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;
import org.github.codeswarm.ColorTest;

public class ColorAssignerProperties {

   private final SimpleStringProperty expression;
   private final SimpleStringProperty label;
   private final SimpleObjectProperty<Color> color;

   public ColorAssignerProperties(ColorTest colorTest) {
      this.expression = new SimpleStringProperty(colorTest.getExpr());
      this.label = new SimpleStringProperty(colorTest.getLabel());
      this.color = new SimpleObjectProperty<>(convertColor(colorTest.getC1()));
   }

   private double toSubColor(int value) {
      double val = 0;

      if (value > 0) {
         val = value / 255d;
      }

      return val;
   }

   public SimpleStringProperty getExpression() {
      return expression;
   }

   public SimpleStringProperty getLabel() {
      return label;
   }

   public SimpleObjectProperty<Color> getColor() {
      return color;
   }

   private Color convertColor(java.awt.Color color) {
      return new Color(toSubColor(color.getRed()), toSubColor(color.getGreen()), toSubColor(color.getBlue()), toSubColor(color.getAlpha()));
   }

}
