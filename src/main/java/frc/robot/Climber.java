/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

//import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value; 


public class Climber {

    // SPARK MAX
    private CANSparkMax liftMotor;
    //private CANSparkMax sparkLift_2;

    // SPARK ID's
    private final int LIFT_MOTOR_ID = 100;

    // ENCODERS
    //private CANEncoder lift_Motor_Encoder;

    // Constants
    private int LIFTER_CURRENT_LIMIT = 50;

    // SOLENOID
    private DoubleSolenoid pistonBottom;
    private DoubleSolenoid pistonMiddle;
    private DoubleSolenoid pistonTop;

    // PCM CAN ID's
    private final int PCM_CAN_ID_BOTTOM         = 0;
    private final int PCM_CAN_ID_MIDDLE         = 0;
    private final int PCM_CAN_ID_TOP            = 0;

    //Solenoid ID's
    private final int SOLENOID_RETRACT_BOTTOM   = 0;
    private final int SOLENOID_DEPLOY_BOTTOM    = 0;

    private final int SOLENOID_RETRACT_MIDDLE   = 0;
    private final int SOLENOID_DEPLOY_MIDDLE    = 0;

    private final int SOLENOID_RETRACT_TOP      = 0;
    private final int SOLENOID_DEPLOY_TOP       = 0;

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


    /**
     * CONSTRUCTOR
     */
    public Climber() {
        // SPARKS
        liftMotor  = new CANSparkMax(LIFT_MOTOR_ID, MotorType.kBrushless);

        // ENCODERS
        //lift_Motor_Encoder  = liftMotor.getEncoder();

        // Spark Current Limit
        liftMotor.setSmartCurrentLimit(LIFTER_CURRENT_LIMIT);

        // Set Motors to 0
        liftMotor.set( 0.0 );

        //Configure Bottom Piston
        pistonBottom = new DoubleSolenoid(PCM_CAN_ID_BOTTOM, SOLENOID_DEPLOY_BOTTOM, SOLENOID_RETRACT_BOTTOM);

        //Configure Middle Piston
        pistonMiddle = new DoubleSolenoid(PCM_CAN_ID_MIDDLE, SOLENOID_DEPLOY_MIDDLE, SOLENOID_RETRACT_MIDDLE);

        //Configure Top Piston
        pistonTop    = new DoubleSolenoid  (PCM_CAN_ID_TOP, SOLENOID_DEPLOY_TOP, SOLENOID_RETRACT_TOP);

        //Retract all pistons
        pistonBottom.set(Value.kReverse);
        pistonMiddle.set(Value.kReverse);
        pistonTop.   set(Value.kReverse);
    }

    /**
     * Methods to raise the arms, either individually or as a whole 
     */
    public void climberUp() {
        bottomArmUp();
        middleArmUp();
        topArmUp();
    }

    public void bottomArmUp() {
        pistonBottom.set(Value.kForward);
    }

    public void middleArmUp() {
        pistonMiddle.set(Value.kForward);
    }

    public void topArmUp() {
        pistonTop.set   (Value.kForward);
    }

    /**
     * Methods to lower the arms, either individually or as a whole
     */
    public void climberDown() {
        bottomArmDown();
        middleArmDown();
        topArmDown();
    }

    private void bottomArmDown() {
        pistonBottom.set(Value.kReverse);
    }

    private void middleArmDown() {
        pistonMiddle.set(Value.kReverse);
    }

    public void topArmDown() {
        pistonTop.set   (Value.kReverse);
    }

    /**
     * Uses the lift motor to pull the robot up 
     * @param power
     */
    public void pullRobotUp(double power) {
        liftMotor.set(power);
    }

} // End of Climber Class