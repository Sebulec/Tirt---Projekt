package hardware;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class InputPort
{
	private final int id;
	private Switch _switch;
	private List<OutputPort> outputPorts;
	private IPacketSource source;
	private final InputPortType type;
	private List<PacketQueue> queues;
	public int connectedTo;
	
	private InputPort(int id, Switch _switch, List<OutputPort> outputPorts, IPacketSource source, InputPortType type)
	{
		this.id = id;
		this._switch = _switch;
		this.outputPorts = outputPorts;
		this.source = source;
		this.type = type;
		
		queues = new LinkedList<PacketQueue>();
		connectedTo = -1;
	}
	
	public InputPort(int id, Switch _switch, List<OutputPort> outputPorts, IPacketSource source)
	{
		this(id, _switch, outputPorts, source, InputPortType.NoQueueing);
		
		queues.add(new PacketQueue(0));
	}
	
	public InputPort(int id, Switch _switch, List<OutputPort> outputPorts, IPacketSource source, int queueSize)
	{
		this(id, _switch, outputPorts, source, InputPortType.Queueing);
		
		queues.add(new PacketQueue(queueSize));
	}
	
	public InputPort(int id, Switch _switch, List<OutputPort> outputPorts, IPacketSource source, int[] queuesSizes)
	{
		this(id, _switch, outputPorts, source, InputPortType.VirtualOutputQueueing);
		
		for(int size : queuesSizes)
		{
			queues.add(new PacketQueue(size));
		}
	}
	
	public int getId()
	{
		return id;
	}
	
	public boolean connect(int outId)
	{
		if(connectedTo == -1)
		{
			connectedTo = outId;
			return true;
		}
		
		return false;
	}
	
	public int connectedOutput()
	{
		return connectedTo;
	}
	
	public Set<Integer> waitsForOutputs()
	{
		Set<Integer> result = new HashSet<Integer>();
		
		if(type == InputPortType.VirtualOutputQueueing)
		{
			for(int i = 0; i < queues.size(); i++)
			{
				if(queues.get(i).getHead() != null)
				{
					result.add(i);
				}
			}
		}
		else
		{
			Packet head = queues.get(0).getHead();
			
			if(head != null)
			{
				result.add(head.outputId);
			}
		}
		
		return result;
	}
	
	public void receivePacket()
	{
		Packet packet = source.getPacket(id, outputPorts.size());
		
		if(packet != null)
		{
			packet.inTimeStamp = _switch.getTime();
			
			PacketQueue queue = queues.get((type == InputPortType.VirtualOutputQueueing) ? packet.outputId : 0);
			
			if(queue.canEnqueue(packet))
			{
				queue.enqueue(packet);
			}
			else
			{
				packet.outTimeStamp = _switch.getTime();
				_switch.getRejectedPackets().get(packet.outputId).add(packet);
			}
		}
	}
	
	public boolean sendPacketFragment()
	{
		if(connectedTo != -1)
		{
			PacketQueue queue = queues.get((type == InputPortType.VirtualOutputQueueing) ? connectedTo : 0);
			OutputPort output = outputPorts.get(connectedTo);
			
			Packet queueHead = queue.getHead();
			
			if(queueHead != null)
			{
				int transferredBytes = output.transferStep(queueHead, Math.min(queue.getRemainingHeadBytes(), _switch.getCellSize()));
				boolean transferSucceded = queue.dequeueBytes(transferredBytes) > 0;
				
				if(queue.getHead() != queueHead)
				{
					connectedTo = -1;
				}
				
				return transferSucceded;
			}
		}
		
		return false;
	}

}
