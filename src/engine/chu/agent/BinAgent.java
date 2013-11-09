package engine.chu.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;


import engine.agent.Agent;
import engine.agent.Part;
import engine.chu.interfaces.Bin;
import engine.interfaces.ConveyorFamily;

import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;


public class BinAgent extends Agent implements Bin{
	public List<Part> waitList = new ArrayList<Part>();

	Timer timer = new Timer();
	
	Transducer transducer;
	
	Boolean wait=false;
	
	ConveyorFamily cf;
	
	Semaphore asked = new Semaphore(0);
	
	Boolean doneCreate=true;
	
	Boolean sensorRealeased=true;
	
	public BinAgent(String name, Transducer transducer, ConveyorFamily cf){
		super(name);
		this.transducer=transducer;
		this.transducer.register(this, TChannel.BIN);
		this.transducer.register(this, TChannel.SENSOR);
		this.cf=cf;
	}
	
	//Messages
	
	public void msgHereIsNewPart(Part part, int quantity)
	{
		Part newpart = new Part(part.getRecipe());
		for(int i=0; i<quantity; i++)
		{
			waitList.add(newpart);
		}
		//System.out.println("added new part to waitList: " + part.getRecipe());
		stateChanged();
	}
	
	
	public void msgBinConveyorReady(){
		//System.out.println("Bin recieved cutterCF is ready");
		wait=false;
		asked.release();
		stateChanged();
	}
	
	/*
	@Override
	public void msgBinConveyorStopping() {
		//System.out.println("Bin recieved cutterCF is stopping");
		wait=true;
		asked.release();
		stateChanged();
		
	}
	*/
	
	@Override
	public boolean pickAndExecuteAnAction() {
		
		/*
		if(waitList.size()>0 && wait==true && doneCreate==true && sensorRealeased==true)
		{
			//System.out.println("in asking state");
			askConveyor();
			return true;
		}
		*/
		if(waitList.size()>0 && wait==false && doneCreate==true && sensorRealeased==true)
		{
			
			wait=true;
			sendPartToCutter();
			return true;
		}
		
		
		return false;
	}
	
	//Actions
	
	/*
	private void askConveyor() {
		cf.msgIsConveyorReady();
		try {
			asked.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stateChanged();
		
	}
	*/

	public void sendPartToCutter()
	{
		//.println("sending part : "+ waitList.get(0).getRecipe() + " to cutterCF");
		cf.msgBinHereIsNewPart(waitList.get(0));
		waitList.remove(0);
		transducer.fireEvent(TChannel.BIN, TEvent.BIN_CREATE_PART, null);
		doneCreate=false;
		sensorRealeased=false;
		wait=true;
		stateChanged();
	}
		

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if(channel==TChannel.BIN && event == TEvent.BIN_PART_CREATED)
		{
			doneCreate=true;
			stateChanged();
		}
		if(channel==TChannel.SENSOR && event == TEvent.SENSOR_GUI_RELEASED && ((Integer) args[0]).equals(0))
		{
			sensorRealeased=true;
			stateChanged();
		}
		
	}
	
	public void setCF(ConveyorFamily cf)
	{
		this.cf=cf;
	}




	
	

}
