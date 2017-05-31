package hardware;

import java.util.Map;

public interface IScheduler
{
	public Map<Integer, Integer> defineConnections(Switch _switch);
}
