package edu.wpi.teamp.Controllers.ServiceRequest;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.teamp.UIController;
import hospital.route.AbstractNode;
import hospital.route.RouteController;
import java.util.ArrayList;
import java.util.Comparator;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class BasicInformationController {

  private oServiceRequestController oController =
      oServiceRequestController.getServiceRequestController();
  private UIController uController = UIController.getUIController();
  private RouteController rController = uController.getRouteController();

  @FXML private ListView<AbstractNode> nodeList;
  @FXML private ListView<AbstractNode> nodeList1;
  @FXML private JFXTextField requester;
  @FXML private JFXTextField requester1;
  @FXML private JFXTextArea description;
  @FXML private VBox primaryBox;
  @FXML private VBox secondaryBox;

  @FXML
  public void initialize() {
    primaryBox.setVisible(true);
    secondaryBox.setVisible(false);
    ArrayList<AbstractNode> rooms = rController.getRooms();
    rooms.sort(Comparator.comparing(AbstractNode::getLongName));
    nodeList.getItems().addAll(rooms);
    nodeList1.getItems().addAll(rooms);

    if (uController.getUser() != null) {
      requester.setText(uController.getUser().getName());
      requester.setDisable(true);
      requester1.setText(uController.getUser().getName());
      requester1.setDisable(true);
    }
  }

  public String getDescription() {
    return description.getText();
  }

  public String getRequester() {
    if (primaryBox.visibleProperty().get()) return requester.getText();
    else return requester1.getText();
  }

  public AbstractNode getLocation() {
    if (primaryBox.visibleProperty().get()) return nodeList.getSelectionModel().getSelectedItem();
    else return nodeList1.getSelectionModel().getSelectedItem();
  }

  public void enableSecondary() {
    primaryBox.setVisible(false);
    secondaryBox.setVisible(true);
  }
}
