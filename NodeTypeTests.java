package edu.wpi.teamp;

import static org.junit.jupiter.api.Assertions.*;

import hospital.*;
import hospital.exceptions.IncompatibleNodeTypeException;
import hospital.exceptions.NullHospitalException;
import hospital.route.AbstractNode;
import hospital.route.HallwayNode;
import hospital.route.NodeType;
import hospital.route.RoomNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class NodeTypeTests extends ApplicationTest {

  public static HospitalController hController = AbstractNodeTests.hController;

  @BeforeAll
  static void initDatabase() {
    Hospital faulkner = new Hospital("Faulkner");
    hController.addHospital(faulkner);
  }

  @Test // for possible node types
  void testRoomNodeTypes() {
    NodeType.valueOf("REST");
    NodeType.valueOf("CONF");
    NodeType.valueOf("DEPT");
    NodeType.valueOf("SERV");
    NodeType.valueOf("FOOD");
    NodeType.valueOf("RETL");
    NodeType.valueOf("STAF");
    NodeType.valueOf("EXIT");
    NodeType.valueOf("KIOS");
    NodeType.valueOf("LAB");
    NodeType.valueOf("HALL");
    NodeType.valueOf("STAI");
    NodeType.valueOf("ELEV");
  }

  @Test // for no IncompatibleNodeTypeException and NullHospitalException errors thrown when
  // creating room nodes
  void testRoomNodeType() throws IncompatibleNodeTypeException, NullHospitalException {
    new RoomNode("id101", 1, 1, "short", "long", NodeType.valueOf("REST"), 1, "Faulkner", "P");
    new RoomNode("id102", 1, 1, "short", "long", NodeType.valueOf("CONF"), 1, "Faulkner", "P");
    new RoomNode("id104", 1, 1, "short", "long", NodeType.valueOf("DEPT"), 1, "Faulkner", "P");
    new RoomNode("id105", 1, 1, "short", "long", NodeType.valueOf("SERV"), 1, "Faulkner", "P");
    new RoomNode("id106", 1, 1, "short", "long", NodeType.valueOf("FOOD"), 1, "Faulkner", "P");
    new RoomNode("id107", 1, 1, "short", "long", NodeType.valueOf("RETL"), 1, "Faulkner", "P");
    new RoomNode("id108", 1, 1, "short", "long", NodeType.valueOf("STAF"), 1, "Faulkner", "P");
    new RoomNode("id109", 1, 1, "short", "long", NodeType.valueOf("EXIT"), 1, "Faulkner", "P");
    new RoomNode("id110", 1, 1, "short", "long", NodeType.valueOf("KIOS"), 1, "Faulkner", "P");
    new RoomNode("id111", 1, 1, "short", "long", NodeType.valueOf("LAB"), 1, "Faulkner", "P");
  }

  @Test // for IncompatibleNodeTypeException errors throw when making rooms with non-room node types
  void testRoomNodeTypeException() {
    assertThrows(
        IncompatibleNodeTypeException.class,
        () -> {
          new RoomNode(
              "id201", 1, 1, "short", "long", NodeType.valueOf("HALL"), 1, "Faulkner", "P");
        });
    assertThrows(
        IncompatibleNodeTypeException.class,
        () -> {
          new RoomNode(
              "id202", 1, 1, "short", "long", NodeType.valueOf("STAI"), 1, "Faulkner", "P");
        });
    assertThrows(
        IncompatibleNodeTypeException.class,
        () -> {
          new RoomNode(
              "id203", 1, 1, "short", "long", NodeType.valueOf("ELEV"), 1, "Faulkner", "P");
        });
  }

  @Test // for adding adjacencies to a room node (rooms cannot be adjacent to other rooms)
  void testAddRoom() throws Exception {
    RoomNode room = new RoomNode("id301", 1, 1, "short", "long", NodeType.CONF, 1, "Faulkner", "P");
    RoomNode otherRoom =
        new RoomNode("id302", 1, 1, "short", "long", NodeType.CONF, 1, "Faulkner", "P");
    room.addAdjacency(otherRoom);
    AbstractNode[] empty = {};
    assertArrayEquals(empty, room.getAdjacencies().toArray());

    HallwayNode hall =
        new HallwayNode("id303", 1, 2, "short", "long", NodeType.HALL, 1, "Faulkner", "P");
    room.addAdjacency(hall);
    AbstractNode[] hallList = {hall};
    assertArrayEquals(hallList, room.getAdjacencies().toArray());
  }

  @Test // for correctly identifying different node types
  void testIsType() {
    assertTrue(NodeType.REST.isRoomType());
    assertTrue(NodeType.CONF.isRoomType());
    assertTrue(NodeType.DEPT.isRoomType());
    assertTrue(NodeType.SERV.isRoomType());
    assertTrue(NodeType.FOOD.isRoomType());
    assertTrue(NodeType.RETL.isRoomType());
    assertTrue(NodeType.STAF.isRoomType());
    assertTrue(NodeType.EXIT.isRoomType());
    assertTrue(NodeType.KIOS.isRoomType());
    assertTrue(NodeType.LAB.isRoomType());
    assertTrue(NodeType.HALL.isHallwayType());
    assertTrue(NodeType.STAI.isHallwayType());
    assertTrue(NodeType.ELEV.isHallwayType());
  }

  @Test // for correctly identifying different node types
  void testIsNotType() {
    assertFalse(NodeType.HALL.isRoomType());
    assertFalse(NodeType.STAI.isRoomType());
    assertFalse(NodeType.ELEV.isRoomType());
    assertFalse(NodeType.DEPT.isHallwayType());
  }
}
