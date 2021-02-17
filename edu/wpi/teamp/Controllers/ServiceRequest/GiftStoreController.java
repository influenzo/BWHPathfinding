package edu.wpi.teamp.Controllers.ServiceRequest;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import edu.wpi.teamp.UIController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class GiftStoreController {

  BasicInformationController bController;
  RequestReceivedPopupController pController;
  oServiceRequestController oController = oServiceRequestController.getServiceRequestController();

  UIController uController = UIController.getUIController();
  @FXML JFXListView itemsList;
  List<StoreItem> storeItems;
  List<StoreItem> checkoutItems = new LinkedList<>();
  double subtotal;
  @FXML Label subTotal;
  @FXML JFXButton clearBtn;
  @FXML JFXListView checkoutList;
  @FXML JFXButton btnCancel;
  @FXML private JFXButton emergency;

  @FXML BasicInformationController basicInformationController;
  @FXML RequestReceivedPopupController requestReceivedPopupController;
  @FXML private JFXButton btnSubmit;
  @FXML private Label errorMessage;

  @FXML
  public void initialize() {
    bController = basicInformationController;
    pController = requestReceivedPopupController;
    storeItems = makeStore();
    for (StoreItem i : storeItems) {
      itemsList.getItems().add(i.name);
    }
  }

  private List<StoreItem> makeStore() {
    List<StoreItem> list = new ArrayList<>();
    list.add(new StoreItem("Skittles", 1.20));
    list.add(new StoreItem("Jolly Ranchers", 1.00));
    list.add(new StoreItem("Milk Duds", 1.50));
    list.add(new StoreItem("Baby Ruth", 1.15));
    list.add(new StoreItem("Starburst", 1.20));
    list.add(new StoreItem("Crunch Bar", 1.15));
    list.add(new StoreItem("Twizzlers", 1.20));
    list.add(new StoreItem("Toblerone", 2.05));
    list.add(new StoreItem("Mr.Goodbar", 1.20));
    list.add(new StoreItem("Almond Joy", 1.20));
    list.add(new StoreItem("Sour Patch Kids", 1.20));
    list.add(new StoreItem("PayDay", 1.20));
    list.add(new StoreItem("Reese's Pieces", 1.20));
    list.add(new StoreItem("Cadbury Egg", 1.20));
    list.add(new StoreItem("Kit Kat", 1.20));
    list.add(new StoreItem("M&M", 1.20));
    list.add(new StoreItem("Snickers", 1.20));
    list.add(new StoreItem("Don't Press This Item", 1.20));

    return list;
  }

  public void listClicked(MouseEvent mouseEvent) {
    if (mouseEvent.getSource().equals(itemsList)) {
      String item = (String) itemsList.getSelectionModel().getSelectedItem();
      for (StoreItem s : storeItems) {
        if (s.name.equals(item)) {
          subtotal += s.price;
          subTotal.setText("Total: $" + String.format("%.2f", subtotal));
          addToCheckout(s);
        }
      }
    } else if (mouseEvent.getSource().equals(checkoutList)) {
      String item = (String) checkoutList.getSelectionModel().getSelectedItem();
      for (StoreItem s : checkoutItems) {
        String namecomp = s.name + " x" + s.count;
        if (namecomp.equals(item)) {
          removeFromCheckout(s);
        }
      }
    }
  }

  private void removeFromCheckout(StoreItem s) {
    for (int i = 0; i < checkoutItems.size(); i++) {
      StoreItem item = checkoutItems.get(i);
      if (item.name.equals(s.name)) {
        if (item.count >= 0) {
          if (item.count == 1) {
            subtotal -= item.price;
            if (subtotal < 0) {
              subtotal = 0;
            }
            subTotal.setText("Total: $" + String.format("%.2f", subtotal));

            checkoutItems.remove(i);
          } else {
            item.count = item.count - 1;
            checkoutItems.set(i, item);
            subtotal -= item.price;
            subTotal.setText("Total: $" + String.format("%.2f", subtotal));
          }
        }
      }
    }
    checkoutList.getItems().clear();
    for (StoreItem x : checkoutItems) {
      checkoutList.getItems().add(String.format(x.name + " x" + x.count));
    }
  }

  private void addToCheckout(StoreItem s) {
    boolean inThere = false;
    for (StoreItem x : checkoutItems) {
      if (x.name.equals(s.name)) {
        x.count++;
        inThere = true;
      }
    }

    if (!inThere) {
      checkoutItems.add(s);
      inThere = false;
    }

    checkoutList.getItems().clear();
    for (StoreItem x : checkoutItems) {
      checkoutList.getItems().add(String.format(x.name + " x" + x.count));
    }
  }

  public void btnClicked(ActionEvent actionEvent) throws IOException {
    if (actionEvent.getSource().equals(clearBtn)) {
      subtotal = 0;
      subTotal.setText("Total: $" + String.format("%.2f", subtotal));
      checkoutList.getItems().clear();
      for (StoreItem c : checkoutItems) {
        c.count = 1;
      }
      checkoutItems.clear();
    } else if (actionEvent.getSource() == emergency) {
      uController.setScene("EmergencyScreen.fxml");
    } else if (actionEvent.getSource().equals(btnCancel)) {
      uController.back();
    } else if (actionEvent.getSource().equals(btnSubmit)) {
      if (bController.getLocation() == null
          || bController.getDescription().isEmpty()
          || bController.getRequester().isEmpty()) {
        errorMessage.setVisible(true);
      } else {
        for (StoreItem s : checkoutItems) {
          oController.addAdditionalField(s.name.substring(0, 2) + " " + s.count);
        }
        errorMessage.setVisible(false);
        oController.createServiceRequest(
            bController.getLocation(), bController.getDescription(), bController.getRequester());
        oController.clearAdditional();
        pController.showPopup();
      }
    }
  }
}

class StoreItem {
  public String name;
  public double price;
  public int count = 1;

  public StoreItem(String name, double price) {
    this.name = name;
    this.price = price;
  }
}
