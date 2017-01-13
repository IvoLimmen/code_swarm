package org.github.gitswarm.gui;

import com.sun.javafx.collections.ObservableSequentialListWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import org.github.gitswarm.GitSwarm;
import org.github.gitswarm.ColorTest;
import org.github.gitswarm.Config;
import org.github.gitswarm.type.DisplayFile;

public class MainConfigPanel extends Application {

   public static void start() {
      launch(new String[]{});
   }

   private ChoiceBox<String> screenSizeCb;

   private TextField framesPerDayTf;

   private ColorPicker backgroundCp;

   private ComboBox<String> fontTypeCb;

   private ComboBox<String> boldFontTypeCb;

   private final ObservableList<ColorAssignerProperties> colorList = new ObservableSequentialListWrapper<>(new ArrayList<>());

   private final ObservableList<String> fontList = new ObservableSequentialListWrapper<>(new ArrayList<>());

   private EditDialog editDialog;

   private final static String[] FONT_DEFAULTS = new String[]{"SansSerif", "Arial"};

   @Override
   public void start(Stage primaryStage) throws Exception {

      initialize();

      this.editDialog = new EditDialog(primaryStage);
      //primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/1f4d1.png")));
      primaryStage.setTitle("Code Swarm Configuration");
      primaryStage.setScene(mainScene());
      primaryStage.setResizable(true);
      primaryStage.show();
   }

   private void initialize() {
      this.colorList.clear();
      Config.getInstance().getColorAssigner().getTests().forEach((ct) -> {
         this.colorList.add(new ColorAssignerProperties(ct));
      });

      this.fontList.addAll(Font.getFontNames());

      // make a logical default
      Arrays.asList(FONT_DEFAULTS).forEach((font) -> {
         if (this.fontList.contains(font)) {
            Config.getInstance().setFont(font);
            Config.getInstance().setBoldFont(font);
         }
      });
   }

   private Scene mainScene() {
      BorderPane borderPane = new BorderPane();
      borderPane.setCenter(tabbedPane());
      borderPane.setBottom(buttonBar());

      StackPane root = new StackPane();
      root.getChildren().add(borderPane);

      return new Scene(root, 500, 400);
   }

   private TabPane tabbedPane() {
      TabPane tabPane = new TabPane();
      tabPane.getTabs().add(tabGeneral());
      tabPane.getTabs().add(tabMovie());
      tabPane.getTabs().add(tabColor());
      tabPane.getTabs().add(tabFiles());
      tabPane.getTabs().add(tabPerson());
      tabPane.getTabs().add(tabEdge());
      tabPane.getTabs().add(tabFileTypes());

      return tabPane;
   }

   private Tab tabGeneral() {
      Tab tab = createTab("General");
      GridPane gridPane = (GridPane) tab.getContent();

      Label screenSizeLbl = new Label("Screen size");
      GridPane.setHalignment(screenSizeLbl, HPos.RIGHT);
      gridPane.add(screenSizeLbl, 0, 1);

      this.screenSizeCb = new ChoiceBox<>(
          FXCollections.observableArrayList("800x600", "960x720", "1024x768", "1920x1080"));
      GridPane.setHalignment(screenSizeCb, HPos.LEFT);
      gridPane.add(screenSizeCb, 1, 1);
      String current = Config.getInstance().getWidth().getValue() + "x" + Config.getInstance().getHeight().getValue();
      screenSizeCb.getSelectionModel().select(current);

      this.framesPerDayTf = new TextField();
      framesPerDayTf.setText("" + Config.getInstance().getFramesPerDay());
      GridPane.setHalignment(framesPerDayTf, HPos.LEFT);
      gridPane.add(framesPerDayTf, 1, 2);

      Label framesPerDayLbl = new Label("Frames per day");
      GridPane.setHalignment(framesPerDayLbl, HPos.RIGHT);
      gridPane.add(framesPerDayLbl, 0, 2);

      CheckBox legend = new CheckBox("Show legend");
      legend.selectedProperty().bindBidirectional(Config.getInstance().getShowLegend());
      GridPane.setHalignment(legend, HPos.LEFT);
      gridPane.add(legend, 1, 3);

      CheckBox histogram = new CheckBox("Show histogram");
      histogram.selectedProperty().bindBidirectional(Config.getInstance().getShowHistogram());
      GridPane.setHalignment(histogram, HPos.LEFT);
      gridPane.add(histogram, 1, 4);

      CheckBox userName = new CheckBox("Show username");
      userName.selectedProperty().bindBidirectional(Config.getInstance().getShowUsername());
      GridPane.setHalignment(userName, HPos.LEFT);
      gridPane.add(userName, 1, 5);

      CheckBox popular = new CheckBox("Show popular");
      popular.selectedProperty().bindBidirectional(Config.getInstance().getShowPopular());
      GridPane.setHalignment(popular, HPos.LEFT);
      gridPane.add(popular, 1, 6);

      CheckBox date = new CheckBox("Show date");
      date.selectedProperty().bindBidirectional(Config.getInstance().getShowDate());
      GridPane.setHalignment(date, HPos.LEFT);
      gridPane.add(date, 1, 7);

      CheckBox edges = new CheckBox("Show edges");
      edges.selectedProperty().bindBidirectional(Config.getInstance().getShowEdges());
      GridPane.setHalignment(edges, HPos.LEFT);
      gridPane.add(edges, 1, 8);

      return tab;
   }

   private Tab tabMovie() {
      Tab tab = createTab("Movie");
      GridPane gridPane = (GridPane) tab.getContent();

      CheckBox legend = new CheckBox("Save snapshots");
      legend.selectedProperty().bindBidirectional(Config.getInstance().getTakeSnapshots());
      GridPane.setHalignment(legend, HPos.LEFT);
      gridPane.add(legend, 1, 1);

      Label screenshotFileMaskLbl = new Label("Screenshot filemask");
      GridPane.setHalignment(screenshotFileMaskLbl, HPos.RIGHT);
      gridPane.add(screenshotFileMaskLbl, 0, 2);

      TextField screenshotFileMaskTf = new TextField();
      screenshotFileMaskTf.textProperty().bind(Config.getInstance().getScreenshotFileMask());
      GridPane.setHalignment(screenshotFileMaskTf, HPos.LEFT);
      gridPane.add(screenshotFileMaskTf, 1, 2);

      return tab;
   }

   private Tab tabFiles() {
      Tab tab = createTab("File");
      GridPane gridPane = (GridPane) tab.getContent();

      Label drawFileLbl = new Label("Draw file");
      GridPane.setHalignment(drawFileLbl, HPos.RIGHT);
      gridPane.add(drawFileLbl, 0, 1);

      ChoiceBox<DisplayFile> displayFile = new ChoiceBox<>();
      displayFile.setConverter(new StringConverter<DisplayFile>() {
         @Override
         public String toString(DisplayFile object) {
            return object.getLabel();
         }

         @Override
         public DisplayFile fromString(String string) {
            // not used
            return DisplayFile.FUZZY;
         }
      });
      displayFile.getItems().addAll(DisplayFile.values());
      displayFile.setOnAction((event) -> {
         Config.getInstance().setDisplayFile(displayFile.getValue());
      });
      displayFile.setValue(Config.getInstance().getDisplayFile());
      GridPane.setHalignment(displayFile, HPos.LEFT);
      gridPane.add(displayFile, 1, 1);

      Label fileMassLbl = new Label("Mass");
      GridPane.setHalignment(fileMassLbl, HPos.RIGHT);
      gridPane.add(fileMassLbl, 0, 2);
      
      IntegerField fileMassTf = new IntegerField();
      fileMassTf.textProperty().bindBidirectional(Config.getInstance().getFileMass(), new NumberStringConverter());
      GridPane.setHalignment(fileMassTf, HPos.LEFT);
      gridPane.add(fileMassTf, 1, 2);
      
      Label fileLifeLbl = new Label("Life");
      GridPane.setHalignment(fileLifeLbl, HPos.RIGHT);
      gridPane.add(fileLifeLbl, 0, 3);

      IntegerField fileLife = new IntegerField();
      fileLife.textProperty().bindBidirectional(Config.getInstance().getFileLife(), new NumberStringConverter());
      GridPane.setHalignment(fileLife, HPos.LEFT);
      gridPane.add(fileLife, 1, 3);
      
      Label fileDecLbl = new Label("Decrement");
      GridPane.setHalignment(fileDecLbl, HPos.RIGHT);
      gridPane.add(fileDecLbl, 0, 4);

      IntegerField fileDec = new IntegerField();
      fileDec.textProperty().bindBidirectional(Config.getInstance().getFileDecrement(), new NumberStringConverter());
      GridPane.setHalignment(fileDec, HPos.LEFT);
      gridPane.add(fileDec, 1, 4);

      Label fileHighlightLbl = new Label("Highlight");
      GridPane.setHalignment(fileHighlightLbl, HPos.RIGHT);
      gridPane.add(fileHighlightLbl, 0, 5);

      IntegerField fileHighlight = new IntegerField();
      fileHighlight.textProperty().bindBidirectional(Config.getInstance().getFileHighlight(), new NumberStringConverter());
      GridPane.setHalignment(fileHighlight, HPos.LEFT);
      gridPane.add(fileHighlight, 1, 5);
      
      return tab;
   }

   private Tab tabPerson() {
      Tab tab = createTab("Person");
      GridPane gridPane = (GridPane) tab.getContent();

      CheckBox drawHalo = new CheckBox("Draw halos");
      drawHalo.selectedProperty().bindBidirectional(Config.getInstance().getDrawNamesHalo());
      GridPane.setHalignment(drawHalo, HPos.LEFT);
      gridPane.add(drawHalo, 1, 0);

      CheckBox drawSharp = new CheckBox("Draw sharp");
      drawSharp.selectedProperty().bindBidirectional(Config.getInstance().getDrawNamesSharp());
      GridPane.setHalignment(drawSharp, HPos.LEFT);
      gridPane.add(drawSharp, 1, 1);

      Label personMassLbl = new Label("Mass");
      GridPane.setHalignment(personMassLbl, HPos.RIGHT);
      gridPane.add(personMassLbl, 0, 2);
      
      IntegerField personMassTf = new IntegerField();
      personMassTf.textProperty().bindBidirectional(Config.getInstance().getPersonMass(), new NumberStringConverter());
      GridPane.setHalignment(personMassTf, HPos.LEFT);
      gridPane.add(personMassTf, 1, 2);
      
      Label personLifeLbl = new Label("Life");
      GridPane.setHalignment(personLifeLbl, HPos.RIGHT);
      gridPane.add(personLifeLbl, 0, 3);

      IntegerField personLife = new IntegerField();
      personLife.textProperty().bindBidirectional(Config.getInstance().getPersonLife(), new NumberStringConverter());
      GridPane.setHalignment(personLife, HPos.LEFT);
      gridPane.add(personLife, 1, 3);
      
      Label personDecLbl = new Label("Decrement");
      GridPane.setHalignment(personDecLbl, HPos.RIGHT);
      gridPane.add(personDecLbl, 0, 4);

      IntegerField personDec = new IntegerField();
      personDec.textProperty().bindBidirectional(Config.getInstance().getPersonDescrement(), new NumberStringConverter());
      GridPane.setHalignment(personDec, HPos.LEFT);
      gridPane.add(personDec, 1, 4);

      Label personHighlightLbl = new Label("Highlight");
      GridPane.setHalignment(personHighlightLbl, HPos.RIGHT);
      gridPane.add(personHighlightLbl, 0, 5);

      IntegerField personHighlight = new IntegerField();
      personHighlight.textProperty().bindBidirectional(Config.getInstance().getPersonHighlight(), new NumberStringConverter());
      GridPane.setHalignment(personHighlight, HPos.LEFT);
      gridPane.add(personHighlight, 1, 5);
      
      return tab;
   }

   private Tab tabEdge() {
      Tab tab = createTab("Edge");
      GridPane gridPane = (GridPane) tab.getContent();
      
      Label edgeLifeLbl = new Label("Life");
      GridPane.setHalignment(edgeLifeLbl, HPos.RIGHT);
      gridPane.add(edgeLifeLbl, 0, 1);

      IntegerField edgeLife = new IntegerField();
      edgeLife.textProperty().bindBidirectional(Config.getInstance().getEdgeLife(), new NumberStringConverter());
      GridPane.setHalignment(edgeLife, HPos.LEFT);
      gridPane.add(edgeLife, 1, 1);
      
      Label edgeDecLbl = new Label("Decrement");
      GridPane.setHalignment(edgeDecLbl, HPos.RIGHT);
      gridPane.add(edgeDecLbl, 0, 2);

      IntegerField edgeDec = new IntegerField();
      edgeDec.textProperty().bindBidirectional(Config.getInstance().getEdgeDecrement(), new NumberStringConverter());
      GridPane.setHalignment(edgeDec, HPos.LEFT);
      gridPane.add(edgeDec, 1, 2);

      Label edgeHighlightLbl = new Label("Length");
      GridPane.setHalignment(edgeHighlightLbl, HPos.RIGHT);
      gridPane.add(edgeHighlightLbl, 0, 3);

      IntegerField edgeHighlight = new IntegerField();
      edgeHighlight.textProperty().bindBidirectional(Config.getInstance().getEdgeLength(), new NumberStringConverter());
      GridPane.setHalignment(edgeHighlight, HPos.LEFT);
      gridPane.add(edgeHighlight, 1, 3);
      
      return tab;
   }
   
   private Tab tabColor() {
      Tab tab = createTab("Color");
      GridPane gridPane = (GridPane) tab.getContent();

      Label backgroundLbl = new Label("Background color");
      GridPane.setHalignment(backgroundLbl, HPos.RIGHT);
      gridPane.add(backgroundLbl, 0, 1);

      this.backgroundCp = new ColorPicker();
      this.backgroundCp.valueProperty().bindBidirectional(Config.getInstance().getBackground());
      GridPane.setHalignment(backgroundCp, HPos.LEFT);
      gridPane.add(backgroundCp, 1, 1);

      Label fontTypeLbl = new Label("Font");
      GridPane.setHalignment(fontTypeLbl, HPos.RIGHT);
      gridPane.add(fontTypeLbl, 0, 2);

      this.fontTypeCb = new ComboBox<>();
      fontTypeCb.setItems(this.fontList);
      fontTypeCb.getSelectionModel().select(Config.getInstance().getFont());
      fontTypeCb.setOnAction((event) -> {
         Config.getInstance().setFont(fontTypeCb.getSelectionModel().getSelectedItem());
      });
      GridPane.setHalignment(fontTypeCb, HPos.LEFT);
      gridPane.add(fontTypeCb, 1, 2);

      Label fontColorLbl = new Label("Font color");
      GridPane.setHalignment(fontColorLbl, HPos.RIGHT);
      gridPane.add(fontColorLbl, 0, 3);

      ColorPicker fontColor = new ColorPicker();
      fontColor.valueProperty().bindBidirectional(Config.getInstance().getFontColor());
      GridPane.setHalignment(fontColor, HPos.LEFT);
      gridPane.add(fontColor, 1, 3);

      Label fontSizeLbl = new Label("Font size");
      GridPane.setHalignment(fontSizeLbl, HPos.RIGHT);
      gridPane.add(fontSizeLbl, 0, 4);

      IntegerField fontSize = new IntegerField();
      fontSize.textProperty().bindBidirectional(Config.getInstance().getFontSize(), new NumberStringConverter());
      GridPane.setHalignment(fontSize, HPos.LEFT);
      gridPane.add(fontSize, 1, 4);

      Label boldFontTypeLbl = new Label("Bold font");
      GridPane.setHalignment(boldFontTypeLbl, HPos.RIGHT);
      gridPane.add(boldFontTypeLbl, 0, 5);

      this.boldFontTypeCb = new ComboBox<>();
      boldFontTypeCb.setItems(this.fontList);
      boldFontTypeCb.getSelectionModel().select(Config.getInstance().getBoldFont());
      boldFontTypeCb.setOnAction((event) -> {
         Config.getInstance().setBoldFont(boldFontTypeCb.getSelectionModel().getSelectedItem());
      });
      GridPane.setHalignment(boldFontTypeCb, HPos.LEFT);
      gridPane.add(boldFontTypeCb, 1, 5);

      Label boldFontSizeLbl = new Label("Bold font size");
      GridPane.setHalignment(boldFontSizeLbl, HPos.RIGHT);
      gridPane.add(boldFontSizeLbl, 0, 6);

      IntegerField boldFontSize = new IntegerField();
      boldFontSize.textProperty().bindBidirectional(Config.getInstance().getBoldFontSize(), new NumberStringConverter());
      GridPane.setHalignment(boldFontSize, HPos.LEFT);
      gridPane.add(boldFontSize, 1, 6);

      return tab;
   }

   private Tab createTab(String title) {
      Tab tab = new Tab();
      tab.setClosable(false);
      tab.setText(title);

      GridPane gridPane = new GridPane();
      gridPane.setPadding(new Insets(5d));
      gridPane.setVgap(5d);
      gridPane.setHgap(5d);

      ColumnConstraints column1 = new ColumnConstraints(150);
      ColumnConstraints column2 = new ColumnConstraints(50, 75, 150);
      column2.setHgrow(Priority.ALWAYS);
      gridPane.getColumnConstraints().addAll(column1, column2);
      tab.setContent(gridPane);

      return tab;
   }

   private Tab tabFileTypes() {
      Tab tab = new Tab();
      tab.setClosable(false);
      tab.setText("Filetype");

      BorderPane borderPane = new BorderPane();
      borderPane.setPadding(new Insets(5d));
      tab.setContent(borderPane);

      VBox vBox = new VBox(5d);
      vBox.setPadding(new Insets(5d));
      vBox.setMinWidth(75d);

      TableView<ColorAssignerProperties> tableView = new TableView<>();

      Button addButton = new Button("Add");
      addButton.setOnAction((event) -> {
         ColorAssignerProperties cap = new ColorAssignerProperties(new ColorTest());
         this.editDialog.createDialog(cap).showAndWait();
         this.colorList.add(0, cap);
      });
      addButton.setMinWidth(75d);
      vBox.getChildren().add(addButton);

      Button editButton = new Button("Edit");
      editButton.setOnAction((event) -> {
         this.editDialog.createDialog(tableView.getSelectionModel().getSelectedItem()).showAndWait();
      });
      editButton.setMinWidth(75d);
      vBox.getChildren().add(editButton);

      Button removeButton = new Button("Remove");
      removeButton.setMinWidth(75d);
      vBox.getChildren().add(removeButton);

      borderPane.setRight(vBox);

      tableView.setItems(colorList);
      tableView.getSelectionModel().select(0);
      tableView.setEditable(true);
      borderPane.setCenter(tableView);

      TableColumn<ColorAssignerProperties, String> labelCol = new TableColumn<>("Label");
      TableColumn<ColorAssignerProperties, String> patternCol = new TableColumn<>("Pattern");
      TableColumn<ColorAssignerProperties, Color> colorCol = new TableColumn<>("Color");

      labelCol.setMinWidth(100d);
      patternCol.setMinWidth(100d);
      colorCol.setMinWidth(80d);

      tableView.getColumns().add(labelCol);
      tableView.getColumns().add(patternCol);
      tableView.getColumns().add(colorCol);
      tableView.setMaxWidth(290d);

      labelCol.setCellValueFactory(c -> c.getValue().getLabel());
      patternCol.setCellValueFactory(c -> c.getValue().getExpression());
      colorCol.setCellValueFactory(c -> c.getValue().getColor());
      colorCol.setCellFactory((param) -> {
         return new TableCell<ColorAssignerProperties, Color>() {
            @Override
            protected void updateItem(Color item, boolean empty) {
               super.updateItem(item, empty);

               if (item != null && !empty) {
                  setText(item.toString());
                  setTextFill(item);
                  setBackground(new Background(new BackgroundFill(backgroundCp.getValue(), CornerRadii.EMPTY, Insets.EMPTY)));
               }
            }
         };
      });

      return tab;
   }

   private Node buttonBar() {
      HBox hBox = new HBox(5d);
      hBox.setPadding(new Insets(5d));
      hBox.setAlignment(Pos.CENTER_RIGHT);

      Button start = new Button("Start");
      start.setOnAction(a -> {
         setConfig();
         GitSwarm.boot();
      });
      hBox.getChildren().add(start);

      Button quit = new Button("Quit");
      quit.setOnAction(a -> {
         System.exit(0);
      });
      hBox.getChildren().add(quit);

      return hBox;
   }

   private void setConfig() {
      Config.getInstance().getColorAssigner().getTests().clear();
      this.colorList.forEach((ct) -> {
         Config.getInstance().getColorAssigner().addRule(ct.getLabel().getValue(), ct.getExpression().getValue(), ColorUtil.toAwtColor(ct.getColor().getValue()));
      });

      Config.getInstance().setFramesPerDay(Integer.parseInt(framesPerDayTf.getText()));

      String screenSize = this.screenSizeCb.getSelectionModel().getSelectedItem();
      Config.getInstance().setWidth(Integer.parseInt(screenSize.split("x")[0]));
      Config.getInstance().setHeight(Integer.parseInt(screenSize.split("x")[1]));
   }
}
