package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.controller.PIDController;

public class Wheel {

    // Motor Controllers Declaration (instantiated in the constructor in order to dependency inject the IDs of each respective controller)
    private CANSparkMax driveMotor;
    private VictorSP rotateMotor;

    // Rotate Sensor Declaration (instantiated in the constructor in order to dependency inject the ID of the sensor)
    // The sensor is just a 0V to 5V voltage signal that plugs into the analog inputs in the RoboRio, hence the AnalogInput objects.
    private AnalogInput rotateMotorSensor;

    // PID Controller Declaration
    private PIDController pidController = new PIDController(kP, kI, kD);

    // PID Controller Values (static, as these constants will not change for each individual motor)
    // TODO: make sure to replace the 0.0's with actual values
    private static final double kP = 0.0;
    private static final double kI = 0.0;
    private static final double kD = 0.0;

    public Wheel(int driveMotorID, MotorType driveMotorType, int rotateMotorID, int rotateMotorSensorID) {
        // Motor Controllers Instantiation
        this.driveMotor = new CANSparkMax(driveMotorID, driveMotorType);
        this.rotateMotor = new VictorSP(rotateMotorID);

        // Rotate Sensor Instantiation
        this.rotateMotorSensor = new AnalogInput(rotateMotorSensorID);
    }

    public void powerRotateMotor(double power) {
        rotateMotor.set(power);
    }

    public void powerDriveMotor(double power) {
        driveMotor.set(power);
    }

    public double getRotateMotorPosition() {
        return rotateMotorSensor.getVoltage();
    }
    
}