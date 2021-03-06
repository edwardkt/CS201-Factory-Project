
package gui.components;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import shared.ImageIcons;
import shared.enums.MachineType;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

/**
 * GUIComponentoffline is the superclass of GUI components off the conveyor
 */
@SuppressWarnings("serial")
public class GUIComponentOffline extends GuiAnimationComponent implements ActionListener, Serializable, MouseListener
{
	/**
	 * The popup for the offline component
	 */
	GUIPopUp myPopUp;

	MachineType type;

	Integer index;

	Integer popUpIndex;

	TChannel channel;
	
	private static volatile int animationMultiplier;

	/**
	 * Frame counter
	 */
	int counter = 0;

	/**
	 * List of icons for animations
	 */
	ArrayList<ImageIcon> imageicons = new ArrayList<ImageIcon>();
	
	final static ImageIcon brokenGlass = new ImageIcon("imageicons/glassImage_BROKEN.png");

	/**
	 * Constructor for GUIComponentOffline
	 */
	public GUIComponentOffline(MachineType type, Transducer t)
	{
		super();
		transducer = t;
		this.type = type;
		initializeImages();
		animationMultiplier = 1;
		addMouseListener( this );
	}

	/**
	 * Method that initializes the imageicons for the specific machines
	 * based on the MachineType enum
	 */
	public void initializeImages()
	{
		if (type == MachineType.CROSS_SEAMER)
		{
			imageicons = (ArrayList<ImageIcon>)ImageIcons.getIconList("crossSeamer");
			channel = TChannel.CROSS_SEAMER;
			transducer.register(this, TChannel.CROSS_SEAMER);
		}

		else if (type == MachineType.DRILL)
		{
			imageicons = (ArrayList<ImageIcon>)ImageIcons.getIconList("drill");
			channel = TChannel.DRILL;
			transducer.register(this, TChannel.DRILL);

		}
		else if (type == MachineType.GRINDER)
		{
			imageicons = (ArrayList<ImageIcon>)ImageIcons.getIconList("grinder");
			channel = TChannel.GRINDER;
			transducer.register(this, TChannel.GRINDER);
		}
		setIcon(imageicons.get(0));
		setSize(getIcon().getIconWidth(), getIcon().getIconHeight());
	}

	/**
	 * Method that does the machine animation
	 */
	public void doAnimate()
	{
		if (counter < imageicons.size() * animationMultiplier)
		{
			setIcon(imageicons.get(counter % imageicons.size()));
			counter++;
		}
		else
		{

			setIcon(imageicons.get(0));
			counter = 0;
			animationState = AnimationState.DONE;

			Object[] args = new Object[1];
			args[0] = index;
			transducer.fireEvent(channel, TEvent.WORKSTATION_GUI_ACTION_FINISHED, args);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (animationState.equals(AnimationState.MOVING))
		{
			if (part != null)
			{
				movePartIn();
			}
		}
		if (animationState.equals(AnimationState.ANIMATING))
		{
			if( part.stateBroken ) {
				setIcon(imageicons.get(0));
				counter = 0;
				animationState = AnimationState.IDLE;
			}
			else
				doAnimate();
		}
	}

	@Override
	public void addPart(GUIGlass part)
	{
		this.part = part;
	}

	public void setIndex(Integer index)
	{
		this.index = index;
	}
	
	static public void setMultiplier( int mult ) {
		if( mult > 0 )
			animationMultiplier = mult;
	}

	public void paint(Graphics g)
	{
		super.paint(g);
	}

	private void movePartIn()
	{
		if (part.getCenterX() < getCenterX())
			part.setCenterLocation(part.getCenterX() + 1, part.getCenterY());
		else if (part.getCenterX() > getCenterX())
			part.setCenterLocation(part.getCenterX() - 1, part.getCenterY());

		if (part.getCenterY() < getCenterY())
			part.setCenterLocation(part.getCenterX(), part.getCenterY() + 1);
		else if (part.getCenterY() > getCenterY())
			part.setCenterLocation(part.getCenterX(), part.getCenterY() - 1);

		if (part.getCenterX() == getCenterX() && part.getCenterY() == getCenterY())
		{
			Object[] args = new Object[1];
			args[0] = index;
			animationState = AnimationState.DONE;
			transducer.fireEvent(channel, TEvent.WORKSTATION_LOAD_FINISHED, args);
		}
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args)
	{
		if (((Integer)args[0]).equals(index))
		{
			if (event == TEvent.WORKSTATION_DO_ACTION)
			{
				animationState = AnimationState.ANIMATING;
				return;
			}
			else if (event == TEvent.WORKSTATION_DO_LOAD_GLASS)
			{
				animationState = AnimationState.MOVING;
				return;
			}
			else if (event == TEvent.WORKSTATION_RELEASE_GLASS)
			{
				//added by monroe
				//animationState = AnimationState.DONE;
				this.transducer.fireEvent(this.channel, TEvent.WORKSTATION_RELEASE_FINISHED, args);
				animationState = AnimationState.IDLE;
				//above added by monroe

				nextComponent.addPart(part);
				return;
			}

		}
	}

	public void mousePressed(MouseEvent e) {
		if( part != null ) {
			Object[] args = new Object[1];
			args[0] = index;
			if( !part.stateBroken ) {
				part.msgPartBroken();
				transducer.fireEvent( channel, TEvent.WORKSTATION_BREAK_GLASS, args);
			}
			else if( part.stateBroken ) {
				part.setVisible( false );
				part = null;
				transducer.fireEvent( channel, TEvent.WORKSTATION_BROKEN_GLASS_REMOVED, args);
			}
		}
	}
	public void mouseClicked(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
