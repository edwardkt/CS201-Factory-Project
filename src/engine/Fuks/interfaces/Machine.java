package engine.Fuks.interfaces;

import transducer.TChannel;
import transducer.TEvent;
import engine.agent.Part;
import engine.interfaces.ConveyorFamily;


public interface Machine {
	
	

	public abstract void setconveyor(Conveyor myConveyor);

	public abstract void setMyConveyorFamily(ConveyorFamily conveyorFamily2);

	public abstract void setRightConveyorFamily(ConveyorFamily rightCF);

	public abstract void startThread();

	public abstract void iAmReady();

	public abstract void conveyorPartReceived(ConveyorFamily cf);

	public abstract void stopThread();


	public abstract void machinePart(Part remove);

	public abstract void setPrintEnabled();

	
	
	}
