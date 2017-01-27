package org.github.gitswarm.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ColorSetLoader {

   public Color[] load(String setName) {
      List<Color> list = new ArrayList<>();
      Properties properties = new Properties();
      
      try {
         properties.load(this.getClass().getResourceAsStream("/" + setName));
      } catch (IOException ex) {
         return null;
      }
      
      int index = 1;
      String item;
      
      while ((item = properties.getProperty("color." + index)) != null) {
         list.add(parse(item));
         index++;
      }
      
      return list.toArray(new Color[list.size()]);
   }

   private Color parse(String item) {
      String[] parts = item.split("\\,");
      
      return new Color(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
   }
}
