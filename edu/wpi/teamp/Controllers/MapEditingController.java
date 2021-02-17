package edu.wpi.teamp.Controllers;

import com.jfoenix.controls.*;
import edu.wpi.teamp.Controllers.ServiceRequest.RequestReceivedPopupController;
import edu.wpi.teamp.UIController;
import fxgraph.cells.AbstractCell;
import fxgraph.graph.Graph;
import fxgraph.graph.GraphHelper;
import fxgraph.graph.IGraphNode;
import hospital.route.AbstractNode;
import hospital.route.NodeData;
import hospital.route.NodeType;
import hospital.route.RouteController;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MapEditingController {

  Stage stage; // initializes stage
  Parent root; // initializes root
  @FXML private AnchorPane anchor;
  @FXML private Pane map;
  @FXML private Pane sideMap;
  @FXML private VBox sideMapBox;
  @FXML private JFXButton saveChanges;
  @FXML private JFXTextField nodeId;
  @FXML private JFXTextField longName;
  @FXML private JFXTextField shortName;
  @FXML private JFXComboBox<String> nodeType;
  @FXML private JFXButton emergency;

  @FXML private JFXButton apply;
  @FXML private JFXButton cancel;
  @FXML private JFXButton download;
  @FXML private JFXButton floor1;
  @FXML private JFXButton floor2;
  @FXML private JFXButton floor3;
  @FXML private JFXButton floor4;
  @FXML private JFXButton floor5;
  @FXML private JFXButton floor1Slide;
  @FXML private JFXButton floor2Slide;
  @FXML private JFXButton floor3Slide;
  @FXML private JFXButton floor4Slide;
  @FXML private JFXButton floor5Slide;
  @FXML private Label floorLabel;
  @FXML private JFXDrawer drawer;
  @FXML private AnchorPane drawerPane;
  @FXML private VBox nodeFieldsBox;
  @FXML private HBox nodeFieldsButtons;
  @FXML private Label errorLabel;
  @FXML private JFXButton drawerLabel;
  @FXML private HBox slideButtonsFaulkner;
  @FXML private HBox slideButtonsMain;
  @FXML private VBox faulknerButtons;
  @FXML private VBox mainButtons;
  @FXML private JFXButton mainL2Button;
  @FXML private JFXButton mainL1Button;
  @FXML private JFXButton mainGButton;
  @FXML private JFXButton main1Button;
  @FXML private JFXButton main2Button;
  @FXML private JFXButton main3Button;
  @FXML private JFXButton mainL2Slide;
  @FXML private JFXButton mainL1Slide;
  @FXML private JFXButton mainGSlide;
  @FXML private JFXButton main1Slide;
  @FXML private JFXButton main2Slide;
  @FXML private JFXButton main3Slide;

  @FXML RequestReceivedPopupController requestReceivedPopupController;

  int floor = 1;
  GraphHelper helper;
  Graph graph;
  IGraphNode source;
  private UIController uController = UIController.getUIController();
  private RouteController rController;
  DirectoryChooser directoryChooser = new DirectoryChooser();
  AbstractCell sourceCell;
  int sourceFloor = -1;
  HashMap<Integer, AbstractNode> sourceNodes = new HashMap<>();
  HashMap<Integer, HashMap<Integer, AbstractCell>> selectedCellsList = new HashMap<>();
  HashMap<Integer, ArrayList<AbstractCell>> deselectedCellsList = new HashMap<>();
  HashMap<Integer, AbstractCell> selectedCells = new HashMap<>();
  ArrayList<AbstractCell> deselectedCells = new ArrayList<>();

  @FXML
  public void initialize() {
    rController = uController.getRouteController();
    anchor.setPrefWidth(1920);

    rController.setMapEditingController(this);
    uController.generateMaps(true);
    uController.addMap(map, 1, uController.getHospital().getName());
    uController.moveMapCenter(
        (uController.getPrimaryScreenBounds().getWidth()) / 2,
        (uController.getPrimaryScreenBounds().getHeight() - 200) / 2);
    sideMap.setClip(new Rectangle(5, 5, 356, 356));

    drawer.close();
    drawer.setSidePane(drawerPane);
    drawer.setDefaultDrawerSize(386);

    if (uController.getHospital().getName() == "Faulkner") {
      faulknerButtons.setVisible(true);
      mainButtons.setVisible(false);
    } else {
      mainButtons.setVisible(true);
      faulknerButtons.setVisible(false);
    }
  }

  public void getNodeData(GraphHelper helper, Graph graph, String type, IGraphNode source) {
    this.helper = helper;
    this.graph = graph;
    this.source = source;

    menuTransition(true);
    toggleNodeFieldOptions(true);
    nodeFieldsButtons.setDisable(false);

    setNodeTypeOptions(type);
    nodeId.setText("");
    nodeId.setDisable(false);
    shortName.setText("");
    longName.setText("");
    nodeType.getSelectionModel().clearSelection();
  }

  @FXML
  public void editNodeData(GraphHelper helper, Graph graph, String type, AbstractCell source) {
    this.helper = helper;
    this.graph = graph;
    this.source = source;
    drawer.close();
    disableMap();

    menuTransition(true);
    toggleNodeFieldOptions(true);
    nodeFieldsButtons.setDisable(false);

    setNodeTypeOptions(type);
    AbstractNode node = source.getNode();
    nodeId.setText(node.getId());
    nodeId.setDisable(true);
    shortName.setText(node.getShortName());
    longName.setText(node.getLongName());
    nodeType.getSelectionModel().select(node.getNodeType().toString());
  }

  public void addFloorTransitionEdge(AbstractCell cell) {
    sourceCell = cell;
    sourceFloor = floor;

    menuTransition(true);
    floorLabel.setVisible(true);
    sideMapBox.setVisible(true);
    drawerLabel.setText("Floor Transition Adjacency");
    cancel.setText("Reset");
    nodeFieldsButtons.setDisable(false);
    disableSourceFloor(sourceFloor, true);
    displayCorrectSlideButtons();
    if (floor != 1 && uController.getHospital().getName().equals("Faulkner")) floor--;
    else if (floor != -2 && uController.getHospital().getName().equals("Main")) floor--;
    else floor++;
    if (floor == -2) floorLabel.setText("Floor: L2");
    else if (floor == -1) floorLabel.setText("Floor: L1");
    else if (floor == 0) floorLabel.setText("Floor: G");
    else floorLabel.setText("Floor: " + floor);
    uController.addMap(sideMap, floor, uController.getHospital().getName());
    rController.getGraph(floor).disableContextMenus();
    uController.moveMapCenterToPoint(floor, 150, 150, cell.getCenterX(), cell.getCenterY());

    if (selectedCellsList.get(cell.getId()) == null) {
      sourceNodes.put(cell.getId(), cell.getNode());
      selectedCellsList.put(cell.getId(), new HashMap<>());
      deselectedCellsList.put(cell.getId(), new ArrayList<>());
    }
    selectedCells = selectedCellsList.get(cell.getId());
    deselectedCells = deselectedCellsList.get((cell.getId()));

    for (AbstractNode adjacency : cell.getNode().getAdjacencies()) {
      if (adjacency.getFloor() != cell.getNode().getFloor()) adjacency.getCell().highlight();
    }
    for (AbstractCell c : deselectedCells) c.unhighlight();
    for (int floor : selectedCells.keySet()) {
      if (selectedCells.get(floor) != null) {
        selectedCells.get(floor).highlight();
      }
    }
  }

  private void setNodeTypeOptions(String type) {
    String options[];

    // option for nodeType drop menu
    if (type == "hall") {
      options = new String[] {"HALL", "STAI", "ELEV"};
    } else {
      options =
          new String[] {
            "REST", "CONF", "DEPT", "SERV", "FOOD", "RETL", "STAF", "EXIT", "KIOS", "LAB", "INFO"
          };
    }
    nodeType.setItems(FXCollections.observableArrayList(options));
  }

  private void disableMap() {
    FadeTransition transition = new FadeTransition(Duration.millis(300));
    transition.setNode(map);
    transition.setFromValue(1);
    transition.setToValue(0.4);
    transition.play();
    map.setDisable(true);
  }

  private void enableMap() {
    FadeTransition transition = new FadeTransition(Duration.millis(300));
    transition.setNode(map);
    transition.setFromValue(0.4);
    transition.setToValue(1);
    transition.play();
    map.setDisable(false);
  }

  private void toggleNodeFieldOptions(boolean enable) {
    if (enable) {
      drawerLabel.setText("Node Information");
      cancel.setText("Cancel");
    } else {
      nodeId.setText("");
      nodeId.setDisable(false);
      shortName.setText("");
      longName.setText("");
      nodeType.getSelectionModel().clearSelection();
    }
    nodeFieldsBox.setDisable(!enable);
  }

  @FXML
  public void btnClicked(ActionEvent e) throws IOException {
    if (e.getSource() == apply) {
      if (!sideMapBox.visibleProperty().get()) sendNodeData();
      menuTransition(false);
    } else if (e.getSource() == cancel) {
      if (sideMapBox.visibleProperty().get()) {
        for (int id : selectedCellsList.keySet()) {
          selectedCellsList.put(id, new HashMap<>());
          deselectedCellsList.put(id, new ArrayList<>());
        }
      }
      menuTransition();
    } else if (e.getSource() == saveChanges) {
      saveChanges();
    } else if (e.getSource() == floor1 || e.getSource() == floor1Slide) {
      floor = 1;
      if (!sideMapBox.visibleProperty().get()) {
        uController.addMap(map, 1, uController.getHospital().getName());
        rController.getGraph(1).enableContextMenus();
        rController.getGraph(1).showEdges();
      } else {
        uController.addMap(sideMap, floor, uController.getHospital().getName());
        rController.getGraph(floor).disableContextMenus();
        uController.moveMapCenterToPoint(
            floor, 150, 150, sourceCell.getCenterX(), sourceCell.getCenterY());
      }
      floorLabel.setText("Floor: 1");
    } else if (e.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");
    } else if (e.getSource() == floor2 || e.getSource() == floor2Slide) {
      floor = 2;
      if (!sideMapBox.visibleProperty().get()) {
        uController.addMap(map, 2, uController.getHospital().getName());
        rController.getGraph(2).enableContextMenus();
        rController.getGraph(2).showEdges();
      } else {
        uController.addMap(sideMap, floor, uController.getHospital().getName());
        rController.getGraph(floor).disableContextMenus();
        uController.moveMapCenterToPoint(
            floor, 150, 150, sourceCell.getCenterX(), sourceCell.getCenterY());
      }
      floorLabel.setText("Floor: 2");
    } else if (e.getSource() == floor3 || e.getSource() == floor3Slide) {
      floor = 3;
      if (!sideMapBox.visibleProperty().get()) {
        uController.addMap(map, 3, uController.getHospital().getName());
        rController.getGraph(3).enableContextMenus();
        rController.getGraph(3).showEdges();
      } else {
        uController.addMap(sideMap, floor, uController.getHospital().getName());
        rController.getGraph(floor).disableContextMenus();
        uController.moveMapCenterToPoint(
            floor, 150, 150, sourceCell.getCenterX(), sourceCell.getCenterY());
      }
      floorLabel.setText("Floor: 3");
    } else if (e.getSource() == floor4 || e.getSource() == floor4Slide) {
      floor = 4;
      if (!sideMapBox.visibleProperty().get()) {
        uController.addMap(map, 4, uController.getHospital().getName());
        rController.getGraph(4).enableContextMenus();
        rController.getGraph(4).showEdges();
      } else {
        uController.addMap(sideMap, floor, uController.getHospital().getName());
        rController.getGraph(floor).disableContextMenus();
        uController.moveMapCenterToPoint(
            floor, 150, 150, sourceCell.getCenterX(), sourceCell.getCenterY());
      }
      floorLabel.setText("Floor: 4");
    } else if (e.getSource() == floor5 || e.getSource() == floor5Slide) {
      floor = 5;
      if (!sideMapBox.visibleProperty().get()) {
        uController.addMap(map, 5, uController.getHospital().getName());
        rController.getGraph(5).enableContextMenus();
        rController.getGraph(5).showEdges();
      } else {
        uController.addMap(sideMap, floor, uController.getHospital().getName());
        rController.getGraph(floor).disableContextMenus();
        uController.moveMapCenterToPoint(
            floor, 150, 150, sourceCell.getCenterX(), sourceCell.getCenterY());
      }
      floorLabel.setText("Floor: 5");
    } else if (e.getSource() == main1Button || e.getSource() == main1Slide) {
      floor = 1;
      if (!sideMapBox.visibleProperty().get()) {
        uController.addMap(map, floor, uController.getHospital().getName());
        rController.getGraph(floor).enableContextMenus();
        rController.getGraph(floor).showEdges();
      } else {
        uController.addMap(sideMap, floor, uController.getHospital().getName());
        rController.getGraph(floor).disableContextMenus();
        uController.moveMapCenterToPoint(
            floor, 150, 150, sourceCell.getCenterX(), sourceCell.getCenterY());
      }
      floorLabel.setText("Floor: 1");
    } else if (e.getSource() == main2Button || e.getSource() == main2Slide) {
      floor = 2;
      if (!sideMapBox.visibleProperty().get()) {
        uController.addMap(map, floor, uController.getHospital().getName());
        rController.getGraph(floor).enableContextMenus();
        rController.getGraph(floor).showEdges();
      } else {
        uController.addMap(sideMap, floor, uController.getHospital().getName());
        rController.getGraph(floor).disableContextMenus();
        uController.moveMapCenterToPoint(
            floor, 150, 150, sourceCell.getCenterX(), sourceCell.getCenterY());
      }
      floorLabel.setText("Floor: 2");
    } else if (e.getSource() == main3Button || e.getSource() == main3Slide) {
      floor = 3;
      if (!sideMapBox.visibleProperty().get()) {
        uController.addMap(map, floor, uController.getHospital().getName());
        rController.getGraph(floor).enableContextMenus();
        rController.getGraph(floor).showEdges();
      } else {
        uController.addMap(sideMap, floor, uController.getHospital().getName());
        rController.getGraph(floor).disableContextMenus();
        uController.moveMapCenterToPoint(
            floor, 150, 150, sourceCell.getCenterX(), sourceCell.getCenterY());
      }
      floorLabel.setText("Floor: 3");
    } else if (e.getSource() == mainL1Button || e.getSource() == mainL1Slide) {
      floor = -1;
      if (!sideMapBox.visibleProperty().get()) {
        uController.addMap(map, floor, uController.getHospital().getName());
        rController.getGraph(floor).enableContextMenus();
        rController.getGraph(floor).showEdges();
      } else {
        uController.addMap(sideMap, floor, uController.getHospital().getName());
        rController.getGraph(floor).disableContextMenus();
        uController.moveMapCenterToPoint(
            floor, 150, 150, sourceCell.getCenterX(), sourceCell.getCenterY());
      }
      floorLabel.setText("Floor: L1");
    } else if (e.getSource() == mainL2Button || e.getSource() == mainL2Slide) {
      floor = -2;
      if (!sideMapBox.visibleProperty().get()) {
        uController.addMap(map, floor, uController.getHospital().getName());
        rController.getGraph(floor).enableContextMenus();
        rController.getGraph(floor).showEdges();
      } else {
        uController.addMap(sideMap, floor, uController.getHospital().getName());
        rController.getGraph(floor).disableContextMenus();
        uController.moveMapCenterToPoint(
            floor, 150, 150, sourceCell.getCenterX(), sourceCell.getCenterY());
      }
      floorLabel.setText("Floor: L2");
    } else if (e.getSource() == mainGButton || e.getSource() == mainGSlide) {
      floor = 0;
      if (!sideMapBox.visibleProperty().get()) {
        uController.addMap(map, floor, uController.getHospital().getName());
        rController.getGraph(floor).enableContextMenus();
        rController.getGraph(floor).showEdges();
      } else {
        uController.addMap(sideMap, floor, uController.getHospital().getName());
        rController.getGraph(floor).disableContextMenus();
        uController.moveMapCenterToPoint(
            floor, 150, 150, sourceCell.getCenterX(), sourceCell.getCenterY());
      }
      floorLabel.setText("Floor: G");
    } else if (e.getSource() == download) {
      String fileLocation = "";

      File file = directoryChooser.showDialog(stage); // opens DirectoryChooser

      if (file != null) { // gets fileLocation to the hospital.route.path to the directory
        fileLocation = file.getAbsolutePath();
        rController.downloadCSV(
            fileLocation + "//"); // calls downloadCSV to save CSV to the user computer
      }
    }
  }

  private void saveChanges() {
    requestReceivedPopupController.showPopup("Saved");
    // create and remove the specified edges between floors
    for (int id : selectedCellsList.keySet()) {
      selectedCells = selectedCellsList.get(id);
      deselectedCells = deselectedCellsList.get(id);
      AbstractNode sourceNode = sourceNodes.get(id);

      for (int floor : selectedCells.keySet()) {
        if (selectedCells.get(floor) != null) {
          AbstractNode node = selectedCells.get(floor).getNode();
          sourceNode.addAdjacency(node);
          node.addAdjacency(sourceNode);
          rController.updateNode(node);
          rController.updateNode(sourceNode);
        }
      }
      for (AbstractCell cell : deselectedCells) {
        AbstractNode node = cell.getNode();
        sourceNode.removeAdjacency(node);
        node.removeAdjacency(sourceNode);
        rController.updateNode(node);
        rController.updateNode(sourceNode);
      }
    }
    selectedCellsList.clear();
    deselectedCellsList.clear();
    sourceNodes.clear();

    rController.saveMapChanges();
  }

  private void sendNodeData() {
    if (nodeId.getText() != ""
        && shortName.getText() != ""
        && longName.getText() != ""
        && nodeType.getSelectionModel().getSelectedItem() != null) {
      NodeData data =
          new NodeData(
              nodeId.getText(),
              shortName.getText(),
              longName.getText(),
              NodeType.valueOf(nodeType.getSelectionModel().getSelectedItem()));
      if (source == null) {
        helper.respond(graph, data);
      } else {
        helper.respond(graph, data, source);
      }
      menuTransition(false);
    } else {
      errorLabel.setVisible(true);
    }
  }

  public void menuTransition(boolean open) {
    if (!open) {
      if (sourceFloor != -1) {
        floor = sourceFloor;
        uController.addMap(map, floor, uController.getHospital().getName());
      }
      sourceFloor = -1;
      drawer.close();
      drawer.setDisable(true);
      drawerPane.setDisable(true);
      disableSlideButtons();
      enableMap();
      if (uController.getHospital().getName().equals("Faulkner")) {
        faulknerButtons.setVisible(true);
      } else if (uController.getHospital().getName().equals("Main")) {
        mainButtons.setVisible(true);
      }
      enableSlideButtons();
    } else {

      drawerPane.setDisable(false);
      drawer.setDisable(false);
      drawer.open();
      disableMap();
      disableNormalFloorButtons();
      floorLabel.setVisible(false);
    }
  }

  public void selectCell(AbstractCell cell) {
    if (cell.getNode().getNodeType() == sourceCell.getNode().getNodeType()) {
      cell.highlight();
      if (deselectedCells.contains(cell)) deselectedCells.remove(cell);
      else selectedCells.put(floor, cell);
    }
  }

  public void deselectCell(AbstractCell cell) {
    cell.unhighlight();
    if (selectedCells.get(floor) == null) deselectedCells.add(cell);
    else selectedCells.put(floor, null);
  }

  public void menuTransition() {
    menuTransition(drawer.isClosed() ? true : false);
  }

  private void disableSourceFloor(int sourceFloor, boolean disabled) {
    switch (sourceFloor) {
      case 1:
        if (uController.getHospital().getName().equals("Faulkner"))
          floor1Slide.setDisable(disabled);
        else if (uController.getHospital().getName().equals("Main"))
          main1Slide.setDisable(disabled);
        break;
      case 2:
        if (uController.getHospital().getName().equals("Faulkner"))
          floor2Slide.setDisable(disabled);
        else if (uController.getHospital().getName().equals("Main"))
          main2Slide.setDisable(disabled);
        break;
      case 3:
        if (uController.getHospital().getName().equals("Faulkner"))
          floor3Slide.setDisable(disabled);
        else if (uController.getHospital().getName().equals("Main"))
          main3Slide.setDisable(disabled);
        break;
      case 4:
        floor4Slide.setDisable(disabled);
        break;
      case 5:
        floor5Slide.setDisable(disabled);
        break;
      case 0:
        mainGSlide.setDisable(disabled);
        break;
      case -1:
        mainL1Slide.setDisable(disabled);
        break;
      case -2:
        mainL2Slide.setDisable(disabled);
        break;
    }
  }

  public void disableNormalFloorButtons() {
    System.out.println("Opening drawer");
    mainButtons.setVisible(false);
    faulknerButtons.setVisible(false);
  }

  private void displayCorrectSlideButtons() {
    if (uController.getHospital().getName().equals("Faulkner")) {
      slideButtonsFaulkner.setVisible(true);
    } else if (uController.getHospital().getName().equals("Main")) {
      slideButtonsMain.setVisible(true);
    }
  }

  private void disableSlideButtons() {
    slideButtonsMain.setVisible(false);
    slideButtonsFaulkner.setVisible(false);
  }

  private void enableSlideButtons() {
    if (uController.getHospital().getName().equals("Faulkner")) {
      floor1Slide.setDisable(false);
      floor2Slide.setDisable(false);
      floor3Slide.setDisable(false);
      floor4Slide.setDisable(false);
      floor5Slide.setDisable(false);
    } else if (uController.getHospital().getName().equals("Main")) {
      mainL2Slide.setDisable(false);
      mainL1Slide.setDisable(false);
      mainGSlide.setDisable(false);
      main1Slide.setDisable(false);
      main2Slide.setDisable(false);
      main3Slide.setDisable(false);
    }
  }
}
