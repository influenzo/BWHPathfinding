package edu.wpi.teamp.Controllers.ServiceRequest;

import com.jfoenix.controls.JFXButton;
import edu.wpi.teamp.UIController;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class OnTheWayController {

  UIController uController = UIController.getUIController();

  @FXML private JFXButton btnHome;

  @FXML
  public void initialize() {}

  @FXML
  private void btnClicked(ActionEvent e) throws IOException {
    if (e.getSource() == btnHome) {
      uController.home();
    }
  }
}
