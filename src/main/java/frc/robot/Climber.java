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
    private CANSparkMax sparkLift_1;
    //private CANSparkMax sparkLift_2;

    // SPARK ID's
    private final int LIFT_MOTOR_1_ID = 0;

    // ENCODERS
    // private CANEncoder encoderLift_1;

    // Constants
    private int LIFTER_CURRENT_LIMIT = 50;

    // SOLENOID
    private DoubleSolenoid pistonBottom;
    private DoubleSolenoid pistonMiddle;
    private DoubleSolenoid pistonTop;

    // Pneumatic ID's
    private final int SOLENOID_RETRACT_BOTTOM   = 0;
    private final int SOLENOID_DEPLOY_BOTTOM    = 7;

    private final int SOLENOID_RETRACT_MIDDLE   = 0;
    private final int SOLENOID_DEPLOY_MIDDLE    = 1;

    private final int SOLENOID_RETRACT_TOP      = 4;
    private final int SOLENOID_DEPLOY_TOP       = 5;

    private final int PCM_CAN_ID_BOTTOM         = 0;
    private final int PCM_CAN_ID_MIDDLE_TOP     = 20;

    //Enums
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
        sparkLift_1  = new CANSparkMax(LIFT_MOTOR_1_ID, MotorType.kBrushless);

        // ENCODERS
        // encoderLift_1  = new CANEncoder(sparkLift_1);

        // Spark Current Limit
        sparkLift_1.setSmartCurrentLimit(LIFTER_CURRENT_LIMIT);

        // Set Motors to 0
        sparkLift_1.set( 0.0 );

        //Configure Bottom Piston
        pistonBottom = new DoubleSolenoid(PCM_CAN_ID_BOTTOM, SOLENOID_DEPLOY_BOTTOM, SOLENOID_RETRACT_BOTTOM);
        pistonBottom.set(Value.kForward);

        //Configure Middle Piston
        pistonMiddle = new DoubleSolenoid(PCM_CAN_ID_MIDDLE_TOP, SOLENOID_DEPLOY_MIDDLE, SOLENOID_RETRACT_MIDDLE);
        pistonMiddle.set(Value.kForward);

        //Configure Top Piston
        pistonTop = new DoubleSolenoid(PCM_CAN_ID_MIDDLE_TOP, SOLENOID_DEPLOY_TOP, SOLENOID_RETRACT_TOP);
        pistonTop.set(Value.kForward);
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
        pistonBottom.set(Value.kReverse);
    }

    public void middleArmUp() {
        pistonMiddle.set(Value.kReverse);
    }

    public void topArmUp() {
        pistonTop.set(Value.kReverse);
    }

    /**
     * Methods to lower the arms, either individually or as a whole
     */
    public void climberDown() {
        bottomArmDown();
        middleArmDown();
        topArmDown();
    }

    public void bottomArmDown() {
        pistonBottom.set(Value.kForward);
    }

    public void middleArmDown() {
        pistonMiddle.set(Value.kForward);
    }

    public void topArmDown() {
        pistonTop.set(Value.kForward);
    }

    /**
     * Uses the lift motor to pull the robot up 
     * @param power
     */
    public void pullRobotUp(double power) {
        sparkLift_1.set(power);
    }

} // End of Climber Class