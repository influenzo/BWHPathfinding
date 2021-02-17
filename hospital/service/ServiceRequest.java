package hospital.service;

import hospital.employee.Employee;
import hospital.route.AbstractNode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ServiceRequest {

  private long id;
  private String description;
  private ServiceType type; // typeCol
  private String requester; // requesterCol
  private Employee appointee; //
  private AbstractNode location;
  private Status status;
  private String startTime;
  private String closeTime;
  private String additional;
  private static DateFormat dateFormat = new SimpleDateFormat("h:mm aa");

  public ServiceRequest(
      long id,
      String description,
      ServiceType type,
      String requester,
      Employee appointee,
      AbstractNode location,
      Status status,
      String startTime,
      String closeTime,
      String additional) {
    this.id = id;
    this.description = description;
    this.type = type;
    this.requester = requester;
    this.appointee = appointee;
    this.location = location;
    this.status = status;
    this.startTime = startTime;
    this.closeTime = closeTime;
    this.additional = additional;
  }

  public ServiceRequest(
      String description,
      ServiceType type,
      String requester,
      AbstractNode location,
      String additional) {
    this(
        Math.abs(new Random().nextLong()),
        description,
        type,
        requester,
        null,
        location,
        Status.pending,
        String.valueOf(new Date().getTime()),
        null,
        additional);
  }

  public void approve(Employee employee) {
    status = Status.active;
    appointee = employee;
  }

  public void deny() {
    status = Status.denied;
    closeTime = String.valueOf(new Date().getTime());
  }

  public void close() {
    status = Status.closed;
    closeTime = String.valueOf(new Date().getTime());
  }

  public long getId() {
    return id;
  }

  public AbstractNode getLocation() {
    return location;
  }

  public String getRequester() {
    return requester;
  }

  public Employee getAppointee() {
    return appointee;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getDescription() {
    return description;
  }

  public ServiceType getType() {
    return type;
  }

  public String getStartTime() {
    return startTime;
  }

  public String getCloseTime() {
    return closeTime;
  }

  public void setCloseTime(String closeTime) {
    this.closeTime = closeTime;
  }

  public String getAdditional() {
    return additional;
  }

  public void setAppointee(Employee appointee) {
    this.appointee = appointee;
  }

  public void setLocation(AbstractNode location) {
    this.location = location;
  }
}
