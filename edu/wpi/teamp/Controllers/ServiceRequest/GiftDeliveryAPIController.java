package edu.wpi.teamp.Controllers.ServiceRequest;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamL.GiftServiceRequest;
import edu.wpi.teamp.UIController;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GiftDeliveryAPIController {

  UIController uController = UIController.getUIController();
  oServiceRequestController oController = oServiceRequestController.getServiceRequestController();
  BasicInformationController bController;

  @FXML BasicInformationController basicInformationController;
  @FXML private JFXButton btnCancel;
  @FXML JFXButton startButton;
  @FXML private JFXButton emergency;
  @FXML private Label error;

  @FXML
  public void initialize() {
    bController = basicInformationController;
    bController.enableSecondary();

    error.setVisible(false);
  }

  @FXML
  private void btnClicked(ActionEvent e) throws IOException {
    if (e.getSource() == btnCancel) {
      oController.clearAdditional();
      uController.back();
    } else if (e.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");
    } else if (e.getSource() == startButton) {
      if (bController.getLocation() != null) {
        error.setVisible(false);
        try {
          uController.cancelTimer();
          GiftServiceRequest gController = new GiftServiceRequest();
          gController.run(0, 0, 1280, 720, null, bController.getLocation().getId(), null);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      } else {
        error.setVisible(true);
      }
    }
  }
}
