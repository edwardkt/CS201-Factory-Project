package engine.Sum.interfaces;

import engine.agent.Part;
import engine.interfaces.Robot;



public interface ConveyorFamily {
	
	void msgConveyorReady( ConveyorFamily cf );
	
	void msgConveyorPartReceived( ConveyorFamily cf );
	
	void msgHereIsNewPart( ConveyorFamily cf, Part part );
	
	void msgRobotReady( Robot robot );
	
	void msgRobotPartReceived( Robot robot );
	
	void msgPartDone( Robot robot );
	
	void msgHereIsPopUpPart( Part part );
}
