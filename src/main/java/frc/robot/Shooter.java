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

	  ruler			limelight		power
	  distance		distance		to make shot
	  10							.65
	  11							.65
	  12							.65
	  13							.65
	  14							
	  15							
	  16							
	  17							.58
	  18							
	  19			17.5			.60
	  20			18.3			.62 .65
	  21			18.4			.65
	  22			18.9			.68
	  23			19.7			.70
	  24			20.4			.70
	  25			20.9			.70
	  */

public class Shooter {
	
	// SPARK MAX
	private CANSparkMax shooter_1;
	private CANSparkMax shooter_2;
	private CANSparkMax hood_Motor;

	// Victor SP
	private VictorSP ball_Feeder; //Negative power makes it intake balls

	// SPARK MAX ID's
	private int SHOOTER_1_ID  = 17;
	private int SHOOTER_2_ID  = 19;
	private int HOOD_MOTOR_ID = 16;

	// Victor SP Port
	private int BALL_FEEDER_ID = 5;

	// DIO Ports
	private final int LIMTSWITCH_1_ID = 0;
	private final int LIMTSWITCH_2_ID = 1;

	// Encoders
	private CANEncoder encoder_Shooter_1;
	private CANEncoder encoder_Shooter_2;
	private CANEncoder encoder_Hood_Motor;

	//DIO SENSORS
	private DigitalInput limitSwitch_1; //Makes it shoot low (closet to the front of the robot)
	private DigitalInput limitSwitch_2;

	// POWER CONSTANTS
	public final double OFF_POWER       = 0.00 ;
	public final double TEN_FOOT_POWER  = 0.622; //0.66
	public final double TRENCH_POWER    = 0.52 ; //.648 //.645
	public final double HAIL_MARY_POWER = 1.00 ;
	public final double FEED_POWER      = -0.50;

	// RPM CONSTANTS
	public final double OFF_TARGET_RPM       = 0;
	public final double MAX_TARGET_RPM       = 5200; //5300 
	public final double ERROR_TARGET_RPM     = 50.0;
	public final double TEN_FOOT_TARGET_RPM  = 3490; //3572
	public final double TRENCH_TARGET_RPM    = 2840; //2940
	public final double HAIL_MARY_TARGET_RPM = 5530; //5325

	// Variables
	public  double targetVelocity;
	private double targetPower;
	private int targetCount = 0;

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

	// Shooter PID Controller
	private PIDController shooterController;

	//private final double kToleranceDegrees = 2.0f;

	private static final double kP = 0.0004; //0.0003 old value
	private static final double kI = 0.00;
	private static final double kD = 0.00;
	
	/**
	 * CONSTRUCTOR
	 */
	public Shooter() {
		// SPARK Max
		shooter_1   = new CANSparkMax(SHOOTER_1_ID, MotorType.kBrushless);
		shooter_2   = new CANSparkMax(SHOOTER_2_ID, MotorType.kBrushless);
		hood_Motor  = new CANSparkMax(HOOD_MOTOR_ID, MotorType.kBrushless);

		//Victor SP
		ball_Feeder = new VictorSP(BALL_FEEDER_ID);
		
		// Set Shooter related motors to off to Start the Match
		shooter_1.set  (0.0);
		shooter_2.set  (0.0);
		hood_Motor.set (0.0);
		ball_Feeder.set(0.0);

		// Encoders
		encoder_Shooter_1  = shooter_1.getEncoder();
		encoder_Shooter_2  = shooter_2.getEncoder();
		encoder_Hood_Motor = hood_Motor.getEncoder();

		// DIO Sensors

		// PID Controller
		shooterController = new PIDController(kP, kI, kD);
		//shooterController.enableContinuousInput(0.0, 5500.0);
		//shooterController.setIntegratorRange(0.0, 1.0);
	}


	public void autoShooterControl(ShootLocation location) {
		double  powerError;
		double  power;

		if (location == ShootLocation.OFF) {
			powerError     = OFF_POWER;
			targetVelocity = OFF_TARGET_RPM;
			targetPower    = OFF_POWER;
		}
		else if (location == ShootLocation.TEN_FOOT) {
			powerError     = shooterController.calculate( encoder_Shooter_1.getVelocity(), TEN_FOOT_TARGET_RPM);
			targetVelocity = TEN_FOOT_TARGET_RPM;
			targetPower    = TEN_FOOT_POWER;

			System.out.println("Ten Foot Shot");
		}
		else if (location == ShootLocation.TRENCH) {
			powerError     = shooterController.calculate( encoder_Shooter_1.getVelocity(), TRENCH_TARGET_RPM);
			targetVelocity = TRENCH_TARGET_RPM;
			targetPower    = TRENCH_POWER;

			System.out.println("Trench Shot");
		}
		else if (location == ShootLocation.HAIL_MARY) {
			powerError     = shooterController.calculate( encoder_Shooter_1.getVelocity(), HAIL_MARY_TARGET_RPM);
			targetVelocity = HAIL_MARY_TARGET_RPM;
			targetPower    = HAIL_MARY_POWER;

			System.out.println("Hail Mary Shot!");
		}
		else {
			powerError     = OFF_POWER;
			targetVelocity = OFF_TARGET_RPM;
			targetPower    = OFF_POWER;
		}

		//power = MathUtil.clamp(targetPower + powerError, 0.0, 1.0);
		power = MathUtil.clamp(targetPower, 0.0, 1.0);
		
		System.out.println("power:" + power);
		System.out.println("rpm:" + encoder_Shooter_2.getVelocity());
		
		SmartDashboard.putNumber("power", power);
		SmartDashboard.putNumber("rpm", encoder_Shooter_2.getVelocity());

		shooter_1.set(power * -1);
		shooter_2.set(power);
	}

	public void manualShooterControl(ShootLocation location) {

		if (location == ShootLocation.OFF) {
			shooter_1.set(OFF_POWER);
			shooter_2.set(OFF_POWER);
			ball_Feeder.set(OFF_POWER);

			targetVelocity = OFF_TARGET_RPM;
		}
		else if (location == ShootLocation.TEN_FOOT) {
			shooter_1.set(TEN_FOOT_POWER * -1);
			shooter_2.set(TEN_FOOT_POWER);
			
			targetVelocity = TEN_FOOT_TARGET_RPM;
		}
		else if (location == ShootLocation.TRENCH) {
			shooter_1.set(TRENCH_POWER * -1);
			shooter_2.set(TRENCH_POWER);
			
			targetVelocity = TRENCH_TARGET_RPM;
		}
		else if (location == ShootLocation.HAIL_MARY) {
			shooter_1.set(HAIL_MARY_POWER * -1);
			shooter_2.set(HAIL_MARY_POWER);
			
			targetVelocity = HAIL_MARY_TARGET_RPM;
		}
		else {
			shooter_1.set(OFF_POWER);
			shooter_2.set(OFF_POWER);
			ball_Feeder.set(OFF_POWER);
			
			targetVelocity = OFF_TARGET_RPM;
		}
	}

	public boolean shooterReadyAuto() {
		double rpm;
		rpm = encoder_Shooter_2.getVelocity();
		
		System.out.println("RPM: " + rpm);
		
		if ((rpm > (Math.abs(targetVelocity) - ERROR_TARGET_RPM)) &&
			(rpm < (Math.abs(targetVelocity) + ERROR_TARGET_RPM)) )  {
			targetCount ++;
			
			if(targetCount >= 5) { //10 old value
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

	public boolean shooterReady() {
	//System.out.println("velocity:" + encoder_Shooter_1.getVelocity() + " tgtVelocity:" + targetVelocity);
		if (encoder_Shooter_1.getVelocity() >= targetVelocity) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Manual Control of the Ball Feeder Motor
	 * @param dir
	 */
	public void manualBallFeederControl(BallFeederDirection dir) {
		if (dir == BallFeederDirection.FORWARD) {
			ball_Feeder.set(FEED_POWER);
		}
		else if (dir == BallFeederDirection.REVERSE) {
			ball_Feeder.set(FEED_POWER * -1);
		}
		else {
			ball_Feeder.set(OFF_POWER);
		}
	}

	/**
	 * Automatic Control of the Ball Feeder Motor
	 */
	public void autoBallFeederControl() {
		if (shooterReadyAuto() == true) {
			ball_Feeder.set(FEED_POWER);
		}
		else {
			ball_Feeder.set(OFF_POWER);
		}
	}

	/**
	 * A test function for the shooter
	 * @param power
	 */
	public void testShoooter(double power) {
		shooter_1.set(power * -1);
		shooter_2.set(power);
		ball_Feeder.set(power * -1); //Negative power makes it intake

		System.out.println("Power: " + power + " RPM: " + encoder_Shooter_1.getVelocity());
	}

	/**
	 * DEBUG / TEST FUNCTIONS
	 */
	/**
	 * Prints the speed of the wheel
	 */
	public void printSpeed() {
		double π = Math.PI;
		double wheel_size = 6;                                                                  // Wheel diameter Inches 

		double RPM = (encoder_Shooter_1.getVelocity() + (encoder_Shooter_2.getVelocity() * -1) ) / 2;   // Rotations per minute average
		double RPH = RPM / 60;                                                                  // Rotations per hour
		
		double circumferenceInches = wheel_size * π;                                            // Circumference in Inches
		double circumferenceFeet = circumferenceInches / 12;                                    // Circumference in Feet
		double circumferenceMiles = circumferenceFeet / 5280;                                   // Circumference in Miles

		double MPH = RPH * circumferenceMiles;                                                  // Miles Per Hour

		if (RPM > 0) { 
			//System.out.println("MPH: " + MPH);
			if (MPH != 0) {
				System.out.println("RPM 1: " + encoder_Shooter_1.getVelocity());
				System.out.println("RPM 2: " + encoder_Shooter_2.getVelocity());
			}
		}
	}

	public void enableShooterFullPower() {
		shooter_1.set(-0.70);
		shooter_2.set(0.70);
		ball_Feeder.set(FEED_POWER);

		System.out.println("RPM 1: " + encoder_Shooter_1.getVelocity());
	}

	/**
	 * Test function to enable all three shooting related motors 
	 */
	public void enableShooter() {
		enableShooterMotor1();
		enableShooterMotor2();

		enableBallFeeder();
	}

	private void enableShooterMotor1() {
		shooter_1.set(-0.50);
	}

	private void enableShooterMotor2() {
		shooter_2.set(0.50);
	}

	private void enableBallFeeder() {
		ball_Feeder.set(-0.50);
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
		shooter_1.set(0.00);
	}

	private void disableShooterMotor2() {
		shooter_2.set(0.00);
	}

	private void disableBallFeeder() {
		ball_Feeder.set(0.00);
	}

	private void disableHoodMotor() {
		hood_Motor.set(0.00);
	}

	/**
	 * Function to display all the different motor RPM's
	 * Doesn't really apply to the hood motor
	 */
	public void displayRPMValues() {
		System.out.println("Shooter 1 RPM: " + displayShooter1RPM());
		System.out.println("Shooter 2 RPM: " + displayShooter2RPM());
	}

	private double displayShooter1RPM() {
		double rpm;
		rpm = encoder_Shooter_1.getVelocity();
		
		return rpm;
	}

	private double displayShooter2RPM() {
		double rpm;
		rpm = encoder_Shooter_2.getVelocity();
		
		return rpm;
	}

	/**
	 * Function to display the current position of each motor
	 */
	public void displayPosition() {
		System.out.println("Shooter 1 Position: " + displayShooter1Position());
		System.out.println("Shooter 2 Position: " + displayShooter2Position());
		System.out.println("Hood Motor Position: " + displayHoodMotorPosition());
	}

	private double displayShooter1Position() {
		double position;
		position = encoder_Shooter_1.getPosition();
		
		return position;
	}

	private double displayShooter2Position() {
		double position;
		position = encoder_Shooter_2.getPosition();
		
		return position;
	}

	private double displayHoodMotorPosition() {
		double position;
		position = encoder_Hood_Motor.getPosition();

		return position;
	}

} //End of the Shooter Class