package edu.wpi.teamp.Controllers.ServiceRequest;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTimePicker;
import edu.wpi.teamp.UIController;
import hospital.route.AbstractNode;
import hospital.route.RouteController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;

public class InternalTransController {

  RequestReceivedPopupController pController;

  @FXML RequestReceivedPopupController requestReceivedPopupController;
  @FXML BasicInformationController basicInformationController;

  UIController uController = UIController.getUIController();
  oServiceRequestController oController = oServiceRequestController.getServiceRequestController();
  private RouteController rController = uController.getRouteController();
  BasicInformationController bController;
  ArrayList<JFXCheckBox> checkboxes;

  // setting up ids
  @FXML private JFXButton btnCancel;
  @FXML private JFXButton btnSubmit;
  @FXML private Label errorMessage;
  @FXML private StackPane requestReceivedPane;
  @FXML private JFXCheckBox box1;
  @FXML private JFXCheckBox box2;
  @FXML private JFXCheckBox box3;
  @FXML private JFXCheckBox box4;
  @FXML private JFXCheckBox box5;
  @FXML private JFXCheckBox box6;
  @FXML private JFXTimePicker pickUpTime;
  @FXML private ListView<AbstractNode> nodeDesiredList;
  @FXML private Label selectMessage;
  @FXML private JFXButton emergency;

  @FXML
  public void initialize() {
    bController = basicInformationController;
    ArrayList<AbstractNode> rooms = rController.getRooms();
    rooms.sort(
        new Comparator<AbstractNode>() {
          @Override
          public int compare(AbstractNode o1, AbstractNode o2) {
            return o1.getLongName().compareTo(o2.getLongName());
          }
        });
    nodeDesiredList.getItems().addAll(rooms);

    checkboxes = new ArrayList<>(Arrays.asList(box1, box2, box3, box4, box5, box6));
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
          || bController.getRequester().isEmpty()
          || !isChecked()
          || nodeDesiredList.getSelectionModel().getSelectedItem() == null
          || pickUpTime.getValue() == null) {
        errorMessage.setVisible(true);
      } else {
        int numSelected = 0;
        if (box1.isSelected()) {
          oController.addAdditionalField("1 person");
          numSelected++;
        }
        if (box2.isSelected()) {
          oController.addAdditionalField("2 people");
          numSelected++;
        }
        if (box3.isSelected()) {
          oController.addAdditionalField("3 people");
          numSelected++;
        }
        if (box4.isSelected()) {
          oController.addAdditionalField("4 people");
          numSelected++;
        }
        if (box5.isSelected()) {
          oController.addAdditionalField("5 people");
          numSelected++;
        }
        if (box6.isSelected()) {
          oController.addAdditionalField("6 people");
          numSelected++;
        }
        if (!(numSelected > 0)) {
          selectMessage.setVisible(true);
        } else {
          oController.createServiceRequest(
              bController.getLocation(), bController.getDescription(), bController.getRequester());
          oController.clearAdditional();
          requestReceivedPopupController.showPopup();
        }
      }
    }
  }

  private boolean isChecked() {
    for (JFXCheckBox box : checkboxes) {
      if (box.isSelected()) {
        return true;
      }
    }
    return false;
  }
}
