package edu.wpi.teamp.Controllers;

import com.jfoenix.controls.JFXButton;
import edu.wpi.teamp.UIController;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

public class CreditsController {
  UIController uController = UIController.getUIController();
  @FXML private ImageView backImg;
  @FXML private ImageView abackImg;
  @FXML private ImageView dbackImg;
  @FXML private ImageView pbackImg;
  @FXML private ImageView tbackImg;
  @FXML private ImageView dmbackImg;

  @FXML
  public void initialize() {
    backImg.setVisible(false);
    dmbackImg.setVisible(false);
    abackImg.setVisible(false);
    dbackImg.setVisible(false);
    pbackImg.setVisible(false);
    tbackImg.setVisible(false);

    if (uController.getColorBlindType() == "aNopia") {
      abackImg.setVisible(true);
      abackImg.viewOrderProperty().set(100);
      abackImg.setFitHeight(1080);
      abackImg.setFitWidth(1920);
    } else if (uController.getColorBlindType() == "dNopia") {
      dbackImg.setVisible(true);
      dbackImg.viewOrderProperty().set(100);
      dbackImg.setFitHeight(1080);
      dbackImg.setFitWidth(1920);
    } else if (uController.getColorBlindType() == "pNopia") {
      pbackImg.setVisible(true);
      pbackImg.viewOrderProperty().set(100);
      pbackImg.setFitHeight(1080);
      pbackImg.setFitWidth(1920);
    } else if (uController.getColorBlindType() == "tNopia") {
      tbackImg.setVisible(true);
      tbackImg.viewOrderProperty().set(100);
      tbackImg.setFitHeight(1080);
      tbackImg.setFitWidth(1920);
    } else if (uController.getTheme() == UIController.Theme.Dark) {
      dmbackImg.setVisible(true);
      dmbackImg.viewOrderProperty().set(100);
      dmbackImg.setFitHeight(1080);
      dmbackImg.setFitWidth(1920);
    } else if (uController.getColorBlindType() == "n"
        || uController.getTheme() == UIController.Theme.Light) {
      backImg.setVisible(true);
      backImg.viewOrderProperty().set(100);
      backImg.setFitHeight(1080);
      backImg.setFitWidth(1920);
    }
  }

  @FXML private JFXButton backBtn;
  @FXML private JFXButton credits;
  @FXML private JFXButton emergency;

  @FXML
  public void btnClicked(ActionEvent e) throws IOException {
    if (e.getSource() == backBtn) {
      uController.setScene("AboutPage02.fxml");
    } else if (e.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");
    } else if (e.getSource() == credits) {
      uController.setScene("Credits.fxml");
    }
  }
}
