package engine.sanders.test;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;
import engine.agent.Part;
import engine.chu.agent.ConveyorFamilyGroup;
import engine.chu.interfaces.Bin;
import engine.sanders.agent.ConveyorAgentWithTruck;
import engine.sanders.agent.ConveyorFamilyContainer;
//testing
//testing
import engine.tam.ConveyorFamily.ConveyorFamilyInLineStation;

public class GuiTestSM implements TReceiver {
	Transducer t;
	Bin bin;
	ConveyorFamilyGroup cf0;
	ConveyorFamilyContainer cf5;
	ConveyorFamilyContainer cf7;
	ConveyorFamilyInLineStation cf11;
	ConveyorAgentWithTruck cf14;

	Integer cutterConveyorIndex = 0;
	Integer automaticBreakoutPreIndex = 1;
	Integer automaticBreakoutPostIndex = 2;
	Integer manualBreakoutIndex = 3;
	Integer secondShuttleIndex = 4;
	Integer drillConveyorIndex = 5;
	Integer crossSeamerConveyorIndex = 6;
	Integer grinderConveyorIndex = 7;
	Integer washerEdward = 8;
	Integer simpleEdward = 9;
	Integer painterEdward = 10;
	Integer washEdward = 8;
	Integer simpleEdward2 = 12;
	Integer ovenEdward = 13;
	Integer uvEdward = 11;
	Integer conveyor14Index = 14;
	boolean offlineDone = true;
	int totalParts = 0;

	// other than transducer, the parameters are pointers to fully functioning
	// agents.
	public GuiTestSM(Transducer t, ConveyorFamilyContainer cf5,
			ConveyorFamilyContainer cf7, ConveyorAgentWithTruck cf14, ConveyorFamilyInLineStation cf11, Bin bin, ConveyorFamilyGroup cf0) {
		this.t = t;
		this.cf5 = cf5;
		this.cf7 = cf7;
		this.cf14 = cf14;
		this.cf11 = cf11;
		this.bin=bin;
		this.cf0=cf0;
		t.register(this, TChannel.CUTTER);
		t.register(this, TChannel.SENSOR);
		t.register(this, TChannel.BREAKOUT);
		t.register(this, TChannel.MANUAL_BREAKOUT);
		// the following registration events are commented to give control to
		// the actual agents
		// t.register(this, TChannel.POPUP);
		// t.register(this, TChannel.DRILL);
		//t.register(this, TChannel.UV_LAMP);
		//t.register(this, TChannel.WASHER);
		//t.register(this, TChannel.OVEN);
		//t.register(this, TChannel.PAINTER);
		// the following registration events are commented to give control to
		// the actual agents
		// t.register(this, TChannel.TRUCK);//added by monroe

		//t.fireEvent(TChannel.BIN, TEvent.BIN_CREATE_PART, null);
//		bin.msgHereIsNewPart(new Part("1111111111"), 0);
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if (channel == TChannel.SENSOR && event == TEvent.SENSOR_GUI_PRESSED) {
			Integer[] newArgs = new Integer[1];
			// added argument 2-5 to ignore the sensors in conveyor families 5-7
			// and 14 and give control to the actual agents
			if (((Integer) args[0] % 2) == 0
					&& !((Integer) args[0]).equals(cutterConveyorIndex * 2)
			//		&& !((Integer) args[0]).equals(automaticBreakoutPreIndex * 2)
			//		&& !((Integer) args[0]).equals(automaticBreakoutPostIndex * 2)
			//		&& !((Integer) args[0]).equals(manualBreakoutIndex * 2)
			//		&& !((Integer) args[0]).equals(secondShuttleIndex * 2)
					&& !((Integer) args[0]).equals(drillConveyorIndex * 2)
					&& !((Integer) args[0]).equals((crossSeamerConveyorIndex) * 2)
					&& !((Integer) args[0]).equals((grinderConveyorIndex) * 2)
					&& !((Integer) args[0]).equals((conveyor14Index) * 2)
					&& !((Integer) args[0]).equals((washerEdward) * 2)
					&& !((Integer) args[0]).equals((simpleEdward) * 2)
					&& !((Integer) args[0]).equals((painterEdward) * 2)
					&& !((Integer) args[0]).equals((uvEdward) * 2)
					&& !((Integer) args[0]).equals((simpleEdward2) * 2)
					&& !((Integer) args[0]).equals((ovenEdward) * 2)
					) {
				newArgs[0] = (Integer) args[0] / 2;
				t.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START,
						newArgs);
			}
			//commented out by christina because it is no longer needed
			// this gives conveyor family 5 an actual Part when the last sensor
			// in the prior conveyor family is pressed
			else if (((Integer) args[0]).equals(drillConveyorIndex * 2 - 1)) { // testing
				cf5.msgHereIsNewPart(null, new Part("0000000111"));
			}
/*			 else if (((Integer) args[0]).equals(uvEdward * 2 - 1)) { // testing
					cf11.msgHereIsNewPart(null, new Part("0000000000"));
				}
			else if (((Integer) args[0]).equals(conveyor14Index * 2 - 1)) { // testing
				cf14.msgHereIsNewPart(null, new Part("0000000000"));
			}
*/
		}


		else if (channel == TChannel.SENSOR
				&& event == TEvent.SENSOR_GUI_RELEASED) {
			// this loads an additional, specified number of parts into the animation for testing
			if (((Integer) args[0]).equals(0) && totalParts < 30) {
				//t.fireEvent(TChannel.BIN, TEvent.BIN_CREATE_PART, null);
				//bin.msgHereIsNewPart(new Part("1111111111"), 0);
				totalParts++;
			}
			//this sends the previous conveyor a ready message when the following conveyor's
			//sensor is released
/*			else if(((Integer) args[0]).equals(washEdward * 2) )
				cf7.msgConveyorReady(null);
*/			if(((Integer) args[0]).equals(2) )
				cf0.msgConveyorReady(null);

		} else if (channel == TChannel.CUTTER
				&& event == TEvent.WORKSTATION_LOAD_FINISHED) {// added by
																// monroe
			t.fireEvent(TChannel.CUTTER, TEvent.WORKSTATION_DO_ACTION, null);
		} 

		else if (channel == TChannel.CUTTER
				&& event == TEvent.WORKSTATION_GUI_ACTION_FINISHED) {
			t.fireEvent(TChannel.CUTTER, TEvent.WORKSTATION_RELEASE_GLASS, null);
		} 

		else if (channel == TChannel.BREAKOUT
				&& event == TEvent.WORKSTATION_LOAD_FINISHED) {// added by
																// monroe
			t.fireEvent(TChannel.BREAKOUT, TEvent.WORKSTATION_DO_ACTION, null);
		} 

		else if (channel == TChannel.BREAKOUT
				&& event == TEvent.WORKSTATION_GUI_ACTION_FINISHED) {
			t.fireEvent(TChannel.BREAKOUT, TEvent.WORKSTATION_RELEASE_GLASS,
					null);
		} 

		else if (channel == TChannel.MANUAL_BREAKOUT
				&& event == TEvent.WORKSTATION_LOAD_FINISHED) {// added by
																// monroe
			t.fireEvent(TChannel.MANUAL_BREAKOUT, TEvent.WORKSTATION_DO_ACTION,
					null);
		} 

		else if (channel == TChannel.MANUAL_BREAKOUT
				&& event == TEvent.WORKSTATION_GUI_ACTION_FINISHED) {
			t.fireEvent(TChannel.MANUAL_BREAKOUT,
					TEvent.WORKSTATION_RELEASE_GLASS, null);
		} 

		else if (channel == TChannel.POPUP
				&& event == TEvent.POPUP_GUI_LOAD_FINISHED) {
			if (offlineDone)
				t.fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_DOWN, args);
			else
				t.fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_UP, args);
		} 

		else if (channel == TChannel.POPUP
				&& event == TEvent.POPUP_GUI_MOVED_UP) {
			Integer[] newArgs = new Integer[1];
			newArgs[0] = 0;
			t.fireEvent(TChannel.DRILL, TEvent.WORKSTATION_DO_LOAD_GLASS,
					newArgs);
		} 

		else if (channel == TChannel.DRILL
				&& event == TEvent.WORKSTATION_LOAD_FINISHED) {
			t.fireEvent(TChannel.DRILL, TEvent.WORKSTATION_DO_ACTION, args);
		} 

		else if (channel == TChannel.DRILL
				&& event == TEvent.WORKSTATION_GUI_ACTION_FINISHED) {
			t.fireEvent(TChannel.DRILL, TEvent.WORKSTATION_RELEASE_GLASS, args);
			offlineDone = true;
		} 

		else if (channel == TChannel.POPUP
				&& event == TEvent.POPUP_GUI_MOVED_DOWN) {
			t.fireEvent(TChannel.POPUP, TEvent.POPUP_RELEASE_GLASS, args);
			// offlineDone = false;
		} 

		else if (channel == TChannel.WASHER
				&& event == TEvent.WORKSTATION_LOAD_FINISHED) {// added by
																// monroe
			t.fireEvent(TChannel.WASHER, TEvent.WORKSTATION_DO_ACTION, null);
		} 

		else if (channel == TChannel.WASHER
				&& event == TEvent.WORKSTATION_GUI_ACTION_FINISHED) {
			t.fireEvent(TChannel.WASHER, TEvent.WORKSTATION_RELEASE_GLASS, null);
		} 

		else if (channel == TChannel.UV_LAMP
				&& event == TEvent.WORKSTATION_LOAD_FINISHED) {// added by
																// monroe
			t.fireEvent(TChannel.UV_LAMP, TEvent.WORKSTATION_DO_ACTION, null);
		} 

		else if (channel == TChannel.UV_LAMP
				&& event == TEvent.WORKSTATION_GUI_ACTION_FINISHED) {
			t.fireEvent(TChannel.UV_LAMP, TEvent.WORKSTATION_RELEASE_GLASS,
					null);
		} 

		else if (channel == TChannel.PAINTER
				&& event == TEvent.WORKSTATION_LOAD_FINISHED) {// added by
																// monroe
			t.fireEvent(TChannel.PAINTER, TEvent.WORKSTATION_DO_ACTION, null);
		} 

		else if (channel == TChannel.PAINTER
				&& event == TEvent.WORKSTATION_GUI_ACTION_FINISHED) {
			t.fireEvent(TChannel.PAINTER, TEvent.WORKSTATION_RELEASE_GLASS,
					null);
		} 

		else if (channel == TChannel.OVEN
				&& event == TEvent.WORKSTATION_LOAD_FINISHED) {// added by
																// monroe
			t.fireEvent(TChannel.OVEN, TEvent.WORKSTATION_DO_ACTION, null);
		} 

		else if (channel == TChannel.OVEN
				&& event == TEvent.WORKSTATION_GUI_ACTION_FINISHED) {
			t.fireEvent(TChannel.OVEN, TEvent.WORKSTATION_RELEASE_GLASS, null);
		} 

		else if (channel == TChannel.TRUCK
				&& event == TEvent.TRUCK_GUI_LOAD_FINISHED) {// added by monroe
			t.fireEvent(TChannel.TRUCK, TEvent.TRUCK_DO_EMPTY, null);
		}
	}

}