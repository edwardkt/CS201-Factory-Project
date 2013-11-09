package engine.sanders.agent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.Timer;

import transducer.*;
import engine.agent.Agent;
import engine.agent.Part;
import engine.interfaces.*;
import engine.sanders.interfaces.Conveyor;
import engine.sanders.interfaces.PopUp;

/**
 * This is an agent class that controls the starting and stopping of
 * a conveyor in the glassline factory
 * 
 * @author Justin
 *
 */
public class ConveyorAgent extends Agent implements Conveyor, TReceiver, ActionListener {
	private boolean leadSensorDepressed = false;
	private boolean followSensorDepressed = false;
	private boolean isRunning = true;
	private LinkedList<Part> parts;
	private Integer myIndex;
	private MyPopUp myPopUp;
	private Integer workstationIndex;
	private ConveyorFamily myConveyorFamily;
	private Transducer transducer;
	private int sensorPartCount = 0;
	private boolean isBroken = false;
	private boolean sensorBroken = false;
	private boolean conveyorRestart = false;
	private Timer readyMessageTimer, startConveyorTimer;

	private class MyPopUp {
		PopUp popUp;
		boolean isUp;
		boolean robotAvailable;
		boolean hasPart;
		
		public MyPopUp( PopUp popUp ) {
			this.popUp = popUp;
			isUp = false;
			robotAvailable = false;
			hasPart = false;
		}
	}
	
	/**
	 * Contstructor for ConveyorAgent
	 * 
	 * @param cf
	 * 			parent ConveyorFamilyContainer
	 * @param transducer
	 * 			transducer that allows communication with the GUI
	 * @param popUp
	 * 			the pop up that is within the same ConveyorFamilyContainer
	 * @param index
	 * 			the index of the conveyor within the factory
	 * @param recipe
	 * 			the code that corresponds with a part that would be machined at the workstation
	 * 			tied to the parent ConveyorFamilyContainer
	 * @param name
	 * 			the name of this agent
	 */
	public ConveyorAgent( ConveyorFamily cf, Transducer transducer, PopUp popUp, Integer index, Integer workstationIndex, String name ) {
		super( name );
		parts = new LinkedList<Part>();
		myConveyorFamily = cf;
		this.transducer = transducer;
		myPopUp = new MyPopUp( popUp );
		myIndex = new Integer( index );
		this.workstationIndex = new Integer( workstationIndex );
		readyMessageTimer = new Timer( 200, this );
		startConveyorTimer = new Timer( 800, this );
		readyMessageTimer.setRepeats( false );
		startConveyorTimer.setRepeats( false );
		
		this.transducer.register( this, TChannel.SENSOR );
		this.transducer.register( this, TChannel.CONVEYOR );
		
		Object[] args = new Object[1];
		args[0] = myIndex;
		
		this.transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args );
	}

	
	//****************MESSAGES***********************
	
	/**
	 * This message is gives the conveyor a new part
	 * 
	 * @param part
	 * 			the part being passed to the conveyor
	 */
	public void msgHereIsNewPart(Part part) {
		parts.add( part );
	}
	
	/**
	 * Tells the conveyor that the pop up in its conveyor family
	 * is up or down
	 * 
	 * @param isUp
	 * 			tells the conveyor if the pop up is up or down
	 */
	public void msgPopUpUp( boolean isUp ) {
		myPopUp.isUp = isUp;
		stateChanged();
	}
	
	/**
	 * Tells the conveyor that a robot is available to machine
	 * parts
	 * 
	 * @param robotAvailable
	 * 			whether or not a robot is available to take a part
	 */
	public void msgRobotAvailable( boolean robotAvailable ) {
		myPopUp.robotAvailable = robotAvailable;
		stateChanged();
	}
	
	/**
	 * Tells the conveyor that the pop up has a part on it
	 * 
	 * @param hasPart
	 * 			whether or not the pop up has a part on it
	 */
	public void msgPopUpHasPart( boolean hasPart ) {
		myPopUp.hasPart = hasPart;
		stateChanged();
	}
	
	/**
	 * Tells the conveyor that a part that it passed to the
	 * pop up was successfully received
	 * 
	 * @param part
	 * 			the part that was received by the conveyor
	 */
	public void msgPopUpPartReceived( Part part ) {
		if ( part != null )
			parts.remove( part );
		else {
			sensorBroken = true;
			System.err.println( getName() + " is stopping because pop up received untracked part" );
			Object[] args = new Object[1];
			args[0] = myIndex * 2 + 1;
			transducer.fireEvent( TChannel.GUI, TEvent.SENSOR_BROKEN, args );
			stateChanged();
		}
	}

	//*****************SCHEDULER********************//
	
	public boolean pickAndExecuteAnAction() {
		
		if( isBroken || sensorBroken ) {
			stopConveyor();
			return false;
		}
		
		else if( sensorPartCount < 0 ) {
			System.err.println( getName() + " has an unexpected number of sensor events" );
			System.err.println( getName() + " is now stopping" );
			Object[] args = new Object[1];
			args[0] = myIndex * 2;
			transducer.fireEvent( TChannel.GUI, TEvent.SENSOR_BROKEN, args );
			sensorBroken = true;
			stopConveyor();
			return false;
		}
		
		if ( followSensorDepressed ) {
			if ( isRunning ) {
					if ( myPopUp.isUp )
						//The second sensor is depressed, the conveyor is running, and the pop up is up
						stopConveyor();
					else if ( !myPopUp.isUp ) {
							if ( myPopUp.hasPart )
								//The second sensor is depressed, the conveyor is running, the pop up is down and it has a part
								stopConveyor();
							else if ( !myPopUp.robotAvailable && parts.getFirst().getRecipe().charAt( workstationIndex ) != '0' )
								//The second sensor is depressed, the conveyor is running, the pop up is down and does not have a part, and the part on the sensor needs to be machined
								stopConveyor();
							else//The second sensor is depressed, the conveyor is running, the pop up is down and does not have a part
								transferPartToPopUp();
					}
			}
			else if ( !isRunning && !myPopUp.isUp && !myPopUp.hasPart ) {
				if ( parts.getFirst().getRecipe().charAt( workstationIndex ) == '0' )
					//The second sensor is depressed, the conveyor is stopped, the pop up is down and does not have a part, and the part on the sensor does not need to be machined
					startConveyor();
				else if ( myPopUp.robotAvailable && parts.getFirst().getRecipe().charAt( workstationIndex ) != '0' )
					//The second sensor is depressed, the conveyor is stopped, the pop up is down and does not have a part, a robot is available,  and the part on the sensor needs to be machined 
					startConveyor();
			}
		}
		else {
			if ( !isRunning && !parts.isEmpty() ) {
				//The second sensor is not pressed and the conveyor is stopped
				startConveyor();	
			}
		}
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
	
	private void transferPartToPopUp() {
		myPopUp.popUp.msgHereIsPopUpPart( this, parts.getFirst() );
	}
	
	//************************MISC METHODS*****************************
	
	//all these methods are only for testing
	public boolean getLeadSensorIsDepressed() {
		return leadSensorDepressed;
	}
	
	public boolean getFollowSensorIsDepressed() {
		return followSensorDepressed;
	}
	
	public boolean getConveyorIsRunning() {
		return isRunning;
	}
	
	public int getSizeOfPartsList() {
		return parts.size();
	}
	
	public boolean getRobotsAvailable() {
		return myPopUp.robotAvailable;
	}
	
	public boolean getPopUpUp() {
		return myPopUp.isUp;
	}
	
	public boolean getPopUpHasPart() {
		return myPopUp.hasPart;
	}
	
	
	//*********************TRANSDUCER EVENTS***************************
	/**
	 * This method is called by the transducer in order for the front end
	 * to communicate with the agent
	 */
	public void eventFired( TChannel channel, TEvent event, Object[] args ) {
		
		if( channel == TChannel.CONVEYOR && myIndex.equals((Integer)args[0]) ) {
			if( event == TEvent.CONVEYOR_BROKEN ) {
				isBroken = true;
				System.err.println( getName() + " is broken" );
				stateChanged();
			}
			else if( event == TEvent.CONVEYOR_FIXED ) {
				isBroken = false;
				System.out.println( getName() + " is fixed" );
				stateChanged();
			}
		}
		
		else if( channel == TChannel.SENSOR && ( myIndex.equals((Integer)args[0] / 2 ) ) ) {
			
			if( (Integer)args[0] % 2 == 0 ) {
				if( event == TEvent.SENSOR_FIXED ) {
					sensorBroken = false;
					myConveyorFamily.msgIReceivedPart();
					System.out.println( "Sensor " + (myIndex*2) + " is reported as fixed\n" + getName() + " is resuming normal operation" );
					sensorPartCount++;
					((ConveyorFamilyContainer)myConveyorFamily).msgIAmReadyForPart();
					stateChanged();
				}
				else if( event == TEvent.SENSOR_GUI_PRESSED ) {
					myConveyorFamily.msgIReceivedPart();
					leadSensorDepressed = true;
					sensorPartCount++;
					stateChanged();
				}
				
				else if( event == TEvent.SENSOR_GUI_RELEASED ) {
					if( !conveyorRestart )
						readyMessageTimer.start();
					leadSensorDepressed = false;
					stateChanged();
				}
			}
			else if ( (Integer)args[0] % 2 == 1 ) {
				if( event == TEvent.SENSOR_FIXED ) {
					sensorBroken = false;
					sensorPartCount--;
					System.out.println( "Sensor " + (myIndex*2+1) + " is reported as fixed\n" + getName() + " is resuming normal operation" );
					parts.remove();
					stateChanged();
				}
				else if( event == TEvent.SENSOR_GUI_PRESSED ) {
					if( parts.getFirst().getRecipe().charAt( workstationIndex ) != '0' )
						((PopUpAgent)myPopUp.popUp).msgNextPartNeedsProcessing( true );
					else
						((PopUpAgent)myPopUp.popUp).msgNextPartNeedsProcessing( false );
					followSensorDepressed = true;
					sensorPartCount--;
					stateChanged();
				}
				
				else if( event == TEvent.SENSOR_GUI_RELEASED ) {
					followSensorDepressed = false;
					stateChanged();
				}
			}
		}
	}

	/**
	 * This method is called when the startConveyorTimer fires.
	 * The purpose of the timer is to spread out the parts to prevent
	 * a sensor from thinking that two parts are one.
	 */
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == startConveyorTimer )
			conveyorRestart = false;
		((ConveyorFamilyContainer)myConveyorFamily).msgIAmReadyForPart();
	}
}

