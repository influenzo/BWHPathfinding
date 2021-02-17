package edu.wpi.teamp.Controllers.ServiceRequest;

import com.jfoenix.controls.*;
import edu.wpi.teamp.UIController;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class BirthdayController {
  UIController uController = UIController.getUIController();
  oServiceRequestController oController = oServiceRequestController.getServiceRequestController();
  BasicInformationController bController;
  RequestReceivedPopupController pController;

  @FXML private JFXButton btnCancel;
  @FXML private JFXButton btnSubmit;
  @FXML private JFXTextField numPeople;
  @FXML private JFXDatePicker deliveryDate;
  @FXML private JFXCheckBox boxIceCream;
  @FXML private JFXCheckBox boxVanilla;
  @FXML private JFXCheckBox boxChocolate;
  @FXML private JFXCheckBox boxRed;
  @FXML private JFXCheckBox boxBlue;
  @FXML private JFXCheckBox boxYellow;
  @FXML private JFXCheckBox boxGreen;
  @FXML private JFXCheckBox boxPurple;
  @FXML private JFXCheckBox boxPink;
  @FXML private JFXCheckBox boxSuperHero;
  @FXML private JFXCheckBox boxPrincess;
  @FXML private JFXCheckBox boxRickMorty;
  @FXML private JFXCheckBox boxSports;
  @FXML private JFXCheckBox boxCars;
  @FXML private JFXCheckBox boxTransformers;
  @FXML private Label selectMessage;

  @FXML BasicInformationController basicInformationController;
  @FXML RequestReceivedPopupController requestReceivedPopupController;
  @FXML private Label errorMessage;

  @FXML
  public void initialize() {
    bController = basicInformationController;
    pController = requestReceivedPopupController;
    errorMessage.setVisible(false);
  }

  public void btnClicked(ActionEvent event) throws IOException {
    if (event.getSource() == btnCancel) {
      oController.clearAdditional();
      uController.back();
    } else if (event.getSource() == btnSubmit) {
      if (bController.getLocation() == null
          || bController.getDescription().isEmpty()
          || bController.getRequester().isEmpty()
          || numPeople.getText().equals("")
          || deliveryDate.getValue() == null) {
        errorMessage.setVisible(true);
      } else {
        int numSelected = 0;
        if (boxIceCream.isSelected()) {
          oController.addAdditionalField("IceCream");
          numSelected++;
        }
        if (boxVanilla.isSelected()) {
          oController.addAdditionalField("Vanilla");
          numSelected++;
        }
        if (boxChocolate.isSelected()) {
          oController.addAdditionalField("CHocolate");
          numSelected++;
        }
        if (boxRed.isSelected()) {
          oController.addAdditionalField("Red");
          numSelected++;
        }
        if (boxBlue.isSelected()) {
          oController.addAdditionalField("Blue");
          numSelected++;
        }
        if (boxYellow.isSelected()) {
          oController.addAdditionalField("Yellow");
          numSelected++;
        }
        if (boxGreen.isSelected()) {
          oController.addAdditionalField("Green");
          numSelected++;
        }
        if (boxPurple.isSelected()) {
          oController.addAdditionalField("Purple");
          numSelected++;
        }
        if (boxPink.isSelected()) {
          oController.addAdditionalField("Pink");
          numSelected++;
        }
        if (boxSuperHero.isSelected()) {
          oController.addAdditionalField("Superhero");
          numSelected++;
        }
        if (boxPrincess.isSelected()) {
          oController.addAdditionalField("Princess");
          numSelected++;
        }
        if (boxRickMorty.isSelected()) {
          oController.addAdditionalField("Rick & Morty");
          numSelected++;
        }
        if (boxSports.isSelected()) {
          oController.addAdditionalField("Sports");
          numSelected++;
        }
        if (boxCars.isSelected()) {
          oController.addAdditionalField("Cars");
          numSelected++;
        }
        if (boxTransformers.isSelected()) {
          oController.addAdditionalField("Transformers");
          numSelected++;
        }
        if (!(numSelected > 0)) {
          selectMessage.setVisible(true);
        } else {
          oController.addAdditionalField(numPeople.getText());
          oController.addAdditionalField(deliveryDate.getValue().toString());
          oController.createServiceRequest(
              bController.getLocation(), bController.getDescription(), bController.getRequester());
          oController.clearAdditional();
          requestReceivedPopupController.showPopup();
        }
      }
    }
  }
}
