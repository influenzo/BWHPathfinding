package fxgraph.graph;

import fxgraph.cells.AbstractCell;
import fxgraph.cells.HallwayCell;
import hospital.route.NodeType;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class NodeGestures {

  final DragContext dragContext = new DragContext();
  final Graph graph;

  public NodeGestures(Graph graph) {
    this.graph = graph;
  }

  public void makeDraggable(final Node node) {
    node.setOnMousePressed(onMousePressedEventHandler);
    node.setOnMouseDragged(onMouseDraggedEventHandler);
    node.setOnMouseReleased(onMouseReleasedEventHandler);
  }

  final EventHandler<MouseEvent> onMousePressedEventHandler =
      new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
          final Node node = (Node) event.getSource();

          if (graph.canEdit()) {
            graph.setDraggingNode(true);

            // if edge creation was in progress and a node was clicked
            if (graph.getEdgeInProgress() != null) {
              AbstractCell targetCell = null;
              for (AbstractCell cell : graph.getCells()) {
                if (cell.getGraphic().equals((Pane) node)) {
                  targetCell = cell;
                  break;
                }
              }
              graph.getEdgeInProgress().setTarget(targetCell);
            }

            final double scale = graph.getScale();

            dragContext.x = node.getBoundsInParent().getMinX() * scale - event.getScreenX();
            dragContext.y = node.getBoundsInParent().getMinY() * scale - event.getScreenY();
          } else {
            for (AbstractCell cell : graph.getCells()) {
              if (cell.getGraphic().equals((Pane) node)) {
                if (cell.getClass().equals(HallwayCell.class)
                    && (cell.getNode().getNodeType() == NodeType.STAI
                        || cell.getNode().getNodeType() == NodeType.ELEV)) {
                  if (!cell.isHighlighted()) {
                    graph.getRouteController().getMapEditingController().selectCell(cell);
                  } else {
                    graph.getRouteController().getMapEditingController().deselectCell(cell);
                  }
                }
              }
            }
          }
        }
      };

  final EventHandler<MouseEvent> onMouseDraggedEventHandler =
      new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
          if (event.getButton() == MouseButton.PRIMARY && graph.canEdit()) {
            final Node node = (Node) event.getSource();
            final Circle shape = (Circle) ((Pane) node).getChildren().get(0);

            double offsetX = event.getScreenX() + dragContext.x;
            double offsetY = event.getScreenY() + dragContext.y;

            // adjust the offset in case we are zoomed
            final double scale = graph.getScale();

            offsetX /= scale;
            offsetY /= scale;

            node.relocate(offsetX, offsetY); // relocate to mouse cursor (possible collision)

            List<AbstractCell> cells = graph.getCells();
            List<Shape> intersects = new ArrayList<>();
            List<Circle> cellShapes = new ArrayList<>();
            // check cells for collision
            for (AbstractCell c : cells) {
              Circle cellShape = (Circle) c.getShape();
              Shape intersect = Shape.intersect(shape, cellShape);
              if (!shape.equals(cellShape) && intersect.getBoundsInLocal().getWidth() != -1) {
                intersects.add(intersect);
                cellShapes.add(cellShape);
              }
            }

            if (intersects.size() > 0) {
              Circle cellShape = new Circle();
              // find largest collision
              if (intersects.size() > 1) {
                double area = 0;
                for (Shape i : intersects) {
                  double newArea =
                      i.getBoundsInLocal().getWidth()
                          * i.getBoundsInLocal().getHeight(); // collision area
                  if (newArea > area) {
                    area = newArea;
                    cellShape = cellShapes.get(intersects.indexOf(i));
                  }
                }
              } else {
                cellShape = cellShapes.get(0);
              }
              cellShapes.remove(cellShape);

              Bounds shapeBounds = shape.getParent().getBoundsInParent(); // node bounds
              double shapeRadius = shape.getRadius(); // node radius
              double shapeX = shapeBounds.getMinX() + shapeRadius; // node center X
              double shapeY = shapeBounds.getMinY() + shapeRadius; // node center Y
              Bounds cellBounds =
                  cellShape.getParent().getBoundsInParent(); // intersection cell bounds
              double cellRadius = cellShape.getRadius(); // intersection cell radius
              double cellX = cellBounds.getMinX() + cellRadius; // intersection cell center X
              double cellY = cellBounds.getMinY() + cellRadius; // intersection cell center Y
              double distance = 9999;
              double angle = 0;
              // find nearest circular edge point to get angle to place node
              for (double a = 0; a < 2 * Math.PI; a += 0.1) {
                double pointX = cellX + Math.sin(a) * cellRadius;
                double pointY = cellY + Math.cos(a) * cellRadius;
                double newDistance = getDistance(pointX, pointY, shapeX, shapeY);
                if (newDistance < distance) {
                  distance = newDistance;
                  angle = a;
                }
              }
              node.relocate(
                  (cellX + Math.sin(angle) * (cellRadius + shapeRadius)) - shapeRadius,
                  (cellY + Math.cos(angle) * (cellRadius + shapeRadius)) - shapeRadius);

              // check for intersections from more surrounding nodes
              for (Shape s : cellShapes) {
                Shape intersect = Shape.intersect(shape, s);
                if (intersect.getBoundsInLocal().getWidth() != -1) {
                  double newAngle;
                  // nudge shape 0.01 radians in each direction until intersection is resolved
                  for (double a = 0; a < Math.PI; a += 0.01) {
                    for (double b : new double[] {a, -a}) {
                      newAngle = angle + b;
                      node.relocate(
                          (cellX + Math.sin(newAngle) * (cellRadius + shapeRadius)) - shapeRadius,
                          (cellY + Math.cos(newAngle) * (cellRadius + shapeRadius)) - shapeRadius);
                      intersect = Shape.intersect(shape, s);
                      if (intersect.getBoundsInLocal().getWidth() == -1) {
                        return;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      };

  public double getDistance(double sx, double sy, double tx, double ty) {
    return Math.sqrt(Math.pow(sx - tx, 2) + Math.pow(sy - ty, 2));
  }

  final EventHandler<MouseEvent> onMouseReleasedEventHandler =
      new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
          graph.setDraggingNode(false);
        }
      };

  public static class DragContext {
    double x;
    double y;
  }
}
