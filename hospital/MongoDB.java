package hospital;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import hospital.employee.Employee;
import hospital.employee.EmployeeController;
import hospital.employee.Language;
import hospital.exceptions.DuplicateEmployeeIdException;
import hospital.exceptions.DuplicateUsernameException;
import hospital.exceptions.IncompatibleNodeTypeException;
import hospital.exceptions.NullHospitalException;
import hospital.route.*;
import hospital.service.ServiceController;
import hospital.service.ServiceRequest;
import hospital.service.ServiceType;
import hospital.service.Status;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import org.bson.Document;

public class MongoDB {

  private static class MongoDBHelper {
    private static final MongoDB mongoDB = new MongoDB();
  }

  public static MongoDB getMongoDB() {
    return MongoDBHelper.mongoDB;
  }

  ConnectionString connectionString =
      new ConnectionString(
          "mongodb+srv://admin:4tpWbamP6P731TiI@hospital-db-d0j18.mongodb.net/test?w=majority");

  MongoClientSettings settings =
      MongoClientSettings.builder()
          .applyConnectionString(connectionString)
          .retryWrites(true)
          .retryReads(true)
          .build();

  private MongoClient client = MongoClients.create(settings);
  private HashMap<String, MongoDatabase> databases =
      new HashMap<>() {
        {
          put("faulkner", client.getDatabase("faulkner"));
          put("main", client.getDatabase("main"));
        }
      };

  private MongoDatabase database;

  public Object getDatabase(String hospitalName) {
    return database = databases.get(hospitalName.toLowerCase());
  }

  public MongoDatabase getDatabaseExplicit(String hospitalName) {
    return databases.get(hospitalName.toLowerCase());
  }

  /*--------------------------*/
  /*     NODES COLLECTION     */
  /*--------------------------*/
  public void updateNode(boolean create, AbstractNode node) {
    MongoCollection collection = database.getCollection("nodes");

    if (create) {
      Document document =
          new Document("_id", node.getId())
              .append("x", node.getX())
              .append("y", node.getY())
              .append("floor", node.getFloor())
              .append("nodeType", node.getNodeType().toString())
              .append("longName", node.getLongName())
              .append("shortName", node.getShortName())
              .append("teamAssigned", node.getTeam())
              .append("adjacencies", node.getAdjacenciesAsStrings());

      collection.insertOne(document);
    } else {
      collection.updateOne(
          eq("_id", node.getId()),
          combine(
              set("x", node.getX()),
              set("y", node.getY()),
              set("floor", node.getFloor()),
              set("nodeType", node.getNodeType().toString()),
              set("longName", node.getLongName()),
              set("shortName", node.getShortName()),
              set("teamAssigned", node.getTeam()),
              set("adjacencies", node.getAdjacenciesAsStrings()),
              currentDate("lastModified")));
    }
  }

  // removes node from the database
  public void removeNode(String id) {
    MongoCollection collection = database.getCollection("nodes");
    collection.deleteOne(eq("_id", id));
  }

  // gets node by id and prints it out
  public void getNode(String id) {
    MongoCollection<Document> collection = database.getCollection("nodes");

    Document myDoc = collection.find(eq("_id", id)).first();
    System.out.println(myDoc);
  }

  // prints all nodes
  public void printNode() {
    MongoCollection collection = database.getCollection("nodes");

    MongoCursor<Document> cursor = collection.find().iterator();
    try {
      while (cursor.hasNext()) {
        System.out.println(cursor.next().toJson());
      }
    } finally {
      cursor.close();
    }
  }

  // clears nodes database
  public void clearNodes() {
    MongoCollection collection = database.getCollection("nodes");
    collection.drop();
    database.createCollection("nodes");
  }

  // loads nodes from database into memory
  public void loadNodes(RouteController rController) {
    MongoCollection collection = database.getCollection("nodes");
    HashMap<String, AbstractNode> nodeMap = new HashMap<>();
    HashMap<String, ArrayList<String>> adjacencyMap = new HashMap<>();

    Block<Document> forEachBlock =
        document -> {
          AbstractNode node = getNodeFromDocument(document, rController);
          rController.addNode(node, 0);
          nodeMap.put(node.getId(), node);
          adjacencyMap.put(node.getId(), (ArrayList) document.get("adjacencies"));
        };

    collection.find(new Document()).forEach((Consumer<Document>) forEachBlock::apply);

    for (String id : adjacencyMap.keySet()) {
      for (String adjacency : adjacencyMap.get(id)) {
        if (adjacency != "") {
          nodeMap.get(id).addAdjacency(nodeMap.get(adjacency));
        }
      }
    }
  }

  public AbstractNode getNodeFromDocument(Document document, RouteController rController) {
    String id = document.getString("_id");
    double x;
    double y;
    try {
      x = Double.valueOf(document.getDouble("x"));
      y = Double.valueOf(document.getDouble("y"));
    } catch (ClassCastException e) {
      x = Double.valueOf(document.getInteger("x"));
      y = Double.valueOf(document.getInteger("y"));
    }
    int floor = document.getInteger("floor");
    String nodeType = document.getString("nodeType");
    String longName = document.getString("longName");
    String shortName = document.getString("shortName");
    String team = document.getString("teamAssigned");

    NodeType nodeTypeEnum = NodeType.valueOf(nodeType);
    AbstractNode node = null;
    try {
      if (nodeTypeEnum.isHallwayType()) {
        node =
            new HallwayNode(
                id,
                x,
                y,
                shortName,
                longName,
                nodeTypeEnum,
                floor,
                rController.getHospitalName(),
                team);
      } else {
        node =
            new RoomNode(
                id,
                x,
                y,
                shortName,
                longName,
                nodeTypeEnum,
                floor,
                rController.getHospitalName(),
                team);
      }
    } catch (IncompatibleNodeTypeException | NullHospitalException e) {
      e.printStackTrace();
    }
    return node;
  }

  /*------------------------------*/
  /*     EMPLOYEES COLLECTION     */
  /*------------------------------*/
  public void updateEmployee(boolean create, Employee employee) {
    MongoCollection collection = database.getCollection("employees");

    if (create) {
      Document document =
          new Document("_id", employee.getId())
              .append("name", employee.getName())
              .append("isAdmin", employee.isAdmin())
              .append("service", employee.getService().toString())
              .append("languages", employee.getLanguagesAsStrings())
              .append("availability", employee.getAvailability())
              .append("username", employee.getUsername())
              .append("password", employee.getPassword());

      collection.insertOne(document);
    } else {
      collection.updateOne(
          eq("_id", employee.getId()),
          combine(
              set("name", employee.getName()),
              set("isAdmin", employee.isAdmin()),
              set("service", employee.getService().toString()),
              set("languages", employee.getLanguagesAsStrings()),
              set("availability", employee.getAvailability()),
              set("username", employee.getUsername()),
              set("password", employee.getPassword()),
              currentDate("lastModified")));
    }
  }

  // removes employee from database
  public void removeEmployee(long id) {
    MongoCollection collection = database.getCollection("employees");
    collection.deleteOne(eq("_id", id));
  }

  // gets employee by id and prints it out
  public void getEmployee(Long id) {
    MongoCollection<Document> collection = database.getCollection("employees");

    Document myDoc = collection.find(eq("_id", id)).first();
    System.out.println(myDoc);
  }

  // prints all employees
  public void printEmployee() {
    MongoCollection collection = database.getCollection("employees");

    MongoCursor<Document> cursor = collection.find().iterator();
    try {
      while (cursor.hasNext()) {
        System.out.println(cursor.next().toJson());
      }
    } finally {
      cursor.close();
    }
  }

  // clears employees collection
  public void clearEmployees() {
    MongoCollection collection = database.getCollection("employees");
    collection.drop();
    database.createCollection("employees");
  }

  // loads employees from database into memory
  public void loadEmployees(EmployeeController eController) {
    MongoCollection collection = database.getCollection("employees");

    Block<Document> forEachBlock =
        new Block<Document>() {
          @Override
          public void apply(final Document document) {
            long id = document.getLong("_id");
            String name = document.getString("name");
            boolean isAdmin = document.getBoolean("isAdmin");
            ServiceType service = ServiceType.valueOf(document.getString("service"));
            ArrayList<String> languageStrings = (ArrayList) document.get("languages");
            String availability = document.getString("availability");
            String username = document.getString("username");
            String password = document.getString("password");

            ArrayList<Language> languages = new ArrayList<>();
            for (String s : languageStrings) {
              languages.add(Language.valueOf(s));
            }
            try {
              eController.addEmployee(
                  new Employee(
                      id, name, isAdmin, service, languages, availability, username, password),
                  0);
            } catch (DuplicateUsernameException | DuplicateEmployeeIdException e) {
              e.printStackTrace();
            }
          }
        };

    collection.find(new Document()).forEach((Consumer<Document>) forEachBlock::apply);
  }

  /*-------------------------------------*/
  /*     SERVICE REQUESTS COLLECTION     */
  /*-------------------------------------*/
  public void updateServiceRequest(boolean create, ServiceRequest request) {
    MongoCollection collection = database.getCollection("service-requests");

    if (create) {
      Document document =
          new Document("_id", request.getId())
              .append("description", request.getDescription())
              .append("type", request.getType().toString())
              .append("requester", request.getRequester())
              .append("appointeeId", request.getAppointee())
              .append(
                  "locationId",
                  request.getLocation() == null ? null : request.getLocation().getId())
              .append("status", request.getStatus().toString())
              .append("startTime", request.getStartTime())
              .append("closeTime", request.getCloseTime())
              .append("additional", request.getAdditional());

      collection.insertOne(document);
    } else {
      collection.updateOne(
          eq("_id", request.getId()),
          combine(
              set("description", request.getDescription()),
              set("type", request.getType().toString()),
              set("requester", request.getRequester()),
              set(
                  "appointeeId",
                  request.getAppointee() == null ? null : request.getAppointee().getId()),
              set("locationId", request.getLocation().getId()),
              set("status", request.getStatus().toString()),
              set("startTime", request.getStartTime()),
              set("closeTime", request.getCloseTime()),
              set("additional", request.getAdditional()),
              currentDate("lastModified")));
    }
  }

  // removes service request from database
  public void removeServiceRequest(String id) {
    MongoCollection collection = database.getCollection("service-requests");
    collection.deleteOne(eq("_id", id));
  }

  // loads serviceRequests from database into memory
  public void loadServiceRequests(
      ServiceController sController, EmployeeController eController, RouteController rController) {
    MongoCollection collection = database.getCollection("service-requests");

    Block<Document> forEachBlock =
        new Block<Document>() {
          @Override
          public void apply(final Document document) {
            ServiceRequest request = getServiceRequestFromDocument(document);
            request.setAppointee(
                document.getLong("appointeeId") == null
                    ? null
                    : eController.getEmployee(document.getLong("appointeeId")));
            request.setLocation(rController.getNode(document.getString("locationId")));

            sController.addServiceRequest(request, 0);
          }
        };

    collection.find(new Document()).forEach((Consumer<Document>) forEachBlock::apply);
  }

  public ServiceRequest getServiceRequestFromDocument(Document document) {
    long id = document.getLong("_id");
    String description = document.getString("description");
    ServiceType type = ServiceType.valueOf(document.getString("type"));
    String requester = document.getString("requester");
    Status status = Status.valueOf(document.getString("status"));
    String startTime = document.getString("startTime");
    String closeTime = document.getString("closeTime");
    String additional = document.getString("additional");

    return new ServiceRequest(
        id, description, type, requester, null, null, status, startTime, closeTime, additional);
  }

  // gets service request from database and prints it out
  public void getServiceRequest(String id) {
    MongoCollection<Document> collection = database.getCollection("service-requests");

    Document myDoc = collection.find(eq("_id", id)).first();
    System.out.println(myDoc);
  }

  // prints all service requests out
  public void printServiceRequest() {
    MongoCollection collection = database.getCollection("service-requests");

    MongoCursor<Document> cursor = collection.find().iterator();
    try {
      while (cursor.hasNext()) {
        System.out.println(cursor.next().toJson());
      }
    } finally {
      cursor.close();
    }
  }

  // clears service requests collection
  public void clearServiceRequests() {
    MongoCollection collection = database.getCollection("service-requests");
    collection.drop();
    database.createCollection("service-requests");
  }

  public MongoCollection getCollection(String collectionName) {
    return database.getCollection(collectionName);
  }
}
