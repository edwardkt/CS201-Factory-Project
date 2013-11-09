package gui.panels.subcontrolpanels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


import engine.agent.Part;
import engine.chu.interfaces.Bin;
import gui.panels.ControlPanel;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


import transducer.TChannel;
import transducer.TEvent;

import javax.swing.*;
import javax.swing.border.*;

/**
 * The GlassMakePanel class contains buttons allowing the user to select what
 * type of glass to produce.
 */
@SuppressWarnings("serial")
public class GlassMakePanel extends JPanel
{
	Bin bin;
	public Part part = new Part("0000000000");
	JLabel nameGlassLabel = new JLabel("Quantity");
	JTextField nameGlassTF = new JTextField(3);
	JCheckBox CutterCB = new JCheckBox("1. Cut");
	JCheckBox BreakoutCB = new JCheckBox("2. Breakout");
	JCheckBox ManualBreakoutCB = new JCheckBox("3. Manual Breakout");
	JCheckBox CrossSeamerCB = new JCheckBox("5. Cross Seam");
	JCheckBox DrillCB = new JCheckBox("4. Drill");
	JCheckBox GrinderCB = new JCheckBox("6. Grind");
	JCheckBox WasherCB = new JCheckBox("7. Wash");
	JCheckBox UVLampCB = new JCheckBox("9. UV Lamp");
	JCheckBox PainterCB = new JCheckBox("8. Paint");
	JCheckBox OvenCB = new JCheckBox("10. Oven");
	
	JButton makeGlassButton = new JButton("Make Glass");
	
	CheckBoxListener CBListener = new CheckBoxListener();
	

	/** The ControlPanel this is linked to */
	private ControlPanel parent;

	/**
	 * Creates a new GlassSelect and links it to the control panel
	 * @param cp
	 *        the ControlPanel linked to it
	 */
	public GlassMakePanel(ControlPanel cp, Bin bin)
	{
		this.setBackground(Color.white);
		//this.setForeground(Color.white);
		
		this.bin=bin;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		// set up CB panel
		JPanel CBPanel = new JPanel();
		GridLayout CBgrid = new GridLayout(5,2);
		CBgrid.setVgap(2);
		CBgrid.setHgap(2);
		CBPanel.setBackground(Color.white);
		CBPanel.setLayout(CBgrid);
		
		nameGlassLabel.setForeground(Color.black);
		nameGlassLabel.setAlignmentX(this.CENTER_ALIGNMENT);
		nameGlassLabel.setVerticalAlignment(SwingConstants.CENTER);
		nameGlassLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		//nameGlassLabel.setOpaque(true);
	
		nameGlassTF.setAlignmentX(this.CENTER_ALIGNMENT);
		nameGlassTF.setMaximumSize(new Dimension(50,30));
		nameGlassTF.setPreferredSize(new Dimension(30,30));
		
		CutterCB.setSelected(false);
		CutterCB.setForeground(Color.black);
		CutterCB.setFont(new Font("SansSerif", Font.PLAIN, 12));
		CutterCB.setVerticalAlignment(SwingConstants.CENTER);
		//CutterCB.setOpaque(true);
		CutterCB.addItemListener(CBListener);
		
		BreakoutCB.setSelected(false);
		BreakoutCB.setForeground(Color.black);
		BreakoutCB.setFont(new Font("SansSerif", Font.PLAIN, 12));
		BreakoutCB.setVerticalAlignment(SwingConstants.CENTER);
		//BreakoutCB.setOpaque(true);
		BreakoutCB.addItemListener(CBListener);
		
		ManualBreakoutCB.setSelected(false);
		ManualBreakoutCB.setForeground(Color.black);
		ManualBreakoutCB.setFont(new Font("SansSerif", Font.PLAIN, 12));
		ManualBreakoutCB.setVerticalAlignment(SwingConstants.CENTER);
		//ManualBreakoutCB.setOpaque(true);
		ManualBreakoutCB.addItemListener(CBListener);
		
		CrossSeamerCB.setSelected(false);
		CrossSeamerCB.setForeground(Color.black);
		CrossSeamerCB.setFont(new Font("SansSerif", Font.PLAIN, 12));
		CrossSeamerCB.setVerticalAlignment(SwingConstants.CENTER);
		//CrossSeamerCB.setOpaque(true);
		CrossSeamerCB.addItemListener(CBListener);
		
		DrillCB.setSelected(false);
		DrillCB.setForeground(Color.black);
		DrillCB.setFont(new Font("SansSerif", Font.PLAIN, 12));
		DrillCB.setVerticalAlignment(SwingConstants.CENTER);
		//DrillCB.setOpaque(true);
		DrillCB.addItemListener(CBListener);
		
		GrinderCB.setSelected(false);
		GrinderCB.setForeground(Color.black);
		GrinderCB.setFont(new Font("SansSerif", Font.PLAIN, 12));
		GrinderCB.setVerticalAlignment(SwingConstants.CENTER);
		//GrinderCB.setOpaque(true);
		GrinderCB.addItemListener(CBListener);
		
		WasherCB.setSelected(false);
		WasherCB.setForeground(Color.black);
		WasherCB.setFont(new Font("SansSerif", Font.PLAIN, 12));
		WasherCB.setVerticalAlignment(SwingConstants.CENTER);
		//WasherCB.setOpaque(true);
		WasherCB.addItemListener(CBListener);
		
		UVLampCB.setSelected(false);
		UVLampCB.setForeground(Color.black);
		UVLampCB.setFont(new Font("SansSerif", Font.PLAIN, 12));
		UVLampCB.setVerticalAlignment(SwingConstants.CENTER);
		//UVLampCB.setOpaque(true);
		UVLampCB.addItemListener(CBListener);
		
		PainterCB.setSelected(false);
		PainterCB.setForeground(Color.black);
		PainterCB.setFont(new Font("SansSerif", Font.PLAIN, 12));
		PainterCB.setVerticalAlignment(SwingConstants.CENTER);
		//PainterCB.setOpaque(true);
		PainterCB.addItemListener(CBListener);
		
		OvenCB.setSelected(false);
		OvenCB.setForeground(Color.black);
		OvenCB.setFont(new Font("SansSerif", Font.PLAIN, 12));
		OvenCB.setVerticalAlignment(SwingConstants.CENTER);
		//OvenCB.setOpaque(true);
		OvenCB.addItemListener(CBListener);
		
		makeGlassButton.setEnabled(true);
		makeGlassButton.addActionListener(new makeGlassButtonListener());
		JPanel makeGlassButtonPanel = new JPanel();
		makeGlassButtonPanel.add(makeGlassButton);
		makeGlassButtonPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		
		CBPanel.add(CutterCB);
		CBPanel.add(BreakoutCB);
		CBPanel.add(ManualBreakoutCB);
		CBPanel.add(DrillCB);
		CBPanel.add(CrossSeamerCB);
		CBPanel.add(GrinderCB);
		CBPanel.add(WasherCB);
		CBPanel.add(PainterCB);
		CBPanel.add(UVLampCB);
		CBPanel.add(OvenCB);
		CBPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);
		this.add(CBPanel);
		this.add(nameGlassLabel);
		this.add(nameGlassTF);
		
		this.add(makeGlassButtonPanel);
		
		parent = cp;
	}

	/**
	 * Returns the parent panel
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
            
            if(e.getStateChange() == ItemEvent.SELECTED)
            {
	            if (source == CutterCB) {
	                System.out.println("cut cb selected");
	            	part.setRecipeElement(0, '1');
	            } else if (source == BreakoutCB) {
	            	System.out.println("breakout cb selected");
	            	part.setRecipeElement(1, '1');
            	}else if (source == ManualBreakoutCB) {
	            	System.out.println("manual breakout cb selected");
	            	part.setRecipeElement(2, '1');
            	} else if (source == DrillCB) {
	            	System.out.println("drill cb selected");
	            	part.setRecipeElement(3, '1');
            	} else if (source == CrossSeamerCB) {
	            	System.out.println("cross seamer cb selected");
	            	part.setRecipeElement(4, '1');
	            } else if (source == GrinderCB) {
	            	System.out.println("grinder cb selected");
	            	part.setRecipeElement(5, '1');
	            }else if (source == WasherCB) {
	            	System.out.println("washer cb selected");
	            	part.setRecipeElement(6, '1');
	            }else if (source == PainterCB) {
	            	System.out.println("painter cb selected");
	            	part.setRecipeElement(7, '1');
	            }else if (source == UVLampCB) {
	            	System.out.println("UV lamp cb selected");
	            	part.setRecipeElement(8, '1');
	            }else if (source == OvenCB) {
	            	System.out.println("oven cb selected");
	            	part.setRecipeElement(9, '1');
	            }
            }

            if (e.getStateChange() == ItemEvent.DESELECTED)
            {
            	 if (source == CutterCB) {
 	                System.out.println("cut cb deselected");
 	               part.setRecipeElement(0, '0');
            	} else if (source == BreakoutCB) {
  	            	System.out.println("breakout cb deselected");
  	            	part.setRecipeElement(1, '0');
 	            } else if (source == ManualBreakoutCB) {
 	            	System.out.println("manual breakout cb deselected");
 	            	part.setRecipeElement(2, '0');
 	           } else if (source == DrillCB) {
	            	System.out.println("drill cb deselected");
	            	part.setRecipeElement(3, '0');
 	            } else if (source == CrossSeamerCB) {
 	            	System.out.println("cross seamer cb deselected");
 	            	part.setRecipeElement(4, '0');
 	            } else if (source == GrinderCB) {
 	            	System.out.println("grinder cb deselected");
 	            	part.setRecipeElement(5, '0');
 	            }else if (source == WasherCB) {
 	            	System.out.println("washer cb deselected");
 	            	part.setRecipeElement(6, '0');
 	            }else if (source == PainterCB) {
 	            	System.out.println("painter cb deselected");
 	            	part.setRecipeElement(7, '0');
 	            }else if (source == UVLampCB) {
 	            	System.out.println("UV lamp cb deselected");
 	            	part.setRecipeElement(8, '0');
 	            }else if (source == OvenCB) {
 	            	System.out.println("oven cb deselected");
 	            	part.setRecipeElement(9, '0');
 	            }
            }
        }
    }
    
    public class makeGlassButtonListener implements ActionListener
	{
		/**
		 * Invoked whenever the button is clicked, starts the control cell Note
		 * that this button is disabled unless a kit config exists and the
		 * factory is stopped
		 */
		public void actionPerformed(ActionEvent ae)
		{
			System.out.println("makeGlass button clicked.");
			System.out.println("New Glass recipe: " + part.getRecipe());
			String text = nameGlassTF.getText();
			if(text.trim().length() == 0 )
			{
				bin.msgHereIsNewPart(part, 1);
			}
			else
			{
				bin.msgHereIsNewPart(part, Integer.parseInt(text));
			}
			nameGlassTF.setText("");
			
			if (parent.getTransducer() == null)
			{
				System.out.println("No transducer connected!");
			}
			else
			{
				//parent.getTransducer().fireEvent(TChannel.CONTROL_PANEL, TEvent.START, null);

			}
		}
	}
    
	/**
	 * Sets the bin
	 */
	public void setBin(Bin bin)
	{
		this.bin=bin;
	}
}
