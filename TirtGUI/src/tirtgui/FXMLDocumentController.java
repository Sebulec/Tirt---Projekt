/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tirtgui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;

class InputPortModel {

    String name;
    Double value;

    public InputPortModel(String name) {
        this.name = name;
    }

    public InputPortModel(String name, Double value) {
        this.name = name;
        this.value = value;
    }
}

class OutputPortModel {

    String name;
    Double value;

    public OutputPortModel(String name) {
        this.name = name;
    }

    public OutputPortModel(String name, Double value) {
        this.name = name;
        this.value = value;
    }
}

/**
 *
 * @author sebastiankotarski
 */
public class FXMLDocumentController implements Initializable {

    SwitchLoop switchLoop; // instance of switch loop, not launched yet
    ChartProvider chartProvider;
    ObservableList<InputPortModel> inputPortModels;
    ObservableList<OutputPortModel> outputPortModels;

    long numberOfIterations = 0;
    @FXML
    private Button resetButton;
    @FXML
    private Button startButton;
    @FXML
    private ChoiceBox switchTypeCombo;
    @FXML
    private Spinner<Double> inputSpinner;
    @FXML
    private Spinner<Double> outputSpinner;
    @FXML
    LineChart<String, Number> lineChart1;
    @FXML
    private Label numberOfIterationsLabel;
    @FXML
    private ListView<InputPortModel> inputParamsListView;
    @FXML
    private ListView<OutputPortModel> outputParamsListView;

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

    @FXML
    private void addInputClicked(ActionEvent event) {
        Double value = this.inputSpinner.getValue();
        String name = "asda";
        InputPortModel inputPortModel = new InputPortModel(name, value);
        this.inputPortModels.add(inputPortModel);
        this.inputSpinner.getValueFactory().setValue(0.01);
    }

    @FXML
    private void editInputClicked(ActionEvent event) {
        InputPortModel inputPortModel = this.inputParamsListView.getSelectionModel().getSelectedItem();
        int selectedIndex = this.inputParamsListView.getSelectionModel().getSelectedIndex();
        Double value = this.inputSpinner.getValue();
        inputPortModel.value = value;
        inputPortModels.set(selectedIndex, inputPortModel);
        inputParamsListView.setItems(inputPortModels);
        this.inputSpinner.getValueFactory().setValue(0.01);
    }

    @FXML
    private void removeInputClicked(ActionEvent event) {
        InputPortModel inputPortModel = this.inputParamsListView.getSelectionModel().getSelectedItem();
        this.inputPortModels.remove(inputPortModel);
        this.inputSpinner.getValueFactory().setValue(0.01);
    }

    private void didSelectInput(InputPortModel inputPort) {
        this.inputSpinner.getValueFactory().setValue(inputPort.value);
    }

    private void didSelectOutput(OutputPortModel outputPortModel) {
        this.outputSpinner.getValueFactory().setValue(outputPortModel.value);
    }

    @FXML
    private void addOutputClicked(ActionEvent event) {
        Double value = this.outputSpinner.getValue();
        String name = "asda";
        OutputPortModel outputPortModel = new OutputPortModel(name, value);
        this.outputPortModels.add(outputPortModel);
        this.outputSpinner.getValueFactory().setValue(1.);
    }

    @FXML
    private void editOutputClicked(ActionEvent event) {
        OutputPortModel outputPortModel = this.outputParamsListView.getSelectionModel().getSelectedItem();
        int selectedIndex = this.outputParamsListView.getSelectionModel().getSelectedIndex();
        Double value = this.outputSpinner.getValue();
        outputPortModel.value = value;
        outputPortModels.set(selectedIndex, outputPortModel);
        outputParamsListView.setItems(outputPortModels);
        this.outputSpinner.getValueFactory().setValue(1.);
    }

    @FXML
    private void removeOutputClicked(ActionEvent event) {
        OutputPortModel outputPortModel = this.outputParamsListView.getSelectionModel().getSelectedItem();
        this.outputPortModels.remove(outputPortModel);
        this.outputSpinner.getValueFactory().setValue(1.);
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
        this.inputSpinner.setEditable(true);
        DoubleSpinnerValueFactory valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.01, 1, 0.5);
        valueFactory.setAmountToStepBy(0.01);
        this.inputSpinner.setValueFactory(valueFactory);
        this.inputPortModels = FXCollections.observableArrayList(
                new InputPortModel("input1", 0.55),
                new InputPortModel("input2", 0.3)
        );
        this.inputParamsListView.setItems(this.inputPortModels);
        this.inputParamsListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends InputPortModel> observable, InputPortModel oldValue, InputPortModel newValue) -> {
            if (newValue != null) {
                this.didSelectInput(newValue);
            }
        });

        this.outputSpinner.setEditable(true);
        valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(1, Double.MAX_VALUE, 100);
        valueFactory.setAmountToStepBy(1);
        this.outputSpinner.setValueFactory(valueFactory);
        this.outputPortModels = FXCollections.observableArrayList(
                new OutputPortModel("input1", 55.),
                new OutputPortModel("input2", 3.)
        );
        this.outputParamsListView.setItems(this.outputPortModels);
        this.outputParamsListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends OutputPortModel> observable, OutputPortModel oldValue, OutputPortModel newValue) -> {
            if (newValue != null) {
                this.didSelectOutput(newValue);
            }
        });
    }

    public void showAlertWithMessage(String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
