package edu.wpi.teamp.Controllers;

import edu.wpi.teamp.UIController;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class BottomBarController {

  UIController uController = UIController.getUIController();

  @FXML private Label timeLabel;
  @FXML private Label dateLabel;
  @FXML private ImageView nLogo;
  @FXML private ImageView aLogo;
  @FXML private ImageView dLogo;
  @FXML private ImageView pLogo;
  @FXML private ImageView tLogo;

  private final Date date = new Date();
  private final DateFormat timeFormat = new SimpleDateFormat("h:mm aa");
  private final DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
  private final int interval = 1000;

  @FXML
  public void initialize() {
    nLogo.setVisible(false);
    aLogo.setVisible(false);
    dLogo.setVisible(false);
    pLogo.setVisible(false);
    tLogo.setVisible(false);
    if (uController.getColorBlindType() == "aNopia") {
      aLogo.setVisible(true);
    } else if (uController.getColorBlindType() == "dNopia") {
      dLogo.setVisible(true);
    } else if (uController.getColorBlindType() == "pNopia") {
      pLogo.setVisible(true);
    } else if (uController.getColorBlindType() == "tNopia") {
      tLogo.setVisible(true);
    } else {
      nLogo.setVisible(true);
    }
    updateTime();
    startTimer();
  }

  public void startTimer() {
    Timer timer = new Timer();
    timer.scheduleAtFixedRate(
        new TimerTask() {
          @Override
          public void run() {
            javafx.application.Platform.runLater(() -> updateTime());
          }
        },
        0,
        interval);
  }

  public void updateTime() {
    date.setTime(date.getTime() + interval);
    timeLabel.setText(timeFormat.format(date));
    dateLabel.setText(dateFormat.format(date));
  }
}
