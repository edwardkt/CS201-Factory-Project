
package gui.panels.subcontrolpanels;

import gui.panels.ControlPanel;
import gui.panels.subcontrolpanels.GlassMakePanel.CheckBoxListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

/**
 * The BreakPanel is responsible for initiating and managing non-normative
 * situations. It contains buttons for each possible non-norm.
 * 
 * The non-normative situations are:
 * 1.
 * 2.
 * 3.
 * 4.
 * 5.
 * 6.
 * 7.
 * 8.
 */
@SuppressWarnings("serial")
public class BreakPanel extends JPanel implements TReceiver
{

	/** The control panel this is linked to */
	ControlPanel parent;

	/** List of checkboxes for each popup */
	List<JCheckBox> breakPopUpCBs;
	
	/** List of checkboxes for each workstation */
	List<JCheckBox> breakWorkstationCBs;
	
	/** List of checkboxes for each conveyor */
	List<JCheckBox> breakConveyorCBs;

	/** Title label **/
	JLabel titleLabel;
	
	/** Break PopUp label **/
	JLabel breakPopUpLabel;
	
	/** Break Conveyor label **/
	JLabel breakConveyorLabel;
	
	/** Break Workstation label **/
	JLabel breakWorkstationLabel;
	
	/** CheckBox Listener **/
	CheckBoxListener CBListener = new CheckBoxListener();

	
	/** Transducer **/
	Transducer transducer = null;

	/**
	 * Creates a new HavocPanel and links the control panel to it
	 * 
	 * @param cp
	 *        the ControlPanel linked to it
	 */
	public BreakPanel(ControlPanel cp)
	{
		parent = cp;

		this.setBackground(Color.black);
		this.setForeground(Color.black);

		// set up layout
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		// set up WorkstationCB panel
		JPanel PopUpCBPanel = new JPanel();
		GridLayout PopUpCBgrid = new GridLayout(1, 3);
		PopUpCBgrid.setVgap(2);
		PopUpCBgrid.setHgap(2);
		PopUpCBPanel.setBackground(Color.white);
		PopUpCBPanel.setLayout(PopUpCBgrid);

		// set up WorkstationCB panel
		JPanel WorkstationCBPanel = new JPanel();
		GridLayout WorkstationCBgrid = new GridLayout(7, 2);
		WorkstationCBgrid.setVgap(2);
		WorkstationCBgrid.setHgap(2);
		WorkstationCBPanel.setBackground(Color.white);
		WorkstationCBPanel.setLayout(WorkstationCBgrid);
		
		// set up ConveyorCB panel
		JPanel ConveyorCBPanel = new JPanel();
		GridLayout ConveyorCBgrid = new GridLayout(3, 5);
		ConveyorCBgrid.setVgap(2);
		ConveyorCBgrid.setHgap(2);
		ConveyorCBPanel.setBackground(Color.white);
		ConveyorCBPanel.setLayout(ConveyorCBgrid);
		
		// make title
		titleLabel = new JLabel("BREAK");
		titleLabel.setForeground(Color.white);
		titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 22));
		JPanel titleLabelPanel = new JPanel();
		titleLabelPanel.add(titleLabel);
		// titleLabelPanel.setAlignmentY(JLabel.CENTER_ALIGNMENT);
		titleLabelPanel.setBackground(Color.black);
		
		// make PopUp Title
		breakPopUpLabel = new JLabel("Break PopUp");
		breakPopUpLabel.setForeground(Color.white);
		breakPopUpLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
		JPanel breakPopUpPanel = new JPanel();
		breakPopUpPanel.add(breakPopUpLabel);
		// breakPopUpPanel.setAlignmentY(JLabel.CENTER_ALIGNMENT);
		breakPopUpPanel.setBackground(Color.black);
		
		// make breakWorkstation Title
		breakWorkstationLabel = new JLabel("Break Machine");
		breakWorkstationLabel.setForeground(Color.white);
		breakWorkstationLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
		JPanel breakWorkstationPanel = new JPanel();
		breakWorkstationPanel.add(breakWorkstationLabel);
		// breakWorkstationPanel.setAlignmentY(JLabel.CENTER_ALIGNMENT);
		breakWorkstationPanel.setBackground(Color.black);
		
		// make breakConveyor Title
		breakConveyorLabel = new JLabel("Break Conveyor");
		breakConveyorLabel.setForeground(Color.white);
		breakConveyorLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
		JPanel breakConveyorPanel = new JPanel();
		breakConveyorPanel.add(breakConveyorLabel);
		// breakConveyorPanel.setAlignmentY(JLabel.CENTER_ALIGNMENT);
		breakConveyorPanel.setBackground(Color.black);

		breakPopUpCBs = new ArrayList<JCheckBox>(3);
		breakPopUpCBs.add(new JCheckBox("Drill"));
		breakPopUpCBs.add(new JCheckBox("Cross Seamer"));
		breakPopUpCBs.add(new JCheckBox("Grinder"));
		
		//make CBs
		breakWorkstationCBs = new ArrayList<JCheckBox>(13);
		breakWorkstationCBs.add(new JCheckBox("Cutter"));
		breakWorkstationCBs.add(new JCheckBox("Breakout"));
		breakWorkstationCBs.add(new JCheckBox("Manual Breakout"));
		breakWorkstationCBs.add(new JCheckBox("Drill #1"));
		breakWorkstationCBs.add(new JCheckBox("Drill #2"));
		breakWorkstationCBs.add(new JCheckBox("Cross Seamer #1"));
		breakWorkstationCBs.add(new JCheckBox("Cross Seamer #2"));
		breakWorkstationCBs.add(new JCheckBox("Grinder #1"));
		breakWorkstationCBs.add(new JCheckBox("Grinder #2"));
		breakWorkstationCBs.add(new JCheckBox("Washer"));
		breakWorkstationCBs.add(new JCheckBox("Painter"));
		breakWorkstationCBs.add(new JCheckBox("UV Lamp"));
		breakWorkstationCBs.add(new JCheckBox("Oven"));
		
		
		
		//make CBs
		breakConveyorCBs = new ArrayList<JCheckBox>(15);
		breakConveyorCBs.add(new JCheckBox("0"));
		breakConveyorCBs.add(new JCheckBox("1"));
		breakConveyorCBs.add(new JCheckBox("2"));
		breakConveyorCBs.add(new JCheckBox("3"));
		breakConveyorCBs.add(new JCheckBox("4"));
		breakConveyorCBs.add(new JCheckBox("5"));
		breakConveyorCBs.add(new JCheckBox("6"));
		breakConveyorCBs.add(new JCheckBox("7"));
		breakConveyorCBs.add(new JCheckBox("8"));
		breakConveyorCBs.add(new JCheckBox("9"));
		breakConveyorCBs.add(new JCheckBox("10"));
		breakConveyorCBs.add(new JCheckBox("11"));
		breakConveyorCBs.add(new JCheckBox("12"));
		breakConveyorCBs.add(new JCheckBox("13"));
		breakConveyorCBs.add(new JCheckBox("14"));
		breakConveyorCBs.add(new JCheckBox("Truck"));
		
		for(int i = 0; i< 3; i++){
			breakPopUpCBs.get(i).setSelected(false);
			breakPopUpCBs.get(i).setForeground(Color.black);
			breakPopUpCBs.get(i).setFont(new Font("SansSerif", Font.PLAIN, 12));
			breakPopUpCBs.get(i).setVerticalAlignment(SwingConstants.CENTER);
			breakPopUpCBs.get(i).addItemListener(CBListener);
		}
		
		for(int i = 0; i< 13; i++){
			breakWorkstationCBs.get(i).setSelected(false);
			breakWorkstationCBs.get(i).setForeground(Color.black);
			breakWorkstationCBs.get(i).setFont(new Font("SansSerif", Font.PLAIN, 12));
			breakWorkstationCBs.get(i).setVerticalAlignment(SwingConstants.CENTER);
			breakWorkstationCBs.get(i).addItemListener(CBListener);
		}
		
		
		for(int i = 0; i< 16; i++){
			breakConveyorCBs.get(i).setSelected(false);
			breakConveyorCBs.get(i).setForeground(Color.black);
			breakConveyorCBs.get(i).setFont(new Font("SansSerif", Font.PLAIN, 12));
			breakConveyorCBs.get(i).setVerticalAlignment(SwingConstants.CENTER);
			breakConveyorCBs.get(i).addItemListener(CBListener);
		}
		
		// add to panel
		
		this.add(breakPopUpPanel);
		for (JCheckBox c : breakPopUpCBs)
		{
			PopUpCBPanel.add(c);
		}
		this.add(PopUpCBPanel);

		JPanel colorLinesPanel1 = new JPanel();
		colorLinesPanel1.setPreferredSize(new Dimension(350, 2));
		colorLinesPanel1.setBackground(Color.black);
		ImageIcon cl = new ImageIcon("imageicons/singleColoredLine.png");
		JLabel clLabel1 = new JLabel(cl);
		colorLinesPanel1.add(clLabel1);
		this.add(colorLinesPanel1);

		this.add(breakWorkstationPanel);
		
		for (JCheckBox c : breakWorkstationCBs)
		{
			WorkstationCBPanel.add(c);
		}
		WorkstationCBPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);
		this.add(WorkstationCBPanel);
		
		JPanel colorLinesPanel3 = new JPanel();
		colorLinesPanel3.setPreferredSize(new Dimension(350, 40));
		colorLinesPanel3.setBackground(Color.black);
		JLabel clLabel3 = new JLabel(cl);
		colorLinesPanel3.add(clLabel3);
		this.add(colorLinesPanel3);
		
		this.add(breakConveyorPanel);
		
		for (JCheckBox c : breakConveyorCBs)
		{
			ConveyorCBPanel.add(c);
		}
		ConveyorCBPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);
		this.add(ConveyorCBPanel);

		JPanel colorLinesPanel2 = new JPanel();
		colorLinesPanel2.setPreferredSize(new Dimension(350, 40));
		colorLinesPanel2.setBackground(Color.black);
		JLabel clLabel2 = new JLabel();
		colorLinesPanel2.add(clLabel2);
		this.add(colorLinesPanel2);
	}

	/**
	 * Returns the parent panel
	 * 
	 * @return the parent panel
	 */
	public ControlPanel getGuiParent()
	{
		return parent;
	}

	
	 //Listens to the check boxes events
    public class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
  
            Object source = e.getSource();
            Integer[] args = new Integer[1];
			
            
            if(e.getStateChange() == ItemEvent.SELECTED)
            {
	            if (source == breakConveyorCBs.get(0)) {
	            	System.out.println("c0 selected");
	            	args[0] = 0;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_BROKEN, args);
	            } else  if (source == breakConveyorCBs.get(1)) {
	            	System.out.println("c1 selected");
	            	args[0] = 1;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_BROKEN, args);
	            	
	            }else if (source == breakConveyorCBs.get(2)) {
	            	System.out.println("c2 selected");
	            	args[0] = 2;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_BROKEN, args);
	            	
            	} else if (source == breakConveyorCBs.get(3)) {
	            	System.out.println("c3 selected");
	            	args[0] = 3;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_BROKEN, args);
	            	
            	} else if (source == breakConveyorCBs.get(4)) {
	            	System.out.println("c4 selected");
	            	args[0] = 4;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_BROKEN, args);
	            	
	            } else if (source == breakConveyorCBs.get(5)) {
	            	System.out.println("c5 selected");
	            	args[0] = 5;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_BROKEN, args);
	            	
	            }else if (source == breakConveyorCBs.get(6))	{
	            	System.out.println("c6 selected");
	            	args[0] = 6;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_BROKEN, args);
	            	
	            }else if (source == breakConveyorCBs.get(7))	{
	            	System.out.println("c7 selected");
	            	args[0] = 7;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_BROKEN, args);
	            	
	            }else if (source == breakConveyorCBs.get(8))	{
	            	System.out.println("c8 selected");
	            	args[0] = 8;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_BROKEN, args);
	            	
	            }else if (source == breakConveyorCBs.get(9))	{
	            	System.out.println("c9 selected");
	            	args[0] = 9;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_BROKEN, args);
	            	
	            }else if (source == breakConveyorCBs.get(10))	{
	            	System.out.println("c10 selected");
	            	args[0] = 10;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_BROKEN, args);
	            	
	            }else if (source == breakConveyorCBs.get(11))	{
	            	System.out.println("c11 selected");
	            	args[0] = 11;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_BROKEN, args);
	            	
	            }else if (source == breakConveyorCBs.get(12))	{
	            	System.out.println("c12 selected");
	            	args[0] = 12;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_BROKEN, args);
	            	
	            }else if (source == breakConveyorCBs.get(13))	{
	            	System.out.println("c13 selected");
	            	args[0] = 13;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_BROKEN, args);
	            	
	            }else if (source == breakConveyorCBs.get(14))	{
	            	System.out.println("c14 selected");
	            	args[0] = 14;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_BROKEN, args);
	            	
	            }else if (source == breakConveyorCBs.get(15))	{
	            	System.out.println("Truck selected");
	            	transducer.fireEvent(TChannel.TRUCK, TEvent.TRUCK_BROKEN, null);
	            	
	            }
	            //begin workstation
	            if (source == breakWorkstationCBs.get(0)) {
	            	System.out.println("broke cutter");
	            	
	            	transducer.fireEvent(TChannel.CUTTER, TEvent.WORKSTATION_BROKEN, null);
	            	
	            } else  if (source == breakWorkstationCBs.get(1)) {
	            	System.out.println("broke breakout");
	            	
	            	transducer.fireEvent(TChannel.BREAKOUT, TEvent.WORKSTATION_BROKEN, null);
	            	
	            }else if (source == breakWorkstationCBs.get(2)) {
	            	System.out.println("broke manual breakout");
	            	
	            	transducer.fireEvent(TChannel.MANUAL_BREAKOUT, TEvent.WORKSTATION_BROKEN, null);
	            	
            	} else if (source == breakWorkstationCBs.get(3)) {
	            	System.out.println("broke drill #1");
	            	args[0] = 0;
	            	transducer.fireEvent(TChannel.DRILL, TEvent.WORKSTATION_BROKEN, args);
            	} else if (source == breakWorkstationCBs.get(4)) {
	            	System.out.println("broke drill #2");
	            	args[0] = 1;
	            	transducer.fireEvent(TChannel.DRILL, TEvent.WORKSTATION_BROKEN, args);
            	
            	} else if (source == breakWorkstationCBs.get(5)) {
	            	System.out.println("broke cross seamer #1");
	            	args[0] = 0;
	            	transducer.fireEvent(TChannel.CROSS_SEAMER, TEvent.WORKSTATION_BROKEN, args);
            	} else if (source == breakWorkstationCBs.get(6)) {
	            	System.out.println("broke cross seamer #2");
	            	args[0] = 1;
	            	transducer.fireEvent(TChannel.CROSS_SEAMER, TEvent.WORKSTATION_BROKEN, args);
	            		
	            } else if (source == breakWorkstationCBs.get(7)) {
	            	System.out.println("broke grinder #1");
	            	args[0] = 0;
	            	transducer.fireEvent(TChannel.GRINDER, TEvent.WORKSTATION_BROKEN, args);
	            } else if (source == breakWorkstationCBs.get(8)) {
	            	System.out.println("broke grinder #2");
	            	args[0] = 1;
	            	transducer.fireEvent(TChannel.GRINDER, TEvent.WORKSTATION_BROKEN, args);
	            		
	            }else if (source == breakWorkstationCBs.get(9))	{
	            	System.out.println("broke washer");
	            	transducer.fireEvent(TChannel.WASHER, TEvent.WORKSTATION_BROKEN, null);
	            	
	            }else if (source == breakWorkstationCBs.get(10))	{
	            	System.out.println("broke painter");
	            	transducer.fireEvent(TChannel.PAINTER, TEvent.WORKSTATION_BROKEN, null);
	            	
	            }else if (source == breakWorkstationCBs.get(11))	{
	            	System.out.println("broke UV Lamp");
	            	transducer.fireEvent(TChannel.UV_LAMP, TEvent.WORKSTATION_BROKEN, null);
	            	
	            }else if (source == breakWorkstationCBs.get(12))	{
	            	System.out.println("broke oven");
	            	transducer.fireEvent(TChannel.OVEN, TEvent.WORKSTATION_BROKEN, null);	
	            //begin PopUp
	            }else if (source == breakPopUpCBs.get(0))	{
	            	System.out.println("broke drill popup");
	            	args[0] = 0;
	            	transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_BROKEN, args);	
	            
		        }else if (source == breakPopUpCBs.get(1))	{
		        	System.out.println("broke cross seamer popup");
		        	args[0] = 1;
		        	transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_BROKEN, args);	
		        
			    }else if (source == breakPopUpCBs.get(2))	{
			    	System.out.println("broke grinder popup");
			    	args[0] = 2;
			    	transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_BROKEN, args);	
			    }
            }
	            

            if (e.getStateChange() == ItemEvent.DESELECTED)
            {
            	if (source == breakConveyorCBs.get(0)) {
	            	System.out.println("c0 deselected");
	            	args[0] = 0;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_FIXED, args);
	            	
	            } else  if (source == breakConveyorCBs.get(1)) {
	            	System.out.println("c1 deselected");
	            	args[0] = 1;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_FIXED, args);
	            	
	            }else if (source == breakConveyorCBs.get(2)) {
	            	System.out.println("c2 deselected");
	            	args[0] = 2;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_FIXED, args);
	            	
            	} else if (source == breakConveyorCBs.get(3)) {
	            	System.out.println("c3 deselected");
	            	args[0] = 3;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_FIXED, args);
	            	
            	} else if (source == breakConveyorCBs.get(4)) {
	            	System.out.println("c4 deselected");
	            	args[0] = 4;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_FIXED, args);
	            	
	            } else if (source == breakConveyorCBs.get(5)) {
	            	System.out.println("c5 deselected");
	            	args[0] = 5;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_FIXED, args);
	            	
	            }else if (source == breakConveyorCBs.get(6))	{
	            	System.out.println("c6 deselected");
	            	args[0] = 6;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_FIXED, args);
	            	
	            }else if (source == breakConveyorCBs.get(7))	{
	            	System.out.println("c7 deselected");
	            	args[0] = 7;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_FIXED, args);
	            	
	            }else if (source == breakConveyorCBs.get(8))	{
	            	System.out.println("c8 deselected");
	            	args[0] = 8;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_FIXED, args);
	            	
	            }else if (source == breakConveyorCBs.get(9))	{
	            	System.out.println("c9 deselected");
	            	args[0] = 9;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_FIXED, args);
	            	
	            }else if (source == breakConveyorCBs.get(10))	{
	            	System.out.println("c10 deselected");
	            	args[0] = 10;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_FIXED, args);
	            	
	            }else if (source == breakConveyorCBs.get(11))	{
	            	System.out.println("c11 deselected");
	            	args[0] = 11;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_FIXED, args);
	            	
	            }else if (source == breakConveyorCBs.get(12))	{
	            	System.out.println("c12 deselected");
	            	args[0] = 12;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_FIXED, args);
	            	
	            }else if (source == breakConveyorCBs.get(13))	{
	            	System.out.println("c13 deselected");
	            	args[0] = 13;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_FIXED, args);
	            	
	            }else if (source == breakConveyorCBs.get(14))	{
	            	System.out.println("c14 deselected");
	            	args[0] = 14;
	            	transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_FIXED, args);
	            	
	            }else if (source == breakConveyorCBs.get(15))	{
	            	System.out.println("Truck deselected");
	            	transducer.fireEvent(TChannel.TRUCK, TEvent.TRUCK_FIXED, null);
	            }
            	
	            //begin workstation
	            if (source == breakWorkstationCBs.get(0)) {
	            	System.out.println("fix cutter");
	            	transducer.fireEvent(TChannel.CUTTER, TEvent.WORKSTATION_FIXED, null);
	            	
	            } else  if (source == breakWorkstationCBs.get(1)) {
	            	System.out.println("fix breakout");
	            	transducer.fireEvent(TChannel.BREAKOUT, TEvent.WORKSTATION_FIXED, null);
	            	
	            }else if (source == breakWorkstationCBs.get(2)) {
	            	System.out.println("fix manual breakout");
	            	transducer.fireEvent(TChannel.MANUAL_BREAKOUT, TEvent.WORKSTATION_FIXED, null);
	            	
            	} else if (source == breakWorkstationCBs.get(3)) {
	            	System.out.println("fix drill #1");
	            	args[0] = 0;
	            	transducer.fireEvent(TChannel.DRILL, TEvent.WORKSTATION_FIXED, args);
            	} else if (source == breakWorkstationCBs.get(4)) {
	            	System.out.println("fix drill #2");
	            	args[0] = 1;
	            	transducer.fireEvent(TChannel.DRILL, TEvent.WORKSTATION_FIXED, args);
	            		
            	} else if (source == breakWorkstationCBs.get(5)) {
	            	System.out.println("fix cross seamer #1");
	            	args[0] = 0;
	            	transducer.fireEvent(TChannel.CROSS_SEAMER, TEvent.WORKSTATION_FIXED, args);
            	} else if (source == breakWorkstationCBs.get(6)) {
	            	System.out.println("fix cross seamer #2");
	            	args[0] = 1;
	            	transducer.fireEvent(TChannel.CROSS_SEAMER, TEvent.WORKSTATION_FIXED, args);
	            		
	            } else if (source == breakWorkstationCBs.get(7)) {
	            	System.out.println("fix grinder #1");
	            	args[0] = 0;
	            	transducer.fireEvent(TChannel.GRINDER, TEvent.WORKSTATION_FIXED, args);
	            } else if (source == breakWorkstationCBs.get(8)) {
	            	System.out.println("fix grinder #2");
	            	args[0] = 1;
	            	transducer.fireEvent(TChannel.GRINDER, TEvent.WORKSTATION_FIXED, args);
	            		
	            }else if (source == breakWorkstationCBs.get(9))	{
	            	System.out.println("fix washer");
	            	transducer.fireEvent(TChannel.WASHER, TEvent.WORKSTATION_FIXED, null);
	            	
	            }else if (source == breakWorkstationCBs.get(10))	{
	            	System.out.println("fix painter");
	            	transducer.fireEvent(TChannel.PAINTER, TEvent.WORKSTATION_FIXED, null);
	            	
	            }else if (source == breakWorkstationCBs.get(11))	{
	            	System.out.println("fix UV Lamp");
	            	transducer.fireEvent(TChannel.UV_LAMP, TEvent.WORKSTATION_FIXED, null);
	            	
	            }else if (source == breakWorkstationCBs.get(12))	{
	            	System.out.println("fix oven");
	            	transducer.fireEvent(TChannel.OVEN, TEvent.WORKSTATION_FIXED, null);
	            	
	            	//begin PopUp
	            }else if (source == breakPopUpCBs.get(0))	{
	            	System.out.println("fixed drill popup");
	            	args[0] = 0;
	            	transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_FIXED, args);	
	            
		        }else if (source == breakPopUpCBs.get(1))	{
		        	System.out.println("fixed cross seamer popup");
		        	args[0] = 1;
		        	transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_FIXED, args);	
		        
			    }else if (source == breakPopUpCBs.get(2))	{
			    	System.out.println("fixed grinder popup");
			    	args[0] = 2;
			    	transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_FIXED, args);	
			    }
 	            
            }
        }
    }
    
    public void setTransducer(Transducer transducer)
    {
    	this.transducer=transducer;
    	this.transducer.register(this, TChannel.GUI);
    }

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if(channel==TChannel.GUI && event==TEvent.WORKSTATION_BREAK_GLASS)
		{
			for(int i=0; i<breakWorkstationCBs.size(); i++)
			if(args[0].equals(i))
			{
				//breakWorkstationCBs.get(i).setSelected(true);
			}
		}
		
	}

}
