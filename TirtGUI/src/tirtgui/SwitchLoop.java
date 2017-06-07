/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tirtgui;

import hardware.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.animation.KeyFrame;
import schedulers.ISLIPScheduler;

enum SwitchType {
    InputQueueing, OutputQueueing, VirtualOutputQueueing
}

/**
 *
 * @author sebastiankotarski
 */
public class SwitchLoop {

    private static Duration PROBE_FREQUENCY;
    private final Timeline timeline;
    public Handler handler;
    public Switch switchEntity;

    public SwitchLoop(long probeDuration) {
        PROBE_FREQUENCY = Duration.seconds(probeDuration);
        timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO, (ActionEvent actionEvent) -> {
                            System.out.println("event handle");
                            this.handle(actionEvent);
                        }),
                new KeyFrame(
                        PROBE_FREQUENCY
                )
        );
        timeline.setDelay(PROBE_FREQUENCY);
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void configureSwitch(int cellSize, SwitchType switchType, HashMap options) {
        switch (switchType) {
            case InputQueueing:
                ArrayList<IPacketSource> sources = new ArrayList<>();
                sources.addAll((Collection<? extends IPacketSource>) options.get("sources"));
                int inputQueuesSizes[] = ((ArrayList<Integer>) options.get("inputQueuesSizes")).stream().mapToInt(i -> i).toArray();
                ArrayList<APacketDestination> destinations = new ArrayList<>();
                destinations.addAll((Collection<? extends APacketDestination>) options.get("destinations"));
                this.switchEntity = new Switch(cellSize, sources, inputQueuesSizes, destinations);
                this.switchEntity.setScheduler(new ISLIPScheduler(sources.size(), destinations.size())); // to do
                return;
            case OutputQueueing:
                sources = new ArrayList<>();
                sources.addAll((Collection<? extends IPacketSource>) options.get("sources"));
                int outputQueuesSizes[] = ((ArrayList<Integer>) options.get("outputQueuesSizes")).stream().mapToInt(i -> i).toArray();
                destinations = new ArrayList<>();
                destinations.addAll((Collection<? extends APacketDestination>) options.get("destinations"));
                this.switchEntity = new Switch(cellSize, sources, destinations, outputQueuesSizes);
                this.switchEntity.setScheduler(new ISLIPScheduler(sources.size(), destinations.size())); // to do
                break;
            default:
                sources = new ArrayList<>();
                sources.addAll((Collection<? extends IPacketSource>) options.get("sources"));
                inputQueuesSizes = ((ArrayList<Integer>) options.get("inputQueuesSizes")).stream().mapToInt(i -> i).toArray();
                outputQueuesSizes = ((ArrayList<Integer>) options.get("outputQueuesSizes")).stream().mapToInt(i -> i).toArray();
                int[][] multiDimensionalQueuesSizes = new int[inputQueuesSizes.length][outputQueuesSizes.length];
                for (int i = 0; i < inputQueuesSizes.length; i++) {
                    multiDimensionalQueuesSizes[i] = outputQueuesSizes;
                }
                destinations = new ArrayList<>();
                destinations.addAll((Collection<? extends APacketDestination>) options.get("destinations"));
                this.switchEntity = new Switch(cellSize, sources, multiDimensionalQueuesSizes, destinations);
                this.switchEntity.setScheduler(new ISLIPScheduler(sources.size(), destinations.size())); // to do
                break;
        }
    }

    public void handle(ActionEvent actionEvent) {
        // recalculate packets
        switchEntity.step();
        handler.updateUI(); // update UI
    }

    public boolean isRunning() {
        return timeline.getCurrentRate() != 0;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void start() {
        timeline.play();
    }

    public void pause() {
        timeline.pause();
    }

    public void stop() {
        timeline.stop();
    }

    public Timeline getTimeline() {
        return this.timeline;
    }
}
