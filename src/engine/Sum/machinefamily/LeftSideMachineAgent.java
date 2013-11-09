package engine.Sum.machinefamily;

import java.util.ArrayList;
import java.util.List;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;
import engine.Sum.conveyorfamily.LeftSidePreMachineConveyorAgent;
import engine.Sum.conveyorfamily.LeftSidePreShuttleConveyorAgent;
import engine.agent.Agent;
import engine.agent.Part;
import engine.interfaces.ConveyorFamily;
import engine.interfaces.Robot;


public class LeftSideMachineAgent extends Agent implements TReceiver {

	private LeftSidePreMachineConveyorAgent priorconveyor;
	private LeftSidePreMachineConveyorAgent nextpremachineconveyor;
	private LeftSidePreShuttleConveyorAgent nextpreshuttleconveyor;
	private String name;
	private Part[] currentpart;
	private enum MachineEvent {respondtoconveyor, notdonewithpart, passtonextfamily};
	private List<MachineEvent> events;
	private final int machineindex = 1;
	private boolean priorconveyorholding;
	private boolean working;
	private String nameofchannel;
	
	public LeftSideMachineAgent(String n, Transducer t, String ch) {
		super(n, t);
		name = n;
		currentpart = new Part[1];
		currentpart[0] = null;
		events = new ArrayList<MachineEvent>();
		transducer = t;
		nameofchannel = ch;
		if (nameofchannel.equals("BREAKOUT")) {
			transducer.register(this, TChannel.BREAKOUT);
		}
		else if (nameofchannel.equals("MANUAL_BREAKOUT")) {
			transducer.register(this, TChannel.MANUAL_BREAKOUT);
		}
		priorconveyorholding = false;
		working = true;
	}
	
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if (nameofchannel.equals("BREAKOUT")) {
			if(channel == TChannel.BREAKOUT) {
				/* this callback means that loading animation was finished, so time to start workstation animation*/
				if(event == TEvent.WORKSTATION_LOAD_FINISHED) {
					synchronized(events) {
						events.add(MachineEvent.notdonewithpart);
						/* changing states will do executeMachineAction() which calls the do_action gui event.*/
						stateChanged();
					}
				}
				/* this callback means that workstation animation has finished*/
				if(event == TEvent.WORKSTATION_GUI_ACTION_FINISHED) {
					nextpremachineconveyor.msgCanYouAcceptPart();
					/* stateChanged will result in actual transducer event being fired*/
					stateChanged();
				}
				/* this callback means that glass has been released to priorconveyor*/
				if(event == TEvent.WORKSTATION_RELEASE_FINISHED) {
					synchronized(currentpart) {
						nextpremachineconveyor.msgHereIsNewPart(null, currentpart[0]);
						/* now that machine has released the part, time to reset the value for new glass part*/
						currentpart[0] = null;
						if (priorconveyorholding) { 
							priorconveyor.msgMachineCanAcceptPart();
							priorconveyorholding = false;
						}
						stateChanged();
					}
				}
				/* if machine is broken: if it has glass, keep it and do nothing until fixed. If it doesn't have glass, don't say it's ready */
				if (event == TEvent.WORKSTATION_BROKEN) {
					working = false;
					stateChanged();
				}
				if (event == TEvent.WORKSTATION_FIXED) {
					working = true;
					stateChanged();
				}
			}
		}
		else if (nameofchannel.equals("MANUAL_BREAKOUT")) {
			if(channel == TChannel.MANUAL_BREAKOUT) {
				/* this callback means that loading animation was finished, so time to start workstation animation*/
				if(event == TEvent.WORKSTATION_LOAD_FINISHED) {
					synchronized(events) {
						events.add(MachineEvent.notdonewithpart);
						/* changing states will do executeMachineAction() which calls the do_action gui event.*/
						stateChanged();
					}
				}
				/* this callback means that workstation animation has finished*/
				if(event == TEvent.WORKSTATION_GUI_ACTION_FINISHED) {
					nextpreshuttleconveyor.msgCanYouAcceptPart();
					/* stateChanged will result in actual transducer event being fired*/
					stateChanged();
				}
				/* this callback means that glass has been released to priorconveyor*/
				if(event == TEvent.WORKSTATION_RELEASE_FINISHED) {
					synchronized(currentpart) {
						nextpreshuttleconveyor.msgHereIsNewPart(null, currentpart[0]);
						/* now that machine has released the part, time to reset the value for new glass part*/
						currentpart[0] = null;
						if (priorconveyorholding) { 
							priorconveyor.msgMachineCanAcceptPart();
							priorconveyorholding = false;
						}
						stateChanged();
					}
				}
				if (event == TEvent.WORKSTATION_BROKEN) {
					working = false;
					stateChanged();
				}
				if (event == TEvent.WORKSTATION_FIXED) {
					working = true;
					stateChanged();
				}
			}
		}
	}

	/**********************************************************MESSAGES************************************************************/
	
	public void msgConveyorCanAcceptPart() {
		synchronized(events) {
			events.add(MachineEvent.passtonextfamily);
			stateChanged();
		}
	}
	
	public void msgConveyorCannotAcceptPart() {
		//do nothing
	}
	
	/* sent by priorconveyor */
	public void msgHereIsNewPart(ConveyorFamily cf, Part part) {
		synchronized(currentpart) {
			currentpart[0] = part;
//			System.out.println(name + " received a new part in msgHereIsNewPart. ");
			stateChanged();
		}
	}

	public void msgCanYouMachinePart() {
		synchronized(events) {
			int temp = events.size();
			events.add(MachineEvent.respondtoconveyor);
//			System.out.println(name + " received msgCanYouMachinePart. Old events size: " + temp + ". New events size " + events.size());
			stateChanged();
		}
	}
	
	/**********************************************************SCHEDULER************************************************************/

	public boolean pickAndExecuteAnAction() {
		synchronized(events) {
			if (events.size() > 0) {
				if (working) {
					if (events.get(0) == MachineEvent.passtonextfamily) {
						synchronized(currentpart) {
							events.remove(0);
							passPartToNextFamily(currentpart[0]);
						}
					}
					else if (events.get(0) == MachineEvent.notdonewithpart) {
						events.remove(0);
						executeMachineAction();
					}
					else if (events.get(0) == MachineEvent.respondtoconveyor) {
						events.remove(0);
						respondToConveyorPartPassQuery();
					}
				}
			}
			else if (currentpart[0] == null && events.size() == 0 && !priorconveyorholding) {
//				System.out.println(name + " going quiescent");
				return false;
			}
			return false;
		}
	}

	/**********************************************************ACTIONS************************************************************/

	private void respondToConveyorPartPassQuery() {
		synchronized(currentpart) {
			if (currentpart[0] == null && working) {
//				System.out.println(name + " sending msgMachineCanAcceptPart to " + priorconveyor.getName() + " in respondToConveyorPartPassQuery");
				priorconveyor.msgMachineCanAcceptPart();
			}
			else {
				priorconveyor.msgMachineCannotAcceptPart();
				priorconveyorholding = true;
//				events.add(MachineEvent.respondtoconveyor);
				stateChanged();
			}
		}
	}
	
	private void executeMachineAction() {
		synchronized(currentpart) {
			/* if the character at the index in the recipe corresponding to this machine is 1, do action */
			if (currentpart[0].getRecipe().charAt(machineindex) == '1') {
//				System.out.println(name + " firing automatic breakout event workstation do action in executeMachineAction");
				if (nameofchannel.equals("BREAKOUT")) {
					transducer.fireEvent(TChannel.BREAKOUT, TEvent.WORKSTATION_DO_ACTION, null);
				}
				else if (nameofchannel.equals("MANUAL_BREAKOUT")) {
					transducer.fireEvent(TChannel.MANUAL_BREAKOUT, TEvent.WORKSTATION_DO_ACTION, null);
				}
			}
			else {
//				System.out.println(name + " not supposed to do anything with this part in executeMachineAction. Trying to pass it on.");
				if (nameofchannel.equals("BREAKOUT")) {
					nextpremachineconveyor.msgCanYouAcceptPart();
				}
				else if (nameofchannel.equals("MANUAL_BREAKOUT")) {
					nextpreshuttleconveyor.msgCanYouAcceptPart();
				}
			}
			stateChanged();
		}
	}
	
	private void passPartToNextFamily(Part p) {
//		System.out.println(name + " firing automatic breakout event release glass in passPartToNextFamily");
		if (nameofchannel.equals("BREAKOUT")) {
			transducer.fireEvent(TChannel.BREAKOUT, TEvent.WORKSTATION_RELEASE_GLASS, null);
		}
		else if (nameofchannel.equals("MANUAL_BREAKOUT")) {
			transducer.fireEvent(TChannel.MANUAL_BREAKOUT, TEvent.WORKSTATION_RELEASE_GLASS, null);
		}
		stateChanged();
	}
	
	/**********************************************************EXTRA************************************************************/
	
	public boolean isCurrentPartEmpty() {
		if (currentpart[0] == null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void setPriorConveyorAgent(LeftSidePreMachineConveyorAgent  input) {
		priorconveyor = input;
	}
	
	public void setNextPreMachineConveyorAgent(LeftSidePreMachineConveyorAgent input) {
		nextpremachineconveyor = input;
	}
	
	public void setNextPreShuttleConveyorAgent(LeftSidePreShuttleConveyorAgent input) {
		nextpreshuttleconveyor = input;
	}
	
	public String getName() {
		return name;
	}
	
	/***********************************************************UNUSED METHODS*****************************************************/
	
	public void msgRobotPartReceived(Robot robot) {
		
	}

	public void msgPartDone(Robot robot) {
		
	}
	
	public void msgConveyorPartReceived(ConveyorFamily cf) {
		
	}
}
