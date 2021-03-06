package engine.Sum.interfaces;

import engine.agent.Part;
import engine.interfaces.Robot;



public interface FrontSensor {
	public abstract void msgConveyorReady(ConveyorFamily cf);
	
	public abstract void msgConveyorNotReady(ConveyorFamily cf);

	public abstract void msgConveyorPartReceived(ConveyorFamily cf);

	public abstract void msgHereIsNewPart(ConveyorFamily cf, Part part);

	public abstract void msgRobotReady(Robot robot);

	public abstract void msgRobotPartReceived(Robot robot);
		
	public abstract void msgPartDone(Robot robot);
		
	public abstract void msgHereIsPopUpPart(Part part);
}
