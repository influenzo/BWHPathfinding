package hospital;

import edu.wpi.teamp.PApp;
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
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Hospital {

  private String name;
  private RouteController rController;
  private ServiceController sController;
  private EmployeeController eController;
  private Database database = Database.getDatabase();
  private MongoDB mongoDB = MongoDB.getMongoDB();
  private MongoDBThread mongoDBThread = null;
  private PropertyChangeSupport support = new PropertyChangeSupport(this);

  public Hospital(String name, double elevatorCost, double stairCost, String homeNodeId) {
    this.name = name;
    this.rController = new RouteController(name, elevatorCost, stairCost, homeNodeId);
    this.sController = new ServiceController(name);
    this.eController = new EmployeeController(name);
  }

  public Hospital(String name) {
    this(name, 10, 10, null);
  }

  public void populateLocalDatabase(
      String nodesFile, String edgesFile, String employeesFile, String requestsFile)
      throws IncompatibleNodeTypeException, NullHospitalException, DuplicateEmployeeIdException,
          DuplicateUsernameException {
    importNodes(nodesFile);
    importEdges(edgesFile);
    importEmployees(employeesFile);
    importServiceRequests(requestsFile);
  }

  public void clear() {
    sController.clear();
    rController.clear();
    eController.clear();
    support.firePropertyChange("database", null, this);
  }

  public void loadLocalDatabase() {
    database.createConnection(name);
    database.loadNodes(rController);
    database.loadEmployees(eController);
    database.loadServiceRequests(sController, eController, rController);
    toggleMongoThread(false);
  }

  public void loadMongoDatabase() {
    toggleMongoThread(true);
    mongoDB.getDatabase(name);
    mongoDB.loadNodes(rController);
    mongoDB.loadEmployees(eController);
    mongoDB.loadServiceRequests(sController, eController, rController);
  }

  public void toggleMongoThread(boolean active) {
    if (mongoDBThread == null) {
      mongoDBThread = new MongoDBThread();
      mongoDBThread.setHospital(this);
    }
    if (active && !mongoDBThread.isAlive()) {
      System.out.println("Starting MongoDB persistence thread...");
      mongoDBThread.start();
    } else if (active && mongoDBThread.isHalted()) {
      System.out.println("Renewing MongoDB persistence thread...");
      mongoDBThread.renew();
    } else if (!active && (mongoDBThread.isAlive() && !mongoDBThread.isHalted())) {
      System.out.println("Halting MongoDB persistence thread...");
      mongoDBThread.halt();
    }
  }

  public void importNodes(String filename)
      throws NullHospitalException, IncompatibleNodeTypeException {
    try {
      InputStream csvStream = PApp.class.getResourceAsStream(filename);
      BufferedReader lineReader = new BufferedReader(new InputStreamReader(csvStream));
      String text;

      lineReader.readLine();

      while ((text = lineReader.readLine()) != null) {
        String[] data = text.split(",");
        String id = data[0];
        double x = Double.parseDouble(data[1]);
        double y = Double.parseDouble(data[2]);
        int floor = Integer.parseInt(data[3]);
        // String building = data[4];
        String nodeType = data[5];
        String longName = data[6];
        String shortName = data[7];

        NodeType nodeTypeEnum = NodeType.valueOf(nodeType);

        AbstractNode node = null;
        if (nodeTypeEnum.isHallwayType()) {
          node = new HallwayNode(id, x, y, shortName, longName, nodeTypeEnum, floor, name, "P");
        } else if (nodeTypeEnum.isRoomType()) {
          node = new RoomNode(id, x, y, shortName, longName, nodeTypeEnum, floor, name, "P");
        }
        rController.addNode(node);
      }
      lineReader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void importEdges(String filename) {
    try {
      InputStream csvStream = PApp.class.getResourceAsStream(filename); // filename?
      BufferedReader lineReader = new BufferedReader(new InputStreamReader(csvStream));
      String text;

      lineReader.readLine();
      HashMap<String, AbstractNode> nodes = rController.getNodes();

      while ((text = lineReader.readLine()) != null) {
        String[] data = text.split(",");
        AbstractNode source = nodes.get(data[1]); // get source node by node id
        AbstractNode target = nodes.get(data[2]); // get target node by node id
        source.addAdjacency(target);
        target.addAdjacency(source);
      }

      for (AbstractNode node : rController.getNodesList()) {
        rController.updateNode(node);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void importEmployees(String filename)
      throws DuplicateEmployeeIdException, DuplicateUsernameException, NullHospitalException {
    try {
      InputStream csvStream = PApp.class.getResourceAsStream(filename);
      BufferedReader lineReader = new BufferedReader(new InputStreamReader(csvStream));
      String text;

      lineReader.readLine();

      while ((text = lineReader.readLine()) != null) {
        String[] data = text.split(",");
        long id = Long.parseLong(data[0]);
        String name = data[1];
        boolean isAdmin = Boolean.parseBoolean(data[2]);
        ServiceType service = ServiceType.valueOf(data[3]);
        String availability = data[5];
        String username = data[6];
        String password = data[7];

        ArrayList<Language> languages = new ArrayList<>();
        for (String s : data[4].split("/")) {
          if (!s.isEmpty() && !s.equals("null")) {
            languages.add(Language.valueOf(s));
          }
        }

        eController.addEmployee(
            new Employee(id, name, isAdmin, service, languages, availability, username, password));
      }

      lineReader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void importServiceRequests(String filename) {
    try {
      InputStream csvStream = PApp.class.getResourceAsStream(filename);
      BufferedReader lineReader = new BufferedReader(new InputStreamReader(csvStream));
      String text;

      lineReader.readLine();

      while ((text = lineReader.readLine()) != null) {
        String[] data = text.split(",");
        long id = Long.parseLong(data[0]);
        String description = data[1];
        ServiceType type = ServiceType.valueOf(data[2]);
        String requestor = data[3];
        Employee appointee =
            data[4].isEmpty() ? null : eController.getEmployee(Long.parseLong(data[4]));
        AbstractNode location = rController.getNode(data[5]);
        Status status = Status.valueOf(data[6]);
        String startTime = data[7].isEmpty() ? null : data[7];
        String closeTime = data[8].isEmpty() ? null : data[8];
        String additional = data[9];

        sController.addServiceRequest(
            new ServiceRequest(
                id,
                description,
                type,
                requestor,
                appointee,
                location,
                status,
                startTime,
                closeTime,
                additional));
      }

      lineReader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public ArrayList<String> getTableIds(String table) {
    database.createConnection(name);
    ArrayList<String> tableIds = database.getTableIds(table);
    database.closeConnection();
    return tableIds;
  }

  public RouteController getRouteController() {
    return rController;
  }

  public ServiceController getServiceController() {
    return sController;
  }

  public EmployeeController getEmployeeController() {
    return eController;
  }

  public String getName() {
    return name;
  }

  public MongoDBThread getMongoDBThread() {
    return mongoDBThread;
  }
}
