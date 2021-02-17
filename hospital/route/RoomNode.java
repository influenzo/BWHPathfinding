package hospital.route;

import fxgraph.cells.AbstractCell;
import fxgraph.cells.RoomCell;
import fxgraph.graph.Graph;
import hospital.exceptions.IncompatibleNodeTypeException;
import hospital.exceptions.NullHospitalException;

public class RoomNode extends AbstractNode {

  public RoomNode(
      String id,
      double x,
      double y,
      String shortName,
      String longName,
      NodeType nodeType,
      int floor,
      String building,
      String team)
      throws IncompatibleNodeTypeException, NullHospitalException {
    super(id, x, y, shortName, longName, nodeType, floor, building, team);
    if (nodeType != null) {
      nodeType.asRoomType();
    }
  }

  public AbstractCell createCell(Graph graph) {
    AbstractCell cell = new RoomCell(getX(), getY(), this, graph);
    setCell(cell);
    return cell;
  }

  public void addAdjacency(AbstractNode node) {
    if (!node.getNodeType().isRoomType()) {
      super.addAdjacency(node);
    }
  }
}
