package org.github.gitswarm.model;

import org.github.gitswarm.Config;

/**
 * An Edge link two nodes together : a File to a Person.
 */
public class Edge extends Drawable {

   private final FileNode nodeFrom;
   private final PersonNode nodeTo;

   /**
    * 1) constructor.
    *
    * @param from FileNode
    * @param to PersonNode
    */
   public Edge(FileNode from, PersonNode to) {
      super(Config.getInstance().getEdgeLife().getValue(), Config.getInstance().getEdgeDecrement().getValue());
      
      this.nodeFrom = from;
      this.nodeTo = to;
   }

   public FileNode getNodeFrom() {
      return nodeFrom;
   }

   public PersonNode getNodeTo() {
      return nodeTo;
   }
}
