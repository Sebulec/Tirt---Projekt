package sources;

import java.util.Random;

import hardware.IPacketSource;
import hardware.Packet;

public class RandomPacketSource implements IPacketSource
{
	private double p;
	private int sizeMin;
	private int sizeDelta;
	
	private Random randGen;
	
	public RandomPacketSource(double probabilityOfPacketArrival, int packetMinimalSize, int packetMaximalSize, int outId)
	{
		p = probabilityOfPacketArrival;
		sizeMin = packetMinimalSize;
		sizeDelta = packetMaximalSize - sizeMin + 1;
		
		randGen = new Random();
	}
	
	@Override
	public Packet getPacket(int inId, int destinationsCount)
	{
		return (Math.random() < p) ? new Packet(inId, randGen.nextInt(destinationsCount), sizeMin + randGen.nextInt(sizeDelta)) : null;
	}

}
