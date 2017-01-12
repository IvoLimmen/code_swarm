package org.github.gitswarm.model;

import java.util.ArrayList;
import java.util.List;
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
   public PersonNode(String n, int initialLife, int decrementLife, int highlightPercent, float mass, int flavor) {
      super(initialLife, decrementLife);
      setName(n);
      this.flavor = flavor;
      this.minBold = (int) (getInitialLife() * (1 - (highlightPercent / 100.0)));
      this.mass = mass;
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
