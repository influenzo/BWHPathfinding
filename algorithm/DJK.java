package algorithm;

import hospital.route.AbstractNode;
import hospital.route.NodeType;
import hospital.route.RouteController;
import java.util.*;

public class DJK implements Algorithm {

  private RouteController hospital;
  private Path path;
  private ArrayList<NodeType> disabledTypes = null;
  private List<AbstractNode> graph;
  private Queue<AbstractNode> queue;

  public DJK(RouteController hospital) {
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
    HashMap<String, AbstractNode> nodes = hospital.getNodes();
    Set<String> keys = nodes.keySet();
    for (String key : keys) {
      AbstractNode now = nodes.get(key);
      if (disabledTypes.contains(now.getNodeType())) {
        now.getAlgoVars().setUsable(false);
      }
    }
  }

  public void getPath(AbstractNode source, NodeType roomType) {
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

  public void getPath(AbstractNode source, AbstractNode target) {
    dijkstra(source, target);

    //    List<AbstractNode> pathList = new LinkedList<AbstractNode>();
    //
    //    AbstractNode current = source;
    //    pathList.add(current);
    //    while (current.getAlgoVars().getParent() != null) {
    //      pathList.add(current);
    //      current = current.getAlgoVars().getParent();
    //    }
    //    pathList.add(current);
    //
    //    path = new Path(pathList);
    clearAll();
  }

  private void clearAll() {
    for (AbstractNode x : hospital.getNodesList()) {
      x.getAlgoVars().clear();
    }
  }

  private void dijkstra(AbstractNode source, AbstractNode target) {
    graph = hospital.getNodesList();
    graph = hospital.getNodesList();
    queue = new LinkedList<>();
    for (AbstractNode node : graph) {
      if (!node.equals(source)) {
        node.getAlgoVars().setG(Double.MAX_VALUE - 1);
      } else if (node.equals(source)) {
        node.getAlgoVars().setG(0.0);
      }
      queue.add(node);
    }
    while (!queue.isEmpty()) {
      AbstractNode v = getMinDistance();

      if (!disabledTypes.contains(v.getNodeType())) {
        for (AbstractNode neighbor : v.getAdjacencies()) {

          double g = distance(v, neighbor);
          if (g < neighbor.getAlgoVars().getG()) {
            neighbor.getAlgoVars().setG(g);
          }
        }
      }
    }
    setPath(source, target);
  }

  public void setPath(AbstractNode source, AbstractNode target) {
    boolean state = true;
    List<AbstractNode> tpath = new ArrayList<>();
    AbstractNode min = target;
    tpath.add(target);
    AbstractNode lastTarget = source;
    while (state) {
      if (lastTarget.getId() != target.getId()) {
        lastTarget = target;
        for (AbstractNode now : target.getAdjacencies()) {
          if (min.getAlgoVars().getG() > now.getAlgoVars().getG()) {
            min = now;
          }
          //        if (min.getId() == target.getId()) {
          //          state = false;
          //        }
        }
        tpath.add(0, min);
        target = min;
        if (min.getAlgoVars().getG() == 0) {
          tpath.add(0, source);
          state = false;
        }
      } else {
        state = false;
      }
    }

    path = new Path(tpath);
  }

  private AbstractNode getMinDistance() {
    AbstractNode minNode = queue.peek();
    for (AbstractNode node : queue) {
      if (minNode.getAlgoVars().getG() > node.getAlgoVars().getG()) {
        minNode = node;
      }
    }
    queue.remove(minNode);
    return minNode;
  }

  private double distance(AbstractNode now, AbstractNode next) {
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
}
