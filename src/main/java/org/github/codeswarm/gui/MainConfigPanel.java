package org.github.codeswarm.gui;

import com.sun.javafx.collections.ObservableSequentialListWrapper;
import java.util.ArrayList;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.github.codeswarm.CodeSwarm;
import org.github.codeswarm.ColorTest;
import org.github.codeswarm.Config;

public class MainConfigPanel extends Application {

   public static void start() {
      launch(new String[]{});
   }

   private ChoiceBox<String> screenSize;

   private TextField framesPerDay;

   private CheckBox legend;

   private CheckBox history;

   private CheckBox userName;

   private CheckBox popular;
   
   private CheckBox date;

   private CheckBox edges;

   private ColorPicker background;

   private ComboBox<String> fontType;

   private ColorPicker fontColor;

   private TextField fontSize;

   private ObservableList<ColorAssignerProperties> colorList;

   @Override
   public void start(Stage primaryStage) throws Exception {
      //primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/1f4d1.png")));
      primaryStage.setTitle("Code Swarm Configuration");
      primaryStage.setScene(mainScene());
      primaryStage.setResizable(true);
      primaryStage.show();
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
      tabPane.getTabs().add(tabColor());
      tabPane.getTabs().add(tabFileTypes());

      return tabPane;
   }

   private Tab tabGeneral() {
      Tab tab = createTab("General settings");
      GridPane gridPane = (GridPane) tab.getContent();

      Label screenSizeLbl = new Label("Screen size");
      GridPane.setHalignment(screenSizeLbl, HPos.RIGHT);
      gridPane.add(screenSizeLbl, 0, 1);

      this.screenSize = new ChoiceBox<>(
          FXCollections.observableArrayList("800x600", "1024x768", "960x720", "1920x1080"));
      GridPane.setHalignment(screenSize, HPos.LEFT);
      gridPane.add(screenSize, 1, 1);
      String current = Config.getInstance().getWidth().getValue() + "x" + Config.getInstance().getHeight().getValue();
      screenSize.getSelectionModel().select(current);

      Label framesPerDayLbl = new Label("Frames per day");
      GridPane.setHalignment(framesPerDayLbl, HPos.RIGHT);
      gridPane.add(framesPerDayLbl, 0, 2);

      this.framesPerDay = new TextField();
      framesPerDay.setText("6");
      GridPane.setHalignment(framesPerDay, HPos.LEFT);
      gridPane.add(framesPerDay, 1, 2);

      this.legend = new CheckBox("Show legend");
      this.legend.selectedProperty().bindBidirectional(Config.getInstance().getShowLegend());
      GridPane.setHalignment(legend, HPos.LEFT);
      gridPane.add(legend, 1, 3);

      this.history = new CheckBox("Show history");
      this.history.selectedProperty().bindBidirectional(Config.getInstance().getShowHistogram());
      GridPane.setHalignment(history, HPos.LEFT);
      gridPane.add(history, 1, 4);

      this.userName = new CheckBox("Show username");
      this.userName.selectedProperty().bindBidirectional(Config.getInstance().getShowUsername());
      GridPane.setHalignment(userName, HPos.LEFT);
      gridPane.add(userName, 1, 5);

      this.popular = new CheckBox("Show popular");
      this.popular.selectedProperty().bindBidirectional(Config.getInstance().getShowPopular());
      GridPane.setHalignment(popular, HPos.LEFT);
      gridPane.add(popular, 1, 6);
      
      this.date = new CheckBox("Show date");
      this.date.selectedProperty().bindBidirectional(Config.getInstance().getShowDate());
      GridPane.setHalignment(date, HPos.LEFT);
      gridPane.add(date, 1, 7);

      this.edges = new CheckBox("Show edges");
      this.edges.selectedProperty().bindBidirectional(Config.getInstance().getShowEdges());
      GridPane.setHalignment(edges, HPos.LEFT);
      gridPane.add(edges, 1, 8);

      return tab;
   }

   private Tab tabColor() {
      Tab tab = createTab("Color settings");
      GridPane gridPane = (GridPane) tab.getContent();

      Label backgroundLbl = new Label("Background color");
      GridPane.setHalignment(backgroundLbl, HPos.RIGHT);
      gridPane.add(backgroundLbl, 0, 1);

      this.background = new ColorPicker();
      this.background.valueProperty().bindBidirectional(Config.getInstance().getBackground());
      GridPane.setHalignment(background, HPos.LEFT);
      gridPane.add(background, 1, 1);

      Label fontTypeLbl = new Label("Font");
      GridPane.setHalignment(fontTypeLbl, HPos.RIGHT);
      gridPane.add(fontTypeLbl, 0, 2);

      this.fontType = new ComboBox<>();
      ObservableList<String> list = new ObservableSequentialListWrapper<>(new ArrayList<>());
      list.add("Arial");
      list.add("Ubuntu");
      fontType.setItems(list);
      fontType.getSelectionModel().select(0);
      GridPane.setHalignment(fontType, HPos.LEFT);
      gridPane.add(fontType, 1, 2);

      Label fontColorLbl = new Label("Font color");
      GridPane.setHalignment(fontColorLbl, HPos.RIGHT);
      gridPane.add(fontColorLbl, 0, 3);

      this.fontColor = new ColorPicker();
      this.fontColor.valueProperty().bindBidirectional(Config.getInstance().getFontColor());
      GridPane.setHalignment(fontColor, HPos.LEFT);
      gridPane.add(fontColor, 1, 3);

      Label fontSizeLbl = new Label("Font size");
      GridPane.setHalignment(fontSizeLbl, HPos.RIGHT);
      gridPane.add(fontSizeLbl, 0, 4);

      this.fontSize = new TextField();
      fontSize.setText("10");
      GridPane.setHalignment(fontSize, HPos.LEFT);
      gridPane.add(fontSize, 1, 4);

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

      ColumnConstraints column1 = new ColumnConstraints(125);
      ColumnConstraints column2 = new ColumnConstraints(50, 75, 150);
      column2.setHgrow(Priority.ALWAYS);
      gridPane.getColumnConstraints().addAll(column1, column2);
      tab.setContent(gridPane);

      return tab;
   }

   private Tab tabFileTypes() {
      Tab tab = new Tab();
      tab.setClosable(false);
      tab.setText("Filetype settings");

      this.colorList = new ObservableSequentialListWrapper<>(new ArrayList<>());
      this.colorList.add(new ColorAssignerProperties(new ColorTest("Docs", ".*doc.*", java.awt.Color.RED)));
      this.colorList.add(new ColorAssignerProperties(new ColorTest("Java", ".*java.*", java.awt.Color.BLUE)));

      TableView<ColorAssignerProperties> tableView = new TableView<>();
      tableView.setItems(colorList);
      tableView.getSelectionModel().select(0);
      tableView.setEditable(true);
      tab.setContent(tableView);

      TableColumn<ColorAssignerProperties, String> patternCol = new TableColumn<>("Pattern");
      TableColumn<ColorAssignerProperties, String> labelCol = new TableColumn<>("Label");
      TableColumn<ColorAssignerProperties, Color> colorCol = new TableColumn<>("Color");

      tableView.getColumns().add(patternCol);
      tableView.getColumns().add(labelCol);
      tableView.getColumns().add(colorCol);

      patternCol.setCellValueFactory(c -> c.getValue().getExpression());
      labelCol.setCellValueFactory(c -> c.getValue().getLabel());
      colorCol.setCellValueFactory(c -> c.getValue().getColor());

      return tab;
   }

   private Node buttonBar() {
      HBox hBox = new HBox(5d);
      hBox.setPadding(new Insets(5d));
      hBox.setAlignment(Pos.CENTER_RIGHT);

      Button preview = new Button("Preview");
      preview.setOnAction(a -> {
         setConfig();
         CodeSwarm.boot();
      });
      hBox.getChildren().add(preview);
      hBox.getChildren().add(new Button("Start"));

      Button quit = new Button("Quit");
      quit.setOnAction(a -> {
         System.exit(0);
      });
      hBox.getChildren().add(quit);

      return hBox;
   }

   private void setConfig() {
      String screenSize = this.screenSize.getSelectionModel().getSelectedItem();
      Config.getInstance().setWidth(Integer.parseInt(screenSize.split("x")[0]));
      Config.getInstance().setHeight(Integer.parseInt(screenSize.split("x")[1]));
   }
}
