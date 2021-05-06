package frc.robot;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.robot.enums.RobotStatus;

public class Drive {

    // An enum containing each wheel's properties including: drive and rotate motor IDs, drive motor types, and rotate sensor IDs 
    public enum WheelProperties {
        // TODO: All of the below 0's should be replaced with real ID numbers
        //Need offset var
        FRONT_RIGHT_WHEEL(15, // DRIVE MOTOR ID
                          1, // ROTATE MOTOR ID
                          1, // ROTATE SENSOR ID
                          (-1 * rotateMotorAngle), // ROTATE MOTOR TARGET ANGLE (IN RADIANS)
                          248), //Offset
        FRONT_LEFT_WHEEL(12, // DRIVE MOTOR ID
                         2, // ROTATE MOTOR ID
                         2, // ROTATE SENSOR ID
                         (-1 * rotateMotorAngle - (Math.PI/2)), // ROTATE MOTOR TARGET ANGLE (IN RADIANS)
                         306), //Offset
        REAR_RIGHT_WHEEL(14, // DRIVE MOTOR ID
                         4, // ROTATE MOTOR ID
                         0, // ROTATE SENSOR ID
                         (-1 * rotateMotorAngle + (Math.PI/2)), // ROTATE MOTOR TARGET ANGLE (IN RADIANS)
                         115), //Offset
        REAR_LEFT_WHEEL(13, // DRIVE MOTOR ID
                        3, // ROTATE MOTOR ID
                        3, // ROTATE SENSOR ID
                        (-1 * rotateMotorAngle + (Math.PI)), // ROTATE MOTOR TARGET ANGLE (IN RADIANS)
                        259); //Offset

        private int driveMotorId;
        private int rotateMotorId;
        private int rotateSensorId;
        private double targetRadians;
        private double targetVoltage;
        private int offsetDegrees; //Inverse of the reading when wheel is physically at 0 degrees

        // Each item in the enum will now have to be instantiated with a constructor with the all of the ids and the motor type constants. Look few lines above, where FRONT_RIGHT_WHEEL(int driveMotorId, MotorType driveMotorType, int rotateMotorId, int rotateSensorId, double targetRadians, double targetVoltage), REAR_LEFT_WHEEL(int driveMotorId, MotorType driveMotorType, int rotateMotorId, int rotateSensorId, double targetRadians, double targetVoltage), etc... are. These are what the constructor is for.
        private WheelProperties(int driveMotorId, int rotateMotorId, int rotateSensorId, double targetRadians, int offsetDegrees) {
            this.driveMotorId = driveMotorId;
            this.rotateMotorId = rotateMotorId;
            this.rotateSensorId = rotateSensorId;
            this.targetRadians = targetRadians;
            //this.targetVoltage = (((targetRadians * 2.5) / Math.PI) + 2.5);
            this.offsetDegrees = offsetDegrees;
        }

        //Ask Sanghyeok why these are private
        private int getDriveMotorId() {
            return this.driveMotorId;
        }

        private int getRotateMotorId() {
            return this.rotateMotorId;
        }

        private int getRotateSensorId() {
            return this.rotateSensorId;
        }

        private double getTargetRadians() {
            return this.targetRadians;
        }

       private double getTargetVoltage() {
            return this.targetVoltage;
        }

        private int getOffsetDegrees(){
            return this.offsetDegrees;
        }
    }

    // TODO: Should the wheel objects be injected using the constructor when instantiating a drive object in Robot.java? Answer: I don't think so. The goal is to encapsulate, not to make everything accessible.
    private Wheel frontRightWheel = new Wheel(WheelProperties.FRONT_RIGHT_WHEEL.getDriveMotorId(),
                                              WheelProperties.FRONT_RIGHT_WHEEL.getRotateMotorId(), 
                                              WheelProperties.FRONT_RIGHT_WHEEL.getRotateSensorId(),
                                              WheelProperties.FRONT_RIGHT_WHEEL.getOffsetDegrees(),
                                              WheelProperties.FRONT_RIGHT_WHEEL);
    private Wheel frontLeftWheel  = new Wheel(WheelProperties.FRONT_LEFT_WHEEL.getDriveMotorId(), 
                                              WheelProperties.FRONT_LEFT_WHEEL.getRotateMotorId(), 
                                              WheelProperties.FRONT_LEFT_WHEEL.getRotateSensorId(),
                                              WheelProperties.FRONT_LEFT_WHEEL.getOffsetDegrees(),
                                              WheelProperties.FRONT_LEFT_WHEEL);
    private Wheel rearRightWheel  = new Wheel(WheelProperties.REAR_RIGHT_WHEEL.getDriveMotorId(), 
                                              WheelProperties.REAR_RIGHT_WHEEL.getRotateMotorId(), 
                                              WheelProperties.REAR_RIGHT_WHEEL.getRotateSensorId(),
                                              WheelProperties.REAR_RIGHT_WHEEL.getOffsetDegrees(),
                                              WheelProperties.REAR_RIGHT_WHEEL);
    private Wheel rearLeftWheel   = new Wheel(WheelProperties.REAR_LEFT_WHEEL.getDriveMotorId(), 
                                              WheelProperties.REAR_LEFT_WHEEL.getRotateMotorId(), 
                                              WheelProperties.REAR_LEFT_WHEEL.getRotateSensorId(),
                                              WheelProperties.REAR_LEFT_WHEEL.getOffsetDegrees(),
                                              WheelProperties.REAR_LEFT_WHEEL);

    // The literal lengths and widths of the robot. Look to the swerve drive Google Doc
    // Note: these fields are static because they must be. They are referenced in the enum, which is in and of itself, static.
    private static final double robotLength = 0.0; // TODO: make sure to replace the 0.0's with actual values
    private static final double robotWidth = 0.0; // TODO: make sure to replace the 0.0's with actual values

    // TODO: Question for any one of the mentors, are these declarations and instantiations in memory done only once at the start when the robot is started and the code loads? I would assume so, which is why I'm not putting these in the constructor, to save unnecessary compute power if we would ever instantiate more than one of the Drive objects
    // Note: this field is static because it must be. It is referenced in the enum, which is in and of itself, static.
    private static final double rotateMotorAngle = Math.atan2(robotLength, robotWidth);

    public Drive() {
    }

    public void teleopCrabDrive(double wheelAngle, double drivePower){
        /*frontRightWheel.powerDriveMotor(power);
        frontLeftWheel.powerDriveMotor(power);
        rearRightWheel.powerDriveMotor(power);
        rearLeftWheel.powerDriveMotor(power);

        // frontRightWheel.rotateAndDrive(wheelAngle, drivePower);

        /*if(frontRightWheel.getRotateMotorPosition() > rotation + 2){
            
        }*/

        frontRightWheel.rotateAndDrive(wheelAngle, drivePower);
        frontLeftWheel.rotateAndDrive(wheelAngle, drivePower);
        rearRightWheel.rotateAndDrive(wheelAngle, drivePower);
        rearLeftWheel.rotateAndDrive(wheelAngle, drivePower);
    }


    
    public void teleopRotate(double joystickZValue) {
        /**
         * Check at what voltage the rotateSensor is at
         * Calculate what angle that voltage correlates to
         * Power the rotateMotor until the rotateSensor hits a certain value
         * Once the rotateSensor has hit a certain value, power the driveMotor with power of the joystickZValue'
         */

        // TODO: Check whether the signs of the voltages are correct as well.
        if ((frontRightWheel.getRotateMotorPosition() >= WheelProperties.FRONT_RIGHT_WHEEL.getTargetVoltage()) && (frontLeftWheel.getRotateMotorPosition() >= WheelProperties.FRONT_LEFT_WHEEL.getTargetVoltage()) && (rearRightWheel.getRotateMotorPosition() >= WheelProperties.REAR_RIGHT_WHEEL.getTargetVoltage()) && (rearLeftWheel.getRotateMotorPosition() >= WheelProperties.REAR_LEFT_WHEEL.getTargetVoltage())) {
            // TODO: Check whether the power should be multiplied by negative 1, depending on if the motors are reversed. This applies for the other following 3 powerDriveMotor(double power) calls as well.
            frontRightWheel.setDriveMotorPower(joystickZValue);
            frontLeftWheel.setDriveMotorPower(joystickZValue);
            rearRightWheel.setDriveMotorPower(joystickZValue);
            rearLeftWheel.setDriveMotorPower(joystickZValue);
            return;
        }

        if (frontRightWheel.getRotateMotorPosition() < WheelProperties.FRONT_RIGHT_WHEEL.getTargetVoltage()) {
            // TODO: Check whether the power should be multiplied by negative 1, depending on if the motors are reversed. This applies for the other following 3 powerRotateMotor(double power) calls as well.
            // TODO: Check whether the signs of the voltages are correct as well.
            frontRightWheel.setRotateMotorPower(1);
        } else {
            frontRightWheel.setRotateMotorPower(0);
        }
        if (frontLeftWheel.getRotateMotorPosition() < WheelProperties.FRONT_LEFT_WHEEL.getTargetVoltage()) {
            frontLeftWheel.setRotateMotorPower(1);
        } else {
            frontLeftWheel.setRotateMotorPower(0);
        }
        if (rearRightWheel.getRotateMotorPosition() < WheelProperties.REAR_RIGHT_WHEEL.getTargetVoltage()) {
            rearRightWheel.setRotateMotorPower(1);
        } else {
            rearRightWheel.setRotateMotorPower(0);
        }
        if (rearLeftWheel.getRotateMotorPosition() < WheelProperties.REAR_LEFT_WHEEL.getTargetVoltage()) {
            rearLeftWheel.setRotateMotorPower(1);
        } else {
            rearLeftWheel.setRotateMotorPower(0);
        }

    }

    public void testWheel(){
        rearRightWheel.setDriveMotorPower(-0.5);
    }
    public void testRotate(){
        System.out.println("Degrees: " + rearLeftWheel.getRotateMotorPosition());
    }

}
