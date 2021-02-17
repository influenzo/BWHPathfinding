package algorithm;

import hospital.route.AbstractNode;
import hospital.route.NodeType;
import hospital.route.RouteController;
import java.util.*;

public class DFS implements Algorithm {
  private RouteController hospital;
  private Path path;
  private ArrayList<NodeType> disabledTypes = null;

  public DFS(RouteController hospital) {
    this.hospital = hospital;
  }

  @Override
  public Path getPath() {
    return path;
  }

  // getPath to node

  private void getPath(AbstractNode source, AbstractNode target) {
    DFS(source, target);
  }

  // getPath to type of node

  private void getPath(AbstractNode source, NodeType targetType) {
    DFS(source, targetType);
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

  // DFS type of node
  private void DFS(AbstractNode source, NodeType targetType) {
    Stack<AbstractNode> stack = new Stack<AbstractNode>();
    clearAll();
    // System.out.println("Starting DFS Search");
    stack.add(source);
    AbstractNode newNode = null;

    while (!stack.isEmpty()) {
      AbstractNode now = stack.pop();
      if (!disabledTypes.contains(now.getNodeType())) {
        if (!now.getAlgoVars().getVisited()
            && now.getAlgoVars().getUsuable()) { // if not visited yet

          List<AbstractNode> neighbor = now.getAdjacencies(); // get list of neighbor
          // System.out.println(now + ": " + neighbor);

          for (int i = 0; i < neighbor.size(); i++) { // for each neighbor
            newNode = neighbor.get(i);
            if (!newNode.getAlgoVars().getVisited()) {
              newNode.getAlgoVars().setParent(now); // set the parent node
              stack.add(newNode); // add to stack
            }

            if (newNode
                .getNodeType()
                .equals(targetType)) { // if the new node is equal to the target
              now.getAlgoVars().setVisited(true);

              break;
            }
          }
        }
        now.getAlgoVars().setVisited(true);
      }
      // System.out.println("out of while loop");

    }
    printParents(newNode, source);
  }

  // DFS to node
  private void DFS(AbstractNode source, AbstractNode target) {
    Stack<AbstractNode> stack = new Stack<AbstractNode>();
    clearAll();
    // System.out.println("Starting DFS Search");
    stack.add(source);
    AbstractNode newNode = null;

    out:
    while (!stack.isEmpty()) {
      AbstractNode now = stack.pop();
      if (!disabledTypes.contains(now.getNodeType())) {
        if (!now.getAlgoVars().getVisited()) { // if not visited yet

          List<AbstractNode> neighbor = now.getAdjacencies(); // get list of neighbor
          // System.out.println(now + ": " + neighbor);

          for (int i = 0; i < neighbor.size(); i++) { // for each neighbor
            newNode = neighbor.get(i);
            if (!newNode.getAlgoVars().getVisited()) {
              newNode.getAlgoVars().setParent(now); // set the parent node

              // if new node isnt visited
              stack.add(newNode); // add to stack
            }
            if (newNode.equals(target)) { // if the new node is equal to the target
              now.getAlgoVars().setVisited(true);

              break out;
            }
          }
        }
        now.getAlgoVars().setVisited(true);
      }
    }
    printParents(target, source);
  }

  // print parents
  public void printParents(AbstractNode end, AbstractNode start) {
    AbstractNode now = end;
    // System.out.println(node.toString());
    List<AbstractNode> DFS = new LinkedList<>();
    // System.out.println(now.toString());
    while (now.getAlgoVars().hasParent()) {
      // System.out.print("Parent: ");
      DFS.add(0, now);
      // System.out.println(now.getAlgoVars().getParent().getId());
      now = now.getAlgoVars().getParent();
    }
    DFS.add(0, now);
    DFS.add(0, start);
    path = new Path(DFS);
  }

  // clear all in algo
  private void clearAll() {
    for (AbstractNode x : hospital.getNodesList()) {
      x.getAlgoVars().clear();
    }
  }
}
