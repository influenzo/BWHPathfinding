package hospital;

import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.OperationType;
import edu.wpi.teamp.Controllers.AnalyticsController;
import edu.wpi.teamp.UIController;
import hospital.employee.EmployeeController;
import hospital.route.AbstractNode;
import hospital.route.RouteController;
import hospital.service.ServiceController;
import hospital.service.ServiceRequest;
import java.util.ArrayList;
import javafx.application.Platform;
import org.bson.Document;

public class MongoDBThread extends Thread {

  private MongoDB mongoDB = MongoDB.getMongoDB();
  private UIController uController = UIController.getUIController();
  private RouteController rController;
  private EmployeeController eController;
  private ServiceController sController;
  private ChangeStreamIterable<Document> changeStream;
  private boolean halted;
  private AnalyticsController analyticsController;

  @Override
  public void run() {
    MongoDatabase database = mongoDB.getDatabaseExplicit(rController.getHospitalName());
    changeStream = database.watch();
    changeStream.forEach(
        change -> {
          if (!halted) {
            String collection = change.getNamespace().toString().split("\\.")[1];
            if (change.getOperationType() == OperationType.REPLACE) {
              if (collection.equals("nodes")) {
                AbstractNode node =
                    mongoDB.getNodeFromDocument(change.getFullDocument(), rController);
                rController.addNode(node, 0); // updates existing node
                ArrayList<String> adjacencyIds =
                    (ArrayList) change.getFullDocument().get("adjacencies");
                for (String id : adjacencyIds) {
                  node.addAdjacency(rController.getNode(id));
                }
              }
            } else if (change.getOperationType() == OperationType.INSERT) {
              if (collection.equals("service-requests")) {
                ServiceRequest request =
                    mongoDB.getServiceRequestFromDocument(change.getFullDocument());
                request.setAppointee(
                    change.getFullDocument().getLong("appointeeId") == null
                        ? null
                        : eController.getEmployee(change.getFullDocument().getLong("appointeeId")));
                request.setLocation(
                    rController.getNode(change.getFullDocument().getString("locationId")));
                sController.addServiceRequest(request, 0);
                if (analyticsController != null) {
                  Platform.runLater(() -> analyticsController.updateData());
                }
              }
            }
          }
        });
  }

  public void halt() {
    this.halted = true;
  }

  public void renew() {
    this.halted = false;
  }

  public boolean isHalted() {
    return halted;
  }

  public void setAnalyticsController(AnalyticsController controller) {
    analyticsController = controller;
  }

  public void setHospital(Hospital hospital) {
    rController = hospital.getRouteController();
    eController = hospital.getEmployeeController();
    sController = hospital.getServiceController();
  }
}
