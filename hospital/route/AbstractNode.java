package hospital.route;

import algorithm.PathVars;
import fxgraph.cells.AbstractCell;
import fxgraph.graph.Graph;
import java.util.ArrayList;

public abstract class AbstractNode {

  private String id;
  private AbstractCell cell;
  private double x;
  private double y;
  private String shortName;
  private String longName;
  private NodeType nodeType;
  private int floor;
  private boolean usable = true;
  private ArrayList<AbstractNode> adjacencies = new ArrayList<AbstractNode>();
  private String team;
  private PathVars vars = new PathVars();
  private String hospitalName;

  public AbstractNode(
      String id,
      double x,
      double y,
      String shortName,
      String longName,
      NodeType nodeType,
      int floor,
      String hospitalName,
      String team) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.shortName = shortName;
    this.longName = longName;
    this.nodeType = nodeType;
    this.floor = floor;
    this.hospitalName = hospitalName;
    this.team = team;
  }

  public abstract AbstractCell createCell(Graph graph);

  public void addAdjacency(AbstractNode node) {
    adjacencies.add(node);
  }

  public void removeAdjacency(AbstractNode node) {
    adjacencies.remove(node);
  }

  public boolean updateCoordinates() {
    if (x != cell.getCenterX() || y != cell.getCenterY()) {
      x = cell.getCenterX();
      y = cell.getCenterY();
      return true;
    }
    return false;
  }

  public void updateNodeData(NodeData data) {
    id = data.id;
    shortName = data.shortName;
    longName = data.longName;
    nodeType = data.nodeType;
  }

  public String getId() {
    return id;
  }

  public AbstractCell getCell() {
    return cell;
  }

  public void setCell(AbstractCell cell) {
    this.cell = cell;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public String getShortName() {
    return shortName;
  }

  public String getLongName() {
    return longName;
  }

  public NodeType getNodeType() {
    return nodeType;
  }

  public int getFloor() {
    return floor;
  }

  public boolean isUsable() {
    return usable;
  }

  public String getTeam() {
    return team;
  }

  public ArrayList<AbstractNode> getAdjacencies() {
    return adjacencies;
  }

  public String getHospitalName() {
    return hospitalName;
  }

  public PathVars getAlgoVars() {
    return vars;
  }

  public void setAlgoVars(PathVars vars) {
    this.vars = vars;
  }

  public String toString() {
    return longName;
  }

  public boolean equals(AbstractNode node) {
    return node != null && id.equals(node.getId());
  }

  public ArrayList<String> getAdjacenciesAsStrings() {
    ArrayList<String> adjacencies = new ArrayList<>();
    for (AbstractNode a : this.adjacencies) {
      adjacencies.add(a.getId());
    }
    return adjacencies;
  }
}
