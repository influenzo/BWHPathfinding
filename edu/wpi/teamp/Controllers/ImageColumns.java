package edu.wpi.teamp.Controllers;

import hospital.route.AbstractNode;
import hospital.route.NodeType;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.ImageView;

public class ImageColumns {

  private SimpleStringProperty direction;
  private SimpleObjectProperty<ImageView> image;
  private SimpleObjectProperty<Integer> floor;
  private SimpleStringProperty hospital;
  private AbstractNode node;

  public ImageColumns() {
    this.image = new SimpleObjectProperty<>(null);
    this.direction = new SimpleStringProperty(null);
  }

  public ImageView getImage() {
    return image.get();
  }

  public void setImage(ImageView image) {
    this.image.set(image);
  }

  public void setDirection(String directions) {
    this.direction.set(directions);
  }

  public String getDirection() {
    return direction.get();
  }

  public Integer getFloor() {
    if (node == null) {
      return Integer.MIN_VALUE;
    } else {
      return node.getFloor();
    }
  }

  public String getHospital() {
    if (node == null) {
      return "";
    } else {
      return node.getHospitalName();
    }
  }

  public void setNode(AbstractNode node) {
    this.node = node;
  }

  public AbstractNode getNode() {
    return node;
  }

  public NodeType getNodeType() {
    try {
      return node.getNodeType();
    } catch (Exception e) {
      return NodeType.HALL;
    }
  }
}
