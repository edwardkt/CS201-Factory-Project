
package gui.panels.subcontrolpanels;

import gui.components.GUIComponentOffline;
import gui.drivers.FactoryFrame;
import gui.panels.ControlPanel;
import gui.panels.subcontrolpanels.StatePanel.SpeedSliderListener;

import java.awt.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

/**
 * The NonNormPanel is responsible for initiating and managing non-normative
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
public class NonNormPanel extends JPanel implements TReceiver
{
	/** The number of different havoc actions that exist */
	public static final int NUM_NON_NORMS = 4;

	/** The control panel this is linked to */
	ControlPanel parent;

	/** List of buttons for each non-norm */
	List<JButton> nonNormButtons;
	
	/** Title label **/
	JLabel titleLabel;
	
	/** Event label **/
	JLabel sliderLabel;
	
	/** Event label **/
	JLabel sensorLabel;
	
	/** Transducer **/
	Transducer transducer = null;
	
	/** The lowest value of the speed slider (corresponds to slow factory) */
	public static final int MIN_SLIDER_VALUE = 0;

	/** The highest value of the speed slider (corresponds to fast factory) */
	public static final int MAX_SLIDER_VALUE = 100;
	
	/** Slider controls how fast the factory should run */
	JSlider speedSlider;
	
	/**
	 * Creates a new HavocPanel and links the control panel to it
	 * 
	 * @param cp
	 *        the ControlPanel linked to it
	 */
	public NonNormPanel(ControlPanel cp)
	{
		parent = cp;

		this.setBackground(Color.black);
		this.setForeground(Color.black);

		// set up layout
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// set up button panel
		JPanel buttonPanel = new JPanel();
		GridLayout grid = new GridLayout(NUM_NON_NORMS / 2, 2);
		grid.setVgap(2);
		grid.setHgap(2);
		buttonPanel.setBackground(Color.black);
		buttonPanel.setLayout(grid);
		
		// set up CB panel
		JPanel CBPanel = new JPanel();
		GridLayout CBgrid = new GridLayout(3, 5);
		CBgrid.setVgap(2);
		CBgrid.setHgap(2);
		CBPanel.setBackground(Color.white);
		CBPanel.setLayout(CBgrid);
		
		// make title
		titleLabel = new JLabel("NON NORMATIVES");
		titleLabel.setForeground(Color.white);
		titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 22));
		JPanel titleLabelPanel = new JPanel();
		titleLabelPanel.add(titleLabel);
		// titleLabelPanel.setAlignmentY(JLabel.CENTER_ALIGNMENT);
		titleLabelPanel.setBackground(Color.black);
	
		
		// initialize timer slider
		int initialSliderValue = 1;

		System.out.println("Initial Speed Slider Value: " + initialSliderValue);
		speedSlider = new JSlider(MIN_SLIDER_VALUE, MAX_SLIDER_VALUE, initialSliderValue);
		
		speedSlider.setToolTipText("Set Offline Station Animation Speed");
		
		// setup sliders
		speedSlider.addChangeListener(new SpeedSliderListener());
		speedSlider.setSnapToTicks(false);
		speedSlider.setPreferredSize(new Dimension(200, 20));
		speedSlider.setBackground(Color.black);
		JPanel stateSliderPanel = new JPanel();
		stateSliderPanel.setPreferredSize(new Dimension(300, 24));
		stateSliderPanel.setBackground(Color.black);
		
		// stateSliderPanel.setLayout(new GridLayout(1, 3));
		stateSliderPanel.setLayout(new FlowLayout());
		JLabel slow = new JLabel("FAST");
		slow.setForeground(Color.white);
		slow.setFont(new Font("SansSerif", Font.PLAIN, 12));
		stateSliderPanel.add(slow);
		stateSliderPanel.add(speedSlider);
		JLabel fast = new JLabel("SLOW");
		fast.setForeground(Color.white);
		fast.setFont(new Font("SansSerif", Font.PLAIN, 12));
		stateSliderPanel.add(fast);
		
		// make slider title
		sliderLabel = new JLabel("Offline Component Processing Speed");
		sliderLabel.setForeground(Color.white);
		sliderLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
		JPanel sliderLabelPanel = new JPanel();
		sliderLabelPanel.add(sliderLabel);
		// eventLabelPanel.setAlignmentY(JLabel.CENTER_ALIGNMENT);
		sliderLabelPanel.setBackground(Color.black);
		
		// make sensor title
		sensorLabel = new JLabel("Sensor Notification");
		sensorLabel.setForeground(Color.white);
		sensorLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
		JPanel sensorLabelPanel = new JPanel();
		sensorLabelPanel.add(sensorLabel);
		// eventLabelPanel.setAlignmentY(JLabel.CENTER_ALIGNMENT);
		sensorLabelPanel.setBackground(Color.black);
		
		
		// make buttons
		nonNormButtons = new ArrayList<JButton>(NUM_NON_NORMS);
		nonNormButtons.add(new JButton("NON NORM 1"));
		nonNormButtons.add(new JButton("NON NORM 2"));
		nonNormButtons.add(new JButton("NON NORM 3"));
		nonNormButtons.add(new JButton("NON NORM 4"));
		/*nonNormButtons.add(new JButton("NON NORM 5"));
		nonNormButtons.add(new JButton("NON NORM 6"));
		nonNormButtons.add(new JButton("NON NORM 7"));
		nonNormButtons.add(new JButton("NON NORM 8"));
		*/
		// add listeners
		nonNormButtons.get(0).addActionListener(new NonNorm1Listener());
		nonNormButtons.get(1).addActionListener(new NonNorm2Listener());
		nonNormButtons.get(2).addActionListener(new NonNorm3Listener());
		nonNormButtons.get(3).addActionListener(new NonNorm4Listener());
		/*
		nonNormButtons.get(4).addActionListener(new NonNorm5Listener());
		nonNormButtons.get(5).addActionListener(new NonNorm6Listener());
		nonNormButtons.get(6).addActionListener(new NonNorm7Listener());
		nonNormButtons.get(7).addActionListener(new NonNorm8Listener());
		 */

		for (int i = 0; i < NUM_NON_NORMS; i++)
		{
//			nonNormButtons.get(i).setBackground(Color.white);
//			nonNormButtons.get(i).setForeground(Color.black);
//			nonNormButtons.get(i).setFont(new Font("SansSerif", Font.BOLD, 14));
//			nonNormButtons.get(i).setOpaque(true);
//			nonNormButtons.get(i).setBorderPainted(false);
//			nonNormButtons.get(i).setSize(20, 30);
//			nonNormButtons.get(i).setMinimumSize(new Dimension(20, 40));
//			nonNormButtons.get(i).setMaximumSize(new Dimension(20, 40));
//			nonNormButtons.get(i).setPreferredSize(new Dimension(20, 40));
			nonNormButtons.get(i).setEnabled(false);
		}

		// add to panel
		this.add(titleLabelPanel);

		JPanel colorLinesPanel1 = new JPanel();
		colorLinesPanel1.setPreferredSize(new Dimension(350, 2));
		colorLinesPanel1.setBackground(Color.black);
		ImageIcon cl = new ImageIcon("imageicons/singleColoredLine.png");
		JLabel clLabel1 = new JLabel(cl);
		colorLinesPanel1.add(clLabel1);
		this.add(colorLinesPanel1);
		
		this.add(sliderLabelPanel);
		this.add(stateSliderPanel);
		
		JPanel colorLinesPanel3 = new JPanel();
		colorLinesPanel3.setPreferredSize(new Dimension(350, 40));
		colorLinesPanel3.setBackground(Color.black);
		JLabel clLabel3 = new JLabel(cl);
		colorLinesPanel3.add(clLabel3);
		this.add(colorLinesPanel3);
		
		this.add(sensorLabelPanel);
		for (JButton j : nonNormButtons)
		{
			buttonPanel.add(j);
		}
		buttonPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);
		this.add(buttonPanel);

		JPanel colorLinesPanel2 = new JPanel();
		colorLinesPanel2.setPreferredSize(new Dimension(350, 40));
		colorLinesPanel2.setBackground(Color.black);
		JLabel clLabel2 = new JLabel();
		colorLinesPanel2.add(clLabel2);
		this.add(colorLinesPanel2);
//		this.add(eventTitleLabelPanel);
//		this.add(eventLabelPanel);
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

	/**
	 * Non-norm 1
	 */
	public class NonNorm1Listener implements ActionListener
	{
		/**
		 * Invoked whenever the button is pressed
		 */
		public void actionPerformed(ActionEvent ae)
		{
			String text;
			String temp[];
			Integer[] args = new Integer[1];
			
			text = nonNormButtons.get(0).getText();
			temp=text.split("#");
			args[0] = Integer.parseInt(temp[1]);
			
			System.out.println("button 1 pressed with args: "+ args[0]);
			transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_FIXED, args);
			nonNormButtons.get(0).setEnabled(false);
			
		}
	}

	/**
	 * Non-norm 2
	 */
	public class NonNorm2Listener implements ActionListener
	{
		/**
		 * Invoked whenever the button is pressed
		 */
		public void actionPerformed(ActionEvent ae)
		{
			String text;
			String temp[];
			Integer[] args = new Integer[1];
			
			text = nonNormButtons.get(1).getText();
			temp=text.split("#");
			args[0] = Integer.parseInt(temp[1]);
			
			System.out.println("button 2 pressed with args: "+ args[0]);
			transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_FIXED, args);
			nonNormButtons.get(1).setEnabled(false);
		}
	}

	/**
	 * Non-norm 3
	 */
	public class NonNorm3Listener implements ActionListener
	{
		/**
		 * Invoked whenever the button is pressed
		 */
		public void actionPerformed(ActionEvent ae)
		{
			String text;
			String temp[];
			Integer[] args = new Integer[1];
			
			text = nonNormButtons.get(2).getText();
			temp=text.split("#");
			args[0] = Integer.parseInt(temp[1]);
			
			System.out.println("button 3 pressed with args: "+ args[0]);
			transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_FIXED, args);
			nonNormButtons.get(2).setEnabled(false);
		}
	}

	/**
	 * Non-norm 4
	 */
	public class NonNorm4Listener implements ActionListener
	{
		/**
		 * Invoked whenever the button is pressed
		 */
		public void actionPerformed(ActionEvent ae)
		{
			String text;
			String temp[];
			Integer[] args = new Integer[1];
			
			text = nonNormButtons.get(3).getText();
			temp=text.split("#");
			args[0] = Integer.parseInt(temp[1]);
			
			System.out.println("button 4 pressed with args: "+ args[0]);
			transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_FIXED, args);
			nonNormButtons.get(3).setEnabled(false);
		}
	}

	/**
	 * Non-norm 5
	 */
	public class NonNorm5Listener implements ActionListener
	{
		/**
		 * Invoked whenever the button is pressed
		 */
		public void actionPerformed(ActionEvent ae)
		{
			String text;
			String temp[];
			Integer[] args = new Integer[1];
			
			text = nonNormButtons.get(4).getText();
			temp=text.split("#");
			args[0] = Integer.parseInt(temp[1]);
			
			System.out.println("button 5 pressed with args: "+ args[0]);
			transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_FIXED, args);
			nonNormButtons.get(4).setEnabled(false);
		}
	}

	/**
	 * Non-norm 6
	 */
	public class NonNorm6Listener implements ActionListener
	{
		/**
		 * Invoked whenever the button is pressed
		 */
		public void actionPerformed(ActionEvent ae)
		{
			String text;
			String temp[];
			Integer[] args = new Integer[1];
			
			text = nonNormButtons.get(5).getText();
			temp=text.split("#");
			args[0] = Integer.parseInt(temp[1]);
			
			System.out.println("button 6 pressed with args: "+ args[0]);
			transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_FIXED, args);
			nonNormButtons.get(5).setEnabled(false);
		}
	}

	/**
	 * Non-norm 7
	 */
	public class NonNorm7Listener implements ActionListener
	{
		/**
		 * Invoked whenever the button is pressed
		 */
		public void actionPerformed(ActionEvent ae)
		{
			String text;
			String temp[];
			Integer[] args = new Integer[1];
			
			text = nonNormButtons.get(6).getText();
			temp=text.split("#");
			args[0] = Integer.parseInt(temp[1]);
			
			System.out.println("button 7 pressed with args: "+ args[0]);
			transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_FIXED, args);
			nonNormButtons.get(6).setEnabled(false);
		}
	}

	/**
	 * Non-norm 8
	 */
	public class NonNorm8Listener implements ActionListener
	{
		/**
		 * Invoked whenever the button is pressed
		 */
		public void actionPerformed(ActionEvent ae)
		{
			String text;
			String temp[];
			Integer[] args = new Integer[1];
			
			text = nonNormButtons.get(7).getText();
			temp=text.split("#");
			args[0] = Integer.parseInt(temp[1]);
			
			System.out.println("button 8 pressed with args: "+ args[0]);
			transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_FIXED, args);
			nonNormButtons.get(7).setEnabled(false);
		}
	}
	
    
    public void setTransducer(Transducer transducer)
    {
    	this.transducer=transducer;
    	this.transducer.register(this, TChannel.GUI);
    }

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if(channel==TChannel.GUI && event==TEvent.SENSOR_BROKEN)
		{
			for(int i=0; i<nonNormButtons.size(); i++)
			{
				if(nonNormButtons.get(i).isEnabled()==false)
				{
					nonNormButtons.get(i).setText("Fixed Sensor #" + args[0]);
					nonNormButtons.get(i).setEnabled(true);
					break;
				}
			}
		}
		
		
	}
	
	/**
	 * Listener class for the speed slider
	 */
	public class SpeedSliderListener implements ChangeListener
	{
		/**
		 * Invoked whenever the timer speed slider is changed, updates the
		 * factory speed. Note that lower on the slider is slower factory
		 */
		public void stateChanged(ChangeEvent ce)
		{
			int newSpeed = (speedSlider.getValue());

			
			
				GUIComponentOffline.setMultiplier(newSpeed);
			

			speedSlider.setToolTipText("" + newSpeed);
		}
	}

}
