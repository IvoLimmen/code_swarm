package org.github.codeswarm;

import org.github.codeswarm.model.Node;
import org.github.codeswarm.model.Edge;
import org.github.codeswarm.model.PersonNode;
import org.github.codeswarm.model.FileNode;
import javax.vecmath.Vector2f;

/**
 * Copyright 2008 code_swarm project team
 *
 * This file is part of code_swarm.
 *
 * code_swarm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * code_swarm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with code_swarm. If not, see
 * <http://www.gnu.org/licenses/>.
 */
/**
 * Abstract interface of any code_swarm physical engine.
 *
 * @note Need to be derived to define force calculation algorithms between Nodes
 * @note Need to use the constructor to apply some configuration options
 *
 * @note For portability, no Processing library should be use there, only standard Java packages
 */
public abstract class PhysicsEngine {

   /**
    * Initialize the Physical Engine
    *
    * @param p Properties file
    */
   public abstract void setup();

   /**
    * Method that allows Physics Engine to initialize the Frame
    *
    */
   public void initializeFrame() {
   }

   /**
    * Method that allows Physics Engine to finalize the Frame
    *
    */
   public void finalizeFrame() {
   }

   public void onRelax(PersonNode p) {
   }

   public void onRelax(FileNode f) {
   }

   public void onRelax(Edge e) {
   }

   public void onUpdate(PersonNode p) {
      updateNode(p);
   }

   public void onUpdate(FileNode f) {
      updateNode(f);
   }

   public void onUpdate(Edge edge) {
      edge.decay();
   }

   private void updateNode(Node node) {
      Vector2f tforce = new Vector2f(node.getPosition().x - node.getLastPosition().x, node.getPosition().y - node.getLastPosition().y);
      node.setLastPosition(new Vector2f(node.getPosition()));
      tforce.scale(node.getFriction()); // Friction!
      node.getPosition().add(tforce);

      node.decay();
   }

   /**
    *
    * @return Vector2f vector holding the starting location for a File Node
    */
   public void startLocation(Node node) {
      node.setPosition(randomLocation());
   }

   public void startVelocity(Node node) {
      Vector2f vec = new Vector2f(((float) Math.random() * 2 - 1), ((float) Math.random() * 2 - 1));
      vec.scale((1 / vec.length()) * (float) Math.random() * 15 / node.getMass());
      
      node.setLastPosition(new Vector2f(node.getPosition()));
      node.getLastPosition().add(vec);
   }

   public Vector2f randomLocation() {
      int width = Config.getInstance().getIntProperty(Config.WIDTH_KEY);
      int height = Config.getInstance().getIntProperty(Config.HEIGHT_KEY);
      
      Vector2f vec = new Vector2f(width * (float) Math.random(), height * (float) Math.random());
      return vec;
   }

   protected float constrain(float value, float min, float max) {
      if (value < min) {
         return min;
      } else if (value > max) {
         return max;
      }

      return value;
   }
}
