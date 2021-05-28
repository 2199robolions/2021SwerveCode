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
   * AUTO SELECTORS
   */
  //Auto Modes
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //VARIABLES
  private double rotatePower;
  private double driveX;
  private double driveY;

  //OBJECT CREATION
  private Drive    drive    = new Drive();
  private Controls controls = new Controls();


  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
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
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
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
    //
  }

  @Override
  public void teleopPeriodic() {
    rotatePower = controls.getRotatePower();
    driveX      = controls.getDriveX();
    driveY      = controls.getDriveY();
    drive.teleopSwerve(driveX, driveY, rotatePower);

    //wheelControl();
    //drive.teleopRotate(rotatePower);

  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
    //drive.testPID();
    //drive.testRotate();
    //System.out.println("Power: " + controls.getDrivePower() + " Angle: " + controls.getDriveAngle());   
  }


  public void wheelControl() {
    drive.teleopCrabDrive(0, 0);
  }

  public void crabDrive() {
    double wheelAngle = controls.getDriveAngle();
    double drivePower = controls.getDrivePower();
    
    drive.teleopCrabDrive(wheelAngle, drivePower);
  }
}
