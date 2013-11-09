package engine.Sum.conveyorfamily;

import java.util.ArrayList;
import java.util.List;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;
import engine.Sum.interfaces.Conveyor;
import engine.Sum.machinefamily.LeftSideMachineAgent;
import engine.agent.Agent;
import engine.agent.Part;
import engine.interfaces.ConveyorFamily;
import engine.interfaces.Robot;



public class LeftSidePreMachineConveyorAgent extends Agent implements ConveyorFamily, Conveyor, TReceiver {
	
	private List<Part> parts;
	private enum ConveyorStatus {moving, stopped}
	private enum ConveyorEvents {respondtoquery};
	private List<ConveyorEvents> events;
	private ConveyorStatus state;
	private String name;
	private final int MAX_PARTS_ALLOWED = 2;
	private LeftSideMachineAgent nextmachine;
	private LeftSideMachineAgent priormachine;
	private LeftSidePreShuttleConveyorAgent priorconveyor;
	private final int conveyorindex;
	private final int frontsensorindex;
	private final int rearsensorindex;
	private boolean loadingintomachine;
	private boolean frontsensoroccupied;
	private boolean needtostop;
	private boolean working;
	private int partcount;
	private String machinechannel;

	public LeftSidePreMachineConveyorAgent(String newname, Transducer t, int ci, int fs, int rs, String ch) {
		super(newname, t);
		parts = new ArrayList<Part>(3);
		name = newname;
		state = ConveyorStatus.moving;
		transducer = t;
		transducer.register(this, TChannel.CONVEYOR);
		transducer.register(this, TChannel.SENSOR);
		machinechannel = ch;
		if (machinechannel.equals("BREAKOUT")) {
			transducer.register(this, TChannel.BREAKOUT);
		}
		else if (machinechannel.equals("MANUAL_BREAKOUT")) {
			transducer.register(this, TChannel.MANUAL_BREAKOUT);
		}
		events = new ArrayList<ConveyorEvents>();
		loadingintomachine = false;
		frontsensoroccupied = false;
		needtostop = false;
		conveyorindex = ci;
		frontsensorindex = fs;
		rearsensorindex = rs;
		partcount = 0;
		working = true;
	}
	
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if (machinechannel.equals("BREAKOUT")) {
			if (channel == TChannel.SENSOR) {
				if (event == TEvent.SENSOR_GUI_PRESSED && (Integer)args[0] == frontsensorindex) {
					partcount++;
					/* only fire start if the state is already moving, keep gui unified with backend*/
					if (state != ConveyorStatus.stopped && working) {
						fireStartConveyorEvent();
					}
					frontsensoroccupied = true;
					stateChanged();
				}
				/* only fire stop everything if the next part (if any) has fully loaded into the machine. This helps prevent a glitch when
				 * the firestop stops all parts on the lane, including the one entering the workstation which causes a freeze*/
				else if (event == TEvent.SENSOR_GUI_PRESSED && (Integer)args[0] == rearsensorindex) {
					needtostop = true;
					stateChanged();
				}
				else if (event == TEvent.SENSOR_GUI_RELEASED && (Integer)args[0] == rearsensorindex) {
					passPartToMachine();
				}
				else if (event == TEvent.SENSOR_GUI_RELEASED && (Integer)args[0] == frontsensorindex) {
					frontsensoroccupied = false;
					stateChanged();
				}
			}
			if (channel == TChannel.BREAKOUT) {
				if (event == TEvent.WORKSTATION_LOAD_FINISHED) {
					loadingintomachine = false;
					parts.remove(0);
					stateChanged();
				}
			}
			if (channel == TChannel.CONVEYOR) {
				if (event == TEvent.CONVEYOR_BROKEN && (Integer)args[0] == conveyorindex) {
					working = false;
					fireStopConveyorEvent();
					state = ConveyorStatus.stopped;
					stateChanged();
				}
				else if (event == TEvent.CONVEYOR_FIXED && (Integer)args[0] == conveyorindex) {
					working = true;
					if (!needtostop) {
						fireStartConveyorEvent();
						state = ConveyorStatus.moving;
					}
					stateChanged();
				}
			}
		}
		else if (machinechannel.equals("MANUAL_BREAKOUT")) {
			if (channel == TChannel.SENSOR) {
				/* breakout machine just realeased a part and it was now arrived at the front sensor of this conveyor, so start
				 * moving it down the line*/
				if (event == TEvent.SENSOR_GUI_PRESSED && (Integer)args[0] == frontsensorindex) {
					partcount++;
					if (state != ConveyorStatus.stopped && working) {
						fireStartConveyorEvent();
					}
					frontsensoroccupied = true;
					stateChanged();
				}
				/* glass part just arrived at the end of the conveyor, so now it is time to stop moving and find out if the manual
				 * breakout machine can receive a new part*/
				else if (event == TEvent.SENSOR_GUI_PRESSED && (Integer)args[0] == rearsensorindex) {
					/*if there are two parts stacked up, one is passed to machine, other is one sensor, you send in to machine, 
					 * everything stops when prior part hits sensor here */
					needtostop = true;
					stateChanged();
				}
				else if (event == TEvent.SENSOR_GUI_RELEASED && (Integer)args[0] == rearsensorindex) {
					passPartToMachine();
				}
				else if (event == TEvent.SENSOR_GUI_RELEASED && (Integer)args[0] == frontsensorindex) {
					/* don't say it's ready for the next part the moment the release occurs when prior agent is a machine since
					 * the machine by default executes a gui action/thinks it has a part when it hears canreceivepart msg*/
					frontsensoroccupied = false;
					stateChanged();
				}
			}
			if (channel == TChannel.MANUAL_BREAKOUT) {
				/* state in passingparttomachine means that machine for certain can receive new part. load finished event
				 * means that it's time to pass the actual part in a message to the manual breakout machine*/
				if (event == TEvent.WORKSTATION_LOAD_FINISHED) {
					loadingintomachine = false;
					parts.remove(0);
					stateChanged();
				}
			}
			if (channel == TChannel.CONVEYOR) {
				if (event == TEvent.CONVEYOR_BROKEN && (Integer)args[0] == conveyorindex) {
					working = false;
					fireStopConveyorEvent();
					state = ConveyorStatus.stopped;
					stateChanged();
				}
				else if (event == TEvent.CONVEYOR_FIXED && (Integer)args[0] == conveyorindex) {
					working = true;
					if (!needtostop) {
						fireStartConveyorEvent();
						state = ConveyorStatus.moving;
					}
					stateChanged();
				}
			}
		}
	}
	
	/*********************************************************************MESSAGES************************************************************************/
	
	/* this message is sent by the automatic breakout machine once the glass is at its doorstep*/
	public void msgMachineCannotAcceptPart() {
		/* do nothing */
	}
	
	/* this message is sent by the next machine once the glass is at its doorstep and it is ready for it*/
	public void msgMachineCanAcceptPart() {
		if (working) {
			state = ConveyorStatus.moving;
			loadingintomachine = true;
			fireStartConveyorEvent();
			stateChanged();
		}
	}
	
	public void msgCanYouAcceptPart() {
		synchronized(events) {
			/* for the very first part received, must respond manually, otherwise for the rest of the parts the sensor release
			 * will handle the sending of a ready message back*/
			events.add(ConveyorEvents.respondtoquery);
			stateChanged();
		}
	}
	
	public void msgHereIsNewPart(ConveyorFamily cf, Part part) {
		synchronized(parts) {
			parts.add(part);
			stateChanged();
		}
	}

	/**********************************************************************SCHEDULER************************************************************************/
	
	public boolean pickAndExecuteAnAction() {
		synchronized(events) {
			if (conveyorindex == 2) {
				if (working) {
					if (needtostop) {
						if (!loadingintomachine) {
							needtostop = false;
							fireStopConveyorEvent();
							state = ConveyorStatus.stopped;
							askMachineIfItCanTakePart();
						}
					}
					else if (events.size() > 0) {
						if (events.get(0) == ConveyorEvents.respondtoquery) {
							events.remove(0);
							respondToPriorConveyor();
						}
					}
					else if (parts.size() == 0 && events.size() == 0 && !frontsensoroccupied && !loadingintomachine) {
						System.out.println(name + " going quiescent");
						return false;
					}
				}
			}
			else if (conveyorindex == 3) {
				if (working) {
					if (needtostop) {
						if (!loadingintomachine) {
							needtostop = false;
							fireStopConveyorEvent();
							state = ConveyorStatus.stopped;
							askMachineIfItCanTakePart();
						}
					}
					else if (events.size() > 0) {
						if (events.get(0) == ConveyorEvents.respondtoquery) {
							events.remove(0);
							respondToPriorMachine();
						}
					}
					else if (parts.size() == 0 && events.size() == 0 && !frontsensoroccupied && !loadingintomachine) {
//						System.out.println(name + " going quiescent");
						return false;
					}
				}
			}
			return false;
		}
	}
	
	/**********************************************************************ACTIONS************************************************************************/
	
	/* front sensor must be empty for next part to arrive. needtostop must be false so that a new piece of glass is only sent
	 * if no stop is about to occur; a stop would result in the part freezing while arriving on the conveyor, which is 
	 * never good. No glass must be loading into the next machine either; a new part arriving at the front sensor automatically
	 * fires a move event. This could result in multiple parts flowing into the next machine. In this way we control the flow of parts */
	private void respondToPriorConveyor() {
		synchronized(parts) {
			if (parts.size() < MAX_PARTS_ALLOWED && state == ConveyorStatus.moving && !frontsensoroccupied && !needtostop && !loadingintomachine) {
				priorconveyor.msgNextConveyorCanAcceptPart();
			}
			else {
				priorconveyor.msgNextConveyorCannotAcceptPart();
				events.add(ConveyorEvents.respondtoquery);
			}
		}
	}
	
	private void respondToPriorMachine() {
		synchronized(parts) {
			if (parts.size() < MAX_PARTS_ALLOWED && state == ConveyorStatus.moving && !frontsensoroccupied && !needtostop && !loadingintomachine) {
				priormachine.msgConveyorCanAcceptPart();
			}
			else {
				priormachine.msgConveyorCannotAcceptPart();
				events.add(ConveyorEvents.respondtoquery);
			}
		}
	}
	
	private void fireStartConveyorEvent() {
		Object[] temp = new Object[1];
		temp[0] = conveyorindex;
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, temp);
	}
	
	private void fireStopConveyorEvent() {
		Object[] temp = new Object[1];
		temp[0] = conveyorindex;
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, temp);
	}
	
	private void passPartToMachine() {
		synchronized(parts) {
//			System.out.println(name + " passing part to " + machine.getName() + " in passPartToMachine. Parts size: " + parts.size());
			nextmachine.msgHereIsNewPart(this, parts.get(0));
			stateChanged();
		}
	}
	
	private void askMachineIfItCanTakePart() {
//		System.out.println(name + " sending msgCanYouMachinePart to " + machine.getName() + " in askMachineIfItCanTakePart");
		nextmachine.msgCanYouMachinePart();
		stateChanged();
	}
	
	/**********************************************************************EXTRA************************************************************************/
	public boolean partsIsEmpty() {
		if (parts.size() == 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void setNextMachine(LeftSideMachineAgent fma) {
		nextmachine = fma;
	}
	
	public void setPriorMachine(LeftSideMachineAgent input) {
		priormachine = input;
	}
	
	public String getName() {
		return name;
	}
	
	public void setPriorConveyor(LeftSidePreShuttleConveyorAgent temp) {
		priorconveyor = temp;
	}
	
	/***********************************************************UNUSED METHODS*****************************************************/

	public void msgRobotReady(Robot robot, Integer index) {
		
	}



	public void msgHereIsPopUpPart(Robot robot, Part part) {
		
	}

	public void msgConveyorStopping() {
		
	}

	public void msgIReceivedPart() {
		
	}

	public void msgPopUpUp(Robot robot) {
		
	}

	public void msgMachinePart(Robot robot, Part part) {
		
	}

	public void msgHereIsPartFromPopUp(Part part) {
		
	}

	public void msgLeadSensorDepressed() {
		
	}

	public void msgLeadSensorReleased() {
		
	}

	public void msgMachineReady() {
		
	}
	
	public void msgRobotReady(Robot robot) {
		
	}

	public void msgRobotPartReceived(Robot robot) {
		
	}

	public void msgPartDone(Robot robot) {
		
	}

	public void msgHereIsPopUpPart(Part part) {

	}
	
	public void msgPopUpCannotAcceptPart() {
		
	}

	public void msgPopUpCanAcceptPart() {
		
	}

	@Override
	public void msgBinConveyorReady() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBinConveyorStopping() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBinHereIsNewPart(Part part) {
		// TODO Auto-generated method stub.
		
	}
	
	public void msgConveyorReady(ConveyorFamily cf) {
		
	}

	public void msgConveyorPartReceived(ConveyorFamily cf) {
		
	}
	
}
