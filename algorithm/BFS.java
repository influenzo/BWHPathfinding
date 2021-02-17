package algorithm;

import hospital.route.AbstractNode;
import hospital.route.NodeType;
import hospital.route.RouteController;
import java.util.*;

public class BFS implements Algorithm {
  private RouteController hospital;
  private Path path;
  private ArrayList<NodeType> disabledTypes = null;

  public BFS(RouteController hospital) {
    this.hospital = hospital;
  }

  @Override
  public Path getPath() {
    return path;
  }

  private void getPath(AbstractNode source, AbstractNode target) {
    BFS(source, target);
    clearAll();
  }

  private void getPath(AbstractNode source, NodeType targetType) {
    BFS(source, targetType);
    clearAll();
  }

  @Override
  public void getPath(AbstractNode source, NodeType nodeType, ArrayList<NodeType> disabledTypes) {
    this.disabledTypes = disabledTypes;
    setUnusable();

    getPath(source, nodeType);
    clearAll();
    this.disabledTypes = null;
  }

  @Override
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

  private void BFS(AbstractNode source, NodeType targetType) {
    Queue<AbstractNode> queue = new LinkedList<AbstractNode>();
    clearAll();
    AbstractNode target = null;
    // System.out.println("Starting BFS Search by node type");
    queue.add(source);

    done:
    while (!queue.isEmpty()) {
      AbstractNode now = queue.poll();
      if (!disabledTypes.contains(now.getNodeType())) {
        for (AbstractNode neighbor : now.getAdjacencies()) {
          if (!neighbor.getAlgoVars().getVisited() && neighbor.isUsable()) {
            neighbor.getAlgoVars().setParent(now);
            queue.add(neighbor);
            if (neighbor.getNodeType().equals(targetType)) {
              //   System.out.println("Were here" + neighbor.toString());
              target = neighbor;
              break done;
            }
          }
        }
        now.getAlgoVars().setVisited(true);
      }
    }
    printParents(target, source);
  }

  private void BFS(AbstractNode source, AbstractNode target) {
    Queue<AbstractNode> queue = new LinkedList<AbstractNode>();
    clearAll();
    // System.out.println("Starting BFS Search");
    queue.add(source);

    while (!queue.isEmpty()) {
      AbstractNode now = queue.poll();
      if (!disabledTypes.contains(now.getNodeType())) {
        for (AbstractNode neighbor : now.getAdjacencies()) {
          if (!neighbor.getAlgoVars().getVisited()) {
            neighbor.getAlgoVars().setParent(now);
            queue.add(neighbor);
            if (neighbor.equals(target)) {
              break;
            }
          }
        }
        now.getAlgoVars().setVisited(true);
      }
    }
    printParents(target, source);
  }

  public void printParents(AbstractNode end, AbstractNode start) {
    AbstractNode now = end;
    // System.out.println(node.toString());
    List<AbstractNode> BFS = new LinkedList<>();
    // System.out.println(now.toString());
    while (now.getAlgoVars().hasParent()) {
      // System.out.print("Parent: ");
      BFS.add(0, now);
      // System.out.println(now.getAlgoVars().getParent().getId());
      now = now.getAlgoVars().getParent();
    }
    BFS.add(0, now);
    BFS.add(0, start);
    path = new Path(BFS);
  }

  private void clearAll() {
    for (AbstractNode x : hospital.getNodesList()) {
      x.getAlgoVars().clear();
    }
  }
}
