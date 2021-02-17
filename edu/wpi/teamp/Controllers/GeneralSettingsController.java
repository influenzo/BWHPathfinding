package edu.wpi.teamp.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleNode;
import edu.wpi.teamp.UIController;
import hospital.Hospital;
import hospital.HospitalController;
import hospital.exceptions.NullHospitalException;
import hospital.route.AbstractNode;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;

public class GeneralSettingsController {

  UIController uController = UIController.getUIController();
  HospitalController hController = HospitalController.getHospitalController();
  Hospital faulkner = hController.getHospital("Faulkner");
  Hospital mainHospital = hController.getHospital("Main");

  @FXML JFXToggleNode testBtn;
  @FXML JFXToggleNode prodBtn;
  @FXML JFXToggleNode mainBtn;
  @FXML JFXToggleNode faulknerBtn;
  @FXML JFXToggleNode aStarBtn;
  @FXML JFXToggleNode dfsBtn;
  @FXML JFXToggleNode bfsBtn;
  @FXML JFXToggleNode djkBtn;
  @FXML JFXToggleNode lightBtn;
  @FXML JFXToggleNode darkBtn;
  @FXML JFXComboBox<String> nodeCombo;
  @FXML JFXTextField timeoutField;
  @FXML JFXButton timeoutBtn;

  ToggleGroup databaseGroup = new ToggleGroup();
  ToggleGroup hospitalGroup = new ToggleGroup();
  ToggleGroup algorithmGroup = new ToggleGroup();
  ToggleGroup themeGroup = new ToggleGroup();

  public GeneralSettingsController() throws NullHospitalException {}

  @FXML
  public void initialize() {
    testBtn.setToggleGroup(databaseGroup);
    prodBtn.setToggleGroup(databaseGroup);
    mainBtn.setToggleGroup(hospitalGroup);
    faulknerBtn.setToggleGroup(hospitalGroup);
    aStarBtn.setToggleGroup(algorithmGroup);
    dfsBtn.setToggleGroup(algorithmGroup);
    bfsBtn.setToggleGroup(algorithmGroup);
    djkBtn.setToggleGroup(algorithmGroup);
    lightBtn.setToggleGroup(themeGroup);
    darkBtn.setToggleGroup(themeGroup);

    int initTimer = uController.getTimeout() / 1000;
    timeoutField.setText(String.valueOf(initTimer));

    if (uController.getHospital().getName().equals("Faulkner")) {
      faulknerBtn.setSelected(true);
    } else if (uController.getHospital().getName().equals("Main")) {
      mainBtn.setSelected(true);
    }

    setupHomeNodeComboBox();

    selectCorrectDB();
    selectCorrectAlgo();
    selectCorrectTheme();

    nodeCombo
        .valueProperty()
        .addListener(
            (o, oldValue, newValue) -> {
              nodeCombo.getSelectionModel().select(newValue);
              String[] newSplit = newValue.split(" - ");
              newSplit = newSplit[1].split(" ");
              uController.getHospital().getRouteController().setHomeNodeId(newSplit[0]);
            });
  }

  public void btnClicked(ActionEvent actionEvent) throws IOException {
    if (actionEvent.getSource() == testBtn) {
      testBtn.setSelected(true);
      uController.setDatabaseFlag(1);

    } else if (actionEvent.getSource() == prodBtn) {
      prodBtn.setSelected(true);
      uController.setDatabaseFlag(2);
    } else if (actionEvent.getSource() == mainBtn) {
      mainBtn.setSelected(true);
      uController.setHospital(mainHospital);
      setupHomeNodeComboBox();
    } else if (actionEvent.getSource() == faulknerBtn) {
      faulknerBtn.setSelected(true);
      uController.setHospital(faulkner);
      setupHomeNodeComboBox();
    } else if (actionEvent.getSource() == aStarBtn) {
      aStarBtn.setSelected(true);
      uController.setAlgoType("AStar");
    } else if (actionEvent.getSource() == dfsBtn) {
      dfsBtn.setSelected(true);
      uController.setAlgoType("DFS");
    } else if (actionEvent.getSource() == bfsBtn) {
      bfsBtn.setSelected(true);
      uController.setAlgoType("BFS");
    } else if (actionEvent.getSource() == djkBtn) {
      djkBtn.setSelected(true);
      uController.setAlgoType("DJK");
    } else if (actionEvent.getSource() == lightBtn) {
      lightBtn.setSelected(true);
      uController.setColorBlindType("n");
      darkBtn.getScene().getStylesheets().clear();
      darkBtn
          .getScene()
          .getStylesheets()
          .addAll(
              "/edu/wpi/teamp/light-mode/light-mode.css",
              "/edu/wpi/teamp/light-mode/basicStructure.css",
              "/edu/wpi/teamp/light-mode/service-request.css",
              "/edu/wpi/teamp/light-mode/tableView.css",
              "/edu/wpi/teamp/light-mode/tabPane.css");
      uController.setTheme(UIController.Theme.Light);

    } else if (actionEvent.getSource() == darkBtn) {
      darkBtn.setSelected(true);
      uController.setColorBlindType("dm");
      darkBtn.getScene().getStylesheets().clear();
      darkBtn
          .getScene()
          .getStylesheets()
          .addAll(
              "/edu/wpi/teamp/dark-mode/dark-mode.css",
              "/edu/wpi/teamp/dark-mode/basicStructure.css",
              "/edu/wpi/teamp/dark-mode/service-request.css",
              "/edu/wpi/teamp/dark-mode/tableView.css",
              "/edu/wpi/teamp/dark-mode/tabPane.css");
      uController.setTheme(UIController.Theme.Dark);
    } else if (actionEvent.getSource() == timeoutBtn) {
      int time = Integer.parseInt(timeoutField.getText()) * 1000;
      uController.setTimeout(time);
    }
  }

  private void selectCorrectHomeNode() {
    AbstractNode currentHome = uController.getHospital().getRouteController().getHomeNode();
    nodeCombo
        .getSelectionModel()
        .select(
            currentHome.getLongName()
                + " - "
                + currentHome.getId()
                + " ("
                + currentHome.getFloor()
                + ")");
  }

  private void selectCorrectDB() {
    testBtn.setSelected(uController.getDatabaseFlag() == 1);
    prodBtn.setSelected(uController.getDatabaseFlag() == 2);
  }

  private void selectCorrectAlgo() {
    aStarBtn.setSelected(uController.getAlgoType().equals("AStar"));
    dfsBtn.setSelected(uController.getAlgoType().equals("DFS"));
    bfsBtn.setSelected(uController.getAlgoType().equals("BFS"));
    djkBtn.setSelected(uController.getAlgoType().equals("DJK"));
  }

  private void selectCorrectTheme() {
    if (uController.getTheme() == UIController.Theme.Light) {
      lightBtn.setSelected(true);
    } else if (uController.getTheme() == UIController.Theme.Dark) {
      darkBtn.setSelected(true);
    }
  }

  private void setupHomeNodeComboBox() {
    selectCorrectHomeNode();
    HashMap<String, AbstractNode> nodes = uController.getRouteController().getNodes();
    Object[] keys = nodes.keySet().toArray();
    String[] comboData = new String[keys.length];
    for (int i = 0; i < keys.length; i++) {
      String id = (String) keys[i];
      comboData[i] =
          nodes.get(id).getLongName() + " - " + id + " (" + nodes.get(id).getFloor() + ")";
    }
    Arrays.sort(comboData);
    nodeCombo.setItems(FXCollections.observableArrayList(comboData));
  }
}
