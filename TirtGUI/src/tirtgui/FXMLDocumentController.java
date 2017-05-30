/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tirtgui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

/**
 *
 * @author sebastiankotarski
 */
public class FXMLDocumentController implements Initializable {

    SwitchLoop switchLoop; // instance of switch loop, not launched yet
    ChartProvider chartProvider;
    long numberOfIterations = 0;

    @FXML
    private Button resetButton;
    @FXML
    private Button startButton;
    @FXML
    private ChoiceBox algorithmCombo;
    @FXML
    LineChart<String, Number> lineChart1;
    @FXML
    private Label numberOfIterationsLabel;

    @FXML
    private void startButtonAction(ActionEvent event) {
        if (switchLoop.isRunning()) {
            startButton.setText("Start");
            switchLoop.stop();
        } else {
            startButton.setText("Stop");
            updateViews();
            resetButton.setDisable(false);
            switchLoop.start();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupViews();
        initSwitch();
        configureCharts();
        resetButton.setDisable(true);
    }

    @FXML
    private void resetButtonAction(ActionEvent event) {
        numberOfIterations = 0;
        initSwitch();
        configureCharts();
    }

    public void updateViews() {
        this.numberOfIterationsLabel.setText("Liczba iteracji: " + this.numberOfIterations);
    }

    public void configureCharts() {
        // TODO
        lineChart1.getData().removeAll(lineChart1.getData());
        this.chartProvider = new ChartProvider(lineChart1);
        lineChart1.setCreateSymbols(false);
        chartProvider.makeChart();
    }
    int count = 0;

    public void updateCharts() {
        System.out.println("Updating charts");
        chartProvider.series1.getData().add(new XYChart.Data(String.valueOf(count++), Math.random() * 500));
    }

    public void initSwitch() {
        switchLoop = new SwitchLoop(2);
        switchLoop.handler = new Handler() {
            @Override
            public void updateUI() {
                updateCharts();
                numberOfIterations++;
                updateViews();
            }
        };
    }

    public void setupViews() {
        // TODO fill list of algorithms
        ObservableList<String> algorithms
                = FXCollections.observableArrayList(
                        "Option 1",
                        "Option 2",
                        "Option 3"
                );
        this.algorithmCombo.setItems(algorithms);
        this.algorithmCombo.getSelectionModel().selectFirst();
    }

    public void showAlertWithMessage(String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
