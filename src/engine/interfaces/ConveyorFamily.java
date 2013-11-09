package engine.interfaces;

import engine.agent.*;

public interface ConveyorFamily {
	
	
	void msgConveyorReady( ConveyorFamily cf );
	
	void msgConveyorPartReceived( ConveyorFamily cf );
	
	void msgHereIsNewPart( ConveyorFamily cf, Part part );
	
	void msgRobotReady( Robot robot );
	
	void msgRobotReady(Robot robot, Integer index );
	
	void msgRobotPartReceived( Robot robot );
	
	void msgPartDone( Robot robot );
	
	void msgHereIsPopUpPart( Robot robot, Part part );

	void msgIReceivedPart();

	void msgPopUpUp(Robot robot);

	void msgMachinePart(Robot robot, Part part);

	void msgHereIsPartFromPopUp(Part part);

	void msgMachineReady();

	void msgBinConveyorReady();

	void msgBinConveyorStopping();

	void msgBinHereIsNewPart(Part part);
}
