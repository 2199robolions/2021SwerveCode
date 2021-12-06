/**
 * Showing Main.java some love <3
 */
package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;

public final class Main {
  private Main() {
    //
  }

  public static void main(String... args) {
    RobotBase.startRobot(Robot::new);
  }
}

// End of Main Class