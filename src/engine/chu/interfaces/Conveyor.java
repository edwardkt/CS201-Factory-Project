package engine.chu.interfaces;

import engine.agent.Part;



public interface Conveyor {

	public abstract void msgPartBeginningConveyor(Part part);
	
	public abstract void msgMachineReady();
	
	public abstract void msgMachineNotReady();
	
	public abstract void msgPopUpReady();

	public abstract void msgPopUpNotReady();

	public abstract void msgPartEndingConveyor();
	
	public abstract void msgMachineReceivedPart();
	
}
