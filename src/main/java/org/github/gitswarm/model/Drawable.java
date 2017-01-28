package org.github.gitswarm.model;

/**
 * Base class for all drawable objects
 *
 * Lists and implements features common to all drawable objects Edge and Node, FileNode and PersonNode
 */
public abstract class Drawable {

   protected int life;

   protected int initialLife;

   protected int decrementLife;
   
   /**
    * 1) constructor(s)
    *
    * Init jobs common to all objects
    */
   public Drawable(int lifeInit, int lifeDecrement) {
      this.initialLife = lifeInit;
      this.decrementLife = lifeDecrement;
      // init life relative vars
      this.life = initialLife;
   }

   public void setDecrementLife(int decrementLife) {
      this.decrementLife = decrementLife;
   }

   public void setInitialLife(int initialLife) {
      this.initialLife = initialLife;
   }

   public int getDecrementLife() {
      return decrementLife;
   }

   public int getInitialLife() {
      return initialLife;
   }

   public void setLife(int life) {
      this.life = life;
   }

   public int getLife() {
      return life;
   }

   /**
    * 4) shortening life.
    */
   public void decay() {
      if (isAlive()) {
         life += decrementLife;
         if (life < 0) {
            life = 0;
         }
      }
   }

   /**
    * 6) reseting life as if new.
    */
   public void freshen() {
      setLife(getInitialLife());
   }

   /**
    * @return true if life > 0
    */
   public boolean isAlive() {
      return life > 0;
   }
}
