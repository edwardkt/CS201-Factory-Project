package engine.tam.interfaces;

import engine.tam.ConveyorFamily.ConveyorAgent;
import engine.agent.Part;

public interface WorkStation
{
	public abstract void msgHereIsPart(Part p);
	
	public abstract void PartOnMachine(); 
	
	public abstract void ProcessingPart();
	
	public abstract void PreparingToload();
	
	public abstract void PartSentToNextConveyor();
	
	public abstract void StallMachine();
	
	public abstract boolean pickAndExecuteAnAction();
	
	public abstract String getName();

}