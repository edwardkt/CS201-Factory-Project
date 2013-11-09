package engine.tam.ShuttleFamily;

import engine.interfaces.ConveyorFamily;
import engine.agent.Part;
import engine.agent.Agent;
import engine.tam.ConveyorFamily.ConveyorAgent.ConveyorSensorIn;
import engine.tam.ConveyorFamily.ConveyorAgent.ConveyorSensorOut;
import engine.tam.ConveyorFamily.ConveyorAgent.MachineStatus;
import engine.tam.interfaces.*;

import java.util.ArrayList;
import java.util.List;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

public class ConveyorAgent extends Agent implements Conveyor, TReceiver {
	public enum ConveyorStatus {ON, OFF}; // shows status of conveyor

	public enum NextConveyorStatus {FREE, BUSY, STALLED};

	public enum ConveyorSensorIn {ON, OFF, CHANGE};

	public enum ConveyorSensorOut {ON, OFF,STALLED};

	public ConveyorStatus status;
	public NextConveyorStatus nextconveyor;
	public ConveyorSensorIn sensor1;
	public ConveyorSensorOut sensor2;

	//public enum Sta	tus 
	//{READY, NOTHINGTODO, STALLED};

	//public Status factorystatus;
	private WorkStation station;
	private ConveyorFamily family;
	private ConveyorFamily backCF;
	private ConveyorFamily NextCF;
	String name;
	public List<Part> parts = new ArrayList<Part>(); // list of all parts
	private Transducer transducer;
	private int ConveyorNumber;
	private boolean ConveyorBroken;
	
	public ConveyorAgent(String name, Transducer transducer, int ConveyorNumber) {
		super();
		this.name = name;
		this.transducer = transducer;
		this.transducer.register(this, TChannel.SENSOR);
		this.transducer.register(this, TChannel.CONVEYOR);
		this.ConveyorNumber = ConveyorNumber;
		this.ConveyorBroken = false;
		// this.family = family;
		//this.factorystatus = Status.NOTHINGTODO;
		this.nextconveyor = NextConveyorStatus.BUSY;
		this.status = ConveyorStatus.OFF;
		this.sensor1 = ConveyorSensorIn.OFF;
		this.sensor2 = ConveyorSensorOut.OFF;
	}

	// MESSAGES

	// conveyorfamily gives a new part to the conveyor
	@Override
	public void msgHereIsNewPart(Part part) {
		//System.out.println(this.getName() + ConveyorNumber + " Received Part from previous conveyor");
		this.parts.add(part);
		//System.out.println(this.getName() + ConveyorNumber + " number of parts on conveyor: " + parts.size());
		stateChanged();
	}
	
	//machine received part
	public void msgPartReceivedFromPreviousCF()
	{
		//System.out.println(this.getName() + ConveyorNumber + "NextCF received part from Conveyor");
		//this.nextconveyor = NextConveyorStatus.BUSY;
		parts.remove(0);
		//System.out.println(this.getName() + ConveyorNumber + " number of parts on conveyor: " + parts.size());
	}
	
	//when machine finished part, change state of machine
	public void msgImFree()
	{

	}
	
	public void NextConveyorBusy()
	{
		this.nextconveyor = NextConveyorStatus.BUSY;
		stateChanged();
	}
	
	public void msgNextConveyorReadyToTakePart()
	{
		this.nextconveyor = NextConveyorStatus.FREE;
		stateChanged();
	}
	
	public void msgMachineFree()
	{
	
	}

	// left sensor pressed
	public void leftSensorPushed() {
		// print("SensorIn Pressed");
		sensor1 = ConveyorSensorIn.ON;
		backCF.msgConveyorPartReceived(family);
		//backCF.msgConveyorStopping();
		stateChanged();
	}

	// right sensor pressed
	public void rightSensorPushed() {
		// print("SensorOut Pressed");
		sensor2 = ConveyorSensorOut.ON;
		stateChanged();
	}

	public void leftSensorReleased() {
		// print("SensorIn released");
		sensor1 = ConveyorSensorIn.OFF;
		stateChanged();
	}

	public void rightSensorReleased() {
		// print("SensorOut released");
		sensor2 = ConveyorSensorOut.OFF;
		stateChanged();
	}
	
	//when conveyor is broken
	public void ConveyorBroken()
	{
		ConveyorBroken = true;
		stateChanged();
	}
	
	//when conveyor is fixed
	public void ConveyorFixed()
	{
		ConveyorBroken = false;
		//this.nextconveyor = NextConveyorStatus.FREE;
		//this.status = ConveyorStatus.ON;
		//System.out.println("SENSORIN IS " + sensor1 + " AND SENSOROUT IS " + sensor2);
		stateChanged();
	}

	// SCHEDULER!
	public boolean pickAndExecuteAnAction() {
		if(ConveyorBroken)
		{
			StallConveyor();
			return false;
		}
		/*// when the in sensor is off
		if (sensor1 == ConveyorSensorIn.OFF) //&& parts.size() <= 2) 
		{
			TellOtherCFImReady();
			return true;
		}*/
		// when the entry sensor is pressed and the out sensor is not and the
		// conveyor is off
		else if ((sensor1 == ConveyorSensorIn.ON) && !ConveyorBroken && (status == ConveyorStatus.OFF) && ((sensor2 == ConveyorSensorOut.OFF) || (sensor2 == ConveyorSensorOut.STALLED))) 
		{
			StartConveyor();
			return false;
		}
		// when the out sensor isvg on and the machine is busy
		else if ((sensor2 == ConveyorSensorOut.ON) && (nextconveyor == NextConveyorStatus.BUSY)) //&& (status == ConveyorStatus.ON)) 
		//if((parts.size() == 0) || (machinestatus == MachineStatus.BUSY))
		{
			StallConveyor();
			return false;
		}
		// when the out sensor is on and the machine is free
		else if (((sensor2 == ConveyorSensorOut.ON) && (nextconveyor == NextConveyorStatus.FREE))) 
		{
			GivePartToNextConveyor();
			return false;
		}
		else if(parts.size() == 0 && status == ConveyorStatus.ON)
		{
			ShutFactory();
			return false;
		}
		/*else if((sensor1 == ConveyorSensorIn.ON) && (sensor2 == ConveyorSensorOut.ON) && (machinestatus == MachineStatus.FREE) && (status == ConveyorStatus.ON)) 
		{ 
			GivePartToMachine(); 
			return true; 
		}*/
		// when none of the sensors are on
		
		
		/*if((sensor1 == ConveyorSensorIn.ON) && (sensor2 == ConveyorSensorOut.ON) && (machinestatus == MachineStatus.FREE) && (status == ConveyorStatus.OFF))
		{
			StartConveyor();
			return true;
		}*/
		 
		return false;
	}

	// ACTIONS

	// conveyor is turned on
	private void StartConveyor() {
		//System.out.println(this.getName() + ConveyorNumber + " conveyor is on and part can go to this conveyor");

		Object[] args = { ConveyorNumber };
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args);
		this.status = ConveyorStatus.ON;
		/*if(sensor1 == ConveyorSensorIn.ON)
			sensor1 = ConveyorSensorIn.CHANGE;
		else if(sensor2 == ConveyorSensorOut.ON)
			sensor2 = ConveyorSensorOut.STALLED;*/
		//stateChanged();

	}

	// stalls conveyor
	private void StallConveyor() {
		//System.out.println(this.getName() + ConveyorNumber + " Stalling conveyor");
		Object[] args = { ConveyorNumber };
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, args);
		//this.nextconveyor = NextConveyorStatus.STALLED;
		this.status = ConveyorStatus.OFF;
		//stateChanged();

	}
	
	//conveyors turn off when there aren't parts.
	private void ShutFactory()
	{
		System.out.println("conveyor" + ConveyorNumber + " is shutting off");
		status = ConveyorStatus.OFF;
	}

	/*// sends msg to previous conveyor to say i'm ready
	private void TellOtherCFImReady() 
	{
		//System.out.println(this.getName() + ConveyorNumber + " notify previous conveyor family that i'm ready");
		this.sensor1 = ConveyorSensorIn.CHANGE;
		backCF.msgConveyorReady(backCF);
	}*/

	// sending part to another conveyor
	private void GivePartToNextConveyor() {
		//System.out.println(this.getName() + ConveyorNumber + " conveyor giving part to machine");
		this.nextconveyor = NextConveyorStatus.BUSY;
		this.sensor2 = ConveyorSensorOut.STALLED;
		NextCF.msgHereIsNewPart(NextCF,parts.get(0));	
		if(status == ConveyorStatus.OFF)
			this.StartConveyor();
	}

	// MISC.
	public String getName() {
		return name;
	}

	public void setBackCF(ConveyorFamily cf) {
		this.backCF = cf;
		backCF.msgConveyorReady(family);
	}
	
	public void setNextCF(ConveyorFamily cf)
	{
		this.NextCF = cf;
	}

	public void setStation(WorkStation station) {
		this.station = station;
	}

	public void setFamily(ConveyorShuttleFamily family,
			int ConveyorNumber) {
		this.family = family;
		this.ConveyorNumber = ConveyorNumber;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) 
	{
		
		if(channel == TChannel.CONVEYOR)
		{
			if((Integer) args[0] == ConveyorNumber && event == TEvent.CONVEYOR_BROKEN)
			{
				this.ConveyorBroken();
			}
			else if((Integer) args[0] == ConveyorNumber && event == TEvent.CONVEYOR_FIXED)
			{
				this.ConveyorFixed();
			}
		}
		if ((ConveyorNumber * 2 + 1) == (Integer) args[0] || ConveyorNumber * 2 == (Integer) args[0]) 
		{
			if (channel == TChannel.SENSOR && event == TEvent.SENSOR_GUI_PRESSED)
			{
				if ((Integer) args[0] % 2 == 0)
				{
					this.leftSensorPushed();

				} 
				else if ((Integer) args[0] % 2 == 1) 
				{
					this.rightSensorPushed();
				}
			} 
			else if (channel == TChannel.SENSOR && event == TEvent.SENSOR_GUI_RELEASED)
			{
				if ((Integer) args[0] % 2 == 0) 
				{
					this.leftSensorReleased();
					backCF.msgConveyorReady(family);

				} 
				else if ((Integer) args[0] % 2 == 1)
				{
					this.rightSensorReleased();
				}
			}

		}
	}

	
	public void msgPartReceived(Part p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPartReceived() {
		// TODO Auto-generated method stub
		
	}



}