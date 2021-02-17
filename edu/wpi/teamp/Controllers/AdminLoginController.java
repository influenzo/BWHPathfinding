package edu.wpi.teamp.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.teamp.UIController;
import hospital.employee.Employee;
import hospital.employee.EmployeeController;
import hospital.exceptions.IncorrectPasswordException;
import hospital.exceptions.NullUsernameException;
import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class AdminLoginController {

  private UIController uController = UIController.getUIController();
  private EmployeeController eController = uController.getEmployeeController();

  @FXML JFXTextField userField;
  @FXML JFXPasswordField passField;
  @FXML JFXButton loginBtn;
  @FXML JFXButton confirmBtn;
  @FXML JFXButton cancelBtn;
  @FXML Label forgotBtn;
  @FXML StackPane stackPaneBox;
  @FXML HBox logInBox;
  @FXML HBox emailBox;
  @FXML HBox confirmBox;
  @FXML JFXButton backBtn;
  @FXML ImageView backImg;
  @FXML ImageView dmbackImg;
  @FXML TextField emailInput;
  @FXML Label invalidInput;
  @FXML Rectangle lightRect;
  @FXML Rectangle darkRect;
  @FXML Rectangle lightForgotRect;
  @FXML Rectangle darkForgotRect;
  @FXML Rectangle lightConfirm;
  @FXML Rectangle darkConfirm;
  @FXML private JFXButton emergency;

  @FXML
  public void initialize() {

    lightForgotRect.setVisible(false);
    darkForgotRect.setVisible(false);

    if (uController.getTheme() == UIController.Theme.Dark) {
      backImg.setVisible(false);
      dmbackImg.setVisible(true);
      lightRect.setVisible(false);
      darkRect.setVisible(true);
      dmbackImg.viewOrderProperty().set(100);
      dmbackImg.setFitHeight(1080);
      dmbackImg.setFitWidth(1920);
    } else if (uController.getTheme() == UIController.Theme.Light) {
      dmbackImg.setVisible(false);
      backImg.setVisible(true);
      lightRect.setVisible(true);
      darkRect.setVisible(false);
      backImg.viewOrderProperty().set(100);
      backImg.setFitHeight(1080);
      backImg.setFitWidth(1920);
    }
    logInBox.setVisible(true);
    stackPaneBox.setVisible(false);
  }

  @FXML
  public void btnClicked(ActionEvent e) throws IOException {
    if (e.getSource() == backBtn) {
      uController.back();
    } else if (e.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");
    } else if (e.getSource() == loginBtn) {
      try {
        Employee user = eController.getEmployee(userField.getText(), passField.getText());
        uController.setUser(user);
        uController.home();
      } catch (IncorrectPasswordException ex) {
        invalidInput.setText("Incorrect Password");
        invalidInput.setVisible(true);
      } catch (NullUsernameException ex) {
        invalidInput.setText("Username does not exist");
        invalidInput.setVisible(true);
      }
    } else if (e.getSource() == cancelBtn) {
      stackPaneBox.setVisible(false);
    } else if (e.getSource() == confirmBtn) {
      // set start visibility
      emailBox.setVisible(false);

      transition(confirmBox, true);

      if (uController.getTheme() == UIController.Theme.Dark) {
        darkConfirm.setVisible(true);
        lightConfirm.setVisible(false);
      } else if (uController.getTheme() == UIController.Theme.Light) {
        lightConfirm.setVisible(true);
        darkConfirm.setVisible(false);
      }

      // create delays and final look
      PauseTransition delayConfirm = new PauseTransition(Duration.seconds(2));
      delayConfirm.setOnFinished(
          event -> {
            transition(confirmBox, false);
            transition(stackPaneBox, false);
            lightConfirm.setVisible(false);
            darkConfirm.setVisible(false);
          });

      // start delays
      delayConfirm.play();
    }
  }

  @FXML
  void labelClicked(MouseEvent e) throws IOException {
    if (e.getSource() == forgotBtn) {
      stackPaneBox.setVisible(true);
      emailBox.setVisible(true);
      confirmBox.setVisible(false);
      if (uController.getTheme() == UIController.Theme.Dark) {
        darkForgotRect.setVisible(true);
      } else if (uController.getTheme() == UIController.Theme.Light) {
        lightForgotRect.setVisible(true);
      }
    }
  }

  private void transition(Node object, boolean show) {
    FadeTransition transition = new FadeTransition(Duration.millis(225), object);
    transition.setFromValue(show ? 0 : 1);
    transition.setToValue(show ? 1 : 0);
    transition.playFromStart();
    if (!show) {
      transition.onFinishedProperty().set((event -> object.setVisible(false)));
    } else {
      object.setVisible(true);
    }
  }
}
