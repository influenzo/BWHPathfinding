package edu.wpi.teamp.Controllers;

import com.jfoenix.controls.*;
import edu.wpi.teamp.UIController;
import hospital.employee.Language;
import hospital.service.ServiceType;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javax.swing.*;

public class CreateEmployeeController {

  UIController uController = UIController.getUIController();
  @FXML JFXTextField nameInput;
  @FXML JFXTextField employeeID;
  @FXML JFXTextField userName;
  @FXML JFXPasswordField passWord;
  @FXML JFXCheckBox isAdmin;
  @FXML JFXCheckBox mondayCheck;
  @FXML JFXCheckBox tusCheck;
  @FXML JFXCheckBox wedCheck;
  @FXML JFXCheckBox thurCheck;
  @FXML JFXCheckBox friCheck;
  @FXML JFXCheckBox satCheck;
  @FXML JFXCheckBox sunCheck;
  @FXML JFXButton createBtn;
  @FXML JFXComboBox<Language> languageBox;
  @FXML JFXComboBox<String> timeIn;
  @FXML JFXComboBox<String> timeOut;
  @FXML JFXComboBox<ServiceType> serviceBox;
  @FXML Label finishLabel;

  public void initialize() {
    languageBox
        .getItems()
        .addAll(
            Language.Dutch,
            Language.French,
            Language.German,
            Language.Hindi,
            Language.Korean,
            Language.Mandarin,
            Language.Spanish,
            Language.Turkish);

    serviceBox
        .getItems()
        .addAll(
            ServiceType.Interpreter,
            ServiceType.Sanitation,
            ServiceType.Mechanical,
            ServiceType.Emotional,
            ServiceType.Medical,
            ServiceType.Supervisor);

    timeIn
        .getItems()
        .addAll(
            "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00",
            "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00",
            "19:00", "20:00", "21:00", "22:00", "23:00", "24:00");
    timeOut
        .getItems()
        .addAll(
            "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00",
            "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00",
            "19:00", "20:00", "21:00", "22:00", "23:00", "24:00");

    finishLabel.setVisible(false);
  }

  @FXML
  public String formatDate() {
    String schedule = "";
    if (mondayCheck.isSelected()) {
      schedule += "T";
    }
    if (!mondayCheck.isSelected()) {
      schedule += "F";
    }
    if (tusCheck.isSelected()) {
      schedule += "T";
    }
    if (!tusCheck.isSelected()) {
      schedule += "F";
    }
    if (wedCheck.isSelected()) {
      schedule += "T";
    }
    if (!wedCheck.isSelected()) {
      schedule += "F";
    }
    if (thurCheck.isSelected()) {
      schedule += "T";
    }
    if (!thurCheck.isSelected()) {
      schedule += "F";
    }
    if (friCheck.isSelected()) {
      schedule += "T";
    }
    if (!friCheck.isSelected()) {
      schedule += "F";
    }
    if (satCheck.isSelected()) {
      schedule += "T";
    }
    if (!satCheck.isSelected()) {
      schedule += "F";
    }
    if (sunCheck.isSelected()) {
      schedule += "T";
    }
    if (!sunCheck.isSelected()) {
      schedule += "F";
    }
    schedule += timeIn.getValue().substring(0, timeIn.getValue().length() - 3);
    schedule += timeOut.getValue().substring(0, timeOut.getValue().length() - 3);
    return schedule;
  }

  public void btnClicked(ActionEvent actionEvent) {
    if (actionEvent.getSource().equals(createBtn)) {
      if (everythingFilled()) {
        String name = nameInput.getText();
        long id = Long.parseLong(employeeID.getText());
        boolean Admin = isAdmin.isSelected();
        String aval = formatDate();
        System.out.println(aval);
        String user = userName.getText();
        String pass = passWord.getText();
      } else {
      }
    }
  }

  public boolean everythingFilled() {
    if (!nameInput.getText().trim().isEmpty()
        && !employeeID.getText().trim().isEmpty() & !userName.getText().trim().isEmpty()
        && !passWord.getText().trim().isEmpty()
        && !serviceBox.getSelectionModel().isEmpty()
        && !timeIn.getSelectionModel().isEmpty()
        && !timeOut.getSelectionModel().isEmpty()) {
      System.out.println("Got everything");
      finishLabel.setText("Employee Created");
      finishLabel.setTextFill(Color.valueOf("7FFF00"));
      t.start();
      return true;
    } else {
      return false;
    }
  }

  Thread t =
      new Thread() {
        public void run() {
          System.out.println("Got everything");
          finishLabel.setText("Employee Created");
          finishLabel.setTextFill(Color.valueOf("7FFF00"));
          finishLabel.setVisible(true);
          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          finishLabel.setVisible(false);
          t.interrupt();
        }
      };

  public void tabChanged(Event event) {}

  public void showEmployees() {}
}
