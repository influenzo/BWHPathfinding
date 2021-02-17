package edu.wpi.teamp.Controllers.ServiceRequest;

import com.jfoenix.controls.JFXButton;
import edu.wpi.teamp.UIController;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SanitationController {

  UIController uController = UIController.getUIController();
  oServiceRequestController oController = oServiceRequestController.getServiceRequestController();
  BasicInformationController bController;
  RequestReceivedPopupController pController;

  // setting up ids
  @FXML RequestReceivedPopupController requestReceivedPopupController;
  @FXML BasicInformationController basicInformationController;
  @FXML private JFXButton btnCancel;
  @FXML private JFXButton btnSubmit;
  @FXML private JFXButton btnSubmit1;

  @FXML private Label errorMessage;
  @FXML private JFXButton emergency;

  // setting up service options
  @FXML private JFXButton missed;
  @FXML private JFXButton overflowing;
  @FXML private JFXButton bulky;
  @FXML private JFXButton spill;
  @FXML private JFXButton hazardous;
  @FXML private JFXButton misc;

  String sanitation = null;

  @FXML
  public void initialize() {
    bController = basicInformationController;
    pController = requestReceivedPopupController;
    missed.setOnMouseClicked(e -> sanitation = "Missed");
    overflowing.setOnMouseClicked(e -> sanitation = "Overflowing");
    bulky.setOnMouseClicked(e -> sanitation = "Bulky");
    spill.setOnMouseClicked(e -> sanitation = "Spill");
    hazardous.setOnMouseClicked(e -> sanitation = "Hazardous");
    misc.setOnMouseClicked(e -> sanitation = "Miscellaneous");
  }

  @FXML
  private void btnClicked(ActionEvent e) throws IOException {
    if (e.getSource() == btnCancel) {
      oController.clearAdditional();
      uController.back();
    } else if (e.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");
    } else if (e.getSource() == btnSubmit || e.getSource() == btnSubmit1) {
      if (bController.getLocation() == null
          || bController.getDescription().isEmpty()
          || bController.getRequester().isEmpty()
          || sanitation == null) {
        errorMessage.setVisible(true);
      } else {
        oController.addAdditionalField(sanitation);
        oController.createServiceRequest(
            bController.getLocation(), bController.getDescription(), bController.getRequester());
        oController.clearAdditional();
        pController.showPopup();
      }
    }
  }
}
