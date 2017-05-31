package hardware;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Switch
{
	private int time;
	private int cellSize;
	
	private List<InputPort> inputPorts;
	private List<OutputPort> outputPorts;
	
	private List<List<Packet>> lists;
	
	private IScheduler scheduler;
	
	private Switch(int cellSize)
	{
		time = 0;
		this.cellSize = cellSize;
		
		inputPorts = new ArrayList<InputPort>();
		outputPorts = new ArrayList<OutputPort>();
		lists = new ArrayList<List<Packet>>();
		
		scheduler = null;
	}
	
	public Switch(int cellSize, List<IPacketSource> sources, int[] inputQueuesSizes, List<APacketDestination> destinations)
	{
		this(cellSize);
		
		for(int i = 0; i < destinations.size(); i++)
		{
			outputPorts.add(new OutputPort(i, this, destinations.get(i)));
		}
		
		for(int i = 0; i < sources.size(); i++)
		{
			inputPorts.add(new InputPort(i, this, outputPorts, sources.get(i), inputQueuesSizes[i]));
		}
		
		createPacketLists();
	}
	
	public Switch(int cellSize, List<IPacketSource> sources, List<APacketDestination> destinations, int[] outputQueuesSizes)
	{
		this(cellSize);
		
		for(int i = 0; i < destinations.size(); i++)
		{
			outputPorts.add(new OutputPort(i, this, destinations.get(i), outputQueuesSizes[i]));
		}
		
		for(int i = 0; i < sources.size(); i++)
		{
			inputPorts.add(new InputPort(i, this, outputPorts, sources.get(i)));
		}
		
		createPacketLists();
	}
	
	public Switch(int cellSize, List<IPacketSource> sources, int[][] inputQueuesSizes, List<APacketDestination> destinations)
	{
		this(cellSize);
		
		for(int i = 0; i < destinations.size(); i++)
		{
			outputPorts.add(new OutputPort(i, this, destinations.get(i)));
		}
		
		for(int i = 0; i < sources.size(); i++)
		{
			inputPorts.add(new InputPort(i, this, outputPorts, sources.get(i), inputQueuesSizes[i]));
		}
		
		createPacketLists();
	}
	
	private void createPacketLists()
	{
		for(int i = 0; i < outputPorts.size(); i++)
		{
			lists.add(new LinkedList<Packet>());
		}
	}
	
	public void setScheduler(IScheduler scheduler)
	{
		this.scheduler = scheduler;
	}
	
	public void step()
	{
		Set<Integer> disconnectedInputs = getDisconnectedInputs();
		Set<Integer> servedInputs = new HashSet<Integer>();
		
		for(int i = 0; i < inputPorts.size(); i++)
		{
			if(!disconnectedInputs.contains(i))
			{
				inputPorts.get(i).sendPacketFragment();
				servedInputs.add(i);
			}
		}
		
		if(scheduler != null)
		{
			connect(scheduler.defineConnections(this));
		}
		
		Set<Integer> excludedInputs = getDisconnectedInputs();
		excludedInputs.addAll(servedInputs);
		
		for(int i = 0; i < inputPorts.size(); i++)
		{
			InputPort inputPort = inputPorts.get(i);
			
			inputPort.receivePacket();
			
			if(!excludedInputs.contains(i))
			{
				inputPort.sendPacketFragment();
			}
		}
		
		time++;
	}
	
	public List<List<Packet>> getRejectedPackets()
	{
		return lists;
	}
	
	public List<List<Packet>> getReceivedPackets()
	{
		List<List<Packet>> result = new ArrayList<List<Packet>>();
		
		for(OutputPort outputPort : outputPorts)
		{
			result.add(outputPort.getSentPackets());
		}
		
		return result;
	}
	
	public Set<Integer> getDisconnectedInputs()
	{
		Set<Integer> result = new HashSet<Integer>();
		
		for(InputPort port : inputPorts)
		{
			if(port.connectedOutput() == -1)
			{
				result.add(port.getId());
			}
		}
		
		return result;
	}
	
	public Set<Integer> getDisconnectedOutputs()
	{
		Set<Integer> result = new HashSet<Integer>();
		
		for(OutputPort port : outputPorts)
		{
			if(port.connectedInput() == -1)
			{
				result.add(port.getId());
			}
		}
		
		return result;
	}
	
	public Set<Integer> inputWaitsForOutputs(int inId)
	{
		return inputPorts.get(inId).waitsForOutputs();
	}
	
	public int getTime()
	{
		return time;
	}
	
	public int getCellSize()
	{
		return cellSize;
	}
	
	public void setCellSize(int cellSize)
	{
		this.cellSize = cellSize;
	}
	
	private void connect(Map<Integer, Integer> connections)
	{
		if(connections != null)
		{
			for(int i = 0; i < inputPorts.size(); i++)
			{
				Integer outId = connections.get(i);
				
				if(outId != null)
				{
					inputPorts.get(i).connect(outId);
					outputPorts.get(outId).connect(i);
				}
			}
		}
	}
}
