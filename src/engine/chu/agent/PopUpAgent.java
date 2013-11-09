package engine.chu.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import engine.agent.Agent;
import engine.agent.Part;
import engine.chu.interfaces.Conveyor;
import engine.chu.interfaces.PopUp;
import engine.chu.interfaces.Robot;
import engine.interfaces.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class PopUpAgent extends Agent implements PopUp{

	private Part currentPart;
	private Transducer transducer;
	public enum NextCFStatus {ready, toldPopUp, full};
	public enum RobotStatus {ready, working, doneWorking};
	public enum PopUpStatus {ready, toldConveyor, receivedPart, waitingForNextCF};
	public enum PartStatus {needsProcessing, noProcessing, beingMachined, doneProcessing, waitingForNextCF};
	
	public List<PartTracker> partList = Collections.synchronizedList(new ArrayList<PartTracker>());
	
	private class PartTracker{
		Part part;
		PartStatus status;
		Robot robot;
		boolean partHere;
		
		public PartTracker(Part part){
			this.part = part;
			String barcode;
			barcode=part.getRecipe();
			System.out.println(barcode);
			if(barcode.charAt(0)=='1')
				status = PartStatus.needsProcessing;
			else
				status = PartStatus.noProcessing;
			robot = null;
			System.out.println("Status: " + status);
			partHere = false;
		}
		
	}
	
	PopUpStatus pStatus;
	RobotStatus r1Status;
	RobotStatus r2Status;
	NextCFStatus nextcfStatus;
	ConveyorFamily cf;
	
	ConveyorFamily nextcf;
	
	//Name of PopUp
	private String name;
	
	//Agent interaction
	Conveyor conveyor;
	Robot robot1;
	Robot robot2;
	
	
	public PopUpAgent(ConveyorFamily cf, String name, Transducer transducer, Conveyor conveyor, Robot robot1, Robot robot2){
		super(name);
		this.name= name;
		this.transducer = transducer;
		this.conveyor=conveyor;
		this.cf=cf;
		this.robot1=robot1;
		this.robot2=robot2;
		currentPart=null;
		pStatus = PopUpStatus.ready;
		r1Status = RobotStatus.ready;
		r2Status = RobotStatus.ready;
		nextcfStatus = NextCFStatus.ready;
		
	}
	
	//Messages
	
	public void msgIsThereSpace(){
		if(pStatus==PopUpStatus.ready){
			conveyor.msgPopUpReady();
		}
			else
				conveyor.msgPopUpNotReady();
		}
	
	
	public void msgPartBeginningConveyor(Part part) {
		synchronized(partList){
		partList.add(new PartTracker(part));
		}
		System.out.println("list size: " + partList.size());
		System.out.println("New part was added to conveyor");
		stateChanged();
	}
	
	public void msgHereIsPart(Part part)
	{
		currentPart = part;
		pStatus=PopUpStatus.receivedPart;
		for(int i=0; i<partList.size(); i++)
		{
			if(partList.get(i).part.equals(part))
			{
				partList.get(i).partHere=true;
				break;
			}
		}
		System.out.println("received msgHereIsPart in popup");
		stateChanged();
		
	}
	
	public void msgPartDone(Robot robot){
		for(int i=0; i<partList.size(); i++)
		{
			if(partList.get(i).robot.equals(robot))
			{
				partList.get(i).status=PartStatus.doneProcessing;
				break;
			}
		}
		System.out.println("received msgPartDone from robot");
		System.out.println(pStatus + " msgPartDone");
		stateChanged();
	}
	
	public void msgConveyorReady(){
		nextcfStatus=NextCFStatus.ready;
		System.out.println("received msgConveyorReady from nextCF");
		stateChanged();
	}
	
	public void msgRobotReady(Robot robot){
		if(robot==robot1)
			r1Status=RobotStatus.ready;
		else if(robot==robot2)
			r2Status=RobotStatus.ready;
		System.out.println("received msgRobotReady from robot");
		//System.out.System.out.printlnln(pStatus + " msgRobotReady " + r1Status);
		stateChanged();
	}
	
	public void msgRobotPartReceived(Robot robot) {
		for(int i=0; i<partList.size(); i++)
		{
			if(partList.get(i).part==robot.getPart())
			{
				partList.get(i).status=PartStatus.beingMachined;
				break;
			}
		}
		System.out.println("received msgRobotPartReceived from robot");
		stateChanged();
		
		
	}
	
	public void msgHereIsPopUpPart(Part p){
		pStatus=PopUpStatus.waitingForNextCF;
		for(int i=0; i<partList.size(); i++)
		{
			if(partList.get(i).part==p)
			{
				partList.get(i).status=PartStatus.waitingForNextCF;
				break;
			}
		}
		System.out.println("received msgHereIsPopUpPart from robot");
		stateChanged();
	}
	
	public boolean pickAndExecuteAnAction() {
		
		PartTracker temp = null;
		
		
		
		for (PartTracker p: partList) {
			System.out.println("Part Status: " + p.status);
		}
		
		
		//System.out.println("begin scheduler pStatus: "+ pStatus);
		if(pStatus==PopUpStatus.waitingForNextCF)
		{
			if(nextcfStatus==NextCFStatus.ready)
			{
				sendAway(currentPart);
				return true;
			}
			else
			{
				//stopConveyor();
			}
			return true;
		}
		
		synchronized(partList){
		for(PartTracker p:partList){
    		if(p.status==PartStatus.doneProcessing){
    			if(pStatus==PopUpStatus.ready){
    				temp = p;
    			}
    	    }
    	}
		}if (temp != null) {
			getFinishedPart(temp.robot);
			return true;
	    }
		
		synchronized(partList){
		for(PartTracker p:partList){
    		if(p.status==PartStatus.noProcessing && p.partHere==true){
    			if(pStatus==PopUpStatus.receivedPart){
    				temp = p;
    			}
    	    }
    	}
		}if (temp != null) {
			sendAway(temp.part);
			return true;
	    }
		
		synchronized(partList){
		for(PartTracker p:partList){
    		if(p.status==PartStatus.needsProcessing && p.partHere==true){
    			
    			if(pStatus==PopUpStatus.receivedPart){
    				if(r1Status==RobotStatus.ready){
    					p.robot=robot1;
    					temp = p;
    					
    				}
    				else if (r2Status==RobotStatus.ready){
    					p.robot=robot2;
    					temp = p;
    					//sendToRobot(robot2, p.part);
    				}
    			}
    	    }}
		}if (temp != null) {
			sendToRobot(temp.robot, temp.part);
			return true;
	    }
		
		/*if(pStatus==PopUpStatus.toldConveyor)
		{
			//Do nothing
			return true;
		}*/
		
		


		System.out.println("reached end of schedular pStatus: "+ pStatus + " r1Status: " +r1Status);
		return false;
	}


	private void getFinishedPart(Robot robot) {
		System.out.println("Bringing popup up to get part");
		robot.msgPopUpUp();
		
	}

	private void sendAway(Part p) {
		System.out.println("Sending part to next CF");
		nextcf.msgHereIsNewPart(cf,p);
		for(int i=0; i<partList.size(); i++)
		{
			if(partList.get(i).part==p)
			{
				synchronized(partList){
				partList.remove(i);
				}
				break;
			}
		}
		pStatus=PopUpStatus.ready;
		
		stateChanged();
	}
//TODO PROBLEM HERE. 
	private void sendToRobot(Robot robot, Part p) {
		System.out.println("Sending to robot");
		robot.msgMachinePart(p);
		if(robot==robot1)
			r1Status = RobotStatus.working;
		else if(robot==robot2)
			r2Status = RobotStatus.working;
		pStatus = PopUpStatus.ready;
		/*for(int i=0; i<partList.size(); i++)
		{
			if(partList.get(i).part==p)
			{
				partList.get(i).status=PartStatus.beingMachined;
				break;
			}
		}*/
		stateChanged();
	}

/*	private void tellConveyorReady() {
		System.out.println("telling conveyor popup is ready");
		conveyor.msgPopUpReady();
		//pStatus=PopUpStatus.toldConveyor;
		stateChanged();
		
	}*/

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		if(channel==TChannel.CUTTER && event==TEvent.WORKSTATION_GUI_ACTION_FINISHED)
		{
			transducer.fireEvent(TChannel.CUTTER, TEvent.WORKSTATION_RELEASE_GLASS, args);
			System.out.println("machine event fired");
		}
		
	}
	
	public void setTransducer(Transducer transducer){
		this.transducer=transducer;
		transducer.register(this, TChannel.CUTTER);
	}
	
	//temp for simulation
	
	private void makeRobotReady(){
		msgRobotReady(robot1);
	}
	
	private void makeNextCFReady(){
		msgConveyorReady();
	}
	
	public void setNextCF(ConveyorFamily next) {
		this.nextcf = next;
	}

	

}
