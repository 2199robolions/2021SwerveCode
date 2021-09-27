package frc.robot;

//import 

public class Auto {
	// Step Variables
	private int          step;
	private int          shootStep;

	// First Time variables 
	private boolean      firstTime        = true;
	private boolean      shootFirstTime   = true;
	private boolean      routineFirstTime = true;
	private boolean      delayFirstTime   = true;

	// Variables
	private long         startMs; 

	// Consants
	//private final int TEST_DELAY = 1000;
	//private final int SHOOT_TIME = 5000;

	// Object creation
	private LedLights   led;
	private Drive       drive;
	private Grabber     grabber;
	private Shooter     shooter;
	

	/****************************************************************************************** 
    *
    *    Constructor
    * 
    ******************************************************************************************/
	public Auto (Drive drive, Grabber grabber, Shooter shooter) {
		led             = LedLights.getInstance();
		this.drive      = drive;
		this.grabber    = grabber;
		this.shooter    = shooter;

		//Sets the step variables
		step = 1;
		shootStep = 1;
	}


	/****************************************************************************************** 
    *
    *    competitionAuto()
	*    Runs auto program based on selected position
    * 
    ******************************************************************************************/
	public int competitionAuto(String position, int delay) {
		if (position.equals("Right")) {
			return rightAuto(delay);
		}
		else if (position.equals("Left")) {
			return leftAuto(delay);
		}
		else if (position.equals("Center")) {
			return centerAuto(delay);
		}
		else if (position.equals("L/R/C Simple")) {
			return basicAuto(delay);
		}
		else {
			System.out.println("Auto position not selected");
			return Robot.FAIL;
		}
	}


	/****************************************************************************************** 
    *
    *    rightAuto()
	*    Runs the auto code for the position right of the target
    * 
    ******************************************************************************************/
	public int rightAuto(int delay) {
		int status = Robot.CONT;
		long delayMsec = delay * 1000;

		if (firstTime == true) {
			firstTime = false;
			step = 1;
		}

		switch (step) {
			// Starts Auto Program
			case 1:
				status = delay(delayMsec);
				break;
			case 2:
				status = shootBall(Shooter.ShootLocation.TEN_FOOT);
				break;
			case 3:
				status = drive.autoCrabDrive(3, 0);
				break;
			default:
				step = 1;
				firstTime = true;
				return Robot.DONE;
		}

		if (status == Robot.DONE) {
			step++;
		}

		return Robot.CONT;
	}


	/****************************************************************************************** 
    *
    *    leftAuto()
	*    Runs the auto code for the position left of the target
    * 
    ******************************************************************************************/
	public int leftAuto(int delay) {
		int status = Robot.CONT;
		long delayMsec = delay * 1000;

		if (firstTime == true) {
			firstTime = false;
			step = 1;
		}

		switch (step) {
			// Starts Auto Program
			case 1:
				led.autoMode();
				status = delay(delayMsec);
				break;
			case 2:
				status = shootBall(Shooter.ShootLocation.TEN_FOOT);
				break;
			case 3:
				status = drive.autoCrabDrive(3, 0);
				break;
			default:
				step = 1;
				firstTime = true;
				return Robot.DONE;
		}

		if (status == Robot.DONE) {
			step++;
		}

		return Robot.CONT;
	}
	

	/****************************************************************************************** 
    *
    *    centerAuto()
	*    Runs the auto code for the position center of the target
    * 
    ******************************************************************************************/
	public int centerAuto(int delay) {
		int status = Robot.CONT;
		long delayMsec = delay * 1000;

		if (firstTime == true) {
			firstTime = false;
			step = 1;
		}

		switch (step) {
			// Starts Auto Program
			case 1:
				led.autoMode();
				status = delay(delayMsec);
				break;
			case 2:
				status = shootBall(Shooter.ShootLocation.TEN_FOOT);
				break;
			case 3:
				status = drive.autoCrabDrive(3, 0);
				break;
			default:
				step = 1;
				firstTime = true;
				return Robot.DONE;
		}

		if (status == Robot.DONE) {
			step++;
		}

		return Robot.CONT;
	}


	/****************************************************************************************** 
    *
    *    basicAuto()
	*    Runs a basic code that can be ran at any position
    * 
    ******************************************************************************************/
	public int basicAuto(int delay) {
		int status = Robot.CONT;
		long delayMsec = delay * 1000;

		if (routineFirstTime == true) {
			routineFirstTime = false;
			step = 1;
		}

		switch (step) {
			// Starts Auto Program
			case 1:
				status = delay(delayMsec);
				break;
			case 2:
				System.out.println("Shooting ball");
				status = shootBall(Shooter.ShootLocation.TEN_FOOT);
				break;
			case 3:
				System.out.println("Driving");
				status = drive.autoCrabDrive(3, 0);
				break;
			default:
				step = 1;
				routineFirstTime = true;
				return Robot.DONE;
		}

		if (status == Robot.DONE) {
			step++;
		}

		return Robot.CONT;
	}


	/****************************************************************************************** 
    *
    *    shootBall()
	*    Autonomously shoots balls in the robot
    * 
    ******************************************************************************************/
	public int shootBall(Shooter.ShootLocation shootLocation) {
		int shootStatus = Robot.CONT;

		if (shootFirstTime == true) {
			System.out.println("shoot first time");
			shootFirstTime = false;
			shootStep = 1;
		}

		switch (shootStep) {
			//Reverses feeder to clear jams
			case 1:
				shooter.disableHoodMotor();
				shooter.disableRightShooterMotor();
				shootStatus = shooter.reverseFeeder(0.25);
				drive.limelightPIDTargeting(Drive.TargetPipeline.TEN_FOOT);
				System.out.println("Case 1");
				break;
			//Starts up shooter
			case 2:
				shooter.disableFeeder();
				shooter.disableHoodMotor();
				shooter.manualShooterControl(shootLocation);
				drive.limelightPIDTargeting(Drive.TargetPipeline.TEN_FOOT);
				shootStatus = Robot.DONE;
				System.out.println("Case 2");
				break;
			//Moves hood to proper location
			case 3:
				shooter.disableFeeder();
				shootStatus = shooter.manualHoodMotorControl(shootLocation);
				drive.limelightPIDTargeting(Drive.TargetPipeline.TEN_FOOT);
				System.out.println("Case 3");
				break;
			//Feeds balls if shooter is up to speed. Lasts for 5 seconds before moving on
			case 4:
				shootStatus = drive.limelightPIDTargeting(Drive.TargetPipeline.TEN_FOOT);
				System.out.println("Case 4");
				break;
			case 5:
				shooter.disableHoodMotor();
				shootStatus = Robot.DONE;
				System.out.println("Case 5");
				break;
			case 6:
				shooter.enableFeeder();
				shootStatus = delay(5000);
				System.out.println("Case 6");
				break;
			default:
				shootStep = 1;
				shootFirstTime = true;
				shooter.disableShooter();
				grabber.setGrabberMotor(Grabber.GrabberDirection.OFF);

				return Robot.DONE;
		}

		if (shootStatus == Robot.DONE) {
			System.out.println("Incrementing shootStep " + shootStep);
			shootStep++;
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
				climber.bottomAndMiddleArmUp();
				status = Robot.DONE;
				break;
			/*case 2:
				status = delay(1);
				break;
			case 3:
				climber.bottomArmUp();

				status = Robot.DONE;
				break;*/
			case 2:
				status = delay(1);
				break;
			case 3:
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

		if (delayFirstTime == true) {
			delayFirstTime = false;
			startMs = System.currentTimeMillis();
		}

		if ( (currentMs - startMs) > delayMsec) {
			delayFirstTime = true;
			return Robot.DONE;
		}
		else  {
			return Robot.CONT;
		}
	}

} // End of Auto class