package edu.wpi.teamp;

import static org.junit.jupiter.api.Assertions.*;

import hospital.*;
import hospital.HospitalController;
import java.io.File;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class DatabaseTests extends ApplicationTest {

  public static HospitalController hController = AbstractNodeTests.hController;
  public static Hospital hospital201;
  public static Hospital hospital202;

  @BeforeAll
  static void initDatabase() {
    hospital201 = new Hospital("hospital 201");
    hospital202 = new Hospital("hospital 202");
    hController.addHospital(hospital201);
    hController.addHospital(hospital202);
  }

  /*@Test
  void testConnection() throws Exception {
    database.createConnection(brig);
    Statement statement = connection.createStatement();
    database.closeConnection();
    assertTrue(statement.isClosed());
  }*/

  @Test
  void testDownloadCSV() {
    hospital202.getRouteController().downloadCSV("C:/");
    File nodesFile = new File("C:/hospitalNodes.csv");
    File edgesFile = new File("C:/hospitalEdges.csv");
    nodesFile.delete();
    edgesFile.delete();
  }
}
