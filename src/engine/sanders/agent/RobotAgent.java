package engine.sanders.agent;

import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import engine.agent.Agent;
import engine.agent.Part;
import engine.interfaces.*;

/**
 * This class controls the robot/machine that is an offline
 * workstation
 * 
 * @author Justin
 *
 */
public class RobotAgent extends Agent implements Robot {
	private ConveyorFamily myConveyorFamily;
	private Part currentPart;
	private boolean popUpIsUp = false;
	private TChannel workstation;
	private RobotState myState;
	private RobotPartState myPartState;
	private Integer myIndex;
	private boolean isBroken;
	
	
	private enum RobotState { Idle, Loading, MachiningPart, Releasing };
	private enum RobotPartState { Empty, HasUnmachinedPart, HasMachinedPart, isBroken };
	
	/**
	 * Constructor for RobotAgent
	 * 
	 * @param cf
	 * 			this is the conveyor family that the robot is assigned to,
	 * 			the one it will receive and give parts to
	 * @param transducer
	 * 			transducer that allows communication with the GUI
	 * @param workstation
	 * 			the channel that the robot should listen on
	 * @param index
	 * 			the index of this robot, either 0 or 1
	 * @param name
	 * 			the name of this agent
	 */
	public RobotAgent( ConveyorFamily cf, Transducer transducer, TChannel workstation, Integer index, String name ) {
		super( name, transducer );
		myConveyorFamily = cf;
		this.workstation = workstation;
		myIndex = index;
		myState = RobotState.Idle;
		myPartState = RobotPartState.Empty;
		informRobotReadyForPart();
		
		transducer.register( this,  workstation );
		
	}
	
	
	//***************************MESSAGES*********************************//
	/**
	 * instructs a robot to machine a part
	 * 
	 * @param part
	 * 			this is the part that the robot must machine
	 */
	public void msgMachinePart(Part part) {
		currentPart = part;
		myState = RobotState.Loading;
		stateChanged();
	}

	/**
	 * informs the robot that the pop is up and waiting for
	 * it to offload its part
	 * 
	 */
	public void msgPopUpUp() {
		popUpIsUp = true;
		stateChanged();
	}
	
	//************************SCHEDULER***********************************//
	
	public boolean pickAndExecuteAnAction() {
		if( isBroken ) {
			return false;
		}
		
		else if( myState == RobotState.Idle ) {
			if( myPartState == RobotPartState.HasUnmachinedPart ) {
				machinePart();
			}
			else if( myPartState == RobotPartState.HasMachinedPart ) {
				if( popUpIsUp ) {
					releasePart();
				}
				else
					informPartDone();
			}
		}
		
		return false;
	}
	
	
	//**************************ACTIONS***********************************//
	
	private void informRobotReadyForPart() {
		myConveyorFamily.msgRobotReady( this, myIndex );
	}
	
	private void informIReceivedPart() {
		myConveyorFamily.msgRobotPartReceived( this );
	}
	
	private void informPartDone() {
		myConveyorFamily.msgPartDone( this );
	}
	
	private void releasePart() {
		myConveyorFamily.msgHereIsPopUpPart( this , currentPart );
		myState = RobotState.Releasing;
		Object[] args = new Object[1];
		args[0] = myIndex;
		transducer.fireEvent( workstation,  TEvent.WORKSTATION_RELEASE_GLASS, args);
	}
	
	private void machinePart() {
		myState = RobotState.MachiningPart;
		Object[] args = new Object[1];
		args[0] = myIndex;
		transducer.fireEvent( workstation, TEvent.WORKSTATION_DO_ACTION, args);
	}
	
	
	//***********************TRANSDUCER EVENTS****************************//
	/**
	 * This method is called by the transducer in order for the front end
	 * to communicate with the agent
	 */
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		
		if( channel == workstation && ( (Integer)args[0] ).equals( myIndex ) ) {
			if( event == TEvent.WORKSTATION_BROKEN ) {
				isBroken = true;
				System.err.println( getName() + " is broken" );
			}
			else if( event == TEvent.WORKSTATION_FIXED ) {
				isBroken = false;
				System.out.println( getName() + " is fixed" );
				if( myPartState == RobotPartState.Empty )
					myConveyorFamily.msgRobotReady( this, myIndex );
				else
					stateChanged();
			}
			else if( event == TEvent.WORKSTATION_BREAK_GLASS ) {
				myPartState = RobotPartState.isBroken;
				System.err.println( "Glass in " + getName() + " is broken" );
			}
			else if( event == TEvent.WORKSTATION_BROKEN_GLASS_REMOVED ) {
				myPartState = RobotPartState.Empty;
				System.out.println( "Broken glass in " + getName() + " is removed and workstation is ready" );
				myConveyorFamily.msgRobotReady( this, myIndex );
			}
			
			else if( event == TEvent.WORKSTATION_LOAD_FINISHED ) {
				myState = RobotState.Idle;
				myPartState = RobotPartState.HasUnmachinedPart;
				informIReceivedPart();
				stateChanged();
			}
			
			else if( event == TEvent.WORKSTATION_GUI_ACTION_FINISHED ) {
				myState = RobotState.Idle;
				myPartState = RobotPartState.HasMachinedPart;
				stateChanged();
			}
			
			else if( event == TEvent.WORKSTATION_RELEASE_FINISHED ) {
				myState = RobotState.Idle;
				myPartState = RobotPartState.Empty;
				currentPart = null;
				popUpIsUp = false;
				informRobotReadyForPart();
				stateChanged();
			}
		}
	}
}


