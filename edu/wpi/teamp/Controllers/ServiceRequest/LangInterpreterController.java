package edu.wpi.teamp.Controllers.ServiceRequest;

import com.jfoenix.controls.JFXButton;
import edu.wpi.teamp.UIController;
import java.awt.*;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LangInterpreterController {

  UIController uController = UIController.getUIController();
  oServiceRequestController oController = oServiceRequestController.getServiceRequestController();
  BasicInformationController bController;
  RequestReceivedPopupController pController;

  // setting up ids
  @FXML RequestReceivedPopupController requestReceivedPopupController;
  @FXML BasicInformationController basicInformationController;
  @FXML private JFXButton btnCancel;
  @FXML private JFXButton btnSubmit;
  @FXML private Label errorMessage;
  @FXML private JFXButton emergency;

  // setting up language options
  @FXML private JFXButton Spanish;
  @FXML private JFXButton Mandarin;
  @FXML private JFXButton French;
  @FXML private JFXButton German;
  @FXML private JFXButton Hindi;
  @FXML private JFXButton Korean;

  String language = null;

  @FXML
  public void initialize() {
    bController = basicInformationController;
    pController = requestReceivedPopupController;
    errorMessage.setVisible(false);

    Spanish.setOnMouseClicked(e -> language = "Spanish");
    Mandarin.setOnMouseClicked(e -> language = "Mandarin");
    French.setOnMouseClicked(e -> language = "French");
    German.setOnMouseClicked(e -> language = "German");
    Hindi.setOnMouseClicked(e -> language = "Hindi");
    Korean.setOnMouseClicked(e -> language = "Korean");
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
          || language == null) {
        errorMessage.setVisible(true);
      } else {
        oController.addAdditionalField(language);
        oController.createServiceRequest(
            bController.getLocation(), bController.getDescription(), bController.getRequester());
        oController.clearAdditional();
        pController.showPopup();
      }
    }
  }
}
