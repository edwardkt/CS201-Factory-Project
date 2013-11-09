package engine.chu.interfaces;

import engine.agent.Part;
import engine.interfaces.ConveyorFamily;



public interface Machine {
	
	public abstract void msgHereIsPart( Part part );
	
	public abstract void sendPart();
	
	public abstract void setConveyor(Conveyor conveyor);
	
	public abstract void msgConveyorPartReceived(ConveyorFamily nextcf);

}
