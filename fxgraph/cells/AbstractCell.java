package fxgraph.cells;

import fxgraph.edges.AbstractEdge;
import fxgraph.edges.Edge;
import fxgraph.graph.Graph;
import fxgraph.graph.GraphHelper;
import fxgraph.graph.IGraphNode;
import hospital.HospitalController;
import hospital.route.AbstractNode;
import hospital.route.NodeData;
import hospital.route.NodeType;
import java.util.ArrayList;
import java.util.Random;
import javafx.beans.binding.DoubleBinding;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public abstract class AbstractCell implements IGraphNode {

  private AbstractNode node;
  private String name;
  public final ContextMenu contextMenu = new ContextMenu();
  public Graph graph;
  private double eventX;
  private double eventY;
  private Shape shape;
  private int id;
  private double radius;
  private String color;
  private Region graphic;
  public HospitalController hController = HospitalController.getHospitalController();
  private boolean isHighlighted;
  private double opacity;

  public AbstractCell(
      double x,
      double y,
      double radius,
      String color,
      AbstractNode node,
      Graph graph,
      double opacity) {
    this.eventX = x;
    this.eventY = y;
    this.radius = radius;
    this.color = color;
    this.id = getRandomId();
    this.node = node;
    this.graph = graph;
    this.opacity = opacity;
  }

  public void checkNodeType(NodeType nodeType) {}

  private int getRandomId() {
    Random r = new Random();
    int randomInt = r.nextInt(1073741823); // max positive int / 2
    return randomInt;
  }

  public Region getGraphic(Graph graph) {
    this.graph = graph;

    final Circle circle = new Circle(radius);
    setShape(circle);
    circle.setCenterX(radius);
    circle.setCenterY(radius);
    circle.setStyle("-fx-fill: " + color + "; -fx-opacity: " + opacity);
    addContextMenu(circle);
    createAddHallwayOption();
    createAddEdgeOption();
    checkNodeType(node.getNodeType());
    createEditInfoOption();
    createDeleteOption();

    final StackPane pane = new StackPane(circle);
    pane.setPrefSize(radius * 2, radius * 2);

    graphic = pane;
    return pane;
  }

  public Region getGraphic() {
    return graphic;
  }

  public void addContextMenu(Node view) {
    view.setOnMousePressed(
        (MouseEvent event) -> {
          if (event.isSecondaryButtonDown()) {
            Node node = (Node) event.getSource();
            Bounds bounds = node.getParent().getBoundsInParent();
            eventX = bounds.getMinX();
            eventY = bounds.getMinY();
            contextMenu.show(view, event.getScreenX(), event.getScreenY());
          }
        });
    contextMenu.setOnShown(graph.onShown);
    contextMenu.setOnHidden(graph.onHidden);
  }

  public void enableContextMenu() {
    contextMenu.setOpacity(1);
  }

  public void disableContextMenu() {
    contextMenu.setOpacity(0);
  }

  private void createAddHallwayOption() {
    MenuItem addHallway = new MenuItem("Add Hallway");
    contextMenu.getItems().addAll(addHallway);
    addHallway.setOnAction((ActionEvent event) -> addHallway());
  }

  private void createAddEdgeOption() {
    MenuItem addEdge = new MenuItem("Add Edge");
    contextMenu.getItems().addAll(addEdge);
    addEdge.setOnAction((ActionEvent event) -> addEdge());
  }

  private void createEditInfoOption() {
    MenuItem editInfo = new MenuItem("Edit Info");
    contextMenu.getItems().addAll(editInfo);
    editInfo.setOnAction((ActionEvent event) -> editInfo());
  }

  private void createDeleteOption() {
    MenuItem delete = new MenuItem("Delete");
    contextMenu.getItems().addAll(delete);
    delete.setOnAction((ActionEvent event) -> delete());
  }

  public void highlight() {
    isHighlighted = true;
    Circle circle = ((Circle) ((Pane) graphic).getChildren().get(0));
    circle.setRadius(radius + 4);
    circle.setStyle("-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.3),15,0,0,0); -fx-fill: #cebc44");
  }

  public void unhighlight() {
    isHighlighted = false;
    Circle circle = ((Circle) ((Pane) graphic).getChildren().get(0));
    circle.setRadius(radius);
    circle.setStyle("-fx-fill: " + color);
  }

  public void disable() {
    graphic.setDisable(true);
  }

  public void enable() {
    graphic.setDisable(false);
  }

  public void show() {
    graphic.setOpacity(1);
  }

  public void hide() {
    graphic.setOpacity(0);
    removeLabel();
  }

  public void addLabel() {
    StackPane pane = ((StackPane) graphic);
    Label label = new Label(node.getLongName());
    label.setStyle("-fx-text-fill: #fff; -fx-background-color: #000;");
    label.translateYProperty().set(-30);
    pane.getChildren().add(label);
    pane.setPrefWidth(label.getPrefWidth());
    double width = 25 + node.getLongName().length() * 3;
    pane.layoutXProperty().set(eventX - width);
  }

  public void removeLabel() {
    StackPane pane = ((StackPane) graphic);
    for (Node child : pane.getChildren()) {
      if (child.getClass() == Label.class) {
        pane.getChildren().remove(child);
        return;
      }
    }
  }

  // this is for displaying starting and end nodes
  public void navigationNode(boolean isStarting) {
    Circle circle = ((Circle) ((Pane) graphic).getChildren().get(0));
    circle.setRadius(radius);
    addLabel();
    show();

    if (isStarting) circle.setStyle("-fx-fill: #0049AF");
    else circle.setStyle("-fx-fill: red");
  }

  // for resetting starting and ending nodes
  public void reset() {
    Circle circle = ((Circle) ((Pane) graphic).getChildren().get(0));
    circle.setRadius(radius);
    circle.setStyle("-fx-fill: " + color);
    hide();
  }

  private void addHallway() {
    GraphHelper helper =
        new GraphHelper() {
          @Override
          public void respond(Graph graph, NodeData data) {}

          @Override
          public void respond(Graph graph, NodeData data, IGraphNode source) {
            AbstractNode node = graph.generateNewNode(data.nodeType);
            node.updateNodeData(data);
            graph.createNode(node);

            AbstractCell cell =
                new HallwayCell(
                    getCenterX(), getCenterY() - radius - HallwayCell.radius, node, graph);
            Edge edge = new Edge(cell, (AbstractCell) source, graph);
            graph.addCell(cell); // add new hallway cell
            graph.addEdge(edge); // add edge to new hallway cell
            graph.update();

            node.setCell(cell);
            graph.createEdge(edge);
          }
        };
    graph.getRouteController().getMapEditingController().getNodeData(helper, graph, "hall", this);
  }

  private void addEdge() {
    Edge edge = new Edge(this, null, graph);
    graph.addEdge(edge);
    graph.update();
    edge.setTargetPosition(getCenterX(), getCenterY());
    graph.setEdgeInProgress(edge);
  }

  private void editInfo() {
    GraphHelper helper =
        new GraphHelper() {
          @Override
          public void respond(Graph graph, NodeData data) {}

          @Override
          public void respond(Graph graph, NodeData data, IGraphNode source) {
            graph.editNode(((AbstractCell) source).getNode().getId(), data);
            System.out.println("Responding");
            ((AbstractCell) source).checkNodeType(data.nodeType);
          }
        };
    String type = node.getNodeType().isRoomType() ? "room" : "hall";
    graph.getRouteController().getMapEditingController().editNodeData(helper, graph, type, this);
  }

  public void delete() {
    // if node was not newly created, delete it from the database
    if (graph.getCreatedNodes().contains(node)) {
      graph.deleteCreatedNode(node);
    } else {
      graph.deleteNode(node);
    }
    // remove associated edges
    ArrayList<AbstractEdge> cellEdges = new ArrayList<>();
    for (AbstractEdge edge : graph.getEdges()) {
      if (this.equals(edge.getSource()) || this.equals(edge.getTarget())) {
        if (graph.getCreatedEdges().contains(edge)) {
          graph.deleteCreatedEdge(edge);
        } else {
          graph.deleteEdge(edge);
        }
        graph.removeEdge(edge);
      }
    }

    graph.removeCell(this); // remove self from model
    graph.update();
  }

  public void relocate(double x, double y) {
    graphic.relocate(x - radius, y - radius);
  }

  boolean equals(AbstractCell cell) {
    return this.id == cell.getId();
  }

  // setters and getters
  public void setShape(Shape shape) {
    this.shape = shape;
  }

  public Shape getShape() {
    return shape;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public double getEventX() {
    return eventX;
  }

  public double getEventY() {
    return eventY;
  }

  public double getCenterX() {
    return Math.round(graphic.getBoundsInParent().getMinX()) + radius;
  }

  public double getCenterY() {
    return Math.round(graphic.getBoundsInParent().getMinY()) + radius;
  }

  public int getId() {
    return id;
  }

  public AbstractNode getNode() {
    return node;
  }

  // anchors
  public DoubleBinding getXAnchor(Graph graph) {
    final Region graphic = graph.getGraphic(this);
    return graphic.layoutXProperty().add(graphic.widthProperty().divide(2));
  }

  public DoubleBinding getYAnchor(Graph graph) {
    final Region graphic = graph.getGraphic(this);
    return graphic.layoutYProperty().add(graphic.heightProperty().divide(2));
  }

  public boolean isHighlighted() {
    return isHighlighted;
  }

  public Graph getGraph() {
    return graph;
  }
}
