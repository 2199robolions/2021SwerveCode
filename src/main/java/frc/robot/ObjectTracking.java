package frc.robot;

import edu.wpi.first.wpiutil.math.MathUtil;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEntry;

import edu.wpi.first.wpilibj.controller.PIDController;

public class ObjectTracking {
	//Object creation
	Drive drive;

	//Network Tables
	NetworkTable TrackingValues;

	//Variables
	private double deadZoneCount = 0.00;
	private double centerX    = 0.00;

	//CONSTANTS
	private static final int IMG_WIDTH = 640;
	//private static final int IMG_HEIGHT = 480;

	//PID controller
	private PIDController detectionController;

	//PID tolerance
	double detectionToleranceDegrees = 2.0f;

	//Detection Controller
	private static final double detP = 0.02;
	private static final double detI = 0.01;
    private static final double detD = 0.01;

	/**
	 * CONSTRUCTOR
	 */
	public ObjectTracking() {
		// Instance creation
		drive = Drive.getInstance();

		// Creates Network Tables instance
		TrackingValues = NetworkTableInstance.getDefault().getTable("TrackingValues");

		// Creates a PID controller
		detectionController = new PIDController(detP, detI, detD);
        detectionController.setTolerance(detectionToleranceDegrees);
        detectionController.enableContinuousInput(-180.0, 180.0);
	}

	/**
	 * Method to turn and face the ball
	 */
	public void faceBall() {
		//Variables
		double m_DetectionCalculatedPower = 0;
		double turnAngle;

		//Calls the ballDetection method
		turnAngle = ballDetection();

		//Clamps turnAngle
		turnAngle = MathUtil.clamp(turnAngle, -180.00, 180.00);

		//Rotate with PID
		m_DetectionCalculatedPower = detectionController.calculate(turnAngle, 0.00);
        m_DetectionCalculatedPower = MathUtil.clamp(m_DetectionCalculatedPower, -0.50, 0.50);
		drive.teleopRotate(m_DetectionCalculatedPower);
	}

	/**
	 * Detects the ball and provides how far off the robot is
	 * @return turnAngle
	 */
	private double ballDetection() {
		// Variables
		final int DEAD_ZONE = 25;
		boolean pipelineEmpty;
		double  emptyCount;
		double  drivePower;
		double  turn;
	
		//Network Tables
		NetworkTableEntry isEmpty = TrackingValues.getEntry("IsEmpty");
		NetworkTableEntry target = TrackingValues.getEntry("CenterX");
		NetworkTableEntry empty  = TrackingValues.getEntry("Empty");
	
		//Sets the double variables
		pipelineEmpty = isEmpty.getBoolean(false);
		centerX       = target.getDouble(0.00);
		emptyCount    = empty.getDouble(0.00);
	
		//Ignores the 50 pixels around the edge
		if ( (centerX < DEAD_ZONE) || (centerX > IMG_WIDTH - DEAD_ZONE) ) {
		  pipelineEmpty = true;
		  deadZoneCount++;
		}
	
		if (pipelineEmpty == true) {
		  //Prints the emptyCount
		  System.out.println("Empty Count: " + emptyCount + " Dead Zone " + deadZoneCount);

		  //Sets turn to 0
		  turn = 0.00;
		}
		else if (pipelineEmpty == false) {
		  //Does the math for tracking the balls
		  turn = centerX - (IMG_WIDTH / 2);
	
		  //Drive Power
		  drivePower = turn * 0.001;
	
		  System.out.println("Turn: " + turn + " CenterX: " + centerX + " drive: " + drivePower);
	
		  //Resets empty counters
		  emptyCount    = 0;
		  deadZoneCount = 0;
		}
		else {
		  //Sets the values to 0 if otherwise
		  emptyCount = 0.00;
		  turn       = 0.00;
		  centerX    = 0.00;
		  drivePower = 0.00;
		}
	
		/*//Does the math for tracking the balls
		if (centerX != -1) {
		  turn = centerX - (IMG_WIDTH / 2);
		
		  //So far it just rotates to look at the ball using a REALLY SLOW speed 
		  //drive.teleopRotate(turn * 0.001);
	
		  System.out.println("Turn: " + turn + " CenterX: " + centerX);
		}
	
		//Sets centerX to -1 (should not happen naturally)
		if (emptyCount != 0) {
		  centerX = -1;
	
		  //Prints the emptyCount
		  System.out.println("Empty Count: " + emptyCount + "\n");
		}*/
		return turn;
	}

}

//End of the ObjectTracking class