package org.github.codeswarm;

import java.util.Date;

/**
 * Describe an event on a file
 */
public class FileEvent implements Comparable<Object> {

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

   public int getLinesadded() {
      return linesadded;
   }

   public int getLinesremoved() {
      return linesremoved;
   }

   /**
    * Comparing two events by date (Not Used)
    *
    * @param o
    * @return -1 if <, 0 if =, 1 if >
    */
   @Override
   public int compareTo(Object o) {
      return date.compareTo(((FileEvent) o).date);
   }
}
