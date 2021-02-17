package edu.wpi.teamp.Controllers;

import com.jfoenix.controls.JFXButton;
import edu.wpi.teamp.UIController;
import java.awt.*;
import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class AdminWelcomeController {
  UIController uController = UIController.getUIController();
  @FXML private JFXButton service;
  @FXML private JFXButton btnLocal;
  @FXML private JFXButton btnAPI;
  @FXML private JFXButton mapEdit;
  @FXML private JFXButton serviceVerify;
  @FXML private JFXButton navigation;
  @FXML private JFXButton events;
  @FXML private ImageView backImg;
  @FXML private ImageView abackImg;
  @FXML private ImageView dbackImg;
  @FXML private ImageView pbackImg;
  @FXML private ImageView tbackImg;
  @FXML private ImageView dmbackImg;
  @FXML private Label welcome1;
  @FXML private JFXButton textSize;
  @FXML private JFXButton emergency;
  @FXML private JFXButton languages;
  @FXML private JFXButton english;
  @FXML private JFXButton spanish;
  @FXML private JFXButton logOut;
  @FXML private VBox vBoxLanguages;

  @FXML
  public void initialize() {
    welcome1.setText("Welcome Back, " + "\n" + (String) uController.getUser().getName());
    backImg.setVisible(false);
    abackImg.setVisible(false);
    dbackImg.setVisible(false);
    pbackImg.setVisible(false);
    tbackImg.setVisible(false);
    dmbackImg.setVisible(false);

    // setting service request buttons to be invisible
    btnLocal.setVisible(false);
    btnAPI.setVisible(false);

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
    } else {
      backImg.setVisible(true);
      backImg.viewOrderProperty().set(100);
      backImg.setFitHeight(1080);
      backImg.setFitWidth(1920);
    }
    uController.setBackground(backImg);
  }

  @FXML
  public void btnClicked(ActionEvent e) throws IOException {
    if (e.getSource() == service) {
      if (btnLocal.isVisible() == false) {
        transition(btnLocal, true);
        transition(btnAPI, true);
      } else {
        transition(btnLocal, false);
        transition(btnAPI, false);
      }
      // uController.setScene("ServiceRequest/ServiceType.fxml");
    } else if (e.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");
    } else if (e.getSource() == navigation) {
      uController.setScene("StartNavigation.fxml");
    } else if (e.getSource() == mapEdit) {
      uController.setScene("AdministratorTools.fxml");
    } else if (e.getSource() == serviceVerify) {
      uController.setScene("ServiceDecision.fxml");
    } else if (e.getSource() == languages) {
      showLang();
    } else if (e.getSource() == logOut) {
      uController.logout();
    } else if (e.getSource() == btnLocal) { // this will change
      uController.setScene("ServiceRequest/ServiceType.fxml");
    } else if (e.getSource() == btnAPI) { // this will change
      uController.setScene("ServiceRequest/ServiceTypeAPI.fxml");
    }
  }

  private void showLang() {
    if (vBoxLanguages.isVisible() == false) {
      vBoxLanguages.setVisible(true);
      FadeTransition transition = new FadeTransition(Duration.millis(200));
      transition.setNode(vBoxLanguages);
      transition.setFromValue(0);
      transition.setToValue(1);
      transition.play();
    } else if (vBoxLanguages.isVisible() == true) {
      vBoxLanguages.setVisible(false);
      FadeTransition transition = new FadeTransition(Duration.millis(200));
      transition.setNode(vBoxLanguages);
      transition.setFromValue(0);
      transition.setToValue(1);
      transition.play();
    }
  }

  private void transition(Node object, boolean show) {
    FadeTransition transition = new FadeTransition(Duration.millis(225), object);
    transition.setFromValue(show ? 0 : 1);
    transition.setToValue(show ? 1 : 0);
    transition.playFromStart();
    if (!show) {
      transition.onFinishedProperty().set((event -> object.setVisible(false)));
    } else {
      object.setVisible(true);
    }
  }
}
