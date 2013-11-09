package engine.chu.interfaces;

import engine.agent.Part;
import engine.chu.agent.ConveyorFamilyGroup;
import engine.interfaces.ConveyorFamily;



public interface Bin {

	public abstract void msgHereIsNewPart(Part part, int quantity);
	
	public abstract void msgBinConveyorReady();

	public abstract void setCF(ConveyorFamily cf);
	

}
