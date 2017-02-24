package org.github.gitswarm.model;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.github.gitswarm.FileEvent;

public class GitHistoryRepository implements HistoryRepository {

   private final String path;

   private static final DateTimeFormatter DATETIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").toFormatter();

   public static void main(String[] args) {
      HistoryRepository hr = new GitHistoryRepository("/home/ivo/projects/java/git_swarm/.git");
      hr.getHistory(-1).forEach((c) -> {
         LocalDateTime dt = LocalDateTime.ofInstant(c.getDate().toInstant(), ZoneId.systemDefault());
         c.getEvents().forEach((f) -> {
            System.out.println(DATETIME_FORMATTER.format(dt) + " Author: " + f.getAuthor() + " File: " + f.getFilename() + " Path: " + f.getPath());
         });
      });
   }

   public GitHistoryRepository(String path) {
      this.path = path;
   }

   @Override
   public List<Commit> getHistory(long limit) {
      SortedSet<Commit> events = new TreeSet<>();
      try {

         Repository repository = FileRepositoryBuilder.create(new File(path));

         Git git = new Git(repository);

         Iterable<RevCommit> log = git.log().all().call();
		 long count = 0;
         for (RevCommit commit : log) {
            List<FileEvent> files = new ArrayList<>();
            RevTree tree = commit.getTree();

            long when = commit.getAuthorIdent().getWhen().getTime();
            String person = commit.getAuthorIdent().getEmailAddress();

            try (TreeWalk treeWalk = new TreeWalk(repository)) {
               treeWalk.addTree(tree);
               treeWalk.setRecursive(false);
               treeWalk.setPostOrderTraversal(false);
               treeWalk.setOperationType(TreeWalk.OperationType.CHECKIN_OP);

               String subPath = null;
               while (treeWalk.next()) {
                  if (treeWalk.isSubtree()) {
                     subPath = treeWalk.getPathString();
                     treeWalk.enterSubtree();
                  } else {
                     files.add(new FileEvent(when, person, subPath, treeWalk.getPathString()));
                  }
               }
            }

            events.add(new Commit(files, new Date(when)));
            commit.disposeBody();
			count++;
			if (limit > 0 && count == limit) {
				break;
			}
         }
      }
      catch (Exception ex) {
         ex.printStackTrace();
      }

      return new ArrayList<>(events);
   }
}
