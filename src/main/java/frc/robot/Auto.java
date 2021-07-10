package frc.robot;

import frc.robot.Grabber.GrabberDirection;

//import frc.robot.Conveyer.ConveyerState;
//import frc.robot.Grabber.GrabberDirection;

//import jdk.nashorn.internal.ir.BreakableNode;

public class Auto {
	// Variables
	private int          step;
	private int          calibrationStep;
	private long         startMs; 
	private boolean      firstTime = true;

	// Consants
	//private final int TEST_DELAY = 1000;
	//private final int SHOOT_TIME = 5000;

	// Object creation
	private LedLights   led;
	private Drive       drive;
	private Grabber     grabber;
	private Shooter     shooter;
	
	/**
	 * CONTRUCTOR
	 */
	public Auto (Drive drive, Grabber grabber, Shooter shooter) {
		led             = LedLights.getInstance();
		this.drive      = drive;
		this.grabber    = grabber;
		this.shooter    = shooter;

		//Sets the step variables
		step = 1;
		calibrationStep = 1;
	}

	public int calibrateHoodMotor() {
		//Standard Auto Variables
		int status = Robot.CONT;

		//Variables
		boolean limit1 = shooter.limitSwitch1Value();
		boolean limit2 = shooter.limitSwitch2Value();
		double  motorPositionInit;
		double  motorPositionOne ;
		double  motorPositionTwo ;
		double  power  = 0.05; //0.01

		switch (calibrationStep) {
			//Starts the calibration program
			case 1:
				//Starts the LED's
				led.autoMode();

				status = Robot.DONE;
				break;
			case 2:
				//Gets the starting position
				motorPositionInit = shooter.hoodMotorPosition();
			
				//Prints the starting position's value
				System.out.println("Initial Motor Position: " + motorPositionInit);

				//Allows other methods in the code to use these numbers
				shooter.ORIGINAL_POSITION = motorPositionInit;

				status = Robot.DONE;

				break;
			case 3:
				//System.out.println("Case 3");
				if (limit1 == true) {
					//Stops the motor
					shooter.disableHoodMotor();

					//Gets the first position
					motorPositionOne = shooter.hoodMotorPosition();

					//Prints position one's value
					System.out.println("Motor Position One: " + motorPositionOne);
				
					//Allows other methods in the code to use these numbers
					shooter.LOW_SHOT = motorPositionOne;

					status = Robot.DONE;
				}
				else {
					//Starts slowly moving the motor forward to try and find the forward limit
					shooter.enableHoodMotor(power);

					status = Robot.CONT;
				}
				break;
			case 4:
				//System.out.println("Case 4");
				if (limit2 == true) {
					//Stops the motor
					shooter.disableHoodMotor();

					//Gets the second position
					motorPositionTwo = shooter.hoodMotorPosition();

					//Prints position two's value
					System.out.println("Motor Position Two: " + motorPositionTwo);

					//Allows other methods to use these numbers
					shooter.HIGH_SHOT = motorPositionTwo;

					status = Robot.DONE;
				}
				else {
					//Starts slowly moving the motor backward to try and find the backward limit
					shooter.enableHoodMotor(power * -1);

					status = Robot.CONT;
				}
				break;
			case 5:
				//Sets hood motor to original position and awaits further input
				shooter.manualHoodMotorControl(Shooter.HoodMotorPosition.ORIGINAL_POSITION);

				status = Robot.DONE;
				break;
			default:
				//Sets calibrationStep to 1
				calibrationStep = 1;

				//Debatable if I want led's in here
				led.autoModeFinished();

				return Robot.DONE;
		}

		if (status == Robot.DONE) {
			calibrationStep++;
		}

		return Robot.CONT;
	}
	
	/**
	 * Default Auto
	 * @param delay
	 * @return status
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
				status = Robot.DONE; //replace the 1 with a method later
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

	public int basicAuto(int delaySec) {
		int status = Robot.CONT;
		long delayMsec = delaySec * 1000;

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
				climber.middleArmUp();

				status = Robot.DONE;
				break;
			case 2:
				status = delay(1);
				break;
			case 3:
				climber.bottomArmUp();

				status = Robot.DONE;
				break;
			case 4:
				status = delay(1);
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
	 * Delaying Program:
	 * the delay is in milliseconds
	 * @param delayMsec
	 * @return wheter the delay is finished
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