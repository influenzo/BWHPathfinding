package edu.wpi.teamp.Controllers;

import edu.wpi.teamp.UIController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

public class TestGoogleMapController {

  UIController uController = UIController.getUIController();

  private final String faulknerAddress = "1153+Centre+Street+Boston+MA";
  private final String mainAddress = "75+Francis+Street+Boston+MA";

  @FXML WebView webView;

  @FXML
  public void initialize() {

    webView
        .sceneProperty()
        .addListener(
            new ChangeListener<Scene>() {

              @Override
              public void changed(
                  ObservableValue<? extends Scene> observable, Scene oldValue, Scene scene) {
                if (scene != null) {
                  webView.setMaxSize(webView.getScene().getWidth(), webView.getScene().getHeight());
                  webView.maxWidthProperty().bind(webView.getScene().widthProperty());
                  webView.maxHeightProperty().bind(webView.getScene().heightProperty());
                } else {
                  webView.maxWidthProperty().unbind();
                  webView.maxHeightProperty().unbind();
                }
              }
            });

    String dest = "";

    switch (dest) {
      case "Faulkner":
        webView
            .getEngine()
            .load(
                this.getClass()
                    .getResource("/edu/wpi/teamp/googlemaps/ToFaulkner.html")
                    .toString());
        break;
      case "Main":
        webView
            .getEngine()
            .load(
                this.getClass()
                    .getResource("/edu/wpi/teamp/googlemaps/ToMainHospital.html")
                    .toString());
        break;
      default:
        webView
            .getEngine()
            .load(
                this.getClass()
                    .getResource("/edu/wpi/teamp/googlemaps/GoogleMaps.html")
                    .toString());
    }
  }
}
