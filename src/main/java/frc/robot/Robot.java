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

  /**
   * SMART DASHBOARD CHOICES
   */
  //Auto Modes
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto  = "My Auto";
  private              String m_autoSelected;
  private final SendableChooser<String> m_pathChooser = new SendableChooser<>();

  //Alliance Color
  private static final String kDefaultColor = "Default";
  private static final String kRedAlliance  = "Red";
  private static final String kBlueAlliance = "Blue";
  private              String m_colorSelected;
  private final SendableChooser<String> m_colorChooser = new SendableChooser<>();

  //VARIABLES
  private double rotatePower;
  private double driveX;
  private double driveY;

  //OBJECT CREATION
  private LedLights led;
  private Drive     drive;
  private Controls  controls;
  private Grabber   grabber;
  private Shooter   shooter;

  public Robot() {
    //Instance Creation
    led      = new LedLights();
    drive    = new Drive();
    controls = new Controls();
    grabber  = new Grabber();
    shooter  = new Shooter();

    //Set Variables
  }

  @Override
  public void robotInit() {
    /**
     * SMART DASHBOARD CHOICES
     */
    //Auto Modes
    m_pathChooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_pathChooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_pathChooser);

    //Alliance Color
    m_colorChooser.setDefaultOption("Default Color", kDefaultColor);
    m_colorChooser.addOption("Blue Alliance", kBlueAlliance);
    m_colorChooser.addOption("Red Alliance", kRedAlliance);
    SmartDashboard.putData("Auto choices", m_colorChooser);
  }

  @Override
  public void robotPeriodic() {
    //
  }

  @Override
  public void autonomousInit() {
    /**
     * AUTO CHOOSERS
     */
    //Auto Modes
    m_autoSelected = m_pathChooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    //LED Color Set
    led.autoMode();
  }

  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  @Override
  public void teleopInit() {
    //Alliance Color Selected
    m_colorSelected = m_colorChooser.getSelected();
    System.out.println("Alliance selected: " + m_colorSelected);
  }

  @Override
  public void teleopPeriodic() {

    //Controls the wheels
    wheelControl();

    //Sets the color of the LED's (when we get them)
    led.defaultMode(m_colorSelected);
    //drive.teleopRotate(rotatePower);
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {
    m_colorSelected = m_colorChooser.getSelected();
    System.out.println("Alliance selected: " + m_colorSelected);
  }

  @Override
  public void testPeriodic() {
    //drive.testPID();
    //drive.testRotate();
    //System.out.println("Power: " + controls.getDrivePower() + " Angle: " + controls.getDriveAngle());
    ballControl();
  }

  /**
   * The program to control the wheels in swerve drive
   */
  public void wheelControl() {
    rotatePower = controls.getRotatePower();
    driveX      = controls.getDriveX();
    driveY      = controls.getDriveY();
    
    drive.teleopSwerve(driveX, driveY, rotatePower);
  }

  public void ballControl() {
    //Variables
    boolean grabberDeployRetract;
    Grabber.GrabberDirection grabberDirection;
		boolean hailMary;
		boolean shooterEnable;
    boolean trenchShot;

    // Get setting from Xbox & Joystick controllers
		grabberDeployRetract          = controls.grabberDeployRetract();
		grabberDirection              = controls.getGrabberDir();
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
				shooter.autoShooterControl( Shooter.ShootLocation.HAIL_MARY );//manual
			}
			else if (trenchShot == true) {
				shooter.autoShooterControl( Shooter.ShootLocation.TRENCH );//manual
			}
			else {
				shooter.autoShooterControl(Shooter.ShootLocation.TEN_FOOT);//auto uses pid
      }
		}
		else {
			shooter.manualShooterControl( Shooter.ShootLocation.OFF );
    }
  }

} // End of the Robot Class