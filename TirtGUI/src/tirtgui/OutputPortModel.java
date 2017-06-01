/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tirtgui;

import destinations.ProbabilisticPacketDestination;
import hardware.APacketDestination;

/**
 *
 * @author sebastiankotarski
 */
public class OutputPortModel {

    int capacity;
    double probabilityOfBusiness;

    public OutputPortModel(int capacity, double probabilityOfBusiness) {
        this.capacity = capacity;
        this.probabilityOfBusiness = probabilityOfBusiness;
    }

    public APacketDestination makeDestinationPacket() {
        return new ProbabilisticPacketDestination(capacity, probabilityOfBusiness);
    }
}
