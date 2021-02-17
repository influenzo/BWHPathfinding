package hospital.route;

import algorithm.*;
import edu.wpi.teamp.Controllers.MapEditingController;
import edu.wpi.teamp.Controllers.NavigationController;
import edu.wpi.teamp.UIController;
import fxgraph.edges.Edge;
import fxgraph.graph.Graph;
import fxgraph.graph.PannableCanvas;
import hospital.Database;
import hospital.IController;
import hospital.MongoDB;
import hospital.exceptions.NullNodeException;
import java.util.*;

public class RouteController implements IController {

  private double elevatorCost;
  private double stairCost;
  private HashMap<Integer, Graph> maps = new HashMap<>();
  public Algorithm pathfinder = new AStar(this);
  private HashMap<String, AbstractNode> nodes = new HashMap<String, AbstractNode>();
  private String homeNodeId;
  private Database database = Database.getDatabase();
  private MongoDB mongoDB = MongoDB.getMongoDB();
  private String hospitalName;
  private MapEditingController mapEditingController;
  private NavigationController navigationController;
  private UIController uController = UIController.getUIController();

  public RouteController(
      String hospitalName, double elevatorCost, double stairCost, String homeNodeId) {
    this.hospitalName = hospitalName;
    this.elevatorCost = elevatorCost;
    this.stairCost = stairCost;
    this.homeNodeId = homeNodeId;
  }

  // adds a node to a hospital and that hospital's database, adds appropriate adjacencies
  // adds node to database based on toDatabase (toDatabase = null when loading from local database)
  public void addNode(AbstractNode node, int databaseFlag) {
    nodes.put(node.getId(), node);
    for (AbstractNode adjacency : node.getAdjacencies()) {
      adjacency.addAdjacency(node);
    }
    if (databaseFlag == 1) {
      database.createConnection(hospitalName);
      database.updateNode(true, node);
      database.closeConnection();
    } else if (databaseFlag == 2) {
      mongoDB.getDatabase(hospitalName);
      mongoDB.updateNode(true, node);
    }
  }

  public void addNode(AbstractNode node) {
    addNode(node, uController.getDatabaseFlag());
  }

  // removes a node from a hospital and that hospital's database, removes appropriate adjacencies
  public void removeNode(String nodeId, int databaseFlag) throws NullNodeException {
    if (hasNode(nodeId)) {
      AbstractNode node = nodes.get(nodeId);
      nodes.remove(nodeId);
      for (AbstractNode adjacency : node.getAdjacencies()) {
        adjacency.removeAdjacency(node);
      }
      if (databaseFlag == 1) {
        database.createConnection(hospitalName);
        database.removeNode(nodeId);
        database.closeConnection();
      } else if (databaseFlag == 2) {
        mongoDB.getDatabase(hospitalName);
        mongoDB.removeNode(nodeId);
      }
    } else {
      throw new NullNodeException();
    }
  }

  public void removeNode(String nodeId) throws NullNodeException {
    removeNode(nodeId, uController.getDatabaseFlag());
  }

  public void updateNode(AbstractNode node, int databaseFlag) {
    if (databaseFlag == 1) {
      database.createConnection(hospitalName);
      database.updateNode(false, node);
      database.closeConnection();
    } else if (databaseFlag == 2) {
      mongoDB.getDatabase(hospitalName);
      mongoDB.updateNode(false, node);
    }
  }

  public void updateNode(AbstractNode node) {
    updateNode(node, uController.getDatabaseFlag());
  }

  public void setAlgorithm(String algo) {
    if (algo.equals("A-Star")) {
      pathfinder = new AStar(this);
    } else if (algo.equals("BFS")) {
      pathfinder = new BFS(this);
    } else if (algo.equals("DFS")) {
      pathfinder = new DFS(this);
    } else if (algo.equals("DJK")) {
      pathfinder = new DJK(this);
    }
  }

  public void updateNodeData(String nodeId, NodeData data) throws NullNodeException {
    if (hasNode(nodeId)) {
      getNode(nodeId).updateNodeData(data);
      updateNode(getNode(nodeId));
    } else {
      throw new NullNodeException();
    }
  }

  public ArrayList<AbstractNode> getNodesList() {
    ArrayList<AbstractNode> nodesList = new ArrayList<AbstractNode>();
    for (String key : nodes.keySet()) {
      nodesList.add(nodes.get(key));
    }
    return nodesList;
  }

  public ArrayList<AbstractNode> getNodesList(int floor) {
    ArrayList<AbstractNode> floorNodes = new ArrayList<AbstractNode>();
    for (AbstractNode node : getNodesList()) {
      if (node.getFloor() == floor) {
        floorNodes.add(node);
      }
    }
    return floorNodes;
  }

  public HashMap<NodeType, ArrayList<AbstractNode>> getNodesListByType() {
    return getNodesListByType(-1);
  }

  public HashMap<NodeType, ArrayList<AbstractNode>> getNodesListByType(int floor) {
    HashMap<NodeType, ArrayList<AbstractNode>> nodeTypeMap = new HashMap<>();
    for (NodeType type : NodeType.values()) {
      nodeTypeMap.put(type, new ArrayList<>());
    }
    for (AbstractNode node : floor == -1 ? getNodesList() : getNodesList(floor)) {
      nodeTypeMap.get(node.getNodeType()).add(node);
    }
    return nodeTypeMap;
  }

  public ArrayList<AbstractNode> getRooms() {
    ArrayList<AbstractNode> rooms = new ArrayList<AbstractNode>();
    for (AbstractNode node : getNodesList()) {
      if (node.getNodeType().isRoomType()) {
        rooms.add(node);
      }
    }
    return rooms;
  }

  public ArrayList<AbstractNode> getRooms(int floor) {
    ArrayList<AbstractNode> rooms = new ArrayList<AbstractNode>();
    for (AbstractNode node : getNodesList(floor)) {
      if (node.getNodeType().isRoomType()) {
        rooms.add(node);
      }
    }
    return rooms;
  }

  public ArrayList<ArrayList<AbstractNode>> getEdges() {
    ArrayList<ArrayList<AbstractNode>> edges = new ArrayList<>();
    HashMap<String, ArrayList<String>> adjacencyLists = new HashMap<>();

    // clone adjacency lists
    for (AbstractNode node : getNodesList()) {
      ArrayList<String> adjacencyList = new ArrayList<>();
      for (AbstractNode adjacency : node.getAdjacencies()) {
        adjacencyList.add(adjacency.getId());
      }
      adjacencyLists.put(node.getId(), adjacencyList);
    }

    for (String nodeId : adjacencyLists.keySet()) {
      for (String adjacency : adjacencyLists.get(nodeId)) {
        ArrayList<AbstractNode> edge = new ArrayList<>(Arrays.asList(nodes.get(nodeId)));
        edge.add(nodes.get(adjacency)); // build edge
        adjacencyLists.get(adjacency).remove(nodeId); // adjacency removal prevents edge duplication
        edges.add(edge);
      }
    }

    return edges;
  }

  public ArrayList<ArrayList<AbstractNode>> getEdges(int floor) {
    ArrayList<ArrayList<AbstractNode>> edges = new ArrayList<>();
    HashMap<String, ArrayList<String>> adjacencyLists = new HashMap<>();

    // clone adjacency lists
    for (AbstractNode node : getNodesList(floor)) {
      ArrayList<String> adjacencyList = new ArrayList<>();
      for (AbstractNode adjacency : node.getAdjacencies()) {
        adjacencyList.add(adjacency.getId());
      }
      adjacencyLists.put(node.getId(), adjacencyList);
    }

    for (String nodeId : adjacencyLists.keySet()) {
      for (String adjacency : adjacencyLists.get(nodeId)) {
        if (nodes.get(adjacency).getFloor() == floor) {
          ArrayList<AbstractNode> edge = new ArrayList<>(Arrays.asList(nodes.get(nodeId)));
          edge.add(nodes.get(adjacency)); // build edge
          adjacencyLists.get(adjacency).remove(nodeId); // edge duplication prevention
          edges.add(edge);
        }
      }
    }

    return edges;
  }

  // source node -> target node
  public List<Direction> getRoute(
      AbstractNode source, AbstractNode target, ArrayList<NodeType> disabledTypes) {
    clearPaths();
    pathfinder.getPath(source, target, disabledTypes);

    List<Direction> directions = pathfinder.getPath().getDirections();

    List<AbstractNode> path = pathfinder.getPath().getNodes();
    if (hospitalName.equals("Main")) {
      for (int floor = -2; floor <= 3; floor++) {
        maps.get(floor).highlightPath(path);
      }

    } else if (hospitalName.equals("Faulkner")) {
      for (int floor = 1; floor <= 5; floor++) {
        maps.get(floor).highlightPath(path);
      }
    }
    centerMaps(path);
    return directions;
  }

  // source node -> target node type w/ hospital.route.path color
  public List<Direction> getRoute(
      AbstractNode source, NodeType nodeType, String color, ArrayList<NodeType> disabledTypes) {
    clearPaths();
    pathfinder.getPath(source, nodeType, disabledTypes);
    List<Direction> directions = pathfinder.getPath().getDirections();
    List<AbstractNode> path = pathfinder.getPath().getNodes();
    if (hospitalName.equals("Main")) {
      for (int floor = -2; floor <= 3; floor++) {
        maps.get(floor).highlightPath(path);
      }

    } else if (hospitalName.equals("Faulkner")) {
      for (int floor = 1; floor <= 5; floor++) {
        maps.get(floor).highlightPath(path);
      }
    }
    centerMaps(path);
    return directions;
  }

  public void getEmergencyExit() {
    clearPaths();
    pathfinder.getPath(
        getHomeNode(), NodeType.EXIT, new ArrayList<NodeType>(Arrays.asList(NodeType.ELEV)));
    List<Direction> directions = pathfinder.getPath().getDirections();
    List<AbstractNode> path = pathfinder.getPath().getNodes();
    if (hospitalName.equals("Main")) {
      for (int floor = -2; floor <= 3; floor++) {
        maps.get(floor).highlightPath(path, "#C62828");
      }

    } else if (hospitalName.equals("Faulkner")) {
      for (int floor = 1; floor <= 5; floor++) {
        maps.get(floor).highlightPath(path, "#C62828");
      }
    }
  }

  // source node -> target node type w/o hospital.route.path color
  public List<Direction> getRoute(
      AbstractNode source, NodeType nodeType, ArrayList<NodeType> disabledTypes) {
    return getRoute(source, nodeType, "#e67e10", disabledTypes);
  }

  public void generateMap(int floor, boolean canEdit) {
    Graph map = new Graph(this, floor, hospitalName);
    maps.put(floor, map);
    for (AbstractNode node : getNodesList(floor)) {
      map.addCell(node.createCell(map));
    }
    for (ArrayList<AbstractNode> edge : getEdges(floor)) {
      map.addEdge(new Edge(edge.get(0).getCell(), edge.get(1).getCell(), map));
    }
    map.update();
    if (canEdit) {
      map.enableEditing();
      map.showCells();
      map.showEdges();
    } else {
      map.disableEditing();
      map.hideCells();
      map.hideEdges();
    }
  }

  public void unhighlightCells() {
    for (int floor : maps.keySet()) {
      maps.get(floor).unhighlightCells();
    }
  }

  public void clearPaths() {
    for (int floor : maps.keySet()) {
      maps.get(floor).clearPath();
    }
  }

  public void saveMapChanges() {
    for (int floor : maps.keySet()) {
      maps.get(floor).saveChanges();
    }
  }

  public void cancelMapChanges() {
    for (int floor : maps.keySet()) {
      maps.get(floor).clearChanges();
    }
  }

  public void escape() {
    for (int floor : maps.keySet()) {
      maps.get(floor).escape();
    }
  }

  public void shift(boolean pressed) {
    for (int floor : maps.keySet()) {
      maps.get(floor).setShiftPressed(pressed);
    }
  }

  public void delete() {
    for (int floor : maps.keySet()) {
      maps.get(floor).delete();
    }
  }

  public void downloadCSV(String directory) {
    database.createConnection(hospitalName);
    database.exportNodesToCSV(directory);
    database.exportEdgesToCSV(directory, this);
    database.closeConnection();
  }

  public void clear() {
    nodes = new HashMap<>();
    maps = new HashMap<>();
  }

  public void navigationClick(double x, double y, int floor) {
    AbstractNode closestNode = null;
    double distance = 999999;
    for (AbstractNode node : getRooms(floor)) {
      double newDistance = getDistance(x, y, node);
      if (newDistance < distance) {
        distance = newDistance;
        closestNode = node;
      }
    }
    navigationController.handleMapClick(closestNode);
  }

  public double getDistance(double x, double y, AbstractNode node) {
    return Math.sqrt(Math.pow(node.getX() - x, 2) + Math.pow(node.getY() - y, 2));
  }

  public AbstractNode getHomeNode() {
    return getNode(homeNodeId);
  }

  public double getElevatorCost() {
    return elevatorCost;
  }

  public void setElevatorCost(double elevatorCost) {
    this.elevatorCost = elevatorCost;
  }

  public double getStairCost() {
    return stairCost;
  }

  public void setStairCost(double stairCost) {
    this.stairCost = stairCost;
  }

  public HashMap<String, AbstractNode> getNodes() {
    return nodes;
  }

  public AbstractNode getNode(String nodeId) {
    return nodes.get(nodeId);
  }

  public boolean hasNode(String nodeId) {
    return nodes.keySet().contains(nodeId);
  }

  public void setMapEditingController(MapEditingController mapEditingController) {
    this.mapEditingController = mapEditingController;
  }

  public MapEditingController getMapEditingController() {
    return mapEditingController;
  }

  public void setNavigationController(NavigationController navigationController) {
    this.navigationController = navigationController;
  }

  public NavigationController getNavigationController() {
    return navigationController;
  }

  public PannableCanvas getMap(int floor) {
    return maps.get(floor).getCanvas();
  }

  public Graph getGraph(int floor) {
    return maps.get(floor);
  }

  public String getHospitalName() {
    return hospitalName;
  }

  public void setHomeNodeId(String id) {
    if (nodes.containsKey(id)) {
      homeNodeId = id;
    }
  }

  public void centerMaps(List<AbstractNode> path) {
    String hospital = path.get(0).getHospitalName();
    int floor = path.get(0).getFloor();
    AbstractNode current;
    double lowestX = Double.MAX_VALUE;
    double lowestY = Double.MAX_VALUE;
    double biggestX = 0;
    double biggestY = 0;
    int counter = 0;
    for (int i = 0; i < path.size(); i++) {
      current = path.get(i);
      if (current.getHospitalName().equals(hospital) && current.getFloor() == floor) {
        counter++;
        if (current.getX() < lowestX) {
          lowestX = current.getX();
        }
        if (current.getX() > biggestX) {
          biggestX = current.getX();
        }
        if (current.getY() < lowestY) {
          lowestY = current.getY();
        }
        if (current.getY() > biggestY) {
          biggestY = current.getY();
        }
      } else {
        if (counter > 1) {

          double centerX = (lowestX + biggestX) / 2;
          double centerY = (lowestY + biggestY) / 2;
          double scale = 1;
          if ((biggestX - lowestX) > (uController.getPrimaryScreenBounds().getWidth() - 500)
              || (biggestY - lowestY) > (uController.getPrimaryScreenBounds().getHeight() - 150)) {

            if (biggestX - lowestX > biggestY - lowestY) {
              scale =
                  (uController.getPrimaryScreenBounds().getWidth() - 500) / (biggestX - lowestX);
            } else if (biggestY - lowestY > biggestX - lowestX) {
              scale =
                  (uController.getPrimaryScreenBounds().getHeight() - 150) / (biggestY - lowestY);
            }
          }
          if (scale != 1) {
            maps.get(path.get(i - 1).getFloor())
                .moveCenterToPoint(
                    (uController.getPrimaryScreenBounds().getWidth() - 454) / 2,
                    (uController.getPrimaryScreenBounds().getHeight() - 90) / 2,
                    centerX,
                    centerY,
                    scale,
                    true);
          } else {
            maps.get(path.get(i - 1).getFloor())
                .moveCenterToPoint(
                    (uController.getPrimaryScreenBounds().getWidth() - 454) / 2,
                    (uController.getPrimaryScreenBounds().getHeight() - 90) / 2,
                    centerX,
                    centerY);
          }
        }
        hospital = current.getHospitalName();
        floor = current.getFloor();
        lowestX = Double.MAX_VALUE;
        lowestY = Double.MAX_VALUE;
        biggestX = 0;
        biggestY = 0;
        counter = 0;
        i--;
      }
    }
    double centerX = (lowestX + biggestX) / 2;
    double centerY = (lowestY + biggestY) / 2;
    double scale = 1;
    if ((biggestX - lowestX) > (uController.getPrimaryScreenBounds().getWidth() - 500)
        || (biggestY - lowestY) > (uController.getPrimaryScreenBounds().getHeight() - 150)) {
      if (biggestX - lowestX > biggestY - lowestY) {
        scale = (uController.getPrimaryScreenBounds().getWidth() - 500) / (biggestX - lowestX);
      } else if (biggestY - lowestY > biggestX - lowestX) {
        scale = (uController.getPrimaryScreenBounds().getHeight() - 150) / (biggestY - lowestY);
      }
    }
    if (scale != 1) {
      maps.get(path.get(path.size() - 1).getFloor())
          .moveCenterToPoint(
              (uController.getPrimaryScreenBounds().getWidth() - 454) / 2,
              (uController.getPrimaryScreenBounds().getHeight() - 90) / 2,
              centerX,
              centerY,
              scale,
              false);
    } else {
      maps.get(path.get(path.size() - 1).getFloor())
          .moveCenterToPoint(
              (uController.getPrimaryScreenBounds().getWidth() - 454) / 2,
              (uController.getPrimaryScreenBounds().getHeight() - 90) / 2,
              centerX,
              centerY);
    }
  }
}
