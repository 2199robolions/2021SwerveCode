package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.VictorSP;

import edu.wpi.first.wpilibj.AnalogPotentiometer;

import edu.wpi.first.wpilibj.controller.PIDController;

import edu.wpi.first.wpiutil.math.MathUtil;

public class Wheel {
    // Variables
    /*private double currWheelAngle;
    private boolean currButtonState = false;
    private boolean oldButtonState  = false;
    private boolean fieldDrive      = false;*/

    //Object Creation
    Drive drive;

    // Motor Controllers Declaration (instantiated in the constructor in order to dependency inject the IDs of each respective controller)
    private CANSparkMax driveMotor;
    private VictorSP rotateMotor;

    private Drive.WheelProperties name;

    // Rotate Sensor Declaration (instantiated in the constructor in order to dependency inject the ID of the sensor)
    // The sensor is just a 0V to 5V voltage signal that plugs into the analog inputs in the RoboRio, hence the AnalogInput objects.

    //private AnalogInput rotateMotorSensor;
    //private AnalogInput rotateMotorSensor;
    private AnalogPotentiometer rotateMotorSensor;
    private PIDController rotationPID;

    // PID Controller Values (static, as these constants will not change for each individual motor)
    // TODO: make sure to replace the 0.0's with actual values
    private static final double kP = 0.03;
    private static final double kI = 0.00;
    private static final double kD = 0.00;

    public Wheel(int driveMotorID, int rotateMotorID, int rotateMotorSensorID, int offsetDegrees, Drive.WheelProperties motorName) {
        // Motor Controllers Instantiation
        this.driveMotor = new CANSparkMax(driveMotorID, MotorType.kBrushless);
        this.rotateMotor = new VictorSP(rotateMotorID);
        this.name = motorName;

        // Rotate Sensor Instantiation
        System.out.println("analog id:" + rotateMotorSensorID + " wheel: " + driveMotorID);
        //this.rotateMotorSensor = new AnalogInput(rotateMotorSensorID);
        //Sensor measures from above going Counter clockwise
        rotateMotorSensor = new AnalogPotentiometer(rotateMotorSensorID, -360, offsetDegrees);
        
        //PID Controller
        rotationPID = new PIDController(kP, kI, kD);
        rotationPID.enableContinuousInput(-180, 180);
        
        //Instance Creation
    }

    /**
     * This is the Crab Drive program that the robot currently opperates with.
     * @param targetWheelAngle
     * @param drivePower
     */
    public void rotateAndDrive(double targetWheelAngle, double drivePower) {
        double currWheelAngle;
        double rotatePower;

        // I'm thinking the P for the PID may be around .01
        // if error greater than 100 output clamped at 1.00
        // error    output
        // >=100    1.00
        // 90       .9
        // 45       .45
        // 20       .2
        // 10       .1
        // 5        .05

        /*if (fieldDrive() == true) {
            //In field drive, the wheels' angles will be the robot's direction + the wheels' directions
            currWheelAngle = adjustValue(getRotateMotorPosition() + drive.getYaw());
        }
        else {
            //This occurs whenever Field Drive is not enabled, and for any other strange cases
            currWheelAngle = getRotateMotorPosition();
        }*/
        currWheelAngle = getRotateMotorPosition();

        /**
         * If PID is 0 to 360
         * Current = 0, Target = 90, Final Power = 0.18
         * Current = 0, Target = 270, Final Power = 0.54
         * Current = 0, Target = -90, Final Power = 0.54
         * 
         * If PID is -180 to 180
         * Current = 0, Target = 90, Final Power = 0.18
         * Current = 0, Target = -90, Final Power = -0.18
         */
        rotatePower = rotationPID.calculate(currWheelAngle, targetWheelAngle);
        
        //System.out.println("Pwr " + rotatePower + " curr " + (int)currWheelAngle + " tgt " + (int)targetWheelAngle);

        rotatePower = MathUtil.clamp(rotatePower, -1, 1);

        /**
         * If PID output is positive you want to rotate the wheel clockwise
         * In order to rotate clockwise, a negative power is required
         * And vise-versa.
         */
        setRotateMotorPower(-1 * rotatePower);


        drivePower = MathUtil.clamp(drivePower, -1, 1);
        setDriveMotorPower(drivePower);

        //System.out.println(" Cur " + currWheelAngle + " Tgt " + targetWheelAngle);
    }

    /**
     * If power is negative then the wheels rotate clockwise.
     * If power is positive then the wheels rotate counter-clockwise.
     * @param power
     */
    public void setRotateMotorPower(double power) {
        rotateMotor.set(power);
    }

    /**
     * Sets the power of the drive motors.
     * @param power
     */
    public void setDriveMotorPower(double power) {
        //Wheels always go forward. To go reverse, rotate wheels
        if ((name == Drive.WheelProperties.FRONT_LEFT_WHEEL) || 
            (name == Drive.WheelProperties.REAR_LEFT_WHEEL))    {
            driveMotor.set(power * -1);
        } 
        else {
            driveMotor.set(power);
        }
    }

    //Brings angle values from -180 to 180
    public double adjustValue(double input){
        double adjustedValue = input;

        if ((adjustedValue >= 0) && (adjustedValue <= 180)) {
            //Does nothing to adjustedValue
        }
        else if ((adjustedValue <= 0) && (adjustedValue >= -180)) {
            //Does nothing to adjustedValue
        }
        else if(adjustedValue > 180) {
            //Makes all values greater than 180 less than it
            adjustedValue -= 360;
        }
        else if(adjustedValue < -180) {
            //Makes all values less than -180 greater than it
            adjustedValue += 360;
        }
        
        return adjustedValue;
    }

    /*
    private boolean fieldDrive() {
        oldButtonState  = currButtonState;
        currButtonState = controls.toggleFieldDrive();

        //If the button was just pressed
        if((currButtonState == true) && (oldButtonState == false)) {
            fieldDrive =! fieldDrive; //Switch the fieldDrive value
        }

        System.out.println("Field Drive toggled to: " + fieldDrive);

        return fieldDrive;
    }*/

    /**
     * Makes the returned value of the sensors -180 to 180 degrees.
     * @return degrees
     */

    //To-do: use adjustValue function above in this function
    public double getRotateMotorPosition() {
        double adjustedValue = rotateMotorSensor.get();
        
        if ((adjustedValue >= 0) && (adjustedValue <= 180)) {
            //Does nothing to adjustedValue
        }
        else if ((adjustedValue <= 0) && (adjustedValue >= -180)) {
            //Does nothing to adjustedValue
        }
        else if(adjustedValue > 180) {
            //Makes all values greater than 180 less than it
            adjustedValue -= 360;
        }
        else if(adjustedValue < -180) {
            //Makes all values less than -180 greater than it
            adjustedValue += 360;
        }
        
        return adjustedValue;
    }

} // End of the Wheel Class