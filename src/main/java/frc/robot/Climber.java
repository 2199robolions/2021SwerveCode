/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;


public class Climber {

    // SPARK MAX
    private CANSparkMax liftMotor;
    private final int LIFT_MOTOR_ID = 20;

    // SOLENOID
    private DoubleSolenoid pistonBottomMiddle;
    private DoubleSolenoid pistonTop;

    // Constants
    private final int LIFTER_CURRENT_LIMIT      = 50;
    
    //Solenoid ID's
    private final int BOTTOM_MIDDLE_DEPLOY_ID   = 5;
    private final int BOTTOM_MIDDLE_RETRACT_ID  = 6;
    private final int TOP_DEPLOY_ID             = 7;
    private final int TOP_RETRACT_ID            = 0;


    /**
     * Climber State Enumeration
     */
    public static enum ClimberState {
        ALL_ARMS_DOWN,
        START_ARMS_UP,
        ALL_ARMS_UP,
        TOP_ARM_DOWN,
        CLIMB;
    }


    /****************************************************************************************** 
    *
    *    Constructor
    *  
    ******************************************************************************************/
    public Climber() {
        //Initializing lift motor
        liftMotor    = new CANSparkMax(LIFT_MOTOR_ID, MotorType.kBrushless);
        liftMotor.setSmartCurrentLimit(LIFTER_CURRENT_LIMIT);
        liftMotor.set( 0.0 );

        //Configure pistons
        pistonBottomMiddle = new DoubleSolenoid(BOTTOM_MIDDLE_DEPLOY_ID, BOTTOM_MIDDLE_RETRACT_ID);
        pistonTop          = new DoubleSolenoid(TOP_DEPLOY_ID, TOP_RETRACT_ID);

        //Retract all pistons
        climberDown();
    }


    /****************************************************************************************** 
    *
    *    climberUp()
    *    Deploys all 3 climber arms
    *    Not used at this time.  Done in auto.climberDeploy() with 1 sec delay
    *    between bottom/middle and top arm deploy
    * 
    ******************************************************************************************/
    public void climberUp() {
        bottomAndMiddleArmUp();
        topArmUp();
    }

    /****************************************************************************************** 
    *
    *    bottomAndMiddleArmUp()
    *    Deploys bottom 2 parts of climber arm
    * 
    ******************************************************************************************/
    public void bottomAndMiddleArmUp() {
        pistonBottomMiddle.set(Value.kForward);
    }

    /****************************************************************************************** 
    *
    *    topArmUp()
    *    Lifts top part of the climber arm
    * 
    ******************************************************************************************/
    public void topArmUp() {
        pistonTop.set(Value.kForward);
    }


    /****************************************************************************************** 
    *
    *    climberDown()
    *    Retracts all of the climber
    *   
    ******************************************************************************************/
    public void climberDown() {
        pistonBottomMiddle.set(Value.kReverse);
        topArmDown();
    }

    /****************************************************************************************** 
    *
    *    topArmDown()
    *    Retracts the top part of the climber
    *   
    ******************************************************************************************/
    public void topArmDown() {
        pistonTop.set(Value.kReverse);
    }


    /****************************************************************************************** 
    *
    *    pullRobotUp()
    *    Turns on lift motor to pull robot up
    * 
    ******************************************************************************************/
    public void pullRobotUp(double power) {
        liftMotor.set(power);
    }

} // End of Climber Class