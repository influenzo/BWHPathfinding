package fxgraph.graph;

import javafx.scene.layout.Region;

public interface IGraphNode {

  Region getGraphic(fxgraph.graph.Graph graph);

  void disable();

  void enable();
}
