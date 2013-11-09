package engine.sanders.interfaces;

import transducer.TChannel;
import transducer.TEvent;
import engine.agent.Part;
import engine.interfaces.Robot;

public interface PopUp {

	
	void msgConveyorReady();
	
	void msgPartReceived();
	
	void msgRobotReady(Robot robot, Integer index);
	
	void msgRobotPartReceived( Robot robot );
	
	void msgPartDone( Robot robot );
	
	void msgHereIsPopUpPart( Robot robot, Part part );
	
	void msgHereIsPopUpPart( Conveyor conveyor, Part part );

	void eventFired( TChannel channel, TEvent event, Object[] args );

}
