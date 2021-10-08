package frc.robot;

import edu.wpi.first.wpiutil.math.MathUtil;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.VictorSP;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.controller.PIDController;

public class Wheel {

    // Motor Controllers Declaration (instantiated in the constructor in order to dependency inject the IDs of each respective controller)
    private CANSparkMax driveMotor;
    private CANEncoder  driveEncoder;
    private VictorSP    rotateMotor;

    private Drive.WheelProperties name;

    // The sensor is just a 0V to 5V voltage signal that plugs into the analog inputs in the RoboRio, hence the AnalogPotentiometer objects.
    private AnalogPotentiometer rotateMotorSensor;
    private PIDController rotationPID;

    // PID Controller Values (static and final so that all wheels share the same values, and they will never change)
    private static final double kP = 0.03;
    private static final double kI = 0.00;
    private static final double kD = 0.00;

    // CONSTANTS
    private static final int WHEEL_CURRENT_LIMIT = 120;



    /****************************************************************************************** 
    *
    *    Wheels constructor
    * 
    ******************************************************************************************/
    public Wheel(int driveMotorID, int rotateMotorID, int rotateMotorSensorID, double offsetDegrees, Drive.WheelProperties motorName) {
        // Motor Controllers Instantiation
        this.driveMotor   = new CANSparkMax(driveMotorID, MotorType.kBrushless);
        this.driveEncoder = driveMotor.getEncoder();
        this.rotateMotor  = new VictorSP(rotateMotorID);
        this.name         = motorName;

        // Adds a current limit and sets the motor mode
        this.driveMotor.setSmartCurrentLimit(WHEEL_CURRENT_LIMIT);
        this.driveMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);

        // Rotate Sensor Instantiation
        rotateMotorSensor = new AnalogPotentiometer(rotateMotorSensorID, -360, offsetDegrees);
        
        //PID Controller
        rotationPID = new PIDController(kP, kI, kD);
        rotationPID.enableContinuousInput(-180, 180);
    }



    /****************************************************************************************** 
    *
    *    rotateAndDrive()
    *    Indivudally rotates each wheel to a set target angle and powers the drive motor 
    * 
    ******************************************************************************************/
    public int rotateAndDrive(double targetWheelAngle, double drivePower) {
        double currWheelAngle;
        double rotatePower;

        currWheelAngle = getRotateMotorPosition();
        rotatePower = rotationPID.calculate(currWheelAngle, targetWheelAngle);

        /**
         * If PID output is positive you want to rotate the wheel clockwise
         * In order to rotate clockwise, a negative power is required
         * And vise-versa.
         */
        setRotateMotorPower(-1 * rotatePower);
        setDriveMotorPower(drivePower);

        //Are we within 2 degrees of target wheel angle? 
        //The return values do not need to be used 
        if (rotatePower < 0.06) {
            return Robot.DONE;
        }
        else {
            return Robot.CONT;
        }
    }




    /****************************************************************************************** 
    *
    *    setRotateMotorPower()
    *    Rotates a certain wheel at a certain power
    * 
    ******************************************************************************************/
    public void setRotateMotorPower(double power) {
        power = MathUtil.clamp(power, -1, 1);
        rotateMotor.set(power);
    }



    /****************************************************************************************** 
    *
    *    setDriveMotorPower()
    *    Powers individual drive motors
    *    Left side gets inversed pwower since it's facing the opposite way
    * 
    ******************************************************************************************/
    public void setDriveMotorPower(double power) {
        power = MathUtil.clamp(power, -1, 1);

        //Wheels always go forward. To go reverse, rotate wheels
        if ((name == Drive.WheelProperties.FRONT_LEFT_WHEEL) || 
            (name == Drive.WheelProperties.REAR_LEFT_WHEEL))    {
            driveMotor.set(power * -1);
        } 
        else {
            driveMotor.set(power);
        }
    }



    /****************************************************************************************** 
    *
    *    normalizeAngle()
    *    Takes an input of degrees and brings it from (0 to 360) to (-180 to 180)
    * 
    ******************************************************************************************/
    private double normalizeAngle(double degrees){
        double adjustedValue = degrees;

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



    /****************************************************************************************** 
    *
    *    getRotateMotorPosition()
    *    Returns rotate motor sensor values in a range from -180 to 180
    * 
    ******************************************************************************************/
    public double getRotateMotorPosition() {
        double rawAngle = rotateMotorSensor.get();
        double adjustedValue = normalizeAngle(rawAngle);
        return adjustedValue;
    }


    /****************************************************************************************** 
    *
    *    getEncoderValue()
    *    Returns the encoder value of the drive motor
    * 
    ******************************************************************************************/
    public double getEncoderValue(){
        double tempValue = driveEncoder.getPosition();

        if ((name.equals(Drive.WheelProperties.FRONT_LEFT_WHEEL)) || 
            (name.equals(Drive.WheelProperties.REAR_LEFT_WHEEL)))    {
            tempValue *= -1;
        } 
        return tempValue;
    }



} // End of the Wheel Class