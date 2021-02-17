package fxgraph.graph;

import edu.wpi.teamp.PApp;
import edu.wpi.teamp.UIController;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;

public class PannableCanvas extends Pane {

  private DoubleProperty scaleProperty;
  private int mapWidth;
  private int mapHeight;
  private static final double layoutX = 0;
  private static final double layoutY = 0;

  public PannableCanvas(int floor, String hospitalName, double minZoom) {
    UIController uController = UIController.getUIController();
    setLayoutX(layoutX);
    setLayoutY(layoutY);
    scaleProperty = new SimpleDoubleProperty(minZoom);
    scaleXProperty().bind(scaleProperty);
    scaleYProperty().bind(scaleProperty);
    String map = "";

    if (uController.getColorBlindType() == "aNopia") {
      // ACHROMATOPSIA
      if (hospitalName.equals("Faulkner")) {
        map = PApp.class.getResource("mapsA/FaulknerFloor" + floor + ".png").toExternalForm();
        mapWidth = 2475;
        mapHeight = 1485;
      } else if (hospitalName.equals("Main")) {
        map = PApp.class.getResource("mapsA/mainHospitalFloor" + floor + ".png").toExternalForm();
        mapWidth = 5000;
        mapHeight = 3400;
      }
    } else if (uController.getTheme() == UIController.Theme.Dark) {
      // DARK MODE
      if (hospitalName.equals("Faulkner")) {
        map = PApp.class.getResource("mapsD/FaulknerFloor" + floor + ".png").toExternalForm();
        mapWidth = 2475;
        mapHeight = 1485;
      } else if (hospitalName.equals("Main")) {
        map = PApp.class.getResource("mapsD/mainHospitalFloor" + floor + ".png").toExternalForm();
        mapWidth = 5000;
        mapHeight = 3400;
      }
    } else if (uController.getColorBlindType() == "pNopia"
        || uController.getColorBlindType() == "dNopia") {
      // PROTANOPIA or Deuteranopia
      if (hospitalName.equals("Faulkner")) {
        map = PApp.class.getResource("mapsP/FaulknerFloor" + floor + ".png").toExternalForm();
        mapWidth = 2475;
        mapHeight = 1485;
      } else if (hospitalName.equals("Main")) {
        map = PApp.class.getResource("mapsP/mainHospitalFloor" + floor + ".png").toExternalForm();
        mapWidth = 5000;
        mapHeight = 3400;
      }
    } else if (uController.getColorBlindType() == "tNopia") {
      // TRITANOPIA
      if (hospitalName.equals("Faulkner")) {
        map = PApp.class.getResource("mapsT/FaulknerFloor" + floor + ".png").toExternalForm();
        mapWidth = 2475;
        mapHeight = 1485;
      } else if (hospitalName.equals("Main")) {
        map = PApp.class.getResource("mapsT/mainHospitalFloor" + floor + ".png").toExternalForm();
        mapWidth = 5000;
        mapHeight = 3400;
      }
    } else {
      // ELSE BASE MAPS
      if (hospitalName.equals("Faulkner")) {
        map = PApp.class.getResource("maps/FaulknerFloor" + floor + ".png").toExternalForm();
        mapWidth = 2475;
        mapHeight = 1485;
      } else if (hospitalName.equals("Main")) {
        map = PApp.class.getResource("maps/mainHospitalFloor" + floor + ".png").toExternalForm();
        mapWidth = 5000;
        mapHeight = 3400;
      }
    }

    setPrefSize(mapWidth, mapHeight);
    this.setStyle("-fx-background-image: url(" + map + "); ");
  }

  public double getScale() {
    return scaleProperty.get();
  }

  public void setScale(double scale) {
    scaleProperty.set(scale);
  }

  public DoubleProperty scaleProperty() {
    return scaleProperty;
  }

  public void setPivot(double x, double y) {
    setTranslateX(getTranslateX() - x);
    setTranslateY(getTranslateY() - y);
  }

  /** Mouse drag context used for scene and nodes. */
  public static class DragContext {

    double mouseAnchorX;
    double mouseAnchorY;

    double translateAnchorX;
    double translateAnchorY;
  }

  public int getMapWidth() {
    return mapWidth;
  }

  public int getMapHeight() {
    return mapHeight;
  }
}
