package edu.wpi.teamp.Controllers.ServiceRequest;

import edu.wpi.teamp.UIController;
import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class RequestReceivedPopupController {

  UIController uController = UIController.getUIController();
  @FXML private AnchorPane anchor;
  @FXML private Label message;

  public void showPopup() {
    FadeTransition transition = new FadeTransition(Duration.millis(200), anchor);
    transition.setFromValue(0);
    transition.setToValue(1);
    transition.setOnFinished(
        (e1 -> {
          PauseTransition delay = new PauseTransition(Duration.seconds(1.25));
          delay.setOnFinished(
              e2 -> {
                try {
                  uController.back();
                } catch (IOException ex) {
                  ex.printStackTrace();
                }
              });
          delay.play();
        }));
    anchor.setVisible(true);
    transition.playFromStart();
  }

  public void showPopup(String message) {
    FadeTransition transition = new FadeTransition(Duration.millis(200), anchor);
    transition.setFromValue(0);
    transition.setToValue(1);
    transition.setOnFinished(
        (e1 -> {
          PauseTransition delay = new PauseTransition(Duration.seconds(1.25));
          delay.setOnFinished(
              e2 -> {
                anchor.setVisible(false);
              });
          delay.play();
        }));
    this.message.setText(message);
    anchor.setVisible(true);
    transition.playFromStart();
  }
}
