package engine.Fuks;

import transducer.TChannel;

import transducer.Transducer;

import engine.Fuks.interfaces.Conveyor;
import engine.Fuks.interfaces.Machine;
import engine.Fuks.ConveyorAgentEdward;

import engine.agent.Part;

import engine.interfaces.ConveyorFamily;
import engine.interfaces.Robot;

/**
 * this CF implements ConveyorFamily Interface
 * it has 
 * ONLY conveyor agent.
 * 
 * Is used next to the shuttle
 * @author Edward
 *
 */
public class ConveyorSimpleFamilyEdward implements ConveyorFamily {

	private Machine myMachine;
	private Conveyor myConveyor;
	private ConveyorFamily backCF;
	private ConveyorFamily frontCF;

	private int myIndex;

	/**
	 * 
	 * @param index
	 * @param name
	 * @param transducer
	 */
	public ConveyorSimpleFamilyEdward(int index, String name,
			Transducer transducer) {

		myConveyor = (Conveyor) new ConveyorAgentEdward(index, "conveyor "
				+ name, transducer);
		myConveyor.setRightMachine(null);
		myIndex = index;
	}

	/**
	 * setting back CF
	 * @param backCF
	 */
	public void setbackCF(ConveyorFamily backCF) {
		this.backCF = backCF;
		this.myConveyor.setConveyorFamily(backCF);
	}

	/**
	 * setting front CF
	 * @param frontCF
	 */
	public void setfrontCF(ConveyorFamily frontCF) {
		this.frontCF = frontCF;

		myConveyor.setFrontCF(frontCF);
	}

	/**
	 * starting the thread
	 */
	public void startFamily() {

		myConveyor.startThread();
	}

	/**
	 * stopping the thread
	 */
	public void stopFamily() {

		myConveyor.stopThread();
	}

	/**
	 * the CF in front sends the message when it is ready to get the part
	 */
	@Override
	public void msgConveyorReady(ConveyorFamily cf) {

		myConveyor.iAmReady();

	}

	/**
	 * the CF in front notifies us when it receives the part
	 */
	@Override
	public void msgConveyorPartReceived(ConveyorFamily cf) {
		
		myConveyor.conveyorPartReceived(backCF);

	}

	/**
	 * the CF in the back passes the part
	 */
	@Override
	public void msgHereIsNewPart(ConveyorFamily cf, Part part) {
		myConveyor.hereIsNewPart(part);

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

	

	@Override
	public void msgMachineReady() {
		// TODO Auto-generated method stub

	}
/**
 * for debugging perposes
 */
	public void setPrintEnabled() {
		myConveyor.setPrintEnabled();
	}

	@Override
	public void msgBinConveyorReady() {
		// TODO Auto-generated method stub

	}

	

	@Override
	public void msgBinHereIsNewPart(Part part) {
		// TODO Auto-generated method stub

	}

	

	@Override
	public void msgBinConveyorStopping() {
		// TODO Auto-generated method stub
		
	}

}
