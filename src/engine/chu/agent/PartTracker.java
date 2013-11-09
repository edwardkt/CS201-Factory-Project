package engine.chu.agent;

import engine.agent.Part;
import engine.chu.agent.ConveyorAgent.PartStatus;



public class PartTracker{
	Part part;
	PartStatus status;
	
	public PartTracker(Part part){
		this.part = part;
		status = PartStatus.notOnConveyor;
	}
	
}