package hardware;

import java.util.List;

public class OutputPort
{
	private final int id;
	private Switch _switch;
	private APacketDestination destination;
	private Packet beingReceived;
	private int bytesLeft;
	private PacketQueue queue;
	public int connectedTo;
	
	public OutputPort(int id, Switch _switch, APacketDestination destination, int queueSize)
	{
		this.id = id;
		this._switch = _switch;
		this.destination = destination;
		beingReceived = null;
		bytesLeft = 0;
		queue = new PacketQueue(queueSize);
		connectedTo = -1;
	}
	
	public OutputPort(int id, Switch _switch, APacketDestination destination)
	{
		this(id, _switch, destination, 0);
	}
	
	public int getId()
	{
		return id;
	}
	
	public List<Packet> getSentPackets()
	{
		return destination.getReceivedPackets();
	}
	
	public boolean connect(int inId)
	{
		if(connectedTo == -1)
		{
			connectedTo = inId;
			return true;
		}
		
		return false;
	}
	
	public int connectedInput()
	{
		return connectedTo;
	}
	
	public int transferStep(Packet packet, int bytes)
	{
		sendPacketFragment();
		
		if(packet.inputId == connectedTo)
		{
			int receivedBytes = receivePacket(packet, bytes);
			
			if(beingReceived == null)
			{
				connectedTo = -1;
			}
			
			return receivedBytes;
		}
		
		return 0;
	}
	
	private int receivePacket(Packet packet, int bytes)
	{
		if(beingReceived == null)
		{
			if(queue.canEnqueue(packet))
			{
				beingReceived = packet;
				bytesLeft = beingReceived.size;
			}
			else
			{
				return 0;
			}
		}
		else if(packet != beingReceived)
		{
			return 0;
		}
		
		bytesLeft -= bytes;
		
		if(bytesLeft == 0)
		{
			queue.enqueue(packet);
			beingReceived = null;
		}
		
		return bytes;
	}
	
	private boolean sendPacketFragment()
	{
		Packet queueHead = queue.getHead();
		
		if(queueHead != null)
		{
			int transferredBytes = destination.transferBytes(queueHead, Math.min(queue.getRemainingHeadBytes(), destination.getCapacity()));
			boolean transferSucceded = queue.dequeueBytes(transferredBytes) > 0;
			
			if(queue.getHead() != queueHead)
			{
				queueHead.outTimeStamp = _switch.getTime();
			}
			
			return transferSucceded;
		}
		
		return false;
	}
	
}
