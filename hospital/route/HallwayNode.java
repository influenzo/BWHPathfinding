package hospital.route;

import fxgraph.cells.AbstractCell;
import fxgraph.cells.HallwayCell;
import fxgraph.graph.Graph;
import hospital.exceptions.IncompatibleNodeTypeException;
import hospital.exceptions.NullHospitalException;

public class HallwayNode extends AbstractNode {

  public HallwayNode(
      String id,
      double x,
      double y,
      String shortName,
      String longName,
      NodeType nodeType,
      int floor,
      String building,
      String team)
      throws NullHospitalException, IncompatibleNodeTypeException {
    super(id, x, y, shortName, longName, nodeType, floor, building, team);
    if (nodeType != null) {
      nodeType.asHallwayType();
    }
  }

  public AbstractCell createCell(Graph graph) {
    AbstractCell cell = new HallwayCell(getX(), getY(), this, graph);
    setCell(cell);
    return cell;
  }
}
