/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tirtgui;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

/**
 *
 * @author sebastiankotarski
 */
public class ChartProvider {

    LineChart<String, Number> chart;
    XYChart.Series<String, Number> series1;

    public ChartProvider(LineChart<String, Number> chart) {
        this.chart = chart;
    }

    public void makeChart() {
        // TODO
        series1 = new XYChart.Series<>();
        chart.getData().addAll(series1);
    }

    public void cleanData() {
        chart.getData().removeAll(chart.getData());
    }
}
