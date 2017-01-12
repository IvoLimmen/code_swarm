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
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.github.gitswarm.type.DisplayFile;

/**
 * @author Michael Ogawa
 */
public final class Config {

   private final static Config CURRENT = new Config();

   /**
    * The input file
    */
   public static final String INPUT_FILE_KEY = "InputFile";
   public static final String COLOR_ASSIGN_KEY = "ColorAssign";
   /**
    * Path to sprite file for nodes
    */
   public static final String SPRITE_FILE_KEY = "ParticleSpriteFile";   
   /**
    * Boolean value, controls using the OpenGL library (experimental)
    */
   public static final String USE_OPEN_GL = "UseOpenGL";
   /**
    * Boolean value, controls showing debug info
    */
   public static final String SHOW_DEBUG = "ShowDebug";
   /**
    * Boolean value, controls drawing halos around names
    */
   public static final String IS_INPUT_SORTED_KEY = "IsInputSorted";
   public static final String DRAW_CIRCULAR_AVATARS = "CircularAvatars";

   public static Config getInstance() {
      return CURRENT;
   }

   public static void init(String configFileName) throws IOException {
      CURRENT.initPropStack();
      addPropertiesLayer(configFileName);
   }

   public static void init(Iterable<String> configFileNames) throws IOException {
      CURRENT.initPropStack();
      for (String filename : configFileNames) {
         if (new File(filename).exists()) {
            addPropertiesLayer(filename);
         }
      }
   }

   public static void addPropertiesLayer(Properties props) {
      CURRENT.propStack.add(0, props);
   }

   public static void addPropertiesLayer(String filename) throws IOException {
      Properties props = new Properties();
      props.load(new FileInputStream(filename));
      addPropertiesLayer(props);
   }

   /**
    *
    * @param key
    * @return Returns the first key found in the stack of config files.
    */
   public static String getStringProperty(String key) {
      for (Properties props : CURRENT.propStack) {
         if (props.containsKey(key)) {
            return props.getProperty(key);
         }
      }
      return null;
   }

   public static Color getColorProperty(String key) {
      return stringToColor(getStringProperty(key));
   }

   /**
    * Specify the path to the Xml-input file containing the repository entries.<br />
    * Further versions should not use input-file but an abstract view of the repository-entries.
    *
    * @see EventList
    * @param filePath the path to the Xml-input file.
    */
   public static void setInputFile(String filePath) {
      CURRENT.propStack.get(0).setProperty(INPUT_FILE_KEY, filePath);
   }

   public static boolean getBooleanProperty(String key) {
      return Boolean.valueOf(getStringProperty(key));
   }

   /**
    *
    * @param key
    * @return value of property if found, 0 if not found.
    */
   public static int getIntProperty(String key) {
      return Integer.parseInt(getStringProperty(key));
   }

   /**
    *
    * @param key
    * @param defValue
    * @return value of property if found.
    */
   public static int getPositiveIntProperty(String key) {
      int value = getIntProperty(key);
      if (value < 0) {
         throw new RuntimeException(key + " must be >0, found " + value);
      }
      return value;
   }

   /**
    *
    * @param key
    * @param defValue
    * @return defValue if not found or found value isn't negative, Value of property if found.
    */
   public static int getNegativeIntProperty(String key) {
      int value = getIntProperty(key);
      if (value > 0) {
         throw new RuntimeException(key + " must be >0, found " + value);
      }
      return value;
   }

   /**
    *
    * @param key
    * @return value of property if found, 0 if not found.
    */
   public static long getLongProperty(String key) {
      return Long.parseLong(getStringProperty(key));
   }

   /**
    *
    * @param key
    * @return value of property if found, 0 if not found.
    */
   public static float getFloatProperty(String key) {
      return Float.parseFloat(getStringProperty(key));
   }

   /**
    *
    * @param index
    * @return String containing the regex and rgb values used to colorcode nodes, null if not found
    */
   public static String getColorAssignProperty(Integer index) {
      return getStringProperty(COLOR_ASSIGN_KEY + index.toString());
   }

   /**
    *
    * @param str
    * @return Color object constructed from values in str
    */
   protected static Color stringToColor(String str) {
      // assume format is "R,G,B"
      String[] tokens = str.split(",");
      int[] values = new int[3];
      for (int i = 0; i < 3; i++) {
         values[i] = Integer.parseInt(tokens[i]);
      }
      return new Color(values[0], values[1], values[2]);
   }

   public static double getDoubleProperty(String key) {
      return Double.parseDouble(getStringProperty(key));
   }
   private String boldFont;
   private String font;
   private int framesPerDay = 6;
   private final ColorAssigner colorAssigner = new ColorAssigner();

   private DisplayFile displayFile = DisplayFile.FUZZY;

   private final SimpleStringProperty screenshotFileMask = new SimpleStringProperty("#####.png");
   private final SimpleBooleanProperty drawNamesHalo = new SimpleBooleanProperty(false);
   private final SimpleBooleanProperty drawNamesSharp = new SimpleBooleanProperty(true);
   private final SimpleIntegerProperty fontSize = new SimpleIntegerProperty(10);
   private final SimpleIntegerProperty boldFontSize = new SimpleIntegerProperty(12);
   private final SimpleIntegerProperty width = new SimpleIntegerProperty(800);
   private final SimpleIntegerProperty height = new SimpleIntegerProperty(600);
   private final SimpleBooleanProperty showLegend = new SimpleBooleanProperty(true);
   private final SimpleBooleanProperty showHistogram = new SimpleBooleanProperty(true);
   private final SimpleBooleanProperty showDate = new SimpleBooleanProperty(true);
   private final SimpleBooleanProperty showEdges = new SimpleBooleanProperty(false);
   private final SimpleBooleanProperty showPopular = new SimpleBooleanProperty(false);
   private final SimpleBooleanProperty showUsername = new SimpleBooleanProperty(true);
   private final SimpleBooleanProperty takeSnapshots = new SimpleBooleanProperty(false);
   private final SimpleObjectProperty<javafx.scene.paint.Color> background = new SimpleObjectProperty<>(javafx.scene.paint.Color.BLACK);
   private final SimpleObjectProperty<javafx.scene.paint.Color> fontColor = new SimpleObjectProperty<>(javafx.scene.paint.Color.WHITE);
   private final SimpleIntegerProperty personMass = new SimpleIntegerProperty(100);
   private final SimpleIntegerProperty personHighlight = new SimpleIntegerProperty(5);
   private final SimpleIntegerProperty personLife = new SimpleIntegerProperty(255);
   private final SimpleIntegerProperty personDescrement = new SimpleIntegerProperty(-1);
   private final SimpleIntegerProperty fileMass = new SimpleIntegerProperty(1);
   private final SimpleIntegerProperty fileHighlight = new SimpleIntegerProperty(5);
   private final SimpleIntegerProperty fileLife = new SimpleIntegerProperty(155);
   private final SimpleIntegerProperty fileDecrement = new SimpleIntegerProperty(-2);
   private final SimpleIntegerProperty edgeLife = new SimpleIntegerProperty(155);
   private final SimpleIntegerProperty edgeDecrement = new SimpleIntegerProperty(-2);
   private final SimpleIntegerProperty edgeLength = new SimpleIntegerProperty(25);

   private List<Properties> propStack;

   private Config() {
      this.colorAssigner.addRule("Misc", ".*", Color.GRAY);
   }

   public SimpleIntegerProperty getPersonDescrement() {
      return personDescrement;
   }

   public SimpleIntegerProperty getPersonLife() {
      return personLife;
   }

   public SimpleIntegerProperty getPersonHighlight() {
      return personHighlight;
   }

   public SimpleIntegerProperty getEdgeLife() {
      return edgeLife;
   }

   public SimpleIntegerProperty getEdgeLength() {
      return edgeLength;
   }

   public SimpleIntegerProperty getEdgeDecrement() {
      return edgeDecrement;
   }

   public SimpleIntegerProperty getFileLife() {
      return fileLife;
   }

   public SimpleIntegerProperty getFileDecrement() {
      return fileDecrement;
   }

   public SimpleIntegerProperty getFileHighlight() {
      return fileHighlight;
   }

   public SimpleIntegerProperty getFileMass() {
      return fileMass;
   }

   public SimpleIntegerProperty getPersonMass() {
      return personMass;
   }

   public SimpleStringProperty getScreenshotFileMask() {
      return screenshotFileMask;
   }

   public int getFramesPerDay() {
      return framesPerDay;
   }

   public void setFramesPerDay(int framesPerDay) {
      this.framesPerDay = framesPerDay;
   }

   public SimpleBooleanProperty getDrawNamesHalo() {
      return drawNamesHalo;
   }

   public SimpleBooleanProperty getDrawNamesSharp() {
      return drawNamesSharp;
   }

   public SimpleIntegerProperty getBoldFontSize() {
      return boldFontSize;
   }

   public SimpleIntegerProperty getFontSize() {
      return fontSize;
   }

   public String getBoldFont() {
      return boldFont;
   }

   public void setBoldFont(String boldFont) {
      this.boldFont = boldFont;
   }

   public String getFont() {
      return font;
   }

   public void setFont(String font) {
      this.font = font;
   }

   public ColorAssigner getColorAssigner() {
      return colorAssigner;
   }

   public DisplayFile getDisplayFile() {
      return displayFile;
   }

   public void setDisplayFile(DisplayFile displayFile) {
      this.displayFile = displayFile;
   }

   public SimpleBooleanProperty getShowUsername() {
      return showUsername;
   }

   public SimpleObjectProperty<javafx.scene.paint.Color> getBackground() {
      return background;
   }

   public SimpleObjectProperty<javafx.scene.paint.Color> getFontColor() {
      return fontColor;
   }

   public SimpleBooleanProperty getShowDate() {
      return showDate;
   }

   public SimpleBooleanProperty getShowEdges() {
      return showEdges;
   }

   public SimpleBooleanProperty getShowHistogram() {
      return showHistogram;
   }

   public SimpleBooleanProperty getShowLegend() {
      return showLegend;
   }

   public SimpleBooleanProperty getShowPopular() {
      return showPopular;
   }

   public SimpleIntegerProperty getWidth() {
      return width;
   }

   public SimpleIntegerProperty getHeight() {
      return height;
   }

   public SimpleBooleanProperty getTakeSnapshots() {
      return takeSnapshots;
   }

   public void setHeight(int height) {
      this.height.set(height);
   }

   public void setWidth(int width) {
      this.width.set(width);
   }

   protected void initPropStack() {
      CURRENT.propStack = new LinkedList<>();
   }
}
