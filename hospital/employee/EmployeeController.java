package hospital.employee;

import edu.wpi.teamp.UIController;
import hospital.Database;
import hospital.IController;
import hospital.MongoDB;
import hospital.exceptions.*;
import hospital.service.ServiceRequest;
import hospital.service.ServiceType;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EmployeeController implements IController {

  private HashMap<Long, Employee> employees = new HashMap<>();
  private List<String> usernames = new ArrayList<>();
  private static Database database = Database.getDatabase();
  private String hospitalName;
  private UIController uController = UIController.getUIController();
  private MongoDB mongoDB = MongoDB.getMongoDB();

  public EmployeeController(String hospitalName) {
    this.hospitalName = hospitalName;
  }

  public void addEmployee(Employee employee, int databaseFlag)
      throws DuplicateEmployeeIdException, DuplicateUsernameException {
    if (!usernames.contains(employee.getUsername())) {
      Employee putEmployee = employees.putIfAbsent(employee.getId(), employee);
      if (putEmployee == null) { // hospital.employee id is new
        usernames.add(employee.getUsername());
        if (databaseFlag == 1) {
          database.createConnection(hospitalName);
          database.updateEmployee(true, employee);
          database.closeConnection();
        } else if (databaseFlag == 2) {
          mongoDB.getDatabase(hospitalName);
          mongoDB.updateEmployee(true, employee);
        }
      } else throw new DuplicateEmployeeIdException();
    } else throw new DuplicateUsernameException(employee.getUsername());
  }

  // default addEmployee adds employee to database
  public void addEmployee(Employee employee)
      throws DuplicateEmployeeIdException, DuplicateUsernameException {
    addEmployee(employee, uController.getDatabaseFlag());
  }

  public void removeEmployee(long employeeId, int databaseFlag) {
    usernames.remove(employees.get(employeeId).getUsername());
    employees.remove(employeeId);
    if (databaseFlag == 1) {
      database.createConnection(hospitalName);
      database.removeEmployee(employeeId);
      database.closeConnection();
    } else if (databaseFlag == 2) {
      mongoDB.getDatabase(hospitalName);
      mongoDB.removeEmployee(employeeId);
    }
  }

  public void removeEmployee(long employeeId) {
    removeEmployee(employeeId, uController.getDatabaseFlag());
  }

  public void updateEmployee(Employee employee, int databaseFlag) {
    if (databaseFlag == 1) {
      database.createConnection(hospitalName);
      database.updateEmployee(false, employee);
      database.closeConnection();
    } else if (databaseFlag == 2) {
      mongoDB.getDatabase(hospitalName);
      mongoDB.updateEmployee(false, employee);
    }
  }

  public void updateEmployee(Employee employee) {
    updateEmployee(employee, uController.getDatabaseFlag());
  }

  public Employee getEmployee(String username, String password)
      throws IncorrectPasswordException, NullUsernameException {
    for (Long id : employees.keySet()) {
      Employee employee = employees.get(id);
      if (employee.getUsername().equals(username)) {
        if (employee.getPassword().equals(password)) return getEmployee(id);
        throw new IncorrectPasswordException();
      }
    }
    throw new NullUsernameException();
  }

  public List<Employee> getAvailableEmployees(ServiceRequest request) {
    List<Employee> available = new ArrayList<>();
    for (long id : employees.keySet()) {
      Employee employee = employees.get(id);
      if (employee.getService().equals(request.getType()) && employee.isAvailable()) {
        if (request.getType().equals(ServiceType.Interpreter)) {
          if (employee
              .getLanguages()
              .contains(Language.valueOf(request.getAdditional().split("/")[0]))) {
            available.add(employee);
          }
        } else available.add(employee);
      }
    }
    return available;
  }

  public void downloadCSV(String directory) {
    database.createConnection(hospitalName);
    database.exportEmployeesToCSV(directory);
    database.closeConnection();
  }

  public void clear() {
    employees = new HashMap<>();
    usernames = new ArrayList<>();
  }

  public Employee getEmployee(long employeeId) {
    return employees.get(employeeId);
  }

  public ObservableList<Employee> getEmployees() {
    List<Employee> employeeList = new ArrayList<>();
    for (Employee e : employees.values()) {
      employeeList.add(e);
    }
    return FXCollections.observableList(employeeList);
  }
}
