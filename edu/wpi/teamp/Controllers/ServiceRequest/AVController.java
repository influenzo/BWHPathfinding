package edu.wpi.teamp.Controllers.ServiceRequest;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import edu.wpi.teamp.UIController;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class AVController {

  UIController uController = UIController.getUIController();
  oServiceRequestController oController = oServiceRequestController.getServiceRequestController();
  BasicInformationController bController;
  RequestReceivedPopupController pController;

  // setting up ids
  @FXML BasicInformationController basicInformationController;
  @FXML private JFXButton btnCancel;
  @FXML private JFXButton btnSubmit;
  @FXML private Label errorMessage;
  @FXML private StackPane requestReceivedPane;
  @FXML private JFXButton emergency;

  // setting up av options
  @FXML private JFXCheckBox boxMic;
  @FXML private JFXCheckBox boxSpeaker;
  @FXML private JFXCheckBox boxCamera;
  @FXML private JFXCheckBox boxLight;
  @FXML private JFXCheckBox boxMixer;
  @FXML private JFXCheckBox boxMicStand;
  @FXML private JFXCheckBox boxSpeakerStand;
  @FXML private JFXCheckBox boxCameraStand;
  @FXML private Label selectMessage;
  @FXML RequestReceivedPopupController requestReceivedPopupController;

  @FXML
  public void initialize() {
    bController = basicInformationController;
    pController = requestReceivedPopupController;
    errorMessage.setVisible(false);
  }

  @FXML
  public void btnClicked(ActionEvent e) throws IOException {
    if (e.getSource() == btnCancel) {
      oController.clearAdditional();
      uController.back();
    } else if (e.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");
    } else if (e.getSource() == btnSubmit) {
      if (bController.getLocation() == null
          || bController.getDescription().isEmpty()
          || bController.getRequester().isEmpty()) {
        errorMessage.setVisible(true);
      } else {
        int numSelected = 0;
        if (boxMic.isSelected()) {
          oController.addAdditionalField("Microphone");
          numSelected++;
        }
        if (boxSpeaker.isSelected()) {
          oController.addAdditionalField("Speaker");
          numSelected++;
        }
        if (boxCamera.isSelected()) {
          oController.addAdditionalField("Camera");
          numSelected++;
        }
        if (boxLight.isSelected()) {
          oController.addAdditionalField("Light Box");
          numSelected++;
        }
        if (boxMixer.isSelected()) {
          oController.addAdditionalField("Audio Mixer");
          numSelected++;
        }
        if (boxMicStand.isSelected()) {
          oController.addAdditionalField("Microphone Stand");
          numSelected++;
        }
        if (boxSpeakerStand.isSelected()) {
          oController.addAdditionalField("Speaker Stand");
          numSelected++;
        }
        if (boxCameraStand.isSelected()) {
          oController.addAdditionalField("Camera Stand");
          numSelected++;
        }
        if (!(numSelected > 0)) {
          selectMessage.setVisible(true);
        } else {
          oController.createServiceRequest(
              bController.getLocation(), bController.getDescription(), bController.getRequester());
          oController.clearAdditional();
          pController.showPopup();
        }
      }
    }
  }
}
