package fxgraph.graph;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/** Listeners for making the scene's viewport draggable and zoomable */
public class ViewportGestures {

  private final DoubleProperty zoomSpeedProperty = new SimpleDoubleProperty(1.2d);
  private final DoubleProperty maxScaleProperty = new SimpleDoubleProperty(1d);
  private final DoubleProperty minScaleProperty = new SimpleDoubleProperty(1d);
  private boolean shiftOnPress = false;

  private final fxgraph.graph.PannableCanvas.DragContext sceneDragContext =
      new fxgraph.graph.PannableCanvas.DragContext();

  PannableCanvas canvas;
  Graph graph;

  public ViewportGestures(PannableCanvas canvas, Graph graph) {
    this.canvas = canvas;
    this.graph = graph;
  }

  public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
    return onMousePressedEventHandler;
  }

  public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
    return onMouseDraggedEventHandler;
  }

  public EventHandler<ScrollEvent> getOnScrollEventHandler() {
    return onScrollEventHandler;
  }

  public void setZoomBounds(double minScale, double maxScale) {
    minScaleProperty.set(minScale);
    maxScaleProperty.set(maxScale);
  }

  private final EventHandler<MouseEvent> onMousePressedEventHandler =
      new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
          // establish starting reference point for panning
          graph.setEventCoordinates(event.getX(), event.getY());

          if (event.isPrimaryButtonDown()) {
            shiftOnPress = graph.getShiftPressed();
            graph.getContextMenu().hide();
            sceneDragContext.mouseAnchorX = event.getSceneX();
            sceneDragContext.mouseAnchorY = event.getSceneY();

            sceneDragContext.translateAnchorX = canvas.getTranslateX();
            sceneDragContext.translateAnchorY = canvas.getTranslateY();

            if (graph.canEdit()
                && !graph.getDraggingNode()
                && !graph.getShiftPressed()) { // selection box
              graph.startBoxSelection();
            }
          } else if (graph.canEdit()
              && event.isSecondaryButtonDown()
              && !graph.getContextMenuOpen()) {
            graph.openContextMenu(event.getScreenX(), event.getScreenY());
          }
        }
      };

  private final EventHandler<MouseEvent> onMouseReleasedEventHandler =
      new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
          if (graph.getBoxSelecting()) {
            graph.endBoxSelection();
          } else if (event.getButton().equals(MouseButton.PRIMARY)
              && !graph.canEdit()
              && !shiftOnPress) {
            graph
                .getRouteController()
                .navigationClick(event.getX(), event.getY(), graph.getFloor());
          }
        }
      };

  public EventHandler<MouseEvent> getOnMouseReleasedEventHandler() {
    return onMouseReleasedEventHandler;
  }

  private final EventHandler<MouseEvent> onMouseDraggedEventHandler =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {

          if (event.isPrimaryButtonDown() && !graph.getDraggingNode()) {
            if (graph.getShiftPressed()) { // canvas panning
              double translateX =
                  sceneDragContext.translateAnchorX
                      + event.getSceneX()
                      - sceneDragContext.mouseAnchorX;
              double translateY =
                  sceneDragContext.translateAnchorY
                      + event.getSceneY()
                      - sceneDragContext.mouseAnchorY;
              canvas.setTranslateX(translateX);
              canvas.setTranslateY(translateY);
              event.consume();
            } else if (graph.canEdit()) { // selection box
              graph.updateBoxSelection(event.getX(), event.getY());
            }
          }
        }
      };

  private final EventHandler<MouseEvent> onMouseMovedEventHandler =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          if (graph.getEdgeInProgress() != null) {
            graph.getEdgeInProgress().setTargetPosition(event.getX(), event.getY());
          }
        }
      };

  public EventHandler<MouseEvent> getOnMouseMovedEventHandler() {
    return onMouseMovedEventHandler;
  }

  /** Mouse wheel handler: zoom to pivot point */
  private final EventHandler<ScrollEvent> onScrollEventHandler =
      new EventHandler<ScrollEvent>() {

        @Override
        public void handle(ScrollEvent event) {
          double scale = canvas.getScale(); // currently we only use Y, same value is used for X
          final double oldScale = scale;

          if (event.getDeltaY() < 0) {
            scale /= getZoomSpeed();
          } else if (event.getDeltaY() > 0) {
            scale *= getZoomSpeed();
          }

          scale = clamp(scale, minScaleProperty.get(), maxScaleProperty.get());
          final double f = (scale / oldScale) - 1;

          // maxX = right overhang, maxY = lower overhang
          final double maxX =
              canvas.getBoundsInParent().getMaxX()
                  - canvas.localToParent(canvas.getPrefWidth(), canvas.getPrefHeight()).getX();
          final double maxY =
              canvas.getBoundsInParent().getMaxY()
                  - canvas.localToParent(canvas.getPrefWidth(), canvas.getPrefHeight()).getY();

          // minX = left overhang, minY = upper overhang
          final double minX =
              canvas.localToParent(0, 0).getX() - canvas.getBoundsInParent().getMinX();
          final double minY =
              canvas.localToParent(0, 0).getY() - canvas.getBoundsInParent().getMinY();

          // adding the overhangs together, as we only consider the width of canvas itself
          final double subX = maxX + minX;
          final double subY = maxY + minY;

          // subtracting the overall overhang from the width and only the left and upper overhang
          // from the upper left point
          final double dx =
              (event.getSceneX()
                  - ((canvas.getBoundsInParent().getWidth() - subX) / 2
                      + (canvas.getBoundsInParent().getMinX() + minX)));
          final double dy =
              (event.getSceneY()
                  - ((canvas.getBoundsInParent().getHeight() - subY) / 2
                      + (canvas.getBoundsInParent().getMinY() + minY)));

          canvas.setScale(scale);

          // note: pivot value must be untransformed, i. e. without scaling
          canvas.setPivot(f * dx, f * dy);

          event.consume();
        }
      };

  public static double clamp(double value, double min, double max) {
    if (Double.compare(value, min) < 0) {
      return min;
    }

    if (Double.compare(value, max) > 0) {
      return max;
    }

    return value;
  }

  public double getMinScale() {
    return minScaleProperty.get();
  }

  public void setMinScale(double minScale) {
    minScaleProperty.set(minScale);
  }

  public DoubleProperty minScaleProperty() {
    return minScaleProperty;
  }

  public double getMaxScale() {
    return maxScaleProperty.get();
  }

  public DoubleProperty maxScaleProperty() {
    return maxScaleProperty;
  }

  public void setMaxScale(double maxScale) {
    maxScaleProperty.set(maxScale);
  }

  public double getZoomSpeed() {
    return zoomSpeedProperty.get();
  }

  public DoubleProperty zoomSpeedProperty() {
    return zoomSpeedProperty;
  }

  public void setZoomSpeed(double zoomSpeed) {
    zoomSpeedProperty.set(zoomSpeed);
  }
}
