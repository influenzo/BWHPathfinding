package edu.wpi.teamp.Controllers;

import edu.wpi.teamp.UIController;
import hospital.service.ServiceController;
import hospital.service.ServiceType;
import java.util.Arrays;
import java.util.HashMap;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class AnalyticsController {

  UIController uController = UIController.getUIController();
  ServiceController sController = uController.getServiceController();

  @FXML BarChart avChart = new BarChart(new CategoryAxis(), new NumberAxis());
  @FXML BarChart flowersChart = new BarChart(new CategoryAxis(), new NumberAxis());
  @FXML BarChart itChart = new BarChart(new CategoryAxis(), new NumberAxis());
  @FXML BarChart interpreterChart = new BarChart(new CategoryAxis(), new NumberAxis());

  @FXML BarChart avSumChart = new BarChart(new CategoryAxis(), new NumberAxis());
  @FXML BarChart flowerSumChart = new BarChart(new CategoryAxis(), new NumberAxis());
  @FXML BarChart itSumChart = new BarChart(new CategoryAxis(), new NumberAxis());
  @FXML BarChart interpreterSumChart = new BarChart(new CategoryAxis(), new NumberAxis());

  XYChart.Series<String, Integer> avData = new XYChart.Series();
  XYChart.Series<String, Integer> flowerData = new XYChart.Series();
  XYChart.Series<String, Integer> itData = new XYChart.Series();
  XYChart.Series<String, Integer> interpreterData = new XYChart.Series();

  XYChart.Series<String, Integer> avSumData = new XYChart.Series();
  XYChart.Series<String, Integer> flowerSumData = new XYChart.Series();
  XYChart.Series<String, Integer> itSumData = new XYChart.Series();
  XYChart.Series<String, Integer> interpreterSumData = new XYChart.Series();

  private String[] colors = new String[10];

  @FXML
  public void initialize() {
    connectToThread();
    Arrays.asList("red", "orange", "#dae01b", "green", "blue", "DodgerBlue", "purple", "pink")
        .toArray(colors);

    loadData();
    avChart.getData().add(avData);
    flowersChart.getData().add(flowerData);
    itChart.getData().add(itData);
    interpreterChart.getData().add(interpreterData);

    avSumChart.getData().add(avSumData);
    flowerSumChart.getData().add(flowerSumData);
    itSumChart.getData().add(itSumData);
    interpreterSumChart.getData().add(interpreterSumData);

    setColors(avChart);
    setColors(flowersChart);
    setColors(itChart);
    setColors(interpreterChart);

    setColors(avSumChart);
    setColors(flowerSumChart);
    setColors(itSumChart);
    setColors(interpreterSumChart);
  }

  public void loadData() {
    HashMap<String, Integer> avDataMap = sController.getCountMap(ServiceType.AudioVisual);
    HashMap<String, Integer> flDataMap = sController.getCountMap(ServiceType.Florist);
    HashMap<String, Integer> itDataMap = sController.getCountMap(ServiceType.InfoTech);
    HashMap<String, Integer> inDataMap = sController.getCountMap(ServiceType.Interpreter);

    for (String type : avDataMap.keySet()) {
      avData.getData().add(new XYChart.Data(type, avDataMap.get(type)));
    }
    for (String type : flDataMap.keySet()) {
      flowerData.getData().add(new XYChart.Data(type, flDataMap.get(type)));
    }
    for (String type : itDataMap.keySet()) {
      itData.getData().add(new XYChart.Data(type, itDataMap.get(type)));
    }
    for (String type : inDataMap.keySet()) {
      interpreterData.getData().add(new XYChart.Data(type, inDataMap.get(type)));
    }

    for (String type : avDataMap.keySet()) {
      avSumData.getData().add(new XYChart.Data(type, avDataMap.get(type)));
    }
    for (String type : flDataMap.keySet()) {
      flowerSumData.getData().add(new XYChart.Data(type, flDataMap.get(type)));
    }
    for (String type : itDataMap.keySet()) {
      itSumData.getData().add(new XYChart.Data(type, itDataMap.get(type)));
    }
    for (String type : inDataMap.keySet()) {
      interpreterSumData.getData().add(new XYChart.Data(type, inDataMap.get(type)));
    }
  }

  public void updateData() {
    connectToThread();
    sController = uController.getServiceController();
    HashMap<String, Integer> avDataMap = sController.getCountMap(ServiceType.AudioVisual);
    HashMap<String, Integer> flDataMap = sController.getCountMap(ServiceType.Florist);
    HashMap<String, Integer> itDataMap = sController.getCountMap(ServiceType.InfoTech);
    HashMap<String, Integer> inDataMap = sController.getCountMap(ServiceType.Interpreter);

    for (XYChart.Data<String, Integer> data : avData.getData()) {
      data.setYValue(avDataMap.get(data.getXValue()));
    }
    for (XYChart.Data<String, Integer> data : flowerData.getData()) {
      data.setYValue(flDataMap.get(data.getXValue()));
    }
    for (XYChart.Data<String, Integer> data : itData.getData()) {
      data.setYValue(itDataMap.get(data.getXValue()));
    }
    for (XYChart.Data<String, Integer> data : interpreterData.getData()) {
      data.setYValue(inDataMap.get(data.getXValue()));
    }

    for (XYChart.Data<String, Integer> data : avSumData.getData()) {
      data.setYValue(avDataMap.get(data.getXValue()));
    }
    for (XYChart.Data<String, Integer> data : flowerSumData.getData()) {
      data.setYValue(flDataMap.get(data.getXValue()));
    }
    for (XYChart.Data<String, Integer> data : itSumData.getData()) {
      data.setYValue(itDataMap.get(data.getXValue()));
    }
    for (XYChart.Data<String, Integer> data : interpreterSumData.getData()) {
      data.setYValue(inDataMap.get(data.getXValue()));
    }
  }

  public void connectToThread() {
    if (uController.getHospital().getMongoDBThread() != null) {
      uController.getHospital().getMongoDBThread().setAnalyticsController(this);
    } else if (uController.getOtherHospital().getMongoDBThread() != null) {
      uController.getOtherHospital().getMongoDBThread().setAnalyticsController(this);
    }
  }

  public void setColors(BarChart chart) {
    Node a;
    int i = 0;
    while ((a = chart.lookup(".data" + i + ".chart-bar")) != null) {
      a.setStyle("-fx-bar-fill: " + colors[i]);
      i++;
    }
  }
}
