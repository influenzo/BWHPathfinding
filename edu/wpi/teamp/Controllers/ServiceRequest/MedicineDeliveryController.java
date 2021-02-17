package edu.wpi.teamp.Controllers.ServiceRequest;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.teamp.UIController;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MedicineDeliveryController {

  RequestReceivedPopupController pController;

  @FXML RequestReceivedPopupController requestReceivedPopupController;

  UIController uController = UIController.getUIController();
  oServiceRequestController oController = oServiceRequestController.getServiceRequestController();
  BasicInformationController bController;
  @FXML private JFXButton emergency;

  @FXML private JFXButton btnCancel;
  @FXML private JFXButton btnSubmit;
  @FXML BasicInformationController basicInformationController;
  @FXML private JFXTextField medicineName;
  @FXML private JFXTextField amount;
  @FXML private JFXDatePicker deliveryDate;
  @FXML private Label errorMessage;

  @FXML
  public void initialize() {

    bController = basicInformationController;
  }

  public void btnClicked(ActionEvent event) throws IOException {
    if (event.getSource() == btnCancel) {
      oController.clearAdditional();
      uController.back();
    } else if (event.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");
    } else if (event.getSource() == btnSubmit) {
      if (bController.getLocation() == null
          || bController.getDescription().isEmpty()
          || bController.getRequester().isEmpty()
          || medicineName.getText().equals("")
          || amount.getText().equals("")
          || deliveryDate.getValue() == null) {
        errorMessage.setVisible(true);
      } else {
        oController.addAdditionalField(medicineName.getText());
        oController.addAdditionalField(amount.getText());
        oController.addAdditionalField(deliveryDate.getValue().toString());
        oController.createServiceRequest(
            bController.getLocation(), bController.getDescription(), bController.getRequester());
        oController.clearAdditional();
        requestReceivedPopupController.showPopup();
      }
    }
  }
}
