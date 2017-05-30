/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tirtgui;

import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.animation.KeyFrame;

/**
 *
 * @author sebastiankotarski
 */
public class SwitchLoop {

    private static Duration PROBE_FREQUENCY;
    private final Timeline timeline;
    public Handler handler;

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

    public void handle(ActionEvent actionEvent) {
        // recalculate packets
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
