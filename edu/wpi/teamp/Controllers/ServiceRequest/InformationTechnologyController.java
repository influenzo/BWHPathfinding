package edu.wpi.teamp.Controllers.ServiceRequest;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import edu.wpi.teamp.UIController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class InformationTechnologyController {

  oServiceRequestController oController = oServiceRequestController.getServiceRequestController();
  UIController uController = UIController.getUIController();
  RequestReceivedPopupController pController;
  BasicInformationController bController;
  ArrayList<JFXCheckBox> checkboxes;
  String problemType;

  @FXML private JFXCheckBox computerBtn;
  @FXML private JFXCheckBox emailBtn;
  @FXML private JFXCheckBox networkBtn;
  @FXML private JFXCheckBox phoneBtn;
  @FXML private JFXCheckBox projectorBtn;
  @FXML private JFXCheckBox softwareBtn;
  @FXML private JFXButton btnSubmit;
  @FXML private JFXButton btnBack;
  @FXML private JFXButton emergency;

  @FXML RequestReceivedPopupController requestReceivedPopupController;
  @FXML BasicInformationController basicInformationController;
  @FXML private Label errorMessage;

  @FXML
  public void initialize() {
    bController = basicInformationController;
    pController = requestReceivedPopupController;
    checkboxes =
        new ArrayList<>(
            Arrays.asList(computerBtn, emailBtn, networkBtn, phoneBtn, projectorBtn, softwareBtn));
  }

  @FXML
  public void btnClicked(ActionEvent e) throws IOException {
    if (e.getSource() == btnBack) {
      uController.back();
    } else if (e.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");
    } else if (e.getSource() == btnSubmit) {
      if (bController.getLocation() == null
          || bController.getDescription().isEmpty()
          || bController.getRequester().isEmpty()) {
        errorMessage.setVisible(true);
      } else {
        oController.addAdditionalField(problemType);
        oController.createServiceRequest(
            bController.getLocation(), bController.getDescription(), bController.getRequester());
        oController.clearAdditional();
        pController.showPopup();
      }
    } else {
      for (JFXCheckBox checkbox : checkboxes) {
        if (e.getSource() != checkbox) {
          checkbox.setSelected(false);
          errorMessage.setVisible(false);
        } else {
          problemType = checkbox.getText();
        }
      }
    }
  }
}
