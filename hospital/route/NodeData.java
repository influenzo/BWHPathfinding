package hospital.route;

public class NodeData {

  public String id;
  public String shortName;
  public String longName;
  public NodeType nodeType;

  public NodeData(String id, String shortName, String longName, NodeType nodeType) {
    this.id = id;
    this.shortName = shortName;
    this.longName = longName;
    this.nodeType = nodeType;
  }
}
