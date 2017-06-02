/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tirtgui;

import hardware.IPacketSource;
import sources.DeterministicDestinationPacketSource;
import sources.RandomPacketSource;

/**
 *
 * @author sebastiankotarski
 */
public class InputPortModel {

    double probabilityOfPacketArrival;
    int packetMinimalSize;
    int packetMaximalSize;
    int outId;

    int queueSize;

    public InputPortModel(double probabilityOfPacketArrival, int packetMinimalSize, int packetMaximalSize, int outId, int queueSize) {
        this.probabilityOfPacketArrival = probabilityOfPacketArrival;
        this.packetMinimalSize = packetMinimalSize;
        this.packetMaximalSize = packetMaximalSize;
        this.outId = outId;
        this.queueSize = queueSize;
    }

    public IPacketSource makePacketSource() {
        if (outId == -1) {
            return new RandomPacketSource(probabilityOfPacketArrival, packetMinimalSize, packetMaximalSize, outId);
        } else {
            return new DeterministicDestinationPacketSource(probabilityOfPacketArrival, packetMinimalSize, packetMaximalSize, outId);
        }
    }

    public int getQueueSize() {
        return queueSize;
    }
}
