package sources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hardware.IScheduler;
import hardware.Switch;

public class ISLIPScheduler implements IScheduler 
{
	private final int inputsCount;
	private final int outputsCount;
	
	private int inputRRs[];
	private int outputRRs[];
	
	public ISLIPScheduler(int inputsCount, int outputsCount)
	{
		this.inputsCount = inputsCount;
		this.outputsCount = outputsCount;
		
		inputRRs = new int[this.inputsCount];
		
		for(int i = 0; i < this.inputsCount; i++)
		{
			inputRRs[i] = 0;
		}
		
		outputRRs = new int[this.outputsCount];
		
		for(int i = 0; i < this.outputsCount; i++)
		{
			outputRRs[i] = 0;
		}
	}
	
	@Override
	public Map<Integer, Integer> defineConnections(Switch _switch) 
	{
		Map<Integer, Integer> connections = new HashMap<Integer, Integer>();
		
		Set<Integer> disconnectedInputs = _switch.getDisconnectedInputs();
		Set<Integer> disconnectedOutputs = _switch.getDisconnectedOutputs();
		
		for(int i = 0; i < outputsCount && !disconnectedOutputs.isEmpty(); i++)
		{
			List<Set<Integer>> requests = createIntegerSetList(outputsCount);
			
			for(int inId : disconnectedInputs)
			{
				for(int outId : _switch.inputWaitsForOutputs(inId))
				{
					requests.get(outId).add(inId);
				}
			}
			
			List<Set<Integer>> grants = createIntegerSetList(inputsCount);
			
			for(int outId : disconnectedOutputs)
			{
				Integer grantedInput = checkRoundRobin(requests.get(outId), outputRRs[outId], inputsCount);
				
				if(grantedInput != null)
				{
					grants.get(grantedInput).add(outId);
				}
			}
			
			Set<Integer> grantedOutputs = new HashSet<Integer>();
			Set<Integer> grantedInputs = new HashSet<Integer>();
			
			for(int inId : disconnectedInputs)
			{
				Integer grantedOutput = checkRoundRobin(grants.get(inId), inputRRs[inId], outputsCount);
				
				if(grantedOutput != null)
				{
					grantedInputs.add(inId);
					grantedOutputs.add(grantedOutput);
					
					inputRRs[inId] = (grantedOutput + 1) % outputsCount;
					outputRRs[grantedOutput] = (inId + 1) % inputsCount;
					
					connections.put(inId, grantedOutput);
				}
			}
			
			disconnectedInputs.removeAll(grantedInputs);
			disconnectedOutputs.removeAll(grantedOutputs);
		}
		
		return connections;
	}
	
	private static Integer checkRoundRobin(Set<Integer> candidates, int rrPointer, int rrSize)
	{
		Integer winner = null;
		int priority = rrSize;
		
		for(int candidate : candidates)
		{
			int candidatePriority = calculatePriority(candidate, rrPointer, rrSize);
			
			if(candidatePriority < priority)
			{
				winner = candidate;
				priority = candidatePriority;
			}
		}
		
		return winner;
	}
	
	private static int calculatePriority(int value, int rrPointer, int rrSize)
	{
		return (value - rrPointer + rrSize) % rrSize;
	}
	
	private static List<Set<Integer>> createIntegerSetList(int size)
	{
		List<Set<Integer>> result = new ArrayList<Set<Integer>>();
		
		for(int i = 0; i < size; i++)
		{
			result.add(new HashSet<Integer>());
		}
		
		return result;
	}

}
