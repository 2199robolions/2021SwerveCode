package frc.robot;

//import 

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

	/**
	 * Old Method to calibrate the hood motor
	 */
	/* I assume that this method won't be used anymore? 
	public int calibrateHoodMotor() {
		//Standard Auto Variables
		int status = Robot.CONT;

		//Variables
		boolean limit1 = shooter.limitSwitch1Value();
		boolean limit2 = shooter.limitSwitch2Value();
		double  motorPositionInit;
		double  motorPositionOne ;
		double  motorPositionTwo ;
		double  motorPositionAvg ;
		double  power  = 0.10; //0.05 just barely makes the motor move

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
				Shooter.originalPosition = motorPositionInit;

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
					Shooter.lowShot = motorPositionOne;

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
					Shooter.highShot = motorPositionTwo;

					status = Robot.DONE;
				}
				else {
					//Starts slowly moving the motor backward to try and find the backward limit
					shooter.enableHoodMotor(power * -1);

					status = Robot.CONT;
				}
				break;
			case 5:
				//Calculates the average position between the two limit switches
				motorPositionAvg = (Shooter.lowShot + Shooter.highShot) / 2;

				//Sets the average position
				Shooter.avgPosition = motorPositionAvg;

				//Sets hood motor to the average position and awaits further input
				shooter.manualHoodMotorControl(Shooter.HoodMotorPosition.AVERAGE_POSITION);

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
	}*/
	
	/**
	 * Default Auto
	 * @param delay
	 * @return status
	 */
	public int defaultAuto(int delay) {
		int status = Robot.CONT;
		long delayMsec = delay * 1000;

		if (firstTime == true) {
			firstTime = false;
			step = 1;
		}

		switch (step) {
			// Starts Auto Program
			case 1:
				//Sets the LED's to their auto mode
				led.autoMode();

				// Delay
				status = delay(delayMsec);
				break;
			case 2:
				status = drive.autoCrabDrive(10, 0, 0.3);
				break;
				/*
			case 3:
				grabber.deployRetract();
				status = Robot.CONT;
				break;*/
			default:
				// Set Step to 1
				step = 1;

				//Reset firstTime
				firstTime = true;

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