package engine.tam.ConveyorFamily;

import engine.tam.ConveyorFamily.*;

import java.util.ArrayList;
import java.util.List;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

import engine.interfaces.ConveyorFamily;
import engine.interfaces.Robot;
import engine.agent.Part;

public class ConveyorFamilyInLineStation implements ConveyorFamily,TReceiver
{
	private Transducer transducer;
	private ConveyorFamily nextCF;
	private ConveyorFamily backCF;
	private int ConveyorNumber;
	private ConveyorAgent conveyor;
	private WorkStationAgent station;
	private TChannel channel;
	private int workStation;
	
	//public Part p;
	String name;
	public ConveyorFamilyInLineStation(String name, Transducer transducer, int ConveyorNumber, int workStation, TChannel channel)
	{
		super();
		this.transducer = transducer;
		this.ConveyorNumber = ConveyorNumber;
		this.name = name;
		this.channel = channel;
		this.workStation = workStation;
		this.setConveyor(conveyor);
		this.setStation(station);
		conveyor = new ConveyorAgent("ConveyorAgent",transducer,ConveyorNumber);
		station = new WorkStationAgent("Station",transducer,channel,workStation,ConveyorNumber);
		conveyor.setFamily(this,ConveyorNumber);
		conveyor.setStation(station);
		station.setConveyor(conveyor);
		station.setFamily(this);
		station.setNextCF(nextCF);
	}
	
	public void startThreads()
	{
		conveyor.startThread();
		station.startThread();
	}
	
	public void msgHereIsPopUpPart(Part part)
	{
		
	}
	
	public void setConveyor(ConveyorAgent conveyor)
	{
		this.conveyor = conveyor;
	}
	
	public void setStation(WorkStationAgent station)
	{
		this.station = station;
	}
	
	public void setNextCF(ConveyorFamily cf)
	{
		this.nextCF = cf;
		station.setNextCF(cf);
	}
	
	public void setBackCF(ConveyorFamily cf)
	{
		this.backCF = cf;
		conveyor.setBackCF(cf);
	}

	


	@Override
	public void msgConveyorReady(ConveyorFamily cf) {
//		System.err.println("Conveyor" + ConveyorNumber + "received msgConveyorReady() from cf12");
		station.msgImReady();
	}

	

	

	
	@Override
	public void msgHereIsNewPart(ConveyorFamily cf, Part part) 
	{
		//System.out.println("Received part from conveyor family");
		conveyor.msgHereIsNewPart(part);
		
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgConveyorPartReceived(ConveyorFamily cf) {
		//System.out.println("Received part from back conveyor family");
		station.msgPartReceived();
		
	}

	
	//public void msgHereIsNewPart(ConveyorFamily cf, engine.agent.Part part) {
		// TODO Auto-generated method stub
		
	//}

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
	public void msgHereIsPopUpPart(Robot robot, engine.agent.Part part) {
		// TODO Auto-generated method stub
		
	}

	/*@Override
	public void msgConveyorStopping() {
		System.out.println("need to stall conveyor, stall machine as well!");
		station.StallMachine();
		
	}*/

	@Override
	public void msgIReceivedPart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPopUpUp(Robot robot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgMachinePart(Robot robot, engine.agent.Part part) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsPartFromPopUp(engine.agent.Part part) {
		// TODO Auto-generated method stub
		
	}

	/*@Override
	public void msgLeadSensorDepressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgLeadSensorReleased() {
		// TODO Auto-generated method stub
		
	}*/

	@Override
	public void msgMachineReady() {
		// TODO Auto-generated method stub
		
	}

	/*@Override
	public void msgIsConveyorReady() {
		// TODO Auto-generated method stub
		
	}*/

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
		// TODO Auto-generated method stub
		
	}

	/*@Override
	public void msgConveyorBroken() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgConveyorFixed() {
		// TODO Auto-generated method stub
		
	}*/

	/*@Override
	public void msgConveyorStopping() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIsConveyorReady() {
		// TODO Auto-generated method stub
		
	}*/
	
}
