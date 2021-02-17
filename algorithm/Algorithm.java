package algorithm;

import hospital.route.AbstractNode;
import hospital.route.NodeType;
import java.util.ArrayList;

public interface Algorithm {

  public Path getPath();

  private void getPath(AbstractNode source, AbstractNode target) {}

  private void getPath(AbstractNode source, NodeType targetType) {}

  public void getPath(AbstractNode source, NodeType nodeType, ArrayList<NodeType> disabledTypes);

  public void getPath(AbstractNode source, AbstractNode target, ArrayList<NodeType> disabledTypes);

  private void clearAll() {}
}
