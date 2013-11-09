package engine.chu.interfaces;

import engine.agent.Part;



public interface Robot {

	void msgMachinePart( Part part );
	
	void msgPopUpUp();

	Part getPart();
}
