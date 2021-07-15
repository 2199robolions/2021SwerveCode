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
  private final int LED_DELAY = 15;

  //VARIABLES
  private int    climberStatus;
  private int    ledCurrent;
  private int    delaySeconds;
  private int    autoStatus      = Robot.CONT;
  private int    calibrateStatus = Robot.CONT;
  private Climber.ClimberState climberState;
  private double rotatePower;
  private double driveX;
  private double driveY;

  //Setting Up WheelMode
	private Drive.WheelMode wheelMode;
	private int targetingStatus;

  /**
   * SMART DASHBOARD CHOICES
   */
  //Position
	private static final String kDefaultAuto      = "Default";
	private static final String kCustomAutoRight  = "Right";
	private static final String kCustomAutoCenter = "Center";
	private static final String kCustomAutoLeft   = "Left";
	private static final String kCustomAutoLRC    = "L/R/C Simple";

	private String m_positionSelected;
	private final SendableChooser<String> m_pathChooser = new SendableChooser<>();

  //Delay
	private static final String kDefaultTime    = "0";
	private static final String kCustomChooser2 = "2";
	private static final String kCustomChooser4 = "4";
	private static final String kCustomChooser6 = "6";

	private String m_delaySelected;
	private final SendableChooser<String> m_delayChooser = new SendableChooser<>();

  // Alliance Color
	private static final String kDefaultColor   = "Default";
	private static final String kBlueAlliance   = "Blue";
	private static final String kRedAlliance   = "Red";

	private String alliance;
	private final SendableChooser<String> allianceColor = new SendableChooser<>();

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
  public void robotInit() {
    /**
     * SMART DASHBOARD CHOICES
     */
    //Auto Positions
		m_pathChooser.addOption(kDefaultAuto , kDefaultAuto);
		m_pathChooser.addOption(kCustomAutoRight, kCustomAutoRight);
		m_pathChooser.addOption(kCustomAutoCenter, kCustomAutoCenter);
		m_pathChooser.addOption(kCustomAutoLeft, kCustomAutoLeft);
		m_pathChooser.addOption(kCustomAutoLRC, kCustomAutoLRC);

		//Default Auto Position
		m_pathChooser.setDefaultOption(kDefaultAuto, kDefaultAuto);
		SmartDashboard.putData("Auto Positions", m_pathChooser);

    //Auto Delay
		m_delayChooser.addOption(kDefaultTime,    "0");
		m_delayChooser.addOption(kCustomChooser2, "2");
		m_delayChooser.addOption(kCustomChooser4, "4");
		m_delayChooser.addOption(kCustomChooser6, "6");
		
		//Default Auto Delay
		m_delayChooser.setDefaultOption(kDefaultTime, kDefaultTime);
		SmartDashboard.putData("Auto delay", m_delayChooser);

    //Alliance Color Options
    allianceColor.addOption("Default Color", kDefaultColor);
    allianceColor.addOption("Blue Alliance", kBlueAlliance);
    allianceColor.addOption("Red Alliance", kRedAlliance);
    
    //Default Alliance Color
    allianceColor.setDefaultOption("Default Color", kDefaultColor);
    SmartDashboard.putData("Alliance Color", allianceColor);

    //Testing shooter powers
		SmartDashboard.putNumber("Input Power", 0.50);

    //Set limelight modes
    drive.changeLimelightLED(Drive.LIMELIGHT_ON);
  }

  @Override
  public void robotPeriodic() {
    //
  }

  @Override
  public void autonomousInit() {
    //Variables
    autoStatus      = Robot.CONT;
    calibrateStatus = Robot.CONT;

    /**
     * AUTO CHOOSERS
     */
    //Auto Modes
    m_positionSelected = m_pathChooser.getSelected();

    //Auto Delay
    delaySeconds = Integer.parseInt(m_delaySelected);

    //Telemetry
    System.out.println("Delay: "    + delaySeconds);
		System.out.println("Position: " + m_positionSelected);
    
    //LED Color Set
    led.autoMode();
  }

  @Override
  public void autonomousPeriodic() {
    if (autoStatus == Robot.CONT) {
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
    }
  }

  @Override
  public void teleopInit() {
    //Alliance Color Selected
    alliance = allianceColor.getSelected();
    System.out.println("Alliance selected: " + alliance);
  }

  @Override
  public void teleopPeriodic() {

    //Sets the color of the LED's (when we get them)
    led.defaultMode(alliance);

    //Controls the wheels
    wheelControl();

    //Controls the grabber and shooter
    ballControl();

    //Controls the climber arm and motor
    climberControl();
  }

  @Override
  public void disabledInit() {
    //I don't know why you'd put anything in this
  }

  @Override
  public void disabledPeriodic() {
    //I don't know why you'd put anything in this
  }

  @Override
  public void testInit() {
    //Variables
    autoStatus = Robot.CONT;

    //Selects alliance color
    alliance = allianceColor.getSelected();
    System.out.println("Alliance selected: " + alliance);
  }

  @Override
  public void testPeriodic() {

    if (controls.autoKill() == true) {
      autoStatus = Robot.FAIL;
    }

    if (autoStatus == Robot.CONT) {
      autoStatus = auto.calibrateHoodMotor();
    }

    //System.out.println("Limit Switch 1 Value: " + shooter.limitSwitch1Value());
    //System.out.println("Limit Switch 2 Value: " + shooter.limitSwitch2Value());

    /*double tempPower;
    tempPower = SmartDashboard.getNumber("Input Power", 0.5);
    shooter.enableShooter(tempPower);*/
  }

  /**
   * The program to control the wheels in swerve drive
   */
  private void wheelControl() {
    //Drive inputs
    rotatePower = controls.getRotatePower();
    driveX      = controls.getDriveX();
    driveY      = controls.getDriveY();

    //Target Lock
		if ( controls.enableTargetLock() == true ) {
			wheelMode = Drive.WheelMode.TARGET_LOCK;
		}

		//Target Lock Auto Kill
		if ( controls.autoKill() == true ) {
			wheelMode = Drive.WheelMode.MANUAL;
    }

    //Manual Drive
    if (wheelMode == Drive.WheelMode.MANUAL) {
      drive.teleopSwerve(driveX, driveY, rotatePower);

      ledCurrent++;

      if (ledCurrent >= LED_DELAY) {
        ledCurrent = 0;

        led.defaultMode(alliance);
      }
    }
    else if (wheelMode == Drive.WheelMode.TARGET_LOCK) {
      ledCurrent = 0;

      //PID Targeting when in Target Lock Mode
      targetingStatus = drive.limelightPIDTargeting(Drive.TargetPipeline.TEN_FOOT);

      if (targetingStatus == Robot.DONE) {
        wheelMode = Drive.WheelMode.MANUAL;
      }
      else if (targetingStatus == Robot.FAIL) {
        wheelMode = Drive.WheelMode.MANUAL;
      }
    }
  }

  private void ballControl() {
    //Grabber Variables
    boolean grabberDeployRetract;
    Grabber.GrabberDirection grabberDirection;
    
    //Shooter Variables
    Shooter.BallFeederDirection feederDirection;
    Shooter.HoodMotorPosition   hoodPosition;
    boolean hailMary;
    boolean trenchShot;
    boolean shooterEnable;
    boolean shooterReady = shooter.shooterReadyAuto();

    /**
     * Get inputs from the Xbox controller & Joystick
     */
    //Grabber
		grabberDeployRetract          = controls.grabberDeployRetract();
		grabberDirection              = controls.getGrabberDir();
    
    //Shooter
    feederDirection               = controls.ballFeederControl();
    hoodPosition                  = controls.hoodMotorControl();
    hailMary                      = controls.hailMary();
    trenchShot                    = controls.enableTrenchShot();
		shooterEnable                 = controls.enableShooter();
    

    /*****   Grabber Deploy Retract   *****/
		if (grabberDeployRetract == true) {
			grabber.deployRetract();
		}
		
		/******   Grabber motor Forward, Reverse or OFF   *****/
		/******   Allows the grabber to be on when shooter on   *****/
    grabber.grabberDirection(grabberDirection);

		/*****   Shooter Control   *****/
		if (shooterEnable == true) {
			if (hailMary == true) {
        //Prepares the robot to shoot
				shooter.autoShooterControl( Shooter.ShootLocation.HAIL_MARY );
			}
			else if (trenchShot == true) {
        //Prepares the robot to shoot
				shooter.autoShooterControl( Shooter.ShootLocation.TRENCH );
			}
			else {
        //Prepaers the robot to shoot
				shooter.autoShooterControl( Shooter.ShootLocation.TEN_FOOT );
      }
		}
		else {
      //Turns the shooter off
			shooter.manualShooterControl( Shooter.ShootLocation.OFF );
    }

    /*****   Hood Motor Control   *****/
    //Hood motor stuff
    if (shooterEnable == true) {
      shooter.autoHoodControl();
    }
    else {
      shooter.manualHoodMotorControl(hoodPosition);
    }

    /*****   Ball Feeder Control   *****/
    // Can't have grabber & shooter on at same time
    if ((grabberDirection == Grabber.GrabberDirection.OFF) && (shooterEnable == true)) {
      //Waits for the shooter to get up to speed
      if (shooterReady == true) {
        System.out.println("Shooter ready. Fire away!");

        //Shooter at required RPM, turn Feed Motor On
        shooter.autoBallFeederControl();
      }
      else { //AKA shooterReady == false
        System.out.println("Shooter NOT ready!");

        //Shooter below required RPM, turn Feed Motor Off
        shooter.autoBallFeederControl();
      }
    }
    else {
      shooter.manualBallFeederControl(feederDirection);
      shooter.manualHoodMotorControl (hoodPosition);
    }
  }

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

} // End of the Robot Class