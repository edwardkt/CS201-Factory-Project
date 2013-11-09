package engine.sanders.agent;

import java.util.*;

import transducer.*;
import engine.agent.Agent;
import engine.agent.Part;
import engine.interfaces.*;
import engine.sanders.interfaces.Conveyor;
import engine.sanders.interfaces.PopUp;

/**
 * This is an agent class that controls the raising, lowering,
 * loading, and releasing of a pop up in the glassline factory
 * 
 * @author Justin
 *
 */
public class PopUpAgent extends Agent implements PopUp, TReceiver {
	
	private boolean leadSensorDepressed = false;
	private boolean followConveyorFamilyReady = false;
	private Conveyor conveyor;
	private List<MyRobot> robots;
	private MyPart currentPart;
	private Integer myIndex;
	private Integer conveyorFamilyIndex;
	private Integer workstationIndex;
	private ConveyorFamily myConveyorFamily;
	private Object lastPartSender;
	private Transducer transducer;
	private MyState myState;
	private MyHeight myHeight;
	private TChannel myWorkStation;
	private boolean isBroken = false;
	private boolean nextPartNeedsProcessing;
	
	enum RobotState{ Available, PartSent, MachiningPart, PartDone, Broken }
	private enum MyHeight{ Down, Lowering, Up, Rising }
	private enum MyState{ Empty, Loading, Releasing, Full }
	
	private class MyRobot {
		Robot robot;
		RobotState state;
		Integer index;
		
		public MyRobot( Robot robot, Integer index ) {
			this.robot = robot;
			this.index = index;
			state = RobotState.Available;
		}
	}
	
	private class MyPart {
		Part part;
		boolean isMachined;
		
		public MyPart( Part part, boolean isMachined ) {
			this.part = part;
			this.isMachined = isMachined;
		}
	}

	/**
	 * Constructor for PopUpAgent
	 * 
	 * @param cf
	 * 			parent ConveyorFamilyContainer
	 * @param transducer
	 * 			transducer that allows communication with the GUI
	 * @param index
	 * 			the index of the conveyor within the factory
	 * @param cfIndex
	 * 			the index of the conveyor family/workstation that the pop up belongs to
	 * @param recipe
	 * 			the code that corresponds with a part that would be machined at the workstation
	 * 			tied to the parent ConveyorFamilyContainer
	 * @param workstation
	 * 			the channel that the pop should listen to to know when the robot finishes
	 * 			loading a part
	 * @param name
	 * 			the name of the agent
	 */
	public PopUpAgent( ConveyorFamily cf, Transducer transducer, Integer index, Integer cfIndex, Integer workstationIndex, TChannel workstation, String name ) {
		super( name );
		
		robots = Collections.synchronizedList( new ArrayList<MyRobot>() );
		currentPart = null;
		myIndex = new Integer( index );
		conveyorFamilyIndex = new Integer( cfIndex );
		this.workstationIndex = new Integer( workstationIndex );
		myConveyorFamily = cf;
		this.transducer = transducer;
		lastPartSender = new Object();
		myState = MyState.Empty;
		myHeight = MyHeight.Down;
		myWorkStation = workstation;
		this.name = name;
		
		this.transducer.register( this, TChannel.POPUP );
		this.transducer.register( this, TChannel.SENSOR );
		this.transducer.register( this,  myWorkStation );

	}
	
	

	//*********************MESSAGES**********************************
	
	/**
	 * Tells pop up that the next conveyor family is ready to take parts
	 */
	public void msgConveyorReady() {
		followConveyorFamilyReady = true;
		//System.out.println( "" + getName() + " received msgConveyorReady" );
		stateChanged();
	}

	/**
	 * Tells the pop up that the next conveyor family successfully
	 * received a part
	 */
	public void msgPartReceived() {
		//myState = MyState.Releasing;
		//stateChanged();
	}

	/**
	 * Tells the pop up that there is a robot that is ready. The pop up
	 * checks to see if it is a new robot.  If it is, it adds it. If not,
	 * it changed the robots state to "available".
	 * 
	 * @param robot	
	 * 			a pointer to the robot that sent the message
	 * @param index
	 * 			the index of the robot
	 */
	public void msgRobotReady(Robot robot, Integer index) {
		//check if it is new robot else set existing robot state to available
		boolean newRobot = true;
		synchronized( robots ) {
			for( MyRobot r : robots ) {
				if( r.robot.equals( robot ) ) {
					newRobot = false;
					r.state = RobotState.Available;
					break;
				}
			}
		}
		if( newRobot )
			robots.add( new MyRobot( robot, index ) );
		
		conveyor.msgRobotAvailable( true );
		stateChanged();
	}

	/**
	 * Tells the pop up the a robot successfully received
	 * a part that the pop up sent
	 * 
	 * @param robot
	 * 			a pointer to the robot that sent the message
	 */
	public void msgRobotPartReceived(Robot robot) {
		currentPart = null;
		myState = MyState.Empty;
		conveyor.msgPopUpHasPart( false );
		synchronized( robots ) {
			for( MyRobot r : robots ) {
				if( r.robot.equals( robot ) ) {
					r.state = RobotState.MachiningPart;
					break;
				}
			}
		}
		stateChanged();
	}

	/**
	 * Tells the pop up that there is a robot that has
	 * a part that is finished machining
	 * 
	 * @param robot
	 * 			a pointer to the robot that sent the message
	 */
	public void msgPartDone(Robot robot) {
		synchronized( robots ) {
			for( MyRobot r : robots ) {
				if( r.robot.equals( robot ) ) {
					r.state = RobotState.PartDone;
					break;
				}
			}
		}
		stateChanged();
	}

	/**
	 * This message give the pop up a part that a robot sent
	 * 
	 * @param robot
	 * 			a pointer to the robot that sent the message
	 * @param part
	 * 			the part being handed off to the pop up
	 */
	public void msgHereIsPopUpPart(Robot robot, Part part) {
		lastPartSender = robot;
		myState = MyState.Loading;
		currentPart = new MyPart( part, true );
//		msgRobotReady( robot, null );
		stateChanged();
	}
	
	/**
	 * This message give the pop up a part that a conveyor sent
	 * 
	 * @param conveyor
	 * 			a pointer to the conveyor that sent the message
	 * @param part
	 * 			the part being handed off to the pop up
	 */
	public void msgHereIsPopUpPart( Conveyor conveyor, Part part ) {
		lastPartSender = conveyor;
		myState = MyState.Loading;
		currentPart = new MyPart( part, false );
		stateChanged();
	}
	
	/**
	 * This message informs the pop up if the part waiting on the sensor
	 * before it needs to be processed at its workstation
	 * 
	 * @param needsProcessing
	 * 			signifies if the part needs to be processed
	 */
	public void msgNextPartNeedsProcessing( boolean needsProcessing ) {
		nextPartNeedsProcessing = needsProcessing;
		stateChanged();
	}

	
	//***************************SCHEDULER****************************
	public boolean pickAndExecuteAnAction() {

		if ( isBroken ) {
			return false;
		}
		else if ( myState == MyState.Empty ) {
			if ( myHeight == MyHeight.Up && !robots.isEmpty() ) {
				boolean partDone = false;
				synchronized( robots ) {
					for( MyRobot r : robots ) {
						if( r.state == RobotState.PartDone ) {
							//the pop up is empty, in the up position, and a robot has a part that is done
							partDone = true;
							informRobot( r );
							break;
						}
					}
				}
				
				if( !partDone ) {
					//the pop up is empty, in the up position, and no robots have a part that is done
					lowerPopUp();
				}
			}
			else if ( myHeight == MyHeight.Down && !robots.isEmpty() && !leadSensorDepressed || nextPartNeedsProcessing && !checkForAvailableRobots() ) {
				synchronized( robots ) {
					for( MyRobot r : robots ) {
						if( r.state == RobotState.PartDone ) {
							//the pop up is empty, in the down position, and a robot has a part that is done
							raisePopUp();
							break;
						}
					}
				}
			}
		}

		else if ( myState == MyState.Full ) {
			if ( myHeight == MyHeight.Down ) {
				if ( followConveyorFamilyReady && currentPart.part.getRecipe().charAt(workstationIndex) == '0' ) {
				//if ( followConveyorFamilyReady && ( ( Integer.parseInt( this.recipe ) & Integer.parseInt( currentPart.part.getRecipe() ) ) != 0 ) ) {
					//PopUp is down with a part on it, the next conveyor family is ready to take 
					//a part and the part on the PopUp should not be machined at this station
					releasePartToConveyor();
				}
				else if ( currentPart.part.getRecipe().charAt(workstationIndex) != '0' ) {
				//else if ( ( Integer.parseInt( this.recipe ) & Integer.parseInt( currentPart.part.getRecipe() ) ) == 1 ) {
					if ( !currentPart.isMachined ) {
						//PopUp is down with a part on it that needs to be machine
						raisePopUp();
					}
					else if ( currentPart.isMachined && followConveyorFamilyReady ) {
						//PopUp is down with a part on it that does not need to be machine and the next conveyor is ready
						releasePartToConveyor();
					}
				}
			}
			else if ( myHeight == MyHeight.Up ) {
				if ( currentPart.isMachined ) {
					//PopUp is up with a part on it that matches the station's recipe and is machined
					lowerPopUp();
				}
				else if ( !currentPart.isMachined && !robots.isEmpty() ) {
					synchronized( robots ) {
						for( MyRobot r : robots ) {
							if( r.state == RobotState.Available ) {
								//pop up is full, in the up position, and contains a part that is not machined
								releasePartToRobot( r );
								break;
							}
						}
					}
				}
			}
		}
								
		
		return false;
	}
	
	//*****************ACTIONS*******************
	
	private void releasePartToRobot( MyRobot robot ) {
		myState = MyState.Releasing;
		myConveyorFamily.msgMachinePart( robot.robot, currentPart.part );
		Object[] args = new Object[1];
		args[0] = robot.index;
		transducer.fireEvent( myWorkStation, TEvent.WORKSTATION_DO_LOAD_GLASS, args );
		robot.state = RobotState.PartSent;
		checkForAvailableRobots();
		stateChanged();
	}
	
	private void releasePartToConveyor() {
		myState = MyState.Releasing;
		myConveyorFamily.msgHereIsPartFromPopUp( currentPart.part );
		followConveyorFamilyReady = false;
		Object[] args = new Object[1];
		args[0] = myIndex;
		transducer.fireEvent( TChannel.POPUP, TEvent.POPUP_RELEASE_GLASS, args );
		stateChanged();
	}
	
	private boolean checkForAvailableRobots() {
		boolean robotAvailable = false;
		synchronized( robots ) {
			for( MyRobot r : robots ) {
				if ( r.state == RobotState.Available ) {			
					robotAvailable = true;
					break;
				}
			}
		}
		conveyor.msgRobotAvailable( robotAvailable );
		return robotAvailable;
	}
	
	private void raisePopUp() {
		conveyor.msgPopUpUp( true );
		myHeight = MyHeight.Rising;
		
		Object[] args = new Object[1];
		args[0] = myIndex;
		transducer.fireEvent( TChannel.POPUP, TEvent.POPUP_DO_MOVE_UP, args );
		stateChanged();
	}
	
	private void lowerPopUp() {
		myHeight = MyHeight.Lowering;
	
		Object[] args = new Object[1];
		args[0] = myIndex;
		transducer.fireEvent( TChannel.POPUP, TEvent.POPUP_DO_MOVE_DOWN, args );
		stateChanged();
	}
	
	private void informRobot( MyRobot robot ) {
		myConveyorFamily.msgPopUpUp( robot.robot );
	}
	
	
	//***********Misc Methods***************
	
	/**
	 * sets the conveyor that is inside the same conveyor family so the
	 * pop up knows who to tell when its state changes
	 * 
	 * @param conveyor
	 * 			the conveyor that is in the same conveyor family
	 */
	public void setConveyor( Conveyor conveyor ) {
		this.conveyor = conveyor;
	}
	
	
	//the rest are used only for testing
	public MyHeight getPopUpHeight() {
		return myHeight;
	}
	
	public MyState getPopUpState() {
		return myState;
	}
	
	public boolean getLeadSensorDepressed() {
		return leadSensorDepressed;
	}
	
	public boolean getFollowConveyorFamilyReady() {
		return followConveyorFamilyReady;
	}

	
	//*******************TRANSDUCER EVENTS*********************
	/**
	 * This method is called by the transducer in order for the front end
	 * to communicate with the agent
	 */
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		
		if( channel == TChannel.SENSOR && ( conveyorFamilyIndex.equals((Integer)args[0] / 2) ) && ((Integer)args[0] % 2 == 1) ) {
			if( event == TEvent.SENSOR_GUI_PRESSED) {
				leadSensorDepressed = true;
				//stateChanged();
			}
			else if( event == TEvent.SENSOR_GUI_RELEASED ) {
				leadSensorDepressed = false;
				stateChanged();
			}
		}
		
		else if( channel == TChannel.POPUP && ( myIndex.equals((Integer)args[0] ) ) ) {
			if( event == TEvent.POPUP_BROKEN ) {
				isBroken = true;
				System.err.println( getName() + " is broken" );
			}
			
			else if( event == TEvent.POPUP_FIXED ) {
				isBroken = false;
				System.out.println( getName() + " is fixed" );
				stateChanged();
			}
			
			else if( event == TEvent.POPUP_GUI_LOAD_FINISHED ) {	
				
				if( currentPart != null ) {
					if( lastPartSender instanceof Conveyor ) {
						conveyor.msgPopUpPartReceived( currentPart.part );
						conveyor.msgPopUpHasPart( true );
					}
					myState = MyState.Full;
					stateChanged();
				}
				//this would happen in the event that the previous sensor does not fire.
				//the agent would not notify the pop up the part is coming so the part
				//is non-existent as far as the agents are concerned
				else { 
					conveyor.msgPopUpPartReceived( null );
					System.err.println( getName() + " loaded untracked part" );
				}
			}
			
			else if( event == TEvent.POPUP_GUI_RELEASE_FINISHED ) {
				currentPart = null;
				myState = MyState.Empty;
				conveyor.msgPopUpHasPart( false );
				stateChanged();
			}
			
			else if( event == TEvent.POPUP_GUI_MOVED_DOWN ) {
				myHeight = MyHeight.Down;
				conveyor.msgPopUpUp( false );
				stateChanged();
			}
			
			else if( event == TEvent.POPUP_GUI_MOVED_UP ) {
				myHeight = MyHeight.Up;
				conveyor.msgPopUpUp( true );
				stateChanged();
			}
		}
		
		else if( channel == myWorkStation ) {
			if( event == TEvent.WORKSTATION_BROKEN ) {
				synchronized( robots ) {
					for( MyRobot robot: robots ) {
						if( robot.index.equals( args[0] ) ) {
							robot.state = RobotState.Broken;
							checkForAvailableRobots();
							break;
						}
					}
				}
			}			
			
			else if( event == TEvent.WORKSTATION_RELEASE_FINISHED ) {
				myState = MyState.Full;
				conveyor.msgPopUpHasPart( true );
				stateChanged();
			}
		}
	}

}
