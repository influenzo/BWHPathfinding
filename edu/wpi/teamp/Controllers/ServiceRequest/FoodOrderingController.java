package edu.wpi.teamp.Controllers.ServiceRequest;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamP.APIController;
import edu.wpi.cs3733.d20.teamP.ServiceException;
import edu.wpi.teamp.UIController;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FoodOrderingController {

  UIController uController = UIController.getUIController();
  oServiceRequestController oController = oServiceRequestController.getServiceRequestController();
  BasicInformationController bController;
  RequestReceivedPopupController pController;

  @FXML BasicInformationController basicInformationController;
  @FXML RequestReceivedPopupController requestReceivedPopupController;
  @FXML JFXButton startButton;
  @FXML private JFXButton btnCancel;
  @FXML private JFXButton emergency;
  @FXML private Label error;

  @FXML
  public void initialize() {
    bController = basicInformationController;
    bController.enableSecondary();
    pController = requestReceivedPopupController;

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

        APIController.setAdmin(false); // restricts admin access
        APIController.setAutoClose(true); // auto-closes window after making a request
        APIController.addListener(request -> pController.showPopup());

        error.setVisible(false);
        try {
          uController.cancelTimer();
          APIController.run(-1, -1, -1, -1, null, bController.getLocation().getId(), null);
        } catch (ServiceException ex) {
          ex.printStackTrace();
        }
      } else {
        error.setVisible(true);
      }
    }
  }
}
