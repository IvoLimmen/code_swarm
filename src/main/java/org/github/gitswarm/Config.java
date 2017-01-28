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

   public static Config getInstance() {
      return CURRENT;
   }
   
   private int allowedEmptyFrames = 50;
   private final SimpleObjectProperty<javafx.scene.paint.Color> background = new SimpleObjectProperty<>(javafx.scene.paint.Color.BLACK);
   private String boldFont;
   private final SimpleIntegerProperty boldFontSize = new SimpleIntegerProperty(12);
   private final ColorAssigner colorAssigner = new ColorAssigner();
   private DisplayFile displayFile = DisplayFile.FUZZY;
   private final SimpleBooleanProperty drawNamesHalo = new SimpleBooleanProperty(false);
   private final SimpleBooleanProperty drawNamesSharp = new SimpleBooleanProperty(true);
   private final SimpleIntegerProperty edgeDecrement = new SimpleIntegerProperty(-2);
   private final SimpleIntegerProperty edgeLength = new SimpleIntegerProperty(25);
   private final SimpleIntegerProperty edgeLife = new SimpleIntegerProperty(155);
   private final SimpleIntegerProperty fileDecrement = new SimpleIntegerProperty(-2);
   private final SimpleIntegerProperty fileHighlight = new SimpleIntegerProperty(5);
   private final SimpleIntegerProperty fileLife = new SimpleIntegerProperty(155);
   private final SimpleIntegerProperty fileMass = new SimpleIntegerProperty(1);
   private String font;
   private final SimpleObjectProperty<javafx.scene.paint.Color> fontColor = new SimpleObjectProperty<>(javafx.scene.paint.Color.WHITE);
   private final SimpleIntegerProperty fontSize = new SimpleIntegerProperty(10);
   private int framesPerDay = 6;
   private String gitDirectory;
   private final SimpleIntegerProperty height = new SimpleIntegerProperty(600);
   private final SimpleIntegerProperty personDescrement = new SimpleIntegerProperty(-1);
   private final SimpleIntegerProperty personHighlight = new SimpleIntegerProperty(5);
   private final SimpleIntegerProperty personLife = new SimpleIntegerProperty(255);
   private final SimpleIntegerProperty personMass = new SimpleIntegerProperty(100);
   private final SimpleStringProperty screenshotFileMask = new SimpleStringProperty("#####.png");
   private final SimpleBooleanProperty showDate = new SimpleBooleanProperty(true);
   private final SimpleBooleanProperty showEdges = new SimpleBooleanProperty(false);
   private final SimpleBooleanProperty showHistogram = new SimpleBooleanProperty(true);
   private final SimpleBooleanProperty showLegend = new SimpleBooleanProperty(true);
   private final SimpleBooleanProperty showPopular = new SimpleBooleanProperty(false);
   private final SimpleBooleanProperty showUsername = new SimpleBooleanProperty(true);
   private final SimpleBooleanProperty takeSnapshots = new SimpleBooleanProperty(false);
   private final SimpleIntegerProperty width = new SimpleIntegerProperty(800);

   private Config() {
      this.colorAssigner.addRule("Misc", ".*", Color.GRAY);
   }

   public int getAllowedEmptyFrames() {
      return allowedEmptyFrames;
   }

   public SimpleObjectProperty<javafx.scene.paint.Color> getBackground() {
      return background;
   }

   public String getBoldFont() {
      return boldFont;
   }

   public SimpleIntegerProperty getBoldFontSize() {
      return boldFontSize;
   }

   public ColorAssigner getColorAssigner() {
      return colorAssigner;
   }

   public DisplayFile getDisplayFile() {
      return displayFile;
   }

   public SimpleBooleanProperty getDrawNamesHalo() {
      return drawNamesHalo;
   }

   public SimpleBooleanProperty getDrawNamesSharp() {
      return drawNamesSharp;
   }

   public SimpleIntegerProperty getEdgeDecrement() {
      return edgeDecrement;
   }

   public SimpleIntegerProperty getEdgeLength() {
      return edgeLength;
   }

   public SimpleIntegerProperty getEdgeLife() {
      return edgeLife;
   }

   public SimpleIntegerProperty getFileDecrement() {
      return fileDecrement;
   }

   public SimpleIntegerProperty getFileHighlight() {
      return fileHighlight;
   }

   public SimpleIntegerProperty getFileLife() {
      return fileLife;
   }

   public SimpleIntegerProperty getFileMass() {
      return fileMass;
   }

   public String getFont() {
      return font;
   }

   public SimpleObjectProperty<javafx.scene.paint.Color> getFontColor() {
      return fontColor;
   }

   public SimpleIntegerProperty getFontSize() {
      return fontSize;
   }

   public int getFramesPerDay() {
      return framesPerDay;
   }

   public String getGitDirectory() {
      return gitDirectory;
   }

   public SimpleIntegerProperty getHeight() {
      return height;
   }

   public SimpleIntegerProperty getPersonDescrement() {
      return personDescrement;
   }

   public SimpleIntegerProperty getPersonHighlight() {
      return personHighlight;
   }

   public SimpleIntegerProperty getPersonLife() {
      return personLife;
   }

   public SimpleIntegerProperty getPersonMass() {
      return personMass;
   }

   public SimpleStringProperty getScreenshotFileMask() {
      return screenshotFileMask;
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

   public SimpleBooleanProperty getShowUsername() {
      return showUsername;
   }

   public SimpleBooleanProperty getTakeSnapshots() {
      return takeSnapshots;
   }

   public SimpleIntegerProperty getWidth() {
      return width;
   }

   public void setAllowedEmptyFrames(int allowedEmptyFrames) {
      this.allowedEmptyFrames = allowedEmptyFrames;
   }

   public void setBoldFont(String boldFont) {
      this.boldFont = boldFont;
   }

   public void setDisplayFile(DisplayFile displayFile) {
      this.displayFile = displayFile;
   }

   public void setFont(String font) {
      this.font = font;
   }

   public void setFramesPerDay(int framesPerDay) {
      this.framesPerDay = framesPerDay;
   }

   public void setGitDirectory(String gitDirectory) {
      this.gitDirectory = gitDirectory;
   }

   public void setHeight(int height) {
      this.height.set(height);
   }

   public void setWidth(int width) {
      this.width.set(width);
   }
}
