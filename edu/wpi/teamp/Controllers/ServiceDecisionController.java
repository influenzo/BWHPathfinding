package edu.wpi.teamp.Controllers;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamP.APIController;
import edu.wpi.cs3733.d20.teamP.ServiceException;
import edu.wpi.teamp.UIController;
import hospital.employee.Employee;
import hospital.employee.EmployeeController;
import hospital.service.ServiceController;
import hospital.service.ServiceRequest;
import hospital.service.ServiceType;
import hospital.service.Status;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class ServiceDecisionController {

  UIController uController = UIController.getUIController();
  ServiceController sController = uController.getServiceController();
  EmployeeController eController = uController.getEmployeeController();

  //  @FXML TableView<ServiceRequest> services;
  //  @FXML TableColumn typeCol;
  //  @FXML TableColumn requesterCol;
  //  @FXML TableColumn statusCol;

  @FXML JFXListView<ServiceRequest> serviceList;
  // ArrayList<ServiceRequest> serviceRequests = new ArrayList<>();

  @FXML JFXTextField id;
  @FXML JFXTextField serviceType;
  @FXML JFXTextField requester;
  @FXML JFXComboBox<Employee> appointeeBox;
  @FXML JFXTextField locationField;
  @FXML JFXTextField additional;
  @FXML JFXTextField status;
  @FXML JFXTextArea description;
  @FXML private JFXButton emergency;

  @FXML JFXButton approveBtn;
  @FXML JFXButton denyBtn;
  @FXML JFXButton closeBtn;
  @FXML JFXButton backBtn;
  @FXML JFXButton foodBtn;
  @FXML HBox approveDenyBox;
  @FXML HBox closeBox;

  @FXML Label errorLabel;

  ServiceRequest selected;

  @FXML
  public void initialize() {

    serviceList.getItems().addAll(sController.getServiceRequests());
    serviceList.setCellFactory(l -> new ServiceListView());

    // Display first item in the list
    if (sController.getServiceRequests().size() > 0) {
      serviceList.getSelectionModel().select(0);
      selectServiceRequest(sController.getServiceRequests().get(0));
    }

    //    services.setEditable(true);
    //    services.setPrefWidth(400);
    //    typeCol.setCellValueFactory(new PropertyValueFactory<ServiceRequest,
    // ServiceType>("type"));
    //    typeCol.setPrefWidth(100);
    //    requesterCol.setCellValueFactory(new PropertyValueFactory<ServiceRequest,
    // String>("requester"));
    //    requesterCol.setPrefWidth(200);
    //    statusCol.setCellValueFactory(new PropertyValueFactory<ServiceRequest, Status>("status"));
    //    statusCol.setPrefWidth(100);
    //    services.getItems().addAll(sController.getServiceRequests());
  }

  @FXML
  public void btnClicked(ActionEvent e) throws IOException {
    if (e.getSource() == backBtn) {
      uController.home();
    } else if (e.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");
    } else if (e.getSource() == approveBtn) {
      if (appointeeBox.getSelectionModel().getSelectedItem() == null) errorLabel.setVisible(true);
      else {
        Employee appointee = appointeeBox.getSelectionModel().getSelectedItem();
        sController.approveServiceRequest(selected, appointee);
        updateServiceRequest();
        serviceList.getItems().clear();
        serviceList.getItems().addAll(sController.getServiceRequests());
      }
    } else if (e.getSource() == denyBtn) {
      sController.denyServiceRequest(selected);
      updateServiceRequest();
      serviceList.getItems().clear();
      serviceList.getItems().addAll(sController.getServiceRequests());
    } else if (e.getSource() == closeBtn) {
      sController.closeServiceRequest(selected);
      updateServiceRequest();
      serviceList.getItems().clear();
      serviceList.getItems().addAll(sController.getServiceRequests());
    } else if (e.getSource() == foodBtn) {
      try {
        APIController.setAdmin(true);
        APIController.setAutoClose(false);
        for (Employee employee : eController.getEmployees()) {
          if (employee.getService() == ServiceType.Starbucks) {
            APIController.addEmployee(employee.getStringID(), employee.getName(), 2);
          } else if (employee.getService() == ServiceType.Cafeteria) {
            APIController.addEmployee(employee.getStringID(), employee.getName(), 1);
          }
        }
        APIController.run(-1, -1, 1280, 720, null, null, null);
      } catch (ServiceException ex) {
        ex.printStackTrace();
      }
    }
  }

  //  @FXML
  //  public void handleTableClick(MouseEvent e) {
  //    selected = services.getSelectionModel().getSelectedItem();
  //    updateServiceRequest();
  //  }

  @FXML
  public void listClicked(MouseEvent e) {
    ServiceRequest request = serviceList.getSelectionModel().getSelectedItem();
    selectServiceRequest(request);
  }

  public void selectServiceRequest(ServiceRequest request) {
    selected = request;
    updateServiceRequest();
  }

  public void updateServiceRequest() {
    errorLabel.setVisible(false);
    id.setText(Long.toString(selected.getId()));
    serviceType.setText(selected.getType().toString());
    requester.setText(selected.getRequester());
    appointeeBox.getItems().clear();
    appointeeBox.getItems().addAll(eController.getAvailableEmployees(selected));
    locationField.setText(selected.getLocation().getLongName());
    additional.setText(selected.getAdditional());
    description.setText(selected.getDescription());
    status.setText(selected.getStatus().toString());

    approveDenyBox.setVisible(false);
    closeBox.setVisible(false);
    if (selected.getStatus() == Status.active) {
      approveDenyBox.setVisible(false);
      closeBox.setVisible(true);
    } else if (selected.getStatus() == Status.pending) {
      approveDenyBox.setVisible(true);
      closeBox.setVisible(false);
    } else {
      approveDenyBox.setVisible(false);
      closeBox.setVisible(false);
    }
  }
}
