package algorithm;

import hospital.route.AbstractNode;
import hospital.route.NodeType;
import java.util.ArrayList;
import java.util.List;

public class Path {

  private double cost = 0;
  private List<AbstractNode> path;

  public Path(List<AbstractNode> path) {
    this.path = path;
    if (path.size() > 0) {
      path.remove(0);
      //      path.add(0, path.get(0));
      //      path.add(path.get(path.size() - 1));
    }

    cost = path.get(path.size() - 1).getAlgoVars().getG();
    //    System.out.println(cost);
    //    System.out.println(getDirections());
    // System.out.println("Path size: " + path.size());
    // System.out.println("Direction size: " + directions.size());
  }

  public List<AbstractNode> getNodes() {
    return path;
  }

  public double getCost() {
    return cost;
  }

  public List<Direction> getDirections() {
    if (path.size() <= 1) {
      return null;
    }
    int hallWidth = 10;
    List<Direction> directions = new ArrayList<Direction>();
    List<Double> distances = getDistances();
    int dist = 0;
    directions.add(
        new Direction("Starting route to " + path.get(path.size() - 1).getLongName(), path.get(0)));
    int lastNode = 1;
    for (int i = 1; i < path.size() - 2; i++) {
      AbstractNode last = path.get(i - 1);
      AbstractNode current = path.get(i);
      AbstractNode next = path.get(i + 1);
      dist += distances.get(i - 1);
      String dir = getTurn(last, current, next);
      System.out.println(lastNode);
      if (dir.contains("elevator")) {
        directions.add(new Direction(dir, path.get(lastNode)));
        lastNode = 1;
      } else if (dir.contains("stair")) {
        directions.add(new Direction(dir, path.get(lastNode)));
        lastNode = 1;
      } else if (dir.equals("First Landing") && lastNode == 1) {
        lastNode = i;
      } else if (!dir.equals("straight ahead") && !dir.equals("First Landing")) {
        directions.add(new Direction("Turn " + dir + " in " + dist + " feet", path.get(i)));
        dist = 0;
      }
    }
    AbstractNode last = path.get(path.size() - 3);
    AbstractNode current = path.get(path.size() - 2);
    AbstractNode next = path.get(path.size() - 1);
    dist += distances.get(distances.size() - 2);
    String dir = getTurn(last, current, next);

    if (dir.contains("elevator")) {
      directions.add(
          new Direction(
              "Take the elevator to floor "
                  + path.get(path.size() - 1).getFloor()
                  + ". "
                  + path.get(path.size() - 1).getLongName()
                  + " will be "
                  + dist
                  + " feet straight ahead",
              path.get(path.size() - 2)));
    } else if (dir.contains("stairs")) {
      directions.add(
          new Direction(
              "Take the stairs to floor "
                  + path.get(path.size() - 1).getFloor()
                  + ". "
                  + path.get(path.size() - 1).getLongName()
                  + " will be "
                  + dist
                  + " feet straight ahead",
              path.get(path.size() - 2)));
    } else if (!dir.equals("straight ahead")) {
      directions.add(
          new Direction(
              "In " + dist + " feet turn " + dir + " to " + path.get(path.size() - 1).getLongName(),
              path.get(path.size() - 2)));
    } else {
      directions.add(
          new Direction(
              path.get(path.size() - 1).getLongName() + " is " + dist + " straight ahead",
              path.get(path.size() - 2)));
    }

    return directions;
  }

  private ArrayList<Double> getDistances() {
    ArrayList<Double> distances = new ArrayList<Double>();
    for (int i = 0; i < path.size() - 1; i++) {
      AbstractNode cur = path.get(i);
      AbstractNode next = path.get(i + 1);
      double dist =
          Math.sqrt(
              (Math.pow(cur.getX() - next.getX(), 2)) + (Math.pow(cur.getY() - next.getY(), 2)));
      if (path.get(i).getHospitalName().equals("Faulkner")) {
        distances.add(dist * 480 / 2188);
      } else if (path.get(i).getHospitalName().equals("Main")) {
        distances.add(dist * 320 / 955);
      }
    }
    return distances;
  }

  private String getTurn(AbstractNode last, AbstractNode current, AbstractNode next) {
    int hallWidth = 10;
    double turnAngle =
        360
            * Math.atan(
                Math.abs(current.getY() - next.getY()) / Math.abs(current.getX() - next.getX()))
            / (2 * Math.PI);
    String dir = "";
    if (current.getNodeType() != NodeType.ELEV && current.getNodeType() != NodeType.STAI) {
      if (last.getX() - current.getX() > hallWidth) {
        if (current.getY() - next.getY() > hallWidth) {
          if (turnAngle < 45 && turnAngle > 5) {
            dir += "slight right";
          } else if (turnAngle > 135 && turnAngle < 175) {
            dir += "sharp right";
          } else {
            dir += "right";
          }
          //            dir = "Go Straight";
        } else if (current.getY() - next.getY() < -hallWidth) {
          if (turnAngle < 45 && turnAngle > 5) {
            dir += "slight left";
          } else if (turnAngle > 135 && turnAngle < 175) {
            dir += "sharp left";
          } else {
            dir += "left";
          }
          //            dir = "Go Straight";
        } else {
          dir += "straight ahead";
        }
      } else if (last.getX() - current.getX() < -hallWidth) {
        if (current.getY() - next.getY() > hallWidth) {
          if (turnAngle < 45 && turnAngle > 5) {
            dir += "slight left";
          } else if (turnAngle > 135 && turnAngle < 175) {
            dir += "sharp left";
          } else {
            dir += "left";
          }
          //            dir = "Go Straight";
        } else if (current.getY() - next.getY() < -hallWidth) {
          if (turnAngle < 45 && turnAngle > 5) {
            dir += "slight right";
          } else if (turnAngle > 135 && turnAngle < 175) {
            dir += "sharp right";
          } else {
            dir += "right";
          }
          //            dir = "Go Straight";
        } else {
          dir += "straight ahead";
        }
      } else if (last.getY() - current.getY() > hallWidth) {
        if (current.getX() - next.getX() > hallWidth) {
          if (turnAngle < 45 && turnAngle > 5) {
            dir += "slight left";
          } else if (turnAngle > 135 && turnAngle < 175) {
            dir += "sharp left";
          } else {
            dir += "left";
          }
          //            dir = "Go Straight";
        } else if (current.getX() - next.getX() < -hallWidth) {
          if (turnAngle < 45 && turnAngle > 5) {
            dir += "slight right";
          } else if (turnAngle > 135 && turnAngle < 175) {
            dir += "sharp right";
          } else {
            dir += "right";
          }
          //            dir = "Go Straight";
        } else {
          dir += "straight ahead";
        }
      } else if (last.getY() - current.getY() < -hallWidth) {
        if (current.getX() - next.getX() > hallWidth) {
          if (turnAngle < 45 && turnAngle > 5) {
            dir += "slight right";
          } else if (turnAngle > 135 && turnAngle < 175) {
            dir += "sharp right";
          } else {
            dir += "right";
          }
          //            dir = "Go Straight";
        } else if (current.getX() - next.getX() < -hallWidth) {
          if (turnAngle < 45 && turnAngle > 5) {
            dir += "slight left";
          } else if (turnAngle > 135 && turnAngle < 175) {
            dir += "sharp left";
          } else {
            dir += "left";
          }
          //            dir = "Go Straight";
        } else {
          dir += "straight ahead";
        }
      }
    } else if (current.getNodeType() == NodeType.ELEV || current.getNodeType() == NodeType.STAI) {
      int floor = next.getFloor();
      if (current.getNodeType() == NodeType.ELEV && next.getNodeType() != NodeType.ELEV) {
        dir = "Take the elevator to floor ";
        if (floor == -2) {
          dir += "L2";
        } else if (floor == -1) {
          dir += "L1";
        } else if (floor == 0) {
          dir += "G";
        } else {
          dir += floor;
        }
      } else if (current.getNodeType() == NodeType.STAI && next.getNodeType() != NodeType.STAI) {
        dir = "Take the stairs to floor ";
        if (floor == -2) {
          dir += "L2";
        } else if (floor == -1) {
          dir += "L1";
        } else if (floor == 0) {
          dir += "G";
        } else {
          dir += floor;
        }
      } else if (last.getNodeType() != NodeType.ELEV || last.getNodeType() != NodeType.STAI) {
        dir = "First Landing";
      }
    }
    return dir;
  }
}
