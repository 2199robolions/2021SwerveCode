/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.hal.util.UncleanStatusException;
import edu.wpi.first.wpilibj.I2C;

public class Lidar extends TimedRobot {

  public Lidar(){
    lidarInit();
  }

  private I2C m_LIDAR;

  public void lidarInit() {
    /*m_LIDAR = new I2C(0, 0); //plug the lidar into PWM 0
    m_LIDAR.setMaxPeriod(1.00); //set the max period that can be measured
    m_LIDAR.setSemiPeriodMode(true); //Set the counter to period measurement
    m_LIDAR.reset(); */
  }
  final double off  = 10; //offset for sensor. test with tape measure

  public void lidarPeriodic() {
    /*double dist;
    if(m_LIDAR.get() < 1)
      dist = 0;
    else
      dist = (m_LIDAR.getPeriod()*1000000.0/10.0) - off; //convert to distance. sensor is high 10 us for every centimeter. 
    SmartDashboard.putNumber("Distance", dist); //put the distance on the dashboard*/
  }

}
