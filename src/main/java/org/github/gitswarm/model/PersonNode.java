package org.github.gitswarm.model;

import java.util.ArrayList;
import java.util.List;
import org.github.gitswarm.Config;
import processing.core.PImage;

/**
 * A node describing a person
 */
public class PersonNode extends Node {

   private int flavor;
   private int colorCount = 1;
   private final int minBold;
   protected int touches;
   public List<FileNode> editing = new ArrayList<>();
   private PImage icon = null;

   /**
    * 1) constructor.
    */
   public PersonNode(String name) {
      super(Config.getInstance().getPersonLife().getValue(), Config.getInstance().getPersonDescrement().getValue());
      
      this.name = name;
      this.flavor = 0;
      this.minBold = (int) (initialLife * (1 - (Config.getInstance().getPersonHighlight().getValue() / 100.0)));
      this.mass = Config.getInstance().getPersonMass().getValue();
      this.touches = 1;
      this.friction = 0.99f;
   }

   public PImage getIcon() {
      return icon;
   }

   public int getFlavor() {
      return flavor;
   }

   public void setColorCount(int colorCount) {
      this.colorCount = colorCount;
   }

   public int getMinBold() {
      return minBold;
   }

   public void setIcon(PImage icon) {
      this.icon = icon;
   }

   @Override
   public void freshen() {
      super.freshen();
      touches++;
   }

   public void setFlavor(int flavor) {
      this.flavor = flavor;
   }

   public int getColorCount() {
      return colorCount;
   }

}
