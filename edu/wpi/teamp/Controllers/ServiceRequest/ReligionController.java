package edu.wpi.teamp.Controllers.ServiceRequest;

import com.jfoenix.controls.JFXButton;
import edu.wpi.teamp.UIController;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ReligionController {

  UIController uController = UIController.getUIController();
  oServiceRequestController oController = oServiceRequestController.getServiceRequestController();
  BasicInformationController bController;
  RequestReceivedPopupController pController;

  // setting up ids
  @FXML BasicInformationController basicInformationController;
  @FXML RequestReceivedPopupController requestReceivedPopupController;
  @FXML private JFXButton btnCancel;
  @FXML private JFXButton btnSubmit;
  @FXML private Label errorMessage;
  @FXML private JFXButton emergency;

  // setting up religion options
  @FXML private JFXButton christianityBtn;
  @FXML private JFXButton bahaiBtn;
  @FXML private JFXButton buddhismBtn;
  @FXML private JFXButton hinduismBtn;
  @FXML private JFXButton islamBtn;
  @FXML private JFXButton judaismBtn;

  String religion = null;

  @FXML
  public void initialize() {
    bController = basicInformationController;
    pController = requestReceivedPopupController;

    christianityBtn.setOnMouseClicked(e -> religion = "Christianity");
    bahaiBtn.setOnMouseClicked(e -> religion = "Bahai");
    buddhismBtn.setOnMouseClicked(e -> religion = "Buddhism");
    hinduismBtn.setOnMouseClicked(e -> religion = "Hinduism");
    islamBtn.setOnMouseClicked(e -> religion = "Islam");
    judaismBtn.setOnMouseClicked(e -> religion = "Judaism");
  }

  @FXML
  private void btnClicked(ActionEvent e) throws IOException {
    if (e.getSource() == btnCancel) {
      oController.clearAdditional();
      uController.back();
    } else if (e.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");
    } else if (e.getSource() == btnSubmit) {
      if (bController.getLocation() == null
          || bController.getDescription().isEmpty()
          || bController.getRequester().isEmpty()
          || religion == null) {
        errorMessage.setVisible(true);
      } else {
        oController.addAdditionalField(religion);
        oController.createServiceRequest(
            bController.getLocation(), bController.getDescription(), bController.getRequester());
        oController.clearAdditional();
        pController.showPopup();
      }
    }
  }
}
