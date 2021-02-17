package edu.wpi.teamp;

import com.google.maps.GeoApiContext;
import edu.wpi.teamp.Controllers.NavigationController;
import hospital.Hospital;
import hospital.HospitalController;
import hospital.employee.Employee;
import hospital.employee.EmployeeController;
import hospital.exceptions.NullHospitalException;
import hospital.route.RouteController;
import hospital.service.ServiceController;
import java.io.IOException;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class UIController {

  private HospitalController hController = HospitalController.getHospitalController();
  private Hospital hospital;
  private Employee user;

  private Stage primaryStage;
  private Rectangle2D primaryScreenBounds; // screen width & height
  private Stack<String> sceneStack =
      new Stack<>(); // stack of the scenes for when we are going back
  private int databaseFlag = 1;

  private final String apiKey = "AIzaSyApJE1UT-glYyCycAcz2W7W0W4nRf6ZKTg";
  private GeoApiContext googleMaps = new GeoApiContext.Builder().apiKey(apiKey).build();

  private String algoType = "AStar";
  private Theme theme = Theme.Light;
  public String styleSheetType = "light-mode";

  private Timer fxTimer = new Timer();
  int timeout = 60000;

  private static class UIControllerHelper {
    private static final UIController uController = new UIController();
  }

  private String homeSearchQuery = "";

  public static UIController getUIController() {
    return UIControllerHelper.uController;
  }

  public void init(Stage primaryStage) throws IOException {
    primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    try {
      hospital = hController.getHospital("Faulkner");
    } catch (NullHospitalException e) {
      e.printStackTrace();
    }
    this.primaryStage = primaryStage;
    primaryStage.setWidth(primaryScreenBounds.getWidth());
    primaryStage.setHeight(primaryScreenBounds.getHeight());
    primaryStage.setMaximized(true);
    setScene("VisitorWelcome.fxml");
  }

  public void setScene(String filename) throws IOException {
    resetTimer();
    sceneStack.push(filename);
    Parent root = FXMLLoader.load(UIController.class.getResource(filename));
    Scene scene = new Scene(root, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
    scene = setSceneColorBlind(scene);
    scene.setOnMouseMoved(e -> resetTimer());
    scene.setOnKeyTyped(e -> resetTimer());
    primaryStage.setTitle("Team P Project");
    primaryStage.setScene(scene);
    if (filename.equals("AdministratorTools.fxml")) {
      bindMapControls(scene);
      bindMapEditingControls(scene);
    } else if (filename.equals("StartNavigation.fxml")) bindMapControls(scene);
    primaryStage.setMaximized(true);
    primaryStage.show();
  }

  public void back() throws IOException {
    sceneStack.pop();
    if (!sceneStack.isEmpty()) setScene(sceneStack.peek());
    else home();
  }

  public void setHomeSearchQuery(String text) {
    homeSearchQuery = text;
  }

  public String getHomeSearchQuery() {
    return homeSearchQuery;
  }

  public void home() throws IOException {
    sceneStack.clear();
    if (user == null) {
      setScene("VisitorWelcome.fxml");
    } else if (user.isAdmin()) {
      setScene("AdminWelcome.fxml");
    } else {
      setScene("EmployeeWelcome.fxml");
    }
  }

  public void generateMaps(boolean canEdit) {
    RouteController otherController = getOtherHospital().getRouteController();
    if (getRouteController().getHospitalName().equals("Faulkner")) {
      getRouteController().generateMap(1, canEdit);
      getRouteController().generateMap(2, canEdit);
      getRouteController().generateMap(3, canEdit);
      getRouteController().generateMap(4, canEdit);
      getRouteController().generateMap(5, canEdit);
      otherController.generateMap(-2, canEdit);
      otherController.generateMap(-1, canEdit);
      otherController.generateMap(0, canEdit);
      otherController.generateMap(1, canEdit);
      otherController.generateMap(2, canEdit);
      otherController.generateMap(3, canEdit);
    } else {
      getRouteController().generateMap(-2, canEdit);
      getRouteController().generateMap(-1, canEdit);
      getRouteController().generateMap(0, canEdit);
      getRouteController().generateMap(1, canEdit);
      getRouteController().generateMap(2, canEdit);
      getRouteController().generateMap(3, canEdit);
      otherController.generateMap(1, canEdit);
      otherController.generateMap(2, canEdit);
      otherController.generateMap(3, canEdit);
      otherController.generateMap(4, canEdit);
      otherController.generateMap(5, canEdit);
    }
  }

  public void generateMaps(boolean canEdit, NavigationController navigationController) {
    getOtherHospital().getRouteController().setNavigationController(navigationController);
    getRouteController().setNavigationController(navigationController);
    generateMaps(canEdit);
  }

  public void addMap(Pane map, int floor, String hospital) {
    if (getRouteController().getHospitalName().equals(hospital)) {
      if (map.getChildren().size() == 0) map.getChildren().add(getRouteController().getMap(floor));
      else map.getChildren().set(0, getRouteController().getMap(floor));
    } else {
      RouteController otherController = getOtherHospital().getRouteController();
      if (map.getChildren().size() == 0) map.getChildren().add(otherController.getMap(floor));
      else map.getChildren().set(0, otherController.getMap(floor));
    }
  }

  public void moveMapCenter(int floor, double x, double y) {
    getRouteController().getGraph(floor).moveCenter(x, y);
    //    getOtherHospital().getRouteController().getGraph(floor).moveCenter(x, y);
  }

  public void moveMapCenter(double x, double y) {
    if (hospital.getName().equals("Faulkner")) {
      for (int i = 1; i <= 5; i++) {
        getRouteController().getGraph(i).moveCenter(x, y);
      }
      for (int i = -2; i <= 3; i++) {
        getOtherHospital().getRouteController().getGraph(i).moveCenter(x, y);
      }
    } else {
      for (int i = -2; i <= 3; i++) {
        getRouteController().getGraph(i).moveCenter(x, y);
      }
      for (int i = 1; i <= 5; i++) {
        getOtherHospital().getRouteController().getGraph(i).moveCenter(x, y);
      }
    }
  }

  public void moveMapCenterToPoint(int floor, double x, double y, double pointX, double pointY) {
    getRouteController().getGraph(floor).moveCenterToPoint(x, y, pointX, pointY);
  }

  public void logout() throws IOException {
    user = null;
    home();
  }

  public void setBackground(ImageView image) {
    image.viewOrderProperty().set(100);
    image.setFitHeight(primaryScreenBounds.getHeight());
    image.setFitWidth(primaryScreenBounds.getWidth());
  }

  private void bindMapControls(Scene scene) {
    scene.addEventFilter(
        KeyEvent.KEY_PRESSED,
        event -> {
          switch (event.getCode()) {
            case SHIFT:
              getRouteController().shift(true);
              getOtherHospital().getRouteController().shift(true);
              break;
          }
        });
    scene.addEventFilter(
        KeyEvent.KEY_RELEASED,
        event -> {
          switch (event.getCode()) {
            case SHIFT:
              getRouteController().shift(false);
              getOtherHospital().getRouteController().shift(false);
              break;
          }
        });
  }

  private void bindMapEditingControls(Scene scene) {
    scene.addEventFilter(
        KeyEvent.KEY_PRESSED,
        event -> {
          switch (event.getCode()) {
            case ESCAPE:
              getRouteController().escape();
              break;
            case DELETE:
              getRouteController().delete();
              break;
          }
        });
  }

  public void setDatabaseFlag(int databaseFlag) {
    if (this.databaseFlag != databaseFlag) {
      this.databaseFlag = databaseFlag;
      hospital.clear();
      getOtherHospital().clear();
      if (databaseFlag == 1) {
        hospital.loadLocalDatabase();
        getOtherHospital().loadLocalDatabase();
      } else if (databaseFlag == 2) {
        hospital.loadMongoDatabase();
        getOtherHospital().loadMongoDatabase();
      }
    }
  }

  public Hospital getOtherHospital() {
    return hController.getOtherHospital(hospital);
  }

  public int getDatabaseFlag() {
    return databaseFlag;
  }

  public Employee getUser() {
    return user;
  }

  public void setUser(Employee employee) {
    user = employee;
  }

  public Hospital getHospital() {
    return hospital;
  }

  public void setHospital(Hospital hospital) {
    this.hospital = hospital;
  }

  public RouteController getRouteController() {
    return hospital.getRouteController();
  }

  public ServiceController getServiceController() {
    return hospital.getServiceController();
  }

  public EmployeeController getEmployeeController() {
    return hospital.getEmployeeController();
  }

  public Rectangle2D getPrimaryScreenBounds() {
    return primaryScreenBounds;
  }

  public String getApiKey() {
    return apiKey;
  }

  public GeoApiContext getGoogleMaps() {
    return googleMaps;
  }

  public String getAlgoType() {
    return algoType;
  }

  public void setAlgoType(String type) {
    algoType = type;
    getRouteController().setAlgorithm(type);
  }

  public Theme getTheme() {
    return theme;
  }

  public void setTheme(Theme theme) {
    this.theme = theme;
  }

  public enum Theme {
    Light,
    Dark,
    Timed;
  }

  public void setColorBlindType(String selectedType) {
    // System.out.println(selectedType);
    switch (selectedType) {
      case ("a"):
        styleSheetType = "aNopia";
        break;
      case ("d"):
        styleSheetType = "dNopia";
        break;
      case ("p"):
        styleSheetType = "pNopia";
        break;
      case ("t"):
        styleSheetType = "tNopia";
        break;
      case ("n"):
        styleSheetType = "light-mode";
        break;
      case ("dm"):
        styleSheetType = "darkmode";
        break;
    }
  }

  public String getColorBlindType() {
    return styleSheetType;
  }

  public Scene setSceneColorBlind(Scene scene) {
    if (!styleSheetType.equals("darkmode")) {
      scene.getStylesheets().clear();
      scene
          .getStylesheets()
          .add("/edu/wpi/teamp/" + styleSheetType + "/" + styleSheetType + ".css");
      scene.getStylesheets().add("/edu/wpi/teamp/light-mode/basicStructure.css");
      scene.getStylesheets().add("/edu/wpi/teamp/" + styleSheetType + "/service-request.css");
      scene.getStylesheets().add("/edu/wpi/teamp/" + styleSheetType + "/tableView.css");
      scene.getStylesheets().add("/edu/wpi/teamp/" + styleSheetType + "/tabPane.css");

      return scene;
    } else if (styleSheetType.equals("darkmode")) {
      // System.out.println("I am dark mode");
      scene.getStylesheets().clear();
      scene.getStylesheets().add("/edu/wpi/teamp/dark-mode/dark-mode.css");
      scene.getStylesheets().add("/edu/wpi/teamp/dark-mode/basicStructure.css");
      scene.getStylesheets().add("/edu/wpi/teamp/dark-mode/service-request.css");
      scene.getStylesheets().add("/edu/wpi/teamp/dark-mode/tableView.css");
      scene.getStylesheets().add("/edu/wpi/teamp/dark-mode/tabPane.css");
      return scene;
    } else {
      return scene;
    }
  }

  public void resetTimer() {
    fxTimer.cancel();
    fxTimer.purge();
    fxTimer = new Timer();
    fxTimer.schedule(
        new TimerTask() {
          @Override
          public void run() {

            Platform.runLater(
                () -> {
                  try {
                    setScene("VisitorWelcome.fxml");
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                });
          }
        },
        timeout);
  }

  public void cancelTimer() {
    fxTimer.cancel();
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }
}
