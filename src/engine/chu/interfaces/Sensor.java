package engine.chu.interfaces;

import engine.agent.Part;
import engine.interfaces.*;
public interface Sensor {
	//Conveyor (Transducer)
	public abstract void msgHereIsNewPart(ConveyorFamily cf, Part part);
	
}
