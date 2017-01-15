package org.github.codeswarm;

import java.util.Date;
import java.util.Objects;

/**
 * Describe an event on a file
 */
public class FileEvent {

   private Date date;
   private String author;
   private String filename;
   private String path;
   private int linesadded;
   private int linesremoved;

   /**
    * short constructor with base data
    */
   public FileEvent(long datenum, String author, String path, String filename) {
      this(datenum, author, path, filename, 0, 0);
   }

   /**
    * constructor with number of modified lines
    */
   public FileEvent(long datenum, String author, String path, String filename, int linesadded, int linesremoved) {
      this.date = new Date(datenum);
      this.author = author;
      this.path = path;
      this.filename = filename;
      this.linesadded = linesadded;
      this.linesremoved = linesremoved;
   }

   public Date getDate() {
      return date;
   }

   public String getAuthor() {
      return author;
   }

   public String getFilename() {
      return filename;
   }

   public String getPath() {
      return path;
   }

   @Override
   public int hashCode() {
      int hash = 3;
      hash = 13 * hash + Objects.hashCode(this.date);
      hash = 13 * hash + Objects.hashCode(this.author);
      hash = 13 * hash + Objects.hashCode(this.filename);
      hash = 13 * hash + Objects.hashCode(this.path);
      return hash;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final FileEvent other = (FileEvent) obj;
      if (!Objects.equals(this.author, other.author)) {
         return false;
      }
      if (!Objects.equals(this.filename, other.filename)) {
         return false;
      }
      if (!Objects.equals(this.path, other.path)) {
         return false;
      }
      if (!Objects.equals(this.date, other.date)) {
         return false;
      }
      return true;
   }

   public int getLinesadded() {
      return linesadded;
   }

   public int getLinesremoved() {
      return linesremoved;
   }
}
