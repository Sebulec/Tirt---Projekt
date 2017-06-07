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
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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

    Stats stats = new Stats();
    SwitchLoop switchLoop; // instance of switch loop, not launched yet
    ObservableList<InputPortModel> inputPortModels = FXCollections.observableArrayList();
    ObservableList<OutputPortModel> outputPortModels = FXCollections.observableArrayList();

    long numberOfIterations = 0;
    @FXML
    private Button startButton;
    @FXML
    private ChoiceBox<SwitchType> switchTypeCombo;
    @FXML
    private Spinner<Double> inputSpinner;
    @FXML
    private Spinner<Integer> outputSpinner;

    /**
     * * charts **
     */
    @FXML
    LineChart<String, Number> lineChart1;
    @FXML
    BarChart<String, Double> barChart1;
    @FXML
    BarChart<String, Double> barChart2;
    @FXML
    BarChart<String, Double> barChart3;
    @FXML
    BarChart<String, Double> barChart4;
    @FXML
    BarChart<String, Double> barChart5;
    @FXML
    BarChart<String, Double> barChart6;
    /**
     * * end charts **
     */
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
    private ComboBox<OutputPortModel> selectOutputComboBox;
    @FXML
    private Spinner<Integer> maxIterSpinner;
    @FXML
    private CheckBox maxIterCheckbox;
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
            configureCharts();
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
    }

    private InputPortModel makeInputPortFromFields() {
        double probabilityOfPacketArrival = this.inputSpinner.getValueFactory().getValue();
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
        barChart1.getData().removeAll(barChart1.getData());
        barChart1.setTitle("Procent odrzuceń ze względu na rozmiar pakietu");
        barChart1.getXAxis().setLabel("Rozmiar pakietu");
        barChart1.getYAxis().setLabel("Wartość[%]");
        barChart1.animatedProperty().set(false);

        barChart2.getData().removeAll(barChart2.getData());
        barChart2.setTitle("Średnie opóźnienie ze względu na rozmiar pakietu");
        barChart2.getXAxis().setLabel("Rozmiar pakietu");
        barChart2.getYAxis().setLabel("Wartość opóźnienia");
        barChart2.animatedProperty().set(false);

        barChart3.getData().removeAll(barChart3.getData());
        barChart3.setTitle("Średnie opóźnienie ze względu na port wejściowy");
        barChart3.getXAxis().setLabel("Port wejsćiowy");
        barChart3.getYAxis().setLabel("Wartość opóźnienia");
        barChart3.animatedProperty().set(false);

        barChart5.getData().removeAll(barChart3.getData());
        barChart5.setTitle("Średnie opóźnienie ze względu na port wyjściowy");
        barChart5.getXAxis().setLabel("Port wyjściowy");
        barChart5.getYAxis().setLabel("Wartość opóźnienia");
        barChart5.animatedProperty().set(false);

        barChart4.getData().removeAll(barChart4.getData());
        barChart4.setTitle("Procent odrzuceń ze względu na port wyjściowy");
        barChart4.getXAxis().setLabel("Port wyjściowy");
        barChart4.getYAxis().setLabel("Wartość[%]");
        barChart4.animatedProperty().set(false);

        barChart6.getData().removeAll(barChart6.getData());
        barChart6.setTitle("Procent odrzuceń ze względu na port wejściowy");
        barChart6.getXAxis().setLabel("Port wejściowy");
        barChart6.getYAxis().setLabel("Wartość[%]");
        barChart6.animatedProperty().set(false);

    }
    int count = 0;

    public void updateCharts() {
        int outId = this.selectOutputComboBox.getSelectionModel().getSelectedIndex();
        // bar chart percent rejected packets for size
        barChart1.getData().removeAll(barChart1.getData());
        barChart1.getData().addAll(stats.getPercentRejectedPackets(this.switchLoop.switchEntity, outId));

        // bar chart percent rejected packets for outputs
        barChart4.getData().removeAll(barChart4.getData());
        barChart4.getData().addAll(stats.getPercentRejectedPackets(this.switchLoop.switchEntity));

        // bar chart percent rejected packets for inputs 
        barChart6.getData().removeAll(barChart6.getData());
        barChart6.getData().addAll(stats.getPercentRejectedPacketsForInputs(this.switchLoop.switchEntity));

        // bar chart average delay for size
        barChart2.getData().removeAll(barChart2.getData());
        barChart2.getData().addAll(stats.getAverageDelayForOutput(this.switchLoop.switchEntity, outId));

        // bar chart average delat for input
        barChart3.getData().removeAll(barChart3.getData());
        barChart3.getData().addAll(stats.getAverageDelayForInputs(this.switchLoop.switchEntity));

        // bar chart average dleat for output
        barChart5.getData().removeAll(barChart5.getData());
        barChart5.getData().addAll(stats.getAverageDelayForOutputs(this.switchLoop.switchEntity));

    }

    public void initSwitch() {
        switchLoop = new SwitchLoop(0.5);
        switchLoop.handler = new Handler() {
            @Override
            public void updateUI() {
                updateCharts();
                numberOfIterations++;
                if (maxIterCheckbox.isSelected() && numberOfIterations > maxIterSpinner.getValue()) {
                    switchLoop.stop();
                    return;
                }
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
        this.selectOutputComboBox.setItems(this.outputParamsListView.getItems());
        this.selectOutputComboBox.getSelectionModel().selectFirst();
        this.selectOutputComboBox.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {

        });

        integerSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1);
        this.maxIterSpinner.setValueFactory(integerSpinnerValueFactory);

        this.maxIterCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                maxIterSpinner.setDisable(!newValue);
            }
        });

//        this.inputPortModels.addListener(new ListChangeListener<Object>() {
//            @Override
//            public void onChanged(ListChangeListener.Change<? extends Object> c) {
//                startButton.setDisable(inputPortModels.size() >= 1);
//            }
//        });
//
//        this.outputPortModels.addListener(new ListChangeListener<OutputPortModel>() {
//            @Override
//            public void onChanged(ListChangeListener.Change<? extends OutputPortModel> c) {
//                if (outputPortModels.size() >= 1) {
//                    // start enabled
//                } else {
//
//                }
//            }
//        });
    }

    public void showAlertWithMessage(String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
