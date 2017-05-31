package destinations;

import hardware.APacketDestination;

public class ProbabilisticPacketDestination extends APacketDestination
{
	private double p;
	
	public ProbabilisticPacketDestination(int capacity, double probabilityOfBusiness)
	{
		super(capacity);
		
		p = probabilityOfBusiness;
	}

	@Override
	protected boolean isBusy()
	{
		return Math.random() < p;
	}

}
