package engine.sanders.agent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.Timer;

import transducer.*;
import engine.agent.Agent;
import engine.agent.Part;
import engine.interfaces.*;

/**
 * This is an agent class that controls the starting and stopping of
 * the last conveyor in the glassline factory.  It is specially designed
 * to control the truck
 * 
 * @author Justin
 *
 */
public class ConveyorAgentWithTruck extends Agent implements ConveyorFamily, TReceiver, ActionListener {
	private boolean followSensorDepressed = false;
	private boolean isRunning = true;
	private LinkedList<Part> parts;
	private ConveyorFamily cfLead;
	private Integer myIndex;
	private Transducer transducer;
	private MyState myState;
	private boolean conveyorBroken = false;
	private boolean sensorBroken = false;
	private boolean truckBroken = false;
	private boolean conveyorRestart = false;
	private Timer readyMessageTimer, startConveyorTimer;
	
	//different than parts.size()
	//used to track the spacing between 4-part groups
	private int partGroupCount = 0;
	private int partGroupEndOfConveyor = 0;
	private int partsLoaded = 0;
	final static int groupSize = 4;

	private enum MyState { Normal, DoneLoading, Unloading }
	
	/**
	 * Contstructor for SimpleConveyorAgent
	 * 
	 * @param cfLead
	 * 			conveyor family that comes before this one
	 * @param transducer
	 * 			transducer that allows communication with the GUI
	 * @param index
	 * 			the index of the conveyor within the factory
	 * @param name
	 * 			name of the conveyor
	 */
	public ConveyorAgentWithTruck( ConveyorFamily cfLead, Transducer transducer, Integer index, String name ) {
		super( name );
		this.cfLead = cfLead;
		parts = new LinkedList<Part>();
		this.transducer = transducer;
		myIndex = new Integer( index );
		myState = MyState.Normal;
		readyMessageTimer = new Timer( 200, this );
		startConveyorTimer = new Timer( 800, this );
		readyMessageTimer.setRepeats( false );
		startConveyorTimer.setRepeats( false );
		
		this.transducer.register( this, TChannel.SENSOR );
		this.transducer.register( this, TChannel.CONVEYOR );
		this.transducer.register( this, TChannel.TRUCK );
		
		Object[] args = new Object[1];
		args[0] = myIndex;
		
		this.transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args );
		
		cfLead.msgConveyorReady( this );
	}

	
	//****************MESSAGES***********************
	
	/**
	 * This message is gives the conveyor a new part
	 * 
	 * @param cf
	 * 			the conveyor family that sent the part
	 * @param part
	 * 			the part sent by the conveyor family
	 */
	public void msgHereIsNewPart(ConveyorFamily cf, Part part) {
		parts.add( part );
	}

	//*****************SCHEDULER********************//
	
	public boolean pickAndExecuteAnAction() {
		if( conveyorBroken || sensorBroken ) {
			stopConveyor();
			return false;
		}
		
		else if( partGroupCount < partGroupEndOfConveyor ) {
			System.err.println( getName() + " has an unexpected number of sensor events" );
			System.err.println( getName() + " is now stopping" );
			Object[] args = new Object[1];
			args[0] = myIndex * 2;
			transducer.fireEvent( TChannel.GUI, TEvent.SENSOR_BROKEN, args );
			sensorBroken = true;
			stopConveyor();
			return false;
		}
		
		else if( partsLoaded > partGroupEndOfConveyor ) {
			System.err.println( "Truck loaded untracked part\n" + getName() + " is now stopping" );
			Object[] args = new Object[1];
			args[0] = myIndex * 2 + 1;
			transducer.fireEvent( TChannel.GUI, TEvent.SENSOR_BROKEN, args );
			sensorBroken = true;
			stopConveyor();
			return false;
		}
		
		else if( !parts.isEmpty() ) {
			if( isRunning ) {
				if( followSensorDepressed && myState != MyState.Normal ) {
					stopConveyor();
				}
			}
			else if( myState == MyState.Normal || !followSensorDepressed ) {
				startConveyor();
			}
		}
		
		
		if ( myState == MyState.DoneLoading && !truckBroken )
			emptyTruck();
		
		return false;
	}
	
	//*************************ACTIONS**********************************
	
	private void stopConveyor() {
		if( isRunning ) {
			isRunning = false;
			if( !parts.isEmpty() ) {
				readyMessageTimer.stop();
				startConveyorTimer.stop();
			}
			
			Object[] args = new Object[1];
			args[0] = myIndex;
			transducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, args );
			
			stateChanged();
		}
	}
	
	private void startConveyor() {
		if( !isRunning ) {
			isRunning = true;
			startConveyorTimer.restart();
			conveyorRestart = true;
			
			Object[] args = new Object[1];
			args[0] = myIndex;
			transducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args );
			stateChanged();
		}
	}
	
	private void emptyTruck() {
		partsLoaded = 0;
		myState = MyState.Unloading;
		cfLead.msgConveyorReady( this );
		partGroupCount = partGroupEndOfConveyor = 0;
		transducer.fireEvent( TChannel.TRUCK, TEvent.TRUCK_DO_EMPTY, null );
	}
	
	//*********************TRANSDUCER EVENTS***************************
	/**
	 * This method is called by the transducer in order for the front end
	 * to communicate with the agent
	 */
	public void eventFired( TChannel channel, TEvent event, Object[] args ) {
		
		if( channel == TChannel.CONVEYOR && myIndex.equals((Integer)args[0]) ) {
			if( event == TEvent.CONVEYOR_BROKEN ) {
				conveyorBroken = true;
				System.err.println( getName() + " is broken" );
				stateChanged();
			}
			else if( event == TEvent.CONVEYOR_FIXED ) {
				conveyorBroken = false;
				System.out.println( getName() + " is fixed" );
				stateChanged();
			}
		}
		
		else if( channel == TChannel.SENSOR && ( myIndex.equals((Integer)args[0] / 2 ) ) ) {
			if( (Integer)args[0] % 2 == 0 ) {
				if( event == TEvent.SENSOR_FIXED ) {
					sensorBroken = false;
					cfLead.msgConveyorPartReceived(this);
					cfLead.msgConveyorReady(this);
					System.out.println( "Sensor " + (myIndex*2) + " is reported as fixed\n" + getName() + " is resuming normal operation" );
					partGroupCount++;
					stateChanged();
				}
				else if( event == TEvent.SENSOR_GUI_PRESSED ) {
					cfLead.msgConveyorPartReceived( this );
					stateChanged();
				}
				
				else if( event == TEvent.SENSOR_GUI_RELEASED) {
					if ( ++partGroupCount < groupSize ) {
						if( !conveyorRestart )
							readyMessageTimer.start();
					}
				}
				
			}
			else if ( (Integer)args[0] % 2 == 1 ) {
				if( event == TEvent.SENSOR_FIXED ) {
					sensorBroken = false;
					System.out.println( "Sensor " + (myIndex*2+1) + " is reported as fixed\n" + getName() + " is resuming normal operation" );
					partGroupEndOfConveyor++;
					stateChanged();
				}
				else if( event == TEvent.SENSOR_GUI_PRESSED ) {
					partGroupEndOfConveyor++;
					followSensorDepressed = true;
					stateChanged();
				}
				
				else if( event == TEvent.SENSOR_GUI_RELEASED ) {
					followSensorDepressed = false;
					stateChanged();
				}
			}
		}
		
		else if( channel == TChannel.TRUCK ) {
			if( event == TEvent.TRUCK_BROKEN ) {
				truckBroken = true;
				System.err.println( "Truck broken" );
				stateChanged();
			}
			else if( event == TEvent.TRUCK_FIXED ) {
				truckBroken = false;
				System.out.println( "Truck fixed" );
				stateChanged();
			}
			
			else if( event == TEvent.TRUCK_GUI_EMPTY_FINISHED ) {
				myState = MyState.Normal;
				stateChanged();
			}
			else if( event == TEvent.TRUCK_GUI_LOAD_FINISHED ) {
				parts.remove();
				if( ++partsLoaded == groupSize ) {
					myState = MyState.DoneLoading;
				}
				stateChanged();
			}
		}
	}

	//unimplemented messages
	public void msgConveyorReady(ConveyorFamily cf) {}
	public void msgConveyorPartReceived(ConveyorFamily cf) {}
	public void msgRobotReady(Robot robot) {}
	public void msgRobotReady(Robot robot, Integer index) {}
	public void msgRobotPartReceived(Robot robot) {}
	public void msgPartDone(Robot robot) {}
	public void msgHereIsPopUpPart(Robot robot, Part part) {}
	public void msgIReceivedPart() {}
	public void msgPopUpUp(Robot robot) {}
	public void msgMachinePart(Robot robot, Part part) {}
	public void msgHereIsPartFromPopUp(Part part) {}
	public void msgMachineReady() {}
	public void msgBinConveyorReady() {}
	public void msgBinConveyorStopping() {}
	public void msgBinHereIsNewPart(Part part) {}
	public void msgIAmReadyForPart() {}


	/**
	 * This method is called when the startConveyorTimer fires.
	 * The purpose of the timer is to spread out the parts to prevent
	 * a sensor from thinking that two parts are one.
	 */
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == startConveyorTimer )
			conveyorRestart = false;
		cfLead.msgConveyorReady(this);		
	}


	

	
}

