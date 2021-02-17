package edu.wpi.teamp.Controllers;

import com.jfoenix.controls.JFXButton;
import edu.wpi.teamp.UIController;
import java.io.IOException;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class EmergencyScreenController {
  UIController uController = UIController.getUIController();
  @FXML private GridPane lightModeIcons;
  @FXML private GridPane darkModeIcons;
  @FXML private JFXButton exit;
  @FXML private JFXButton notify;
  @FXML private JFXButton exit1;
  @FXML private JFXButton notify1;
  @FXML private JFXButton homeBtn;
  @FXML private StackPane notificationReceived;

  @FXML
  public void initialize() {
    notificationReceived.setVisible(false);

    darkModeIcons.setVisible(false);
    lightModeIcons.setVisible(false);

    if (uController.getTheme() == UIController.Theme.Dark) {
      darkModeIcons.setVisible(true);
    } else if (uController.getTheme() == UIController.Theme.Light) {
      lightModeIcons.setVisible(true);
    }
  }

  @FXML
  public void btnClicked(ActionEvent e) throws IOException {
    if (e.getSource() == notify || e.getSource() == notify1) {
      // set start visibility
      notificationReceived.setVisible(true);

      // create delays and final look
      PauseTransition delayConfirm = new PauseTransition(Duration.seconds(2));
      delayConfirm.setOnFinished(
          event -> {
            notificationReceived.setVisible(false);
            try {
              uController.setScene("ExitPath.fxml");
            } catch (IOException ex) {
              ex.printStackTrace();
            }
          });

      // start delays
      delayConfirm.play();
    } else if (e.getSource() == exit || e.getSource() == exit1) {
      uController.setScene("ExitPath.fxml");
    } else if (e.getSource() == homeBtn) {
      uController.home();
    }
  }
}
