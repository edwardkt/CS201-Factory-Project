package engine.sanders.test.mock;


import engine.agent.Part;
import engine.interfaces.ConveyorFamily;
import engine.interfaces.Robot;

public class MockConveyorFamilyInterface implements ConveyorFamily {

	
	@Override
	public void msgConveyorReady(ConveyorFamily cf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgConveyorPartReceived(ConveyorFamily cf) {
		//log.add( new LoggedEvent( "Received message msgConveyorPartReceived from conveyor" ) );	
		
	}

	@Override
	public void msgHereIsNewPart(ConveyorFamily cf, Part part) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgRobotReady(Robot robot) {
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

	public void msgIAmStopping() {
		//log.add( new LoggedEvent( "Received message msgIAmStopping from conveyor" ) );
		
	}

	public void msgIAmStarting() {
		//log.add( new LoggedEvent( "Received message msgIAmStarting from conveyor" ) );	
		
	}

	@Override
	public void msgIReceivedPart() {
		//log.add( new LoggedEvent( "Received message msgIReceivedPart from conveyor" ) );
		
	}

	@Override
	public void msgPopUpUp(Robot robot) {
		//log.add( new LoggedEvent( "Received message msgPopUpUp from popUp" ) );	
		
	}

	@Override
	public void msgMachinePart(Robot robot, Part part) {
		//log.add( new LoggedEvent( "Received message msgMachinePart from popUp" ) );	
		
	}

	@Override
	public void msgHereIsPartFromPopUp(Part part) {
		//log.add( new LoggedEvent( "Received message msgHereIsPartFromPopUp from popUp" ) );	
	}

	@Override
	public void msgRobotReady(Robot robot, Integer index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgMachineReady() {
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
