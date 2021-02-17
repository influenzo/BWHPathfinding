package edu.wpi.teamp;

import static org.junit.jupiter.api.Assertions.*;

import hospital.Hospital;
import hospital.HospitalController;
import hospital.employee.Employee;
import hospital.employee.EmployeeController;
import hospital.exceptions.DuplicateEmployeeIdException;
import hospital.exceptions.DuplicateUsernameException;
import hospital.exceptions.IncorrectPasswordException;
import hospital.exceptions.NullUsernameException;
import hospital.service.ServiceType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EmployeeTest {

  private static HospitalController hController = HospitalController.getHospitalController();
  private static EmployeeController eController;
  private static Hospital hospital401 = new Hospital("Hospital 401");;

  @BeforeAll
  static void initDatabase() {
    hController.addHospital(hospital401);
    eController = hospital401.getEmployeeController();
  }

  @Test
  void testAvailability() {
    Employee e101 =
        new Employee("e101", false, ServiceType.Sanitation, null, "TTTTTFF0917", "e101", "p101");
    LocalDateTime monday1045 =
        LocalDateTime.of(LocalDate.of(2020, 4, 20), LocalTime.of(10, 45, 00, 00));

    // Working Monday to Friday from 9AM-5PM so monday at 1045 should work
    assertTrue(e101.parseAvailability(monday1045));
    LocalDateTime monday0700 =
        LocalDateTime.of(LocalDate.of(2020, 4, 20), LocalTime.of(7, 00, 00, 00));

    // Working Monday to Friday from 9AM-5PM so monday at 700 should not work
    assertFalse(e101.parseAvailability(monday0700));

    e101.setAvailability("FFFFFTT0917");

    // Working Saturday and Sunday from 9AM-5PM so Monday at anytime will not work
    assertFalse(e101.parseAvailability(monday1045));

    e101.setAvailability("TTTTTFF2004");

    // Working Monday to Friday from 10PM-4AM so 7AM on monday not should work
    assertFalse(e101.parseAvailability(monday0700));

    LocalDateTime monday0200 =
        LocalDateTime.of(LocalDate.of(2020, 4, 20), LocalTime.of(2, 00, 00, 00));

    // Working Monday to Friday from 10PM-4AM so 2AM on monday should work
    assertTrue(e101.parseAvailability(monday0200));
  }

  @Test
  void unamePasswordGetter()
      throws DuplicateEmployeeIdException, DuplicateUsernameException, NullUsernameException,
          IncorrectPasswordException {
    Employee e201 =
        new Employee(
            "e201", false, ServiceType.Sanitation, null, "TTTTTFF0917", "username1", "password");
    Employee e202 =
        new Employee(
            "e202", false, ServiceType.Sanitation, null, "TTTTTFF0917", "username2", "password!");
    Employee e203 =
        new Employee(
            "e203", false, ServiceType.Sanitation, null, "TTTTTFF0917", "username3", "password12");
    Employee e204 =
        new Employee(
            "e204", false, ServiceType.Sanitation, null, "TTTTTFF0917", "username4", "p@ssword");
    Employee e205 =
        new Employee(
            "e205", false, ServiceType.Sanitation, null, "TTTTTFF0917", "username5", "pa$$word");
    Employee e206 =
        new Employee(
            "e206", false, ServiceType.Sanitation, null, "TTTTTFF0917", "username6", "passw0rd");

    eController.addEmployee(e201);
    eController.addEmployee(e202);
    eController.addEmployee(e203);
    eController.addEmployee(e204);
    eController.addEmployee(e205);
    eController.addEmployee(e206);

    Employee result = eController.getEmployee("username1", "password");

    assertEquals("e201", result.getName());

    assertThrows(
        IncorrectPasswordException.class, () -> eController.getEmployee("username2", "lala"));

    result = eController.getEmployee("username6", "passw0rd");

    assertEquals("e206", result.getName());

    assertThrows(
        NullUsernameException.class, () -> eController.getEmployee("username7", "password"));
  }

  @Test
  void getEmployeeIDs() throws DuplicateEmployeeIdException, DuplicateUsernameException {
    Employee e301 =
        new Employee("e301", false, ServiceType.Sanitation, null, "TTTTTFF0917", "u301", "p301");
    Employee e302 =
        new Employee("e302", false, ServiceType.Sanitation, null, "TTTTTFF0917", "u302", "p302");

    eController.addEmployee(e301);

    ArrayList<String> ids = hospital401.getTableIds("Employees");

    assertTrue(ids.contains(Long.toString(e301.getId())));
    assertFalse(ids.contains(Long.toString(e302.getId())));
  }

  @Test
  void sameIDException() throws DuplicateEmployeeIdException, DuplicateUsernameException {
    Employee e401 =
        new Employee(1, "e401", false, ServiceType.Sanitation, null, "TTTTTFF0917", "u401", "p401");
    Employee e402 =
        new Employee(1, "e402", false, ServiceType.Sanitation, null, "TTTTTFF0917", "u402", "p402");

    eController.addEmployee(e401);
    assertThrows(DuplicateEmployeeIdException.class, () -> eController.addEmployee(e402));
  }

  @Test
  void sameUsernameException() throws DuplicateEmployeeIdException, DuplicateUsernameException {
    Employee e501 =
        new Employee(
            "e501", false, ServiceType.Sanitation, null, "TTTTTFF0917", "sameusername", "p501");
    Employee e502 =
        new Employee(
            "e502", false, ServiceType.Sanitation, null, "TTTTTFF0917", "sameusername", "p502");

    eController.addEmployee(e501);

    assertThrows(
        DuplicateUsernameException.class,
        () -> {
          eController.addEmployee(e502);
        });
  }
}
