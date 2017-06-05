package hardware;

public class Packet {

    public final int inputId;
    public int inTimeStamp;
    public final int outputId;
    public int outTimeStamp;
    public final int size;

    public Packet(int inId, int outId, int size) {
        inputId = inId;
        inTimeStamp = -1;
        outputId = outId;
        outTimeStamp = -1;
        this.size = size;
    }

    public int getDelay() {
        return outTimeStamp - inTimeStamp;
    }

    public int getInputId() {
        return inputId;
    }

    public int getOutputId() {
        return outputId;
    }

    public int getSize() {
        return size;
    }

}
