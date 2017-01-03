package org.github.codeswarm;

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
import org.github.codeswarm.avatar.AvatarFetcher;
import org.github.codeswarm.model.Drawable;
import org.github.codeswarm.model.Edge;
import org.github.codeswarm.model.PersonNode;
import org.github.codeswarm.model.FileNode;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.github.codeswarm.avatar.GravatarFetcher;
import org.github.codeswarm.avatar.LocalAvatar;
import org.github.codeswarm.gui.MainConfigPanel;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class CodeSwarm extends PApplet implements EndOfFileEvent {

   /**
    * @remark needed for any serializable class
    */
   public static final long serialVersionUID = 0;

   private static Map<String, FileNode> nodes;
   private static Map<Pair<FileNode, PersonNode>, Edge> edges;
   private static Map<String, PersonNode> people;
   // Liveness cache
   static List<PersonNode> livingPeople = new ArrayList<>();
   static List<Edge> livingEdges = new ArrayList<>();
   static List<FileNode> livingNodes = new ArrayList<>();
   //kinda a hack that these two are static
   protected static String userConfigFilename = null;

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

   /**
    * code_swarm Entry point.
    *
    * @param args : should be the path to the config file
    */
   static public void main(String args[]) {
      try {
         if (args.length > 0) {
            userConfigFilename = args[0];
            List<String> configFileStack = Arrays.asList(new String[]{"defaults/CodeSwarm.config",
               "defaults/user.config", userConfigFilename});
            Config.init(configFileStack);
            PApplet.main(new String[]{"org.github.codeswarm.CodeSwarm"});
         } else {
            // FIXME: Temporary for testing in IDE
            userConfigFilename = "data/sample.config";
            List<String> configFileStack = Arrays.asList(new String[]{"defaults/CodeSwarm.config",
               "defaults/user.config", userConfigFilename});
            Config.init(configFileStack);
            MainConfigPanel.start();

//            System.err.println("Specify a config file.");
//            System.exit(2);
         }
      }
      catch (IOException e) {
         System.err.println("Failed due to exception: " + e.getMessage());
         System.exit(2);
      }
   }

   static public void boot() {
      PApplet.main(new String[]{"org.github.codeswarm.CodeSwarm"});
   }

   @Override
   public void exitActual() {
      // no no...
      this.surface.setVisible(false);
   }
     
   // User-defined variables
   int FRAME_RATE = 24;
   long UPDATE_DELTA = -1;
   String SPRITE_FILE = "particle.png";
   String MASK_FILE = "src/main/resources/mask.png";
   String SCREENSHOT_FILE;
   int background;
   int PARTICLE_SIZE = 2;

   // Data storage
   BlockingQueue<FileEvent> eventsQueue;
   boolean isInputSorted = false;
   boolean showUserName = false;

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
   boolean showHistogram;
   boolean showDate;
   boolean showLegend;
   boolean showPopular;
   boolean showEdges;
   boolean showHelp;
   boolean takeSnapshots;
   boolean showDebug;
   boolean drawNamesSharp;
   boolean drawNamesHalos;
   boolean drawFilesSharp;
   boolean drawFilesFuzzy;
   boolean drawFilesJelly;

   // Color mapper
   ColorAssigner colorAssigner;
   int currentColor;

   // Physics engine configuration
   private final PhysicsEngine physicsEngine = new PhysicsEngineOrderly();
   private boolean circularAvatars = false;

   // Formats the date string nicely
   DateFormat formatter = DateFormat.getDateInstance();

   private long lastDrawDuration = 0;
   private int maxFramesSaved;

   protected ExecutorService backgroundExecutor;

   public AvatarFetcher avatarFetcher;

   private int fontColor;

   @Override
   public void settings() {

      this.width = Config.getInstance().getWidth().getValue();
      this.height = Config.getInstance().getHeight().getValue();

      if (Config.getBooleanProperty(Config.USE_OPEN_GL)) {
         size(width, height, FX2D);
      } else {
         size(width, height);
      }
   }

   /**
    * Initialization
    */
   @Override
   public void setup() {

      showLegend = Config.getInstance().getShowLegend().getValue();
      showHistogram = Config.getInstance().getShowHistogram().getValue();
      showDate = Config.getInstance().getShowDate().getValue();
      showEdges = Config.getInstance().getShowEdges().getValue();
      showPopular = Config.getInstance().getShowPopular().getValue();
      takeSnapshots = Config.getInstance().getTakeSnapshots().getValue();
      showUserName = Config.getInstance().getShowUsername().getValue();

      
      int maxBackgroundThreads = Config.getPositiveIntProperty(Config.MAX_THREADS_KEY);
      backgroundExecutor = new ThreadPoolExecutor(1, maxBackgroundThreads, Long.MAX_VALUE, TimeUnit.NANOSECONDS, new ArrayBlockingQueue<>(4 * maxBackgroundThreads), new ThreadPoolExecutor.CallerRunsPolicy());
      
      showDebug = Config.getBooleanProperty(Config.SHOW_DEBUG);
      drawNamesSharp = Config.getBooleanProperty(Config.DRAW_NAMES_SHARP);
      drawNamesHalos = Config.getBooleanProperty(Config.DRAW_NAMES_HALOS);
      drawFilesSharp = Config.getBooleanProperty(Config.DRAW_FILES_SHARP);
      drawFilesFuzzy = Config.getBooleanProperty(Config.DRAW_FILES_FUZZY);
      drawFilesJelly = Config.getBooleanProperty(Config.DRAW_FILES_JELLY);
      circularAvatars = Config.getBooleanProperty(Config.DRAW_CIRCULAR_AVATARS);

      background = Config.getColorProperty(Config.BACKGROUND_KEY).getRGB();
      fontColor = Config.getColorProperty(Config.FONT_COLOR_KEY).getRGB();

      double framesperday = Config.getDoubleProperty(Config.FRAMES_PER_DAY_KEY);
      UPDATE_DELTA = (long) (86400000 / framesperday);

      isInputSorted = Config.getBooleanProperty(Config.IS_INPUT_SORTED_KEY);      

      smooth();
      frameRate(FRAME_RATE);

      // init data structures
      nodes = new HashMap<>();
      edges = new HashMap<>();
      people = new HashMap<>();
      history = new LinkedList<>();
      //If the input is sorted, we only need to store the next few events
      if (isInputSorted) {
         eventsQueue = new ArrayBlockingQueue<>(50000);
      } else {
         //Otherwise we need to store them all at once in a data structure that will sort them
         eventsQueue = new PriorityBlockingQueue<>();
      }

      // Init color map
      initColors();

      avatarFetcher = getAvatarFetcher(Config.getStringProperty("AvatarFetcher"));
      avatarFetcher.setSize(Config.getPositiveIntProperty("AvatarSize"));
      if (avatarFetcher.getClass().equals(LocalAvatar.class)) {
         ((LocalAvatar) avatarFetcher).setLocalAvatarDefaultPic(Config.getStringProperty("LocalAvatarDefaultPic"));
         ((LocalAvatar) avatarFetcher).setLocalAvatarDirectory(Config.getStringProperty("LocalAvatarDirectory"));
      } else if (avatarFetcher.getClass().equals(GravatarFetcher.class)) {
         ((GravatarFetcher) avatarFetcher).setGravatarFallback(Config.getStringProperty("GravatarFallback"));
      }

      loadRepEvents(Config.getStringProperty(Config.INPUT_FILE_KEY)); // event formatted (this is the standard)
      while (!finishedLoading && eventsQueue.isEmpty());
      if (eventsQueue.isEmpty()) {
         System.out.println("No events found in repository xml file.");
         return;
      }
      prevDate = eventsQueue.peek().getDate();

      SCREENSHOT_FILE = Config.getStringProperty(Config.SNAPSHOT_LOCATION_KEY);

      maxFramesSaved = (int) Math.pow(10, SCREENSHOT_FILE.replaceAll("[^#]", "").length());

      // Create fonts
      String fontName = Config.getStringProperty(Config.FONT_KEY);
      String boldFontName = Config.getStringProperty(Config.FONT_KEY_BOLD);
      Integer fontSize = Config.getPositiveIntProperty(Config.FONT_SIZE);
      Integer fontSizeBold = Config.getPositiveIntProperty(Config.FONT_SIZE_BOLD);
      font = createFont(fontName, fontSize);
      boldFont = createFont(boldFontName, fontSizeBold);

      textFont(font);

      String SPRITE_FILE = Config.getStringProperty(Config.SPRITE_FILE_KEY);
      // Create the file particle image
      sprite = loadImage(SPRITE_FILE);
      avatarMask = loadImage(MASK_FILE);
      avatarMask.resize(Config.getPositiveIntProperty("AvatarSize"), Config.getPositiveIntProperty("AvatarSize"));
      // Add translucency (using itself in this case)
      sprite.mask(sprite);
   }

   @SuppressWarnings("unchecked")
   private AvatarFetcher getAvatarFetcher(String avatarFetcherName) {
      try {
         Class<AvatarFetcher> c = (Class<AvatarFetcher>) Class.forName(avatarFetcherName);
         return c.getConstructor().newInstance();
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Load a colormap
    */
   public void initColors() {
      colorAssigner = new ColorAssigner();
      int i = 1;
      String property;
      while ((property = Config.getColorAssignProperty(i)) != null) {
         ColorTest ct = new ColorTest();
         ct.loadProperty(property);
         colorAssigner.addRule(ct);
         i++;
      }
      // Load the default.
      ColorTest ct = new ColorTest();
      ct.loadProperty(Config.DEFAULT_COLOR_ASSIGN);
      colorAssigner.addRule(ct);
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
      if (showEdges) {
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
      if (drawNamesHalos) {
         drawPeopleNodesBlur();
      }

      // Then draw names again, but sharp
      if (drawNamesSharp) {
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
      } else if (showLegend) {
         // legend only if nothing "more important"
         drawLegend();
      }

      if (showPopular) {
         drawPopular();
      }

      if (showHistogram) {
         drawHistory();
      }

      if (showDate) {
         drawDate();
      }

      if (takeSnapshots) {
         dumpFrame();
      }

      // Stop animation when we run out of data
      if (finishedLoading && eventsQueue.isEmpty()) {
         // noLoop();
         backgroundExecutor.shutdown();
         try {
            backgroundExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
         }
         catch (InterruptedException e) {
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
         float currentWidth = 0f;
         if (drawFilesSharp) {
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
         if (drawFilesFuzzy) {
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
         if (drawFilesJelly) {
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
         /**
          * TODO : this would become interesting on some special event, or for special materials colorMode( RGB ); fill(
          * 0, life ); textAlign( CENTER, CENTER ); text( name, x, y ); Example below:
          */
         if (showPopular) {
            textAlign(CENTER, CENTER);
            fill(fontColor, 200);
            if (n.qualifies()) {
               text(n.getTouches(), n.getPosition().x, n.getPosition().y - (8 + (int) Math.sqrt(n.getTouches())));
            }
         }
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
         if (showUserName) {
            text(p.getName(), p.getPosition().x, p.getPosition().y + 10);
         }
         if (p.getIcon() != null) {
            colorMode(RGB);
            tint(255, 255, 255, max(0, p.getLife() - 80));
            image(p.getIcon(), p.getPosition().x - (avatarFetcher.getSize() / 2), p.getPosition().y - (avatarFetcher.size - (showUserName ? 5 : 15)));
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
      for (int i = 0; i < colorAssigner.getTests().size(); i++) {
         ColorTest t = colorAssigner.getTests().get(i);
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
      text("Queue: " + eventsQueue.size(), 0, 20);
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
         final File outputFile = new File(insertFrame(SCREENSHOT_FILE));
         final PImage image = get();
         outputFile.getParentFile().mkdirs();

         backgroundExecutor.execute(() -> {
            image.save(outputFile.getAbsolutePath());
         });
      }
   }

   /**
    * Update the particle positions
    */
   public void update() {
      // Create a new histogram line
      List<Integer> colorList = new ArrayList<>();
      history.add(colorList);

      nextDate = new Date(prevDate.getTime() + UPDATE_DELTA);
      currentEvent = eventsQueue.peek();

      while (currentEvent != null && currentEvent.getDate().before(nextDate)) {
         if (finishedLoading) {
            currentEvent = eventsQueue.poll();
            if (currentEvent == null) {
               return;
            }
         } else {
            try {
               currentEvent = eventsQueue.take();
            }
            catch (InterruptedException e) {
               System.out.println("Interrupted while fetching current event from eventsQueue");
               continue;
            }
         }

         FileNode file = findNode(currentEvent.getPath() + currentEvent.getFilename());
         if (file == null) {
            int dec = Config.getIntProperty(Config.FILE_DECREMENT_KEY);
            int life = Config.getIntProperty(Config.FILE_LIFE_KEY);
            int highlight = Config.getIntProperty(Config.HIGHLIGHT_PCT_KEY);
            float mass = Config.getFloatProperty(Config.FILE_MASS_KEY);
            file = new FileNode(currentEvent, life, dec, highlight, mass, colorAssigner.getColor(currentEvent.getPath() + currentEvent.getFilename()), maxTouches);
            physicsEngine.startLocation(file);
            physicsEngine.startVelocity(file);
            colorMode(RGB);
            nodes.put(currentEvent.getPath() + currentEvent.getFilename(), file);
         } else {
            file.freshen();
         }

         // add to histogram
         colorList.add(file.getNodeHue());

         PersonNode person = findPerson(currentEvent.getAuthor());
         if (person == null) {
            float mass = Config.getFloatProperty(Config.PERSON_MASS_KEY);
            int dec = Config.getIntProperty(Config.PERSON_DECREMENT_KEY);
            int life = Config.getIntProperty(Config.PERSON_LIFE_KEY);
            int highlight = Config.getIntProperty(Config.HIGHLIGHT_PCT_KEY);
            person = new PersonNode(currentEvent.getAuthor(), life, dec, highlight, mass, color(0));

            String iconFile = avatarFetcher.fetchUserImage(person.getName());
            if (iconFile != null) {
               PImage icon = loadImage(iconFile, "unknown");
               icon.resize(avatarFetcher.getSize(), avatarFetcher.getSize());
               if (circularAvatars) {
                  icon.mask(avatarMask);
               }
               person.setIcon(icon);
            }

            physicsEngine.startLocation(person);
            physicsEngine.startVelocity(person);
            people.put(currentEvent.getAuthor(), person);
         } else {
            person.freshen();
         }
         colorMode(RGB);
         person.setFlavor(lerpColor(person.getFlavor(), file.getNodeHue(), 1.0f / person.getColorCount()));
         person.setColorCount(person.getColorCount() + 1);

         Edge edge = findEdge(file, person);
         if (edge == null) {
            float length = Config.getFloatProperty(Config.EDGE_LENGTH_KEY);
            int dec = Config.getIntProperty(Config.EDGE_DECREMENT_KEY);
            int life = Config.getIntProperty(Config.EDGE_LIFE_KEY);
            edge = new Edge(file, person, life, dec, length);
            edges.put(new MutablePair<>(file, person), edge);
         } else {
            edge.freshen();
         }

         file.setEditor(person);

         /*
       * if ( currentEvent.date.equals( prevDate ) ) { Edge e = findEdge( n, prevNode
       * ); if ( e == null ) { e = new Edge( n, prevNode ); edges.add( e ); } else {
       * e.freshen(); } }
          */
         // prevDate = currentEvent.date;
         prevNode = file;
         if (finishedLoading) {
            currentEvent = eventsQueue.peek();
         } else {
            while (eventsQueue.isEmpty());
            currentEvent = eventsQueue.peek();
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

      /*
	We cache liveness information at the beginning on the update cycle.

	Have have to do it this way as the physics engine onRelax methods
	loop on all living elements and filtering this for every element
	gets too painfull slow on logs with over 100.000 entries.
       */
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
      return nodes.get(name);
   }

   /**
    * Searches for the Edge connecting the given nodes
    *
    * @param n1 From
    * @param n2 To
    * @return Edge connecting n1 to n2 or null if not found
    */
   private Edge findEdge(FileNode n1, PersonNode n2) {
      return edges.get(new MutablePair<>(n1, n2));
   }

   /**
    * Searches for the PersonNode with a given name.
    *
    * @param name
    * @return PersonNode for given name or null if not found.
    */
   private PersonNode findPerson(String name) {
      return people.get(name);
   }

   /**
    * Load the standard event-formatted file.
    *
    * @param filename
    */
   public void loadRepEvents(String filename) {
      if (userConfigFilename != null) {
         String parentPath = new File(userConfigFilename).getParentFile().getAbsolutePath();
         File fileInConfigDir = new File(parentPath, filename);
         if (fileInConfigDir.exists()) {
            filename = fileInConfigDir.getAbsolutePath();
         }
      }

      final String fullFilename = filename;
      Runnable eventLoader = new XMLQueueLoader(fullFilename, eventsQueue, isInputSorted, this, avatarFetcher);

      if (isInputSorted) {
         backgroundExecutor.execute(eventLoader);
      } else {
         //we have to load all of the data before we can continue if it isn't sorted
         eventLoader.run();
      }
   }

   @Override
   public void endOfFile() {
      this.finishedLoading = true;
   }
}
