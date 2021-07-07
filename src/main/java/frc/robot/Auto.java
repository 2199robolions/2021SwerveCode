package frc.robot;

import frc.robot.Grabber.GrabberDirection;

//import frc.robot.Conveyer.ConveyerState;
//import frc.robot.Grabber.GrabberDirection;

//import jdk.nashorn.internal.ir.BreakableNode;

public class Auto {
	// Variables
	private int          step;
	private long         startMs; 
	private boolean      firstTime = true;

	// Consants
	private final int TEST_DELAY = 1000;
	private final int SHOOT_TIME = 5000;

	// Object creation
	private LedLights   led;
	private Drive       drive;
	private Grabber     grabber;
	private Shooter     shooter;
	
	/**
	 * CONTRUCTOR
	 */
	public Auto (Drive drive, Grabber grabber, Shooter shooter, LedLights ledLights) {
		led             = ledLights;
		this.drive      = drive;
		this.grabber    = grabber;
		this.shooter    = shooter;

		step = 1;
	}

	/**
	 * Default Auto
	 * @param delay
	 * @return
	 */
	public int defaultAuto(int delay) {
		int status = Robot.CONT;
		long delayMsec = delay * 1000;

		switch (step) {
			// Starts Auto Program
			case 1:
				//Sets the LED's to their auto mode
				led.autoMode();

				// Delay
				status = delay(delayMsec);
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

	public int basicAuto(int delay) {
		int status = Robot.CONT;
		long delayMsec = delay * 1000;

		switch (step) {
			//Starts Auto Program
			case 1:
				//Sets the LED's to their auto mode
				led.autoMode();

				//Initial delay 
				status = delay(delayMsec);
				break;
			case 2:
				//Deploys grabber
				grabber.deployRetract();

				//Sets status to DONE
				status = Robot.DONE;
				break;
			case 3:
				//Starts grabber
				grabber.grabberDirection(GrabberDirection.FORWARD);

				//Sets status to DONE
				status = Robot.DONE;
				break;
			case 4:
				//Targets with the limelight
				status = drive.limelightPIDTargeting(Drive.TargetPipeline.TEN_FOOT);
				break;
			case 5:
				//Starts the shooter
				shooter.enableShooter();

				//Sets status to DONE
				status = Robot.DONE;
				break;
			default:
				//Set step to 1
				step = 1;

				//Turns everything off
				drive.disableMotors();
				grabber.grabberDirection(GrabberDirection.OFF);
				shooter.disableShooter();

				//Auto program finished
				led.autoModeFinished();

				return Robot.DONE;
		}

		if (status == Robot.DONE) {
			step ++;
		}

		return Robot.CONT;
	}

	/**
	 * Climber Arm Control
	 */
	/**
	 * Deploy Climber Pistons
	 */
	public int climberDeploy(Climber climber) {
		int status = Robot.CONT;

		/**
		 * Fire Middle
		 * Fire Bottom
		 * Fire Top
		 */
		switch(step) {
			case 1:
				climber.bottomArmUp();

				status = Robot.DONE;
				break;
			case 2:
				status = delay(0);
				break;
			case 3:
				climber.middleArmUp();

				status = Robot.DONE;
				break;
			case 4:
				status = delay(0);
				break;
			case 5:
				climber.topArmUp();

				status = Robot.DONE;
				break;
			default:
				step = 1;

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