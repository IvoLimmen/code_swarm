package org.github.gitswarm.gui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;
import org.github.gitswarm.ColorTest;

public class ColorAssignerProperties {

   private final SimpleStringProperty expression;
   private final SimpleStringProperty label;
   private final SimpleObjectProperty<Color> color;

   public ColorAssignerProperties(ColorTest colorTest) {
      this.expression = new SimpleStringProperty(colorTest.getExpr());
      this.label = new SimpleStringProperty(colorTest.getLabel());
      this.color = new SimpleObjectProperty<>(ColorUtil.toFxColor(colorTest.getC1()));
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
}
