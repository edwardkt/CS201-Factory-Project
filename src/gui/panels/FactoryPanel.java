
package gui.panels;

import engine.Fuks.ConveyorFamilyEdward;
import engine.Fuks.ConveyorSimpleFamilyEdward;
import engine.Sum.conveyorfamily.LeftSidePreMachineConveyorAgent;
import engine.Sum.conveyorfamily.LeftSidePreShuttleConveyorAgent;
import engine.Sum.machinefamily.LeftSideMachineAgent;
import engine.chu.agent.BinAgent;
import engine.chu.agent.ConveyorFamilyGroup;
import engine.sanders.agent.ConveyorAgentWithTruck;
import engine.sanders.agent.ConveyorFamilyContainer;
import engine.sanders.agent.RobotAgent;
import engine.tam.ConveyorFamily.ConveyorFamilyInLineStation;
import engine.tam.ShuttleFamily.ConveyorShuttleFamily;
import gui.drivers.FactoryFrame;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import transducer.TChannel;
import transducer.Transducer;

/**
 * The FactoryPanel is highest level panel in the actual kitting cell. The
 * FactoryPanel makes all the back end components, connects them to the
 * GuiComponents in the DisplayPanel. It is responsible for handing
 * communication between the back and front end.
 */
@SuppressWarnings("serial")
public class FactoryPanel extends JPanel
{
	/** The frame connected to the FactoryPanel */
	private FactoryFrame parent;

	/** The control system for the factory, displayed on right */
	private ControlPanel cPanel;

	/** The graphical representation for the factory, displayed on left */
	private DisplayPanel dPanel;

	/** Allows the control panel to communicate with the back end and give commands */
	private Transducer transducer;

	/**
	 * Constructor links this panel to its frame
	 */
	public FactoryPanel(FactoryFrame fFrame)
	{
		parent = fFrame;

		// initialize transducer
		transducer = new Transducer();
		transducer.startTransducer();

		// use default layout
		// dPanel = new DisplayPanel(this);
		// dPanel.setDefaultLayout();
		// dPanel.setTimerListeners();

		// initialize and run
		this.initialize();
		this.initializeBackEnd();
	}

	/**
	 * Initializes all elements of the front end, including the panels, and lays
	 * them out
	 */
	private void initialize()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		// initialize control panel
		cPanel = new ControlPanel(this, transducer);

		// initialize display panel
		dPanel = new DisplayPanel(this, transducer);

		// add panels in
		// JPanel tempPanel = new JPanel();
		// tempPanel.setPreferredSize(new Dimension(830, 880));
		// this.add(tempPanel);

		this.add(dPanel);
		this.add(cPanel);
	}

	/**
	 * Feel free to use this method to start all the Agent threads at the same time
	 */
	private void initializeBackEnd()
	{		

		LeftSidePreShuttleConveyorAgent cf1;
		LeftSidePreMachineConveyorAgent cf2;
		LeftSideMachineAgent breakout;
		LeftSidePreMachineConveyorAgent cf3;
		LeftSideMachineAgent manualbreakout;
		LeftSidePreShuttleConveyorAgent cf4;
		
		//Bin and Conveyor 0 add by Christina
		BinAgent bin= new BinAgent("bin", transducer, null);
		ConveyorFamilyGroup cf0 = new ConveyorFamilyGroup(transducer, null, 0, bin);
		bin.setCF(cf0);
		cPanel.setBin(bin);
		
		
		//conveyor 1-4
		
		cf1 = new LeftSidePreShuttleConveyorAgent("cf1", transducer, 1, 2, 3);
		cf2 = new LeftSidePreMachineConveyorAgent("cf2", transducer, 2, 4, 5, "BREAKOUT");
		breakout = new LeftSideMachineAgent("breakout", transducer, "BREAKOUT");
		cf3 = new LeftSidePreMachineConveyorAgent("cf3", transducer, 3, 6, 7, "MANUAL_BREAKOUT");
		manualbreakout = new LeftSideMachineAgent("manual breakout", transducer, "MANUAL_BREAKOUT");
		cf4 = new LeftSidePreShuttleConveyorAgent("cf4", transducer, 4, 8, 9);
		
		cf1.setPriorCutterMachine(cf0);
		cf1.setNextConveyor(cf2);
		
		cf2.setPriorConveyor(cf1);	
		cf2.setNextMachine(breakout);

		breakout.setPriorConveyorAgent(cf2);
		breakout.setNextPreMachineConveyorAgent(cf3);
		
		manualbreakout.setPriorConveyorAgent(cf3);
		manualbreakout.setNextPreShuttleConveyorAgent(cf4);
		
		cf3.setNextMachine(manualbreakout);
		cf3.setPriorMachine(breakout);
		
		cf4.setPriorMachine(manualbreakout);
		
		
		//start thread
		//MockConveyorFamily nextcf = new MockConveyorFamily();
		cf0.setNextcf(cf1);
		bin.startThread();
		cf1.startThread();
		cf2.startThread();
		breakout.startThread();
		cf3.startThread();
		manualbreakout.startThread();
		cf4.startThread();
	
		



		// START: conveyor families 5-7 with offline workstations

			//MockConveyorFamilyInterface cf4 = new MockConveyorFamilyInterface();


			//instantiate conveyor family 5
				ConveyorFamilyContainer cf5 = new ConveyorFamilyContainer( transducer, cf4, 0, 5, 3, TChannel.DRILL );
			//instantiate conveyor family 6
				ConveyorFamilyContainer cf6 = new ConveyorFamilyContainer( transducer, cf5, 1, 6, 4, TChannel.CROSS_SEAMER );
			//set conveyor 6 as the following conveyor family after conveyor family 5
				cf5.setCFFollow( cf6 );
			//instantiate conveyor family 7
				ConveyorFamilyContainer cf7 = new ConveyorFamilyContainer( transducer, cf6, 2, 7, 5, TChannel.GRINDER );
			//set conveyor 7 as the following conveyor family after conveyor family 6

			//instantiate conveyor family 8
				ConveyorFamilyEdward cf8=new ConveyorFamilyEdward(8,"Washer_Edward",transducer,TChannel.WASHER,6); //0000001000
			//instantiate conveyor family 9
				ConveyorSimpleFamilyEdward cf9 =new ConveyorSimpleFamilyEdward(9,"Shuttle_Edward", transducer);
			//instantiate conveyor family 10
				ConveyorFamilyEdward cf10=new ConveyorFamilyEdward(10,"Painter_Edward",transducer,TChannel.PAINTER,7); //0000001000
			//instantiate conveyor family 11
				ConveyorFamilyInLineStation cf11=new ConveyorFamilyInLineStation("Tam",transducer,11,8,TChannel.UV_LAMP); //0000000100

				cf4.setNextConveyorFamily(cf5);
				cf6.setCFFollow( cf7 );
				//set conveyor 8 as the following conveyor family after conveyor family 7
				cf7.setCFFollow( cf8 );
				//set conveyor 7 as  conveyor family before conveyor family 8
				cf8.setbackCF(cf7);
				//set conveyor 9 as the following conveyor family after conveyor family 8
				cf8.setfrontCF(cf9);
				//set conveyor 8 as  conveyor family before conveyor family 9
				cf9.setbackCF(cf8);
				//set conveyor 10 as the following conveyor family after conveyor family 9
				cf9.setfrontCF(cf10);
				//set conveyor 9 as  conveyor family before conveyor family 10
				cf10.setbackCF(cf9);
				//set conveyor 11 as the following conveyor family after conveyor family 10
				cf10.setfrontCF(cf11);
				//set conveyor 10 as  conveyor family before conveyor family 11
				cf11.setBackCF(cf10);




			//instantiate workstation robots for conveyor family 5
				RobotAgent robot0 = new RobotAgent( cf5, transducer, TChannel.DRILL, 0, "Robot0" );
				RobotAgent robot1 = new RobotAgent( cf5, transducer, TChannel.DRILL, 1, "Robot1" );
			//instantiate workstation robots for conveyor family 6
				RobotAgent robot2 = new RobotAgent( cf6, transducer, TChannel.CROSS_SEAMER, 0, "Robot2" );
				RobotAgent robot3 = new RobotAgent( cf6, transducer, TChannel.CROSS_SEAMER, 1, "Robot3" );
			//instantiate workstation robots for conveyor family 7
				RobotAgent robot4 = new RobotAgent( cf7, transducer, TChannel.GRINDER, 0, "Robot4" );
				RobotAgent robot5 = new RobotAgent( cf7, transducer, TChannel.GRINDER, 1, "Robot5" );

			//start all robot threads
				robot0.startThread();
				robot1.startThread();
				robot2.startThread();
				robot3.startThread();
				robot4.startThread();
				robot5.startThread();	



				ConveyorShuttleFamily cf12 =new ConveyorShuttleFamily("Tam2", transducer,12);
				cf11.setNextCF(cf12);
				cf12.setBackCF(cf11);

				ConveyorFamilyInLineStation cf13 =new ConveyorFamilyInLineStation("Tam3",transducer,13,9,TChannel.OVEN); //0000000010
				cf12.setNextCF(cf13);
				cf13.setBackCF(cf12);

				ConveyorAgentWithTruck cf14 = new ConveyorAgentWithTruck( cf13, transducer, 14, "Conveyor14" );	
				cf13.setNextCF(cf14);

				cf8.startFamily();
				cf9.startFamily();
				cf10.startFamily();
				cf11.startThreads();
				cf12.startThreads();
				cf13.startThreads();
				cf14.startThread();
		//END: conveyor 14 with truck control	


		/*
			NOTE: You can observe the factory work on one piece of glass without agents by leaving the below line alone. Comment it out when you are ready to start working.
		*/		

		//GuiTestSM test = new GuiTestSM( transducer, cf5, cf7, cf14, cf11, bin, cf0);
		System.out.println("Back end initialization finished.");
	}

	/**
	 * Returns the parent frame of this panel
	 * 
	 * @return the parent frame
	 */
	public FactoryFrame getGuiParent()
	{
		return parent;
	}

	/**
	 * Returns the control panel
	 * 
	 * @return the control panel
	 */
	public ControlPanel getControlPanel()
	{
		return cPanel;
	}

	/**
	 * Returns the display panel
	 * 
	 * @return the display panel
	 */
	public DisplayPanel getDisplayPanel()
	{
		return dPanel;
	}
}