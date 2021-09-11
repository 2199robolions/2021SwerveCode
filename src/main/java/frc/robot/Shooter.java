package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.controller.PIDController;

	/**
	 * All of these numbers need to be fine tuned
	 * At 0.6 Power RPM is 3240
	 * At 0.69 Power RPM is 3834 MAX
	 * At 0.7 Power RPM is 3750
	 * At 0.8 Power RPM is 4220
	 * At 0.9 Power RPM is 4770
	 * At 1.0 Power RPM is 5240 (5400)
	 */

	 /****************************************
	  * 
	  *  Data from March 10, 2020
	  *
	  power		rpm
	  .60		3420
	  .65		3680
	  .70		3942
	  .75		4200
	  .80		4451
	  .85		4697
	  .90		4942
	  .95		5182
	  1.00		5422
		*/
	

public class Shooter {
	// SPARK MAX
	private CANSparkMax leftShooter;
	private CANSparkMax rightShooter;
	private CANSparkMax hoodMotor;

	// Victor SP
	private VictorSP feedMotor; //Negative power makes it intake balls

	// SPARK MAX ID's
	private int LEFT_SHOOTER_ID  = 17;
	private int RIGHT_SHOOTER_ID  = 19;
	private int HOOD_MOTOR_ID = 16;

	// Victor SP Port
	private int BALL_FEEDER_ID = 5;

	// DIO Ports
	public final int FRONT_SWITCH_ID = 0; //public final int LIMITSWITCH_1_ID = 0;
	public final int REAR_SWITCH_ID  = 1; //public final int LIMITSWITCH_2_ID = 1;

	// Encoders
	private CANEncoder leftShooterEncoder;
	private CANEncoder rightShooterEncoder;
	private CANEncoder hoodMotorEncoder;

	//DIO SENSORS
	private DigitalInput frontSwitch; //This one is the sensor closest to the front of the robot
	private DigitalInput rearSwitch; //This one is the sensor closest to the back of the robot

	// POWER CONSTANTS
	public final double SHOOT_POWER = 1.00; //It's more effective to adjust the angle for each shot than the speed
	public final double OFF_POWER   = 0.00;

	// RPM CONSTANTS
	public final double SHOOT_TARGET_RPM   = 5000; //Not tested
	public final double OFF_TARGET_RPM     = 0;

	// HOOD MOTOR CONSTANTS
	public static final double   TEN_FOOT_HOOD_ENCODER    = -9.20;
	public static final double   TRENCH_SHOT_HOOD_ENCODER = -6; //Not certain
	public static final double   LAY_UP_HOOD_ENCODER      = -12; //Not tested
	public static final double   LOW_SHOT_HOOD_ENCODER    = 0;
	public static final double   HIGH_SHOT_HOOD_ENCODER   = -15; //Not tested
	public static final double   FRONT_MAX_ENCODER        = 0;
	public static final double   REAR_MAX_ENCODER         = -15;

	private static final double   HOOD_POWER = 0.075;

	// Current Limit Constants
	private static final int SHOOTER_CURRENT_LIMIT = 80;
	private static final int HOOD_CURRENT_LIMIT    = 30;

	// FEED MOTOR CONSTANTS
	public static final double   FEED_POWER = -0.25;

	// Variables
	public  double                targetVelocity;
	private double                targetPower;
	private int                   targetCount        = 0;
	private Shooter.ShootLocation shotLocation       = null;
	private Shooter.ShootLocation hoodPrevPosition   = null;
	private boolean               firstTime          = true;
	public  boolean               hoodFirstTime      = true;
	private double                hoodTargetEncoder  = 0;
	private double                calibrateStartTime = 0;
	private double                hoodStartEncoder;
	private double                startSec           = 0;     
	Shooter.ShootLocation         startPosition      = Shooter.ShootLocation.OFF;


	public static enum ShootLocation {
		LAY_UP,
		TRENCH,
		TEN_FOOT,
		OFF;
	}

	public static enum BallFeederDirection {
		FORWARD,
		REVERSE,
		OFF;
	}


	// Shooter PID Controller
	private PIDController shooterController;

	private static final double kP = 0.0004; //0.0003 old value
	private static final double kI = 0.00;
	private static final double kD = 0.00;
	


	/****************************************************************************************** 
    *
    *    Constructor
    *   
    ******************************************************************************************/
	public Shooter() {
		// SPARK Max
		leftShooter    = new CANSparkMax(LEFT_SHOOTER_ID, MotorType.kBrushless); //Shooter 1 requires negative power to shoot
		rightShooter   = new CANSparkMax(RIGHT_SHOOTER_ID, MotorType.kBrushless); //Shooter 2 requires positive power to shoot
		hoodMotor      = new CANSparkMax(HOOD_MOTOR_ID, MotorType.kBrushless);

		leftShooter.follow(rightShooter, true);

		// Sets the current limtis for the motors
		leftShooter .setSmartCurrentLimit(SHOOTER_CURRENT_LIMIT);
		rightShooter.setSmartCurrentLimit(SHOOTER_CURRENT_LIMIT);
		hoodMotor   .setSmartCurrentLimit(HOOD_CURRENT_LIMIT);

		// Sets the mode of the motors (if this works in the code)
		leftShooter. setIdleMode(CANSparkMax.IdleMode.kCoast);
		rightShooter.setIdleMode(CANSparkMax.IdleMode.kCoast);
		hoodMotor.   setIdleMode(CANSparkMax.IdleMode.kBrake);

		//Victor SP
		feedMotor = new VictorSP(BALL_FEEDER_ID);
		
		// Set Shooter related motors to off to Start the Match
		leftShooter .set(0.0);
		rightShooter.set(0.0);
		hoodMotor.set (0.0);
		feedMotor.set (0.0);

		// Encoders
		leftShooterEncoder  = leftShooter.getEncoder();
		rightShooterEncoder = rightShooter.getEncoder();
		hoodMotorEncoder    = hoodMotor.getEncoder();

		// DIO Sensors
		frontSwitch = new DigitalInput(FRONT_SWITCH_ID); //limitSwitch_1 = new DigitalInput(LIMITSWITCH_1_ID);
		rearSwitch  = new DigitalInput(REAR_SWITCH_ID ); //limitSwitch_2 = new DigitalInput(LIMITSWITCH_2_ID);

		// PID Controller
		shooterController = new PIDController(kP, kI, kD);
	}


	/****************************************************************************************** 
    *
    *    autoShooterControl()
	*    Uses PID to get shooter to appropriate speed for given shot
    *   
	******************************************************************************************/
	/*
	public void autoShooterControl(ShootLocation location) {
		shotLocation = location;
		double  powerError;
		double  power;

		if (location == ShootLocation.OFF) {
			powerError     = OFF_POWER;
			targetVelocity = OFF_TARGET_RPM;
			targetPower    = OFF_POWER;
		}
		else if ( ((location == ShootLocation.TEN_FOOT) || (location == ShootLocation.TRENCH)) || (location == ShootLocation.HAIL_MARY) ) {
			powerError     = shooterController.calculate( getabsRPM(LEFT_SHOOTER_ID), SHOOT_TARGET_RPM);
			targetVelocity = SHOOT_TARGET_RPM;
			targetPower    = SHOOT_POWER;
		}
		else {
			powerError     = OFF_POWER;
			targetVelocity = OFF_TARGET_RPM;
			targetPower    = OFF_POWER;
		}

		power = targetPower + powerError;
		power = MathUtil.clamp(power, 0.0, 1.0);
		
		System.out.println("power:" + power);
		System.out.println("rpm:" + getabsRPM(LEFT_SHOOTER_ID));
		
		SmartDashboard.putNumber("power", power);
		SmartDashboard.putNumber("rpm", getabsRPM(LEFT_SHOOTER_ID));

		rightShooter.set(power);
	}*/


	/****************************************************************************************** 
    *
    *    manualShooterControl()
	*    Gets shooter to appropriate speed without PID
    *   
    ******************************************************************************************/
	public void manualShooterControl(ShootLocation location) {
		shotLocation = location;

		if (location == ShootLocation.OFF) {
			rightShooter.set(OFF_POWER);
			feedMotor.set(OFF_POWER);
			targetVelocity = OFF_TARGET_RPM;
		}
		else if ( ((location == ShootLocation.TEN_FOOT) || (location == ShootLocation.TRENCH)) || (location == ShootLocation.LAY_UP) ) {
			rightShooter.set(SHOOT_POWER);
			targetVelocity = SHOOT_TARGET_RPM;
		}
		else {
			rightShooter.set(OFF_POWER);
			feedMotor.set(OFF_POWER);
			targetVelocity = OFF_TARGET_RPM;
		}
	}


	/****************************************************************************************** 
    *
    *    shooterReadyAuto()
	*    Checks if shooter is ready to fire
    *   
    ******************************************************************************************/
	public boolean shooterReadyAuto() {
		double rpm;
		rpm = getabsRPM(LEFT_SHOOTER_ID);
				
		if ( rpm > targetVelocity )  {
			targetCount ++;
			
			if(targetCount >= 5) { 
				return true;
			}
			else {
				return false;
			}
		}
		else {
			targetCount = 0;
			return false;
		}
	}


	/****************************************************************************************** 
    *
    *    autoShooterControl()
	*    Feeds balls if the shooter is up to speed
    *   
    ******************************************************************************************/
	public void autoBallFeederControl() {
		if (shooterReadyAuto() == true) {
			feedMotor.set(FEED_POWER);
		}
		else {
			feedMotor.set(OFF_POWER);
		}
	}


	/****************************************************************************************** 
     *
     *    Methods relating to the ball feeder
     * 
     ******************************************************************************************/
	/**
	 * Manually controls the feed motor
	 * @param dir
	 */
	public void manualBallFeederControl(BallFeederDirection dir) {
		if (dir == BallFeederDirection.FORWARD) {
			feedMotor.set(FEED_POWER);
		}
		else if (dir == BallFeederDirection.REVERSE) {
			feedMotor.set(FEED_POWER * -1);
		}
		else {
			feedMotor.set(OFF_POWER);
		}
	}

	/**
	 * Enables the feed motor
	 */
	public void enableFeeder() {
		if (shooterReadyAuto() == true) {
			manualBallFeederControl(BallFeederDirection.FORWARD);
		}
	}

	public void disableFeeder() {
		feedMotor.set(OFF_POWER);
	}

	/**
	 * Reverses the feeder motor for 0.5 seconds
	 * @param activePeriodSec
	 * @return status
	 */
	public int reverseFeeder(double activePeriodSec) {
		//Variables
		long startMili;
		long currentTimeMilli;
		double currentTimeSec;

		//Gets the current time
		currentTimeMilli = System.currentTimeMillis();
		//Allows for calculations in seconds
		currentTimeSec = currentTimeMilli / 1000;

		//First Time routine
		if (firstTime == true) {
			//Gets the time at the start of the program
			startMili = System.currentTimeMillis();
			//Allows for calculations in seconds
			startSec = startMili / 1000;

			firstTime = false;
		}

		if ( (currentTimeSec - startSec) <= activePeriodSec ) {
			manualBallFeederControl(BallFeederDirection.REVERSE);
			
			return Robot.CONT;
		}
		else if ( (currentTimeSec - startSec) > activePeriodSec ) {
			disableBallFeeder();

			return Robot.DONE;
		}
		else { //There is no case that I can think of that will trigger this
			disableBallFeeder();

			return Robot.FAIL;
		}
	}


	/****************************************************************************************** 
     *
     *    manualHoodMotorControl()
	 *    Moves hood to given location
     *   
     ******************************************************************************************/
	public int manualHoodMotorControl(Shooter.ShootLocation motorPosition) {

		//Variables
		boolean atFrontSwitch = getFrontSwitchValue();
		boolean atRearSwitch  = getRearSwitchValue ();
		double  hoodCurrentEncoder = hoodMotorEncoder.getPosition();

		//First time through, setting initial values
		if (hoodFirstTime == true) {
			hoodStartEncoder = hoodCurrentEncoder;
			startPosition = motorPosition;

			//Sets target encoder value
			if (motorPosition == ShootLocation.TEN_FOOT) {
				hoodTargetEncoder = TEN_FOOT_HOOD_ENCODER;
			}
			else if (motorPosition == ShootLocation.TRENCH) {
				hoodTargetEncoder = TRENCH_SHOT_HOOD_ENCODER;
			}
			else if (motorPosition == ShootLocation.LAY_UP) {
				hoodTargetEncoder = LAY_UP_HOOD_ENCODER;
			}
			else {
				hoodFirstTime = true;
				disableHoodMotor();
				return Robot.FAIL;
			}

			hoodFirstTime = false;
		}

		//Error checking
		if (startPosition != motorPosition) {
			hoodFirstTime = true;
			disableHoodMotor();
			System.out.println("Changed shoot location, resetting hood movement");
			return Robot.DONE;
		}
		else if (hoodMotor.getOutputCurrent() > HOOD_CURRENT_LIMIT) {
			hoodFirstTime = true;
			disableHoodMotor();
			System.out.println("Hood current limit exceded. Value at " + hoodMotor.getOutputCurrent());
			return Robot.FAIL;
		}
		else if (   ((hoodCurrentEncoder > FRONT_MAX_ENCODER) || (atFrontSwitch))    && (hoodStartEncoder < hoodTargetEncoder) ){ //Going forward and at limit switch 1
			hoodFirstTime = true;
			disableHoodMotor();
			System.out.println("Hood at front limit");
			return Robot.DONE;
		}
		else if (   ((hoodCurrentEncoder < REAR_MAX_ENCODER) || (atRearSwitch))  &&  (hoodStartEncoder > hoodTargetEncoder) ){ //Going backwards and at limit switch 2
			hoodFirstTime = true;
			disableHoodMotor();
			System.out.println("Hood at back limit");
			return Robot.DONE;
		}
		
		//Checking if we have reached target

		//Started behind the target position, move hood forward
		if (hoodStartEncoder < hoodTargetEncoder) {
			//Have moved forward past the target position
			if (hoodCurrentEncoder > hoodTargetEncoder) {
				hoodFirstTime = true;
				disableHoodMotor();
				System.out.println("Hood at target position of " + hoodTargetEncoder + ". Current encoder is " + hoodCurrentEncoder);
				return Robot.DONE;
			}
			else {
				hoodMotor.set(HOOD_POWER);
				return Robot.CONT;
			}
		}
		//Started in front of the target position, move hood backwards
		else if (hoodStartEncoder > hoodTargetEncoder) {
			//Have moved back past the target position
			if (hoodCurrentEncoder < hoodTargetEncoder) {
				hoodFirstTime = true;
				disableHoodMotor();
				System.out.println("Hood at target position of " + hoodTargetEncoder + ". Current encoder is " + hoodCurrentEncoder);
				return Robot.DONE;
			}
			else {
				hoodMotor.set(-1 * HOOD_POWER);
				return Robot.CONT;
			}
		}
		else {
			hoodFirstTime = true;
			disableHoodMotor();
			return Robot.DONE;
		}
	}


	/****************************************************************************************** 
     *
     *    Methods relating to the hood motor and its sensors
     * 
     ******************************************************************************************/
	/**
	 * Gets the value from the frontSwitch
	 * The value returned is opposite from what the sensor gets since it returns true when there is nothing
	 * @return The value of the frontSwitch
	 */
	public boolean getFrontSwitchValue() {
		boolean frontSwitchTriggered;

		frontSwitchTriggered = frontSwitch.get();

		if (frontSwitchTriggered == false) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Gets the value from the rearSwitch
	 * The value returned is opposite from what the sensor gets since it returns true when there is nothing
	 * @return The value of the rearSwitch
	 */
	public boolean getRearSwitchValue() {
		boolean rearSwitchTriggered;

		rearSwitchTriggered = rearSwitch.get();

		if (rearSwitchTriggered == false) {
			return true;
		}
		else {
			return false;
		}
	}

	public void enableHoodMotor(double power) {
		hoodMotor.set(power);
	}

	public void disableHoodMotor() {
		hoodMotor.set(0.00);
	}


    /****************************************************************************************** 
    *
    *    moveHoodFullForward()
    *    Moves hood to forward sensor and calibrates encoders
    * 
    ******************************************************************************************/
	public int moveHoodFullForward() {

		if (calibrateStartTime == 0) {
			calibrateStartTime = System.currentTimeMillis();
		}
		double currentTime = System.currentTimeMillis();

		if (getFrontSwitchValue() == true) { //Reached position sensor
			disableHoodMotor();
			hoodMotorEncoder.setPosition(0.0);
			System.out.println("Hood at sensor 1, encoder is " + hoodMotorEncoder.getPosition());
			return Robot.DONE;
		}
		else if (hoodMotor.getOutputCurrent() >= HOOD_CURRENT_LIMIT) { //Current spike
			disableHoodMotor();
			System.out.println("Hood motor amps: " + hoodMotor.getOutputCurrent());
			return Robot.FAIL;
		} 
		else if (currentTime - calibrateStartTime > 10000) {
			disableHoodMotor();
			System.out.println("Hood calibration timed out");
			return Robot.FAIL;
		}
		else { //No need to stop
			hoodMotor.set(HOOD_POWER);
			return Robot.CONT;
		}
	}


	/**
	 * DEBUG / TEST FUNCTIONS
	 */
	
	 /**
	 * A debug function for the shooter
	 * @param power
	 */
	public void testShoooter(double power) {
		//leftShooter.set(power * -1);
		rightShooter.set(power);
		feedMotor.set(power * -1); //Negative power makes it intake

		System.out.println("Power: " + power + " RPM: " + getabsRPM(LEFT_SHOOTER_ID));
	}

	/**
	 * Prints the speed of the wheel
	 */
	public void printSpeed() {
		double π = Math.PI;
		double wheel_size = 6;                                                 // Wheel diameter Inches 

		double RPM = (getabsRPM(LEFT_SHOOTER_ID) + getabsRPM(RIGHT_SHOOTER_ID) ) / 2; // Rotations per minute average
		double RPH = RPM / 60;                                                 // Rotations per hour
		
		double circumferenceInches = wheel_size * π;                           // Circumference in Inches
		double circumferenceFeet = circumferenceInches / 12;                   // Circumference in Feet
		double circumferenceMiles = circumferenceFeet / 5280;                  // Circumference in Miles

		double MPH = RPH * circumferenceMiles;                                 // Miles Per Hour

		if (RPM > 0) { 
			//System.out.println("MPH: " + MPH);
			if (MPH != 0) {
				System.out.println("RPM 1: " + getabsRPM(LEFT_SHOOTER_ID));
				System.out.println("RPM 2: " + getabsRPM(RIGHT_SHOOTER_ID));
			}
		}
	}

	public void enableShooterFullPower() {
		//leftShooter.set(-0.70);
		rightShooter.set(0.70);
		feedMotor.set(FEED_POWER);

		System.out.println("RPM 1: " + getabsRPM(LEFT_SHOOTER_ID));
		System.out.println("RPM 2: " + getabsRPM(RIGHT_SHOOTER_ID));
	}

	/**
	 * Debug function to enable all three shooting related motors 
	 */
	public void testShooter(double power) {
		testRightShooterMotor(power);
		testBallFeeder();
	}

	/*private void enableLeftShooterMotor(double power) { //Was enableShooterMotor1() 
		leftShooter.set(power * -1);
	}*/

	private void testRightShooterMotor(double power) { //Was enableShooterMotor2()
		rightShooter.set(power);
	}

	private void testBallFeeder() {
		feedMotor.set(-0.50);
	}

	/**
	 * Debug function to disable all motors that are controlled by this class
	 */
	public void disableShooter(){
		disableRightShooterMotor();
		disableBallFeeder();
		disableHoodMotor();
	}

	public void disableRightShooterMotor() { //Was disableShooterMotor2
		rightShooter.set(0.00);
	}

	private void disableBallFeeder() {
		feedMotor.set(0.00);
	}

	/**
	 * Function to display all the different motor RPM's
	 * Doesn't really apply to the hood motor
	 */
	public void displayRPMValues() {
		System.out.println("Shooter 1 RPM: " + shooter1RPM());
		System.out.println("Shooter 2 RPM: " + shooter2RPM());
	}

	private double shooter1RPM() {
		double rpm;
		rpm = getabsRPM(LEFT_SHOOTER_ID);
		
		return rpm;
	}

	private double shooter2RPM() {
		double rpm;
		rpm = getabsRPM(RIGHT_SHOOTER_ID);
		
		return rpm;
	}

	/**
	 * Gets the abs RPM of the passed motor
	 * @return absRPM
	 */
	private double getabsRPM(int MOTOR_CAN_ID) {
		double rpm;
		double absRPM;

		if (MOTOR_CAN_ID == LEFT_SHOOTER_ID) {
			rpm = leftShooterEncoder.getVelocity();
		}
		else if (MOTOR_CAN_ID == RIGHT_SHOOTER_ID) {
			rpm = rightShooterEncoder.getVelocity();
		}
		else if (MOTOR_CAN_ID == HOOD_MOTOR_ID) {
			rpm = hoodMotorEncoder.getVelocity();
		}
		else {
			//It should never come to this case
			rpm = 0;
		}

		absRPM = Math.abs(rpm);

		return absRPM;
	}

   /****************************************************************************************** 
   *
   *    Test functions for shooter 
   * 
   ******************************************************************************************/
	public void testHoodMotor(double power) {
		//Positive power moves hood forward. Reasonable speed is 0.75
		hoodMotor.set(power);
		//System.out.println("Amps: " + hoodMotor.getOutputCurrent() + " Encoder: " + hoodMotorEncoder.getPosition());
		System.out.println("Encoder: " + hoodMotorEncoder.getPosition());

	}

	public void testFeedMotor(double power) {
		//Negative values makes it intake balls
		feedMotor.set(power);
	}

	public void testShootMotors(double power) {
		//Shooter motor 1 (left motor) needs to be negative to shoot a ball
		//Shooter motor 2 (right motor) needs to be positive to shoot a ball
		//leftShooter.set(-power);
		leftShooter.follow(rightShooter, true); //put in the constructor
		rightShooter.set(power);
		System.out.println("Shooter motor power: " + rightShooter.getOutputCurrent());
	}

	public int testHoodMotorEncoder(double encoderTarget){
		
		double encoderCurrent = hoodMotorEncoder.getPosition();

		//Hasn't reached target yet
		if (encoderCurrent > encoderTarget){
			hoodMotor.set(-1* HOOD_POWER);
			return Robot.CONT;
		}
		else if (encoderCurrent <= encoderTarget) {
			hoodMotor.set(0.00);
			return Robot.DONE;
		}
		else {
			return Robot.FAIL;
		}

	}


} //End of the Shooter Class