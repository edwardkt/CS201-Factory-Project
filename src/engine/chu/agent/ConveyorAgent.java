package engine.chu.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import engine.agent.Agent;
import engine.agent.Part;

import engine.interfaces.*;

import shared.enums.SensorPosition;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import engine.agent.*;
import engine.chu.interfaces.Conveyor;
import engine.chu.interfaces.Machine;
import engine.chu.interfaces.Sensor;

/**
 * This is an agent class that controls conveyor 0
 * in the glassline factory
 * 
 * @author Christina
 *
 */

public class ConveyorAgent extends Agent implements Conveyor{

	public enum PartStatus{notOnConveyor, passedSensorOne,toldStart, passedSensorTwo, approved, done};
	
	public List<PartTracker> partList = Collections.synchronizedList(new ArrayList<PartTracker>());
	
	public enum ConveyorStatus{run, stop};
	
	public int conveyorIndex;
	
	private boolean machineReady;
	private boolean machineReceived = false;
	private ConveyorStatus cStatus;
	private boolean sensor1free;
	
	public Transducer transducer;
	
	public ConveyorFamily cf;
	
	//Name of Conveyer
	private String name;
	
	//agent interaction
	//public Sensor s1;
	//public Sensor s2;
	public Machine machine;
	
	boolean broken=false;
	boolean sensorBroken=false;
	//Semaphore askMachine;
	
	int sensorCount=0;
	
	/**
	 * Constructor for ConveyorAgent
	 * 
	 * @param cf
	 * 			parent ConveyorFamilyContainer
	 * @param name
	 * 			name of agent
	 * @param transducer
	 * 			transducer that allows communication with the GUI
	 * @param conveyorIndex
	 * 			the index of the conveyor within the factory
	 */
	
	
	public ConveyorAgent(ConveyorFamily cf, String name, Transducer transducer, int conveyorIndex){
		super(name);
		this.name = name;
		this.transducer=transducer;
		this.cf=cf;
		this.conveyorIndex = conveyorIndex;
		cStatus=ConveyorStatus.run;
		machineReady=true;
		//askMachine= new Semaphore(0);
		sensor1free=true;
	
		
	}
	
	
	//****************MESSAGES***********************
	
	
	/*
	public void msgPartBeginningConveyor(Part part) {
		synchronized(partList){
		partList.add(new PartTracker(part));
		}
		
		Integer[] args = new Integer[1];
		args[0] = (Integer)conveyorIndex;
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args);
		//System.out.println("New "+part.getBarcode()+ " was added to conveyor");
		stateChanged();
	}
	
	public void msgPartEndingConveyor() {
	
		partList.get(0).status=PartStatus.passedSensorTwo;
		//System.out.println("Part reached end of conveyor");
		stateChanged();	
	}
	*/
	/**
	 * This message is gives the conveyor a new part
	 */

	public void msgHereIsNewPart(Part part)
	{
		//System.out.println("C0 here is new part: "+ part.getRecipe() + " recieved");
			partList.add(new PartTracker(part));
			Integer[] args = new Integer[1];
			args[0] = 0;
		//stateChanged();
	}
	
	

	
	public void msgMachineReady(){
		machineReady=true;
		machineReceived=false;
		stateChanged();
	}
	
	
	public void msgMachineNotReady(){
		machineReady=false;
		//stopConveyor();
		stateChanged();
	}
	
	public void msgMachineReceivedPart(){
		machineReceived=true;
		stateChanged();
	}
	
	
	//*****************SCHEDULER********************//
	public boolean pickAndExecuteAnAction() {
		/*if(partList.isEmpty()==false)
			System.out.println("Schedular Top recipe " + partList.get(0).part.getRecipe());
		*/
		//System.out.println("in schedular");
		if(broken==true || sensorBroken==true)
		{
			stopConveyor();
			return false;
		}
		
		
		
		if(cStatus==ConveyorStatus.run){
			
			synchronized(partList) { 
				for(PartTracker p:partList){
		    		if(p.status==PartStatus.passedSensorTwo && machineReady==true){
		    					sendPartToMachine(partList.get(0).part);
		    				return true;
		    		}
		    		else if(p.status==PartStatus.passedSensorTwo && machineReady==false){
		    			stopConveyor();
		    			return true;
		    		}
		    	}
	
			}
			
			synchronized(partList) { 
				for(PartTracker p:partList){
		    		if(p.status==PartStatus.passedSensorOne){
		    				startConveyor();
		    				
		    				return true;
		    		}
				}
			}
			
			if(partList.isEmpty() && machineReceived==true)
			{
				stopConveyor();
				return true;
			}
				
		}
		if(cStatus==ConveyorStatus.stop)
		{
			if(!partList.isEmpty())
			{
				if(machineReady==true)
				{
					startConveyor();
					return true;
				}
				else if(machineReady==false)
				{
					stopConveyor();
					return true;
				}
			}
			if(partList.isEmpty())
			{
				if(machineReady==true)
					System.out.println("Conveyor 0 is going quiescent");
				return false;
			}
			
		}

		/*else if(cStatus==ConveyorStatus.stop && !partList.isEmpty() && machineReady==true)
		{
			
			startConveyor();
			return true;
		}
		else if(partList.isEmpty() || machineReady==false)
		{
			stopConveyor();
			return true;
		}
		
		if(broken==false && machineReady==true && !partList.isEmpty())
		{
			//System.out.println("checking if this works");
			startConveyor();
			return false;
		}*/
		
		return false;
	}



	/*
	private void checkMachine(PartTracker temp) {
		//System.out.println("checkMachine");
		machine.msgIsMachineEmpty();
		try {
			askMachine.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(machineReady==true)
		{
			temp.status=PartStatus.approved;
			machineReady=false;
		}
		else if(machineReady==false)
			stopConveyor();
		stateChanged();
	}
	*/
	

	private void stopConveyor() {
		if(cStatus==ConveyorStatus.run)
		{	//System.out.println("stopConveyor");
			cStatus=ConveyorStatus.stop;
		
		Integer[] args = new Integer[1];
		args[0] = (Integer)conveyorIndex;
		transducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, args );
		stateChanged();
		}
	}
	
	private void startConveyor() {
		//System.out.println("startConveyor");
		boolean needStateChanged=false;
		if(cStatus==ConveyorStatus.stop)
		{
			needStateChanged=true;
			cStatus=ConveyorStatus.run;
		}
		
		Integer[] args = new Integer[1];
		args[0] = (Integer)conveyorIndex;
		
		synchronized(partList) { 
			for(int i=0; i<partList.size(); i++) {
				if(partList.get(i).status==PartStatus.passedSensorOne)
				{
					partList.get(i).status=PartStatus.toldStart;
					transducer.fireEvent( TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args );
					//stateChanged();
					break;
				}
			}
		}
		if(needStateChanged==true)
			stateChanged();
		
	}


	private void sendPartToMachine(Part part) {
		//System.out.println("Sending part to Machine");	
		//cStatus= ConveyorStatus.run;
		Integer[] args = new Integer[1];
		args[0] = (Integer)conveyorIndex;
		//System.out.println("sending recipe: " + part.getRecipe() + " to machine");
		machine.msgHereIsPart(part);
		machineReady=false;
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START , args);
		
		/*for(int i=0; i<partList.size(); i++)   //////////////if this gets used again, it needs to be synchronized
		{
			if(partList.get(i).part==part)
			{
				synchronized(partList){
				partList.remove(i);
				}
			}
		}*/
		partList.remove(0);
		stateChanged();
	}

	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if(channel==TChannel.GUI && args[0].equals(conveyorIndex*2+1))
		{
			
			if(event==TEvent.SENSOR_BROKEN)
			{
				System.out.println("Sensor was broken");
				sensorBroken=true;
				
				stateChanged();
				
			}
		}
			if(channel==TChannel.SENSOR && event==TEvent.SENSOR_FIXED && args[0].equals(conveyorIndex*2+1) )
			{
				Object[] conveyorArg = new Object[1];
				conveyorArg[0] = (Integer)conveyorIndex;
				
				System.out.println("Sensor 1 was fixed");
				
				if(!partList.isEmpty())
				{
					partList.remove(0);
					//System.out.println(partList.get(0).part.getRecipe());
				}
				
				sensorBroken=false;
				if(cStatus==ConveyorStatus.stop)
				{
					cStatus=ConveyorStatus.run;
				}
				transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START , conveyorArg);
				stateChanged();
			}
		
		
		if(channel==TChannel.CONVEYOR &&  event == TEvent.CONVEYOR_FIXED && args[0].equals(conveyorIndex))
		{
			//System.out.println("conveyor is fixed");
			broken=false;
			if(cStatus==ConveyorStatus.stop)
			{
				cStatus=ConveyorStatus.run;
			}
			transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START , args);
			stateChanged();
		}
		
		if(channel==TChannel.CONVEYOR &&  event == TEvent.CONVEYOR_BROKEN && args[0].equals(conveyorIndex))
		{
			//System.out.println("conveyor is broken");
			broken=true;
			stateChanged();
		}
		
		if(channel==TChannel.SENSOR && args[0].equals(conveyorIndex*2))
		{
			
			if(event==TEvent.SENSOR_GUI_PRESSED && args[0].equals(conveyorIndex*2)){
				//stopConveyor()
				sensor1free=false;
				sensorCount++;
				for(int i=0; i<partList.size(); i++)
				{
					//System.out.println("Recipe: " + partList.get(i).part.getRecipe());
					if(partList.get(i).status==PartStatus.notOnConveyor)
					{
						
						//System.out.println("part past sensor 1");
						partList.get(i).status=PartStatus.passedSensorOne;
						//System.out.println("Recipe: " + partList.get(i).part.getRecipe());
						stateChanged();
						break;
					}
				}
				
				
			}
			else if(event==TEvent.SENSOR_GUI_RELEASED && args[0].equals(conveyorIndex*2)){
				sensor1free=true;
				cf.msgBinConveyorReady();
				stateChanged();
			}
			
		}
		else{ 
			
			if (channel ==TChannel.SENSOR && args[0].equals(conveyorIndex*2+1))
			{
				if(event==TEvent.SENSOR_GUI_PRESSED){
					sensorCount--;
					synchronized(partList) { 
						for(int i=0; i<partList.size(); i++) {
							if(partList.get(i).status==PartStatus.toldStart)
							{
								//System.out.println("part past sensor 2");
								partList.get(i).status=PartStatus.passedSensorTwo;
								//System.out.println("Recipe: " + partList.get(i).part.getRecipe());
								//startConveyor();
								stateChanged();
								break;
							}
						}
					}
				}
				
				
			}
		}
		
		

	}
	
	/*public void setSensors(Sensor s1, Sensor s2){
		this.s1=s1;
		this.s2=s2;
	}*/
	
	
	public void setMachine(Machine machine){
		this.machine=machine;
	}
	
	public void setTransducer(Transducer transducer){
		this.transducer=transducer;
		this.transducer.register(this, TChannel.SENSOR);
		this.transducer.register(this, TChannel.CONVEYOR);
		this.transducer.register(this, TChannel.GUI);
		//System.out.println("transducer registered");
	}
	
    public String getName(){
        return name;
    }


	@Override
	public void msgPartBeginningConveyor(Part part) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void msgPopUpReady() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void msgPopUpNotReady() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void msgPartEndingConveyor() {
		// TODO Auto-generated method stub
		
	}


	public void msgIsConveyorReady() {
		if(sensor1free && cStatus==ConveyorStatus.run)
		{
			cf.msgBinConveyorReady();
		}
		else
		{
			cf.msgBinConveyorStopping();
		}
		
	}
   



}
