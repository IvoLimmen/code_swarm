package org.github.gitswarm.model;

import javax.vecmath.Vector2f;

/**
 * A node is an abstraction for a File or a Person.
 */
public abstract class Node extends Drawable {

   protected float currentWidth;
   
   protected float friction;
   
   protected Vector2f lastPosition;
   
   protected int mass;
   
   protected String name;
   
   protected Vector2f position;

   /**
    * 1) constructor.
    */
   public Node(int lifeInit, int lifeDecrement) {
      super(lifeInit, lifeDecrement);
      position = new Vector2f();
      lastPosition = new Vector2f();
      friction = 1.0f;
   }

   public float getFriction() {
      return friction;
   }

   public Vector2f getLastPosition() {
      return lastPosition;
   }

   public int getMass() {
      return mass;
   }

   public String getName() {
      return name;
   }

   public Vector2f getPosition() {
      return position;
   }

   public void setLastPosition(Vector2f lastPosition) {
      this.lastPosition = lastPosition;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setPosition(Vector2f position) {
      this.position = position;
   }
}
