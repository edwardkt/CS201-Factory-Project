package engine.sanders.agent;

import transducer.TChannel;
import transducer.Transducer;
import engine.agent.Part;
import engine.interfaces.*;

/**
 * This class is an interface between a conveyor family which contains
 * 1 conveyor and 1 pop up and the neighboring conveyor families and
 * robots
 * 
 * @author Justin
 *
 */
public class ConveyorFamilyContainer implements ConveyorFamily {
	
	private PopUpAgent popUp;
	private ConveyorAgent conveyor;
	private ConveyorFamily cfLead;
	private ConveyorFamily cfFollow;
	
	public ConveyorFamilyContainer(){}
	
	/**
	 * This is a constructor for ConveyorFamilyContainer
	 *
	 * @param transducer
	 * 				the transducer used to communication with the GUI
	 * @param cfLead
	 * 				the ConveyorFamilyContainer that is before this one
	 * @param popUpIndex
	 * 				the index of the pop up within this conveyor family
	 * @param conveyorIndex
	 * 				the index of the conveyor within this family
	 * @param recipe
	 * 				the recipe associated with this conveyor family that
	 * 				tells it if it needs to machine a part or not
	 * @param workstation
	 * 				this is the workstation that the agents within this 
	 * 				conveyor family belong to
	 */
	public ConveyorFamilyContainer( Transducer transducer, ConveyorFamily cfLead, Integer popUpIndex, Integer conveyorIndex, Integer workstationIndex, TChannel workstation ) {
		popUp = new PopUpAgent( this, transducer, popUpIndex, conveyorIndex, workstationIndex, workstation, "PopUp" + popUpIndex );
		conveyor = new ConveyorAgent( this, transducer, popUp, conveyorIndex, workstationIndex, "Conveyor" + conveyorIndex );
		popUp.setConveyor( conveyor );
		this.cfLead = cfLead;
		
		popUp.startThread();
		conveyor.startThread();
		
		msgIAmReadyForPart();
	}
	
	/**
	 * Informs this conveyor family that the conveyor family after it
	 * is ready to take parts
	 */
	public void msgConveyorReady( ConveyorFamily cf ) {
		popUp.msgConveyorReady();
	}

	public void msgIAmReadyForPart() {
		cfLead.msgConveyorReady( this );
	}

	/**
	 * Informs this conveyor family that the conveyor family after it
	 * successfully received a part that was passed to it
	 */
	public void msgConveyorPartReceived( ConveyorFamily cf) {
		popUp.msgPartReceived();
	}

	/**
	 * Informs this conveyor family that a robot at its
	 * workstation is ready to machine a part
	 */
	public void msgRobotReady(Robot robot) {
		popUp.msgRobotReady( robot, null );
	}
	
	/**
	 * Informs this conveyor family that a robot at its
	 * workstation is ready to machine a part
	 */
	public void msgRobotReady(Robot robot, Integer index ) {
		popUp.msgRobotReady( robot, index );
	}
	
	/**
	 * Message sent from a pop up informing its container class to
	 * have a robot machine a part
	 */
	public void msgMachinePart( Robot robot, Part part ) {
		robot.msgMachinePart( part );
	}

	/**
	 * Informs this conveyor family that a robot successfully
	 * received a part
	 */
	public void msgRobotPartReceived(Robot robot) {
		popUp.msgRobotPartReceived( robot );
	}

	/**
	 * A robot calls this method to inform the conveyor family
	 * that is has finished machining a part
	 */
	public void msgPartDone(Robot robot) {
		popUp.msgPartDone( robot );
	}
	
	/**
	 * Pop up tells the conveyor family container that it is up
	 * so that it can tell a robot that has a machined part
	 */
	public void msgPopUpUp( Robot robot ) {
		robot.msgPopUpUp();
	}

	/**
	 * A part being given to this conveyor family from the conveyor
	 * family before it
	 */
	public void msgHereIsNewPart( ConveyorFamily cf, Part part ) {
		conveyor.msgHereIsNewPart( part );
	}
	
	/**
	 * This is a popUp passing a part through the conveyor family interface
	 * to the next conveyor family
	 */
	public void msgHereIsPartFromPopUp( Part part ) {
		cfFollow.msgHereIsNewPart( this, part );
	}

	/**
	 * This is a robot giving a part to the pop up through the
	 * conveyor family interface
	 */
	public void msgHereIsPopUpPart(Robot robot, Part part) {
		popUp.msgHereIsPopUpPart( robot, part );
	}
	
	/**
	 * Tells the conveyor family before this one that the part it sent
	 * was successfully received
	 */
	public void msgIReceivedPart() {
		cfLead.msgConveyorPartReceived( this );
	}
	
	
	//************MISC****************
	/**
	 * this sets the conveyor family that comes after
	 * this one
	 * 	
	 * @param cfFollow
	 * 			the conveyor family that follows this one
	 */
	public void setCFFollow( ConveyorFamily cfFollow ) {
		this.cfFollow = cfFollow;
	}
		
	
		
	
	
	//unimplemented methods
	public void msgMachineReady() {}
	public void msgBinConveyorReady() {}
	public void msgBinConveyorStopping() {}
	public void msgBinHereIsNewPart(Part part) {}
	
}
