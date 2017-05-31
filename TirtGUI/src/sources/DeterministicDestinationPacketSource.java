package sources;

import java.util.Random;

import hardware.IPacketSource;
import hardware.Packet;

public class DeterministicDestinationPacketSource implements IPacketSource
{
	private double p;
	private int sizeMin;
	private int sizeDelta;
	private int outId;
	
	private Random randGen;
	
	public DeterministicDestinationPacketSource(double probabilityOfPacketArrival, int packetMinimalSize, int packetMaximalSize, int outId)
	{
		p = probabilityOfPacketArrival;
		sizeMin = packetMinimalSize;
		sizeDelta = packetMaximalSize - sizeMin + 1;
		this.outId = outId;
		
		randGen = new Random();
	}
	
	@Override
	public Packet getPacket(int inId, int destinationsCount)
	{
		return (Math.random() < p) ? new Packet(inId, outId, sizeMin + randGen.nextInt(sizeDelta)) : null;
	}
}
