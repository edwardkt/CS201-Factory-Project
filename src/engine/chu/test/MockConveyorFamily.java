package engine.chu.test;


import engine.agent.Part;
import engine.interfaces.ConveyorFamily;
import engine.interfaces.Robot;

public class MockConveyorFamily implements ConveyorFamily{

	public EventLog log = new EventLog();
	@Override
	public void msgConveyorReady(ConveyorFamily cf) {
		log.add(new LoggedEvent(
				"Received message msgConveyorReady from pastcf"));
	}

	@Override
	public void msgConveyorPartReceived(ConveyorFamily cf) {
		log.add(new LoggedEvent(
				"Received message msgConveyorPartReceived from cf"));
		
	}

	@Override
	public void msgHereIsNewPart(ConveyorFamily cf, Part part) {
		log.add(new LoggedEvent(
				"Received message msgHereIsNewPart from pastcf"));
		
	}

	@Override
	public void msgRobotReady(Robot robot) {
		log.add(new LoggedEvent(
				"Received message msgRobotReady from robot"));
		
	}

	@Override
	public void msgRobotPartReceived(Robot robot) {
		log.add(new LoggedEvent(
				"Received message msgRobotPartReceived from robot"));
		
	}

	@Override
	public void msgPartDone(Robot robot) {
		log.add(new LoggedEvent(
				"Received message msgPartDone from robot"));
		
		
	}



	@Override
	public void msgMachineReady() {
		log.add(new LoggedEvent(
				"Received message msgMachineReady from machine"));
		
	}



	@Override
	public void msgRobotReady(Robot robot, Integer index) {
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

}
