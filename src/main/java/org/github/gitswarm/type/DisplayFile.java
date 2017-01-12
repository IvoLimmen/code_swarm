package org.github.gitswarm.type;

public enum DisplayFile {
   FUZZY("fuzzy"),
   JELLY("jelly"),
   SHARP("sharp");
   
   private final String label;
   
   private DisplayFile(String label) {
      this.label = label;
   }

   public String getLabel() {
      return label;
   }      
}
