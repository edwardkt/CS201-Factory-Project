package gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

/**
 * Class for animation of sensor
 */
@SuppressWarnings("serial")
public class GUISensor extends GuiComponent implements MouseListener {
	/**
	 * ImageIcons for sensor
	 */
	public static final ImageIcon sensor = new ImageIcon(
			"imageicons/sensorAnimated.gif");

	public static final ImageIcon sensorOn = new ImageIcon(
			"imageicons/ConveyorSensorRed.png");

	public static final ImageIcon sensorOff = new ImageIcon(
			"imageicons/ConveyorSensorGreen.png");
	
	public static final ImageIcon sensorBroken = new ImageIcon(
			"imageicons/ConveyorSensorBroken.png");

	int counter = 8;
	
	private SensorState sensorState;
	
	private enum SensorState { Normal, Unresponsive };

	/**
	 * The rectangle of the glass pane
	 */
	Rectangle2D glassRect;

	ArrayList<GUIGlass> activePieces;

	GUIGlass currentGlassPiece;

	/** Index of the conveyor */
	private Integer myIndex;

	/** Whether the sensor is pressed */
	private boolean pressed = false;

	public Integer getIndex() {
		return myIndex;
	}

	public void setIndex(int index) {
		myIndex = index;
	}

	/**
	 * Public constructor for GUISensor
	 */
	public GUISensor(Transducer t) {
		glassRect = new Rectangle2D.Double();
		setIcon(sensorOff);
		setSize(getIcon().getIconWidth(), getIcon().getIconHeight());
		setupRect();
		transducer = t;
		sensorState = SensorState.Normal;
		addMouseListener(this);
		transducer.register(this, TChannel.SENSOR);
	}

	/**
	 * Moves the sensor's rectangle and sets its width and height to match that
	 * of the JLabel
	 */
	public void setupRect() {
		glassRect.setRect(getX(), getY(), getIcon().getIconWidth(), getIcon()
				.getIconHeight());
	}

	/**
	 * Getter for background rectangle
	 * 
	 * @return the background rectangle of the sensor
	 */
	public Rectangle2D getRect() {
		return glassRect;
	}

	/**
	 * The actionPerformed will check if the sensor should be showing On or Off
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if( sensorState == SensorState.Normal )
			checkGlassDetected();
	}

	/**
	 * If the state is equal to false then it will check through all active
	 * glass pieces to see if it interesects with the sensor using guiX and
	 * guiY, if it finds one it will update its icon set its state to true. if
	 * the state is true then it will wait for the current piece of glass to
	 * move off the sensor then it will change it's icon back to the red sensor
	 * icon.
	 */
	public void checkGlassDetected() {
		// Will update it's current list with the current roster of
		// GuiGlassPieces
		activePieces = parent.getActivePieces();
		
		//half the dimension of the sensor triggered with a 2 pixel buffer
		//since conveyors are not symmetrically positioned around pop ups
		int halfSensorHeight = getHeight() / 2 - 2;
		int halfSensorWidth = getWidth() / 2 - 2;
		
		if (pressed == false) {
			// This is if the sensor is not pressed
			// it will constantly check if it's intersecting
			// with any guiglasspiece in the activeGlassPieces
			// list.
			for (int k = 0; k < this.activePieces.size(); k++) {
				
				//half the dimension of the glass piece 
				int halfGlassHeight = activePieces.get(k).getHeight() / 2;
				int halfGlassWidth = activePieces.get(k).getWidth() / 2;

				// Checks to see if the glass piece intersects with the sensor
				//
				if (activePieces.get(k).getCenterX() + halfGlassWidth >= getCenterX() - halfSensorWidth
						&& activePieces.get(k).getCenterX() - halfGlassWidth <= getCenterX() + halfSensorWidth
						&& activePieces.get(k).getCenterY() + halfGlassHeight >= getCenterY() - halfSensorHeight
						&& activePieces.get(k).getCenterY() - halfGlassHeight <= getCenterY() + halfSensorHeight) {
					
					setIcon(sensorOn);
					// If one does intersect it will change it's icon to
					// pressed(green)

					// modifies it's current state to true - pressed
					this.pressed = true;

					// Sets the current piece to the piece that the sensor
					// intersects with
					this.currentGlassPiece = activePieces.get(k);

					// Notifies the agent that the sensor has been pressed.
					Object[] args = new Object[2];
					args[0] = myIndex;
					args[1] = k;

					transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
				}
			}
		} else {
			//half the dimension of the glass piece currently on the sensor
			int halfGlassHeight = currentGlassPiece.getHeight() / 2;
			int halfGlassWidth = currentGlassPiece.getWidth() / 2;
			
			// If it is already pressed then it will wait til
			// it's current glass piece no longer intersects
			// within a certain range and will reset it's icon
			// to red(not pressed).
			if (currentGlassPiece.getCenterX() + halfGlassWidth < getCenterX() - halfSensorWidth
					|| currentGlassPiece.getCenterX() - halfGlassWidth > getCenterX() + halfSensorWidth
					|| currentGlassPiece.getCenterY() + halfGlassHeight < getCenterY() - halfSensorHeight
					|| currentGlassPiece.getCenterY() - halfGlassHeight > getCenterY() + halfSensorHeight) {
				// Redraws the sensor to it's red icon
				setIcon(sensorOff);

				// Resets the state to false: ie not pressed
				pressed = false;
				currentGlassPiece = null;

				// Notifies the agent
				Object[] args = new Object[1];
				args[0] = myIndex;
				transducer.fireEvent(TChannel.SENSOR,TEvent.SENSOR_GUI_RELEASED, args);
			}
		}
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// should never happen, since the sensors receive no messages from agent
	}
	
	public void mousePressed(MouseEvent e) {
		if( sensorState == SensorState.Normal ) {
			setIcon(sensorBroken);
			sensorState = SensorState.Unresponsive;
		}
		else {
			setIcon(sensorOff);
			sensorState = SensorState.Normal;
		}
	}
	
	public void mouseClicked(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
