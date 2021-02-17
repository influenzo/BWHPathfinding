package edu.wpi.teamp;

import java.io.IOException;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PApp extends Application {

  @Override
  public void init() {
    log.info("Starting Up");
  }

  // @Override
  // public void start(Stage primaryStage) {}
  @Override
  public void start(Stage primaryStage) throws IOException {
    primaryStage
        .getIcons()
        .add(new Image(Main.class.getResourceAsStream("images/static/hospitalLogo_blue.png")));

    UIController uController = UIController.getUIController();
    uController.init(primaryStage);
    // uController.setScene("Analytics");
  }

  @Override
  public void stop() {
    log.info("Shutting Down");
    System.exit(0); // to kill threads
  }
}
