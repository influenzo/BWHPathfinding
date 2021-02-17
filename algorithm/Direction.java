package algorithm;

import hospital.route.AbstractNode;

public class Direction {
  String direction;
  AbstractNode node;

  public Direction(String direction, AbstractNode node) {
    this.direction = direction;
    this.node = node;
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public AbstractNode getNode() {
    return node;
  }

  public String toString() {
    return "Node: " + node.getLongName() + " Direction: " + direction;
  }
}
