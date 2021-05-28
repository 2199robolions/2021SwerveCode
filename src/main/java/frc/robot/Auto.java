/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

//import frc.robot.Conveyer.ConveyerState;
//import frc.robot.Grabber.GrabberDirection;

//import jdk.nashorn.internal.ir.BreakableNode;

public class Auto {
	// Variables
	private int          step;
	private long         startMs; 
	private boolean      firstTime = true;

	// Object creation
	private LedLights   led;
	private Wheel       wheel;
	private Drive       drive;
	//private Grabber     grabber;
	//private Conveyer    conveyer;
	//private Shooter     shooter;

	// Consants
	private final int TEST_DELAY = 1000;
	private final int SHOOT_TIME = 5000;


	/**
	 * CONTRUCTOR
	 */
	public Auto (LedLights ledLights, Wheel wheel/*, Shooter shooter, Conveyer conveyer, Grabber grabber*/) {
		led             = ledLights;
		this.wheel      = wheel;
		//this.shooter    = shooter;
		//this.conveyer   = conveyer;
		//this.grabber    = grabber;

		step = 1;
	}

	/**
	 * Default Auto 
	 */
	public int defaultAuto(int delay) {
		int status = Robot.CONT;
		long delayMs = delay * 1000;

		switch (step) {
			// Starts Auto Program
			case 1: 
				led.autoMode();

				// Delay
				status = delay(delayMs);
				break;
			case 2:
				status = 1; //replace the 1 with a method later
				break;
			default:
				// Set Step to 1
				step = 1;

				// Auto Program Finished
				led.autoModeFinished();

				return Robot.DONE;
		}

		if (status == Robot.DONE) {
			step++;
		}

		return Robot.CONT;
	}

	/**
	 * Delaying Program  delay is in milliseconds
	 * @param delayMsec
	 */
	private int delay(long delayMsec) {
		long currentMs = System.currentTimeMillis();

		if (firstTime == true) {
			firstTime = false;
			startMs = System.currentTimeMillis();
		}

		if ( (currentMs - startMs) > delayMsec) {
			firstTime = true;
			return Robot.DONE;
		}
		else  {
			return Robot.CONT;
		}
	}

} // End of Auto class