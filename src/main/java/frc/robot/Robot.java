package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private Drive drive = new Drive();
  private Controls controls = new Controls();


  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {
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
    // TODO: Test the teleop rotate. Teleop rotate will most likely be packaged in a single teleop control method, so this is just to package it in one place.
    //drive.teleopRotate(controls.getJoystickZ());
  }

  @Override
  public void teleopPeriodic() {
    wheelControl();
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
    drive.testPID();
    //drive.testRotate();
    //System.out.println("Power: " + controls.getDrivePower() + " Angle: " + controls.getDriveAngle());   
  }


  public void wheelControl(){
    double drivePower = controls.getDrivePower();
    double wheelAngle = controls.getDriveAngle();
    drive.teleopCrabDrive(wheelAngle, drivePower);
  }
}
