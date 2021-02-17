package algorithm;

import hospital.*;
import hospital.route.AbstractNode;
import hospital.route.NodeType;
import hospital.route.RouteController;
import java.util.*;
import javax.swing.*;

public class AStar implements Algorithm {

  private RouteController hospital;
  private Path path;
  private ArrayList<NodeType> disabledTypes = null;

  public AStar(RouteController hospital) {
    this.hospital = hospital;
  }

  public Path getPath() {
    return path;
  }

  public void getPath(AbstractNode source, NodeType roomType, ArrayList<NodeType> disabledTypes) {

    this.disabledTypes = disabledTypes;
    setUnusable();

    getPath(source, roomType);
    clearAll();
    this.disabledTypes = null;
  }

  public void getPath(AbstractNode source, AbstractNode target, ArrayList<NodeType> disabledTypes) {
    this.disabledTypes = disabledTypes;
    setUnusable();
    getPath(source, target);
    clearAll();
    this.disabledTypes = null;
  }

  private void setUnusable() {
    if (disabledTypes != null) {
      HashMap<String, AbstractNode> nodes = hospital.getNodes();
      Set<String> keys = nodes.keySet();
      for (String key : keys) {
        AbstractNode now = nodes.get(key);
        if (disabledTypes.contains(now.getNodeType())) {
          now.getAlgoVars().setUsable(false);
        }
      }
    }
  }

  private void getPath(AbstractNode source, NodeType roomType) {
    HashMap<String, AbstractNode> nodes = hospital.getNodes();
    Set<String> keys = nodes.keySet();
    double smallestCost = Double.MAX_VALUE;
    AbstractNode target = source;

    for (String key : keys) {
      AbstractNode now = nodes.get(key);
      if (now.getNodeType().equals(roomType)) {
        getPath(source, now);
        clearAll();
        setUnusable();
        if (path.getCost() < smallestCost) {
          smallestCost = path.getCost();
          target = now;
        }
      }
    }
    getPath(source, target);
  }

  private void getPath(AbstractNode source, AbstractNode target) {
    aStar(source, target);

    List<AbstractNode> pathList = new LinkedList<AbstractNode>();

    AbstractNode current = source;
    pathList.add(current);
    while (current.getAlgoVars().getParent() != null) {
      pathList.add(current);
      current = current.getAlgoVars().getParent();
    }
    pathList.add(current);

    path = new Path(pathList);
    clearAll();
  }

  private void clearAll() {
    for (AbstractNode x : hospital.getNodesList()) {
      x.getAlgoVars().clear();
    }
  }

  private void aStar(AbstractNode source, AbstractNode target) {
    setHeuristic(target);

    List<AbstractNode> path = new ArrayList<AbstractNode>();
    List<AbstractNode> open = new ArrayList<AbstractNode>();
    List<AbstractNode> closed = new ArrayList<AbstractNode>();

    if (source.getAlgoVars().getH() == 0) {
      return;
    }
    double cost = 0;
    AbstractNode now = source;

    // Start of A* Algorithm
    open.add(now);

    while (!now.equals(target)) {
      now = getlowestF(open, now);

      open.remove(now);
      closed.add(now);
      for (AbstractNode neighbor : now.getAdjacencies()) {

        if (neighbor.getAlgoVars().getUsuable()) {
          cost = gManhattan(now, neighbor);

          if (cost > neighbor.getAlgoVars().getG() && closed.contains(neighbor)) {
            neighbor.getAlgoVars().setParent(now);
          } else if (cost < neighbor.getAlgoVars().getG() && open.contains(neighbor)) {
            neighbor.getAlgoVars().setG(cost);
            neighbor.getAlgoVars().setParent(now);
          } else if (!open.contains(neighbor) && (!closed.contains(neighbor))) {
            neighbor.getAlgoVars().setG(cost);
            open.add(neighbor);
          }
        }
      }
    }
  }

  private AbstractNode getlowestF(List<AbstractNode> openList, AbstractNode now) {
    double lowestf = Double.MAX_VALUE;
    double tmpF = 0;
    AbstractNode tmp = now;

    for (AbstractNode Node : openList) {
      if (Node.getAlgoVars().getUsuable()) {
        tmpF = Node.getAlgoVars().getG() + Node.getAlgoVars().getH();
        Node.getAlgoVars().setF(tmpF);
        if (Node.getAlgoVars().getF() < lowestf) {
          tmp = Node;
          lowestf = Node.getAlgoVars().getF();
        }
      }
    }
    return tmp;
  }
  // TODO:: Add if for landings
  private double gManhattan(AbstractNode now, AbstractNode next) {
    double g;
    if (now.getNodeType() == NodeType.STAI && next.getNodeType() == NodeType.STAI) {
      g = now.getAlgoVars().getG() + hospital.getStairCost();
    } else if (now.getNodeType() == NodeType.ELEV && next.getNodeType() == NodeType.ELEV) {
      g = now.getAlgoVars().getG() + hospital.getElevatorCost();
    } else {
      g =
          now.getAlgoVars().getG()
              + Math.abs(now.getX() - next.getX())
              + Math.abs(now.getY() - next.getY());
    }
    return g;
  }
  // TODO:: Add if for landings
  private double hManhattan(AbstractNode now, AbstractNode next) {
    double h;
    if (now.getNodeType() == NodeType.STAI && next.getNodeType() == NodeType.STAI) {
      h = now.getAlgoVars().getH() + hospital.getStairCost();
    } else if (now.getNodeType() == NodeType.ELEV && next.getNodeType() == NodeType.ELEV) {
      h = now.getAlgoVars().getH() + hospital.getElevatorCost();
    } else {
      h =
          now.getAlgoVars().getH()
              + Math.abs(now.getX() - next.getX())
              + Math.abs(now.getY() - next.getY());
    }
    return h;
  }

  // heuristic (this value is equal to the cost of moving from n to the goal, by running a BFS from
  // our goal)
  // this allows for the A* to follow the best algorithm and never expand anything else
  private void setHeuristic(AbstractNode target) {
    Queue<AbstractNode> queue = new LinkedList<AbstractNode>();
    queue.add(target);
    target.getAlgoVars().setH(0);
    target.getAlgoVars().setVisited(true);

    while (!queue.isEmpty()) {
      AbstractNode now = queue.poll();
      if (!disabledTypes.contains(now.getNodeType())) {
        for (AbstractNode neighbor : now.getAdjacencies()) {
          if (!neighbor.getAlgoVars().getVisited()) {
            double h = hManhattan(now, neighbor);
            neighbor.getAlgoVars().setH(h);
            queue.add(neighbor);
          }
        }
        now.getAlgoVars().setVisited(true);
      }
    }
  }
}
