package edu.wpi.teamp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fxgraph.cells.AbstractCell;
import fxgraph.graph.Graph;
import hospital.*;
import hospital.exceptions.IncompatibleNodeTypeException;
import hospital.exceptions.NullHospitalException;
import hospital.route.AbstractNode;
import hospital.route.NodeType;
import hospital.route.RoomNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class AbstractNodeTests extends ApplicationTest {

  public static HospitalController hController = HospitalController.getHospitalController();
  public static Hospital hospital101;
  public static Hospital hospital102;

  @BeforeAll
  static void initDatabase() {
    hospital101 = new Hospital("hospital 101");
    hospital102 = new Hospital("hospital 102");
    hController.addHospital(hospital101);
    hController.addHospital(hospital102);
  }

  @Test // for node equivalency checking
  void testEquals() throws IncompatibleNodeTypeException, NullHospitalException {
    AbstractNode node =
        new RoomNode(
            "id501", 1, 1, "short", "long", NodeType.valueOf("DEPT"), 1, "hospital 101", "P");
    AbstractNode node2 =
        new RoomNode(
            "id502", 1, 1, "short", "long", NodeType.valueOf("DEPT"), 1, "hospital 101", "P");
    assertTrue(node.equals(node));
    assertFalse(node.equals(node2));
  }

  @Test
  void testUpdateCoordinates() throws IncompatibleNodeTypeException, NullHospitalException {
    AbstractNode node =
        new RoomNode(
            "id601", 1, 1, "short", "long", NodeType.valueOf("DEPT"), 1, "hospital 101", "P");
    assertTrue(node.getX() == 1);
    assertTrue(node.getY() == 1);
    Graph graph = new Graph(hospital101.getRouteController(), 5, "hospital 101");
    AbstractCell cell = node.createCell(graph);
    cell.getGraphic(graph);
    graph.addCell(cell);
    graph.update();
    node.updateCoordinates();
    assertTrue(node.getX() == 1);
    assertTrue(node.getY() == 1);
    cell.relocate(2, 2);
    node.updateCoordinates();
    assertTrue(node.getX() == 2);
    assertTrue(node.getY() == 2);
  }
}
