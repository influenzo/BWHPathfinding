package hospital;

import hospital.exceptions.NullHospitalException;
import hospital.route.*;
import java.util.HashMap;
import java.util.Set;

public class HospitalController {

  private HashMap<String, Hospital> hospitals = new HashMap<>();
  private Database database = Database.getDatabase();

  private static class HospitalControllerHelper {
    private static final HospitalController hController = new HospitalController();
  }

  public static HospitalController getHospitalController() {
    return HospitalControllerHelper.hController;
  }

  public void addHospital(Hospital hospital) {
    hospitals.put(hospital.getName(), hospital);
    database.createConnection(hospital.getName());
    database.createNodesTable();
    database.createServiceRequestsTable();
    database.createEmployeesTable();
    database.closeConnection();
  }

  public boolean hasHospital(String hospitalName) {
    return hospitals.keySet().contains(hospitalName);
  }

  public Hospital getHospital(String hospitalName) throws NullHospitalException {
    if (!hasHospital(hospitalName)) throw new NullHospitalException();
    return hospitals.get(hospitalName);
  }

  public Hospital getOtherHospital(Hospital currentHospital) {
    Set<String> keys = hospitals.keySet();
    for (String key : keys) {
      if (!key.equals(currentHospital.getName())) {
        return hospitals.get(key);
      }
    }
    return null;
  }
}
