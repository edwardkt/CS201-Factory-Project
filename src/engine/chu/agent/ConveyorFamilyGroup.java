package engine.chu.agent;


import shared.enums.SensorPosition;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

import engine.agent.Part;
import engine.chu.interfaces.Bin;
import engine.chu.interfaces.Machine;
import engine.interfaces.ConveyorFamily;

import engine.interfaces.Robot;



public class ConveyorFamilyGroup implements ConveyorFamily, TReceiver{
	private Transducer transducer = new Transducer();
	public ConveyorFamily nextcf;
	public ConveyorAgent conveyor = new ConveyorAgent(this, "conveyor", null, 0);
	//SensorAgent sensor1 = new SensorAgent(this, "sensor1", 0, null, conveyor);
	//SensorAgent sensor2 = new SensorAgent(this, "sensor2", 1, null, conveyor);
	PopUpAgent popUp = new PopUpAgent(this, "popUp", null, null, null, null );
	int conveyorIndex;
	Bin bin;
	MachineAgent machine = new MachineAgent("cutterMachine", this);
	
	public ConveyorFamilyGroup(Transducer transducer, ConveyorFamily nextcf, int conveyorIndex, Bin bin){
		this.transducer=transducer;
		this.transducer.register(this, TChannel.BIN);
		this.nextcf=nextcf;
		this.conveyorIndex=conveyorIndex;
		this.bin=bin;
		//sensor1.setSensorIndex(conveyorIndex*2);
		//sensor2.setSensorIndex(conveyorIndex*2+1);
		conveyor.setTransducer(transducer);
		//conveyor.setPopUp(popUp);
		conveyor.setMachine(machine);
		machine.setTransducer(transducer, TChannel.CUTTER);
		machine.setConveyor(conveyor);
		
		
		//popUp.setNextCF(this.nextcf);
		//sensor1.setTransducer(this.transducer);
		//sensor2.setTransducer(this.transducer);
		//popUp.setTransducer(this.transducer);
		
		conveyor.startThread();
		machine.startThread();
		//sensor1.startThread();
		//sensor2.startThread();
		//popUp.startThread();
		
		//Part glass1 = new Part("111111");
		//transducer.fireEvent(TChannel.BIN, TEvent.BIN_CREATE_PART, null);
		//msgHereIsNewPart(this,glass1);
		
		
	}
	
	@Override
	public void msgConveyorReady(ConveyorFamily cf) {
		machine.msgConveyorReady(nextcf);
	}
	

	@Override
	public void msgConveyorPartReceived(ConveyorFamily cf) {
		machine.msgConveyorPartReceived(nextcf);
		
	}

	@Override
	public void msgHereIsNewPart(ConveyorFamily cf, Part part) {
		Part newpart = new Part(part.getRecipe());
		//System.out.println("In cf0 hereisnewpart: "+newpart.getRecipe());
		nextcf.msgHereIsNewPart(this, newpart);
			
	}
	
	@Override
	public void msgBinHereIsNewPart(Part part) {
		Part newpart = new Part(part.getRecipe());
		conveyor.msgHereIsNewPart(newpart);
			
	}


	@Override
	public void msgMachineReady() {
		conveyor.msgMachineReady();
		
	}
	
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {

	}

	@Override
	public void msgRobotReady(Robot robot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgRobotReady(Robot robot, Integer index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgRobotPartReceived(Robot robot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPartDone(Robot robot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsPopUpPart(Robot robot, Part part) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIReceivedPart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPopUpUp(Robot robot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgMachinePart(Robot robot, Part part) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsPartFromPopUp(Part part) {
		// TODO Auto-generated method stub
		
	}

	public void setNextcf(ConveyorFamily nextcf)
	{
		this.nextcf=nextcf;
	}

	@Override
	public void msgBinConveyorReady() {
		bin.msgBinConveyorReady();
		
	}

	@Override
	public void msgBinConveyorStopping() {
		// TODO Auto-generated method stub
		
	}

	
}
