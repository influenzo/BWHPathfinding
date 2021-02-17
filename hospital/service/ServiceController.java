package hospital.service;

import edu.wpi.teamp.UIController;
import hospital.Database;
import hospital.IController;
import hospital.MongoDB;
import hospital.employee.Employee;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ServiceController implements IController {

  private HashMap<Long, ServiceRequest> requests = new HashMap<>();
  private static Database database = Database.getDatabase();
  private String hospitalName;
  private UIController uController = UIController.getUIController();
  private MongoDB mongoDB = MongoDB.getMongoDB();

  public ServiceController(String hospitalName) {
    this.hospitalName = hospitalName;
  }

  public void addServiceRequest(ServiceRequest request, int databaseFlag) {
    requests.putIfAbsent(request.getId(), request);
    if (databaseFlag == 1) {
      database.createConnection(hospitalName);
      database.updateServiceRequest(true, request);
      database.closeConnection();
    } else if (databaseFlag == 2) {
      mongoDB.getDatabase(hospitalName);
      mongoDB.updateServiceRequest(true, request);
    }
  }

  public void addServiceRequest(ServiceRequest request) {
    addServiceRequest(request, uController.getDatabaseFlag());
  }

  public void updateServiceRequest(ServiceRequest request, int databaseFlag) {
    if (databaseFlag == 1) {
      database.createConnection(hospitalName);
      database.updateServiceRequest(false, request);
      database.closeConnection();
    } else if (databaseFlag == 2) {
      mongoDB.getDatabase(hospitalName);
      mongoDB.updateServiceRequest(false, request);
    }
  }

  public void updateServiceRequest(ServiceRequest request) {
    updateServiceRequest(request, uController.getDatabaseFlag());
  }

  public ArrayList<ServiceRequest> getServiceRequests() {
    ArrayList<ServiceRequest> requestsList = new ArrayList<>();
    for (Long id : requests.keySet()) {
      requestsList.add(requests.get(id));
    }
    return requestsList;
  }

  public void approveServiceRequest(ServiceRequest request, Employee appointee) {
    request.approve(appointee);
    appointee.setActive(true);
    updateServiceRequest(request);
  }

  public void denyServiceRequest(ServiceRequest request) {
    request.deny();
    updateServiceRequest(request);
  }

  public void closeServiceRequest(ServiceRequest request) {
    request.close();
    updateServiceRequest(request);
  }

  public void downloadCSV(String directory) {
    database.createConnection(hospitalName);
    database.exportServiceRequestToCSV(directory);
    database.closeConnection();
  }

  public void clear() {
    requests = new HashMap<>();
  }

  public ServiceRequest getServiceRequest(long requestId) {
    return requests.get(requestId);
  }

  public ArrayList<ServiceRequest> getServiceRequests(ServiceType type) {
    ArrayList<ServiceRequest> requestsList = new ArrayList<>();
    for (Long id : requests.keySet()) {
      if (requests.get(id).getType() == type) {
        requestsList.add(requests.get(id));
      }
    }
    return requestsList;
  }

  public LinkedHashMap<String, Integer> getCountMap(ServiceType serviceType) {
    if (serviceType == ServiceType.AudioVisual) {
      return getCountMapAudioVisual();
    } else if (serviceType == ServiceType.Florist) {
      return getCountMapFlorist();
    } else if (serviceType == ServiceType.InfoTech) {
      return getCountMapInfoTech();
    } else if (serviceType == ServiceType.Interpreter) {
      return getCountMapInterpreter();
    } else if (serviceType == ServiceType.Religious) {
      return getCountMapReligion();
    } else if (serviceType == ServiceType.Sanitation) {
      return getCountMapSanitation();
    } else {
      return new LinkedHashMap<>();
    }
  }

  private LinkedHashMap<String, Integer> getCountMapAudioVisual() {
    LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();
    ArrayList<String> types =
        new ArrayList<>(
            Arrays.asList(
                "Audio Mixer",
                "Camera",
                "Camera Stand",
                "Light Box",
                "Microphone",
                "Microphone Stand",
                "Speaker",
                "Speaker Stand"));
    for (String type : types) counts.put(type, 0);

    ArrayList<ServiceRequest> requests = getServiceRequests(ServiceType.AudioVisual);

    for (ServiceRequest request : requests) {
      String additional = request.getAdditional();
      while (additional.contains("/")) {
        String type = additional.split("/")[0];
        additional = additional.substring(type.length() + 1);
        counts.put(type, counts.get(type) + 1);
      }
    }
    return counts;
  }

  private LinkedHashMap<String, Integer> getCountMapFlorist() {
    LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();
    ArrayList<String> types =
        new ArrayList<>(Arrays.asList("Bluebell", "Daisy", "Lily", "Orchid", "Rose", "Tulip"));
    for (String type : types) counts.put(type, 0);

    ArrayList<ServiceRequest> requests = getServiceRequests(ServiceType.Florist);

    for (ServiceRequest request : requests) {
      String additional = request.getAdditional();
      int i = 0;
      while (additional.contains("/")) {
        String count = additional.split("/")[0];
        additional = additional.substring(count.length() + 1);
        counts.put(types.get(i), counts.get(types.get(i)) + Integer.parseInt(count));
        i++;
      }
    }
    return counts;
  }

  private LinkedHashMap<String, Integer> getCountMapInfoTech() {
    LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();
    ArrayList<String> types =
        new ArrayList<>(
            Arrays.asList("Computer", "Email", "Network", "Phone", "Projector", "Software"));
    for (String type : types) counts.put(type, 0);

    ArrayList<ServiceRequest> requests = getServiceRequests(ServiceType.InfoTech);

    for (ServiceRequest request : requests) {
      String additional = request.getAdditional();
      String type = additional.split("/")[0];
      counts.put(type, counts.get(type) + 1);
    }
    return counts;
  }

  private LinkedHashMap<String, Integer> getCountMapInterpreter() {
    LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();
    ArrayList<String> types =
        new ArrayList<>(
            Arrays.asList("Spanish", "Mandarin", "French", "German", "Hindi", "Korean"));
    for (String type : types) counts.put(type, 0);

    ArrayList<ServiceRequest> requests = getServiceRequests(ServiceType.Interpreter);

    for (ServiceRequest request : requests) {
      String additional = request.getAdditional();
      String type = additional.split("/")[0];
      counts.put(type, counts.get(type) + 1);
    }
    return counts;
  }

  private LinkedHashMap<String, Integer> getCountMapReligion() {
    LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();
    ArrayList<String> types =
        new ArrayList<>(
            Arrays.asList("Christianity", "Bahai", "Buddhism", "Hinduism", "Islam", "Judaism"));
    for (String type : types) counts.put(type, 0);

    ArrayList<ServiceRequest> requests = getServiceRequests(ServiceType.Religious);

    for (ServiceRequest request : requests) {
      String additional = request.getAdditional();
      String type = additional.split("/")[0];
      counts.put(type, counts.get(type) + 1);
    }
    return counts;
  }

  private LinkedHashMap<String, Integer> getCountMapSanitation() {
    LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();
    ArrayList<String> types =
        new ArrayList<>(
            Arrays.asList("Missed", "Overflowing", "Bulky", "Spill", "Hazardous", "Miscellaneous"));
    for (String type : types) counts.put(type, 0);

    ArrayList<ServiceRequest> requests = getServiceRequests(ServiceType.Sanitation);

    for (ServiceRequest request : requests) {
      String additional = request.getAdditional();
      String type = additional.split("/")[0];
      counts.put(type, counts.get(type) + 1);
    }
    return counts;
  }
}
