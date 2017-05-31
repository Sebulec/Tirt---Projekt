package hardware;

public interface IPacketSource
{
	public Packet getPacket(int inId, int destinationsCount);
}
