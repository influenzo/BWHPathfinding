package fxgraph.graph;

import fxgraph.cells.AbstractCell;
import fxgraph.cells.HallwayCell;
import fxgraph.cells.RoomCell;
import fxgraph.edges.AbstractEdge;
import fxgraph.edges.Edge;
import hospital.*;
import hospital.exceptions.IncompatibleNodeTypeException;
import hospital.exceptions.NullHospitalException;
import hospital.exceptions.NullNodeException;
import hospital.route.*;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.WindowEvent;

public class Graph {

  private PannableCanvas pannableCanvas;
  private Map<IGraphNode, Region> graphics;
  private NodeGestures nodeGestures;
  private ViewportGestures viewportGestures;
  private final List<AbstractEdge> edges = new ArrayList<>(); // current existing edges
  private final List<AbstractCell> cells = new ArrayList<>(); // current existing cells
  private final List<AbstractEdge> newEdges = new ArrayList<>(); // edges to add
  private final List<AbstractCell> newCells = new ArrayList<>(); // cells to add
  private final List<AbstractEdge> oldEdges = new ArrayList<>(); // edges to be removed
  private final List<AbstractCell> oldCells = new ArrayList<>(); // cells to be removed
  private ArrayList<AbstractNode> deletedNodes = new ArrayList<>(); // nodes to be deleted
  private ArrayList<AbstractEdge> deletedEdges = new ArrayList<>(); // node edges to be deleted
  private ArrayList<AbstractEdge> createdEdges = new ArrayList<>(); // node edges to be deleted
  private ArrayList<AbstractNode> createdNodes =
      new ArrayList<>(); // nodes to be added to a hospital
  private HashMap<String, NodeData> editedNodes = new HashMap<>(); // nodes to edit info
  private boolean canEdit = false;
  private int floor;
  private AbstractEdge edgeInProgress = null;
  private boolean draggingNode = false;
  private ContextMenu contextMenu = new ContextMenu();
  private double eventX;
  private double eventY;
  private boolean contextMenuOpen = false;
  public EventHandler<WindowEvent> onShown =
      event -> {
        contextMenuOpen = true;
        contextMenu.hide();
      };
  public EventHandler<WindowEvent> onHidden = event -> contextMenuOpen = false;
  private RouteController rController;
  private boolean shiftPressed = false;
  private Rectangle selectionBox;
  private Timer selectionBoxBorderTimer;
  private boolean boxSelecting;
  private double minZoom;
  private double maxZoom;
  private ArrayList<AbstractEdge> path = new ArrayList<>();

  public Graph(RouteController rController, int floor, String hospitalName) {
    this.rController = rController;
    this.floor = floor;
    nodeGestures = new NodeGestures(this);

    if (hospitalName.equals("Faulkner")) {
      minZoom = 0.6;
      maxZoom = 1.2;
    } else if (hospitalName.equals("Main")) {
      minZoom = 0.3;
      maxZoom = 0.6;
    }

    pannableCanvas = new PannableCanvas(floor, hospitalName, minZoom);
    viewportGestures = new ViewportGestures(pannableCanvas, this);
    viewportGestures.setZoomBounds(minZoom, maxZoom);

    pannableCanvas.addEventHandler(
        MouseEvent.MOUSE_PRESSED, viewportGestures.getOnMousePressedEventHandler());
    pannableCanvas.addEventHandler(
        MouseEvent.MOUSE_RELEASED, viewportGestures.getOnMouseReleasedEventHandler());
    pannableCanvas.addEventHandler(
        MouseEvent.MOUSE_DRAGGED, viewportGestures.getOnMouseDraggedEventHandler());
    pannableCanvas.addEventHandler(
        MouseEvent.MOUSE_MOVED, viewportGestures.getOnMouseMovedEventHandler());
    pannableCanvas.addEventHandler(ScrollEvent.ANY, viewportGestures.getOnScrollEventHandler());

    MenuItem addHallway = new MenuItem("Add Hallway");
    contextMenu.getItems().addAll(addHallway);
    addHallway.setOnAction((ActionEvent event) -> addHallway());

    MenuItem addRoom = new MenuItem("Add Room");
    contextMenu.getItems().addAll(addRoom);
    addRoom.setOnAction((ActionEvent event) -> addRoom());

    graphics = new HashMap<>();
  }

  public void update() {
    addCells(); // add new cells
    addEdges(); // add new edges
    removeCells(); // remove old cells
    removeEdges(); // remove old edges
    newCells.clear();
    newEdges.clear();
    oldCells.clear();
    oldEdges.clear();
  }

  // escape key event
  public void escape() {
    if (edgeInProgress != null) {
      removeEdge(edgeInProgress); // remove self from model
      edgeInProgress = null;
      update();
    } else if (selectionBox != null && !boxSelecting) {
      getCanvas().getChildren().remove(selectionBox);
      selectionBox = null;
      selectionBoxBorderTimer.cancel();
    }
  }

  // delete key event
  public void delete() {
    // delete any selected nodes
    if (selectionBox != null && !boxSelecting) {
      for (int i = 0; i < cells.size(); i++)
        if (selectionBox
            .getBoundsInParent()
            .contains(cells.get(i).getGraphic().getBoundsInParent())) {
          cells.get(i).delete();
          i--;
        }
    }
  }

  public void setEventCoordinates(double eventX, double eventY) {
    this.eventX = eventX;
    this.eventY = eventY;
  }

  public void openContextMenu(double screenX, double screenY) {
    contextMenu.show(pannableCanvas, screenX, screenY);
  }

  public void startBoxSelection() {
    if (selectionBox != null) {
      getCanvas().getChildren().remove(selectionBox);
      selectionBoxBorderTimer.cancel();
    }

    selectionBox = new Rectangle(eventX, eventY, 0, 0);
    selectionBox.setFill(Color.WHITE);
    selectionBox.setOpacity(0.35);
    selectionBox.setStrokeWidth(1);
    selectionBox.getStrokeDashArray().addAll(3.0, 7.0, 3.0, 7.0);
    selectionBox.setStroke(Color.BLACK);

    getCanvas().getChildren().add(selectionBox);
    boxSelecting = true;
  }

  public void updateBoxSelection(double eventX, double eventY) {
    double startX = eventX < this.eventX ? eventX : this.eventX;
    double startY = eventY < this.eventY ? eventY : this.eventY;
    selectionBox.setX(startX);
    selectionBox.setY(startY);
    selectionBox.setWidth(Math.abs(eventX - this.eventX));
    selectionBox.setHeight(Math.abs(eventY - this.eventY));
  }

  public void endBoxSelection() {
    selectionBoxBorderTimer = new Timer();
    selectionBoxBorderTimer.scheduleAtFixedRate(
        new TimerTask() {
          @Override
          public void run() {
            javafx.application.Platform.runLater(
                () -> selectionBox.setStrokeDashOffset(selectionBox.getStrokeDashOffset() + 1));
          }
        },
        0,
        25);
    boxSelecting = false;
  }

  private void addHallway() {
    GraphHelper helper =
        new GraphHelper() {
          @Override
          public void respond(Graph graph, NodeData data) {
            AbstractNode node = graph.generateNewNode(data.nodeType);
            node.updateNodeData(data);
            graph.createNode(node);

            AbstractCell cell = new HallwayCell(eventX, eventY, node, graph);
            graph.addCell(cell); // add new hallway cell
            graph.update();
            node.setCell(cell);
          }

          @Override
          public void respond(Graph graph, NodeData data, IGraphNode source) {}
        };
    rController.getMapEditingController().getNodeData(helper, this, "hall", null);
  }

  private void addRoom() {
    GraphHelper helper =
        new GraphHelper() {
          @Override
          public void respond(Graph graph, NodeData data) {
            AbstractNode node = graph.generateNewNode(data.nodeType);
            node.updateNodeData(data);
            graph.createNode(node);

            AbstractCell cell = new RoomCell(eventX, eventY, node, graph);
            graph.addCell(cell); // add new hallway cell
            graph.update();
            node.setCell(cell);
          }

          @Override
          public void respond(Graph graph, NodeData data, IGraphNode source) {}
        };
    rController.getMapEditingController().getNodeData(helper, this, "room", null);
  }

  public AbstractNode generateNewNode(NodeType type) {
    try {
      if (type.isHallwayType()) {
        return new HallwayNode(null, 0, 0, null, null, null, floor, null, "P");
      } else {
        return new RoomNode(null, 0, 0, null, null, null, floor, null, "P");
      }
    } catch (NullHospitalException | IncompatibleNodeTypeException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void addEdge(AbstractEdge edge) {
    this.newEdges.add(edge);
  }

  public void removeEdge(AbstractEdge edge) {
    this.oldEdges.add(edge);
  }

  public void addCell(AbstractCell cell) {
    this.newCells.add(cell);
  }

  public void removeCell(AbstractCell cell) {
    this.oldCells.add(cell);
  }

  private void addEdges() {
    this.edges.addAll(newEdges);
    int i = 0;
    for (AbstractEdge edge : newEdges) {
      getCanvas().getChildren().add(getGraphic(edge));
      if (!canEdit) {
        edge.disable();
      }
    }
    newEdges.clear();
  }

  private void removeEdges() {
    this.edges.removeAll(oldEdges);
    for (AbstractEdge edge : oldEdges) {
      getCanvas().getChildren().remove(getGraphic(edge));
    }
  }

  private void addCells() {
    this.cells.addAll(newCells);
    for (AbstractCell cell : newCells) {
      Region cellGraphic = getGraphic(cell);
      getCanvas().getChildren().add(cellGraphic);
      nodeGestures.makeDraggable(cellGraphic);
      cell.relocate(cell.getEventX(), cell.getEventY());
      if (!canEdit) {
        cell.disable();
      }
    }
  }

  private void removeCells() {
    this.cells.removeAll(oldCells);
    for (AbstractCell cell : oldCells) {
      getCanvas().getChildren().remove(getGraphic(cell));
    }
  }

  public void deleteNode(AbstractNode node) {
    deletedNodes.add(node);
  }

  public void deleteCreatedNode(AbstractNode node) {
    createdNodes.remove(node);
  }

  public void deleteEdge(AbstractEdge edge) {
    deletedEdges.add(edge);
  }

  public void deleteCreatedEdge(AbstractEdge edge) {
    createdEdges.remove(edge);
  }

  public void createEdge(AbstractEdge edge) {
    createdEdges.add(edge);
  }

  public void createNode(AbstractNode node) {
    createdNodes.add(node);
  }

  public void editNode(String nodeId, NodeData data) {
    editedNodes.put(nodeId, data);
  }

  public void saveChanges() {
    try {
      for (AbstractNode node : deletedNodes) {
        rController.removeNode(node.getId());
      }
      for (AbstractEdge edge : deletedEdges) {
        AbstractNode node1 = edge.getSource().getNode();
        AbstractNode node2 = edge.getTarget().getNode();
        node1.removeAdjacency(node2);
        node2.removeAdjacency(node1);
        rController.updateNode(node1);
        rController.updateNode(node2);
      }
      for (AbstractNode node : createdNodes) {
        rController.addNode(node);
      }
      for (AbstractEdge edge : createdEdges) {
        AbstractNode node1 = edge.getSource().getNode();
        AbstractNode node2 = edge.getTarget().getNode();
        node1.addAdjacency(node2);
        node2.addAdjacency(node1);
        rController.updateNode(node1);
        rController.updateNode(node2);
      }
      for (String nodeId : editedNodes.keySet()) {
        rController.updateNodeData(nodeId, editedNodes.get(nodeId));
      }
      for (AbstractCell cell : cells) {
        if (cell.getNode().updateCoordinates()) {
          rController.updateNode(cell.getNode());
        }
      }
    } catch (NullNodeException e) {
      e.printStackTrace();
    }
    clearChanges();
  }

  public void clearChanges() {
    deletedNodes.clear();
    deletedEdges.clear();
    createdNodes.clear();
    createdEdges.clear();
  }

  public Region getGraphic(IGraphNode node) {
    try {
      if (!graphics.containsKey(node)) {
        graphics.put(node, createGraphic(node));
      }
      return graphics.get(node);
    } catch (final Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  public void moveCenterToPoint(double x, double y, double pointX, double pointY) {
    pannableCanvas.setScale(1);
    pannableCanvas.setTranslateX(-pointX + x);
    pannableCanvas.setTranslateY(-pointY + y);
  }

  public void moveCenterToPoint(
      double x, double y, double pointX, double pointY, double scale, boolean willTranslate) {
    //    pannableCanvas.setScale(1);
    pannableCanvas.setScale(scale);
    if (willTranslate) {
      pannableCanvas.setTranslateX(-pointX + x - ((x * 2) / ((1 / scale) * 2)));
      pannableCanvas.setTranslateY(-pointY + y + ((y * 2) / ((1 / scale) * 2)) - 50);
    } else {
      pannableCanvas.setTranslateX(-pointX + x);
      pannableCanvas.setTranslateY(-pointY + y);
    }
  }

  public void moveCenter(double x, double y) {
    pannableCanvas.setTranslateX(-(pannableCanvas.getMapWidth() / 2) + x);
    pannableCanvas.setTranslateY(-(pannableCanvas.getMapHeight() / 2) + y);
  }

  public void enableEditing() {
    canEdit = true;
    for (AbstractCell cell : cells) cell.enable();
    for (AbstractEdge edge : edges) {
      edge.enable();
      edge.unhighlight();
    }
    viewportGestures.setMaxScale(1.5d);
  }

  public void disableEditing() {
    canEdit = false;
    for (AbstractCell cell : cells) cell.disable();
    for (AbstractEdge edge : edges) edge.disable();
    viewportGestures.setMaxScale(1.2d);
  }

  public void disableContextMenus() {
    canEdit = false;
    for (AbstractCell cell : cells) cell.disableContextMenu();
    for (AbstractEdge edge : edges) edge.disableContextMenu();
  }

  public void enableContextMenus() {
    canEdit = true;
    for (AbstractCell cell : cells) cell.enableContextMenu();
    for (AbstractEdge edge : edges) edge.disableContextMenu();
  }

  public void showCells() {
    for (AbstractCell cell : cells) cell.show();
  }

  public void hideCells() {
    for (AbstractCell cell : cells) cell.hide();
  }

  public void resetCellColors() {
    for (AbstractCell cell : cells) cell.reset();
  }

  public void showEdges() {
    for (AbstractEdge edge : edges) edge.show();
  }

  public void hideEdges() {
    for (AbstractEdge edge : edges) edge.hide();
  }

  public Region createGraphic(IGraphNode node) {
    return node.getGraphic(this);
  }

  public AbstractEdge getEdgeInProgress() {
    return edgeInProgress;
  }

  public void setEdgeInProgress(AbstractEdge edgeInProgress) {
    this.edgeInProgress = edgeInProgress;
  }

  public void setDraggingNode(boolean draggingNode) {
    this.draggingNode = draggingNode;
  }

  public boolean getDraggingNode() {
    return draggingNode;
  }

  public double getScale() {
    return getCanvas().getScale();
  }

  public boolean edgeExists(long hash) {
    for (AbstractEdge edge : edges) {
      if (hash == edge.getId()) {
        return true;
      }
    }
    return false;
  }

  public void highlightPath(List<AbstractNode> nodes, String color) {
    resetCellColors();
    oldEdges.addAll(path);
    removeEdges();
    path.clear();

    for (int i = 0; i < nodes.size() - 1; i++) {
      if (nodes.get(i).getFloor() == floor
          && nodes.get(i).getHospitalName().equals(rController.getHospitalName())
          && nodes.get(i + 1).getFloor() == floor
          && nodes.get(i + 1).getHospitalName().equals(rController.getHospitalName())) {
        path.add(new Edge(nodes.get(i).getCell(), nodes.get(i + 1).getCell(), this));
      }
    }
    newEdges.addAll(path);
    addEdges();

    // make starting and ending node visible
    nodes.get(0).getCell().navigationNode(true);
    nodes.get(nodes.size() - 1).getCell().navigationNode(false);

    for (AbstractEdge edge : path) {
      // edge.hide();
      edge.highlight(color == null ? "#00c8ff" : color);
    }
  }

  public void unhighlightCells() {
    for (AbstractCell cell : cells) {
      cell.unhighlight();
    }
  }

  public void highlightPath(List<AbstractNode> nodes) {
    // highlightPath(nodes, "#e67e10");
    highlightPath(nodes, "#0049AF");
  }

  public void clearPath() {
    for (AbstractEdge edge : edges) {
      edge.hide();
    }
  }

  public PannableCanvas getCanvas() {
    return pannableCanvas;
  }

  public List<AbstractEdge> getEdges() {
    return edges;
  }

  public List<AbstractCell> getCells() {
    return cells;
  }

  public ArrayList<AbstractEdge> getCreatedEdges() {
    return createdEdges;
  }

  public ArrayList<AbstractNode> getCreatedNodes() {
    return createdNodes;
  }

  public ContextMenu getContextMenu() {
    return contextMenu;
  }

  public RouteController getRouteController() {
    return rController;
  }

  public boolean getShiftPressed() {
    return shiftPressed;
  }

  public void setShiftPressed(boolean shiftPressed) {
    this.shiftPressed = shiftPressed;
  }

  public boolean canEdit() {
    return canEdit;
  }

  public boolean getContextMenuOpen() {
    return contextMenuOpen;
  }

  public Rectangle getSelectionBox() {
    return selectionBox;
  }

  public boolean getBoxSelecting() {
    return boxSelecting;
  }

  public int getFloor() {
    return floor;
  }
}
