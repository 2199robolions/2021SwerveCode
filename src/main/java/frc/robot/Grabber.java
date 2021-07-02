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

/**
 * Add your docs here.
 */
public class Grabber {

    //SPARK MAX ID's
    private static final int SPARK_ID  = 18;

    private int GRABBER_CURRENT_LIMIT = 60;

    // Enumerator for Grabber States
    public static enum GrabberState {
        DEPLOY,
        RETRACT;
    }
    private GrabberState grabberState;

    private final double GRABBER_POWER = 0.8;  //0.7
//    private final double GRABBER_POWER = 1.0;

    // Enumerater for Grabber State
    public static enum GrabberDirection {
        FORWARD,
        REVERSE,
        OFF;
    }

    // SOLENOID CHANNELS
    private DoubleSolenoid grabberPiston;

    private final int DEPLOY_ID     = 2;
    private final int RETRACT_ID    = 4;
    private final int PCM_CAN_ID    = 0;

    private CANSparkMax grabberMotor; 

    /**
     * CONSTRUCTOR
     */
    public Grabber()  {
        //Grabber Motor Init
        grabberMotor = new CANSparkMax(SPARK_ID, MotorType.kBrushless);
        grabberMotor.setSmartCurrentLimit(GRABBER_CURRENT_LIMIT);
        grabberMotor.set(0.0);

        //Grabber Position Init
        grabberPiston = new DoubleSolenoid(PCM_CAN_ID, DEPLOY_ID, RETRACT_ID);
        grabberPiston.set(Value.kReverse);
        grabberState = GrabberState.RETRACT;
    }


    /*
     * EXTEND / RETRACT FORWARDING PISTON
     */
    public void deployRetract() {
        // Change the State of the Piston
        if (grabberState == GrabberState.DEPLOY) {
            grabberPiston.set(Value.kReverse);
            grabberState = GrabberState.RETRACT;
        }
        else if (grabberState == GrabberState.RETRACT) {
            grabberPiston.set(Value.kForward);
            grabberState = GrabberState.DEPLOY;
        }

        //System.out.println("Forwarding State: " + grabberState);
    }

    public void grabberDirection(GrabberDirection dir) {
        //Don't allow grabber to turn if it's retracted
        if(grabberState == GrabberState.RETRACT)  {
            grabberMotor.set(0.0);
            return;
        }
        
        // Grabber Intake (Negative Power Brings Ball In)
        if (dir == GrabberDirection.FORWARD) {
            grabberMotor.set(GRABBER_POWER);
        }
        // Grabber Reverse
        else if (dir == GrabberDirection.REVERSE) {
            grabberMotor.set(GRABBER_POWER * -1);
        }
        else {
            grabberMotor.set(0.0);
        }    
    }

} // End of the Grabber Class