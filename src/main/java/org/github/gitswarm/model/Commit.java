package org.github.gitswarm.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.github.gitswarm.FileEvent;

public class Commit implements Comparable<Commit> {

   private final Date date;

   private List<FileEvent> events = new ArrayList<>();

   public Commit(List<FileEvent> events, Date date) {
      this.events = events;
      this.date = date;
   }

   public List<FileEvent> getEvents() {
      return events;
   }

   public Date getDate() {
      return date;
   }      

   @Override
   public int compareTo(Commit o) {
      return this.date.compareTo(o.date);
   }
}
