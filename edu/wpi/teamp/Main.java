package edu.wpi.teamp;

import hospital.Hospital;
import hospital.HospitalController;
import hospital.exceptions.DuplicateEmployeeIdException;
import hospital.exceptions.DuplicateUsernameException;
import hospital.exceptions.IncompatibleNodeTypeException;
import hospital.exceptions.NullHospitalException;

public class Main {

  public static void main(String[] args) {

    HospitalController hController = HospitalController.getHospitalController();
    UIController.getUIController();
    Hospital faulkner = new Hospital("Faulkner", 60000, 90000, "PDEPT01205");
    hController.addHospital(faulkner);
    Hospital mainHospital = new Hospital("Main", 60000, 90000, "FSERV00501");
    hController.addHospital(mainHospital);

    try {
      faulkner.populateLocalDatabase(
          "nodes/MapPAllNodes.csv",
          "edges/MapPAllEdges.csv",
          "employees/EmployeeList.csv",
          "requests/RequestList.csv");
      mainHospital.populateLocalDatabase(
          "nodes/mainHospitalNodes.csv",
          "edges/mainHospitalEdges.csv",
          "employees/MainHospitalEmployeeList.csv",
          "requests/MainHospitalRequestList.csv");
    } catch (NullHospitalException
        | IncompatibleNodeTypeException
        | DuplicateUsernameException
        | DuplicateEmployeeIdException e) {
      e.printStackTrace();
    }

    // DON"T FUCKING TOUCH, DON'T DESTROY MY DATABASE
    //    MongoDB mongoDB = MongoDB.getMongoDB();
    //    mongoDB.getDatabase("faulkner");
    //    mongoDB.clearNodes();
    //    mongoDB.clearEmployees();
    //    mongoDB.clearServiceRequests();
    //
    //    for (AbstractNode n : mainHospital.getRouteController().getNodesList()) {
    //      mongoDB.updateNode(true, n);
    //    }
    //    for (Employee e : mainHospital.getEmployeeController().getEmployees()) {
    //      mongoDB.updateEmployee(true, e);
    //    }
    //    for (ServiceRequest s : mainHospital.getServiceController().getServiceRequests()) {
    //      mongoDB.updateServiceRequest(true, s);
    //    }

    PApp.launch(PApp.class, args);
  }
}
