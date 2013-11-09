package engine.sanders.interfaces;

import transducer.TChannel;
import transducer.TEvent;
import engine.agent.*;

public interface Conveyor {

	
	void msgHereIsNewPart( Part part );
	
	void msgPopUpUp( boolean isUp );
	
	void msgRobotAvailable( boolean robotAvailable );
	
	void msgPopUpHasPart( boolean hasPart );
	
	void msgPopUpPartReceived( Part part );
	
	void eventFired( TChannel channel, TEvent event, Object[] args );

}
