package fxgraph.edges;

import fxgraph.cells.AbstractCell;
import fxgraph.graph.Graph;

public class Edge extends AbstractEdge {

  public Edge(AbstractCell source, AbstractCell target, Graph graph) {
    super(source, target, graph);
  }
}
