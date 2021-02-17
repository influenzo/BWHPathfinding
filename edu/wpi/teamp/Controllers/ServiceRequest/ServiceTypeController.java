package edu.wpi.teamp.Controllers.ServiceRequest;

import com.jfoenix.controls.JFXButton;
import edu.wpi.teamp.UIController;
import hospital.service.ServiceType;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class ServiceTypeController {

  UIController uController = UIController.getUIController();
  oServiceRequestController oController = oServiceRequestController.getServiceRequestController();

  @FXML private JFXButton btnHome;
  @FXML private JFXButton btnInterpreter;
  @FXML private JFXButton btnSanitation;
  @FXML private JFXButton btnInternalTrans;
  @FXML private JFXButton btnReligious;
  @FXML private JFXButton btnInfoTech;
  @FXML private JFXButton btnFlorist;
  @FXML private JFXButton btnMedicine;
  @FXML private JFXButton btnAudioVisual;
  @FXML private JFXButton btnGiftShop;
  @FXML private JFXButton btnFood;
  @FXML private JFXButton btnAppointment;
  @FXML private JFXButton btnHome1;
  @FXML private JFXButton btnInterpreter1;
  @FXML private JFXButton btnSanitation1;
  @FXML private JFXButton btnInternalTrans1;
  @FXML private JFXButton btnReligious1;
  @FXML private JFXButton btnInfoTech1;
  @FXML private JFXButton btnFlorist1;
  @FXML private JFXButton btnMedicine1;
  @FXML private JFXButton btnAudioVisual1;
  @FXML private JFXButton btnGiftShop1;
  @FXML private JFXButton btnFood1;
  @FXML private JFXButton btnAppointment1;
  @FXML private JFXButton btnBirthday;
  @FXML private JFXButton btnBirthday1;
  @FXML private JFXButton btnAudioVisualAPI;
  @FXML private JFXButton btnGiftDeliveryAPI;
  @FXML private JFXButton btnTransportationAPI;
  @FXML private JFXButton btnInterpreterAPI;
  @FXML private JFXButton btnIncidentAPI;
  @FXML private JFXButton btnAudioVisualAPI1;
  @FXML private JFXButton btnGiftDeliveryAPI1;
  @FXML private JFXButton btnTransportationAPI1;
  @FXML private JFXButton btnInterpreterAPI1;
  @FXML private JFXButton btnIncidentAPI1;
  @FXML private JFXButton btnFoodAPI1;
  @FXML private VBox buttonsDark;
  @FXML private VBox buttonsLight;
  @FXML private JFXButton emergency;

  @FXML
  public void initialize() {
    buttonsDark.setVisible(false);
    buttonsLight.setVisible(false);

    if (uController.getTheme() == UIController.Theme.Dark) {
      buttonsDark.setVisible(true);
    } else if (uController.getTheme() == UIController.Theme.Light) {
      buttonsLight.setVisible(true);
    }
  }

  @FXML
  private void btnClicked(ActionEvent e) throws IOException {
    if (e.getSource() == btnHome) {
      uController.home();
    } else if (e.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");
    } else if (e.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");
    } else if (e.getSource() == btnInterpreter || e.getSource() == btnInterpreter1) {
      oController.selectServiceType(ServiceType.Interpreter);
      uController.setScene("ServiceRequest/LangInterpreter.fxml");
    } else if (e.getSource() == btnSanitation || e.getSource() == btnSanitation1) {
      oController.selectServiceType(ServiceType.Sanitation);
      uController.setScene("ServiceRequest/Sanitation.fxml");
    } else if (e.getSource() == btnInternalTrans || e.getSource() == btnInternalTrans1) {
      oController.selectServiceType(ServiceType.InternalTransport);
      uController.setScene("ServiceRequest/InternalTransportation.fxml");
    } else if (e.getSource() == btnReligious || e.getSource() == btnReligious1) {
      oController.selectServiceType(ServiceType.Religious);
      uController.setScene("ServiceRequest/Religion.fxml");
    } else if (e.getSource() == btnInfoTech || e.getSource() == btnInfoTech1) {
      oController.selectServiceType(ServiceType.InfoTech);
      uController.setScene("ServiceRequest/InformationTechnology.fxml");
    } else if (e.getSource() == btnGiftShop || e.getSource() == btnGiftShop1) {
      oController.selectServiceType(ServiceType.GiftShop);
      uController.setScene("ServiceRequest/GiftStore.fxml");
    } else if (e.getSource() == btnFlorist || e.getSource() == btnFlorist1) {
      oController.selectServiceType(ServiceType.Florist);
      uController.setScene("ServiceRequest/Florist.fxml");
    } else if (e.getSource() == btnMedicine || e.getSource() == btnMedicine1) {
      oController.selectServiceType(ServiceType.Medicine);
      uController.setScene("ServiceRequest/MedicineDelivery.fxml");
    } else if (e.getSource() == btnAudioVisual || e.getSource() == btnAudioVisual1) {
      oController.selectServiceType(ServiceType.AudioVisual);
      uController.setScene("ServiceRequest/AVServices.fxml");
    } else if (e.getSource() == btnFood || e.getSource() == btnFood1) {
      oController.selectServiceType(ServiceType.Food);
      uController.setScene("ServiceRequest/Food.fxml");
    } else if (e.getSource() == btnBirthday || e.getSource() == btnBirthday1) {
      oController.selectServiceType(ServiceType.Birthday);
      uController.setScene("ServiceRequest/Birthday.fxml");
    } else if (e.getSource() == btnAudioVisualAPI || e.getSource() == btnAudioVisualAPI1) {
      oController.selectServiceType(ServiceType.AudioVisual);
      uController.setScene("ServiceRequest/AudioVisualAPI.fxml");
    } else if (e.getSource() == btnGiftDeliveryAPI || e.getSource() == btnGiftDeliveryAPI1) {
      oController.selectServiceType(ServiceType.GiftDelivery);
      uController.setScene("ServiceRequest/GiftDelivery.fxml");
    } else if (e.getSource() == btnTransportationAPI || e.getSource() == btnTransportationAPI) {
      oController.selectServiceType(ServiceType.InternalTransport);
      uController.setScene("ServiceRequest/InternalTransportationAPI.fxml");
    } else if (e.getSource() == btnInterpreterAPI || e.getSource() == btnInterpreterAPI1) {
      oController.selectServiceType(ServiceType.Interpreter);
      uController.setScene("ServiceRequest/InterpreterAPI.fxml");
    } else if (e.getSource() == btnIncidentAPI || e.getSource() == btnIncidentAPI1) {
      oController.selectServiceType(ServiceType.Security);
      uController.setScene("ServiceRequest/Incident.fxml");
    }
  }
}
