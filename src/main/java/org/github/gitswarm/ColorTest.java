package org.github.gitswarm;

/*
   Copyright 2008 Michael Ogawa

   This file is part of code_swarm.

   code_swarm is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   code_swarm is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with code_swarm.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.awt.Color;
import java.util.regex.*;

public class ColorTest {

   private Pattern pattern;
   private String expr;
   private String label;
   private Color c1;

   public ColorTest() {
      this("Label", ".*ext*", Color.WHITE);
   }

   public ColorTest(String label, String expr, Color c1) {
      this.expr = expr;
      this.pattern = Pattern.compile(expr);
      this.label = label;
      this.c1 = c1;
   }

   public Pattern getPattern() {
      return pattern;
   }

   public void setPattern(Pattern pattern) {
      this.pattern = pattern;
   }

   public String getExpr() {
      return expr;
   }

   public void setExpr(String expr) {
      this.expr = expr;
   }

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public Color getC1() {
      return c1;
   }

   public void setC1(Color c1) {
      this.c1 = c1;
   }

   public boolean passes(String s) {
      Matcher m = pattern.matcher(s);
      return m.matches();
   }

   public int assign() {
      return c1.getRGB();
   }

   public void loadProperty(String value) {
      String[] tokens;
      // should have the format "label", "regex", r1,g1,b1, r2,g2,b2
      // get the stuff in quotes first
      int firstQ = value.indexOf('\"');
      int lastQ = value.lastIndexOf('\"');
      String firstpart = value.substring(firstQ + 1, lastQ);
      tokens = firstpart.split("\"");
      label = tokens[0];
      if (tokens.length == 3) {
         pattern = Pattern.compile(tokens[2]);
         expr = tokens[2];
      } else {
         pattern = Pattern.compile(tokens[0]);
         expr = tokens[0];
      }
      // then the comma delimited colors
      String rest = value.substring(lastQ + 1);
      tokens = rest.split(",");
      int[] components = new int[6];

      int j = 0;
      for (String token : tokens) {
         String tok = token.trim();
         if (tok.length() > 0) {
            components[j++] = Integer.parseInt(tok);
         }
      }
      c1 = new Color(components[0], components[1], components[2]);
   }

}
