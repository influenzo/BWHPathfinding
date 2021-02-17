package hospital.service;

public enum FlowerType {
  Rose(3.5),
  Orchid(3.25),
  Bluebell(6.0),
  Tulip(1.5),
  Lily(3.0),
  Daisy(2.0);

  private double cost;

  FlowerType(double cost) {
    this.cost = cost;
  }

  public double price() {
    return cost;
  }
}
