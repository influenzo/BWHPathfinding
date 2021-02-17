package edu.wpi.teamp.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleNode;
import edu.wpi.teamp.UIController;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

public class AdminToolsController {

  UIController uController = UIController.getUIController();
  AnalyticsController aController;
  EmployeeDatabaseController dController;
  MapEditingController mController;

  @FXML AnalyticsController analyticsPaneController;
  @FXML EmployeeDatabaseController employeePaneController;
  @FXML MapEditingController mapEditingPaneController;
  @FXML JFXButton emergency;

  @FXML JFXToggleNode mapEditing;
  @FXML JFXToggleNode analyticsBtn;
  @FXML JFXToggleNode employeeBtn;
  @FXML JFXToggleNode settingsBtn;
  @FXML JFXButton backBtn;
  @FXML AnchorPane mapEditingPane;
  @FXML AnchorPane analyticsPane;
  @FXML AnchorPane employeePane;
  @FXML AnchorPane settingsPane;

  ToggleGroup tg = new ToggleGroup();

  @FXML
  public void initialize() {
    aController = analyticsPaneController;
    dController = employeePaneController;
    mController = mapEditingPaneController;

    mapEditing.setSelected(false);
    mapEditingPane.setVisible(false);
    analyticsPane.setVisible(false);
    employeePane.setVisible(false);
    settingsPane.setVisible(true);

    mapEditing.setToggleGroup(tg);
    analyticsBtn.setToggleGroup(tg);
    employeeBtn.setToggleGroup(tg);
    settingsBtn.setToggleGroup(tg);

    settingsBtn.setSelected(true);
  }

  @FXML
  public void buttonClicked(ActionEvent e) throws IOException {
    if (e.getSource().equals(backBtn)) {
      uController.home();
    } else if (e.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");

    } else if (e.getSource().equals(mapEditing)) {
      mapEditing.setSelected(true);
      mapEditingPane.setVisible(true);
      analyticsPane.setVisible(false);
      employeePane.setVisible(false);
      settingsPane.setVisible(false);
      mController.initialize();
    } else if (e.getSource().equals(analyticsBtn)) {
      analyticsBtn.setSelected(true);
      analyticsPane.setVisible(true);
      mapEditingPane.setVisible(false);
      employeePane.setVisible(false);
      settingsPane.setVisible(false);
      aController.updateData();
    } else if (e.getSource().equals(employeeBtn)) {
      employeeBtn.setSelected(true);
      analyticsPane.setVisible(false);
      mapEditingPane.setVisible(false);
      settingsPane.setVisible(false);
      employeePane.setVisible(true);
      dController.updateData();
    } else if (e.getSource().equals(settingsBtn)) {
      settingsBtn.setSelected(true);
      settingsPane.setVisible(true);
      analyticsPane.setVisible(false);
      mapEditingPane.setVisible(false);
      employeePane.setVisible(false);
    }
  }
}
