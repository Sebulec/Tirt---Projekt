package hardware;

public class Packet
{
	public final int inputId;
	public int inTimeStamp;
	public final int outputId;
	public int outTimeStamp;
	public final int size;
	
	public Packet(int inId, int outId, int size)
	{
		inputId = inId;
		inTimeStamp = -1;
		outputId = outId;
		outTimeStamp = -1;
		this.size = size;
	}
}
