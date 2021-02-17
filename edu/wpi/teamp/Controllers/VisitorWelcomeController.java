package edu.wpi.teamp.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.teamp.UIController;
import hospital.route.AbstractNode;
import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class VisitorWelcomeController {
  UIController uController = UIController.getUIController();
  @FXML private JFXButton navigation;
  @FXML private JFXButton service;
  @FXML private JFXButton btnLocal;
  @FXML private JFXButton btnAPI;
  @FXML private JFXButton wifi;
  @FXML private JFXButton admin;
  @FXML private JFXButton emergency;
  @FXML private JFXButton home;
  @FXML private ImageView backImg;
  @FXML private ImageView abackImg;
  @FXML private ImageView dbackImg;
  @FXML private ImageView pbackImg;
  @FXML private ImageView tbackImg;
  @FXML private ImageView dmbackImg;
  @FXML private ImageView backImg1;
  @FXML private ImageView abackImg1;
  @FXML private ImageView dbackImg1;
  @FXML private ImageView pbackImg1;
  @FXML private ImageView tbackImg1;
  @FXML private ImageView dmbackImg1;
  @FXML private AnchorPane wifiPanel;
  @FXML private HBox colorBlindOptions;
  @FXML private JFXButton colorBlindBtn;
  @FXML private JFXButton aNopiaBtn;
  @FXML private JFXButton dNopiaBtn;
  @FXML private JFXButton tNopiaBtn;
  @FXML private JFXButton pNopiaBtn;
  @FXML private JFXButton normalBtn;
  @FXML private JFXButton search;
  @FXML private Rectangle recLight;
  @FXML private Rectangle recDark;

  @FXML public JFXTextField startSearch;
  @FXML private JFXComboBox inputComboBox;

  @FXML
  public void initialize() {

    backImg.setVisible(false);
    abackImg.setVisible(false);
    dbackImg.setVisible(false);
    pbackImg.setVisible(false);
    tbackImg.setVisible(false);
    dmbackImg.setVisible(false);

    // service request butttons
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
      recDark.setVisible(true);
      recLight.setVisible(false);
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
    colorBlindOptions.setVisible(false);
  }

  @FXML
  public void btnClicked(ActionEvent e) throws IOException {
    if (e.getSource() == navigation) {
      uController.setScene("StartNavigation.fxml");

    } else if (e.getSource() == search) {
      if (!startSearch.getText().equals("")) {
        uController.setHomeSearchQuery(startSearch.getText());
        if (!(uController.getHomeSearchQuery() == "")) {
          uController.setScene("StartNavigation.fxml");
        }
      }
    } else if (e.getSource() == wifi) {
      backImg1.setVisible(false);
      abackImg1.setVisible(false);
      dbackImg1.setVisible(false);
      pbackImg1.setVisible(false);
      tbackImg1.setVisible(false);
      dmbackImg1.setVisible(false);

      if (uController.getTheme() == UIController.Theme.Dark) {
        dmbackImg1.setVisible(true);
        dmbackImg1.viewOrderProperty().set(100);
        dmbackImg1.setFitHeight(1080);
        dmbackImg1.setFitWidth(1920);
      } else if (uController.getColorBlindType() == "aNopia") {
        abackImg1.setVisible(true);
        abackImg1.viewOrderProperty().set(100);
        abackImg1.setFitHeight(1080);
        abackImg1.setFitWidth(1920);
      } else if (uController.getColorBlindType() == "dNopia") {
        dbackImg1.setVisible(true);
        dbackImg1.viewOrderProperty().set(100);
        dbackImg1.setFitHeight(1080);
        dbackImg1.setFitWidth(1920);
      } else if (uController.getColorBlindType() == "pNopia") {
        pbackImg1.setVisible(true);
        pbackImg1.viewOrderProperty().set(100);
        pbackImg1.setFitHeight(1080);
        pbackImg1.setFitWidth(1920);
      } else if (uController.getColorBlindType() == "tNopia") {
        tbackImg1.setVisible(true);
        tbackImg1.viewOrderProperty().set(100);
        tbackImg1.setFitHeight(1080);
        tbackImg1.setFitWidth(1920);
      } else {
        backImg1.setVisible(true);
        backImg1.viewOrderProperty().set(100);
        backImg1.setFitHeight(1080);
        backImg1.setFitWidth(1920);
      }
      transition(wifiPanel, true);

    } else if (e.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");
    } else if (e.getSource() == admin) {
      uController.setScene("AdminLogin.fxml");
    } else if (e.getSource() == home) {
      transition(wifiPanel, false);
    } else if (e.getSource() == service) {
      if (btnLocal.isVisible() == false) {
        transition(btnLocal, true);
        transition(btnAPI, true);
      } else {
        transition(btnLocal, false);
        transition(btnAPI, false);
      }
      // uController.setScene("ServiceRequest/ServiceTypeVisitor.fxml");
    } else if (e.getSource() == colorBlindBtn) {
      if (colorBlindOptions.isVisible() == false) {
        transition(colorBlindOptions, true);
      } else {
        transition(colorBlindOptions, false);
      }
    } else if (e.getSource() == aNopiaBtn) {
      uController.setColorBlindType("a");
      aNopiaBtn.getScene().getStylesheets().clear();
      aNopiaBtn
          .getScene()
          .getStylesheets()
          .addAll(
              "/edu/wpi/teamp/aNopia/aNopia.css", "/edu/wpi/teamp/light-mode/basicStructure.css");
      transition(colorBlindOptions, false);
      initialize();

    } else if (e.getSource() == dNopiaBtn) {
      uController.setColorBlindType("d");
      aNopiaBtn.getScene().getStylesheets().clear();
      aNopiaBtn
          .getScene()
          .getStylesheets()
          .addAll(
              "/edu/wpi/teamp/dNopia/dNopia.css", "/edu/wpi/teamp/light-mode/basicStructure.css");
      transition(colorBlindOptions, false);
      initialize();

    } else if (e.getSource() == pNopiaBtn) {
      uController.setColorBlindType("p");
      aNopiaBtn.getScene().getStylesheets().clear();
      aNopiaBtn
          .getScene()
          .getStylesheets()
          .addAll(
              "/edu/wpi/teamp/pNopia/pNopia.css", "/edu/wpi/teamp/light-mode/basicStructure.css");
      transition(colorBlindOptions, false);
      initialize();

    } else if (e.getSource() == tNopiaBtn) {
      uController.setColorBlindType("t");
      aNopiaBtn.getScene().getStylesheets().clear();
      aNopiaBtn
          .getScene()
          .getStylesheets()
          .addAll(
              "/edu/wpi/teamp/tNopia/tNopia.css", "/edu/wpi/teamp/light-mode/basicStructure.css");
      transition(colorBlindOptions, false);
      initialize();

    } else if (e.getSource() == normalBtn) {
      uController.setColorBlindType("n");
      uController.setTheme(UIController.Theme.Light);
      aNopiaBtn.getScene().getStylesheets().clear();
      aNopiaBtn
          .getScene()
          .getStylesheets()
          .addAll(
              "/edu/wpi/teamp/light-mode/light-mode.css",
              "/edu/wpi/teamp/light-mode/basicStructure.css");
      transition(colorBlindOptions, false);
      initialize();
    } else if (e.getSource() == btnLocal) { // this will change
      uController.setScene("ServiceRequest/ServiceTypeVisitor.fxml");
    } else if (e.getSource() == btnAPI) { // this will change
      uController.setScene("ServiceRequest/ServiceTypeAPI.fxml");
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

  public void updateComboBox(KeyEvent keyEvent) {
    String input = startSearch.getText().toLowerCase();
    String rInput = startSearch.getText();
    ObservableList<AbstractNode> rooms = FXCollections.observableArrayList();

    if (keyEvent.getCode().equals(KeyCode.ENTER)) {
      System.out.println(inputComboBox.getSelectionModel().getSelectedItem().toString());
      startSearch.setText(inputComboBox.getSelectionModel().getSelectedItem().toString());
    }
    for (AbstractNode x : uController.getRouteController().getRooms()) {
      if (x.getLongName().toLowerCase().contains(input)) {
        rooms.add(x);
      }
    }
    inputComboBox.setItems(rooms);
    inputComboBox.show();
  }

  public void clickedItem(ActionEvent e) {
    startSearch.setText(inputComboBox.getValue().toString());
  }

  // color mode stuff here
}
