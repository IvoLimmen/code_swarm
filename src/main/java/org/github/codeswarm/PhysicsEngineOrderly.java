package org.github.codeswarm;


/**
 * Copyright 2008 code_swarm project team
 *
 * This file is part of code_swarm.
 *
 * code_swarm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * code_swarm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with code_swarm.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.github.codeswarm.model.PersonNode;
import org.github.codeswarm.model.FileNode;
import java.util.Iterator;
import javax.vecmath.Vector2f;

/**
 * @brief A modification of the Legacy engine to prevent people from piling up in the center
 *
 * @see PhysicsEngine Physics Engine Interface
 */
public class PhysicsEngineOrderly extends PhysicsEngine {

   private float MIN_DISTANCE_SQR;

   /**
    * Method for initializing parameters.
    *
    * @param p The CodeSwarmConfig for this code_swarm instance
    */
   @Override
   public void setup() {
      MIN_DISTANCE_SQR = 40000;
   }

   /**
    * Method that allows Physics Engine to modify Speed / Position during the relax phase.
    *
    * @param pNode the node to which the force apply
    *
    * @Note Standard physics is "Position Variation = Speed x Duration" with a convention of "Duration=1" between to
    * frames
    */
   @Override
   public void onRelax(PersonNode pNode) {
      Vector2f delta;

      int width = Config.getInstance().getWidth().getValue();
      int height = Config.getInstance().getHeight().getValue();
      
      // A gentle force to attract pNodes to the center
      // NOTE: this should be done prior to attraction/repulsion forces, otherwise
      // tends to generate a grid-like pattern
      Vector2f midpoint = new Vector2f(width / 2, height / 2);
      delta = new Vector2f();
      delta.sub(midpoint, pNode.getPosition());

      delta.scale(1 / delta.length() * 0.003f);
      pNode.getPosition().add(delta);

      // All person nodes attract each other, but only to a certain point, then they repel with gentle force
      for (PersonNode n : CodeSwarm.getLivingPeople()) {
         if (pNode != n) {
            delta.sub(pNode.getPosition(), n.getPosition());
            if (delta.lengthSquared() < MIN_DISTANCE_SQR) {
               // This calculation gives a 'stiff spring' affect
               //float toMove = ((float)Math.sqrt(MIN_DISTANCE_SQR) - delta.length()) / 10.0f;

               // This calculation gives a much nicer flow
               float toMove = 0.03f;
               delta.scale((1 / delta.length()) * toMove);

               n.getPosition().sub(delta);
               pNode.getPosition().add(delta);
            } else {
               float toMove = -0.003f;
               delta.scale((1 / delta.length()) * toMove);

               n.getPosition().sub(delta);
               pNode.getPosition().add(delta);
            }
         }
      }

      // place the edited files around the person
      Iterator<FileNode> editedFiles = pNode.editing.iterator();
      int index = 0;
      int radius = 45;
      final int node_size = 4;
      final int salt = pNode.hashCode(); // used to randomize orientation of circle of nodes
      int num_nodes_in_ring = (int) ((2 * radius * Math.PI) / node_size);
      while (editedFiles.hasNext()) {
         //if we've placed all the nodes in this ring...
         if (index == num_nodes_in_ring) {
            //start on a new ring
            radius += node_size;
            num_nodes_in_ring = (int) ((2 * radius * Math.PI) / node_size);
            index = 0;
         }
         index++;

         FileNode file = editedFiles.next();
         //leave a hole for the null files
         if (file == null) {
            continue;
         }

         final int place_around_ring = index * num_nodes_in_ring + salt;
         int x = (int) (radius * Math.sin(place_around_ring));
         int y = (int) (radius * Math.cos(place_around_ring));

         delta = new Vector2f();
         delta.sub(file.getPosition(), new Vector2f(pNode.getPosition().x + x, pNode.getPosition().y + y));
         float distance = delta.length();
         delta.scale(1 / delta.length() * -0.01f * distance);
         file.getPosition().add(delta);
      }
   }

   /**
    *
    * @return Vector2f vector holding the starting location for a Person Node
    */
   public Vector2f pStartLocation() {
      // Try to place the new node in a location that won't disrupt the system too much (by being too close to another person)
      for (int i = 0; i < 100; i++) {
         Vector2f testStart = this.randomLocation();
         boolean good = true;
         for (PersonNode n : CodeSwarm.getLivingPeople()) {
            Vector2f delta = new Vector2f();
            delta.sub(n.getPosition(), testStart);
            if (delta.lengthSquared() < MIN_DISTANCE_SQR) {
               good = false;
               break;
            }
         }
         if (good) {
            return testStart;
         }
      }
      return randomLocation();
   }
}
