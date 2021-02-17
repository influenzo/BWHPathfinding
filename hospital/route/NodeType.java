package hospital.route;

import hospital.exceptions.IncompatibleNodeTypeException;

public enum NodeType {
  REST(1),
  CONF(1),
  DEPT(1),
  SERV(1),
  FOOD(1),
  RETL(1),
  STAF(1),
  EXIT(1),
  KIOS(1),
  LAB(1),
  INFO(1),
  HALL(2),
  STAI(2),
  ELEV(2);

  private final int type; // 1 = RoomType, 2 = HallwayType, 3 = LandingType

  NodeType(int type) {
    this.type = type;
  }

  public NodeType asRoomType() throws IncompatibleNodeTypeException {
    return checkType(1);
  }

  public NodeType asHallwayType() throws IncompatibleNodeTypeException {
    return checkType(2);
  }

  public boolean isRoomType() {
    return this.type == 1;
  }

  public boolean isHallwayType() {
    return this.type == 2;
  }

  private NodeType checkType(int type) throws IncompatibleNodeTypeException {
    if (this.type == type) {
      return this;
    } else {
      throw new IncompatibleNodeTypeException();
    }
  }
}
