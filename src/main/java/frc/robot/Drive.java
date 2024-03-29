package frc.robot;

import edu.wpi.first.wpiutil.math.MathUtil;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.networktables.*;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.SPI;

public class Drive {
    //Object creation
    NetworkTable limelightEntries = NetworkTableInstance.getDefault().getTable("limelight");

    //NAVX
    private static AHRS ahrs;

    //PID controllers
    private PIDController rotateController;
    private PIDController autoCrabDriveController;
    private PIDController targetController;

    private static final double rotateToleranceDegrees = 2.0f;
    private static final double kLimeLightToleranceDegrees = 1.0f;
    
    // Turn Controller
	private static final double kP = 0.01; //0.02
	private static final double kI = 0.00;
    private static final double kD = 0.00;
    
    //Auto crab drive controller
    private static final double acdP = 0.02; //0.03
    private static final double acdI = 0;
    private static final double acdD = 0;

	//Target Controller
	private static final double tP = 0.01; //0.033
	private static final double tI = 0.00;
    private static final double tD = 0.00;


	//Variables
    private boolean firstTime               = true;
    private boolean rotateFirstTime         = true;
    private int     count                   = 0;
    private double  encoderTarget           = 0;
    private double  targetOrientation       = 0;
    
    //CONSTANTS
    private final int    FAIL_DELAY   = 5;
    private final double ticksPerFoot = 6; //5.75


	//Limelight Variables
    private int     noTargetCount      = 0;
    private int     targetLockedCount  = 0;
    private long    timeOut;
    private boolean limeLightFirstTime = true;
	private static final int ON_TARGET_COUNT = 5;
    private static final int ON_ANGLE_COUNT  = 10;

    //Limelight
	public              boolean limeControl   = false;
	public              int     limeStatus    = 0;
	public static final int     LIMELIGHT_ON  = 3;
    public static final int     LIMELIGHT_OFF = 1;

        
    /**
     * Enumerators
     */
    /**
     * The enumerator for locking the drive wheels for targeting
     */
    public static enum WheelMode {
		MANUAL,
        TRACKING,
        LOCKED;
    }
    
    /**
     * The enumerator for choosing a target location
     */
    public static enum TargetPipeline {
		TEN_FOOT,
        TRENCH,
        HAIL_MARY;
	}

    // An enum containing each wheel's properties including: drive and rotate motor IDs, drive motor types, and rotate sensor IDs 
    public enum WheelProperties {
        FRONT_RIGHT_WHEEL(15, // DRIVE MOTOR ID
                          1, // ROTATE MOTOR ID
                          1, // ROTATE SENSOR ID
                          (-1 * rotateMotorAngleRad), // ROTATE MOTOR TARGET ANGLE (IN RADIANS)
                          249.65), //Offset
        FRONT_LEFT_WHEEL(12, // DRIVE MOTOR ID
                         2, // ROTATE MOTOR ID
                         2, // ROTATE SENSOR ID
                         (-1 * rotateMotorAngleRad - (Math.PI/2)), // ROTATE MOTOR TARGET ANGLE (IN RADIANS)
                         306.75), //Offset
        REAR_RIGHT_WHEEL(14, // DRIVE MOTOR ID
                         4, // ROTATE MOTOR ID
                         0, // ROTATE SENSOR ID
                         (-1 * rotateMotorAngleRad + (Math.PI/2)), // ROTATE MOTOR TARGET ANGLE (IN RADIANS)
                         114.6), //Offset
        REAR_LEFT_WHEEL(13, // DRIVE MOTOR ID
                        3, // ROTATE MOTOR ID
                        3, // ROTATE SENSOR ID
                        (-1 * rotateMotorAngleRad + (Math.PI)), // ROTATE MOTOR TARGET ANGLE (IN RADIANS)
                        257.9); //Offset

        private int driveMotorId;
        private int rotateMotorId;
        private int rotateSensorId;
        private double offsetDegrees; //Inverse of the reading when wheel is physically at 0 degrees

        // Each item in the enum will now have to be instantiated with a constructor with the all of the ids and the motor type constants. Look few lines above, where FRONT_RIGHT_WHEEL(int driveMotorId, MotorType driveMotorType, int rotateMotorId, int rotateSensorId, double targetRadians, double targetVoltage), REAR_LEFT_WHEEL(int driveMotorId, MotorType driveMotorType, int rotateMotorId, int rotateSensorId, double targetRadians, double targetVoltage), etc... are. These are what the constructor is for.
        private WheelProperties(int driveMotorId, int rotateMotorId, int rotateSensorId, double targetRadians, double offsetDegrees) {
            this.driveMotorId = driveMotorId;
            this.rotateMotorId = rotateMotorId;
            this.rotateSensorId = rotateSensorId;
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


        private double getOffsetDegrees(){
            return this.offsetDegrees;
        }
    }

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
    
    /**
     * The literal lengths and widths of the robot. Look to the swerve drive Google Doc
     * Note: these fields are static because they must be. They are referenced in the enum, which is in and of itself, static.
     * These measurements are in inches
     */
    private static final double robotLength = 30.0;
    private static final double robotWidth  = 18.0;
    private static final double rotateMotorAngleRad = Math.atan2(robotLength, robotWidth);
    private static final double rotateMotorAngleDeg = Math.toDegrees(rotateMotorAngleRad);
 
    // These numbers were selected to make the angles between -180 and +180
    private static final double rotateRightFrontMotorAngle = -1 * rotateMotorAngleDeg; //-1 * rotateMotorAngleDeg;
    private static final double rotateLeftFrontMotorAngle = -180 + rotateMotorAngleDeg; //rotateRightFrontMotorAngle - 90;
    private static final double rotateRightRearMotorAngle = rotateMotorAngleDeg; //rotateRightFrontMotorAngle + 90;
    private static final double rotateLeftRearMotorAngle =  180 -rotateMotorAngleDeg;       //rotateRightFrontMotorAngle + 180;


    /****************************************************************************************** 
    *
    *    PowerAndAngle class
    *    Stores angle and power instead of x and y values
    * 
    ******************************************************************************************/
    public class PowerAndAngle{
        public double power;
        public double angle;

        /****************************************************************************************** 
        *
        *    PowerAndAngle constructor
        * 
        ******************************************************************************************/
        public PowerAndAngle(double powerParam, double angleParam){
            this.power = powerParam;
            this.angle = angleParam;
        }

        //Getters for power and angle
        public double getPower()  {
            return power;
        }

        public double getAngle()  {
            return angle;
        }
    }

    
    /****************************************************************************************** 
    *
    *    Drive constructor
    * 
    ******************************************************************************************/
    public Drive() {

        //NavX
        try {
            ahrs = new AHRS(SPI.Port.kMXP);
        } catch (RuntimeException ex) {
            System.out.println("Error Instantiating navX MXP: " + ex.getMessage());
        }
    
        ahrs.reset();
    
        while (ahrs.isConnected() == false) {
            System.out.println("Connecting navX");
        }
        System.out.println("navX Connected");
    
        while (ahrs.isCalibrating() == true) {
            System.out.println("Calibrating navX");
        }
        System.out.println("navx Ready");
    
        ahrs.zeroYaw();

        //PID Controllers
        rotateController = new PIDController(kP, kI, kD);
        rotateController.setTolerance(rotateToleranceDegrees);
        rotateController.enableContinuousInput(-180.0, 180.0);

        autoCrabDriveController = new PIDController(acdP, acdI, acdD);
        autoCrabDriveController.enableContinuousInput(-180.0, 180.0);

        targetController = new PIDController(tP, tI, tD);
        targetController.setTolerance(kLimeLightToleranceDegrees);

        
        /**
		 * Limelight Modes
		 */
		//Force the LED's to off to start the match
		limelightEntries.getEntry("ledMode").setNumber(1);
		//Set limelight mode to vision processor
		limelightEntries.getEntry("camMode").setNumber(0);
		//Sets limelight streaming mode to Standard (The primary camera and the secondary camera are displayed side by side)
		limelightEntries.getEntry("stream").setNumber(0);
		//Sets limelight pipeline to 0 (light off)
		limelightEntries.getEntry("pipeline").setNumber(0);
    }



    /****************************************************************************************** 
    *
    *    calcSwerve()
    *    For each wheel, the inputted X, Y, Z, and individual angle for rotation are used to calculate the angle and power 
    * 
    ******************************************************************************************/
    private PowerAndAngle calcSwerve(double crabX, double crabY, double rotatePower, double rotateAngle, boolean fieldDriveEnabled){
        double swerveX;
        double swerveY;
        double swervePower;
        double swerveAngle;
        double rotateX;
        double rotateY;

        //If field drive is active then the crab drive values are shifted based on gyro reading
        if (fieldDriveEnabled) {
            double crabPower = Math.sqrt((crabX * crabX) + (crabY * crabY));
            double crabAngle = Math.toDegrees(Math.atan2(crabX, crabY));
            crabAngle -= ahrs.getYaw();

            crabX = Math.sin(Math.toRadians(crabAngle)) * crabPower;
            crabY = Math.cos(Math.toRadians(crabAngle)) * crabPower;
        }
       
        /**
         * The incomming rotate angle will cause the robot to rotate counter-clockwise
         * the incomming power is negative for a counter-clockwise rotation and vise versa for clockwise
         * therefore, we want power to be positive to achieve a counter-clockwise rotation
         * which means that we have to multiply the power by negative 1  
         */ 
        rotateX = (-1 * rotatePower) * Math.sin(Math.toRadians(rotateAngle));
        rotateY = (-1 * rotatePower) * Math.cos(Math.toRadians(rotateAngle));

        swerveX = crabX + rotateX;
        swerveY = crabY + rotateY;

        swervePower = Math.sqrt((swerveX * swerveX) + (swerveY * swerveY));
        swerveAngle = Math.toDegrees(Math.atan2(swerveX, swerveY));

        //If we are rotating CCW, and we are not crab driving, then the robot will flip the wheel angles and powers
        //This keeps the wheels in the same position when turning both ways, making small rotations easier
        if ((rotatePower < 0) && (crabX == 0 && crabY == 0)) {
            swervePower *= -1;
            swerveAngle += 180;
        }

        PowerAndAngle swerveNums = new PowerAndAngle(swervePower, swerveAngle);

        return swerveNums;
    }



    /****************************************************************************************** 
    *
    *    teleopSwerve()
    *    Takes X, Y, and Z and rotates each wheel to proper angle and sets correct power
    * 
    ******************************************************************************************/
    public void teleopSwerve(double driveX, double driveY, double rotatePower, boolean fieldDriveEnabled) {
        PowerAndAngle coor;

        coor = calcSwerve(driveX, driveY, rotatePower, rotateRightFrontMotorAngle, fieldDriveEnabled);
        frontRightWheel.rotateAndDrive(coor.getAngle(), coor.getPower());

        coor = calcSwerve(driveX, driveY, rotatePower, rotateLeftFrontMotorAngle, fieldDriveEnabled);
        frontLeftWheel.rotateAndDrive(coor.getAngle(), coor.getPower());

        coor = calcSwerve(driveX, driveY, rotatePower, rotateRightRearMotorAngle, fieldDriveEnabled);
        rearRightWheel.rotateAndDrive(coor.getAngle(), coor.getPower());

        coor = calcSwerve(driveX, driveY, rotatePower, rotateLeftRearMotorAngle, fieldDriveEnabled);
        rearLeftWheel.rotateAndDrive(coor.getAngle(), coor.getPower());
    }



    /****************************************************************************************** 
    *
    *    teleopCrabDrive()
    *    Only uses X and Y to crab drive the robot
    * 
    ******************************************************************************************/
    public void teleopCrabDrive(double wheelAngle, double drivePower){
        frontLeftWheel.rotateAndDrive(wheelAngle, drivePower);
        frontRightWheel.rotateAndDrive(wheelAngle, drivePower);
        rearLeftWheel.rotateAndDrive(wheelAngle, drivePower);
        rearRightWheel.rotateAndDrive(wheelAngle, drivePower);
    }


   
    /****************************************************************************************** 
    *
    *    autoCrabDrive()
    *    Drives robot for certain distance at a given heading and speed
    *    Generic function for autoCrabDrive with default power of 0.6
    *    @param distanceInFeet
    *    @param targetHeading
    *    @return Robot Status
    * 
    ******************************************************************************************/
    public int autoCrabDrive(double distance, double targetHeading) { 
        return autoCrabDrive(distance, targetHeading, 0.6);
    }


    /****************************************************************************************** 
    *
    *    autoCrabDrive()
    *    Drives robot for certain distance at a given heading and speed
    *    Distance has to be positive
    *    Initial orientation of robot is maintained throughout function
    *    @param distanceInFeet
    *    @param targetHeading
    *    @param power
    *    @return Robot Status
    * 
    ******************************************************************************************/
    public int autoCrabDrive(double distance, double targetHeading, double power) {

        double encoderCurrent = getAverageEncoder(); //Average of 4 wheels

        //First time through initializes target values
        if (firstTime == true) {
            firstTime = false;
            targetOrientation = ahrs.getYaw();
            encoderTarget = encoderCurrent + (ticksPerFoot * distance);
        }

        //Halfs speed within 3 feet of target, if total distance is at least 5 feet
        if ((encoderCurrent + (3 * ticksPerFoot) > encoderTarget) && distance > 5) {
            power *= 0.5;
        }

        double orientationError;

        double x = power * Math.sin(Math.toRadians(targetHeading));
        double y = power * Math.cos(Math.toRadians(targetHeading));


        if (distance < 0){
            System.out.println("Error from autoCrabDrive(), negative distance not allowed");
            return Robot.DONE;
        }

        

        //Adjusts wheel angles
        orientationError = autoCrabDriveController.calculate(ahrs.getYaw(), targetOrientation); 
        teleopSwerve(x, y, orientationError, false);

        //Checks if target distance has been reached, then ends function if so
        if (encoderCurrent >= encoderTarget) {
            firstTime = true;
            //stopWheels();
            rotateController.reset();
            return Robot.DONE;
        } 
        else {
            return Robot.CONT;
        }

    }



    /****************************************************************************************** 
    *
    *    teleopRotate()
    *    Only uses Z to rotate robot
    *    This function negates rotatePower in order to make positive inputs turn the robot clockwise
    * 
    ******************************************************************************************/
    public void teleopRotate(double rotatePower) {
        frontRightWheel.rotateAndDrive(rotateRightFrontMotorAngle, rotatePower * -1);
        frontLeftWheel.rotateAndDrive(rotateLeftFrontMotorAngle, rotatePower * -1);
        rearRightWheel.rotateAndDrive(rotateRightRearMotorAngle, rotatePower * -1);
        rearLeftWheel.rotateAndDrive(rotateLeftRearMotorAngle, rotatePower * -1);
    }



    /****************************************************************************************** 
    *
    *    autoRotate()
    *    Rotates robot to inputted angle
    * 
    ******************************************************************************************/
    public int autoRotate(double degrees) {
        double rotateError;
        long currentMs = System.currentTimeMillis();

        if (rotateFirstTime == true) {
            rotateFirstTime = false;
            count = 0;
            timeOut = currentMs + 2500; //Makes the time out 2.5 seconds
        }

        if (currentMs > timeOut) {
			count = 0;
            rotateFirstTime = true;
            
			System.out.println("Timed out");
            stopWheels();
            return Robot.FAIL;
		}

		// Rotate
        rotateError = rotateController.calculate(ahrs.getYaw(), degrees);
        rotateError = MathUtil.clamp(rotateError, -0.5, 0.5);
        System.out.println(rotateError + " " + ahrs.getYaw());
		teleopRotate(rotateError);

		// CHECK: Routine Complete
		if (rotateController.atSetpoint() == true) {
            count++;            

			if (count == ON_ANGLE_COUNT) {
				count = 0;
                rotateFirstTime = true;
                rotateController.reset();
                stopWheels();                                
                return Robot.DONE;
            }
            else {
				return Robot.CONT;
			}
		}
		else {    
			count = 0;
            return Robot.CONT;
		}
    }


    /****************************************************************************************** 
    *
    *    circle()
    *    Moves robot around circle with given radius (radius is from center of circle to center of robot)
    * 
    ******************************************************************************************/
    public void circle(double radiusFeet) {

        double radius = radiusFeet*12;

        //Finds angle of the radius to each wheel, used to find the angle the wheels need to go to
        double innerAngle = Math.toDegrees(Math.atan2(robotWidth/2, radius - (robotLength/2)));
        double outerAngle = Math.toDegrees(Math.atan2(robotWidth/2, radius + (robotLength/2)));

        //The distance that each wheel is from the center of the circle is found with the pythagorean theorem
        double innerDist = Math.pow(   Math.pow((robotWidth/2), 2) + Math.pow(radius - robotLength/2, 2),    0.5);
        double outerDist = Math.pow(   Math.pow((robotWidth/2), 2) + Math.pow(radius + robotLength/2, 2),    0.5);

        //The ratio between the inner and outer speeds is equal to the ratio of their distances
        double outerSpeed = 0.25; //Sets basis for speed of turning
        double innerSpeed = outerSpeed * (innerDist/outerDist);

        frontLeftWheel.rotateAndDrive(innerAngle + 90, innerSpeed);
        frontRightWheel.rotateAndDrive(-90 - innerAngle, -1*innerSpeed);

        rearLeftWheel.rotateAndDrive(outerAngle + 90, outerSpeed);
        rearRightWheel.rotateAndDrive(-90 - outerAngle, -1*outerSpeed);
    }


    /****************************************************************************************** 
    *
    *    spiral()
    *    Robot moves forward while spinning around
    * 
    ******************************************************************************************/
    public void spiral() {
        teleopSwerve(0, 0.3, 0.3, true);
    }


    /****************************************************************************************** 
    *
    *    autoAdjustWheels()
    *    Rotates wheels to desired angle
    * 
    ******************************************************************************************/
    public int autoAdjustWheels(double degrees) {
        long currentMs = System.currentTimeMillis();

        if (rotateFirstTime == true) {
            rotateFirstTime = false;
            count = 0;
            timeOut = currentMs + 500; //Makes the time out 2.5 seconds
        }

        if (currentMs > timeOut) {
			count = 0;
            rotateFirstTime = true;
            
			System.out.println("Timed out");
            stopWheels();
            return Robot.FAIL;
		}

		// Rotate
        int FR = frontRightWheel.rotateAndDrive(degrees, 0);
        int FL = frontLeftWheel.rotateAndDrive(degrees, 0);
        int BR = rearRightWheel.rotateAndDrive(degrees, 0);
        int BL = rearLeftWheel.rotateAndDrive(degrees, 0);

        //Checks if all wheels are at target angle
        if (FR == Robot.DONE && FL == Robot.DONE && BR == Robot.DONE && BL == Robot.DONE) {
            return Robot.DONE;
        }
        else {
            return Robot.CONT;
        }
    }


    /****************************************************************************************** 
    *
    *    getAverageEncoder()
    *    Returns average value of all 4 wheels' encoders
    * 
    ******************************************************************************************/
    private double getAverageEncoder(){
        double sum =    frontRightWheel.getEncoderValue() +
                        frontLeftWheel.getEncoderValue()  +
                        rearRightWheel.getEncoderValue()  +
                        rearLeftWheel.getEncoderValue();
        return sum / 4.0;
    }


    /****************************************************************************************** 
    *
    *    stopWheels()
    *    Turns off all motors instead of turning wheels back to 0 degrees
    * 
    ******************************************************************************************/
    public void stopWheels(){
        frontLeftWheel.setDriveMotorPower(0);
        frontRightWheel.setDriveMotorPower(0);
        rearLeftWheel.setDriveMotorPower(0);
        rearRightWheel.setDriveMotorPower(0);

        frontLeftWheel.setRotateMotorPower(0);
        frontRightWheel.setRotateMotorPower(0);
        rearLeftWheel.setRotateMotorPower(0);
        rearRightWheel.setRotateMotorPower(0);

    }



    /****************************************************************************************** 
    *
    *    LIMELIGHT METHODS
    * 
    ******************************************************************************************/
    /**
     * Limelight targeting using PID
     * @param pipeline
     * @return program status
     */
	public int limelightPIDTargeting( TargetPipeline pipeline) {
		double m_LimelightCalculatedPower = 0;
        long currentMs = System.currentTimeMillis();
        final long TIME_OUT = 5000;

		if (limeLightFirstTime == true) {
            //Sets limeLightFirstTime to false
            limeLightFirstTime = false;

            //Resets the variables for tracking targets
			noTargetCount    = 0;
            targetLockedCount = 0;
            
            //Sets and displays the forced time out
			timeOut = currentMs + TIME_OUT;
            System.out.println("Limelight timeOut " + timeOut / 1000 + " seconds");
            
            //Turns the limelight on
            changeLimelightLED(LIMELIGHT_ON);
		}

		// Whether the limelight has any valid targets (0 or 1)
        double tv = limelightEntries.getEntry("tv").getDouble(0);
        //System.out.println("tv: " + tv);
		// Horizontal Offset From Crosshair To Target (-27 degrees to 27 degrees) [54 degree tolerance]
		double tx = limelightEntries.getEntry("tx").getDouble(0);
        //System.out.println("tx: " + tx);

		/*// Vertical Offset From Crosshair To Target (-20.5 degrees to 20.5 degrees) [41 degree tolerance]
        double ty = limelightEntries.getEntry("ty").getDouble(0);
        System.out.println("ty: " + ty);*/
		// Target Area (0% of image to 100% of image) [Basic way to determine distance]
		// Use lidar for more acurate readings in future
        //double ta = limelightEntries.getEntry("ta").getDouble(0);
        //System.out.println("ta: " + ta);
        //ta of 1.6% for the 10ft shot

		if (tv < 1.0) {
            stopWheels();

            //Adds one to the noTargetCount (will exit this program if that count exceedes 5) 
			noTargetCount++;

			if (noTargetCount <= FAIL_DELAY) {
                //Tells the robot to continue searching
				return Robot.CONT;
			}
			else {
                //Reset variables
				noTargetCount      = 0;
                targetLockedCount  = 0;
                limeLightFirstTime = true;
                targetController.reset();

                stopWheels();
                                
                //Returns the error code for failure
				return Robot.FAIL;
			}
		}
        else {
            //Keeps the no target count at 0
            noTargetCount = 0;
		}

        // Rotate
        // Need a -1 angle because limelight is slightly offset
		m_LimelightCalculatedPower = targetController.calculate(tx, -1.0);
        m_LimelightCalculatedPower = MathUtil.clamp(m_LimelightCalculatedPower, -0.50, 0.50);
		teleopRotate(m_LimelightCalculatedPower * -1);
		//System.out.println("Pid out: " + m_LimelightCalculatedPower);

		// CHECK: Routine Complete
		if (targetController.atSetpoint() == true) {
            targetLockedCount++;
            
			//System.out.println("On target");
		}

		if (targetLockedCount >= ON_TARGET_COUNT) {
            //Reset variables
            targetLockedCount = 0;
            noTargetCount     = 0;
            limeLightFirstTime = true;
            targetController.reset();
            
			stopWheels();

            //System.out.println("On target or not moving");

            //Returns the error code for success
			return Robot.DONE;
        }
        
		// limelight time out readjust
		if (currentMs > timeOut) {
            targetLockedCount = 0;
            noTargetCount     = 0;
            limeLightFirstTime = true;
            targetController.reset();
            
            stopWheels();
                        
            System.out.println("timeout " + tx + " Target Acquired " + tv);

            //Returns the error code for failure
			return Robot.FAIL;
        }
        
		return Robot.CONT;   
    }

    /**
     * This is a test method for moving the robot under the control of the limelight
     * Do not use it yet as it isn't finished or checked
     * @return limelight motion finished
     */
    public int limelightMotion() {
        // Status Variables
        int status = 0;

        // Variables
        Shooter.ShootLocation attemptedShot = Shooter.ShootLocation.TEN_FOOT; //.getShooterLocation(); can't use controls in drive
        Shooter.ShootLocation LAY_UP = Shooter.ShootLocation.LAY_UP;
        Shooter.ShootLocation TEN_FOOT = Shooter.ShootLocation.TEN_FOOT;
        Shooter.ShootLocation TRENCH = Shooter.ShootLocation.TRENCH;

        // Limelight variables
        final double LAY_UP_TARGET   = 0.00; //definitely not this
        final double TEN_FOOT_TARGET = 1.83;
        final double TRENCH_TARGET   = 0.00; //definitely not this
        double ta = limelightEntries.getEntry("ta").getDouble(0);

        if (attemptedShot == LAY_UP) {
            if (ta - LAY_UP_TARGET > 0.01) { //ta is greater than 0.00, a.k.a you're too close
                //status = forward()
            }
            else if (ta - LAY_UP_TARGET < 0.01) { //ta is less than 0.00, a.k.a you're too far
                //status = forward()
            }
        }
        else if (attemptedShot == TEN_FOOT) {
            if (ta - TEN_FOOT_TARGET > 0.01) { //ta is greater than 1.84, a.k.a you're too close
                //status = forward()
            }
            else if (ta - TEN_FOOT_TARGET < 0.01) { //ta is less than 1.82, a.k.a you're too far
                //status = forward()
            }
        }
        else if (attemptedShot == TRENCH) {
            if (ta - TRENCH_TARGET > 0.01) { //ta is greater than 0.00, a.k.a you're too close
                //status = forward()
            }
            else if (ta - TRENCH_TARGET < 0.01) { //ta is less than 0.00, a.k.a you're too far
                //status = forward()
            }
        }

        return status;
    }


	/**
	 * Change Limelight Modes
	 */
	// Changes Limelight Pipeline
	public void changeLimelightPipeline(int pipeline) {
		// Limelight Pipeline
		limelightEntries.getEntry("pipeline").setNumber(pipeline);
	}

	// Change Limelight LED's
	public void changeLimelightLED(int mode) {
		// if mode = 0 limelight on : mode = 1 limelight off
		limelightEntries.getEntry("ledMode").setNumber(mode);
	}
    
   


    /****************************************************************************************** 
    *
    *    TEST FUNCTIONS
    * 
    ******************************************************************************************/
    public void testWheel(){
        rearRightWheel.setDriveMotorPower(-0.5);
    }
    
    public void testRotate(){
        double power = -.2;
        frontLeftWheel.setRotateMotorPower(power);
        frontRightWheel.setRotateMotorPower(power);
        rearLeftWheel.setRotateMotorPower(power);
        rearRightWheel.setRotateMotorPower(power);
        System.out.println("Degrees: " + rearLeftWheel.getRotateMotorPosition());
    }

    public void testPID() {
        frontLeftWheel.rotateAndDrive(0, 0);
    }

    public void testEncoder(){
        System.out.println("FR encoder: " + frontRightWheel.getEncoderValue());
    }

    public void testWheelAngle(){
        System.out.println("Angle: " + rearRightWheel.getRotateMotorPosition());
    }


} // End of the Drive Class