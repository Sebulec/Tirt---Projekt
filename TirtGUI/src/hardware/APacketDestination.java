package hardware;

import java.util.LinkedList;
import java.util.List;

public abstract class APacketDestination
{
	protected int capacity;
	protected List<Packet> receivedPackets;
	
	protected Packet beingReceived;
	protected int bytesLeft;
	
	public APacketDestination(int capacity)
	{
		this.capacity = capacity;
		receivedPackets = new LinkedList<Packet>();
		beingReceived = null;
		bytesLeft = 0;
	}
	
	public int getCapacity()
	{
		return capacity;
	}
	
	public List<Packet> getReceivedPackets()
	{
		return receivedPackets;
	}
	
	public int transferBytes(Packet packet, int bytes)
	{
		if(isBusy())
		{
			return 0;
		}
		else
		{
			if(beingReceived == null)
			{
				beingReceived = packet;
				bytesLeft = beingReceived.size;
			}
			else if(packet != beingReceived)
			{
				return 0;
			}
			
			bytesLeft -= bytes;
			
			if(bytesLeft == 0)
			{
				receivedPackets.add(beingReceived);
				beingReceived = null;
			}
			
			return bytes;
		}
	}
	
	protected abstract boolean isBusy();
}
