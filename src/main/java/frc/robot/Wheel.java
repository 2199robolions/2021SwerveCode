package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.controller.PIDController;

public class Wheel {

    // Motor Controllers Declaration (instantiated in the constructor in order to dependency inject the IDs of each respective controller)
    private CANSparkMax driveMotor;
    private VictorSP rotateMotor;

    private Drive.WheelProperties name;

    // Rotate Sensor Declaration (instantiated in the constructor in order to dependency inject the ID of the sensor)
    // The sensor is just a 0V to 5V voltage signal that plugs into the analog inputs in the RoboRio, hence the AnalogInput objects.

  //  private AnalogInput rotateMotorSensor;
    //private AnalogInput rotateMotorSensor;
    private AnalogPotentiometer rotateMotorSensor;

    //PID Controller Declaration
    //private PIDController pidController = new PIDController(kP, kI, kD);

    // PID Controller Values (static, as these constants will not change for each individual motor)
    // TODO: make sure to replace the 0.0's with actual values
    private static final double kP = 0.0;
    private static final double kI = 0.0;
    private static final double kD = 0.0;

    public Wheel(int driveMotorID, int rotateMotorID, int rotateMotorSensorID, int offsetDegrees, Drive.WheelProperties motorName) {
        // Motor Controllers Instantiation
        this.driveMotor = new CANSparkMax(driveMotorID, MotorType.kBrushless);
        this.rotateMotor = new VictorSP(rotateMotorID);
        this.name = motorName;

        // Rotate Sensor Instantiation
        System.out.println("analog id:" + rotateMotorSensorID + " wheel: " + driveMotorID);
        //this.rotateMotorSensor = new AnalogInput(rotateMotorSensorID);
        //Sensor measures from above going Counter clockwise
        rotateMotorSensor = new AnalogPotentiometer(rotateMotorSensorID, -360, offsetDegrees);    }


/*  public void rotateAndDrive(double targetWheelAngle, double drivePower)
    {
        setDriveMotorPower(drivePower);

        // I'm thinking the P for the PID may be around .01
        // if error greater than 100 output clamped at 1.00
        // error    output
        // >=100    1.00
        // 90       .9
        // 45       .45
        // 20       .2
        // 10       .1
        // 5        .05
        currWheelAngle = getRotateMotorPosition();
        rotatePower = rotatePid.calculate(currWheelAngle, targetWheelAngle);
        setRotateMotorPower = rotatePower;
    }
*/
    public void powerRotateMotor(double power) {
        rotateMotor.set(power);
    }

    public void powerDriveMotor(double power) {
        //Wheels always go forward. To go reverse, rotate wheels
        if ((name == Drive.WheelProperties.FRONT_LEFT_WHEEL) || 
            (name == Drive.WheelProperties.REAR_LEFT_WHEEL))    {
            driveMotor.set(power * -1);
        } 
        else {
            driveMotor.set(power);
        }
    }

    public double getRotateMotorPosition() {
        double adjustedValue = rotateMotorSensor.get();
        if(adjustedValue > 360){
            adjustedValue -= 360;
        }
        if(adjustedValue < 0){
            adjustedValue += 360;
        }
        return adjustedValue;
    }
}
