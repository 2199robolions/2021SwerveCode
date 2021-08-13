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
	public final int LIMITSWITCH_1_ID = 0;
	public final int LIMITSWITCH_2_ID = 1;

	// Encoders
	private CANEncoder shooter_1_Encoder;
	private CANEncoder shooter_2_Encoder;
	private CANEncoder hood_Motor_Encoder;

	//DIO SENSORS
	private DigitalInput limitSwitch_1; //Makes it shoot lower (closet to the front of the robot)
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

	// HOOD MOTOR CONSTANTS
	public static double originalPosition;
	public static double avgPosition;
	public static double highShot;
	public static double lowShot;

	// Variables
	public  double targetVelocity;
	private double targetPower;
	private int targetCount = 0;
	private Shooter.ShootLocation shotLocation = null;
	Shooter.HoodMotorPosition hoodPrevPosition = null;

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

	public static enum HoodMotorPosition {
		LOW_SHOT,
		HIGH_SHOT,
		AVERAGE_POSITION;
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
		shooter_1_Encoder  = shooter_1.getEncoder();
		shooter_2_Encoder  = shooter_2.getEncoder();
		hood_Motor_Encoder = hood_Motor.getEncoder();

		// DIO Sensors
		limitSwitch_1 = new DigitalInput(LIMITSWITCH_1_ID);
		limitSwitch_2 = new DigitalInput(LIMITSWITCH_2_ID);

		// PID Controller
		shooterController = new PIDController(kP, kI, kD);
		//shooterController.enableContinuousInput(0.0, 5500.0);
		//shooterController.setIntegratorRange(0.0, 1.0);
	}

	//  private int    calibrateStatus = Robot.CONT;
	/*
	ALEX PUT THE CALIBRATION STUFF HERE SO IT DOESNT TAKE UP SPACE IN ROBOT
      switch (m_positionSelected) {
        case kCustomAutoRight:
          //Calibrates the hood motor
          if (calibrateStatus == Robot.CONT) {
            calibrateStatus = auto.calibrateHoodMotor();
          }

          autoStatus = auto.defaultAuto(delaySeconds);
          break;
        case kCustomAutoLeft:
          //Calibrates the hood motor
          if (calibrateStatus == Robot.CONT) {
            calibrateStatus = auto.calibrateHoodMotor();
          }

          //Runs the actual auto program
          auto.defaultAuto(delaySeconds);
          break;
        case kCustomAutoCenter:
          //Calibrates the hood motor
          auto.defaultAuto(delaySeconds);
          break;
        case kCustomAutoLRC:
          //Calibrates the hood motor
          auto.defaultAuto(delaySeconds);
          break;
        default:
          //Calibrates the hood motor
          if (calibrateStatus == Robot.CONT) {
            calibrateStatus = auto.calibrateHoodMotor();
          }

          //Runs a default auto program
          auto.defaultAuto(delaySeconds);
          break;
      }
      */


	public void autoShooterControl(ShootLocation location) {
		double  powerError;
		double  power;

		if (location == ShootLocation.OFF) {
			powerError     = OFF_POWER;
			targetVelocity = OFF_TARGET_RPM;
			targetPower    = OFF_POWER;
		}
		else if (location == ShootLocation.TEN_FOOT) {
			powerError     = shooterController.calculate( getabsRPM(SHOOTER_1_ID), TEN_FOOT_TARGET_RPM);
			targetVelocity = TEN_FOOT_TARGET_RPM;
			targetPower    = TEN_FOOT_POWER;

			System.out.println("Ten Foot Shot");
		}
		else if (location == ShootLocation.TRENCH) {
			powerError     = shooterController.calculate( getabsRPM(SHOOTER_1_ID), TRENCH_TARGET_RPM);
			targetVelocity = TRENCH_TARGET_RPM;
			targetPower    = TRENCH_POWER;

			System.out.println("Trench Shot");
		}
		else if (location == ShootLocation.HAIL_MARY) {
			powerError     = shooterController.calculate( getabsRPM(SHOOTER_1_ID), HAIL_MARY_TARGET_RPM);
			targetVelocity = HAIL_MARY_TARGET_RPM;
			targetPower    = HAIL_MARY_POWER;

			System.out.println("Hail Mary Shot!");
		}
		else {
			powerError     = OFF_POWER;
			targetVelocity = OFF_TARGET_RPM;
			targetPower    = OFF_POWER;
		}

		power = MathUtil.clamp(targetPower + powerError, 0.0, 1.0);
		//power = MathUtil.clamp(targetPower, 0.0, 1.0);
		
		System.out.println("power:" + power);
		System.out.println("rpm:" + getabsRPM(SHOOTER_1_ID));
		
		SmartDashboard.putNumber("power", power);
		SmartDashboard.putNumber("rpm", getabsRPM(SHOOTER_1_ID));

		shooter_1.set(power * -1);
		shooter_2.set(power);
	}

	public void manualShooterControl(ShootLocation location) {
		shotLocation = location;

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
		rpm = getabsRPM(SHOOTER_1_ID);
		
		System.out.println("RPM: " + rpm);
		
		if ((rpm > (targetVelocity - ERROR_TARGET_RPM)) &&
			(rpm < (targetVelocity + ERROR_TARGET_RPM)) )  {
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
		if (getabsRPM(SHOOTER_1_ID) >= targetVelocity) {
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
	 * Allows for manual control of the Hood Motor
	 * @param motorPosition
	 */
	public void manualHoodMotorControl(Shooter.HoodMotorPosition motorPosition) {
		//Shorter enum names because I'm lazy
		Shooter.HoodMotorPosition high = Shooter.HoodMotorPosition.HIGH_SHOT;
		Shooter.HoodMotorPosition low  = Shooter.HoodMotorPosition.LOW_SHOT;
		Shooter.HoodMotorPosition avg  = Shooter.HoodMotorPosition.AVERAGE_POSITION;

		//Variables
		boolean limit1 = limitSwitch1Value();
		boolean limit2 = limitSwitch2Value();
		double  hoodPosition = hoodMotorPosition();

		//CONSTANTS
		double  deadZone = 5;
		double  power = 0.10;

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
	}

	/**
	 * Automatic control of the Hood Motor
	 */
	public void autoHoodControl() {
		if (shotLocation == Shooter.ShootLocation.TEN_FOOT) {
			manualHoodMotorControl( Shooter.HoodMotorPosition.HIGH_SHOT );
		}
		else if (shotLocation == Shooter.ShootLocation.TRENCH) {
			manualHoodMotorControl( Shooter.HoodMotorPosition.AVERAGE_POSITION );
		}
		else if (shotLocation == Shooter.ShootLocation.HAIL_MARY) {
			manualHoodMotorControl( Shooter.HoodMotorPosition.LOW_SHOT );
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
		shooter_1.set(power * -1);
		shooter_2.set(power);
		ball_Feeder.set(power * -1); //Negative power makes it intake

		System.out.println("Power: " + power + " RPM: " + getabsRPM(SHOOTER_1_ID));
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

		double RPM = (getabsRPM(SHOOTER_1_ID) + getabsRPM(SHOOTER_2_ID) ) / 2; // Rotations per minute average
		double RPH = RPM / 60;                                                 // Rotations per hour
		
		double circumferenceInches = wheel_size * π;                           // Circumference in Inches
		double circumferenceFeet = circumferenceInches / 12;                   // Circumference in Feet
		double circumferenceMiles = circumferenceFeet / 5280;                  // Circumference in Miles

		double MPH = RPH * circumferenceMiles;                                 // Miles Per Hour

		if (RPM > 0) { 
			//System.out.println("MPH: " + MPH);
			if (MPH != 0) {
				System.out.println("RPM 1: " + getabsRPM(SHOOTER_1_ID));
				System.out.println("RPM 2: " + getabsRPM(SHOOTER_2_ID));
			}
		}
	}

	public void enableShooterFullPower() {
		shooter_1.set(-0.70);
		shooter_2.set(0.70);
		ball_Feeder.set(FEED_POWER);

		System.out.println("RPM 1: " + getabsRPM(SHOOTER_1_ID));
		System.out.println("RPM 2: " + getabsRPM(SHOOTER_2_ID));
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
		shooter_1.set(power * -1);
	}

	private void enableShooterMotor2(double power) {
		shooter_2.set(power);
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
		rpm = getabsRPM(SHOOTER_1_ID);
		
		return rpm;
	}

	private double shooter2RPM() {
		double rpm;
		rpm = getabsRPM(SHOOTER_2_ID);
		
		return rpm;
	}

	/**
	 * Gets the abs RPM of the passed motor
	 * @return absRPM
	 */
	private double getabsRPM(int MOTOR_CAN_ID) {
		double rpm;
		double absRPM;

		if (MOTOR_CAN_ID == SHOOTER_1_ID) {
			rpm = shooter_1_Encoder.getVelocity();
		}
		else if (MOTOR_CAN_ID == SHOOTER_2_ID) {
			rpm = shooter_2_Encoder.getVelocity();
		}
		else if (MOTOR_CAN_ID == HOOD_MOTOR_ID) {
			rpm = hood_Motor_Encoder.getVelocity();
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
		hood_Motor.set(power);
	}

	public void disableHoodMotor() {
		hood_Motor.set(0.00);
	}

	public double hoodMotorPosition() {
		double motorPosition;
		motorPosition = hood_Motor_Encoder.getPosition();

		return motorPosition;
	}

} //End of the Shooter Class