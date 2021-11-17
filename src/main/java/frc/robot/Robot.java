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
  private final double REVERSE_FEEDER_TIME = 0.25;

  //VARIABLES
  private int     autoStatus      = Robot.CONT;
  private int     shooterStatus   = Robot.CONT;
  private double  rotatePower;
  private double  driveX;
  private double  driveY;
  private boolean hoodCalibrated  = false;
  private boolean reverseFeeder   = false;

  //State Trackers
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
  
  //Auto Delay
  private static final String kCustomDelayZero  = "0";
	private static final String kCustomDelayTwo   = "2";
	private static final String kCustomDelayFour  = "4";
	private static final String kCustomDelaySix   = "6";

	private int m_delaySelected;
  private final SendableChooser<String> m_delayChooser = new SendableChooser<>();

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
    
    //Set Different Status Cues
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

    //Default Auto Delay
    m_delayChooser.addOption(kCustomDelayZero, kCustomDelayZero);
		m_delayChooser.addOption(kCustomDelayTwo , kCustomDelayTwo);
		m_delayChooser.addOption(kCustomDelayFour, kCustomDelayFour);
    m_delayChooser.addOption(kCustomDelaySix , kCustomDelaySix);
    
    //Default Auto Position
		m_delayChooser.setDefaultOption(kCustomDelayZero, kCustomDelayZero);
		SmartDashboard.putData("Auto Delay", m_delayChooser);

    //Set limelight modes
    drive.changeLimelightLED(Drive.LIMELIGHT_ON);
  }




  @Override
  /****************************************************************************************** 
  *
  *    robotPeriodic()
  *    Always runs while the robot is on
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
    //Set some variables
    autoStatus      = Robot.CONT;
    
    //Auto positions
    m_positionSelected = m_pathChooser.getSelected();

    //Auto Delay
    m_delaySelected = Integer.parseInt(m_delayChooser.getSelected());

    //Telemetry
    System.out.println("Delay: "    + m_delaySelected);
		System.out.println("Position: " + m_positionSelected);
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
        autoStatus = auto.competitionAuto(m_positionSelected, m_delaySelected);
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
  
    drive.circle(3);

    /*switch (step) {
      case 1:
        shooter.testHoodMotor(-0.03);
        if (shooter.getHoodEncoder() < -13) {
          shooter.disableHoodMotor();
          autoStatus = DONE;
        }
        else {
          autoStatus = CONT;
        }
        break;
      case 2:
        shooter.enableShooterFullPower();
        break;
      default:
        step = 1;
    }

    if ( (autoStatus == Robot.DONE) || (autoStatus == Robot.FAIL) ) {
      step++;
    }*/
  
    //autoStatus = shooter.moveHoodFullForward();
    //shooter.testHoodMotorEncoder();
    
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
    rotatePower            = controls.getRotatePower();
    driveX                 = controls.getDriveX();
    driveY                 = controls.getDriveY();
    shootLocation          = controls.getShooterLocation();
    boolean killTargetLock = controls.autoKill();
    boolean fieldDrive     = controls.getFieldDrive();
  
    //Only turns on targetLock mode if autoKill isn't being pressed
    if (killTargetLock == true) {
			wheelMode = Drive.WheelMode.MANUAL;
    } 


    //Manual Drive
    if (wheelMode == Drive.WheelMode.MANUAL) {

      //If robot is out of deadzone, drive normally
      if ((Math.sqrt(driveX*driveX + driveY*driveY) > 0.01) || (Math.abs(rotatePower) > 0.01)) {
        drive.teleopSwerve(driveX, driveY, rotatePower, fieldDrive);
      }
      else {
        //Robot is in dead zone
        drive.stopWheels();
      }

      if ( (shootLocation == Shooter.ShootLocation.TEN_FOOT) || (shootLocation == Shooter.ShootLocation.TRENCH) ) {
        wheelMode = Drive.WheelMode.TRACKING;
      }
      else if (shootLocation == Shooter.ShootLocation.LAY_UP) {
        wheelMode = Drive.WheelMode.LOCKED;
      }
    }
    //Limelight targetting
    else if (wheelMode == Drive.WheelMode.TRACKING) {

      //PID Targeting when in Target Lock Mode
      targetingStatus = drive.limelightPIDTargeting(Drive.TargetPipeline.TEN_FOOT);

      if (targetingStatus == Robot.DONE) {
        wheelMode = Drive.WheelMode.LOCKED;
      }
      else if (targetingStatus == Robot.FAIL) {
        wheelMode = Drive.WheelMode.LOCKED;
      }
      else if (targetingStatus == Robot.CONT) {
        wheelMode = Drive.WheelMode.TRACKING;
      }
      else if (shootLocation == Shooter.ShootLocation.OFF) {
        wheelMode = Drive.WheelMode.MANUAL;
      }
      else if (shootLocation == Shooter.ShootLocation.LAY_UP) {
        wheelMode = Drive.WheelMode.LOCKED;
      }
    }
    // If a target has been aquired, or it times out
    else if (wheelMode == Drive.WheelMode.LOCKED) {
      if (shootLocation == Shooter.ShootLocation.OFF) {
        wheelMode = Drive.WheelMode.MANUAL;
      }
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
    Shooter.BallFeederDirection feedMotorDirection = controls.ballFeederControl();
    boolean changedShooterLocation = (prevShootLocation != shootLocation);
    reverseFeeder = (controls.reverseFeederPower() > 0.1);

    
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

      if (reverseFeeder == true) {
        shooter.manualBallFeederControl(Shooter.BallFeederDirection.REVERSE);
      }
      else {
        shooter.disableFeeder();
      }

      shooter.disableRightShooterMotor();
      shooter.disableHoodMotor();

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
        System.out.println("Done moving hood, going to enable feeder state");
        shooterState = ShooterState.ENABLE_FEEDER_STATE;
      }
      else if (shooterStatus == Robot.CONT) {
        shooterState = ShooterState.MOVE_HOOD_STATE;
      }
      else if ((shooterStatus == Robot.FAIL) || (shootLocation == Shooter.ShootLocation.OFF)) {
        System.out.println("Hood failed, going back to start");
        shooterState = ShooterState.SHOOTER_OFF_STATE;
      }
    }


    //Turns on feed motor if shooter is up to speed
    else if (shooterState == ShooterState.ENABLE_FEEDER_STATE) {
      if (wheelMode == Drive.WheelMode.LOCKED) {
        auto.moveBalls();
      }
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

    //Any other case
    else {
      shooterState = ShooterState.SHOOTER_OFF_STATE;
      shooter.manualBallFeederControl(feedMotorDirection);
    }

    //System.out.println("Hood Encoder: " + shooter.getHoodEncoder());
  }


  /****************************************************************************************** 
  *
  *    climberControl()
  *    Controls the climber mechanism during teleop
  * 
  ******************************************************************************************/
  private void climberControl() {
    //Variables
    boolean enableAllArms;
		boolean DisableTopArm;
    double  climberMotorPower;
    int     climberStatus;
    
    //Get inputs from controllers
    enableAllArms     = controls.climberAllArmsUp();
    DisableTopArm     = controls.climberTopArmDown();
    climberMotorPower = controls.getClimberPower();

    //All Arms Down
    if (climberState == Climber.ClimberState.ALL_ARMS_DOWN) {
      climber.climberDown();
      if (enableAllArms == true) {
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
				climberState = Climber.ClimberState.TOP_ARM_DOWN;
			}
			// Attempting to redeploy arms
			else if (enableAllArms == true) {
				climberState = Climber.ClimberState.START_ARMS_UP;
			}
		}
		else if (climberState == Climber.ClimberState.TOP_ARM_DOWN) {
      // Top arm Down and ready to climb, unless you need to redeploy the climber arms
      climber.topArmDown();

			if (climberMotorPower > 0) {        
				climberState = Climber.ClimberState.CLIMB;
			}
			else if (enableAllArms == true) {
				climberState = Climber.ClimberState.START_ARMS_UP;
			}
		}
		else if (climberState == Climber.ClimberState.CLIMB) {
      // You have climbed
      if (climberMotorPower > 0) {
        climber.climberDown();
      }
      climber.pullRobotUp(climberMotorPower);
      if (enableAllArms == true) {
        climberState = Climber.ClimberState.START_ARMS_UP;
      }
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

} // End of the Robot Class