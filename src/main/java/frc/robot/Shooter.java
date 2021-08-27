package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpiutil.math.MathUtil;

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
	public final int LIMITSWITCH_1_ID = 0;
	public final int LIMITSWITCH_2_ID = 1;

	// Encoders
	private CANEncoder leftShooterEncoder;
	private CANEncoder rightShooterEncoder;
	private CANEncoder hoodMotorEncoder;

	//DIO SENSORS
	private DigitalInput limitSwitch_1; //Makes it shoot lower (closet to the front of the robot)
	private DigitalInput limitSwitch_2;

	// POWER CONSTANTS
	public final double SHOOT_POWER     = 1.00; //It's more effective to adjust the angle for each shot than the speed
	public final double OFF_POWER       = 0.00;

	// RPM CONSTANTS
	public final double SHOOT_TARGET_RPM   = 5500; //Not tested
	public final double OFF_TARGET_RPM     = 0;

	// HOOD MOTOR CONSTANTS
	public static  final double   TEN_FOOT_HOOD_ENCODER    = -6;
	public static  final double   TRENCH_SHOT_HOOD_ENCODER = -4; //Not certain
	public static  final double   HAIL_MARY_HOOD_ENCODER   = -7; //Not tested
	public static  final double   LOW_SHOT_HOOD_ENCODER    = 0;
	public static  final double   HIGH_SHOT_HOOD_ENCODER   = -15; //Not tested     

	private static final double   HOOD_POWER = 0.075;
	private static final int      HOOD_CURRENT_LIMIT = 3;

	// FEED MOTOR CONSTANTS
	private static final double   FEED_POWER = -0.25;

	// Variables
	public  double                     targetVelocity;
	private double                     targetPower;
	private int                        targetCount = 0;
	private boolean                    hoodCalibrated = false;
	private Shooter.ShootLocation      shotLocation = null;
	Shooter.HoodMotorPosition          hoodPrevPosition = null;
	private boolean                    firstTime = true;
	private double                     hoodTargetPosition = 0;
	private Shooter.HoodMotorDirection hoodDirection = HoodMotorDirection.OFF;


	public static enum ShootLocation {
		HAIL_MARY,
		TRENCH,
		TEN_FOOT,
		OFF;
	}

	public static enum BallFeederDirection {
		FORWARD,
		REVERSE,
		OFF;
	}

	public static enum HoodMotorDirection {
		FORWARD,
		REVERSE,
		OFF;
	}

	public static enum HoodMotorPosition {
		TEN_FOOT_SHOT,
		TRENCH_SHOT,
		HAIL_MARY_SHOT,
		LOW_SHOT,
		HIGH_SHOT;
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

		leftShooter.follow(RIGHT_SHOOTER_ID, true); 

		//Victor SP
		feedMotor = new VictorSP(BALL_FEEDER_ID);
		
		// Set Shooter related motors to off to Start the Match
		leftShooter.set (0.0);
		rightShooter.set (0.0);
		hoodMotor.set (0.0);
		feedMotor.set (0.0);

		// Encoders
		leftShooterEncoder  = leftShooter.getEncoder();
		rightShooterEncoder  = rightShooter.getEncoder();
		hoodMotorEncoder   = hoodMotor.getEncoder();

		// DIO Sensors
		limitSwitch_1 = new DigitalInput(LIMITSWITCH_1_ID);
		limitSwitch_2 = new DigitalInput(LIMITSWITCH_2_ID);

		// PID Controller
		shooterController = new PIDController(kP, kI, kD);
	}


	/****************************************************************************************** 
    *
    *    autoShooterControl()
	*    Uses PID to get shooter to appropriate speed for given shot
    *   
    ******************************************************************************************/
	public void autoShooterControl(ShootLocation location) {
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
	}


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
		else if ( ((location == ShootLocation.TEN_FOOT) || (location == ShootLocation.TRENCH)) || (location == ShootLocation.HAIL_MARY) ) {
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
		
		System.out.println("RPM: " + rpm);
		
		if ((rpm > targetVelocity )  {
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
    *    manualBallFeederControl()
	*    Manually controls the feeder motor
    *   
    ******************************************************************************************/
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
    *    manualHoodMotorControl()
	*    Moves hood to given location
    *   
    ******************************************************************************************/
	public int manualHoodMotorControl(Shooter.HoodMotorPosition motorPosition) {

		//Calibrates hood motor if it hasn't been done yet
		if (hoodCalibrated == false) {
			moveHoodFullForward(); //Once moveHoodFullForward is complete it will set hoodCalibrated to true and not run this line again
		}

		//Variables
		boolean limit1 = limitSwitch1Value();
		boolean limit2 = limitSwitch2Value();
		double  hoodPosition = hoodMotorPosition();


		//First time through, setting initial values
		if (firstTime == true) {
			double firstPosition = hoodPosition;

			//Sets target encoder value
			if (motorPosition == HoodMotorPosition.LOW_SHOT) {
				hoodTargetPosition = LOW_SHOT_HOOD_ENCODER;
			}
			else if (motorPosition == HoodMotorPosition.HIGH_SHOT) {
				hoodTargetPosition = HIGH_SHOT_HOOD_ENCODER;
			}
			else if (motorPosition == HoodMotorPosition.TEN_FOOT) {
				hoodTargetPosition = TEN_FOOT_HOOD_ENCODER;
			}
			else if (motorPosition == HoodMotorPosition.TRENCH_SHOT) {
				hoodTargetPosition = TRENCH_SHOT_HOOD_ENCODER;
			}
			else if (motorPosition == HoodMotorPosition.HAIL_MARY) {
				hoodTargetPosition = HAIL_MARY_HOOD_ENCODER;
			}
			else {
				return Robot.FAIL;
			}

			//Finds direction that hood needs to move
			if (firstPosition > hoodTargetPosition) {
				direction = HoodMotorDirection.REVERSE;
			}
			else if (firstPosition < hoodTargetPosition) {
				direction = HoodMotorDirection.FORWARD;
			}
			else if (firstPosition == hoodTargetPosition) {
				direction = HoodMotorDirection.OFF;
			}
			else {
				return Robot.FAIL;
			}

			firstTime == false;
		}


		//Error checking
		if (hoodMotor.getOutputCurrent > HOOD_CURRENT_LIMIT) {
			firstTime = true;
			hoodMotor.set(OFF_POWER);
			return Robot.FAIL;
		}
		else if ( ((hoodPosition > limit1) || (limit1)) && (direction == HoodMotorDirection.FORWARD) ){ //Going forward and at limit switch
			firstTime = true;
			hoodMotor.set(OFF_POWER);
			return Robot.DONE;
		}


		//Checking if we have reached target
		if (direction == HoodMotorDirection.FORWARD) {
			if (hoodPosition > hoodTargetPosition) {
				firstTime = true;
				hoodMotor.set(OFF_POWER);
				return Robot.DONE;
			}
			else {
				hoodMotor.set(HOOD_POWER);
				return Robot.CONT;
			}
		}
		else if (direction == HoodMotorDirection.REVERSE) {
			if (hoodPosition < hoodTargetPosition) {
				firstTime = true;
				hoodMotor.set(OFF_POWER);
				return Robot.DONE;
			}
			else {
				hoodMotor.set(-1 * HOOD_POWER);
				return Robot.CONT;
			}
		}
		else {
			return Robot.FAIL;
		}


/*
		if (motorPosition == high) {
			if (hoodPrevPosition == low || hoodPrevPosition == avg) {
				if (limit1 == false) {
					if (hoodPosition < (lowShot - deadZone)) {
						enableHoodMotor(power);
					}
					else {
						disableHoodMotor();
					}					
				}
				else {
					disableHoodMotor();
				}
			}
			else {
				disableHoodMotor();
			}

			hoodPrevPosition = high;
		}
		else if (motorPosition == low) {
			if (hoodPrevPosition == high || hoodPrevPosition == avg) {
				if (limit2 == false) {
					if (hoodPosition > (lowShot + deadZone)) {
						enableHoodMotor(power * -1);
					}
					else {
						disableHoodMotor();
					}
				}
				else {
					disableHoodMotor();
				}
			}
			else {
				disableHoodMotor();
			}

			hoodPrevPosition = low;
		}
		else if (motorPosition == avg) {
			if (hoodPrevPosition == low) {
				if (limit1 == false) {
					if (hoodPosition < (avgPosition - deadZone)) {
						enableHoodMotor(power);
					}
					else {
						disableHoodMotor();
					}
				}
				else {
					disableHoodMotor();
				}
			}
			else if (hoodPrevPosition == high) {
				if (limit2 == false) {
					if (hoodPosition > (avgPosition + deadZone)) {
						enableHoodMotor(power * -1);
					}
					else {
						disableHoodMotor();
					}
				}
				else {
					disableHoodMotor();
				}
			}
			else {
				disableHoodMotor();
			}

			hoodPrevPosition = avg;
		}
		else {
			disableHoodMotor();
		}
	}*/





	/****************************************************************************************** 
    *
    *    autoHoodMotorControl()
	*    Moves hood to location that we are shooting from
    *   
    ******************************************************************************************/
	public void autoHoodControl() {
		if (shotLocation == Shooter.ShootLocation.TEN_FOOT) {
			manualHoodMotorControl( Shooter.HoodMotorPosition.TEN_FOOT_SHOT );
		}
		else if (shotLocation == Shooter.ShootLocation.TRENCH) {
			manualHoodMotorControl( Shooter.HoodMotorPosition.TRENCH_SHOT );
		}
		else if (shotLocation == Shooter.ShootLocation.HAIL_MARY) {
			manualHoodMotorControl( Shooter.HoodMotorPosition.HAIL_MARY_SHOT );
		}
		else {
			disableHoodMotor();
		}
	}

	/**
	 * A test function for the shooter
	 * @param power
	 */
	public void testShoooter(double power) {
		leftShooter.set(power * -1);
		rightShooter.set(power);
		feedMotor.set(power * -1); //Negative power makes it intake

		System.out.println("Power: " + power + " RPM: " + getabsRPM(LEFT_SHOOTER_ID));
	}

	/**
	 * DEBUG / TEST FUNCTIONS
	 */
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
		leftShooter.set(-0.70);
		rightShooter.set(0.70);
		feedMotor.set(FEED_POWER);

		System.out.println("RPM 1: " + getabsRPM(LEFT_SHOOTER_ID));
		System.out.println("RPM 2: " + getabsRPM(RIGHT_SHOOTER_ID));
	}

	/**
	 * Test function to enable all three shooting related motors 
	 */
	public void enableShooter(double power) {
		enableShooterMotor1(power);
		enableShooterMotor2(power);

		enableBallFeeder();
	}

	private void enableShooterMotor1(double power) {
		leftShooter.set(power * -1);
	}

	private void enableShooterMotor2(double power) {
		rightShooter.set(power);
	}

	private void enableBallFeeder() {
		feedMotor.set(-0.50);
	}

	/**
	 * Test function to disable all motors that are controlled by this class
	 */
	public void disableShooter(){
		disableShooterMotor1();
		disableShooterMotor2();
		disableBallFeeder();
		disableHoodMotor();
	}

	private void disableShooterMotor1() {
		leftShooter.set(0.00);
	}

	private void disableShooterMotor2() {
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
			rpm = leftShooter_Encoder.getVelocity();
		}
		else if (MOTOR_CAN_ID == RIGHT_SHOOTER_ID) {
			rpm = rightShooter_Encoder.getVelocity();
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

	/**
	 * Methods relating to the Hood Motor and its sensors
	 */
	/**
	 * Gets the value from limit switch 1
	 * The value returned is opposite from what the sensor gets since it returns true when there is nothing
	 * @return The value of limitswitch 1
	 */
	public boolean limitSwitch1Value() {
		boolean limitSwitch1Triggered;

		limitSwitch1Triggered = limitSwitch_1.get();

		if (limitSwitch1Triggered == false) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Gets the value from limit switch 2
	 * The value returned is opposite from what the sensor gets since it returns true when there is nothing
	 * @return The value of limitswitch 2
	 */
	public boolean limitSwitch2Value() {
		boolean limitSwitch2Triggered;

		limitSwitch2Triggered = limitSwitch_2.get();

		if (limitSwitch2Triggered == false) {
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
   *    hoodMotorPosiiton()
   *    Returns hood motor encoder value
   *    Positive power increases encoder value
   * 
   ******************************************************************************************/
	public double hoodMotorPosition() {
		double motorPosition;
		motorPosition = hoodMotorEncoder.getPosition();

		return motorPosition;
	}



   /****************************************************************************************** 
   *
   *    moveHoodFullForward()
   *    Moves hood to forward sensor and calibrates encoders
   * 
   ******************************************************************************************/
	public int moveHoodFullForward() {

		if (limitSwitch1Value() == true) { //Reached position sensor
			disableHoodMotor();
			hoodMotorEncoder.setPosition(0.0);
			System.out.println("At sensor 1");
			hoodCalibrated = true;
			return Robot.DONE;
		}
		else if (hoodMotor.getOutputCurrent() >= HOOD_CURRENT_LIMIT) { //Current spike
			disableHoodMotor();
			System.out.println("Amps: " + hoodMotor.getOutputCurrent());
			return Robot.FAIL;
		} 
		else { //No need to stop
			hoodMotor.set(HOOD_POWER);
			return Robot.CONT;
		}
	}

	/****************************************************************************************** 
   *
   *    moveHoodFullForward()
   *    Moves hood to forward sensor and calibrates encoders
   * 
   ******************************************************************************************/
  public int moveHoodToTenFeet() {

	if (limitSwitch1Value() == true) { //Reached position sensor
		disableHoodMotor();
		hoodMotorEncoder.setPosition(0.0);
		System.out.println("At sensor 1");
		return Robot.DONE;
	}
	else if (hoodMotor.getOutputCurrent() >= HOOD_CURRENT_LIMIT) { //Current spike
		disableHoodMotor();
		System.out.println("Amps: " + hoodMotor.getOutputCurrent());
		return Robot.FAIL;
	} 
	else { //No need to stop
		hoodMotor.set(HOOD_POWER);
		return Robot.CONT;
	}
}


   /****************************************************************************************** 
   *
   *    Test functions for shooter 
   * 
   ******************************************************************************************/
	public void testHoodMotor(double power) {
		//Positive power moves hood forward. Reasonable speed is 0.75
		hoodMotor.set(power);
		System.out.println("Amps: " + hoodMotor.getOutputCurrent() + " Encoder: " + hoodMotorEncoder.getPosition());
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
		
		double encoderCurrent = hoodMotorPosition();

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