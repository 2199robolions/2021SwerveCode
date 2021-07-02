/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANEncoder;

import edu.wpi.first.wpilibj.DigitalInput;

public class Conveyer {

    // Enumerator for Conveyer States
    public static enum ConveyerState {
        OFF,
        FORWARD,
        REVERSE;
    }

    // SPARK MAX
    private CANSparkMax verticalConveyer;
    private CANSparkMax horizontalConveyer;

    // CONSTANTS
    private final int DELAY_HORIZONTAL_TARGET   = 5;
    private final int DELAY_VERTICAL_TARGET     = 2;
    private final double BELT_POWER = .5;

    // SPARK MAX ID's
    private final int VERTICAL_CONVEYER_ID     = 0;
    private final int HORIZONTAL_CONVEYER__ID  = 0;

    // ENCODERS
    private CANEncoder verticalEncoder;
    private CANEncoder horizontalEncoder;

    // CURRENT LIMIT
    private int CONVEYER_CURRENT_LIMIT = 30;

    // SOLENOID
    private DoubleSolenoid forwarding;

    // SOLENOID CHANNELS
    private final int FORWARDING_EXTEND_ID  = 1;
    private final int FORWARDING_RETRACT_ID = 2;
    private final int PCM_CAN_ID            = 0;

    private static int delayHorizontalStop  = 0;
    private static int delayVerticalStop    = 0;

    // Pnuematic State Variables
    private enum ForwardingState {
        DEPLOY,
        RETRACT;
    }
    private ForwardingState forwardingState;

    // Object Creation
    private DigitalInput frontSensor; 
    private DigitalInput backSensor; 

    /**
     * CONSTRUCTOR
     */
    public Conveyer() {
        
        // Sparks
        verticalConveyer    = new CANSparkMax(VERTICAL_CONVEYER_ID  , MotorType.kBrushless);
        horizontalConveyer  = new CANSparkMax(HORIZONTAL_CONVEYER__ID, MotorType.kBrushless);
        verticalConveyer.set(0.0);
        horizontalConveyer.set(0.0);

        // Encoders
        verticalEncoder    = new CANEncoder(verticalConveyer  );
        horizontalEncoder  = new CANEncoder(horizontalConveyer);

        // Spark Current Limit
        verticalConveyer.setSmartCurrentLimit(CONVEYER_CURRENT_LIMIT);
        horizontalConveyer.setSmartCurrentLimit(CONVEYER_CURRENT_LIMIT);

        // Solenoid Initialization at Deploy
        forwarding = new DoubleSolenoid(PCM_CAN_ID, FORWARDING_EXTEND_ID, FORWARDING_RETRACT_ID);
        forwarding.set(Value.kReverse);
        forwardingState = ForwardingState.RETRACT;

        // Instance Creation
        frontSensor = new DigitalInput(0);
        backSensor = new DigitalInput(1);
    }

    /*
     * CONTROL HORIZONTAL CONVEYER
     */
    public void manualHorizontalControl(ConveyerState state) {

        if  (state == ConveyerState.OFF) {
            horizontalConveyer.set(0);
        } else if (state == ConveyerState.FORWARD) {
            horizontalConveyer.set(BELT_POWER);
        } else if (state == ConveyerState.REVERSE) {
            horizontalConveyer.set(BELT_POWER * -1);
        }
    }

    /*
     * CONTROL VERTICAL CONVEYER
     */
    public void manualVerticalControl(ConveyerState state) {

        if  (state == ConveyerState.OFF) {
            verticalConveyer.set(0);
        } else if (state == ConveyerState.FORWARD) {
            verticalConveyer.set(BELT_POWER * -1);
        } else if (state == ConveyerState.REVERSE) {
            verticalConveyer.set(BELT_POWER);
        }
    }

    /**
     * AUTO HORIZONTAL CONVEYER
     */
    public void autoHorizontalControl() {
        /**
         * Digital IO
         * false means that there is a ball
         * true means that there is a no ball
         */ 
        //sensor0Test();

        //detected ball
        if (frontSensor.get() == false) {
            horizontalConveyer.set(BELT_POWER);
            delayHorizontalStop = 1;
        }
        //no ball detected
        else {
            //run horizontal conveyer for short time after ball not detected
            if ( (delayHorizontalStop != 0) && (delayHorizontalStop <= DELAY_HORIZONTAL_TARGET) ) {
                horizontalConveyer.set(BELT_POWER);
                delayHorizontalStop++;
            }
            //turn off horizontal conveyer
            else {
                horizontalConveyer.set(0);
                delayHorizontalStop = 0;
            }
        }
    }

    /**
     * Automatic Vertical Down
     */
    public void autoVerticalDown() {
        verticalConveyer.set(BELT_POWER);
    }

    /**
     * AUTO VERTICAL CONVEYER
     */
    public void autoVerticalControl() {
        /**
         * Digital IO
         * true means that there is a ball
         * flase means that there is a no ball
         */
        //sensor1Test();

        // no ball detected run vertical conveyor up
        if (backSensor.get() == false) {
            verticalConveyer.set(BELT_POWER * -1);
            delayVerticalStop = 1;
        }
        else {
            // ball detected run vertical conveyor down for a short time
            if ( (delayVerticalStop != 0) && (delayVerticalStop <= DELAY_VERTICAL_TARGET) ) {
                verticalConveyer.set(BELT_POWER);
                delayVerticalStop++;
            }
            else {
                verticalConveyer.set(0);
                delayVerticalStop = 0;
            }
        }
    }

    /*
     * EXTEND / RETRACT FORWARDING PISTON
     */
    public void changeForwardingState() {
        
        // Change the State of the Piston
        if (forwardingState == ForwardingState.DEPLOY) {
            forwarding.set(Value.kReverse);
            forwardingState = ForwardingState.RETRACT;
        } else if (forwardingState == ForwardingState.RETRACT) {
            forwarding.set(Value.kForward);
            forwardingState = ForwardingState.DEPLOY;
        }
    }

    public void forwardingDeploy() {
        forwarding.set(Value.kForward);
        forwardingState = ForwardingState.DEPLOY;
    }

    public void forwardingRetract() {
        forwarding.set(Value.kReverse);
        forwardingState = ForwardingState.RETRACT;
    }

    /**
     * DEBUG
     */
    public void sensor0Test() {
        boolean ballDetected = frontSensor.get();
        System.out.println("Ball Not Detected: " + ballDetected);
    }

    public void sensor1Test() {
        boolean ballDetected = backSensor.get();
        System.out.println("Ball Not Detected: " + ballDetected);
    }
/*
    public void verticalConveyerSpeed() {

        double verticalConveyerSpeed = verticalEncoder.getVelocity();

        System.out.println("Vertical Conveyer Speed: " + verticalConveyerSpeed);
    }

    public void horizontalConveyerSpeed() {

        double horizontalConveyerSpeed = horizontalEncoder.getVelocity();

        System.out.println("Horizontal Conveyer Speed: " + horizontalConveyerSpeed);
    }
*/
} // End of the Conveyer Class