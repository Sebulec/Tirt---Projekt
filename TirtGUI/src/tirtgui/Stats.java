/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tirtgui;

import hardware.Packet;
import hardware.Switch;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

/**
 *
 * @author sebastiankotarski
 */
public class Stats {

    public XYChart.Series<String, Double> getPercentRejectedPackets(Switch switchEntity, int outId) {
        List<List<Packet>> rejectedPackets = switchEntity.getRejectedPackets();
        List<List<Packet>> receivedPackets = switchEntity.getReceivedPackets();
        XYChart.Series<String, Double> series1 = new XYChart.Series<>();
        if (outId < 0) {
            return series1;
        }
        ObservableList<Data<String, Double>> percentages = FXCollections.observableArrayList();
        HashSet<Integer> packetsSizes = new HashSet<>();
        List<Packet> rejectedPacketsForOutput = rejectedPackets.get(outId);
        packetsSizes.addAll(Arrays.asList(rejectedPacketsForOutput.stream().map((packet) -> packet.size).toArray(Integer[]::new)));
        List<Packet> receivedPacketsForOutput = receivedPackets.get(outId);
        packetsSizes.addAll(Arrays.asList(receivedPacketsForOutput.stream().map((packet) -> packet.size).toArray(Integer[]::new)));
        rejectedPacketsForOutput.sort((Packet o1, Packet o2) -> Integer.compare(o1.size, o2.size));
        rejectedPacketsForOutput.stream().forEach((packet) -> {
            double receivedPacketsWithSameSize = receivedPacketsForOutput.stream().filter((Packet t) -> t.size == packet.size).count();
            double numberOfSameRejectedPacketSize = rejectedPacketsForOutput.stream().filter((Packet t) -> t.size == packet.size).count();
            double sum = numberOfSameRejectedPacketSize + receivedPacketsWithSameSize;
            if (sum != 0) {
                double percentage = numberOfSameRejectedPacketSize / sum;
                percentages.add(new Data<>(String.valueOf(packet.size), percentage * 100.));
            }
        });
        series1.setData(percentages);
        return series1;
    }

    public XYChart.Series<String, Double> getAverageDelayForOutput(Switch switchEntity, int outId) {
        XYChart.Series<String, Double> series1 = new XYChart.Series<>();
        if (outId < 0) {
            return series1;
        }
        List<Packet> receivedPacketsForOutput = switchEntity.getReceivedPackets().get(outId);
        HashSet<Integer> packetsSizes = new HashSet<>();
        packetsSizes.addAll(Arrays.asList(receivedPacketsForOutput.stream().map((packet) -> packet.size).toArray(Integer[]::new)));
        receivedPacketsForOutput.sort((Packet o1, Packet o2) -> Integer.compare(o1.size, o2.size));
        ObservableList<Data<String, Double>> averages = FXCollections.observableArrayList();
        packetsSizes.stream().forEach((packetSize) -> {
            double average = receivedPacketsForOutput.stream().filter((packet) -> packet.size == packetSize).mapToInt(Packet::getDelay).average().getAsDouble();
            averages.add(new Data<>(String.valueOf(packetSize), average));
        });
        series1.setData(averages);
        return series1;
    }

    public XYChart.Series<String, Double> getAverageDelayForInputs(Switch switchEntity) {
        XYChart.Series<String, Double> series1 = new XYChart.Series<>();
        HashSet<Integer> inputs = new HashSet<>();
        List<Packet> receivedPackets = switchEntity.getReceivedPackets().stream().flatMap(List::stream).collect(Collectors.toList());

        inputs.addAll(receivedPackets.stream().mapToInt(Packet::getInputId).boxed().collect(Collectors.toList()));

        ObservableList<Data<String, Double>> averages = FXCollections.observableArrayList();
        inputs.stream().forEach((input) -> {
            double average = receivedPackets.stream().filter((packet) -> packet.getInputId() == input).mapToInt(Packet::getDelay).average().getAsDouble();
            averages.add(new Data<>(String.valueOf(input), average));
        });
        return series1;
    }

    public XYChart.Series<String, Double> getAverageDelayForOutputs(Switch switchEntity) {
        XYChart.Series<String, Double> series1 = new XYChart.Series<>();
        HashSet<Integer> outputs = new HashSet<>();
        List<Packet> receivedPackets = switchEntity.getReceivedPackets().stream().flatMap(List::stream).collect(Collectors.toList());

        outputs.addAll(receivedPackets.stream().mapToInt(Packet::getOutputId).boxed().collect(Collectors.toList()));

        ObservableList<Data<String, Double>> averages = FXCollections.observableArrayList();
        outputs.stream().forEach((output) -> {
            double average = receivedPackets.stream().filter((packet) -> packet.getOutputId() == output).mapToInt(Packet::getDelay).average().getAsDouble();
            averages.add(new Data<>(String.valueOf(output), average));
        });
        return series1;
    }

    public XYChart.Series<String, Double> getPercentRejectedPackets(Switch switchEntity) {
        List<List<Packet>> rejectedPackets = switchEntity.getRejectedPackets();
        List<List<Packet>> receivedPackets = switchEntity.getReceivedPackets();
        XYChart.Series<String, Double> series1 = new XYChart.Series<>();
        ObservableList<Data<String, Double>> percentages = FXCollections.observableArrayList();
        int index = 0;
        for (List<Packet> packets : rejectedPackets) {
            double numberOfRejectedPackets = packets.size();
            double sum = numberOfRejectedPackets + receivedPackets.get(index).size();
            if (sum != 0) {
                double percentage = numberOfRejectedPackets / sum;
                percentages.add(new Data<>("Output " + index, percentage * 100.));
                System.out.println("Output: " + index + ", %: " + percentage);
            }
            index++;
        }
        series1.setData(percentages);
        return series1;
    }

    public XYChart.Series<String, Double> getPercentRejectedPacketsForInputs(Switch switchEntity) {
        List<Packet> receivedPackets = switchEntity.getReceivedPackets().stream().flatMap(List::stream).collect(Collectors.toList());
        List<Packet> rejectedPackets = switchEntity.getRejectedPackets().stream().flatMap(List::stream).collect(Collectors.toList());

        HashSet<Integer> inputs = new HashSet();
        inputs.addAll(receivedPackets.stream().mapToInt(Packet::getInputId).boxed().collect(Collectors.toList()));
        inputs.addAll(rejectedPackets.stream().mapToInt(Packet::getInputId).boxed().collect(Collectors.toList()));

        XYChart.Series<String, Double> series1 = new XYChart.Series<>();
        ObservableList<Data<String, Double>> percentages = FXCollections.observableArrayList();
        int index = 0;

        for (Integer input : inputs) {
            double numberOfRejectedPackets = rejectedPackets.stream().filter((packet) -> packet.getInputId() == input).count();
            double sum = numberOfRejectedPackets + receivedPackets.stream().filter((packet) -> packet.getInputId() == input).count();
            if (sum != 0) {
                double percentage = numberOfRejectedPackets / sum;
                percentages.add(new Data<>("Input " + index, percentage * 100.));
                System.out.println("Input: " + index + "perctange: " + percentage);
            }
            index++;
        }
        series1.setData(percentages);
        return series1;
    }
}
