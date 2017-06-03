/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tirtgui;

import hardware.Packet;
import hardware.Switch;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
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
            double sum = numberOfSameRejectedPacketSize + receivedPacketsWithSameSize + 1; // todo remove
            System.out.println("Size: " + packet.size);
            if (sum != 0) {
                double percentage = numberOfSameRejectedPacketSize / sum;
                percentages.add(new Data<>(String.valueOf(packet.size), percentage * 100.));
                System.out.println("percentages: " + percentage);
            }
        });
        series1.setData(percentages);
        return series1;
    }

}
