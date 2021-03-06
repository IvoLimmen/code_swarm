package org.github.gitswarm;

/**
 * Copyright 2008 Michael Ogawa
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
import org.github.gitswarm.model.Drawable;
import org.github.gitswarm.model.Edge;
import org.github.gitswarm.model.PersonNode;
import org.github.gitswarm.model.FileNode;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.github.gitswarm.avatar.AvatarFetcher;
import org.github.gitswarm.avatar.AvatarFetcherChainer;
import org.github.gitswarm.avatar.GitHubFetcher;
import org.github.gitswarm.avatar.GravatarFetcher;
import org.github.gitswarm.gui.ColorUtil;
import org.github.gitswarm.model.Commit;
import org.github.gitswarm.model.GitHistoryRepository;
import org.github.gitswarm.model.HistoryRepository;
import org.github.gitswarm.type.DisplayFile;
import static org.github.gitswarm.type.DisplayFile.FUZZY;
import static org.github.gitswarm.type.DisplayFile.JELLY;
import static org.github.gitswarm.type.DisplayFile.SHARP;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class GitSwarm extends PApplet {

   /**
    * @remark needed for any serializable class
    */
   public static final long serialVersionUID = 0;

   private static Map<String, FileNode> nodes;
   private static Map<Pair<FileNode, PersonNode>, Edge> edges;
   private static Map<String, PersonNode> people;
   // Liveness cache
   private static List<PersonNode> livingPeople = new ArrayList<>();
   private static List<Edge> livingEdges = new ArrayList<>();
   private static List<FileNode> livingNodes = new ArrayList<>();

   /**
    * @return list of people whose life is > 0
    */
   static List<PersonNode> getLivingPeople() {
      return Collections.unmodifiableList(livingPeople);
   }

   /**
    * @return list of edges whose life is > 0
    */
   private static List<Edge> getLivingEdges() {
      return Collections.unmodifiableList(livingEdges);
   }

   /**
    * @return list of file nodes whose life is > 0
    */
   private static List<FileNode> getLivingNodes() {
      return Collections.unmodifiableList(livingNodes);
   }

   private static <T extends Drawable> List<T> filterLiving(Collection<T> iter) {
      ArrayList<T> livingThings = new ArrayList<>(iter.size());
      iter.stream().filter((thing) -> (thing.isAlive())).forEachOrdered((thing) -> {
         livingThings.add(thing);
      });
      return livingThings;
   }

   static public void boot() {
      PApplet.main(new String[]{"org.github.gitswarm.GitSwarm"});
   }

   @Override
   public void exitActual() {
      // no no...
      this.surface.setVisible(false);
   }

   // User-defined variables
   long UPDATE_DELTA = -1;
   int background;
   int PARTICLE_SIZE = 2;

   // Data storage
   HistoryRepository historyRepository;
   List<Commit> commits = new ArrayList<>();

   LinkedList<List<Integer>> history;
   boolean finishedLoading = false;

   // Temporary variables
   FileEvent currentEvent;
   Date nextDate;
   Date prevDate;
   FileNode prevNode;
   int maxTouches;

   // Graphics objects
   PFont font;
   PFont boldFont;
   PImage sprite;
   PImage avatarMask;

   boolean paused = false;

   // Graphics state variables
   boolean showHelp;
   boolean showDebug;

   // Color mapper
   int currentColor;

   // Physics engine configuration
   private final PhysicsEngine physicsEngine = new PhysicsEngineOrderly();

   // Formats the date string nicely
   SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

   private long lastDrawDuration = 0;
   private int maxFramesSaved;

   protected ExecutorService backgroundExecutor;

   public AvatarFetcher avatarFetcher = new AvatarFetcherChainer(new GitHubFetcher(), new GravatarFetcher());

   private int fontColor;

   @Override
   public void settings() {

      this.width = Config.getInstance().getWidth().getValue();
      this.height = Config.getInstance().getHeight().getValue();

      size(width, height);
   }

   /**
    * Initialization
    */
   @Override
   public void setup() {

      int maxBackgroundThreads = 4;
      backgroundExecutor = new ThreadPoolExecutor(1, maxBackgroundThreads, Long.MAX_VALUE, TimeUnit.NANOSECONDS, new ArrayBlockingQueue<>(4 * maxBackgroundThreads), new ThreadPoolExecutor.CallerRunsPolicy());

      showDebug = false;

      background = ColorUtil.toAwtColor(Config.getInstance().getBackground().getValue()).getRGB();
      fontColor = ColorUtil.toAwtColor(Config.getInstance().getFontColor().getValue()).getRGB();

      double framesperday = Config.getInstance().getFramesPerDay();
      UPDATE_DELTA = (long) (86400000 / framesperday);

      smooth();
      frameRate(24);

      // init data structures
      nodes = new HashMap<>();
      edges = new HashMap<>();
      people = new HashMap<>();
      history = new LinkedList<>();

      loadRepEvents();

      if (commits.isEmpty()) {
         return;
      }

      prevDate = commits.get(0).getDate();

      maxFramesSaved = (int) Math.pow(10, Config.getInstance().getScreenshotFileMask().getValue().replaceAll("[^#]", "").length());

      // Create fonts
      String fontName = Config.getInstance().getFont();
      String boldFontName = Config.getInstance().getBoldFont();
      Integer fontSize = Config.getInstance().getFontSize().getValue();
      Integer fontSizeBold = Config.getInstance().getBoldFontSize().getValue();
      font = createFont(fontName, fontSize);
      boldFont = createFont(boldFontName, fontSizeBold);

      textFont(font);

      // Create the file particle image
      sprite = loadImage("src/main/resources/particle.png");
      avatarMask = loadImage("src/main/resources/mask.png");
      avatarMask.resize(40, 40);
      // Add translucency (using itself in this case)
      sprite.mask(sprite);
   }

   /**
    * Main loop
    */
   @Override
   public void draw() {
      long start = System.currentTimeMillis();
      background(background); // clear screen with background color

      this.update(); // update state to next frame

      // Draw edges (for debugging only)
      if (Config.getInstance().getShowEdges().getValue()) {
         edges.values().forEach((edge) -> {
            if (edge.getLife() > 40) {
               stroke(255, edge.getLife() + 100);
               strokeWeight(0.35f);
               line(edge.getNodeFrom().getPosition().x, edge.getNodeFrom().getPosition().y, edge.getNodeTo().getPosition().x, edge.getNodeTo().getPosition().y);
            }
         });
      }

      // Surround names with aura
      // Then blur it
      if (Config.getInstance().getDrawNamesHalo().getValue()) {
         drawPeopleNodesBlur();
      }

      // Then draw names again, but sharp
      if (Config.getInstance().getDrawNamesSharp().getValue()) {
         drawPeopleNodesSharp();
      }

      // Draw file particles
      getLivingNodes().forEach((node) -> {
         drawFileNode(node);
      });

      textFont(font);

      if (showDebug) {
         // debug override legend information
         drawDebugData();
      } else if (Config.getInstance().getShowLegend().getValue()) {
         // legend only if nothing "more important"
         drawLegend();
      }

      if (Config.getInstance().getShowPopular().getValue()) {
         drawPopular();
      }

      if (Config.getInstance().getShowHistogram().getValue()) {
         drawHistory();
      }

      if (Config.getInstance().getShowDate().getValue()) {
         drawDate();
      }

      if (Config.getInstance().getTakeSnapshots().getValue()) {
         dumpFrame();
      }

      // Stop animation when we run out of data
      if (finishedLoading) {
         // noLoop();
         backgroundExecutor.shutdown();
         try {
            backgroundExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
         } catch (InterruptedException e) {
            /* Do nothing, just exit */
         }
         exit();
      }

      long end = System.currentTimeMillis();
      lastDrawDuration = end - start;
   }

   /**
    * Surround names with aura
    */
   public void drawPeopleNodesBlur() {
      colorMode(HSB);
      // First draw the name
      getLivingPeople().stream().map((p) -> {
         fill(hue(p.getFlavor()), 64, 255, p.getLife());
         return p;
      }).forEachOrdered((p) -> {
         drawPersonNode(p);
      });
      // Then blur it
      filter(BLUR, 3);
   }

   private void drawFileNode(FileNode n) {
      if (n.isAlive()) {
         DisplayFile displayFile = Config.getInstance().getDisplayFile();

         float currentWidth = 0f;
         if (displayFile.equals(SHARP)) {
            colorMode(RGB);
            fill(n.getNodeHue(), n.getLife());
            float w = 3 * PARTICLE_SIZE;
            currentWidth = w;
            if (n.getLife() >= n.getMinBold()) {
               stroke(255, 128);
               w *= 2;
            } else {
               noStroke();
            }

            ellipseMode(CENTER);
            ellipse(n.getPosition().x, n.getPosition().y, w, w);
         }
         if (displayFile.equals(FUZZY)) {
            tint(n.getNodeHue(), n.getLife());

            float w = (8 + (sqrt(n.getTouches()) * 4)) * PARTICLE_SIZE;
            currentWidth = w;
            // not used float dubw = w * 2;
            float halfw = w / 2;
            if (n.getLife() >= n.getMinBold()) {
               colorMode(HSB);
               tint(hue(n.getNodeHue()), saturation(n.getNodeHue()) - 192, 255, n.getLife());
               // image( sprite, x - w, y - w, dubw, dubw );
            }
            // else
            image(sprite, n.getPosition().x - halfw, n.getPosition().y - halfw, w, w);
         }
         if (displayFile.equals(JELLY)) {
            noFill();
            if (n.getLife() >= n.getMinBold()) {
               stroke(255);
            } else {
               stroke(n.getNodeHue(), n.getLife());
            }
            float w = sqrt(n.getTouches()) * PARTICLE_SIZE;
            currentWidth = w;
            ellipseMode(CENTER);
            ellipse(n.getPosition().x, n.getPosition().y, w, w);
         }
         // Draw motion blur
         // float d = mPosition.distance(mLastPosition);

         float nx = n.getPosition().x - n.getLastPosition().x;
         float ny = n.getPosition().y - n.getLastPosition().y;
         float d = (float) Math.sqrt(nx * nx + ny * ny);

         stroke(n.getNodeHue(), min(255f * (d / 10f), 255f) / 10f);
         strokeCap(ROUND);
         strokeWeight(currentWidth / 4f);
         // strokeWeight((float)life / 10.0 * (float)PARTICLE_SIZE);
         line(n.getPosition().x, n.getPosition().y, n.getLastPosition().x, n.getLastPosition().y);
      }
   }

   private void drawPersonNode(PersonNode p) {
      if (p.isAlive()) {
         textAlign(CENTER, CENTER);

         /**
          * TODO: proportional font size, or light intensity, or some sort of thing to disable the flashing
          */
         if (p.getLife() >= p.getMinBold()) {
            textFont(boldFont);
         } else {
            textFont(font);
         }

         fill(fontColor, p.getLife());
         if (Config.getInstance().getShowUsername().getValue()) {
            text(p.getName(), p.getPosition().x, p.getPosition().y + 10);
         }
         if (p.getIcon() != null) {
            colorMode(RGB);
            tint(255, 255, 255, max(0, p.getLife() - 80));
            image(p.getIcon(), p.getPosition().x - (avatarFetcher.getSize() / 2), p.getPosition().y - (avatarFetcher.getSize() - (Config.getInstance().getShowUsername().getValue() ? 5 : 15)));
         }
      }
   }

   /**
    * Draw person's name
    */
   public void drawPeopleNodesSharp() {
      colorMode(RGB);
      getLivingPeople().stream().map((p) -> {
         fill(lerpColor(p.getFlavor(), color(255), 0.5f), max(p.getLife() - 50, 0));
         return p;
      }).forEachOrdered((p) -> {
         drawPersonNode(p);
      });
   }

   /**
    * Draw date in lower-right corner
    */
   public void drawDate() {
      fill(fontColor, 255);
      String dateText = formatter.format(prevDate);
      textAlign(RIGHT, BASELINE);
      textSize(font.getSize());
      text(dateText, width - 3, height - (2 + textDescent()));
   }

   /**
    * Draw histogram in lower-left
    */
   public void drawHistory() {
      int counter = 0;
      strokeWeight(PARTICLE_SIZE);
      for (List<Integer> list : history) {
         if (!list.isEmpty()) {
            int color = list.get(0);
            int start = 0;
            int end = 0;
            for (int nextColor : list) {
               if (nextColor == color) {
                  end++;
               } else {
                  stroke(color, 255);
                  rectMode(CORNERS);
                  rect(counter, height - start - 3, counter, height - end - 3);
                  start = end;
                  color = nextColor;
               }
            }
         }
         counter += 1;
      }
   }

   /**
    * Show color codings
    */
   public void drawLegend() {
      noStroke();
      fill(fontColor, 255);
      textFont(font);
      textAlign(LEFT, TOP);
      text("Legend:", 3, 3);
      for (int i = 0; i < Config.getInstance().getColorAssigner().getTests().size(); i++) {
         ColorTest t = Config.getInstance().getColorAssigner().getTests().get(i);
         fill(t.getC1().getRGB(), 200);
         text(t.getLabel(), font.getSize(), 3 + ((i + 1) * (font.getSize() + 2)));
      }
   }

   /**
    * Show debug information about all drawable objects
    */
   public void drawDebugData() {
      noStroke();
      textFont(font);
      textAlign(LEFT, TOP);
      fill(fontColor, 200);
      text("Nodes: " + nodes.size(), 0, 0);
      text("People: " + people.size(), 0, 10);
      text("Queue: " + commits.size(), 0, 20);
      text("Last render time: " + lastDrawDuration, 0, 30);
   }

   /**
    * TODO This could be made to look a lot better.
    */
   private void drawPopular() {
      CopyOnWriteArrayList<FileNode> al = new CopyOnWriteArrayList<>();
      noStroke();
      textFont(font);
      textAlign(RIGHT, TOP);
      fill(fontColor, 200);
      text("Popular Nodes (touches):", width - 120, 0);
      for (FileNode fn : nodes.values()) {
         if (fn.qualifies()) {
            // Insertion Sort
            if (al.size() > 0) {
               int j = 0;
               for (; j < al.size(); j++) {
                  if (fn.compareTo(al.get(j)) > 0) {
                     break;
                  }
               }
               al.add(j, fn);
            } else {
               al.add(fn);
            }
         }
      }

      int i = 1;
      ListIterator<FileNode> it = al.listIterator();
      while (it.hasNext()) {
         FileNode n = it.next();
         // Limit to the top 10.
         if (i <= 10) {
            text(n.getName() + "  (" + n.getTouches() + ")", width - 100, 10 * i++);
         } else if (i > 10) {
            break;
         }
      }
   }

   /**
    * Take screenshot
    */
   public void dumpFrame() {
      if (frameCount < this.maxFramesSaved) {
         String screenshotFileMask = Config.getInstance().getScreenshotFileMask().getValue();
         final File outputFile = new File(insertFrame("data/" + screenshotFileMask));
         final PImage image = get();
         outputFile.getParentFile().mkdirs();

         backgroundExecutor.execute(() -> {
            image.save(outputFile.getAbsolutePath());
         });
      }
   }

   private int commitIndex = 0;
   private Commit currentCommit;
   private int emptyDate = 0;

   /**
    * Update the particle positions
    */
   public void update() {
      // Create a new histogram line
      List<Integer> colorList = new ArrayList<>();
      history.add(colorList);

      nextDate = new Date(prevDate.getTime() + UPDATE_DELTA);

      if (commitIndex + 1 >= commits.size()) {
         exit();
      }

      currentCommit = commits.get(commitIndex);

      if (currentCommit.getDate().before(nextDate)) {
         emptyDate = 0;
         commitIndex = commitIndex + 1;
         currentCommit.getEvents().stream().map((event) -> {
            FileNode file = findNode(event.getPath() + event.getFilename());
            // add to histogram
            colorList.add(file.getNodeHue());
            PersonNode person = findPerson(event.getAuthor());
            colorMode(RGB);
            person.setFlavor(lerpColor(person.getFlavor(), file.getNodeHue(), 1.0f / person.getColorCount()));
            person.setColorCount(person.getColorCount() + 1);
            Edge edge = findEdge(file, person);
            file.setEditor(person);
            return file;
         }).forEachOrdered((file) -> {
            prevNode = file;
         });
      } else {
         emptyDate++;
         if (emptyDate > Config.getInstance().getAllowedEmptyFrames()) {
            nextDate = currentCommit.getDate();
         }
      }

      prevDate = nextDate;

      // sort colorbins
      Collections.sort(colorList);

      // restrict history to drawable area
      while (history.size() > 320) {
         history.remove();
      }

      // Init frame:
      physicsEngine.initializeFrame();

      livingPeople = filterLiving(people.values());
      livingNodes = filterLiving(nodes.values());
      livingEdges = filterLiving(edges.values());

      // update velocity
      getLivingEdges().forEach((edge) -> {
         physicsEngine.onRelax(edge);
      });

      // update velocity
      getLivingNodes().forEach((node) -> {
         physicsEngine.onRelax(node);
      });

      // update velocity
      getLivingPeople().forEach((person) -> {
         physicsEngine.onRelax(person);
      });

      // update position
      getLivingEdges().forEach((edge) -> {
         physicsEngine.onUpdate(edge);
      });

      // update position
      getLivingNodes().forEach((node) -> {
         physicsEngine.onUpdate(node);
      });

      // update position
      getLivingPeople().stream().map((person) -> {
         physicsEngine.onUpdate(person);
         return person;
      }).map((person) -> {
         person.getPosition().x = max(50, min(width - 50, person.getPosition().x));
         return person;
      }).forEachOrdered((person) -> {
         person.getPosition().y = max(45, min(height - 15, person.getPosition().y));
      });

      // Finalize frame:
      physicsEngine.finalizeFrame();
   }

   /**
    * Searches for the FileNode with a given name
    *
    * @param name
    * @return FileNode with matching name or null if not found.
    */
   private FileNode findNode(String name) {
      FileNode file = nodes.get(name);

      if (file == null) {
         file = new FileNode(name, maxTouches);
         physicsEngine.startLocation(file);
         physicsEngine.startVelocity(file);
         colorMode(RGB);
         nodes.put(name, file);
      } else {
         file.freshen();
      }
      
      return file;
   }

   /**
    * Searches for the Edge connecting the given nodes
    */
   private Edge findEdge(FileNode file, PersonNode person) {
      Edge edge = edges.get(new MutablePair<>(file, person));

      if (edge == null) {
         edge = new Edge(file, person);
         edges.put(new MutablePair<>(file, person), edge);
      } else {
         edge.freshen();
      }
      return edge;
   }

   /**
    * Searches for the PersonNode with a given name.
    */
   private PersonNode findPerson(String name) {
      PersonNode person = people.get(name);
      if (person == null) {
         person = new PersonNode(name);
         String iconFile = avatarFetcher.fetchUserImage(person.getName());
         if (iconFile != null) {
            PImage icon = loadImage(iconFile, "unknown");
            icon.resize(avatarFetcher.getSize(), avatarFetcher.getSize());
            icon.mask(avatarMask);
            person.setIcon(icon);
         }
         physicsEngine.startLocation(person);
         physicsEngine.startVelocity(person);
         people.put(name, person);
      } else {
         person.freshen();
      }

      return person;
   }

   /**
    * Load the standard event-formatted file.
    */
   public void loadRepEvents() {
      this.commits = new GitHistoryRepository(Config.getInstance().getGitDirectory()).getHistory(-1);
   }
}
