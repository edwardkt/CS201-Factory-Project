package engine.chu.interfaces;

import engine.agent.Part;



public interface PopUp {

	public abstract void msgIsThereSpace();
	
	public abstract void msgPartBeginningConveyor(Part part);
	
	public abstract void msgHereIsPart(Part part);
	
	public abstract void msgPartDone(Robot robot);
	
	public abstract void msgConveyorReady();
	
	public abstract void msgRobotReady(Robot robot);
	
	public abstract void msgRobotPartReceived(Robot robot);
	
	public abstract void msgHereIsPopUpPart(Part p);
}
