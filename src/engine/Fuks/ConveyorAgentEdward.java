package engine.Fuks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

import engine.Fuks.interfaces.Conveyor;
import engine.Fuks.interfaces.Machine;
import engine.agent.Agent;
import engine.agent.Part;

import engine.interfaces.ConveyorFamily;

/**
 * this is COnveyor agent. It is located in the COnveyour fAMILY AND IS
 * CONNECTED WITH PopUp FROM THE right and with COnveyorFamily from the left
 * 
 * @author Edward Fuks
 * 
 */
public class ConveyorAgentEdward extends Agent implements Conveyor, TReceiver {
	// ------------------------DATA
	private int myIndex;
	private String name;
	private Transducer myTransducer;

	private ConveyorFamily cFBack; // COnveyorFamily from the left
	// ------------------only used in the ConveyorSimpleFamily-------
	private ConveyorFamily cFFront; // COnveyorFamily from the left
	// -----------------------
	private ConveyorFamily myCF; // my ConveorFamily
	private Machine myMachine; // machine from the right

	// states of conveyor
	// broken state used when the sensor is broken
	// brokenConveyor is used when the entire conveyor is broken
	private boolean brokenConveyor;

	private enum MyState {
		broken, working
	};

	// convayoe can be broken or working normally
	private MyState myState;

	private enum MachineState {
		busy, ready, broken
	};

	private boolean debug_mode;

	private MachineState machineRightState;

	enum SensorState {
		pressed, released, BROKEN
	};

	SensorState leftSensorState;
	SensorState rightSensorState;

	private boolean moving; // moving conveyor or not moving

	private List<Part> partsOnMe = Collections
			.synchronizedList(new ArrayList<Part>()); // parts on the conveyor

	// keeps track of how many parts are on the conveyor.
	private int numberOfPartsAgent;
	private int numberOfPartsSensor;
	// when the machine tells that it received the part (load finished)
	boolean receivedMsgPartReceived;

	// used to remember whever notified the back CF that this CF received the
	// part
	// is false when received the message hereISNewPart
	// and true when notifyCFReaidy is called
	boolean notifiedBackCF;

	// -----------------------------Constructors
	public ConveyorAgentEdward(int index, String name, Transducer transducer) {
		super(name, transducer);

		moving = false;

		partsOnMe.clear();
		leftSensorState = SensorState.released;
		rightSensorState = SensorState.released;

		myTransducer = transducer;

		myIndex = index;

		myTransducer.register(this, TChannel.SENSOR);
		myTransducer.register(this, TChannel.CONVEYOR);

		this.name = name;

		debug_mode = false;

		numberOfPartsAgent = 0;
		numberOfPartsSensor = 0;

		myState = MyState.working;

		brokenConveyor = false;

		receivedMsgPartReceived = true;

		// didn't notify back CF yet
		notifiedBackCF = false;

	}

	@Override
	public void stopThread() {
		super.stopAgent();

	}

	@Override
	public void unexpectedStopConveyor() {

	}

	public void setConveyorFamily(ConveyorFamily conveyorFamily) {
		cFBack = conveyorFamily;
		cFBack.msgConveyorReady(myCF);

	}

	@Override
	public void setFrontCF(ConveyorFamily frontCF) {
		this.cFFront = frontCF;

	}

	@Override
	public void setRightMachine(Machine machine) {
		myMachine = machine;

	}

	public void setMyConveyorFamily(ConveyorFamily conveyorFamily) {
		myCF = conveyorFamily;

	}

	// -------------------------messages

	/**
	 * the machine notifies us that it received the part
	 * 
	 */
	@Override
	public void conveyorPartReceived(ConveyorFamily cf) {
		synchronized (partsOnMe) {
			partsOnMe.remove(0);
			print("msg: I received message part received now I have"
					+ partsOnMe.size());
			machineRightState = MachineState.busy;
			receivedMsgPartReceived = true;
			stateChanged();

		}
	}

	/**
	 * the machine sends this message when it is ready
	 */
	@Override
	public void iAmReady() {
		machineRightState = MachineState.ready;
		stateChanged();
		print("msg: the my machine says that it is ready");

	}

	/**
	 * the Conveyor Family sends the part from the left to us
	 */
	@Override
	public void hereIsNewPart(Part part) {
		synchronized (partsOnMe) {
			partsOnMe.add(part);
			print("msg: the back Conveyor Family passes me the part "
					+ part.toString());
			numberOfPartsAgent++;

		}

	}

	/**
	 * for non-normative stopping of the conveyor
	 */
	@Override
	public void msgConveyorStopping() {
		// TODO Auto-generated method stub

	}

	/**
	 * this message is sent by the front machine regarding it's failure conveyor
	 * should not send any parts to it untill it notifies about its readiness
	 */
	@Override
	public void iAmBroken() {
		machineRightState = MachineState.busy;
		System.err.println(name + " the machine tells that it is broken");
		stateChanged();

	}

	/**
	 * the gui notified the conveyor that it is fixed trhough the transducer
	 */
	private void fixConveyor() {
		myState = MyState.working;
		stateChanged();

	}

	/**
	 * the gui notified the conveyor that it is broken trhough the transducer
	 */
	private void conveyorIsBroken() {
		myState = MyState.broken;
		stateChanged();

	}

	// ------------------------------- Scheduler
	@Override
	public boolean pickAndExecuteAnAction() {

		if (myState == MyState.broken) {
			stopConveyor();
			return false;
		}

		else if (leftSensorState == SensorState.pressed && !moving
				&& rightSensorState == SensorState.released
				&& myState == MyState.working) {
			startConveyor();
			return false;
		} else if (rightSensorState == SensorState.pressed
				&& machineRightState == MachineState.busy && moving
				&& myState == MyState.working) {
			stopConveyor();
			return false;
		} else if (!partsOnMe.isEmpty()
				&& rightSensorState == SensorState.released && !moving
				&& myState == MyState.working) {
			startConveyor();
			return false;
		} else if (rightSensorState == SensorState.pressed
				&& machineRightState == MachineState.ready && !moving
				&& myState == MyState.working) {
			startConveyor();
			this.passThePartTotheMachine();
			return false;
		} else if (rightSensorState == SensorState.pressed
				&& machineRightState == MachineState.ready && moving
				&& receivedMsgPartReceived && myState == MyState.working) {

			this.passThePartTotheMachine();
			return false;
		} else if (rightSensorState == SensorState.pressed
				&& machineRightState == MachineState.busy && moving
				&& receivedMsgPartReceived && myState == MyState.working) {
			stopConveyor();
			return false;
		} else if (partsOnMe.isEmpty() && moving && myState == MyState.working) {
			stopConveyor();
			return false;
		}
		printErr(" no action in scheduler has been called");
		return false;

	}

	private void reportConveyorBroken() {
		myState = MyState.broken;

	}

	// ---------------------------------Actions
	/**
	 * the function stops the coneyor
	 */

	private void stopConveyor() {
		moving = false;
		Integer args[] = new Integer[1];
		args[0] = myIndex;
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, args);
		System.out.println(name+"  conveyor quiescent");

	}

	/**
	 * the conveyor passes the first part in the list to the popUp
	 */
	private void passThePartTotheMachine() {
		synchronized (partsOnMe) {
			if (myMachine != null) {
				myMachine.machinePart(partsOnMe.get(0));
				print(" act: passing the part to machine "
						+ partsOnMe.get(0).toString() + " total num  i have "
						+ partsOnMe.size());

				// will become true when the CF in front will notify that it
				// received the part
				this.receivedMsgPartReceived = false;

			} else {
				this.cFFront.msgHereIsNewPart(myCF, partsOnMe.get(0));

				print(" act: passing the part to conveyor "
						+ partsOnMe.get(0).toString() + " total num  i have "
						+ partsOnMe.size());

				// will become true when the CF in front will notify that it
				// received the part
				this.receivedMsgPartReceived = false;

			}
		}// end synchronized

	}

	/**
	 * the function starts the conveyor
	 */
	private void startConveyor() {
		moving = true;
		Integer args[] = new Integer[1];
		args[0] = myIndex;
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args);
		print("act : conveyor started");

	}

	/**
	 * the conveyor tels the CF from the left that it can accept the parts.
	 */

	/**
	 * used to output the messages in debugging mode
	 * 
	 * @param string
	 */
	private void print(String string) {
		if (debug_mode)
			System.out.println(this.name + ":" + string);
	}

	/**
	 * by calling this public function you can see print statements when t he
	 * agent runs
	 */
	@Override
	public void setPrintEnabled() {
		debug_mode = true;

	}

	/**
	 * used to output the error messages in debugging mode
	 * 
	 * @param string
	 */
	private void printErr(String string) {
		if (debug_mode)
			System.err.println(this.name + ":" + string);

	}

	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if ((Integer) args[0] / 2 == myIndex && channel == TChannel.SENSOR) {

			if (channel == TChannel.SENSOR
					&& event == TEvent.SENSOR_GUI_RELEASED) {
				if ((Integer) args[0] % 2 == 0) {

					this.leftSensorState = SensorState.released;
					printErr("msg: the left sensor is released");

					stateChanged();
					cFBack.msgConveyorReady(myCF);
					printErr("sending message that I am ready");

					print(name
							+ " notifying Back CF that my CF is ready to accept the part");
				} else if ((Integer) args[0] % 2 == 1) {

					this.rightSensorState = SensorState.released;

					print("msg: the right sensor is released");

					stateChanged();

				}

			} else if (channel == TChannel.SENSOR
					&& event == TEvent.SENSOR_GUI_PRESSED) {
				if ((Integer) args[0] % 2 == 0) {

					this.leftSensorState = SensorState.pressed;
					print("msg: the left sensor is pressed and the number of parts is "
							+ partsOnMe.size());

					cFBack.msgConveyorPartReceived(myCF); // telling that CF
					// received the part
					// when the sensor
					// is released

					stateChanged();
				} else if ((Integer) args[0] % 2 == 1) {

					this.rightSensorState = SensorState.pressed;
					print("msg: the right sensor is  pressed ");
					stateChanged();
				}
			}

		} else if ((Integer) args[0] == myIndex && channel == TChannel.CONVEYOR) {
			if (channel == TChannel.CONVEYOR && event == TEvent.CONVEYOR_BROKEN) {
				this.conveyorIsBroken();
				stateChanged();
				System.err.println(name + " conveyor is broken");
			} else if (channel == TChannel.CONVEYOR
					&& event == TEvent.CONVEYOR_FIXED) {
				this.fixConveyor();
				stateChanged();
				System.err.println(name + " conveyor is fixed");
			}
		}
	}

}
