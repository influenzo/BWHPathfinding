package edu.wpi.teamp.Controllers.ServiceRequest;

import edu.wpi.teamp.UIController;
import hospital.route.AbstractNode;
import hospital.service.ServiceController;
import hospital.service.ServiceRequest;
import hospital.service.ServiceType;
import java.io.IOException;

public class oServiceRequestController {

  UIController uController = UIController.getUIController();
  ServiceController sController = uController.getServiceController();

  private static class oServiceRequestControllerHelper {
    private static final oServiceRequestController sController = new oServiceRequestController();
  }

  public static oServiceRequestController getServiceRequestController() {
    return oServiceRequestControllerHelper.sController;
  }

  ServiceType serviceType;
  String additional = "";

  public ServiceType getServiceType() {
    return serviceType;
  }

  public void selectServiceType(ServiceType type) throws IOException {
    this.serviceType = type;
  }

  public void addAdditionalField(String field) {
    additional += field + "/";
  }

  public void clearAdditional() {
    additional = "";
  }

  public void createServiceRequest(AbstractNode location, String description, String requester)
      throws IOException {
    sController.addServiceRequest(
        new ServiceRequest(description, serviceType, requester, location, additional));
  }
}
