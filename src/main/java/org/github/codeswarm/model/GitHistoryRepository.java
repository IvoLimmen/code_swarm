package org.github.codeswarm.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.github.codeswarm.FileEvent;

public class GitHistoryRepository implements HistoryRepository {

   private final String path;

   public static void main(String[] args) {
      HistoryRepository hr = new GitHistoryRepository("/home/ivo/projects/java/code_swarm/.git");
      hr.getHistory();
   }

   public GitHistoryRepository(String path) {
      this.path = path;
   }

   @Override
   public Collection<FileEvent> getHistory() {
      Set<FileEvent> events = new HashSet<>();
      try {

         Repository repository = FileRepositoryBuilder.create(new File(path));

         Git git = new Git(repository);

         Iterable<RevCommit> log = git.log().all().call();
         for (RevCommit commit : log) {
            RevTree tree = commit.getTree();
            System.out.println("revCommit:" + commit.getShortMessage());

            long when = commit.getAuthorIdent().getWhen().getTime();
            String person = commit.getAuthorIdent().getEmailAddress();

            List<String> items = new ArrayList<>();

            try (TreeWalk treeWalk = new TreeWalk(repository)) {
               treeWalk.addTree(tree);
               treeWalk.setRecursive(false);
               treeWalk.setPostOrderTraversal(false);

               while (treeWalk.next()) {
                  items.add(treeWalk.getPathString());
               }
            }

            items.forEach(i -> {
               events.add(new FileEvent(when, person, i, null));
            });
         }
      }
      catch (Exception ex) {
         ex.printStackTrace();
      }

      return events;
   }
}
