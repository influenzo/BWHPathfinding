package edu.wpi.teamp.Controllers;

import hospital.service.ServiceRequest;
import hospital.service.ServiceType;
import hospital.service.Status;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class ServiceListView extends ListCell<ServiceRequest> {

  @FXML Label requesterLabel;
  @FXML Label locationLabel;
  @FXML ImageView serviceType;
  @FXML ImageView status;
  @FXML HBox cellBox;

  FXMLLoader loader;

  @Override
  public void updateItem(ServiceRequest request, boolean empty) {
    super.updateItem(request, empty);

    if (empty) {
      setText(null);
      setGraphic(null);
    } else {
      if (loader == null) {
        loader = new FXMLLoader(getClass().getResource("../ServiceListView.fxml"));
        loader.setController(this);

        try {
          loader.load();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      // set labels
      requesterLabel.setText(request.getRequester());
      locationLabel.setText(request.getLocation().toString());

      // set service type
      if (request.getType().equals(ServiceType.AudioVisual)) {
        serviceType.setImage(
            new Image(
                getClass()
                    .getResource("../images/icons/baseline_volume_up_blue_light.png")
                    .toExternalForm()));
      } else if (request.getType().equals(ServiceType.GiftShop)) {
        serviceType.setImage(
            new Image(
                getClass()
                    .getResource("../images/icons/thicc-candy-bar_blue.png")
                    .toExternalForm()));
      } else if (request.getType().equals(ServiceType.Appointment)) {
        serviceType.setImage(
            new Image(
                getClass()
                    .getResource("../images/icons/doctor-icon-blue-light.png")
                    .toExternalForm()));
      } else if (request.getType().equals(ServiceType.Florist)) {
        serviceType.setImage(
            new Image(
                getClass()
                    .getResource("../images/icons/baseline_local_florist_blue_light.png")
                    .toExternalForm()));
      } else if (request.getType().equals(ServiceType.Food)) {
        serviceType.setImage(
            new Image(getClass().getResource("../images/icons/food_icon_4.png").toExternalForm()));
      } else if (request.getType().equals(ServiceType.InfoTech)) {
        serviceType.setImage(
            new Image(
                getClass()
                    .getResource("../images/icons/baseline_devices_blue_light.png")
                    .toExternalForm()));
      } else if (request.getType().equals(ServiceType.InternalTransport)) {
        serviceType.setImage(
            new Image(
                getClass()
                    .getResource("../images/icons/wheelchair_blue_light.png")
                    .toExternalForm()));
      } else if (request.getType().equals(ServiceType.Interpreter)) {
        serviceType.setImage(
            new Image(
                getClass()
                    .getResource("../images/icons/baseline_translate_blue_light.png")
                    .toExternalForm()));
      } else if (request.getType().equals(ServiceType.Medicine)) {
        serviceType.setImage(
            new Image(
                getClass()
                    .getResource("../images/icons/round_local_pharmacy_blue_light.png")
                    .toExternalForm()));
      } else if (request.getType().equals(ServiceType.Sanitation)) {
        serviceType.setImage(
            new Image(
                getClass()
                    .getResource("../images/icons/sanitation_icon_blue_light.png")
                    .toExternalForm()));
      } else if (request.getType().equals(ServiceType.Religious)) {
        serviceType.setImage(
            new Image(
                getClass().getResource("../images/icons/prayer_hands_blue.png").toExternalForm()));
      } else { // will display bus if it does not find service type
        serviceType.setImage(
            new Image(
                getClass()
                    .getResource("../images/icons/baseline_directions_bus_blue_light.png")
                    .toExternalForm()));
      }

      // set status
      if (request.getStatus().equals(Status.denied)) {
        status.setImage(
            new Image(
                getClass().getResource("../images/icons/status_rejected.png").toExternalForm()));
      } else if (request.getStatus().equals(Status.active)) {
        status.setImage(
            new Image(
                getClass().getResource("../images/icons/completed-icon.png").toExternalForm()));
      } else if (request.getStatus().equals(Status.pending)) {
        status.setImage(
            new Image(
                getClass().getResource("../images/icons/progress-icon.png").toExternalForm()));
      } else if (request.getStatus().equals(Status.closed)) {
        status.setImage(
            new Image(getClass().getResource("../images/icons/closed-icon.png").toExternalForm()));
      } else { // will display bus if it does not find status
        status.setImage(
            new Image(
                getClass()
                    .getResource("../images/icons/baseline_directions_bus_blue_light.png")
                    .toExternalForm()));
      }

      requesterLabel.setFont(new Font("Segoe UI", 12));
      locationLabel.setFont(new Font("Segoe UI", 12));
      setText(null);
      setGraphic(cellBox);
    }
  }
}
