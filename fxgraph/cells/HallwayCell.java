package fxgraph.cells;

import fxgraph.edges.Edge;
import fxgraph.graph.Graph;
import fxgraph.graph.GraphHelper;
import fxgraph.graph.IGraphNode;
import hospital.route.AbstractNode;
import hospital.route.NodeData;
import hospital.route.NodeType;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;

public class HallwayCell extends AbstractCell {

  public static double radius = 6;
  private static String color = "#afa8cc";
  MenuItem addFloorTransitionEdge;

  public HallwayCell(double x, double y, AbstractNode node, Graph graph) {
    super(x, y, radius, color, node, graph, 1);
    createAddRoomOption();
  }

  private void createAddRoomOption() {
    MenuItem addRoom = new MenuItem("Add Room");
    contextMenu.getItems().add(addRoom);
    addRoom.setOnAction((ActionEvent event) -> addRoom());
  }

  private void createAddFloorTransitionEdgeOption() {
    if (addFloorTransitionEdge == null) {
      addFloorTransitionEdge = new MenuItem("Add Floor Transition Edge");
      addFloorTransitionEdge.setOnAction((ActionEvent event) -> addFloorTransitionEdge());
      contextMenu.getItems().add(3, addFloorTransitionEdge);
    }
  }

  private void removeAddFloorTransitionEdgeOption() {
    if (addFloorTransitionEdge != null) {
      contextMenu.getItems().remove(addFloorTransitionEdge);
      addFloorTransitionEdge = null;
    }
  }

  @Override
  public void checkNodeType(NodeType nodeType) {
    if (nodeType == NodeType.ELEV || nodeType == NodeType.STAI) {
      createAddFloorTransitionEdgeOption();
    } else removeAddFloorTransitionEdgeOption();
  }

  private void addRoom() {
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
                new RoomCell(getCenterX(), getCenterY() - radius - RoomCell.radius, node, graph);
            Edge edge = new Edge(cell, (AbstractCell) source, graph);
            graph.addCell(cell); // add new room cell
            graph.addEdge(edge); // add edge to new room cell
            graph.update();

            node.setCell(cell);
            graph.createEdge(edge);
          }
        };
    graph.getRouteController().getMapEditingController().getNodeData(helper, graph, "room", this);
  }

  private void addFloorTransitionEdge() {
    graph.getRouteController().getMapEditingController().addFloorTransitionEdge(this);
  }
}
