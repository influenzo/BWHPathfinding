package edu.wpi.teamp.Controllers.ServiceRequest;

import com.jfoenix.controls.JFXButton;
import edu.wpi.teamp.UIController;
import hospital.service.FlowerType;
import java.io.IOException;
import java.util.HashMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javax.swing.*;

public class FloristController {

  UIController uController = UIController.getUIController();
  oServiceRequestController oController = oServiceRequestController.getServiceRequestController();
  BasicInformationController bController;
  RequestReceivedPopupController pController;

  @FXML private JFXButton btnRoses;
  @FXML private JFXButton btnOrchids;
  @FXML private JFXButton btnBluebells;
  @FXML private JFXButton btnTulips;
  @FXML private JFXButton btnLilies;
  @FXML private JFXButton btnDaisies;
  @FXML private JFXButton emergency;

  private int numRoses = 0;
  private int numOrchids = 0;
  private int numBluebells = 0;
  private int numTulips = 0;
  private int numLilies = 0;
  private int numDaisies = 0;

  @FXML private Text totalCost;
  @FXML private Label errorCode;
  @FXML private ListView<String> cartList;

  @FXML BasicInformationController basicInformationController;
  @FXML RequestReceivedPopupController requestReceivedPopupController;
  @FXML private JFXButton btnCancel;
  @FXML private JFXButton addBtn;
  @FXML private JFXButton btnSubmit;
  @FXML private JFXButton clearBtn;
  @FXML private Label errorMessage;

  private HashMap<String, Double> flowerPrices =
      new HashMap<>() {
        {
          put("numRoses", FlowerType.Rose.price());
          put("numOrchids", FlowerType.Orchid.price());
          put("numBluebells", FlowerType.Bluebell.price());
          put("numTulips", FlowerType.Tulip.price());
          put("numLilies", FlowerType.Lily.price());
          put("numDaisies", FlowerType.Daisy.price());
        }
      };

  private HashMap<String, Integer> flowerCounts =
      new HashMap<>() {
        {
          put("numRoses", 0);
          put("numOrchids", 0);
          put("numBluebells", 0);
          put("numTulips", 0);
          put("numLilies", 0);
          put("numDaisies", 0);
        }
      };

  private double total = 0.0;

  @FXML
  public void initialize() {
    bController = basicInformationController;
    pController = requestReceivedPopupController;
  }

  /*
  @FXML
  public void handle(KeyEvent e) {
    JFXTextField source = ((JFXTextField) e.getSource());
    try {
      int flowerCount = Integer.parseInt(source.getText());
      errorCode.setVisible(false);
      flowerCounts.put(source.getId(), flowerCount);
    } catch (NumberFormatException e1) {
      if (!source.getText().equals("")) errorCode.setVisible(true);
      flowerCounts.put(source.getId(), 0);
    }
    setCost();
    updateCart();
  }
  */
  public void btnClicked(ActionEvent e) throws IOException {
    if (e.getSource() == btnCancel) {
      oController.clearAdditional();
      uController.back();

    } else if (e.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");
    } else if (e.getSource() == btnRoses) {
      numRoses++;
      flowerCounts.put("numRoses", numRoses);
      setCost();
      updateCart();

    } else if (e.getSource() == btnOrchids) {
      numOrchids++;
      flowerCounts.put("numOrchids", numOrchids);
      setCost();
      updateCart();

    } else if (e.getSource() == btnBluebells) {
      numBluebells++;
      flowerCounts.put("numBluebells", numBluebells);
      setCost();
      updateCart();

    } else if (e.getSource() == btnTulips) {
      numTulips++;
      flowerCounts.put("numTulips", numTulips);
      setCost();
      updateCart();

    } else if (e.getSource() == btnLilies) {
      numLilies++;
      flowerCounts.put("numLilies", numLilies);
      setCost();
      updateCart();

    } else if (e.getSource() == btnDaisies) {
      numDaisies++;
      flowerCounts.put("numDaisies", numDaisies);
      setCost();
      updateCart();

    } else if (e.getSource() == btnSubmit) {
      if (bController.getLocation() == null
          || bController.getDescription().isEmpty()
          || bController.getRequester().isEmpty()) {
        errorMessage.setVisible(true);
      } else {
        // Order of additional info number of flowers ordered in alphabetical order with total cost
        // last
        oController.addAdditionalField("" + flowerCounts.get("numBluebells"));
        oController.addAdditionalField("" + flowerCounts.get("numDaisies"));
        oController.addAdditionalField("" + flowerCounts.get("numLilies"));
        oController.addAdditionalField("" + flowerCounts.get("numOrchids"));
        oController.addAdditionalField("" + flowerCounts.get("numRoses"));
        oController.addAdditionalField("" + flowerCounts.get("numTulips"));
        oController.createServiceRequest(
            bController.getLocation(), bController.getDescription(), bController.getRequester());
        oController.clearAdditional();
        pController.showPopup();
      }
    } else if (e.getSource() == clearBtn) {
      // update counts
      numRoses = 0;
      numOrchids = 0;
      numBluebells = 0;
      numTulips = 0;
      numDaisies = 0;
      numLilies = 0;

      // update num of flowers
      for (String flower : flowerCounts.keySet()) {
        flowerCounts.put(flower, 0);
      }

      // update cart
      setCost();
      updateCart();
    }
  }

  public void removeItem(javafx.scene.input.MouseEvent mouseEvent) {
    if (mouseEvent.getSource().equals(cartList)) {
      String item = cartList.selectionModelProperty().getValue().getSelectedItem();

      for (String selected : cartList.getItems()) {
        System.out.println("Selected: " + selected);
        System.out.println("Item: " + item);

        if (selected.equals(item)) {
          String num = "";
          String flower = "";
          String current = "";
          int index = 0;

          // iterate through string to find number
          for (int i = 0; i < selected.length() - 1; i++) {
            current = selected.substring(i, i + 1);
            index = i;

            if (current.equals("x")) {
              flower = selected.substring(0, index - 1);
              while (!current.equals(" ")) {
                index++;
                current = selected.substring(index, index + 1);
                if (!current.equals(" ")) {
                  num += current;
                }
                break;
              }
              int numInt = Integer.parseInt(num);
            }
          }
          String numFlower = "num" + flower;
          // remove count
          if (flowerCounts.get(numFlower) != 0) {
            flowerCounts.put(numFlower, flowerCounts.get(numFlower) - 1);
          }
          // update num counts
          for (String name : flowerCounts.keySet()) {
            if (name.equals("numRoses")) {
              numRoses = flowerCounts.get(name);
            } else if (name.equals("numOrchids")) {
              numOrchids = flowerCounts.get(name);
            } else if (name.equals("numBluebells")) {
              numBluebells = flowerCounts.get(name);
            } else if (name.equals("numTulips")) {
              numTulips = flowerCounts.get(name);
            } else if (name.equals("numLilies")) {
              numLilies = flowerCounts.get(name);
            } else if (name.equals("numDaisies")) {
              numDaisies = flowerCounts.get(name);
            }
          }
          setCost();
          updateCart();
        }
      }
    }
  }

  private void updateCart() {
    cartList.getItems().clear();
    for (String flower : flowerCounts.keySet()) {
      if (flowerCounts.get(flower) != 0) {
        String total = String.format("%.2f", flowerCounts.get(flower) * flowerPrices.get(flower));
        cartList
            .getItems()
            .add(flower.substring(3) + " x" + flowerCounts.get(flower) + " = $" + total);
      }
    }
  }

  private void setCost() {
    double total = 0;
    for (String flower : flowerCounts.keySet()) {
      total += flowerCounts.get(flower) * flowerPrices.get(flower);
    }
    totalCost.setText(String.format("%.2f", total));
  }
}
