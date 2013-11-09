package engine.Fuks.interfaces;

import transducer.TChannel;
import transducer.TEvent;
import engine.agent.Part;
import engine.interfaces.ConveyorFamily;



public interface Conveyor {

	



	public abstract void iAmReady();

	

	public abstract void setConveyorFamily(ConveyorFamily conveyorFamily);

	public abstract void setMyConveyorFamily(ConveyorFamily conveyorFamily);

	public abstract void startThread();

	public abstract void stopThread();
	
	

	public abstract void eventFired(TChannel channel, TEvent event,
			Object[] args);

	public abstract void setRightMachine(Machine myMachine);
	public abstract void hereIsNewPart(Part part);



	public abstract void setFrontCF(ConveyorFamily frontCF);




	public abstract void unexpectedStopConveyor();



	public abstract void conveyorPartReceived(ConveyorFamily cf);



	



	public abstract void setPrintEnabled();



	public abstract void msgConveyorStopping();



	public abstract void iAmBroken();



	



}
