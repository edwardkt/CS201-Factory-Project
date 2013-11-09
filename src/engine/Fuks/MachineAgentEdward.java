package engine.Fuks;

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
 * Machine agent online station
 * 
 * @author Edward Fuks
 * 
 */
public class MachineAgentEdward extends Agent implements Machine, TReceiver {

	// -----------------------------Data

	private enum  MyOtherState {broken,working};
	
	MyOtherState otherState;

	// only used for debigging printing
	private boolean debug_mode;

	private Integer args[] = new Integer[1];
	// index of the machine
	private int myIndex;
	// name of the machine
	private String name;

	private Transducer myTransducer;

	private enum AgentState {
		busy, ready,
	};

	private enum MyState {
		loadFinished, releaseFinished, actionFinished, noAction, waiting, checkRecipe, doAction, Broken
	};

	private MyState myState;

	// Conveyor Family from the right
	private ConveyorFamily frontCF;

	// conveyor from the right
	private Conveyor conveyorBack;

	// conveyor Family from the right
	private ConveyorFamily myCF;

	private AgentState frontCFState;

	// part on the machine
	private Part myPart;

	// channel
	private TChannel myChannel;

	private int myRecipe;

	//---------------------------- constructors
	/**
	 * 
	 * @param index
	 * @param name
	 * @param transducer
	 * @param channel
	 * @param recipe
	 */
	public MachineAgentEdward(int index, String name, Transducer transducer,
			TChannel channel, int recipe) {
		super(name, transducer);
		myPart = null;

		this.name = name;
		myState = MyState.noAction;

		frontCFState = AgentState.busy;

		myTransducer = transducer;

		myChannel = channel;
		myTransducer.register(this, channel);
		args[0] = myIndex;

		debug_mode = false;

		myRecipe = recipe;
		
		otherState=MyOtherState.working;

	}

	//-------------------------- Constractors and setters
	public void setMyConveyorFamily(ConveyorFamily cf) {
		myCF = cf;

	}

	public void setconveyor(Conveyor conveyor) {
		conveyorBack = conveyor;
	}

	public void setRightConveyorFamily(ConveyorFamily frontCF) {
		this.frontCF = frontCF;
	}

	// ---------------------------messages
	/**
	 * the machine gets this message when the front CF gets the part
	 */
	public void conveyorPartReceived(ConveyorFamily frontCF) {
		myPart = null;
		myState = MyState.noAction;
		this.frontCFState = AgentState.busy;
		stateChanged();
		print("msg: the CF in front of me received the part");
	}

	/**
	 * the machine gets the message from the front CF
	 * 
	 * @Override
	 */
	public void iAmReady() {
		this.frontCFState = AgentState.ready;
		print("msg: the CF in front of me says that it is ready");
		stateChanged();

	}

	/**
	 * the machine needs to treat the part
	 */
	private void msgDoAction() {
		

		this.myState = MyState.doAction;
		print("msg: asked to doAction");
	}

	
	
	@Override
	public void machinePart(Part part) {
		myState = MyState.waiting;
		myPart = part;

		print("msg: my  conveyor asked to machine part "+part.toString()+"-waiting till the loading finishes");

	}

	/**
	 * stopping the agent thread
	 */
	@Override
	public void stopThread() {
		super.stopAgent();

	}

	/**
	 * starting the agent thread
	 */
	@Override
	public void startThread() {

		super.startThread();

	}

	
	

	

	// -----------------------------scheduler
	@Override
	public boolean pickAndExecuteAnAction() {
		if (myState == MyState.Broken) {
			notifyMachineBreaks();

			return true;
		}

		if (myState == MyState.actionFinished
				&& frontCFState == AgentState.ready && otherState==MyOtherState.working) {
			releasePart();
			return true;
		}

		else if (myState == MyState.releaseFinished && otherState==MyOtherState.working) {
			myState = MyState.waiting;
			return true;
		} else if (myState == MyState.loadFinished && otherState==MyOtherState.working) {
			doCheckRecipe();
			return true;
		} else if (myState == MyState.doAction && otherState==MyOtherState.working) {
			doAction();
			return true;
		} else if (myState == MyState.noAction && otherState==MyOtherState.working) {
			notifyReady();
			return true;
		}
		return false;
	}

	
	
	//------------------Actions
	
	
	private void notifyMachineBreaks() {
		conveyorBack.iAmBroken();
		

	}

	private void doCheckRecipe() {

		if (myPart.getRecipe().charAt(myRecipe) == '1') {
			myState = MyState.waiting;
			this.msgDoAction();

		} else {
			myState = MyState.actionFinished;

			stateChanged();
		}
		print("act: I do check recipe");

	}

	/**
	 * the machine notifies the conveyor from the back that the machine is ready
	 */
	private void notifyReady() {
		this.conveyorBack.iAmReady();
		myState = MyState.waiting;
		print("act: I am notifying the conveyor ready");

	}

	/**
	 * the machine's scheduler calls this action to treat the part
	 */
	private void doAction() {
		transducer.fireEvent(myChannel, TEvent.WORKSTATION_DO_ACTION, args);
		myState = MyState.waiting;

		print("act: I am doing action");

	}

	/**
	 * used for debugging
	 * 
	 * @param string
	 */
	private void print(String string) {
		if (debug_mode)
			System.out.println(this.name + " " + string);

	}
	
	private void printErr(String string) {
		if (debug_mode)
			System.err.println(this.name + " " + string);

	}

	/**
	 * the scheduler calls this action to release the part
	 */
	private void releasePart() {
		transducer.fireEvent(myChannel, TEvent.WORKSTATION_RELEASE_GLASS, args);
		myState = MyState.waiting;
		this.frontCF.msgHereIsNewPart(myCF, myPart);

		print("act: I  am releasing the part");
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {

		if (channel == myChannel && event == TEvent.WORKSTATION_LOAD_FINISHED) {
			if (myPart != null) {
				myState = MyState.loadFinished;

				conveyorBack.conveyorPartReceived(myCF);
				stateChanged();
				print("msg: the animation says load finishesd and I am notifying my conveyor that part received");
			} else {
				System.err
						.println(name
								+ " the error has occured- I am staying in noAction state-the front"
								+ " sensor of the conveyor is broken");
				myState = MyState.waiting;
				conveyorBack.iAmBroken();

			}
		}

		else if (channel == myChannel
				&& event == TEvent.WORKSTATION_GUI_ACTION_FINISHED) {
			myState = MyState.actionFinished;

			stateChanged();
			print("msg: the animation says work finished");
		} else if (channel == myChannel
				&& event == TEvent.WORKSTATION_RELEASE_FINISHED) {

			myState = MyState.releaseFinished;
			stateChanged();
			print("msg the animation says release finished");
		}
		
		else if (channel == myChannel
				&& event == TEvent.WORKSTATION_BREAK_GLASS) {
			//this.msgBreakGlass();
		}
		else if (channel == myChannel
				&& event == TEvent.WORKSTATION_BROKEN) {
			otherState = MyOtherState.broken;
			printErr("workstation is broken");
			stateChanged();
			
		}
		
		else if (channel == myChannel
				&& event == TEvent.WORKSTATION_FIXED) {
			otherState=MyOtherState.working;
			printErr("workstation is fixed");
			stateChanged();
			
		}

	}

	/**
	 * used for debugging
	 */
	@Override
	public void setPrintEnabled() {
		debug_mode = true;

	}

	
}
