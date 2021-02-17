package edu.wpi.teamp.Controllers;

import com.jfoenix.controls.*;
import edu.wpi.teamp.UIController;
import hospital.employee.Employee;
import hospital.employee.EmployeeController;
import hospital.employee.Language;
import hospital.exceptions.DuplicateEmployeeIdException;
import hospital.exceptions.DuplicateUsernameException;
import hospital.service.ServiceType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.animation.PauseTransition;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class EmployeeDatabaseController {

  UIController uController = UIController.getUIController();
  EmployeeController eController = uController.getEmployeeController();
  @FXML TableView tableView;
  @FXML JFXButton createBtn;
  TableColumn idCol;
  TableColumn nameCol;
  TableColumn userNameCol;
  TableColumn passwordCol;
  TableColumn avalCol;
  TableColumn serviceCol;
  TableColumn<Employee, String> adminCol;
  TableColumn<Employee, String> langCol;

  List<Employee> empMade = new ArrayList<>();
  List<Employee> editedEmployees = new ArrayList<>();
  ObservableList<Employee> data;
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
  @FXML JFXButton addBtn;
  @FXML private JFXButton emergency;

  @FXML JFXComboBox<Language> languageBox;
  @FXML JFXComboBox<String> timeIn;
  @FXML JFXComboBox<String> timeOut;
  @FXML JFXComboBox<ServiceType> serviceBox;
  @FXML Label finishLabel;
  @FXML HBox employeePopUp;
  @FXML HBox shadowBox;
  @FXML JFXButton cancelBtn;
  @FXML JFXButton saveBtn;
  @FXML JFXButton csvBtn;

  public void initialize() {
    initPrompt();
    tableView.setEditable(true);
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    data = uController.getEmployeeController().getEmployees();
    // for (Employee e : data) {
    // e.getLanguages().subList(0, 1);
    // }

    idCol = new TableColumn("ID");
    idCol.setPrefWidth(100);
    idCol.setCellValueFactory(new PropertyValueFactory<Employee, Long>("id"));

    nameCol = new TableColumn("Full Name");
    nameCol.setPrefWidth(200);
    nameCol.setCellValueFactory(new PropertyValueFactory<Employee, String>("name"));
    nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
    nameCol.setOnEditCommit(
        new EventHandler<TableColumn.CellEditEvent<Employee, String>>() {
          @Override
          public void handle(TableColumn.CellEditEvent<Employee, String> event) {
            Employee employee =
                ((Employee) event.getTableView().getItems().get(event.getTablePosition().getRow()));
            employee.setName(event.getNewValue());
            editedEmployees.add(employee);
          }
        });

    userNameCol = new TableColumn("User Name");
    userNameCol.setPrefWidth(200);
    userNameCol.setCellValueFactory(new PropertyValueFactory<Employee, String>("username"));
    userNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
    userNameCol.setOnEditCommit(
        new EventHandler<TableColumn.CellEditEvent<Employee, String>>() {
          @Override
          public void handle(TableColumn.CellEditEvent<Employee, String> event) {
            Employee employee =
                ((Employee) event.getTableView().getItems().get(event.getTablePosition().getRow()));
            employee.setUsername(event.getNewValue());
            editedEmployees.add(employee);
          }
        });

    passwordCol = new TableColumn("Password");
    passwordCol.setPrefWidth(200);
    passwordCol.setCellValueFactory(new PropertyValueFactory<Employee, String>("password"));
    passwordCol.setCellFactory(TextFieldTableCell.forTableColumn());
    passwordCol.setOnEditCommit(
        new EventHandler<TableColumn.CellEditEvent<Employee, String>>() {
          @Override
          public void handle(TableColumn.CellEditEvent<Employee, String> event) {
            Employee employee =
                ((Employee) event.getTableView().getItems().get(event.getTablePosition().getRow()));
            employee.setPassword(event.getNewValue());
            editedEmployees.add(employee);
          }
        });

    avalCol = new TableColumn("Availability");
    avalCol.setPrefWidth(200);
    avalCol.setCellValueFactory(new PropertyValueFactory<Employee, String>("availability"));
    avalCol.setCellFactory(TextFieldTableCell.forTableColumn());
    avalCol.setOnEditCommit(
        new EventHandler<TableColumn.CellEditEvent<Employee, String>>() {
          @Override
          public void handle(TableColumn.CellEditEvent<Employee, String> event) {
            Employee employee =
                ((Employee) event.getTableView().getItems().get(event.getTablePosition().getRow()));
            employee.setAvailability(event.getNewValue());
            editedEmployees.add(employee);
          }
        });

    ObservableList serviceList = FXCollections.observableArrayList();
    List<ServiceType> list = Arrays.asList(ServiceType.values());
    serviceList.addAll(list);
    serviceCol = new TableColumn("Service");
    serviceCol.setPrefWidth(200);
    serviceCol.setCellValueFactory(new PropertyValueFactory<Employee, String>("service"));
    serviceCol.setCellFactory(ComboBoxTableCell.forTableColumn(serviceList));
    serviceCol.setOnEditCommit(
        new EventHandler<TableColumn.CellEditEvent>() {
          @Override
          public void handle(TableColumn.CellEditEvent event) {}
        });

    adminCol = new TableColumn("Is Admin?");
    adminCol.setPrefWidth(75);
    adminCol.setCellValueFactory(
        cellData -> {
          boolean isAdmin = cellData.getValue().isAdmin();
          String admin;
          if (isAdmin) {
            admin = "Yes";
          } else {
            admin = "No";
          }
          return new ReadOnlyStringWrapper(admin);
        });

    ObservableList langList = FXCollections.observableArrayList();
    List<Language> langL = Arrays.asList(Language.values());
    langList.addAll(langL);
    langCol = new TableColumn("Language");
    langCol.setPrefWidth(200);
    langCol.setCellValueFactory(new PropertyValueFactory<Employee, String>("languages"));

    tableView.setItems(data);
    tableView
        .getColumns()
        .addAll(idCol, nameCol, userNameCol, passwordCol, avalCol, serviceCol, adminCol, langCol);
  }

  public void updateData() {
    data = uController.getEmployeeController().getEmployees();
  }

  public void btnClicked(ActionEvent actionEvent)
      throws IOException, DuplicateEmployeeIdException, DuplicateUsernameException {
    if (actionEvent.getSource().equals(createBtn)) {
      employeePopUp.setVisible(true);
      shadowBox.setVisible(true);
    } else if (actionEvent.getSource().equals(addBtn)) {
      if (everythingFilled()) {
        String name = nameInput.getText();
        long id = Long.parseLong(employeeID.getText());
        boolean Admin = isAdmin.isSelected();
        String aval = formatDate();
        String user = userName.getText();
        String pass = passWord.getText();
        ServiceType service = serviceBox.getValue();
        ArrayList<Language> langs = new ArrayList<>();
        if (languageBox.getValue() != null) langs.add(languageBox.getValue());

        Employee tmp = new Employee(id, name, Admin, service, langs, aval, user, pass);
        empMade.add(tmp);
        data.add(tmp);
        tableView.setItems(data);
        employeePopUp.setVisible(false);
        shadowBox.setVisible(false);
        clearFields();
      }
    } else if (actionEvent.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");
    } else if (actionEvent.getSource().equals(cancelBtn)) {
      clearFields();
      employeePopUp.setVisible(false);
      shadowBox.setVisible(false);
    } else if (actionEvent.getSource().equals(saveBtn)) {
      for (Employee employee : editedEmployees) {
        eController.updateEmployee(employee);
      }
      editedEmployees = new ArrayList<>();
      for (Employee e : empMade) {
        eController.addEmployee(e);
      }
    } else if (actionEvent.getSource().equals(csvBtn)) {
      eController.downloadCSV(
          "C:\\CondingProjects\\CS3733-D20-Team-P-Project\\src\\main\\resources\\edu\\wpi\\teamp\\employees");
    }
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

  public void initPrompt() {
    employeePopUp.setVisible(false);
    shadowBox.setVisible(false);

    languageBox.getItems().addAll(Language.values());
    serviceBox.getItems().addAll(ServiceType.values());

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

  public boolean everythingFilled() {
    if (!nameInput.getText().trim().isEmpty()
        && !employeeID.getText().trim().isEmpty() & !userName.getText().trim().isEmpty()
        && !passWord.getText().trim().isEmpty()
        && !serviceBox.getSelectionModel().isEmpty()
        && !timeIn.getSelectionModel().isEmpty()
        && !timeOut.getSelectionModel().isEmpty()) {
      System.out.println("Got everything");
      return true;
    } else {
      showError();
      System.out.println("Didn't get everything");
      return false;
    }
  }

  public void showError() {
    finishLabel.setVisible(true);
    // create delays and final look
    PauseTransition delayRequest = new PauseTransition(Duration.seconds(2));
    delayRequest.setOnFinished(
        event -> {
          finishLabel.setVisible(false);
        });
    delayRequest.play();
  }

  public void clearFields() {
    nameInput.clear();
    employeeID.clear();
    userName.clear();
    passWord.clear();
    isAdmin.setSelected(false);
    mondayCheck.setSelected(false);
    tusCheck.setSelected(false);
    wedCheck.setSelected(false);
    thurCheck.setSelected(false);
    friCheck.setSelected(false);
    satCheck.setSelected(false);
    sunCheck.setSelected(false);
    timeIn.getSelectionModel().clearSelection();
    timeOut.getSelectionModel().clearSelection();
    languageBox.getSelectionModel().clearSelection();
  }
}
