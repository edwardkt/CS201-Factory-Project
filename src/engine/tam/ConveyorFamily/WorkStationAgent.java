package engine.tam.ConveyorFamily;

import engine.tam.ConveyorFamily.ConveyorFamilyInLineStation;
import engine.agent.Agent;
import engine.tam.interfaces.*;


import java.util.ArrayList;
import java.util.List;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;
import engine.agent.Part;
import engine.interfaces.ConveyorFamily;

public class WorkStationAgent extends Agent implements WorkStation,TReceiver
{
	public enum StationStatus {EMPTY,NOTHINGTODO,PARTONSTATION,NEEDSPROCESSING,NONEEDPROCESSING,PROCESSING,NEEDNOTIFICATION,PREPARETOLOAD,PARTSENT};
	public StationStatus status;
	public enum NextCFState{READY,BUSY};
	public NextCFState stateOfnextCF;
	private Conveyor conveyor;
	private ConveyorFamily family;
	private ConveyorFamily nextCF;
	public List<Part> parts = new ArrayList<Part>(); //list of all parts
	String name;
	private Transducer transducer;
	private TChannel channel;
	private int workStation;
	private int ConveyorNumber;
	private boolean StationBroken;
	public WorkStationAgent(String name,Transducer transducer,TChannel channel,int workStation,int ConveyorNumber)
	{
		super();
		this.name = name;
		this.transducer = transducer;
		this.workStation = workStation;
		this.ConveyorNumber = ConveyorNumber;
		this.transducer.register(this, channel);
		this.channel = channel;
		this.StationBroken = false;
		//this.status = StationStatus.EMPTY;
		this.stateOfnextCF = NextCFState.BUSY;
		this.status = StationStatus.NOTHINGTODO;
	}
	
	//MESSAGES
	//gets part from conveyor and sends msg back to conveyor to verify
	public void msgHereIsPart(Part p)
	{
		this.parts.add(p);
		//System.out.println(this.getName() + ConveyorNumber + " size of parts in workstation: " + this.parts.size());
		stateChanged();
	}
	
	//machine ready
	public void msgImReady()
	{
		stateOfnextCF = NextCFState.READY;
		//System.out.println(this.getName() + ConveyorNumber + " next conveyor is ready");
		stateChanged();
	}
	
	//part received from previous conveyor, so we can now delete the previous part off the list
	public void msgPartReceived()
	{
		//conveyor.msgImFree();
		this.status = StationStatus.NOTHINGTODO;
		this.stateOfnextCF = NextCFState.BUSY;
		parts.remove(0);
		//System.out.println(this.getName() + ConveyorNumber + " remove parts size: " + parts.size());
		//System.out.println("c14 state is: " + stateOfnextCF);
		stateChanged();
		
	}
	
	//part is now on machine and lets conveyor know that he received the part
	public void PartOnMachine() 
	{
		//System.out.println(this.getName() + ConveyorNumber + " Part is on machine");
		//stateOfnextCF = NextCFState.READY;
		status = StationStatus.PARTONSTATION;
		conveyor.msgPartReceived();
		stateChanged();
	}
	
	//preparing to UV LAMP the part
	public void ProcessingPart()
	{
		//System.out.println(this.getName() + ConveyorNumber + " preparing to process part");
		status = StationStatus.PROCESSING;
		stateChanged();
	}
	
	//preparing to load to next CF
	public void PreparingToload()
	{
		//System.out.println(this.getName() + ConveyorNumber + " preparing to load to next CF");
		status = StationStatus.PREPARETOLOAD;
		stateChanged();
	}
	
	//successfully sent the part to the next conveyor
//	This method is unnecessary
//	commented out by Justin
 	public void PartSentToNextConveyor()
	{
/*		//stateOfnextCF = NextCFState.BUSY;
		status = StationStatus.PARTSENT;
		//conveyor.msgMachineFree();
		stateChanged();
*/	} 
	
	//stall the machine
	/*public void StallMachine()
	{
		System.out.println("Machine stalled!");
		stateOfnextCF = NextCFState.BUSY;
	}*/

	//SCHEDULER!
    public boolean pickAndExecuteAnAction() 
    {
    	 if(status == StationStatus.NOTHINGTODO) //&& stateOfnextCF != NextCFState.BUSY)
    	{
    		NotifyConveyorImFree();
    		return false;
    	}
    	 else if(status == StationStatus.PARTONSTATION && !StationBroken)
    	{
    		CheckIfPartNeedsProcessing();
    		return false;
    	}
    	
    	else if(status == StationStatus.PREPARETOLOAD && stateOfnextCF == NextCFState.READY)
    	{
    		UnloadToNextCF();
    		return false;
    	}
    	else if(status == StationStatus.PROCESSING && !StationBroken)
    	{
    		ProcessThePart();
    		return false;
    	}
//	this is unnecessary
//	commented out by Justin
/*    	else if(status == StationStatus.PARTSENT)
    	{
    		status = StationStatus.NEEDNOTIFICATION;
    		return true;
    	}
  */  	
    	
    	return false;
    }

    //Check to see if part needs processing 
    private void CheckIfPartNeedsProcessing()
    {
    	if(parts.get(0).getRecipe().charAt(workStation) == '1')
    	{
    		status = StationStatus.NEEDSPROCESSING;
    		//System.out.println(this.getName() + ConveyorNumber + " PART NEEDS TO BE PROCESSED!");
    		this.ProcessingPart();
    	}
    	else
    	{
    		status = StationStatus.NONEEDPROCESSING;
    		//System.out.println(this.getName() + ConveyorNumber + " PART DOES NOT NEED TO BE PROCESSED!");
    		this.PreparingToload();
    	}
    }
    
    //UV LAMPING PART
    private void ProcessThePart()
    {
    	//System.out.println(this.getName() + ConveyorNumber + " Processing part");
    	transducer.fireEvent(channel, TEvent.WORKSTATION_DO_ACTION, null);
    	status = StationStatus.NEEDNOTIFICATION;
    }
    
    //load to next CF
    private void UnloadToNextCF()
    {
    	//System.out.println(this.getName() + ConveyorNumber + " loading to next conveyor");
    	transducer.fireEvent(channel,TEvent.WORKSTATION_RELEASE_GLASS, null);
    	status = StationStatus.NEEDNOTIFICATION;
    	this.nextCF.msgHereIsNewPart(nextCF, parts.get(0));
    	//this.stateOfnextCF = NextCFState.BUSY;
    	//stateOfnextCF = NextCFState.BUSY;
    	//stateChanged();
    	
    }
    //check if next conveyor family is ready, if ready, send msg
    private void NotifyConveyorImFree()
    {
    	//System.out.println(this.getName() + ConveyorNumber + " notifying conveyor machine is free");
    	//if(stateOfnextCF == NextCFState.READY)
    	conveyor.msgImFree();
    	status = StationStatus.NEEDNOTIFICATION;
    }
    
    
    //MISC.
    public String getName(){
        return name;
    } 
    
    public void setConveyor(Conveyor conveyor)
    {
    	this.conveyor = conveyor;
    }
    
    public void setFamily(ConveyorFamily cf)
    {
    	this.family = cf;

    }
    
    public void setNextCF(ConveyorFamily cf)
    {
    	this.nextCF = cf;

    }

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) 
	{
		if (channel == this.channel && event == TEvent.WORKSTATION_LOAD_FINISHED) 
		{
			this.PartOnMachine();
		}

		else if (channel == this.channel && event == TEvent.WORKSTATION_GUI_ACTION_FINISHED) 
		{
			this.PreparingToload();
		} 
		else if (channel == this.channel && event == TEvent.WORKSTATION_RELEASE_FINISHED) 
		{
			//not necessary
			//commented by Justin
			//this.PartSentToNextConveyor();
		}
		else if(channel == this.channel && event == TEvent.WORKSTATION_BROKEN)
		{
			this.StationBroken = true;
			stateChanged();
		}
		else if(channel == this.channel && event == TEvent.WORKSTATION_FIXED)
		{
			this.StationBroken = false;
			stateChanged();
		}
		
	}

	@Override
	public void StallMachine() {
		// TODO Auto-generated method stub
		
	}



	
}
