package edu.wpi.teamp.Controllers;

import edu.wpi.teamp.UIController;
import hospital.exceptions.NullHospitalException;
import hospital.route.AbstractNode;
import hospital.route.RouteController;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class ExitPathController {
  private UIController uController = UIController.getUIController();
  private RouteController rController = uController.getRouteController();
  AbstractNode homeNode;

  @FXML private Button homeBtn;
  @FXML private Pane mapPane1;
  @FXML private Pane mapPane2;
  @FXML private Button btnDark;
  @FXML private GridPane gridPane;

  public void initialize() throws NullHospitalException {
    homeNode = rController.getHomeNode();

    uController.generateMaps(false);
    uController.addMap(
        mapPane1,
        uController.getHospital().getRouteController().getHomeNode().getFloor(),
        uController.getHospital().getName());
    uController.addMap(mapPane2, 1, uController.getHospital().getName());
    rController.getMap(homeNode.getFloor()).setScale(0.4);
    uController.moveMapCenter(
        uController.getHospital().getRouteController().getHomeNode().getFloor(),
        (uController.getPrimaryScreenBounds().getWidth() / 4),
        (uController.getPrimaryScreenBounds().getHeight() - 90) / 2);
    uController.moveMapCenter(
        uController.getHospital().getRouteController().getHomeNode().getFloor(),
        (3 * uController.getPrimaryScreenBounds().getWidth() / 4),
        (uController.getPrimaryScreenBounds().getHeight() - 90) / 2);
    rController.getMap(1).setScale(0.4);
    rController.getEmergencyExit();
  }

  @FXML
  public void btnClicked(ActionEvent e) throws IOException, NullHospitalException {
    if (e.getSource() == homeBtn) {
      rController.getMap(homeNode.getFloor()).setScale(1);
      rController.getMap(1).setScale(1);
      uController.home();
    } else if (e.getSource() == btnDark) {
      gridPane.getStylesheets().clear();
      gridPane.getStylesheets().add("/edu/wpi/teamp/light-mode/basicStructure.css");
    }
  }
}
