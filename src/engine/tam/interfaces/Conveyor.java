package engine.tam.interfaces;

import engine.agent.Part;
//import engine.tam.ConveyorFamily.*;

public interface Conveyor
{
	public abstract void msgHereIsNewPart(Part part);
	
	public abstract void msgPartReceived(Part p);
	
	public abstract void msgPartReceived();
	
	public abstract void msgPartReceivedFromPreviousCF();
	
	public abstract void msgImFree();
	
	public abstract void msgMachineFree();
	
	public abstract void leftSensorPushed();
	
	public abstract void rightSensorPushed();
	
	public abstract void leftSensorReleased();
	
	public abstract void rightSensorReleased();
	
	public abstract boolean pickAndExecuteAnAction();
	
    public abstract String getName(); 
    
    
}