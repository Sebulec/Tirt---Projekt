/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tirtgui;

import hardware.APacketDestination;
import hardware.IPacketSource;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;

/**
 *
 * @author sebastiankotarski
 */
public class FXMLDocumentController implements Initializable {

    SwitchLoop switchLoop; // instance of switch loop, not launched yet
    ChartProvider chartProvider;
    ObservableList<InputPortModel> inputPortModels = FXCollections.observableArrayList();
    ObservableList<OutputPortModel> outputPortModels = FXCollections.observableArrayList();

    long numberOfIterations = 0;
    @FXML
    private Button resetButton;
    @FXML
    private Button startButton;
    @FXML
    private ChoiceBox<SwitchType> switchTypeCombo;
    @FXML
    private Spinner<Double> inputSpinner;
    @FXML
    private Spinner<Integer> outputSpinner;
    @FXML
    LineChart<String, Number> lineChart1;
    @FXML
    private Label numberOfIterationsLabel;
    @FXML
    private ListView<InputPortModel> inputParamsListView;
    @FXML
    private ListView<OutputPortModel> outputParamsListView;
    @FXML
    private Spinner<Integer> cellSize;
    @FXML
    private ComboBox<String> packetTypeCombo;
    @FXML
    private Spinner<Integer> packetMinimalSizeSpinner;
    @FXML
    private Spinner<Integer> packetMaximalSizeSpinner;
    @FXML
    private Spinner<Double> probabilityOfBusinessSpinner;
    @FXML
    private Group inputsGroup;
    @FXML
    private Group outputsGroup;
    @FXML
    private Spinner<Integer> inputQueueSizeSpinner;
    @FXML
    private Spinner<Integer> outputQueueSizeSpinner;
    @FXML
    private ComboBox<OutputPortModel> outputComboBox; // todo

    @FXML
    private void startButtonAction(ActionEvent event) {
        if (switchLoop.isRunning()) {
            startButton.setText("Start");
            inputsGroup.setDisable(false);
            outputsGroup.setDisable(false);
            switchTypeCombo.setDisable(false);
            cellSize.setDisable(false);
            switchLoop.stop();
        } else {
            startButton.setText("Stop");
            inputsGroup.setDisable(true);
            outputsGroup.setDisable(true);
            switchTypeCombo.setDisable(true);
            cellSize.setDisable(true);
            prepareValues();
            updateViews();
            resetButton.setDisable(false);
            switchLoop.start();
        }
    }

    public void prepareValues() {
        SwitchType switchType = switchTypeCombo.getValue();
        int cellSizeValue = this.cellSize.getValue();
        ArrayList<IPacketSource> sources = new ArrayList<>();
        ArrayList<APacketDestination> destinations = new ArrayList<>();
        ArrayList<Integer> inputQueuesSizes = new ArrayList<>(inputPortModels.size());
        ArrayList<Integer> outputQueuesSizes = new ArrayList<>(outputPortModels.size());
        inputPortModels.forEach((inputPortModel) -> {
            sources.add(inputPortModel.makePacketSource());
            inputQueuesSizes.add(inputPortModel.getQueueSize());
        });
        outputPortModels.forEach((outputPortModel) -> {
            destinations.add(outputPortModel.makeDestinationPacket());
            outputQueuesSizes.add(outputPortModel.queueSize);
        });
        HashMap options = new HashMap();
        options.put("sources", sources);
        options.put("destinations", destinations);
        options.put("inputQueuesSizes", inputQueuesSizes);
        options.put("outputQueuesSizes", outputQueuesSizes);
        switchLoop.configureSwitch(cellSizeValue, switchType, options);
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

    private InputPortModel makeInputPortFromFields() {
        double probabilityOfPacketArrival = this.inputSpinner.getValue();
        int packetMinimalSize = this.packetMinimalSizeSpinner.getValue();
        int packetMaximalSize = this.packetMaximalSizeSpinner.getValue();
        int queueSize = this.inputQueueSizeSpinner.getValue();
        int outId = -1;
        if (!this.outputComboBox.isDisabled()) {
            outId = this.outputComboBox.getSelectionModel().getSelectedIndex();
        }
        return new InputPortModel(probabilityOfPacketArrival, packetMinimalSize, packetMaximalSize, outId, queueSize);
    }

    private OutputPortModel makeOutputPortFromFields() {
        double probabilityOfBusiness = this.probabilityOfBusinessSpinner.getValue();
        int capacity = this.outputSpinner.getValue();
        int queueSize = this.outputQueueSizeSpinner.getValue();
        return new OutputPortModel(capacity, probabilityOfBusiness, queueSize);
    }

    private void clearInputPorts() {
        this.inputSpinner.getValueFactory().setValue(0.01);
        this.packetMaximalSizeSpinner.getValueFactory().setValue(1);
        this.packetMinimalSizeSpinner.getValueFactory().setValue(1);
        this.inputQueueSizeSpinner.getValueFactory().setValue(1);
    }

    private void clearOutputPorts() {
        this.outputSpinner.getValueFactory().setValue(1);
        this.probabilityOfBusinessSpinner.getValueFactory().setValue(0.5);
        this.outputQueueSizeSpinner.getValueFactory().setValue(1);
    }

    @FXML

    private void addInputClicked(ActionEvent event) {
        InputPortModel inputPortModel = makeInputPortFromFields();
        clearInputPorts();
        this.inputPortModels.add(inputPortModel);
        this.inputParamsListView.setItems(inputPortModels);
    }

    @FXML
    private void editInputClicked(ActionEvent event) {
        InputPortModel inputPortModel;
        int selectedIndex = this.inputParamsListView.getSelectionModel().getSelectedIndex();
        inputPortModel = makeInputPortFromFields();
        inputPortModels.set(selectedIndex, inputPortModel);
        inputParamsListView.setItems(inputPortModels);
        this.clearInputPorts();
    }

    @FXML
    private void removeInputClicked(ActionEvent event) {
        InputPortModel inputPortModel = this.inputParamsListView.getSelectionModel().getSelectedItem();
        this.inputPortModels.remove(inputPortModel);
        clearInputPorts();
    }

    private void didSelectInput(InputPortModel inputPortModel) {
        this.inputSpinner.getValueFactory().setValue(inputPortModel.probabilityOfPacketArrival);
        this.packetMinimalSizeSpinner.getValueFactory().setValue(inputPortModel.packetMinimalSize);
        this.packetMaximalSizeSpinner.getValueFactory().setValue(inputPortModel.packetMaximalSize);
        this.packetTypeCombo.setValue(inputPortModel.outId != -1 ? "Wybrany output" : "Losowy output");
        this.inputQueueSizeSpinner.getValueFactory().setValue(inputPortModel.queueSize);
        if (inputPortModel.outId != -1) {
            // todo
            this.outputComboBox.setDisable(false);
        } else {
            this.outputComboBox.setDisable(true);
        }
    }

    private void didSelectOutput(OutputPortModel outputPortModel) {
        this.probabilityOfBusinessSpinner.getValueFactory().setValue(outputPortModel.probabilityOfBusiness);
        this.outputSpinner.getValueFactory().setValue(outputPortModel.capacity);
        this.outputQueueSizeSpinner.getValueFactory().setValue(outputPortModel.queueSize);
    }

    private void didSelectSwitchType(SwitchType switchType) {
        switch (switchType) {
            case InputQueueing:
                this.outputQueueSizeSpinner.setDisable(true);
                this.inputQueueSizeSpinner.setDisable(false);
                break;
            case OutputQueueing:
                this.outputQueueSizeSpinner.setDisable(false);
                this.inputQueueSizeSpinner.setDisable(true);
                break;
            case VirtualOutputQueueing:
                this.inputQueueSizeSpinner.setDisable(false);
                this.outputQueueSizeSpinner.setDisable(false);
                break;
        }
    }

    @FXML
    private void addOutputClicked(ActionEvent event) {
        OutputPortModel outputPortModel = makeOutputPortFromFields();
        this.outputPortModels.add(outputPortModel);
        this.outputParamsListView.setItems(outputPortModels);
        clearOutputPorts();
    }

    @FXML
    private void editOutputClicked(ActionEvent event) {
        OutputPortModel outputPortModel;
        int selectedIndex = this.outputParamsListView.getSelectionModel().getSelectedIndex();
        outputPortModel = makeOutputPortFromFields();
        outputPortModels.set(selectedIndex, outputPortModel);
        outputParamsListView.setItems(outputPortModels);
    }

    @FXML
    private void removeOutputClicked(ActionEvent event) {
        OutputPortModel outputPortModel = this.outputParamsListView.getSelectionModel().getSelectedItem();
        this.outputPortModels.remove(outputPortModel);
        clearOutputPorts();
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
                // step for switch
                switchLoop.switchEntity.step();
                System.out.println("Packets: " + switchLoop.switchEntity.getRejectedPackets());
                updateCharts();
                numberOfIterations++;
                updateViews();
            }
        };
    }

    public void setupViews() {
        this.packetTypeCombo.setItems(FXCollections.observableArrayList("Losowy output", "Wybrany output"));
        this.packetTypeCombo.setValue("Losowy output");
        this.packetTypeCombo.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            this.outputComboBox.setDisable(!newValue.equals("Wybrany output"));
            this.outputComboBox.setItems(this.outputParamsListView.getItems());
        });

        IntegerSpinnerValueFactory integerSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1);
        this.cellSize.setValueFactory(integerSpinnerValueFactory);
        integerSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1);
        packetMinimalSizeSpinner.setValueFactory(integerSpinnerValueFactory);
        integerSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1);
        packetMaximalSizeSpinner.setValueFactory(integerSpinnerValueFactory);

        integerSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1);
        this.inputQueueSizeSpinner.setValueFactory(integerSpinnerValueFactory);

        integerSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1);
        outputQueueSizeSpinner.setValueFactory(integerSpinnerValueFactory);

        this.switchTypeCombo.setItems(FXCollections.observableArrayList(SwitchType.values()));
        this.switchTypeCombo.getSelectionModel().selectFirst();
        this.switchTypeCombo.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends SwitchType> observable, SwitchType oldValue, SwitchType newValue) -> {
            if (newValue != null) {
                this.didSelectSwitchType(newValue);
            }
        });

        this.inputSpinner.setEditable(true);
        DoubleSpinnerValueFactory valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.01, 1, 0.5);
        valueFactory.setAmountToStepBy(0.01);
        this.inputSpinner.setValueFactory(valueFactory);

        valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.01, 1, 0.5);
        valueFactory.setAmountToStepBy(0.01);
        probabilityOfBusinessSpinner.setValueFactory(valueFactory);

        this.inputParamsListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends InputPortModel> observable, InputPortModel oldValue, InputPortModel newValue) -> {
            if (newValue != null) {
                this.didSelectInput(newValue);
            }
        });

        this.outputSpinner.setEditable(true);
        integerSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1);
        this.outputSpinner.setValueFactory(integerSpinnerValueFactory);
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
