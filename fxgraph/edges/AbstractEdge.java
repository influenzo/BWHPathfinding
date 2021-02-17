package fxgraph.edges;

import fxgraph.cells.AbstractCell;
import fxgraph.cells.HallwayCell;
import fxgraph.graph.Graph;
import fxgraph.graph.GraphHelper;
import fxgraph.graph.IGraphNode;
import hospital.HospitalController;
import hospital.route.AbstractNode;
import hospital.route.NodeData;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.binding.DoubleBinding;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public abstract class AbstractEdge implements IGraphNode {

  private AbstractCell source;
  private AbstractCell target;
  public Graph graph;
  private long id; // hashed id from source and target id's
  private Pane graphic;
  public final ContextMenu contextMenu = new ContextMenu();
  private double centerX; // centerX
  private double centerY; // centerY
  private HospitalController hController = HospitalController.getHospitalController();
  private String color = "#3a1ea6";
  // for animation
  private Timer pathTimer;

  public AbstractEdge(AbstractCell source, AbstractCell target, Graph graph) {
    this.source = source;
    this.target = target;
    this.graph = graph;
    if (target != null) {
      this.id = hash(source.getId(), target.getId());
    }
  }

  public static long hash(int i1, int i2) {
    long[] i = {i1, i2};
    Arrays.sort(i);
    return ((i[0] + i[1]) * (i[0] + i[1] + 1)) / 2 - i[1];
  }

  public Region getGraphic(Graph graph) {
    this.graph = graph;

    Line line = new Line();
    line.setStrokeWidth(5);
    line.setStyle("-fx-stroke: " + color);
    line.strokeLineCapProperty().set(StrokeLineCap.ROUND);
    addContextMenu(line);
    createDeleteOption();
    createAddHallwayOption();

    final DoubleBinding sourceX = source.getXAnchor(graph);
    final DoubleBinding sourceY = source.getYAnchor(graph);
    line.startXProperty().bind(sourceX);
    line.startYProperty().bind(sourceY);
    if (target != null) {
      final DoubleBinding targetX = target.getXAnchor(graph);
      final DoubleBinding targetY = target.getYAnchor(graph);
      line.endXProperty().bind(targetX);
      line.endYProperty().bind(targetY);
    }

    final Pane pane = new Pane(line);
    pane.viewOrderProperty().set(10);
    pane.setPickOnBounds(false); // allows transparent, surrounding pane to be clicked through

    graphic = pane;
    return pane;
  }

  public void setTargetPosition(double x, double y) {
    Line line = ((Line) graphic.getChildren().get(0));
    line.endXProperty().set(x);
    line.endYProperty().set(y);
  }

  public void setTarget(AbstractCell target) {
    graph.setEdgeInProgress(null);
    // delete if source = target or adding room to room
    if (source.getId() == target.getId()
        || (source.getNode().getNodeType().isRoomType()
            && target.getNode().getNodeType().isRoomType())) {
      graph.removeEdge(this); // remove self from model
      graph.update();
      return;
    }

    this.target = target;
    long hash = hash(source.getId(), target.getId());
    if (graph.edgeExists(hash)) { // delete if edge exists
      graph.removeEdge(this); // remove self from model
      graph.update();
      return;
    }
    id = hash;

    Line line = ((Line) graphic.getChildren().get(0));
    final DoubleBinding targetX = target.getXAnchor(graph);
    final DoubleBinding targetY = target.getYAnchor(graph);
    line.endXProperty().bind(targetX);
    line.endYProperty().bind(targetY);

    graph.createEdge(this);
  }

  public void addContextMenu(Node view) {
    view.setOnMousePressed(
        (MouseEvent event) -> {
          if (event.isSecondaryButtonDown()) {
            Node node = (Node) event.getSource();
            Bounds bounds = node.getBoundsInParent();
            centerX = bounds.getMaxX() - bounds.getWidth() / 2;
            centerY = bounds.getMaxY() - bounds.getHeight() / 2;
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
    MenuItem addIntersection = new MenuItem("Add Hallway");
    contextMenu.getItems().addAll(addIntersection);
    addIntersection.setOnAction((ActionEvent event) -> addHallway());
  }

  private void createDeleteOption() {
    MenuItem delete = new MenuItem("Delete");
    contextMenu.getItems().addAll(delete);
    delete.setOnAction((ActionEvent event) -> delete());
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

            AbstractEdge sourceEdge = (Edge) source;
            AbstractCell cell =
                new HallwayCell(sourceEdge.getCenterX(), sourceEdge.getCenterY(), node, graph);
            Edge edge1 = new Edge(cell, sourceEdge.getSource(), graph);
            Edge edge2 = new Edge(cell, sourceEdge.getTarget(), graph);

            graph.addCell(cell); // add new hallway cell
            graph.addEdge(edge1); // add edges to new hallway cell
            graph.addEdge(edge2);
            sourceEdge.delete();

            node.setCell(cell);
            graph.createEdge(edge1);
            graph.createEdge(edge2);
          }
        };
    graph.getRouteController().getMapEditingController().getNodeData(helper, graph, "hall", this);
  }

  // editor deletion
  private void delete() {
    // if node was not newly created, delete it from the database
    if (graph.getCreatedEdges().contains(this)) {
      graph.deleteCreatedEdge(this);
    } else {
      graph.deleteEdge(this); // to be deleted
    }
    graph.removeEdge(this); // remove self from model
    graph.update();
  }

  public void highlight(String color) {
    // System.out.println(graphic.getChildren().get(0));
    // make it a dotted line
    ((Line) graphic.getChildren().get(0)).getStrokeDashArray().addAll(20d, 20d, 20d, 20d);
    ((Line) graphic.getChildren().get(0)).setStrokeWidth(5);
    // set color
    graphic.getChildren().get(0).setStyle("-fx-stroke: " + color + ";");
    // animation timer
    pathTimer = new Timer();
    pathTimer.scheduleAtFixedRate(
        new TimerTask() {
          @Override
          public void run() {
            javafx.application.Platform.runLater(
                () ->
                    ((Line) graphic.getChildren().get(0))
                        .setStrokeDashOffset(
                            ((Line) graphic.getChildren().get(0)).getStrokeDashOffset() - 1));
          }
        },
        0,
        25);
    //    graphic
    //        .getChildren()
    //        .get(0)
    //        .setStyle(
    //            "-fx-stroke: "
    //                + color
    //                + ";-fx-stroke-dash-array: 20;"
    //                + "fx-stroke-dash-offset: 1000;");
    //    graphic.setOpacity(1);
  }

  public void unhighlight() {
    graphic.getChildren().get(0).setStyle("-fx-stroke: " + color);
  }

  public void show() {
    graphic.setOpacity(1);
  };

  public void hide() {
    graphic.setOpacity(0);
  }

  public void disable() {
    graphic.setDisable(true);
  }

  public void enable() {
    graphic.setDisable(false);
  }

  // getters and setters
  public long getId() {
    return id;
  }

  public AbstractCell getSource() {
    return source;
  }

  public AbstractCell getTarget() {
    return target;
  }

  public double getCenterX() {
    return centerX;
  }

  public double getCenterY() {
    return centerY;
  }

  public boolean equals(AbstractEdge edge) {
    return edge != null && id == edge.getId();
  }
}
