package org.github.gitswarm.model;

import org.github.gitswarm.Config;

/**
 * A node describing a file
 */
public class FileNode extends Node implements Comparable<FileNode> {

   private final int nodeHue;
   private final int minBold;
   protected int touches;
   private PersonNode lastEditor = null;
   private int maxTouches;

   /**
    * 1) constructor.
    */
   public FileNode(String name, int maxTouches) {
      super(Config.getInstance().getFileLife().getValue(), Config.getInstance().getFileDecrement().getValue());
      
      this.name = name;
      this.touches = 1;      
      this.minBold = (int) (initialLife * ((100.0f - Config.getInstance().getFileHighlight().getValue()) / 100));
      this.nodeHue = Config.getInstance().getColorAssigner().getColor(name);
      this.mass = Config.getInstance().getFileMass().getValue();
      this.friction = 0.9f;
      this.maxTouches = maxTouches;
   }

   public int getMinBold() {
      return minBold;
   }

   public int getNodeHue() {
      return nodeHue;
   }

   public int getTouches() {
      return touches;
   }

   /**
    * @return file node as a string
    */
   @Override
   public String toString() {
      return "FileNode{" + "name='" + getName() + '\'' + ", nodeHue=" + nodeHue + ", touches=" + touches + '}';
   }

   /**
    * 6) reseting life as if new.
    */
   @Override
   public void freshen() {
      super.freshen();
      if (++touches > maxTouches) {
         maxTouches = touches;
      }
   }

   @Override
   public boolean isAlive() {
      boolean alive = getLife() > 0;
      if (!alive && lastEditor != null) {
         int idx = lastEditor.editing.indexOf(this);
         if (idx != -1) {
            lastEditor.editing.set(idx, null);
         }
         lastEditor = null;
      }

      return alive;
   }

   public void setEditor(PersonNode editor) {
      if (editor == lastEditor) {
         return;
      }
      if (lastEditor != null) {
         lastEditor.editing.set(lastEditor.editing.indexOf(this), null);
      }
      lastEditor = editor;
      int firstNullIndex = editor.editing.indexOf(null);
      if (firstNullIndex == -1) {
         editor.editing.add(this);
      } else {
         editor.editing.set(firstNullIndex, this);
      }
   }

   public boolean qualifies() {
      return this.touches >= (maxTouches * 0.5f);
   }

   @Override
   public int compareTo(FileNode fn) {
      int retval = 0;
      if (this.touches < fn.touches) {
         retval = -1;
      } else if (this.touches > fn.touches) {
         retval = 1;
      }
      return retval;
   }
}
