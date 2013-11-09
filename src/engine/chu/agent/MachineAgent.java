package engine.chu.agent;


import engine.agent.Agent;
import engine.agent.Part;
import engine.chu.interfaces.Conveyor;
import engine.chu.interfaces.Machine;
import engine.interfaces.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

public class MachineAgent extends Agent implements Machine, TReceiver{

	private String name;
	ConveyorFamily cf;
	Transducer transducer;
	TChannel channel;
	Conveyor conveyor;
	
	public enum MachineStatus{empty, loaded, needsProcessing, inProcess, doneProcessing};
	
	MachineStatus status;
	
	boolean nextReady;
	
	Part part;
	
	boolean GUIFinishedLoad=false;
	
	boolean broken=false;
	
	boolean sensorBroken=false;
	
	
	public MachineAgent (String name, ConveyorFamily cf)
	{
		super(name);
		this.name = name;
		this.cf = cf;
		status = MachineStatus.empty;
		nextReady = true;
		part = null;
		conveyor = null;
	}
	
	//Messages
	public void msgHereIsPart( Part part )
	{
		//System.out.println("Machine Received msgHereIsPart: "+ part.getRecipe());
		this.part = part;
		status=MachineStatus.loaded;
		stateChanged();
	}
	
	public void msgConveyorReady(ConveyorFamily nextcf)
	{
		//System.out.println("cf0: Received msgConveyorReady from cf1");
		nextReady = true;
		
		if(part!=null)
			stateChanged();
	}
	
	public void msgConveyorStopping()
	{
		//System.out.println("Recieved msgConveyorStopping from next cf");
		nextReady=false;
		stateChanged();
	}
	
	
	//Scheduler
	
	public boolean pickAndExecuteAnAction() {
		
		if(sensorBroken==true)
		{
			tellConveyorToStop();
			return false;
		}
		
		if(broken==true)
		{
			return false;
		}
		
		if(status==MachineStatus.doneProcessing && part!=null && nextReady==true)
		{
			sendPart();
			return true;
		}
		
		if(status==MachineStatus.needsProcessing)
		{
			machinePart();
			return true;
		}
		
		if(status==MachineStatus.loaded && GUIFinishedLoad==true)
		{
			checkPart();
			return true;
		}
	
		
		
		return false;
	}
	
	//Actions

	public void checkPart() {
		//System.out.println("Part : "+ part.getRecipe() + " being checked");
		if(part.getRecipe().charAt(0)=='1')
			status = MachineStatus.needsProcessing;
		else
			status = MachineStatus.doneProcessing;
		stateChanged();
	}

	public void machinePart() {
		//System.out.println("Part : "+ part.getRecipe() + " being machined");
		status = MachineStatus.inProcess;
		transducer.fireEvent(channel, TEvent.WORKSTATION_DO_ACTION, null);
		stateChanged();
		
	}
	
	public void sendPart() {
		//System.out.println("Sending part to next conveyor");
		cf.msgHereIsNewPart(cf, this.part);
		part = null;
		nextReady=false;
		transducer.fireEvent(channel, TEvent.WORKSTATION_RELEASE_GLASS, null);
		stateChanged();
	}
	
	public void tellConveyorToStop(){
		conveyor.msgMachineNotReady();
	}


	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if(channel==this.channel && event == TEvent.WORKSTATION_LOAD_FINISHED)
		{
			if(part==null)
			{
				sensorBroken = true;
				System.err.println("Unexpected glass in Cutter");
				Object[] sensorArg = new Object[1];
				sensorArg[0] = 1;
				transducer.fireEvent( TChannel.GUI, TEvent.SENSOR_BROKEN, sensorArg );
				stateChanged();
				
			}
			conveyor.msgMachineReceivedPart();
			GUIFinishedLoad=true;
			stateChanged();
		}
		if(channel==this.channel && event == TEvent.WORKSTATION_GUI_ACTION_FINISHED)
		{
			status = MachineStatus.doneProcessing;
			stateChanged();
		}
		if(channel==this.channel && event == TEvent.WORKSTATION_RELEASE_FINISHED)
		{
			//System.out.println("cf0machine release finished");
			status = MachineStatus.empty;
			conveyor.msgMachineReady();
			GUIFinishedLoad=false;
			stateChanged();
		}
		
		if(channel==this.channel && event == TEvent.WORKSTATION_BROKEN)
		{
			broken=true;
			stateChanged();
		}
		
		if(channel==this.channel && event == TEvent.WORKSTATION_FIXED)
		{
			broken=false;
			if(part!=null)
			{
				checkPart();
			}
			stateChanged();
		}
		
		if(channel==TChannel.SENSOR && event==TEvent.SENSOR_FIXED && args[0].equals(1)){
			sensorBroken=false;
			status= MachineStatus.empty;
			GUIFinishedLoad=false;
			conveyor.msgMachineReady();
		}
		
		
	}
	
	public void setCF(ConveyorFamily cf)
	{
		this.cf = cf;
	}
	
	public void setConveyor(Conveyor conveyor)
	{
		this.conveyor = conveyor;
	}
	
	public void setTransducer(Transducer transducer, TChannel channel)
	{
		this.transducer=transducer;
		this.transducer.register(this, channel);
		this.channel = channel;
		this.transducer.register(this, TChannel.SENSOR);
	}

	public void msgConveyorPartReceived(ConveyorFamily nextcf) {
		//Can't use while testing. will use to  tell conveyor machine is ready
		/*
		status = MachineStatus.empty;
		conveyor.msgMachineReady();
		stateChanged();
		*/
		
	}

}
