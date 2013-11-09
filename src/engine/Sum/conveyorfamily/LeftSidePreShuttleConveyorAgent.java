package engine.Sum.conveyorfamily;
import java.util.ArrayList;
import java.util.List;

import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import engine.Sum.interfaces.Conveyor;
import engine.Sum.machinefamily.LeftSideMachineAgent;
import engine.agent.Agent;
import engine.agent.Part;
import engine.chu.agent.ConveyorFamilyGroup;
import engine.chu.agent.MachineAgent;
import engine.interfaces.ConveyorFamily;
import engine.interfaces.Robot;
import engine.sanders.agent.ConveyorFamilyContainer;

public class LeftSidePreShuttleConveyorAgent extends Agent implements ConveyorFamily, Conveyor {
	private List<Part> parts;
	private enum ConveyorStatus {moving, stopped}
	private enum ConveyorEvents {respondtoquery, priormachinestopped, sendconveyorready, partwaitingforreadymsg};
	private ConveyorStatus state;
	private List<ConveyorEvents> events;
	private String name;
	private final int MAX_PARTS_ALLOWED = 2;
	private ConveyorFamily priorcuttermachine;
	private LeftSideMachineAgent priormachine;
	private ConveyorFamilyContainer nextfamily;
	private LeftSidePreMachineConveyorAgent nextconveyor;
	private boolean receivednewreadymessage;
	private boolean partonfrontsensor;
	private boolean partonrearsensor;
	private boolean working;
	private boolean sentnewinstanceofreadymessagetopriormachine;
	private final int conveyorindex;
	private final int frontsensorindex;
	private final int rearsensorindex;
	private int sensorfirecount;
	private boolean sensorjam;

	public LeftSidePreShuttleConveyorAgent(String newname, Transducer t, int ci, int fs, int rs) {
		super(newname, t);
		parts = new ArrayList<Part>(3);
		name = newname;
		state = ConveyorStatus.moving;
		transducer = t;
		transducer.register(this, TChannel.CONVEYOR);
		transducer.register(this, TChannel.SENSOR);
		transducer.register(this, TChannel.BIN);
		sentnewinstanceofreadymessagetopriormachine = false;
		events = new ArrayList<ConveyorEvents>();
		conveyorindex = ci;
		frontsensorindex = fs;
		rearsensorindex = rs;
		partonfrontsensor = false;
		working = true;
		partonrearsensor = false;
		receivednewreadymessage = false;
		sensorfirecount = 0;
		sensorjam = false;
	}
	
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if (channel == TChannel.SENSOR) {
			/* if the part has arrived at the start of the conveyor, start moving the glass down the line*/
			if (event == TEvent.SENSOR_GUI_PRESSED && (Integer)args[0] == frontsensorindex) {
				if (!partonrearsensor && working) {
					fireStartConveyorEvent();
				}
				partonfrontsensor = true;
				sensorfirecount++;
				stateChanged();
			}
			/* if the part has arrived at the rear sensor of the conveyor, check if the next conveyor can accept a new part*/
			else if (event == TEvent.SENSOR_GUI_PRESSED && (Integer)args[0] == rearsensorindex) {
				if (conveyorindex == 1) {
					sensorfirecount--;
					partonrearsensor = true;
					/* 0 and 1 as well since another part behind it could hit the sensor*/
					if (sensorfirecount != 0 && sensorfirecount != 1) {
						sensorjam = true;
						fireStopConveyorEvent();
						System.out.println(name + " found a sensor jam. sensor count: " + sensorfirecount);
						state = ConveyorStatus.stopped;
						stateChanged();
					}
					else {
						fireStopConveyorEvent();
						askNextConveyorIfItCanTakePart();
						state = ConveyorStatus.stopped;
						stateChanged();
					}
				}
				else if (conveyorindex == 4) {
					sensorfirecount--;
					partonrearsensor = true;
					if (sensorfirecount != 0 && sensorfirecount != 1) {
						sensorjam = true;
						fireStopConveyorEvent();
						System.out.println(name + " found a sensor jam");
						state = ConveyorStatus.stopped;
						stateChanged();
					}
					else if (!receivednewreadymessage) {
						fireStopConveyorEvent();
						events.add(ConveyorEvents.partwaitingforreadymsg);
						state = ConveyorStatus.stopped;
					}
				}
				stateChanged();
			}
			else if (event == TEvent.SENSOR_GUI_RELEASED && (Integer)args[0] == rearsensorindex) {
				synchronized(parts) {
					if (conveyorindex == 4) {
						receivednewreadymessage = false;
						partonrearsensor = false;
						nextfamily.msgHereIsNewPart(this, parts.get(0));
						parts.remove(0);
						stateChanged();
					}
					else if (conveyorindex == 1) {
						synchronized(parts) {
							receivednewreadymessage = false;
							partonrearsensor = false;
							nextconveyor.msgHereIsNewPart(this, parts.get(0));
							parts.remove(0);
							stateChanged();
						}
						stateChanged();
					}
				}
			}
			if (conveyorindex == 1) {
				if (event == TEvent.SENSOR_GUI_RELEASED && (Integer)args[0] == frontsensorindex) {
					partonfrontsensor = false;
//					notifyPriorMachineThatConveyorReady();
					events.add(ConveyorEvents.sendconveyorready);
					stateChanged();
				}
			}
		}
		else if (channel == TChannel.CONVEYOR) {
			if (event == TEvent.CONVEYOR_BROKEN && (Integer)args[0] == conveyorindex) {
				working = false;
				fireStopConveyorEvent();
				state = ConveyorStatus.stopped;
				stateChanged();
			}
			else if (event == TEvent.CONVEYOR_FIXED && (Integer)args[0] == conveyorindex) {
				working = true;
				if (conveyorindex == 4) {
					state = ConveyorStatus.moving;
					if (receivednewreadymessage) {
						fireStartConveyorEvent();
					}
					else if (!partonrearsensor) {
						fireStartConveyorEvent();
					}
				}
				else if (conveyorindex == 1) {
					/* ask again after fix */
					if (partonrearsensor) {
						fireStopConveyorEvent();
						askNextConveyorIfItCanTakePart();
						state = ConveyorStatus.stopped;
						stateChanged();
					}
					else if (!partonrearsensor) {
						state = ConveyorStatus.moving;
						fireStartConveyorEvent();
					}
				}
				stateChanged();
			}
		}
	}
	
	/*********************************************************************MESSAGES************************************************************************/
	
	public void msgCanYouAcceptPart() {
		synchronized(events) {
			events.add(ConveyorEvents.respondtoquery);
//			System.out.println(name + " received msgcanyouacceptpart and events size: " + events.size());
			stateChanged();
		}
	}
	
	/* sent by next family */
	public void msgConveyorReady(ConveyorFamily cf) {
		if (state == ConveyorStatus.stopped && working && !sensorjam) {
			fireStartConveyorEvent();
		}
		state = ConveyorStatus.moving;
		receivednewreadymessage = true;
		stateChanged();
	}

	public void msgConveyorPartReceived(ConveyorFamily cf) {
		state = ConveyorStatus.moving;
//		System.out.println(name + " heard that next family received the new part");
		stateChanged();
	}

	/* sent by prior agent when release of part is finished */
	public void msgHereIsNewPart(ConveyorFamily cf, Part part) {
		synchronized(parts) {
			parts.add(part);
			sentnewinstanceofreadymessagetopriormachine = false;
			stateChanged();
		}
	}
	
	/* sent by next conveyor (should be conveyor 3)*/
	public void msgNextConveyorCanAcceptPart() {
		if (working && !sensorjam) {
			state = ConveyorStatus.moving;
			fireStartConveyorEvent();
			/* because a shuttle is ahead, we must actually send the part here since there won't be a workstation transducer event to 
			 * cooperate with*/
			stateChanged();
		}
	}
	
	public void msgNextConveyorCannotAcceptPart() {
		state = ConveyorStatus.stopped;
		stateChanged();
	}

	/**********************************************************************SCHEDULER************************************************************************/
	
	public boolean pickAndExecuteAnAction() {
		synchronized(events) {
			if (working) {
				if (events.size() > 0) {
					if (conveyorindex == 4) {
						/* manual breakout machineis on hold, so notify it if the conveyor can accept another part*/
						if (events.get(0) == ConveyorEvents.respondtoquery || events.get(0) == ConveyorEvents.priormachinestopped) {
							events.remove(0);
							respondToPriorMachine();
						}
						else if (events.get(0) == ConveyorEvents.partwaitingforreadymsg) {
							events.remove(0);
							if (receivednewreadymessage) {
								fireStartConveyorEvent();
							}
							else {
								events.add(ConveyorEvents.partwaitingforreadymsg);
							}
						}
					}
					else if (conveyorindex == 1) {
						if (events.get(0) == ConveyorEvents.sendconveyorready) {
							events.remove(0);
							if (parts.size() < MAX_PARTS_ALLOWED && state == ConveyorStatus.moving && working && !sensorjam) {
								notifyPriorMachineThatConveyorReady();
							}
							else {
								events.add(ConveyorEvents.sendconveyorready);
							}
						}
					}
				}
				if (parts.size() == 0 && events.size() == 0 && !partonfrontsensor && !partonrearsensor) {
	//				System.out.println(name + " going quiescent");
					return false;
				}
			}
			return false;
		}
	}
	
	/**********************************************************************ACTIONS************************************************************************/

	private void askNextConveyorIfItCanTakePart() {
		nextconveyor.msgCanYouAcceptPart();
		stateChanged();
	}
	
	private void respondToPriorMachine() {
		if (working) {
			if (sensorjam) {
				priormachine.msgConveyorCannotAcceptPart();
				events.add(ConveyorEvents.priormachinestopped);
			}
			else if (state == ConveyorStatus.moving) {
				synchronized(parts) {
					if (parts.size() < MAX_PARTS_ALLOWED) {
						priormachine.msgConveyorCanAcceptPart();
					}
					/* conveyor is moving but cannot accept part, so keep track of fact that it must get back to machine when 
					 * conveyor frees up */
					else {
						synchronized(events) {
							priormachine.msgConveyorCannotAcceptPart();
							events.add(ConveyorEvents.priormachinestopped);
						}
					}
				}
			}
			else if (state == ConveyorStatus.stopped) {
				synchronized(events) {
					priormachine.msgConveyorCannotAcceptPart();
					events.add(ConveyorEvents.priormachinestopped);
				}
			}
		}
		else {
			priormachine.msgConveyorCannotAcceptPart();
			events.add(ConveyorEvents.priormachinestopped);
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
 	
 	private void notifyPriorMachineThatConveyorReady() {
 		priorcuttermachine.msgConveyorReady(this);
		stateChanged();
 	}
 	
 	private void notifyPriorMachineThatPartReceived() {
 		priorcuttermachine.msgIReceivedPart();
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
	
	public void setPriorCutterMachine(ConveyorFamilyGroup input) {
		priorcuttermachine = input;
	}
	
	public void setPriorMachine(LeftSideMachineAgent input) {
		priormachine = input;
	}
	
	public void setNextConveyorFamily(ConveyorFamilyContainer input) {
		nextfamily = input;
	}
	
	public void setNextConveyor(LeftSidePreMachineConveyorAgent input) {
		nextconveyor = input;
	}
	
	public String getName() {
		return name;
	}

	/**********************************************************UNUSED METHODS**************************************************/
	
	public void msgRobotReady(Robot robot, Integer index) {
		
	}

	

	public void msgHereIsPopUpPart(Robot robot, Part part) {
		
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

	public void msgBinConveyorReady() {
		
	}

	public void msgBinConveyorStopping() {
		
	}

	public void msgBinHereIsNewPart(Part part) {
		
	}
}
