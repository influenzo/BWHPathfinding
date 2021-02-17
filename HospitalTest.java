package edu.wpi.teamp;

import static org.junit.jupiter.api.Assertions.*;

import hospital.*;
import hospital.exceptions.IncompatibleNodeTypeException;
import hospital.exceptions.NullHospitalException;
import hospital.route.AbstractNode;
import hospital.route.NodeType;
import hospital.route.RoomNode;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class HospitalTest extends ApplicationTest {

  public static HospitalController hController = AbstractNodeTests.hController;
  public static Hospital hospital301;
  public static Hospital hospital302;

  @BeforeAll
  static void intiDatabase() {
    hospital301 = new Hospital("hospital 301");
    hospital302 = new Hospital("hospital 302");
    hController.addHospital(hospital301);
    hController.addHospital(hospital302);
  }

  @Test
  // Add the same node twice, should only do it once
  void addSameNode() throws IncompatibleNodeTypeException, NullHospitalException {
    AbstractNode node =
        new RoomNode("id701", 0, 0, "node", "node", NodeType.DEPT, 5, "hospital 301", "P");
    hospital301.getRouteController().addNode(node);
    assertEquals(1, hospital301.getRouteController().getNodes().size());
    hospital301.getRouteController().addNode(node);
  }

  @Test
  void getFloorList() throws IncompatibleNodeTypeException, NullHospitalException {
    AbstractNode node =
        new RoomNode("id801", 0, 0, "node", "node", NodeType.DEPT, 1, "hospital 302", "P");
    AbstractNode node1 =
        new RoomNode("id802", 1, 1, "node1", "node1", NodeType.FOOD, 2, "hospital 302", "P");
    AbstractNode node2 =
        new RoomNode("id803", 1, 1, "node2", "node2", NodeType.KIOS, 2, "hospital 302", "P");
    AbstractNode node3 =
        new RoomNode("id804", 1, 1, "node3", "node3", NodeType.LAB, 1, "hospital 302", "P");
    AbstractNode node4 =
        new RoomNode("id805", 1, 1, "node4", "node4", NodeType.EXIT, 1, "hospital 302", "P");
    AbstractNode node5 =
        new RoomNode("id806", 1, 1, "node5", "node5", NodeType.KIOS, 1, "hospital 302", "P");
    hospital302.getRouteController().addNode(node);
    hospital302.getRouteController().addNode(node1);
    hospital302.getRouteController().addNode(node2);
    hospital302.getRouteController().addNode(node3);
    hospital302.getRouteController().addNode(node4);
    hospital302.getRouteController().addNode(node5);

    ArrayList<AbstractNode> floor1 = hospital302.getRouteController().getNodesList(1);
    ArrayList<String> floor1Ids = new ArrayList<>();
    for (AbstractNode n : floor1) {
      floor1Ids.add(n.getId());
    }
    assertTrue(floor1Ids.contains("id801"));
    assertFalse(floor1Ids.contains("id802"));
    assertFalse(floor1Ids.contains("id803"));
    assertTrue(floor1Ids.contains("id804"));
    assertTrue(floor1Ids.contains("id805"));
    assertTrue(floor1Ids.contains("id806"));
  }
}
