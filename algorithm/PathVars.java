package algorithm;

import hospital.route.AbstractNode;

public class PathVars {
  private AbstractNode parent;
  private double g;
  private double h;
  private double f;
  private boolean usable;
  private boolean visited;

  public PathVars() {
    this.g = 0;
    this.h = 0;
    this.f = 0;
    this.usable = true;
    this.visited = false;
    this.parent = null;
  }

  void setG(double g) {
    this.g = g;
  }

  void setH(double h) {
    this.h = h;
  }

  void setF(double f) {
    this.f = f;
  }

  void setUsable(boolean use) {
    this.usable = use;
  }

  double getG() {
    return g;
  }

  double getH() {
    return h;
  }

  double getF() {
    return f;
  }

  boolean getUsuable() {
    return usable;
  }

  boolean getVisited() {
    return this.visited;
  }

  void setVisited(boolean v) {
    this.visited = v;
  }

  void setParent(AbstractNode rent) {
    // System.out.println(rent.getLongName() + "->");
    this.parent = rent;
  }

  void clear() {
    this.g = 0;
    this.h = 0;
    this.f = 0;
    this.usable = true;
    this.visited = false;
    this.parent = null;
  }

  AbstractNode getParent() {
    return parent;
  }

  boolean hasParent() {
    if (parent != null) {
      return true;
    } else {
      return false;
    }
  }
}
