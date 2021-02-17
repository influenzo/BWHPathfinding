package fxgraph.graph;

import hospital.route.NodeData;

public interface GraphHelper {

  void respond(Graph graph, NodeData data);

  void respond(Graph graph, NodeData data, IGraphNode source);
}
