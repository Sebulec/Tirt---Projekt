package hardware;

import java.util.LinkedList;
import java.util.Queue;
import java.lang.Math;

public class PacketQueue
{
	private int maxCapacity;
	private int capacity;
	
	private Queue<Packet> packets;
	private Packet head;
	private int remainingHeadBytes;
	
	public PacketQueue(int maxCapacity)
	{
		this.maxCapacity = Math.max(maxCapacity, 0);
		capacity = this.maxCapacity;
		
		packets = new LinkedList<Packet>();
		head = null;
		remainingHeadBytes = 0;
	}
	
	public int getMaxCapacity()
	{
		return maxCapacity;
	}
	
	public int setMaxCapacity(int c)
	{
		maxCapacity = Math.max(capacity, c);
		
		return maxCapacity;
	}
	
	public int getCapacity()
	{
		return capacity;
	}
	
	public boolean canEnqueue(Packet packet)
	{
		return (packet == null) ? false : ((maxCapacity == 0) ? head == null : packet.size <= capacity);
	}
	
	public boolean enqueue(Packet packet)
	{
		if(packet != null)
		{
			if(maxCapacity == 0)
			{
				if(head == null)
				{
					head = packet;
					remainingHeadBytes = packet.size;
					
					return true;
				}
			}
			else if(capacity >= packet.size)
			{
				if(head != null)
				{
					packets.offer(packet);
				}
				else
				{
					head = packet;
					remainingHeadBytes = packet.size;
				}
				
				capacity -= packet.size;
				
				return true;
			}
		}
		
		return false;
	}
	
	public Packet getHead()
	{
		return head;
	}
	
	public int getRemainingHeadBytes()
	{
		return remainingHeadBytes;
	}
	
	public int dequeueBytes(int bytes)
	{
		if(head == null)
		{
			return 0;
		}
		else if(bytes < remainingHeadBytes)
		{
			remainingHeadBytes -= bytes;
			capacity += (maxCapacity == 0) ? 0 : bytes;
			
			return bytes;
		}
		else
		{
			int removed = remainingHeadBytes;
			head = packets.poll();
			
			remainingHeadBytes = (head == null) ? 0 : head.size;
			capacity += (maxCapacity == 0) ? 0 : removed;
			
			return removed;
		}
	}
	
	public int packetsInQueue()
	{
		return packets.size() + ((head == null) ? 0 : 1);
	}
}
