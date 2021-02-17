package hospital.employee;

import hospital.service.ServiceType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

public class Employee {

  private long id;
  private String name;
  private boolean isAdmin;
  private ServiceType service;
  private ArrayList<Language> languages = new ArrayList<>();
  private String username;
  private String password;
  private String availability;
  private boolean active;

  public Employee(
      long id,
      String name,
      boolean isAdmin,
      ServiceType service,
      ArrayList<Language> languages,
      String availability,
      String username,
      String password) {
    this.id = id;
    this.name = name;
    this.service = service;
    this.isAdmin = isAdmin;
    if (languages != null) this.languages = languages;
    this.availability = availability;
    this.username = username;
    this.password = password;
    active = false;
  }

  public Employee(
      String name,
      boolean isAdmin,
      ServiceType service,
      ArrayList<Language> languages,
      String availability,
      String username,
      String password) {
    this(
        Math.abs(new Random().nextLong()),
        name,
        isAdmin,
        service,
        languages,
        availability,
        username,
        password);
  }

  public boolean isAvailable() {
    return parseAvailability(LocalDateTime.now()) && !active;
  }

  public boolean parseAvailability(LocalDateTime dayTime) {
    int dayOfWeek = dayTime.getDayOfWeek().getValue() - 1;
    if (availability.charAt(dayOfWeek) == 'T') {
      int hour = dayTime.getHour();
      int startShift = Integer.parseInt(availability.substring(7, 9));
      int endShift = Integer.parseInt(availability.substring(9));
      if (startShift > endShift) {
        if (startShift <= hour || endShift > hour) {
          return true;
        }
      }
      if (startShift <= hour && endShift > hour) {
        return true;
      }
    }
    return false;
  }

  public long getId() {
    return id;
  }

  public String getStringID() {
    return String.valueOf(id);
  }

  public void setID(String id) {
    this.id = Long.parseLong(id);
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setAvalability(String avalability) {
    this.availability = avalability;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isAdmin() {
    return isAdmin;
  }

  public ArrayList<Language> getLanguages() {
    return languages;
  }

  public ServiceType getService() {
    return service;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getAvailability() {
    return availability;
  }

  public void setAvailability(String availability) {
    this.availability = availability;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public String toString() {
    return name;
  }

  public ArrayList<String> getLanguagesAsStrings() {
    ArrayList<String> languages = new ArrayList<>();
    for (Language l : this.languages) {
      languages.add(l.toString());
    }
    return languages;
  }
}
