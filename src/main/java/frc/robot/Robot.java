package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
  // ERROR CODES
  public static final int FAIL = -1;
  public static final int PASS =  1;
  public static final int DONE =  2;
  public static final int CONT =  3;

  //OBJECT CREATION
  private LedLights led;
  private Auto      auto;
  private Drive     drive;
  private Controls  controls;
  private Grabber   grabber;
  private Shooter   shooter;
  private Climber   climber;

  //CONSTANTS
  private final int    LED_DELAY           = 15;
  private final double REVERSE_FEEDER_TIME = 0.25;

  //VARIABLES
  private int     climberStatus;
  private int     ledCurrent;
  private int     delaySeconds;
  private int     autoStatus      = Robot.CONT;
  private int     shooterStatus   = Robot.CONT;
  private double  rotatePower;
  private double  driveX;
  private double  driveY;
  private boolean fieldDriveState = false;
  private double  testHoodPower;
  private int     step           = 1;
  private boolean hoodCalibrated = false;

  private Shooter.ShootLocation shootLocation     = Shooter.ShootLocation.OFF;
  private Shooter.ShootLocation prevShootLocation = Shooter.ShootLocation.OFF;
  private Climber.ClimberState  climberState;

  private static enum ShooterState {
    SHOOTER_OFF_STATE,
    REVERSE_FEEDER_STATE,
    POWER_SHOOTER_STATE,
    MOVE_HOOD_STATE, 
    ENABLE_FEEDER_STATE; 
  }
  private ShooterState shooterState = ShooterState.SHOOTER_OFF_STATE;


  //Setting Up WheelMode for limelight
	private Drive.WheelMode wheelMode;
	private int targetingStatus;


  /**
   * SMART DASHBOARD CHOICES
   */
  //Position
	private static final String kCustomAutoRight  = "Right";
	private static final String kCustomAutoCenter = "Center";
	private static final String kCustomAutoLeft   = "Left";
	private static final String kCustomAutoLRC    = "L/R/C Simple";

	private String m_positionSelected;
	private final SendableChooser<String> m_pathChooser = new SendableChooser<>();


  /**
   * Constructor
   */
  public Robot() {
    //Instance Creation
    led      = LedLights.getInstance();
    controls = Controls.getInstance();
    drive    = new Drive();
    grabber  = new Grabber();
    shooter  = new Shooter();
    climber  = new Climber();
    auto     = new Auto(drive, grabber, shooter);

    //Set Variables
    ledCurrent = 0;

    //Set Different Status Cues
    climberStatus = Robot.DONE;
    climberState  = Climber.ClimberState.ALL_ARMS_DOWN;
    wheelMode     = Drive.WheelMode.MANUAL;
  }

  @Override
  /****************************************************************************************** 
  *
  *    robotInit()
  *    Runs once when the robot is started
  * 
  ******************************************************************************************/
  public void robotInit() {
    /**
     * SMART DASHBOARD CHOICES
     */
    //Auto Positions
		m_pathChooser.addOption(kCustomAutoRight, kCustomAutoRight);
		m_pathChooser.addOption(kCustomAutoCenter, kCustomAutoCenter);
		m_pathChooser.addOption(kCustomAutoLeft, kCustomAutoLeft);
		m_pathChooser.addOption(kCustomAutoLRC, kCustomAutoLRC);

		//Default Auto Position
		m_pathChooser.setDefaultOption(kCustomAutoLRC, kCustomAutoLRC);
		SmartDashboard.putData("Auto Positions", m_pathChooser);

    //Auto Delay
    SmartDashboard.putNumber("Auto delay", 0);
		
    //Set limelight modes
    drive.changeLimelightLED(Drive.LIMELIGHT_ON);

    //Smartdashboard test values
    //SmartDashboard.putNumber("Hood power", 0.05);
    //SmartDashboard.putNumber("Hood target", -2);

  }




  @Override
  /****************************************************************************************** 
  *
  *    robotPeriodic()
  *    Always runs while the robot is on?????
  * 
  ******************************************************************************************/
  public void robotPeriodic() {
    //
  }




  @Override
  /****************************************************************************************** 
  *
  *    autonomousInit()
  *    Runs once when autonomous is started
  * 
  ******************************************************************************************/
  public void autonomousInit() {
    autoStatus      = Robot.CONT;

    
    //Auto positions
    m_positionSelected = m_pathChooser.getSelected();

    //Auto Delay
    delaySeconds = (int) SmartDashboard.getNumber("Auto Delay", 0);

    //Telemetry
    System.out.println("Delay: "    + delaySeconds);
		System.out.println("Position: " + m_positionSelected);
    
    //LED Color Set
    led.autoMode();
  }




  @Override
  /****************************************************************************************** 
  *
  *    autonomousPeriodic()
  *    Runs every 20 ms while auto mode is on
  * 
  ******************************************************************************************/
  public void autonomousPeriodic() {

    //Calibrates robot when necessary
    if (hoodCalibrated == false) {
      calibrateRobot();
    }
    else {
      if (autoStatus == Robot.CONT) {
        autoStatus = auto.competitionAuto(m_positionSelected, delaySeconds);
      }
      else {
        drive.stopWheels();
      }
    }

    
  }



  @Override
  /****************************************************************************************** 
  *
  *    teleopInit()
  *    Runs once when teleop is started
  * 
  ******************************************************************************************/
  public void teleopInit() {
    //
  }



  @Override
  /****************************************************************************************** 
  *
  *    teleopPeriodic()
  *    Runs every 20 ms while teleop mode is on
  * 
  ******************************************************************************************/
  public void teleopPeriodic() {

    //Sets the color of the LED's (when we get them)
    led.defaultMode("Blue");

    //Controls the wheels
    wheelControl();

    //Controls the grabber and shooter
    if (hoodCalibrated == true) {
      ballControl();
    }
    else {
      calibrateRobot();
    }

    //Controls the climber arm and motor
    climberControl();
  }



  @Override
  /****************************************************************************************** 
  *
  *    disabledInit()????
  *    Runs once when robot is disabled
  * 
  ******************************************************************************************/
  public void disabledInit() {
    //I don't know why you'd put anything in this
  }

  @Override
  /****************************************************************************************** 
  *
  *    disabledPeriodic()??????????????
  *    Runs every 20 ms while robot is disabled
  * 
  ******************************************************************************************/
  public void disabledPeriodic() {
    //I don't know why you'd put anything in this
  }




  @Override
  /****************************************************************************************** 
  *
  *    testInit()
  *    Runs once when test mode is started
  * 
  ******************************************************************************************/
  public void testInit() {
    autoStatus = Robot.CONT;
    step = 1;
  }



  @Override
  /****************************************************************************************** 
  *
  *    testPeriodic()
  *    Runs every 20 ms while test mode is on
  * 
  ******************************************************************************************/
  public void testPeriodic() {

    if (controls.autoKill() == true) {
      autoStatus = Robot.FAIL;
    }
  /*
    switch (step) {
      case 1:
        autoStatus = shooter.moveHoodFullForward();
        break;
      case 2:
        autoStatus = shooter.testHoodMotorEncoder(SmartDashboard.getNumber("Hood target", -2));
        break;
      case 3:
        shooter.testShootMotors(1.0);
        shooter.testFeedMotor(Shooter.FEED_POWER);
        break;
      default:
        step = 1;
    }

    if ( (autoStatus == Robot.DONE) || (autoStatus == Robot.FAIL) ) {
      step++;
    }*/
  
      //autoStatus = shooter.moveHoodFullForward();
      //shooter.testHoodMotorEncoder();
      shooter.testHoodMotor(-0.03);
      //shooter.testFeedMotor(-0.25);
      //shooter.testShootMotors(1);
    

 
    /*double tempPower;
    tempPower = SmartDashboard.getNumber("Input Power", 0.5);
    shooter.enableShooter(tempPower);*/
  }



  /****************************************************************************************** 
  *
  *    wheelControl()
  *    Controls wheel portion of teleop code
  * 
  ******************************************************************************************/
  private void wheelControl() {
    //Drive inputs
    rotatePower = controls.getRotatePower();
    driveX      = controls.getDriveX();
    driveY      = controls.getDriveY();


    //Only turns on targetLock mode if autoKill isn't being pressed
    if ( controls.autoKill() == true ) {
			wheelMode = Drive.WheelMode.MANUAL;
    } 
    else if ( controls.enableTargetLock() == true ) {
			wheelMode = Drive.WheelMode.TARGET_LOCK;
    } 
    else {
      wheelMode = Drive.WheelMode.MANUAL; 
    }


    //Manual Drive
    if (wheelMode == Drive.WheelMode.MANUAL) {

      //If robot is out of deadzone, drive normally
      if ((Math.sqrt(driveX*driveX + driveY*driveY) > 0.01) || (Math.abs(rotatePower) > 0.01)) {
        drive.teleopSwerve(driveX, driveY, rotatePower);
      } 
      else {
        //Robot is in dead zone
        drive.stopWheels();
      }

      //Ensures that the drivers can actually see the limelight signals
      ledCurrent ++;

      if (ledCurrent >= LED_DELAY) {
        ledCurrent = 0;

        led.defaultMode("Team");
      }
    }
    //Limelight targetting
    else if (wheelMode == Drive.WheelMode.TARGET_LOCK) {

      //PID Targeting when in Target Lock Mode
      targetingStatus = drive.limelightPIDTargeting(Drive.TargetPipeline.TEN_FOOT);

      if (targetingStatus == Robot.DONE) {
        wheelMode = Drive.WheelMode.MANUAL;
      }
      else if (targetingStatus == Robot.FAIL) {
        wheelMode = Drive.WheelMode.MANUAL;
      }

      ledCurrent = 0;
    }
  }

  
  /****************************************************************************************** 
  *
  *    ballControl()
  *    Controls ball manipulation portion of teleop code
  * 
  ******************************************************************************************/
  private void ballControl() {
    //Grabber Variables
    boolean grabberDeployRetract;
    Grabber.GrabberDirection grabberDirection;
    
    //Grabber
		grabberDeployRetract = controls.grabberDeployRetract();
		grabberDirection     = controls.getGrabberDir();
    
    //Shooter
    prevShootLocation    = shootLocation;
    shootLocation        = controls.getShooterLocation();
    boolean changedShooterLocation = (prevShootLocation != shootLocation);

    
    /*****   Grabber Deploy Retract   *****/
		if (grabberDeployRetract == true) {
			grabber.deployRetract();
		}
		
		/******   Grabber motor Forward, Reverse or OFF   *****/
    grabber.setGrabberMotor(grabberDirection);



    /******   SHOOTER STATE MACHINE   *****/
    //Shooter is off, reset values and check if we should begin to shoot
    if (shooterState == ShooterState.SHOOTER_OFF_STATE) {
      //Resetting values
      shooterStatus = Robot.CONT;
      
      shooter.disableShooter();

      //Checking if shooting should begin
      if (shootLocation != Shooter.ShootLocation.OFF) {
        shooterState = ShooterState.REVERSE_FEEDER_STATE;
      }
    }


    //Feed motor reverses to clear jams then moves on
    else if (shooterState == ShooterState.REVERSE_FEEDER_STATE) {
      //Method to reverse the feeder for a certain ammount of time
      shooterStatus = shooter.reverseFeeder(REVERSE_FEEDER_TIME);
      shooter.disableHoodMotor();
      shooter.disableRightShooterMotor();

      if (shooterStatus == Robot.DONE) {
        shooterState = ShooterState.POWER_SHOOTER_STATE;
      }
      else if ((shooterStatus == Robot.FAIL) || (shootLocation == Shooter.ShootLocation.OFF)) {
        shooterState = ShooterState.SHOOTER_OFF_STATE;
      }
      else if (shooterStatus == Robot.CONT) {
        shooterState = ShooterState.REVERSE_FEEDER_STATE;
      }                                           
      
    } 


    //Turns on shooter motor and goes to next step
    else if (shooterState == ShooterState.POWER_SHOOTER_STATE) {
      shooter.manualShooterControl(shootLocation);
      shooter.disableHoodMotor();
      shooter.disableFeeder();
      
      if (shootLocation == Shooter.ShootLocation.OFF) {
        shooterState = ShooterState.SHOOTER_OFF_STATE;
      }
      else {
        shooterState = ShooterState.MOVE_HOOD_STATE;
      }
    }


    //Moves hood to proper location. Goes back to previous step if user changed shoot location
    else if (shooterState == ShooterState.MOVE_HOOD_STATE) {
      shooterStatus = shooter.manualHoodMotorControl(shootLocation);
      shooter.disableFeeder();
      //Leave shooter motor on, needs to maintain speed

      if (shooterStatus == Robot.DONE) {
        shooterState = ShooterState.ENABLE_FEEDER_STATE;
      }
      else if (shooterStatus == Robot.CONT) {
        shooterState = ShooterState.MOVE_HOOD_STATE;
      }
      else if ((shooterStatus == Robot.FAIL) || (shootLocation == Shooter.ShootLocation.OFF)) {
        shooterState = ShooterState.SHOOTER_OFF_STATE;
      }
    }


    //Turns on feed motor if shooter is up to speed
    else if (shooterState == ShooterState.ENABLE_FEEDER_STATE) {
      shooter.enableFeeder();
      shooter.disableHoodMotor();
      //Leave shooter motor on, needs to maintain speed

      if (shootLocation == Shooter.ShootLocation.OFF) {
        shooterState = ShooterState.SHOOTER_OFF_STATE;
      }    
      else if (changedShooterLocation == true) {
        shooterState = ShooterState.MOVE_HOOD_STATE;
      } 
      else {
        shooterState = ShooterState.ENABLE_FEEDER_STATE;
      }        
    }

    else { 
      shooterState = ShooterState.SHOOTER_OFF_STATE;
    }
  }


  /*****   Climber Control   *****/
  //Climber stuff
  private void climberControl() {
    //Variables
    boolean enableAllArms;
		boolean DisableTopArm;
    double  climberMotorPower;
    
    //Get inputs from controllers
    enableAllArms     = controls.climberAllArmsUp();
    DisableTopArm     = controls.climberTopArmDown();
    climberMotorPower = controls.getClimberPower();

    //All Arms Down
    if (climberState == Climber.ClimberState.ALL_ARMS_DOWN) {
      if (enableAllArms == true) {
        climberStatus = auto.climberDeploy(climber);
				climberState = Climber.ClimberState.START_ARMS_UP;
      }
    }
		else if (climberState == Climber.ClimberState.START_ARMS_UP) {
      // Ready to deploy climber arms
			climberStatus = auto.climberDeploy(climber);
      
      if ( climberStatus == Robot.DONE ) {
				climberState = Climber.ClimberState.ALL_ARMS_UP;
			}
		}
		else if (climberState == Climber.ClimberState.ALL_ARMS_UP) {
      // All arms are Up
			if (DisableTopArm == true) {
        climber.topArmDown();
        
				climberState = Climber.ClimberState.TOP_ARM_DOWN;
			}
			// Attempting to redeploy arms
			else if (enableAllArms == true) {
				climberState = Climber.ClimberState.START_ARMS_UP;
			}
		}
		else if (climberState == Climber.ClimberState.TOP_ARM_DOWN) {
      // Top arm Down and ready to climb, unless you need to redeploy the climber arms
			if (climberMotorPower > 0) {
				climber.pullRobotUp(climberMotorPower);
        climber.climberDown();
        
				climberState = Climber.ClimberState.CLIMB;
			}
			else if (enableAllArms == true) {
				climberState = Climber.ClimberState.START_ARMS_UP;
			}
		}
		else if (climberState == Climber.ClimberState.CLIMB) {
      // You have climbed
			climber.pullRobotUp(climberMotorPower);
		}
  }

  /****************************************************************************************** 
  *
  *    calibrateRobot()
  *    Calibrates hoodMotor and anything else the robot may need to calibrate, called in auto or teleop once
  * 
  ******************************************************************************************/
  private void calibrateRobot() {
    //Calibrates hood motor    
    if (hoodCalibrated == false) {
      int hoodStatus;

      hoodStatus = shooter.moveHoodFullForward();
      
      if ( (hoodStatus == Robot.DONE) || (hoodStatus == Robot.FAIL) ) {
        hoodCalibrated = true;
      }
      
      System.out.println("Calibrating hood motor");
      
      return;
    }
  }

  /****************************************************************************************** 
   *
   *    fieldDrive()
   *    returns if we are in field drive mode   
   * 
   ******************************************************************************************/
   private boolean fieldDrive() {

    if (controls.toggleFieldDrive() == true) {
      fieldDriveState = !fieldDriveState; //Toggles fieldDriveState
    }
    
    return fieldDriveState;
  }

} // End of the Robot Class