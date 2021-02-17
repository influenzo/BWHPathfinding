package fxgraph.cells;

import fxgraph.graph.Graph;
import hospital.route.AbstractNode;

public class RoomCell extends AbstractCell {

  public static double radius = 12;
  private static String color = "#6a6970";

  public RoomCell(double x, double y, AbstractNode node, Graph graph) {
    super(x, y, radius, color, node, graph, 0.7);
  }
}
