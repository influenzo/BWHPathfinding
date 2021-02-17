package hospital;

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
import java.io.*;
import java.sql.*;
import java.util.*;

public class Database {

  private static Connection conn;

  private static class DatabaseHelper {
    private static final Database database = new Database();
  }

  public static Database getDatabase() {
    return DatabaseHelper.database;
  }

  public void createConnection(String hospitalName) {
    try {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver"); // registering the driver
      conn =
          DriverManager.getConnection(
              "jdbc:derby:memory:edu.wpi.teamp."
                  + hospitalName
                  + ";create=true"); // creating connection
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    }
  }

  public void closeConnection() {
    try {
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
      return;
    }
  }

  /*-------------------------*/
  /*       NODES TABLE       */
  /*-------------------------*/
  public void createNodesTable() {
    try {
      Statement stmt = conn.createStatement();
      String query =
          "CREATE TABLE Nodes("
              + "id VARCHAR(100) NOT NULL, "
              + "xcoord DOUBLE, "
              + "ycoord DOUBLE, "
              + "floor INT, "
              + "building VARCHAR(100),"
              + "nodeType VARCHAR(100), "
              + "longName VARCHAR(100), "
              + "shortName VARCHAR(100), "
              + "teamAssigned VARCHAR(100), "
              + "adjacencies VARCHAR(1000),"
              + "PRIMARY KEY (id))";
      stmt.execute(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void updateNode(boolean create, AbstractNode node) {
    try {
      String query;
      int i = 0; // dynamic indexer variable
      if (create) {
        query =
            "INSERT INTO Nodes (id, xcoord, ycoord, floor, building, nodeType, longName, shortName, "
                + "teamAssigned, adjacencies) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        i = 1;
      } else {
        query =
            "UPDATE Nodes SET xcoord = ?, ycoord = ?, floor = ?, building = ?, nodeType = ?, longName = ?, "
                + "shortName = ?, teamAssigned = ?, adjacencies = ? WHERE id = ?";
      }
      PreparedStatement stmt = conn.prepareStatement(query);

      List<String> stringList = new ArrayList<>(); // list of adjacency id's
      for (AbstractNode adjacency : node.getAdjacencies()) {
        stringList.add(adjacency.getId());
      }

      stmt.setDouble((1 + i), node.getX());
      stmt.setDouble((2 + i), node.getY());
      stmt.setInt((3 + i), node.getFloor());
      stmt.setString((4 + i), node.getHospitalName());
      stmt.setString((5 + i), String.valueOf(node.getNodeType()));
      stmt.setString((6 + i), node.getLongName());
      stmt.setString((7 + i), node.getShortName());
      stmt.setString((8 + i), node.getTeam());
      stmt.setString((9 + i), String.join(",", stringList));
      stmt.setString(i == 1 ? 1 : 10, node.getId());

      stmt.execute();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // loads existing database nodes
  public void loadNodes(RouteController rController) {
    try {
      Statement stmt = conn.createStatement();
      ResultSet results = stmt.executeQuery("SELECT * FROM Nodes");
      HashMap<String, AbstractNode> nodeMap = new HashMap<>();
      HashMap<String, String[]> adjacencyMap = new HashMap<>();

      while (results.next()) {
        String id = results.getString("id");
        double x = results.getLong("xcoord");
        double y = results.getLong("ycoord");
        int floor = results.getInt("floor");
        String hospital = results.getString("building");
        String nodeType = results.getString("nodeType");
        String longName = results.getString("longName");
        String shortName = results.getString("shortName");
        String team = results.getString("teamAssigned");
        String adjacencyString = results.getString("adjacencies");

        NodeType nodeTypeEnum = NodeType.valueOf(nodeType);

        AbstractNode node;
        if (nodeTypeEnum.isHallwayType()) {
          node =
              new HallwayNode(id, x, y, shortName, longName, nodeTypeEnum, floor, hospital, team);
        } else {
          node = new RoomNode(id, x, y, shortName, longName, nodeTypeEnum, floor, hospital, team);
        }
        rController.addNode(node, 0);
        nodeMap.put(id, node);
        adjacencyMap.put(id, adjacencyString.split(","));
      }

      for (String id : adjacencyMap.keySet()) {
        for (String adjacency : adjacencyMap.get(id)) {
          if (!adjacency.isEmpty()) {
            nodeMap.get(id).addAdjacency(nodeMap.get(adjacency));
          }
        }
      }
    } catch (SQLException | IncompatibleNodeTypeException | NullHospitalException e) {
      e.printStackTrace();
    }
  }

  public void removeNode(String id) {
    try {
      String query = "DELETE FROM Nodes WHERE id = ?";
      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setString(1, id);
      stmt.execute();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void exportNodesToCSV(String directory) {
    try {
      File csv = createCSV(directory + "hospitalNodes");
      PrintStream fileStream = new PrintStream(csv);
      Statement stmt = conn.createStatement();
      ResultSet results = stmt.executeQuery("SELECT * FROM Nodes");
      ResultSetMetaData metadata = results.getMetaData();
      int columnCount = metadata.getColumnCount() - 1;
      List<String> columnNames = new ArrayList<>();
      for (int i = 1; i <= columnCount; i++) {
        columnNames.add(metadata.getColumnName(i).toLowerCase());
      }
      fileStream.println(String.join(",", columnNames));
      while (results.next()) {
        List<String> stringList = new ArrayList<>();
        for (String column : columnNames) {
          if (column.equals("xcoord") || column.equals("ycoord")) {
            stringList.add(Integer.toString((int) results.getDouble(column))); // doubles to int
          } else {
            stringList.add(results.getString(column));
          }
        }
        fileStream.println(String.join(",", stringList));
      }
      results.close();
    } catch (SQLException | FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void exportEdgesToCSV(String directory, RouteController rController) {
    try {
      File csv = createCSV(directory + "hospitalEdges");
      PrintStream fileStream = new PrintStream(csv);
      fileStream.println("id,startNode,endNode");

      ArrayList<ArrayList<AbstractNode>> edges = rController.getEdges();
      for (ArrayList<AbstractNode> edge : edges) {
        String edgeId = edge.get(0).getId() + "_" + edge.get(1).getId();
        String[] stringList = {edgeId, edge.get(0).getId(), edge.get(1).getId()};
        fileStream.println(String.join(",", stringList));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /*-------------------------*/
  /*     EMPLOYEES TABLE     */
  /*-------------------------*/
  public void createEmployeesTable() {
    try {
      Statement stmt = conn.createStatement();
      String query =
          "CREATE TABLE Employees("
              + "id BIGINT NOT NULL,"
              + "name VARCHAR(100),"
              + "isAdmin BOOLEAN,"
              + "service VARCHAR(100),"
              + "languages VARCHAR(100),"
              + "availability VARCHAR(20),"
              + "username VARCHAR(50), "
              + "password VARCHAR(50), "
              + "PRIMARY KEY (id))";
      stmt.execute(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void removeEmployee(long id) {
    try {
      String query = "DELETE FROM Employees WHERE id = ?";
      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setString(1, "" + id);
      stmt.execute();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void updateEmployee(boolean create, Employee employee) {
    try {
      String query;
      int i = 0; // dynamic id indexer variable
      if (create) {
        query =
            "INSERT INTO Employees (id, name, isAdmin, service, languages, availability, username, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        i = 1;
      } else {
        query =
            "UPDATE Employees SET name = ?, isAdmin = ?, service = ?, languages = ?, availability = ?, username = ?, password = ? WHERE id = ?";
      }
      PreparedStatement stmt = conn.prepareStatement(query);

      String languages;
      if (employee.getLanguages().size() > 0) {
        ArrayList<String> languageList = new ArrayList<>();
        for (Language language : employee.getLanguages()) {
          languageList.add(language.toString());
        }
        languages = String.join("/", languageList);
      } else languages = "null";

      stmt.setString((1 + i), employee.getName());
      stmt.setString((2 + i), String.valueOf(employee.isAdmin()));
      stmt.setString((3 + i), String.valueOf(employee.getService()));
      stmt.setString((4 + i), languages);
      stmt.setString((5 + i), employee.getAvailability());
      stmt.setString((6 + i), employee.getUsername());
      stmt.setString((7 + i), employee.getPassword());
      stmt.setLong(i == 1 ? 1 : 8, employee.getId());

      stmt.execute();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // loads existing database employees
  public void loadEmployees(EmployeeController eController) {
    try {
      Statement stmt = conn.createStatement();
      ResultSet results = stmt.executeQuery("SELECT * FROM Employees");

      while (results.next()) {
        long id = results.getLong("id");
        String name = results.getString("name");
        boolean isAdmin = results.getBoolean("isAdmin");
        ServiceType service = ServiceType.valueOf(results.getString("service"));
        String languageString = results.getString("languages");
        String availability = results.getString("availability");
        ;
        String username = results.getString("username");
        String password = results.getString("password");

        ArrayList<Language> languages = new ArrayList<>();
        for (String s : languageString.split("/")) {
          if (!s.isEmpty() && !s.equals("null")) {
            languages.add(Language.valueOf(s));
          }
        }

        eController.addEmployee(
            new Employee(id, name, isAdmin, service, languages, availability, username, password),
            0);
      }
    } catch (SQLException | DuplicateEmployeeIdException | DuplicateUsernameException e) {
      e.printStackTrace();
    }
  }

  public void exportEmployeesToCSV(String directory) {
    try {
      File csv = createCSV(directory + "hospitalEmployees");
      PrintStream fileStream = new PrintStream(csv);
      Statement stmt = conn.createStatement();
      ResultSet results = stmt.executeQuery("SELECT * FROM Employees");
      ResultSetMetaData metadata = results.getMetaData();
      int columnCount = metadata.getColumnCount();
      List<String> columnNames = new ArrayList<>();
      for (int i = 1; i <= columnCount; i++) {
        columnNames.add(metadata.getColumnName(i).toLowerCase());
      }
      fileStream.println(String.join(",", columnNames));
      while (results.next()) {
        List<String> stringList = new ArrayList<>();
        for (String column : columnNames) {
          stringList.add(results.getString(column));
        }
        fileStream.println(String.join(",", stringList));
      }
      results.close();
    } catch (SQLException | FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /*-------------------------*/
  /* SERVICE REQUESTS TABLE  */
  /*-------------------------*/
  public void createServiceRequestsTable() {
    try {
      Statement stmt = conn.createStatement();
      String query =
          "CREATE TABLE serviceRequests("
              + "id BIGINT NOT NULL,"
              + "description VARCHAR(1000),"
              + "type VARCHAR(100),"
              + "requester VARCHAR(100),"
              + "appointeeId BIGINT,"
              + "locationId VARCHAR(50),"
              + "status VARCHAR(100),"
              + "startTime VARCHAR(20),"
              + "closeTime VARCHAR(20),"
              + "additional VARCHAR(255),"
              + "PRIMARY KEY (id))";
      stmt.execute(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void updateServiceRequest(boolean create, ServiceRequest request) {
    try {
      String query;
      int i = 0; // dynamic id indexer variable
      if (create) {
        query =
            "INSERT INTO serviceRequests (id, description, type, requester, appointeeId, locationId, status, startTime, closeTime, additional) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        i = 1;
      } else {
        query =
            "UPDATE serviceRequests SET description = ?, type = ?, requester = ?, appointeeId = ?, locationId = ?, status = ?, startTime = ?, closeTime = ?, additional = ? WHERE id = ?";
      }
      PreparedStatement stmt = conn.prepareStatement(query);

      stmt.setString((1 + i), request.getDescription());
      stmt.setString((2 + i), request.getType().toString());
      stmt.setString((3 + i), "" + request.getRequester());
      stmt.setLong((4 + i), request.getAppointee() == null ? -1 : request.getAppointee().getId());
      stmt.setString((5 + i), request.getLocation() == null ? null : request.getLocation().getId());
      stmt.setString((6 + i), request.getStatus().toString());
      stmt.setString((7 + i), request.getStartTime());
      stmt.setString((8 + i), request.getCloseTime());
      stmt.setString((9 + i), request.getAdditional());
      stmt.setLong(i == 1 ? 1 : 10, request.getId());
      stmt.execute();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // loads existing database service requests
  public void loadServiceRequests(
      ServiceController sController, EmployeeController eController, RouteController rController) {
    try {
      Statement stmt = conn.createStatement();
      ResultSet results = stmt.executeQuery("SELECT * FROM serviceRequests");

      while (results.next()) {
        long id = results.getLong("id");
        String description = results.getString("description");
        ServiceType type = ServiceType.valueOf(results.getString("type"));
        String requester = results.getString("requester");
        long appointeeId = results.getLong("appointeeId");
        Employee appointee = appointeeId == -1 ? null : eController.getEmployee(appointeeId);
        AbstractNode location = rController.getNode(results.getString("locationId"));
        Status status = Status.valueOf(results.getString("status"));
        String startTime = results.getString("startTime");
        String closeTime = results.getString("closeTime");
        String additional = results.getString("additional");

        sController.addServiceRequest(
            new ServiceRequest(
                id,
                description,
                type,
                requester,
                appointee,
                location,
                status,
                startTime,
                closeTime,
                additional),
            0);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void exportServiceRequestToCSV(String directory) {
    try {
      File csv = createCSV(directory + "hospitalServiceRequests");
      PrintStream fileStream = new PrintStream(csv);
      Statement stmt = conn.createStatement();
      ResultSet results = stmt.executeQuery("SELECT * FROM serviceRequests");
      ResultSetMetaData metadata = results.getMetaData();
      int columnCount = metadata.getColumnCount() - 1;
      List<String> columnNames = new ArrayList<>();
      for (int i = 1; i <= columnCount; i++) {
        columnNames.add(metadata.getColumnName(i).toLowerCase());
      }
      fileStream.println(String.join(",", columnNames));
      while (results.next()) {
        List<String> stringList = new ArrayList<>();
        for (String column : columnNames) {
          stringList.add(results.getString(column));
        }
        fileStream.println(String.join(",", stringList));
      }
      results.close();
    } catch (SQLException | FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private File createCSV(String path) {
    File csv = new File(path + ".csv");
    if (csv.exists()) {
      int i = 1;
      while (csv.exists()) {
        csv = new File(path + "(" + i + ").csv");
        i++;
      }
    }
    try {
      csv.createNewFile();
    } catch (IOException e) {
      System.out.println("File already exists.");
    }
    return csv;
  }

  public ArrayList<String> getTableIds(String table) {
    ArrayList<String> tableIds = new ArrayList<>();
    try {
      Statement stmt = conn.createStatement();
      ResultSet results = stmt.executeQuery("SELECT * FROM " + table);
      while (results.next()) {
        tableIds.add(results.getString("id"));
      }
      results.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return tableIds;
  }
}
