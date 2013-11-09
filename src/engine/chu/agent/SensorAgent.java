package engine.chu.agent;



import shared.enums.SensorPosition;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;
import engine.util.*;
import engine.agent.Agent;
import engine.agent.Part;
import engine.chu.interfaces.Conveyor;
import engine.chu.interfaces.Sensor;
import engine.interfaces.*;


public class SensorAgent extends Agent implements Sensor, TReceiver{
	private boolean partOn = false;
	public Part currentPart;
	Transducer transducer;
	ConveyorFamily cf;
	SensorPosition type;
	
	//Name of Sensor
	private String name;
	
	//Agent interaction
	Conveyor conveyor;
	
	int sensorIndex;
	
	public SensorAgent(ConveyorFamily cf, String name, int sensorIndex, Transducer transducer, Conveyor conveyor){
		super(name);
		this.name=name;
		this.transducer = transducer;
		this.conveyor=conveyor;
		this.cf=cf;
		this.sensorIndex=sensorIndex;
		
		if(sensorIndex%2==0)
		{
			type=SensorPosition.START;
		}
		else
			type=SensorPosition.END;
		currentPart=null;
		//this.transducer.register(this, TChannel.SENSOR);
		//register?
	}
	
	//Messages
	
	public void msgHereIsNewPart(ConveyorFamily cf, Part part){
		//partOn=true;
		currentPart = part;
		if(currentPart==null)
			System.out.println("NULL part received in sensor " + sensorIndex );
		System.out.println("msgHereIsNewPart recieved");
		stateChanged();
	}
	

	@Override
	public boolean pickAndExecuteAnAction() {
		if(partOn)
		{
			tellConveyor();
			return true;
		}
		
		return false;
	}
	
	public void tellConveyor()
	{
		
		if(type==SensorPosition.START && sensorIndex%2==0){
			if(currentPart==null)
				System.out.println("NULL part"); 
			System.out.println("telling Conveyor that part "+currentPart.getRecipe() +" is at start");
			conveyor.msgPartBeginningConveyor(currentPart);
			partOn=false;
			stateChanged();
		}
		else if(type==SensorPosition.END && sensorIndex%2==1)
		{
			System.out.println("telling Conveyor that part is at end");
			conveyor.msgPartEndingConveyor();
			partOn=false;
		}
	}
	
	public void setTransducer(Transducer transducer){
		this.transducer=transducer;
		this.transducer.register(this, TChannel.SENSOR);
		System.out.println("transducer set");
	}
	
	

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		
		if(channel==TChannel.SENSOR && event == TEvent.SENSOR_GUI_PRESSED && args[0].equals(sensorIndex))
		{
			System.out.println("event here sensor pressed");
			/*Part glass = new Part("1111");
			if(sensorIndex%2==0)
			{
				System.out.println("here is new part sent");		//simulation
				cf.msgHereIsNewPart(cf, glass);
			}*/
			partOn=true;
			stateChanged();
		}
		if(channel==TChannel.SENSOR && event == TEvent.SENSOR_GUI_RELEASED && args[0].equals(sensorIndex))
		{
			System.out.println("sensor released gui");
			partOn=false;
			//currentPart=null;
			stateChanged();
		}
		
	}
	
	
    public String getName(){
        return name;
    }
    
    public void setSensorIndex(int sensorIndex)
    {
		this.sensorIndex=sensorIndex;
		
		if(sensorIndex%2==0)
		{
			type=SensorPosition.START;
		}
		else
			type=SensorPosition.END;
    }

}
