package edu.wpi.teamp.Controllers;

import algorithm.Direction;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.teamp.UIController;
import hospital.exceptions.NullHospitalException;
import hospital.route.AbstractNode;
import hospital.route.NodeType;
import hospital.route.RouteController;
import java.io.IOException;
import java.util.*;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.util.Duration;

public class NavigationController {

  private UIController uController = UIController.getUIController();
  private RouteController rController = uController.getRouteController();
  @FXML private Pane map;
  @FXML private JFXButton backBtn;
  @FXML private JFXButton bathBtn;
  @FXML private JFXButton exitBtn;
  @FXML private JFXButton info;
  @FXML private JFXButton btnStairs;
  @FXML private JFXButton btnElevator;
  @FXML private JFXButton emergency;

  @FXML private JFXButton faulknerBtn;
  @FXML private JFXButton mainCampusBtn;
  @FXML private JFXButton amenitiesBtn;
  @FXML private JFXButton conferenceBtn;
  @FXML private JFXButton departmentsBtn;
  @FXML private JFXButton labsBtn;
  @FXML private JFXButton servicesBtn;
  @FXML private JFXButton staffBtn;
  @FXML private JFXButton directoryBtn;
  @FXML private JFXButton clearStartBtn;
  @FXML private JFXButton clearEndBtn;
  @FXML private ImageView backArrow;
  @FXML private JFXButton floor1;
  @FXML private JFXButton floor2;
  @FXML private JFXButton floor3;
  @FXML private JFXButton floor4;
  @FXML private JFXButton floor5;
  @FXML private JFXButton loneBtn;
  @FXML private JFXButton ltwoBtn;
  @FXML private JFXButton groundBtn;
  @FXML private JFXButton floorOneBtn;
  @FXML private JFXButton floorTwoBtn;
  @FXML private JFXButton floorThreeBtn;
  @FXML private JFXButton switchBtn;
  @FXML private VBox directoriesPane;
  @FXML private VBox hospitalsPane;
  @FXML private VBox faulknerFloors;
  @FXML private VBox mainFloors;
  @FXML private ScrollPane directionBox;

  @FXML private JFXTextField startSearch;
  @FXML public JFXTextField endSearch;
  @FXML private Label floorLabel;

  private Accordion directionAcc = new Accordion();
  @FXML private JFXListView<AbstractNode> nodesListView;
  @FXML private WebView webView;
  private RouteType routeType = RouteType.None;

  ArrayList<AbstractNode> nodesList = rController.getNodesList();
  HashMap<NodeType, ArrayList<AbstractNode>> nodeTypeMap = rController.getNodesListByType();
  ArrayList<NodeType> disabledTypes = new ArrayList<NodeType>();
  AbstractNode homeNode = rController.getHomeNode();
  AbstractNode startNode = homeNode;
  AbstractNode endNode;
  private JFXTextField activeSearch;
  int floor = 1;
  String directoryButtonLabel; // used to hold directory button label during direction display
  String hospitalLocation = uController.getHospital().getName();
  String hold = "";

  @FXML TableView<ImageColumns> tableView;
  TableColumn dirCol;
  TableColumn pictureCol;
  private ObservableList<ImageColumns> data = FXCollections.observableArrayList();

  @FXML
  public void initialize() {
    // directionAcc.setOnMouseClicked(e -> accMouseclicked(e));
    hospitalsPane.setVisible(true);
    directoriesPane.setVisible(true);
    mainFloors.setVisible(false);
    faulknerFloors.setVisible(true);
    directionAcc.setVisible(false);
    directionAcc.setDisable(true);

    directionBox.setContent(directionAcc);
    directionBox.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    hold = "Main";
    if (!(uController.getHomeSearchQuery() == "")) {
      endSearch.setText(uController.getHomeSearchQuery());
      nodesListView.setVisible(true);
      sortNodes(nodesListView.getItems());
      transitionDirectoriesPane(false);
      transitionHospitalPane(false);
      updateDirectoryButton("Directory", false);

      if (nodesListView.getItems().isEmpty()) {
        nodesListView.getItems().clear();
        nodesListView.getItems().addAll(nodesList);
        nodesListView
            .getItems()
            .removeIf(
                x -> !x.getLongName().toLowerCase().contains(endSearch.getText().toLowerCase()));
        sortNodes(nodesListView.getItems());
      } else {
        nodesListView
            .getItems()
            .removeIf(
                x -> !x.getLongName().toLowerCase().contains(endSearch.getText().toLowerCase()));
        sortNodes(nodesListView.getItems());
      }
    }
    tableView
        .widthProperty()
        .addListener(
            new ChangeListener<Number>() {
              @Override
              public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                // Get the table header
                Pane header = (Pane) tableView.lookup("TableHeaderRow");
                if (header != null && header.isVisible()) {
                  header.setMaxHeight(1);
                  header.setMinHeight(0);
                  header.setPrefHeight(1);
                  header.setVisible(false);
                  header.setManaged(false);
                }
              }
            });
    // tableView.setStyle("-fx-font-size: 15;");

    uController.generateMaps(false, this);
    uController.addMap(map, floor, uController.getHospital().getName());
    floorLabel.setText("Floor " + floor);
    uController.moveMapCenter(
        (uController.getPrimaryScreenBounds().getWidth() - 454) / 2,
        (uController.getPrimaryScreenBounds().getHeight() - 90) / 2);
    sortNodes(nodesList);
    for (NodeType type : nodeTypeMap.keySet()) {
      sortNodes(nodeTypeMap.get(type));
    }
    nodesListView.setOnMouseClicked(e -> clickNodeHandle(e));
    //    directionsListView.setOnMouseClicked(e -> clickDirectionHandle(e));
    updateDirectoryButton("Main", true);
    startSearch.setText(homeNode.getLongName());
    final BooleanProperty firstTime = new SimpleBooleanProperty(true);
    startSearch
        .focusedProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue && firstTime.get()) {
                floorLabel.requestFocus();
                firstTime.setValue(false);
              }
            });

    startSearch.setOnKeyPressed(e -> searchHandle(e, startSearch));
    endSearch.setOnKeyPressed(e -> searchHandle(e, endSearch));
    startSearch
        .focusedProperty()
        .addListener(
            (o, oldValue, newValue) -> {
              if (newValue) activeSearch = startSearch;
            });
    endSearch
        .focusedProperty()
        .addListener(
            (o, oldValue, newValue) -> {
              if (newValue) activeSearch = endSearch;
            });
    initTable();

    // DO NOT TOUCH NEEDED FOR GOOGLE MAP
    webView
        .sceneProperty()
        .addListener(
            new ChangeListener<Scene>() {

              @Override
              public void changed(
                  ObservableValue<? extends Scene> observable, Scene oldValue, Scene scene) {
                if (scene != null) {
                  webView.setMaxSize(webView.getScene().getWidth(), webView.getScene().getHeight());
                  webView.maxWidthProperty().bind(webView.getScene().widthProperty());
                  webView.maxHeightProperty().bind(webView.getScene().heightProperty());
                } else {
                  webView.maxWidthProperty().unbind();
                  webView.maxHeightProperty().unbind();
                }
              }
            });

    if (hospitalLocation.equals("Faulkner")) {
      faulknerFloors.setVisible(true);
    } else if (hospitalLocation.equals("Main")) {
      mainFloors.setVisible(true);
    }

    if (!uController.getHomeSearchQuery().equals("")) {
      endNode = nodesListView.getItems().get(0);
      getRoute();
      sortNodes(nodesListView.getItems());
      transitionDirectoriesPane(false);
      transitionHospitalPane(false);
      updateDirectoryButton("Directory", false);
    }
  }

  @FXML
  public void btnClicked(ActionEvent e) throws IOException, NullHospitalException {
    if (e.getSource() == backBtn) {
      uController.setHomeSearchQuery("");
      uController.home();
    } else if (e.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");
    } else if (e.getSource() == info) {
      uController.setScene("AboutPage02.fxml");
    } else if (e.getSource() == bathBtn) {
      uController.setHomeSearchQuery("");
      directionAcc.getPanes().clear();
      transitionHospitalPane(false);
      transitionDirectoriesPane(false);
      updateDirectoryButton("Back", false);
      hold = directoryButtonLabel;
      createDirections(rController.getRoute(homeNode, NodeType.REST, disabledTypes));
      map.setVisible(true);
      webView.setVisible(false);
    } else if (e.getSource() == exitBtn) {
      uController.setHomeSearchQuery("");
      directionAcc.getPanes().clear();
      updateDirectoryButton("Back", false);
      hold = directoryButtonLabel;
      transitionHospitalPane(false);
      transitionDirectoriesPane(false);
      createDirections(rController.getRoute(homeNode, NodeType.EXIT, disabledTypes));
      map.setVisible(true);
      webView.setVisible(false);
    } else if (e.getSource() == clearStartBtn) {
      uController.setHomeSearchQuery("");
      directionAcc.getPanes().clear();
      startNode = null;
      startSearch.clear();
      rController.clearPaths();
      nodesListView.setVisible(true);
      updateDirectoryButton(hold, false);
    } else if (e.getSource() == clearEndBtn) {
      uController.setHomeSearchQuery("");
      directionAcc.getPanes().clear();
      endNode = null;
      endSearch.clear();
      rController.clearPaths();
      nodesListView.setVisible(true);
      updateDirectoryButton(hold, false);
    } else if (e.getSource() == floor1) {

      uController.addMap(map, 1, hospitalLocation);
      floorLabel.setText("Floor 1");
    } else if (e.getSource() == floor2) {
      uController.addMap(map, 2, hospitalLocation);
      floorLabel.setText("Floor 2");
    } else if (e.getSource() == floor3) {
      uController.addMap(map, 3, hospitalLocation);
      floorLabel.setText("Floor 3");
    } else if (e.getSource() == floor4) {
      uController.addMap(map, 4, hospitalLocation);
      floorLabel.setText("Floor 4");
    } else if (e.getSource() == floor5) {
      uController.addMap(map, 5, hospitalLocation);
      floorLabel.setText("Floor 5");
    } else if (e.getSource() == loneBtn) {
      uController.addMap(map, -1, hospitalLocation);
      floorLabel.setText("Floor L1");
    } else if (e.getSource() == ltwoBtn) {
      uController.addMap(map, -2, hospitalLocation);
      floorLabel.setText("Floor L2");
    } else if (e.getSource() == groundBtn) {
      uController.addMap(map, 0, hospitalLocation);
      floorLabel.setText("Ground Floor");
    } else if (e.getSource() == floorOneBtn) {
      uController.addMap(map, 1, hospitalLocation);
      floorLabel.setText("Floor 1");
    } else if (e.getSource() == floorTwoBtn) {
      uController.addMap(map, 2, hospitalLocation);
      floorLabel.setText("Floor 2");
    } else if (e.getSource() == floorThreeBtn) {
      uController.addMap(map, 3, hospitalLocation);
      floorLabel.setText("Floor 3");
    } else if (e.getSource() == switchBtn) {
      String holdName = endSearch.getText();
      endSearch.setText(startSearch.getText());
      startSearch.setText(holdName);

      AbstractNode holdNode = endNode;
      endNode = startNode;
      startNode = holdNode;
      getRoute();
    } else if (e.getSource() == btnElevator) {
      directionAcc.getPanes().clear();
      if (disabledTypes.contains(NodeType.ELEV)) {
        disabledTypes.remove(NodeType.ELEV);
        btnElevator.getGraphic().setOpacity(1);
      } else {
        disabledTypes.remove(NodeType.STAI);
        btnStairs.getGraphic().setOpacity(1);
        disabledTypes.add(NodeType.ELEV);
        btnElevator.getGraphic().setOpacity(.5);
      }
    } else if (e.getSource() == btnStairs) {
      directionAcc.getPanes().clear();
      if (disabledTypes.contains(NodeType.STAI)) {
        disabledTypes.remove(NodeType.STAI);
        btnStairs.getGraphic().setOpacity(1);
      } else {
        disabledTypes.remove(NodeType.ELEV);
        btnElevator.getGraphic().setOpacity(1);
        disabledTypes.add(NodeType.STAI);
        btnStairs.getGraphic().setOpacity(.5);
      }
    } else if (e.getSource() == faulknerBtn) {
      // uController.setHospital(hController.getHospital("Faulkner"));
      hospitalLocation = "Faulkner";
      if (rController.getHospitalName().equals("Faulkner")) {
        nodeTypeMap = rController.getNodesListByType();
      } else {
        nodeTypeMap = uController.getOtherHospital().getRouteController().getNodesListByType();
      }
      transitionHospitalPane(false);
      transitionDirectoriesPane(true);
      faulknerFloors.setVisible(true);
      mainFloors.setVisible(false);
      updateDirectoryButton("Directory", false);
    } else if (e.getSource() == mainCampusBtn) {
      // uController.setHospital(hController.getHospital("Main"));
      hospitalLocation = "Main";
      if (rController.getHospitalName().equals("Main")) {
        nodeTypeMap = rController.getNodesListByType();
      } else {
        nodeTypeMap = uController.getOtherHospital().getRouteController().getNodesListByType();
      }
      transitionHospitalPane(false);
      transitionDirectoriesPane(true);
      faulknerFloors.setVisible(false);
      mainFloors.setVisible(true);
      updateDirectoryButton("Directory", false);
    } else if (e.getSource() == amenitiesBtn) {
      nodesListView.getItems().addAll(nodeTypeMap.get(NodeType.REST));
      nodesListView.getItems().addAll(nodeTypeMap.get(NodeType.FOOD));
      sortNodes(nodesListView.getItems());
      transitionDirectoriesPane(false);
      updateDirectoryButton("Amenities", false);
      hold = "Amenities";
    } else if (e.getSource() == conferenceBtn) {
      nodesListView.getItems().addAll(nodeTypeMap.get(NodeType.CONF));
      sortNodes(nodesListView.getItems());
      transitionDirectoriesPane(false);
      updateDirectoryButton("Conference Rooms", false);
      hold = "Conference Rooms";
    } else if (e.getSource() == departmentsBtn) {
      nodesListView.getItems().addAll(nodeTypeMap.get(NodeType.DEPT));
      sortNodes(nodesListView.getItems());
      transitionDirectoriesPane(false);
      updateDirectoryButton("Departments", false);
      hold = "Departments";
    } else if (e.getSource() == labsBtn) {
      nodesListView.getItems().addAll(nodeTypeMap.get(NodeType.LAB));
      sortNodes(nodesListView.getItems());
      transitionDirectoriesPane(false);
      updateDirectoryButton("Labs", false);
    } else if (e.getSource() == servicesBtn) {
      nodesListView.getItems().addAll(nodeTypeMap.get(NodeType.SERV));
      nodesListView.getItems().addAll(nodeTypeMap.get(NodeType.RETL));
      // nodesListView.getItems().addAll(nodeTypeMap.get(NodeType.INFO))
      sortNodes(nodesListView.getItems());
      transitionDirectoriesPane(false);
      updateDirectoryButton("Services", false);
      hold = "Services";
    } else if (e.getSource() == staffBtn) {
      nodesListView.getItems().addAll(nodeTypeMap.get(NodeType.STAF));
      sortNodes(nodesListView.getItems());
      transitionDirectoriesPane(false);
      updateDirectoryButton("Staff Only", false);
      hold = "Staff Only";
    } else if (e.getSource() == directoryBtn) {
      if (directoryButtonLabel == null) {
        transitionHospitalPane(true);
        directionAcc.setVisible(false);
        directionAcc.setDisable(true);
        nodesListView.setVisible(true);
        updateDirectoryButton("Main", true);
      } else if (directoryButtonLabel == "Directory") {
        transitionDirectoriesPane(true);
        directionAcc.setVisible(false);
        directionAcc.setDisable(true);
        nodesListView.setVisible(true);
        updateDirectoryButton("Directory", false);
      } else {
        directionAcc.setVisible(false);
        directionAcc.setDisable(true);
        directionAcc.getPanes().clear();
        nodesListView.setVisible(true);
        updateDirectoryButton(hold, false);
      }
    } else {
      webView.setVisible(false);
      map.setVisible(true);
      directionAcc.setVisible(false);
      directionAcc.setDisable(true);
      nodesListView.setVisible(true);
      updateDirectoryButton(directoryButtonLabel, false);
    }
  }

  private void updateDirectoryButton(String text, boolean disable) {
    String hold = directoryButtonLabel;
    directoryBtn.setText(text);
    directoryBtn.setMouseTransparent(disable);
    backArrow.setVisible(!disable);
    backArrow.setFitWidth(disable ? 1 : 35);
    if (text == "Main") directoryButtonLabel = null;
    if (text == "Directory") directoryButtonLabel = null;
    if (text == "Amenities") directoryButtonLabel = "Directory";
    if (text == "Conference Rooms") directoryButtonLabel = "Directory";
    if (text == "Departments") directoryButtonLabel = "Directory";
    if (text == "Labs") directoryButtonLabel = "Directory";
    if (text == "Services") directoryButtonLabel = "Directory";
    if (text == "Staff Only") directoryButtonLabel = "Directory";
    if (text == "Back") directoryButtonLabel = hold;
  }

  public void setLabelText(String text) {
    endSearch.setText(text);
  }

  private void sortNodes(List<AbstractNode> nodes) {
    nodes.sort(
        new Comparator<AbstractNode>() {
          @Override
          public int compare(AbstractNode o1, AbstractNode o2) {
            return o1.getLongName().compareTo(o2.getLongName());
          }
        });
  }

  private void transitionDirectoriesPane(boolean show) {
    KeyValue keyValue =
        new KeyValue(
            directoriesPane.translateXProperty(),
            show ? 0 : -directoriesPane.getPrefWidth(),
            show ? Interpolator.EASE_IN : Interpolator.EASE_OUT);
    var timeline = new Timeline(new KeyFrame(Duration.millis(300), keyValue));
    if (show) timeline.setOnFinished((e) -> nodesListView.getItems().clear());
    timeline.play();
  }

  private void transitionHospitalPane(boolean show) {
    KeyValue keyValue =
        new KeyValue(
            hospitalsPane.translateXProperty(),
            show ? 0 : -hospitalsPane.getPrefWidth(),
            show ? Interpolator.EASE_IN : Interpolator.EASE_OUT);
    var timeline = new Timeline(new KeyFrame(Duration.millis(300), keyValue));
    timeline.play();
  }

  @FXML
  public void clickNodeHandle(MouseEvent e) {
    if (activeSearch == startSearch || startNode == null) {
      startNode = nodesListView.getSelectionModel().getSelectedItem();
      startSearch.setText(startNode.getLongName());
    } else {
      endNode = nodesListView.getSelectionModel().getSelectedItem();
      endSearch.setText(endNode.getLongName());
    }
    if (startNode != null && endNode != null) {
      // nodesListView.setVisible(false);
      getRoute();
      updateDirectoryButton("Back", false);
    }
  }

  //  @FXML
  //  public void clickDirectionHandle(MouseEvent e) {
  //    if (directionsListView.getSelectionModel().getSelectedIndex() < floorChangeIndex) {
  //      floor = startNode.getFloor();
  //    } else {
  //      floor = endNode.getFloor();
  //    }
  //    floorLabel.setText("Floor " + floor);
  //    uController.addMap(map, floor);
  //  }

  @FXML
  public void searchHandle(KeyEvent e, JFXTextField search) {
    activeSearch = search;

    final String word;
    if (e.getText().isEmpty() && !search.getText().isEmpty()) word = search.getText();
    else word = search.getText() + e.getText();

    nodesListView.setVisible(true);
    sortNodes(nodesListView.getItems());
    transitionDirectoriesPane(false);
    transitionHospitalPane(false);
    updateDirectoryButton("Directory", false);

    if (nodesListView.getItems().isEmpty()) {
      nodesListView.getItems().clear();
      nodesList.clear();
      nodesList.addAll(uController.getHospital().getRouteController().getNodesList());
      nodesList.addAll(uController.getOtherHospital().getRouteController().getNodesList());
      nodesListView.getItems().addAll(nodesList);
      nodesListView
          .getItems()
          .removeIf(x -> !x.getLongName().toLowerCase().contains(word.toLowerCase()));
      sortNodes(nodesListView.getItems());
    } else {
      nodesListView
          .getItems()
          .removeIf(x -> !x.getLongName().toLowerCase().contains(word.toLowerCase()));
      sortNodes(nodesListView.getItems());
    }
  }

  private void getRoute() {
    directionAcc.getPanes().clear();
    uController.addMap(map, startNode.getFloor(), startNode.getHospitalName());
    ArrayList<Direction> directions = new ArrayList<>();
    RouteController otherRouteController = uController.getOtherHospital().getRouteController();
    List<Direction> path1 = new ArrayList<>();
    List<Direction> path2 = new ArrayList<>();
    if (startNode.getHospitalName().equals("Faulkner")
        && endNode.getHospitalName().equals("Main")) {
      if (rController.getHospitalName().equals("Faulkner")) {
        if ((path1 = rController.getRoute(startNode, NodeType.EXIT, disabledTypes)) == null
            || (path2 =
                    otherRouteController.getRoute(
                        otherRouteController.getNode("FEXIT00201"), endNode, disabledTypes))
                == null) {
          directions = null;
        } else {
          directions.addAll(path1);
          directions.addAll(setUpToMain());
          directions.addAll(path2);
        }
      } else {
        if ((path1 = otherRouteController.getRoute(startNode, NodeType.EXIT, disabledTypes)) == null
            || (path2 =
                    rController.getRoute(rController.getNode("FEXIT00201"), endNode, disabledTypes))
                == null) {
          directions = null;
        } else {
          directions.addAll(path1);
          directions.addAll(setUpToMain());
          directions.addAll(path2);
        }
      }
      routeType = RouteType.ToMain;
    } else if (startNode.getHospitalName().equals("Main")
        && endNode.getHospitalName().equals("Faulkner")) {
      if (rController.getHospitalName().equals("Faulkner")) {
        directions.addAll(
            otherRouteController.getRoute(
                startNode, otherRouteController.getNode("FEXIT00201"), disabledTypes));
        directions.addAll(setUpToFaulkner());
        directions.addAll(
            rController.getRoute(rController.getNode("PEXIT00101"), endNode, disabledTypes));
      } else {
        directions.addAll(
            rController.getRoute(startNode, rController.getNode("FEXIT00201"), disabledTypes));
        directions.addAll(setUpToFaulkner());
        directions.addAll(
            otherRouteController.getRoute(
                otherRouteController.getNode("PEXIT00101"), endNode, disabledTypes));
      }
      routeType = RouteType.ToFaulkner;
    } else {
      if (startNode.getHospitalName().equals(rController.getHospitalName())) {
        if (rController.getRoute(startNode, endNode, disabledTypes) == null) {
          directions = null;
        } else {
          directions.addAll(rController.getRoute(startNode, endNode, disabledTypes));
        }
      } else {
        if (otherRouteController.getRoute(startNode, endNode, disabledTypes) == null) {
          directions = null;
        } else {
          directions.addAll(otherRouteController.getRoute(startNode, endNode, disabledTypes));
        }
      }
      routeType = RouteType.None;
    }
    // transitionDirectionsPane(true);
    createDirections(directions);
    directoryButtonLabel = directoryBtn.getText();
  }

  public void createDirections(List<Direction> directions) {
    nodesListView.setVisible(false);

    data.clear();
    String hospital = startNode.getHospitalName();
    int floor = homeNode.getFloor();
    if (directions != null) {
      for (Direction dir : directions) {
        ImageColumns tmp = new ImageColumns();
        tmp.setNode(dir.getNode());
        tmp.setDirection(dir.getDirection());

        if (dir.getDirection().toLowerCase().contains("sharp left")) {
          tmp.setImage(
              new ImageView(new Image("edu/wpi/teamp/images/icons/hard_left_icon_blue.png")));

        } else if (dir.getDirection().toLowerCase().contains("sharp right")) {
          tmp.setImage(
              new ImageView(new Image("edu/wpi/teamp/images/icons/hard_right_icon_blue.png")));

        } else if (dir.getDirection().toLowerCase().contains("slight right")) {
          tmp.setImage(
              new ImageView(new Image("edu/wpi/teamp/images/icons/slight_right_icon_blue.png")));

        } else if (dir.getDirection().toLowerCase().contains("slight left")) {
          tmp.setImage(
              new ImageView(new Image("edu/wpi/teamp/images/icons/slight_left_icon_blue.png")));

        } else if (dir.getDirection().toLowerCase().contains("left")) {
          // imageString = "images/icons/left_icon_blue.png";
          tmp.setImage(new ImageView(new Image("edu/wpi/teamp/images/icons/left_icon_blue.png")));
        } else if (dir.getDirection().toLowerCase().contains("right")) {
          // imageString = "images/icons/right_icon_blue.png";
          tmp.setImage(new ImageView(new Image("edu/wpi/teamp/images/icons/right_icon_blue.png")));

        } else if (dir.getDirection().toLowerCase().contains("straight")) {
          // imageString = "images/icons/straight_icon_blue.png";
          tmp.setImage(
              new ImageView(new Image("edu/wpi/teamp/images/icons/straight_icon_blue.png")));

        } else if (dir.getDirection().toLowerCase().contains("elevator")) {
          // imageString = "images/icons/elevator_icon_blue.png";
          tmp.setImage(
              new ImageView(new Image("edu/wpi/teamp/images/icons/elevator_icon_blue.png")));

        } else if (dir.getDirection().toLowerCase().contains("stairs")) {
          // imageString = "images/icons/stairs_icon_blue.png";
          tmp.setImage(new ImageView(new Image("edu/wpi/teamp/images/icons/stairs_icon_blue.png")));

        } else if (dir.getDirection().toLowerCase().contains("destination")) {
          // imageString = "images/icons/endpoint_icon_green.png";
          tmp.setImage(new ImageView(new Image("edu/wpi/teamp/images/icons/startpoint_icon.png")));

        } else {
          // imageString = "images/icons/endpoint_icon.png";
          tmp.setImage(new ImageView(new Image("edu/wpi/teamp/images/icons/endpoint_icon.png")));
        }

        data.add(tmp);
      }
    } else {
      ImageColumns errorMessage = new ImageColumns();
      errorMessage.setDirection("Can not reach destination with current settings");
      data.add(errorMessage);
    }
    // tableView.setVisible(true);
    // tableView.setDisable(false);
    // tableView.setItems(data);
    splitDirections();
  }

  @FXML
  public void initTable() {
    tableView.setVisible(false);
    tableView.setDisable(true);
    tableView.setPrefWidth(454);
    //    dirCol = new TableColumn();
    //    dirCol.setPrefWidth(400);
    //    dirCol.setCellValueFactory(new PropertyValueFactory<>("direction"));
    //    pictureCol = new TableColumn();
    //    pictureCol.setPrefWidth(54);
    //    pictureCol.setCellValueFactory(new PropertyValueFactory<>("image"));
    //    tableView.getColumns().addAll(dirCol, pictureCol);
  }

  public void clickDirection(MouseEvent mouseEvent) {
    ImageColumns tmp = tableView.getSelectionModel().getSelectedItem();
    if (tmp != null) {
      hospitalLocation = tmp.getHospital();

      if (!hospitalLocation.equals("")) {

        uController.addMap(map, tmp.getFloor(), tmp.getHospital());
        floorLabel.setText("Floor " + tmp.getFloor());
        map.setVisible(true);
        webView.setVisible(false);
        if (tmp.getHospital().equals("Faulkner")) {
          faulknerFloors.setVisible(true);
          mainFloors.setVisible(false);
        } else if (tmp.getHospital().equals("Main")) {
          mainFloors.setVisible(true);
          faulknerFloors.setVisible(false);
        }
      } else {
        webView.setVisible(true);
        map.setVisible(false);
        floorLabel.setText("");
        switch (routeType) {
          case ToFaulkner:
            webView
                .getEngine()
                .load(
                    this.getClass()
                        .getResource("/edu/wpi/teamp/googlemaps/ToFaulkner.html")
                        .toString());
            break;
          case ToMain:
            webView
                .getEngine()
                .load(
                    this.getClass()
                        .getResource("/edu/wpi/teamp/googlemaps/ToMainHospital.html")
                        .toString());
            break;
          default:
            webView
                .getEngine()
                .load(
                    this.getClass()
                        .getResource("/edu/wpi/teamp/googlemaps/GoogleMaps.html")
                        .toString());
        }
      }
    }
  }

  public void hoverDirections(MouseEvent e) {}

  private enum RouteType {
    ToFaulkner,
    ToMain,
    None;
  }

  public ArrayList<Direction> setUpToFaulkner() {
    ArrayList<Direction> directions = new ArrayList<>();
    directions.add(new Direction("Turn right onto Francis St.", null));
    directions.add(new Direction("Drive straight for 374 feet.", null));
    directions.add(new Direction("Turn left onto Brookline Ave.", null));
    directions.add(new Direction("Drive straight for 354 feet.", null));
    directions.add(new Direction("Turn left onto Riverway.", null));
    directions.add(new Direction("Continue straight for 1.9 miles.", null));
    directions.add(new Direction("Turn slight left onto Arborway.", null));
    directions.add(new Direction("Drive straight for 0.2 miles.", null));
    directions.add(new Direction("At the rotary take the 1st exit to Centre St.", null));
    directions.add(new Direction("Drive straight for 0.6 miles.", null));
    directions.add(new Direction("Turn right on Whitcomb Ave.", null));
    directions.add(new Direction("Drive straight for 466 feet.", null));
    directions.add(new Direction("Turn left into Faulkner Parking lot.", null));
    return directions;
  }

  public ArrayList<Direction> setUpToMain() {
    ArrayList<Direction> directions = new ArrayList<>();
    directions.add(new Direction("Turn right on Whitcomb Ave.", null));
    directions.add(new Direction("Drive straight for 466 feet.", null));
    directions.add(new Direction("Turn left onto Centre St.", null));
    directions.add(new Direction("Drive straight for 0.5 miles.", null));
    directions.add(new Direction("At the rotary take the 3rd exit to Arborway.", null));
    directions.add(new Direction("Drive straight for 2.1 miles.", null));
    directions.add(new Direction("Turn right on Brookline Ave.", null));
    directions.add(new Direction("Drive straight for 328 feet.", null));
    directions.add(new Direction("Turn right onto Francis street.", null));
    directions.add(new Direction("Drive straight for 0.1 miles.", null));
    directions.add(new Direction("Brigham and Women's Hospital is on your left.", null));
    return directions;
  }

  public void splitDirections() {
    int lastFloor = data.get(0).getFloor();

    ObservableList<ImageColumns> newData = FXCollections.observableArrayList();

    int stepCnt = 0;
    String lastHop = data.get(0).getHospital();
    int floor = data.get(0).getFloor();
    TitledPane tmp;
    for (ImageColumns i : data) {
      floor = i.getFloor();
      newData.add(i);
      if (floor != lastFloor) {
        stepCnt++;
        if (lastFloor != Integer.MIN_VALUE) {
          String currentFloor = Integer.toString(lastFloor);
          if (currentFloor.equals("0")) {
            currentFloor = "Ground";
          } else if (currentFloor.equals("-1")) {
            currentFloor = "L1";
          } else if (currentFloor.equals("-2")) {
            currentFloor = "L2";
          }
          tmp =
              new TitledPane(
                  String.format(
                      "Step " + stepCnt + ": " + lastHop + " Campus " + "Floor " + currentFloor),
                  getDirByFloor(lastFloor, lastHop));
        } else {
          tmp =
              new TitledPane(
                  String.format("Step " + stepCnt + ": " + "Driving Directions"),
                  getDirByFloor(lastFloor, lastHop));
        }
        tmp.setOnMouseClicked(e -> titledPaneClicked(e));
        directionAcc.getPanes().add(tmp);
        newData.clear();
      }

      newData.add(i);
      lastFloor = i.getFloor();
      lastHop = i.getHospital();
    }
    String currentFloor = Integer.toString(lastFloor);
    if (currentFloor.equals("0")) {
      currentFloor = "Ground";
    } else if (currentFloor.equals("-1")) {
      currentFloor = "L1";
    } else if (currentFloor.equals("-2")) {
      currentFloor = "L2";
    }

    stepCnt++;
    // tmpTableView.setItems(newData);
    tmp =
        new TitledPane(
            String.format(
                "Step " + stepCnt + ": " + lastHop + " Campus " + "Floor " + currentFloor),
            getDirByFloor(lastFloor, lastHop));

    tmp.setOnMouseClicked(e -> titledPaneClicked(e));
    directionAcc.getPanes().add(tmp);
    directionAcc.setExpandedPane(tmp);
    newData.clear();

    directionBox.setVisible(true);
    directionBox.setDisable(false);
    directionAcc.setVisible(true);
    directionAcc.setDisable(false);
    directionAcc.getPanes().get(0).setExpanded(true);
  }

  public TableView<ImageColumns> getDirByFloor(int i, String hos) {

    TableView<ImageColumns> tmpTableView = new TableView();
    dirCol = new TableColumn();
    dirCol.setPrefWidth(400);
    dirCol.setCellValueFactory(new PropertyValueFactory<>("direction"));
    pictureCol = new TableColumn();
    pictureCol.setPrefWidth(54);
    pictureCol.setCellValueFactory(new PropertyValueFactory<>("image"));
    tmpTableView.getColumns().addAll(pictureCol, dirCol);

    ObservableList<ImageColumns> newData = FXCollections.observableArrayList();

    tmpTableView.getStyleClass().add("noheader");

    if (uController.styleSheetType.equals("darkmode")) {
      tmpTableView.getStylesheets().add("edu/wpi/teamp/dark-mode/tableView.css");
    } else {
      tmpTableView.getStylesheets().add("edu/wpi/teamp/light-mode/tableView.css");
    }

    for (ImageColumns x : data) {
      if (x.getFloor() == i && x.getHospital().equals(hos)) {
        newData.add(x);
      }
    }

    tmpTableView.setItems(newData);
    tmpTableView.setOnMouseClicked(e -> clickDirection(e, tmpTableView));
    tmpTableView.setFixedCellSize(35);
    tmpTableView
        .prefHeightProperty()
        .bind(Bindings.size(tmpTableView.getItems()).multiply(tmpTableView.getFixedCellSize()));

    return tmpTableView;
  }

  public void clickDirection(MouseEvent mouseEvent, TableView<ImageColumns> tb) {
    ImageColumns tmp = tb.getSelectionModel().getSelectedItem();
    if (tmp != null) {
      hospitalLocation = tmp.getHospital();

      if (!hospitalLocation.equals("")) {

        uController.addMap(map, tmp.getFloor(), tmp.getHospital());
        floorLabel.setText("Floor " + tmp.getFloor());
        map.setVisible(true);
        webView.setVisible(false);
        if (tmp.getHospital().equals("Faulkner")) {
          faulknerFloors.setVisible(true);
          mainFloors.setVisible(false);
        } else if (tmp.getHospital().equals("Main")) {
          mainFloors.setVisible(true);
          faulknerFloors.setVisible(false);
        }
      } else {
        webView.setVisible(true);
        map.setVisible(false);
        floorLabel.setText("");
        switch (routeType) {
          case ToFaulkner:
            webView
                .getEngine()
                .load(
                    this.getClass()
                        .getResource("/edu/wpi/teamp/googlemaps/ToFaulkner.html")
                        .toString());
            break;
          case ToMain:
            webView
                .getEngine()
                .load(
                    this.getClass()
                        .getResource("/edu/wpi/teamp/googlemaps/ToMainHospital.html")
                        .toString());
            break;
          default:
            webView
                .getEngine()
                .load(
                    this.getClass()
                        .getResource("/edu/wpi/teamp/googlemaps/GoogleMaps.html")
                        .toString());
        }
      }
    }
  }

  public void titledPaneClicked(MouseEvent e) {
    TitledPane tmp = (TitledPane) e.getSource();
    String txt = tmp.getText();

    TableView<ImageColumns> tmpTableView = (TableView<ImageColumns>) tmp.getContent();
    System.out.println(tmpTableView.getItems().get(0));
    clickDirection(tmpTableView.getItems().get(0));
  }

  public void clickDirection(ImageColumns imgCol) {
    ImageColumns tmp = imgCol;
    if (tmp != null) {
      hospitalLocation = tmp.getHospital();

      if (!hospitalLocation.equals("")) {

        uController.addMap(map, tmp.getFloor(), tmp.getHospital());
        String currentFloor = Integer.toString(tmp.getFloor());
        if (currentFloor.equals("0")) {
          currentFloor = "Ground";
        } else if (currentFloor.equals("-1")) {
          currentFloor = "L1";
        } else if (currentFloor.equals("-2")) {
          currentFloor = "L2";
        }
        floorLabel.setText("Floor " + currentFloor);
        map.setVisible(true);
        webView.setVisible(false);
        if (tmp.getHospital().equals("Faulkner")) {
          faulknerFloors.setVisible(true);
          mainFloors.setVisible(false);
        } else if (tmp.getHospital().equals("Main")) {
          mainFloors.setVisible(true);
          faulknerFloors.setVisible(false);
        }
      } else {
        webView.setVisible(true);
        map.setVisible(false);
        floorLabel.setText("");
        switch (routeType) {
          case ToFaulkner:
            webView
                .getEngine()
                .load(
                    this.getClass()
                        .getResource("/edu/wpi/teamp/googlemaps/ToFaulkner.html")
                        .toString());
            break;
          case ToMain:
            webView
                .getEngine()
                .load(
                    this.getClass()
                        .getResource("/edu/wpi/teamp/googlemaps/ToMainHospital.html")
                        .toString());
            break;
          default:
            webView
                .getEngine()
                .load(
                    this.getClass()
                        .getResource("/edu/wpi/teamp/googlemaps/GoogleMaps.html")
                        .toString());
        }
      }
    }
  }

  public void handleMapClick(AbstractNode node) {
    if (endNode == null) {
      endNode = node;
      endSearch.setText(node.getLongName());
      getRoute();
      transitionDirectoriesPane(false);
      transitionHospitalPane(false);
      updateDirectoryButton("Directory", false);
    }
  }
}
