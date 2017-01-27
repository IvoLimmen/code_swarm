package org.github.gitswarm.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import javafx.concurrent.Task;
import org.github.gitswarm.ColorTest;
import org.github.gitswarm.Config;
import org.github.gitswarm.model.Commit;
import org.github.gitswarm.model.GitHistoryRepository;

public class GitHistoryAnalyzer extends Task<List<ColorTest>> {

   private int currentColor = 0;

   private int currentCommit = 0;
   
   private final Color[] colors;

   public GitHistoryAnalyzer(Color[] colors) {
      this.colors = colors;
   }   
   
   private String determineExtention(String fileName) {
      if (fileName.contains(".")) {
         return fileName.substring(fileName.lastIndexOf(".") + 1);
      }
      return "";
   }

   private Color getColor() {
      if (currentColor == colors.length) {
         currentColor = 0;
      }
      return colors[currentColor++];
   }
   
   @Override
   protected List<ColorTest> call() throws Exception {
      List<ColorTest> colorList = new ArrayList<>();
      List<Commit> commits = new GitHistoryRepository(Config.getInstance().getGitDirectory()).getHistory();

      Set<FileTypeCount> extentions = new TreeSet<>();
      currentCommit = 0;
      
      commits.forEach((c) -> {         
         updateProgress(currentCommit++, commits.size());
         c.getEvents().forEach((f) -> {
            String ext = determineExtention(f.getFilename());
            Optional<FileTypeCount> ftc = extentions.stream().filter(sf -> sf.getExt().equals(ext)).findFirst();
            if (!ftc.isPresent()) {
               extentions.add(new FileTypeCount(ext));
            } else {
               ftc.get().add();
            }
         });         
      });

      extentions.stream().forEach(e -> {
         if (!e.getExt().equals("")) {
            colorList.add(new ColorTest(e.getExt(), ".*" + e.getExt() + "*", getColor()));
         }
      });

      return colorList;
   }

   private class FileTypeCount implements Comparable<FileTypeCount> {

      private final String ext;

      private int count;

      public FileTypeCount(String ext) {
         this.ext = ext;
         this.count = 1;
      }

      public void add() {
         this.count++;
      }

      public int getCount() {
         return count;
      }

      public String getExt() {
         return ext;
      }

      @Override
      public int compareTo(FileTypeCount o) {
         return o.count - this.count;
      }
   }
}
