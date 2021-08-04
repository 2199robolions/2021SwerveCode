package frc.robot;

import edu.wpi.first.wpiutil.math.MathUtil;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.SPI;

import edu.wpi.first.networktables.*;

public class Drive {
    //Object creation
    Controls controls;
    LedLights led;
    NetworkTable limelightEntries = NetworkTableInstance.getDefault().getTable("limelight");

    //NAVX
    private static AHRS ahrs;
    private PIDController rotateController;
    private PIDController autoCrabDriveController;
    private PIDController targetController;

    static final double rotateToleranceDegrees = 2.0f;
	static final double kLimeLightToleranceDegrees = 1.0f;

	//Variables
    private boolean firstTime       = true;
    private boolean rotateFirstTime = true;
    private int     count           = 0;
    private double  encoderTarget   = 0;
    private double  targetYaw       = 0;
    
    //CONSTANTS
    private final int FAIL_DELAY = 5;

	//Limelight Variables
    private int     noTargetCount      = 0;
    private int     limeCount          = 0;
    private long    timeOut;
    private boolean limeLightFirstTime = true;
	private static final int ON_TARGET_COUNT = 20;
    private static final int ON_ANGLE_COUNT  = 10;

    //Limelight
	public              boolean limeControl                   = false;
	public              int     limeStatus                    = 0;
	public static final int     LIMELIGHT_ON                  = 3;
	public static final int     LIMELIGHT_OFF                 = 1;

    //Limelight distance calc
    private static final double CameraMountingAngle = 22.0;	                     // 25.6 degrees, 22.0
	private static final double CameraHeightFeet 	= 26.5 / 12;	             // 16.5 inches
	private static final double TargetHeightFt 	    = 7 + (7.5 / 12.0) ;	     // 8ft 2.25 inches
	private static double mountingRadians = Math.toRadians(CameraMountingAngle); // a1, converted to radians

	// find result of h2 - h1, or Δh
	private static double DifferenceInHeight = TargetHeightFt - CameraHeightFeet;
    
    // Turn Controller
	private static final double kP = 0.02;
	private static final double kI = 0.00;
    private static final double kD = 0.00;
    
    //Auto crab drive controller
    private static final double acdP = 2.5;
    private static final double acdI = 0;
    private static final double acdD = 0;

	//Target Controller
	private static final double tP = 0.02; //0.2
	private static final double tI = 0.00;
    private static final double tD = 0.00;

    public static final double ticksPerFoot = 5.75; //Been validated
    
    /**
     * Enumerators
     */
    /**
     * The enumerator for locking the drive wheels for targeting
     */
    public static enum WheelMode {
		MANUAL,
		TARGET_LOCK;
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
            this.targetVoltage = (((targetRadians * 2.5) / Math.PI) + 2.5);
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

        //We prefer to use degrees
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
    
    /**
     * The literal lengths and widths of the robot. Look to the swerve drive Google Doc
     * Note: these fields are static because they must be. They are referenced in the enum, which is in and of itself, static.
     * These measurements are in inches
     */
    private static final double robotLength = 30.0;
    private static final double robotWidth  = 18.0;
    // TODO: Question for any one of the mentors, are these declarations and instantiations in memory done only once at the start when the robot is started and the code loads? I would assume so, which is why I'm not putting these in the constructor, to save unnecessary compute power if we would ever instantiate more than one of the Drive objects
    // Note: this field is static because it must be. It is referenced in the enum, which is in and of itself, static.
    private static final double rotateMotorAngle = Math.atan2(robotLength, robotWidth);
    private static final double rotateMotorAngleRad = Math.atan2(robotLength, robotWidth);
    private static final double rotateMotorAngleDeg = Math.toDegrees(rotateMotorAngleRad);
 
    // These numbers were selected to make the angles between -180 and +180
    private static final double rotateRightFrontMotorAngle = -1 * rotateMotorAngleDeg;
    private static final double rotateLeftFrontMotorAngle = rotateRightFrontMotorAngle - 90;
    private static final double rotateRightRearMotorAngle = rotateRightFrontMotorAngle + 90;
    private static final double rotateLeftRearMotorAngle =  rotateRightFrontMotorAngle + 180;

    public class PowerAndAngle{
        public double power;
        public double angle;

        public PowerAndAngle(double powerParam, double angleParam){
            this.power = powerParam;
            this.angle = angleParam;
        }

        // added getters
        public double getPower()  {
            return power;
        }

        public double getAngle()  {
            return angle;
        }
    }

    /**
     * Contructor for the Drive class
     */
    public Drive() {
        //Instance creation
        led = LedLights.getInstance();
        
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
    
        // At Start, Set navX to ZERO
        ahrs.zeroYaw();

        //PID Controllers
        rotateController        = new PIDController(kP, kI, kD);
        autoCrabDriveController = new PIDController(acdP, acdI, acdD);
        targetController        = new PIDController(tP, tI, tD);
		
		/* Max/Min input values.  Inputs are continuous/circle */
        rotateController.enableContinuousInput(-180.0, 180.0);
        autoCrabDriveController.enableContinuousInput(-180.0, 180.0);
		targetController.enableContinuousInput(-30.0, 30.0);

		/* Max/Min output values */
		//Turn Controller
		rotateController.setIntegratorRange(-.25, .25); // do not change 
		rotateController.setTolerance(rotateToleranceDegrees);

		//Target Controller
		targetController.setIntegratorRange(-.2, .2); // do not change 
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

    public PowerAndAngle calcSwerve(double driveX, double driveY, double rotatePower, double rotateAngle){
        double swerveX;
        double swerveY;
        double swervePower;
        double swerveAngle;
        double rotateX;
        double rotateY;

        //System.out.println("X:" + (float)driveX + " Y:" + (float)driveY);
        //System.out.println("RotatePower:" + rotatePower + " RotateAngle:" + rotateAngle);
        //System.out.println("RotateX:" + rotatePower * Math.sin(Math.toRadians(rotateAngle)));

        /**
         * The incomming rotate angle will cause the robot to rotate counter-clockwise
         * the incomming power is negative for a counter-clockwise rotation and vise versa for clockwise
         * therefore, we want power to be positive to achieve a counter-clockwise rotation
         * which means that we have to multiply the power by negative 1  
         */ 
        rotateX = (-1 * rotatePower) * Math.sin(Math.toRadians(rotateAngle));
        rotateY = (-1 * rotatePower) * Math.cos(Math.toRadians(rotateAngle));

        swerveX = driveX + rotateX;
        swerveY = driveY + rotateY;

        //System.out.println("swerveX:" + swerveX + " swerveY:" + swerveY);
        //Issue occurs around here
        swervePower = Math.sqrt((swerveX * swerveX) + (swerveY * swerveY));
        // converted radians to degrees
        //Definetely x, y
        swerveAngle = Math.toDegrees(Math.atan2(swerveX, swerveY));
        //System.out.println("swerveAngle:" + swerveAngle);

        PowerAndAngle swerveNums = new PowerAndAngle(swervePower, swerveAngle);

        return swerveNums;
    }

    /**
     * The unfinished Swerve Drive program.
     * @param targetWheelAngle
     * @param drivePower
     * @param rotatePower
     */
    public void teleopSwerve(double driveX, double driveY, double rotatePower) {
        PowerAndAngle coor;

        coor = calcSwerve(driveX, driveY, rotatePower, rotateRightFrontMotorAngle);
        frontRightWheel.rotateAndDrive(coor.getAngle(), coor.getPower());
        //System.out.println("FR angle: " + coor.angle + " FR power " + coor.power);

        coor = calcSwerve(driveX, driveY, rotatePower, rotateLeftFrontMotorAngle);
        frontLeftWheel.rotateAndDrive(coor.getAngle(), coor.getPower());
        //System.out.println("FL angle: " + coor.angle + " FR power " + coor.power);

        coor = calcSwerve(driveX, driveY, rotatePower, rotateRightRearMotorAngle);
        rearRightWheel.rotateAndDrive(coor.getAngle(), coor.getPower());

        coor = calcSwerve(driveX, driveY, rotatePower, rotateLeftRearMotorAngle);
        rearLeftWheel.rotateAndDrive(coor.getAngle(), coor.getPower());
    }

    /**
     * The current Crab Drive program.
     * @param wheelAngle
     * @param drivePower
     */
    public void teleopCrabDrive(double wheelAngle, double drivePower){
        frontLeftWheel.rotateAndDrive(wheelAngle, drivePower);
        frontRightWheel.rotateAndDrive(wheelAngle, drivePower);
        rearLeftWheel.rotateAndDrive(wheelAngle, drivePower);
        rearRightWheel.rotateAndDrive(wheelAngle, drivePower);
    }

    /**
     * A positive rotate power will make it rotate counter-clockwise and a negative will make it rotate clockwise
     * We don't want this therefore the motors will RECIEVE a negated power. 
     * @param rotatePower
     */
    public void teleopRotate(double rotatePower) {
        frontRightWheel.rotateAndDrive(rotateRightFrontMotorAngle, rotatePower * -1);
        frontLeftWheel.rotateAndDrive(rotateLeftFrontMotorAngle, rotatePower * -1);
        rearRightWheel.rotateAndDrive(rotateRightRearMotorAngle, rotatePower * -1);
        rearLeftWheel.rotateAndDrive(rotateLeftRearMotorAngle, rotatePower * -1);
    }




     /**
     * Autonomous rotate:
     * rotates to a certain angle
     * @param degrees
     * @return status
     */
    public int autoRotate(double degrees) {
        double pidOutput;
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
            disableMotors();
            return Robot.FAIL;
		}

		// Rotate
		pidOutput = rotateController.calculate(getYaw(), degrees);
        pidOutput = MathUtil.clamp(pidOutput, -0.75, 0.75);
        //System.out.println("Degrees: " + getYaw() + " Pidoutput: " + pidOutput);
		//System.out.println("Yaw: " + getYaw());
		//System.out.println(pidOutput);
		teleopRotate(pidOutput);

		// CHECK: Routine Complete
		if (rotateController.atSetpoint() == true) {
            count++;            
			System.out.println("Count: " + count);

			if (count == ON_ANGLE_COUNT) {
				count = 0;
                rotateFirstTime = true;
                rotateController.reset();

                disableMotors();                
                System.out.println("DONE");
                
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




    /**
     * The getYaw function for the NavX
     * @return The NavX's Yaw
     */
    public double getYaw(){
        return ahrs.getYaw();
    }






    /**
	 * LIMELIGHT METHODS
	 */
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
			noTargetCount = 0;
            limeCount = 0;
            
            //Resets the targeting PID's zero
			targetController.setSetpoint(0.0);
            
            //Sets and displays the forced time out
			timeOut = currentMs + TIME_OUT;
            System.out.println("TimeOut " + timeOut / 1000 + " seconds");
            
            //Turns the limelight on
            changeLimelightLED(LIMELIGHT_ON);

            //Makes the LED's go to targeting mode 
			led.limelightAdjusting();
		}

		// Whether the limelight has any valid targets (0 or 1)
        double tv = limelightEntries.getEntry("tv").getDouble(0);
        System.out.println("tv: " + tv);
		// Horizontal Offset From Crosshair To Target (-27 degrees to 27 degrees) [54 degree tolerance]
		double tx = limelightEntries.getEntry("tx").getDouble(0);
		System.out.println("tx: " + tx);

		/*// Vertical Offset From Crosshair To Target (-20.5 degrees to 20.5 degrees) [41 degree tolerance]
        double ty = limelightEntries.getEntry("ty").getDouble(0);
        System.out.println("ty: " + ty);*/
		/*// Target Area (0% of image to 100% of image) [Basic way to determine distance]
		// Use lidar for more acurate readings in future
        double ta = limelightEntries.getEntry("ta").getDouble(0);
        System.out.println("ta: " + ta);*/

		if (tv < 1.0) {
            //Has the LED's display that there is no valid target
            led.limelightNoValidTarget();
            
			teleopRotate(0.00);

            //Adds one to the noTargetCount (will exit this program if that count exceedes 5) 
			noTargetCount++;

			if (noTargetCount <= FAIL_DELAY) {
                //Tells the robot to continue searching
				return Robot.CONT;
			}
			else {
                //Reset variables
				noTargetCount = 0;
                limeCount = 0;
                limeLightFirstTime = true;
                targetController.reset();

                teleopRotate(0.00);
                
                //Displays a failed attempt on the LED's
                led.limelightNoValidTarget();
                
                //Returns the error code for failure
				return Robot.FAIL;
			}
		}
        else {
            //Keeps the no target count at 0
            noTargetCount = 0;

            //Keeps the LED's displaying that the robot is targeting
			led.limelightAdjusting();
		}

		// Rotate
		m_LimelightCalculatedPower = targetController.calculate(tx, 0.0);
		m_LimelightCalculatedPower = MathUtil.clamp(m_LimelightCalculatedPower, -0.50, 0.50);
		teleopRotate(m_LimelightCalculatedPower);
		System.out.println("Pid out: " + m_LimelightCalculatedPower);

		// CHECK: Routine Complete
		if (targetController.atSetpoint() == true) {
            limeCount++;
            
			System.out.println("On target");
		}

		if (limeCount >= ON_TARGET_COUNT) {
            //Reset variables
			limeCount = 0;
            limeLightFirstTime = true;
            targetController.reset();
            
			teleopRotate(0.00);
            
            //Makes the LED's show that the robot is done targeting 
            led.limelightFinished();

            System.out.println("On target or not moving");

            //Returns the error code for success
			return Robot.DONE;
        }
        
		// limelight time out readjust
		if (currentMs > timeOut) {
			limeCount = 0;
            limeLightFirstTime = true;
            
            targetController.reset();
            
            teleopRotate(0.00);
            
            led.limelightNoValidTarget();
            
            System.out.println("timeout " + tx + " Target Acquired " + tv);

            //Returns the error code for failure
			return Robot.FAIL;
        }
        
        if (controls.autoKill() == true) {
            //Returns the error code for failure
            return Robot.FAIL;
        }

		return Robot.CONT;   
    }





    /** 
     * tan(a1+a2) = (h2-h1) / d
	 * D = (h2 - h1) / tan(a1 + a2).
     * These equations, along with known numbers, helps find the distance from a target.
	 */
	public double getDistance() {
	  // Vertical Offset From Crosshair To Target (-20.5 degrees to 20.5 degrees) [41 degree tolerance]
	  double ty = limelightEntries.getEntry("ty").getDouble(0);
		
	  // a2, converted to radians
	  double radiansToTarget = Math.toRadians(ty); 

	  // find result of a1 + a2
	  double angleInRadians = mountingRadians + radiansToTarget;

	  // find the tangent of a1 + a2
	  double tangentOfAngle = Math.tan(angleInRadians); 

	  // Divide the two results ((h2 - h1) / tan(a1 + a2)) for the distance to target
	  double distance = DifferenceInHeight / tangentOfAngle;

	  // outputs the distance calculated
	  return distance; 
	}





	/** 
	 * a1 = arctan((h2 - h1) / d - tan(a2)). This equation, with a known distance input, helps find the 
	 * mounted camera angle.
	 */
	public double getCameraMountingAngle(double measuredDistance) {
	  // Vertical Offset From Crosshair To Target (-20.5 degrees to 20.5 degrees) [41 degree tolerance]
	  double ty = limelightEntries.getEntry("ty").getDouble(0);

	  // convert a2 to radians
	  double radiansToTarget = Math.toRadians(ty);

	  // find result of (h2 - h1) / d
	  double heightOverDistance = DifferenceInHeight / measuredDistance;

	  // find result of tan(a2)
	  double tangentOfAngle = Math.tan(radiansToTarget);

	  // (h2-h1)/d - tan(a2) subtract two results for the tangent of the two sides
	  double TangentOfSides = heightOverDistance - tangentOfAngle; 

	  // invert tangent operation to get the camera mounting angle in radians
	  double newMountingRadians = Math.atan(TangentOfSides);

	  // change result into degrees
	  double cameraMountingAngle = Math.toDegrees(newMountingRadians);
	  
	  return cameraMountingAngle; // output result
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
    


    private double getAverageEncoder(){
        double sum =    frontRightWheel.getEncoderValue() +
                        frontLeftWheel.getEncoderValue()  +
                        rearRightWheel.getEncoderValue()  +
                        rearLeftWheel.getEncoderValue();
        return sum / 4.0;

    }



    /**
     * AUTONOMOUS DRIVE METHODS
     */
    /**
     * Autonomous drive:
     * drives a certain number of feet at a certain angle
     * @param distance - the number of feet the robot drives
     * @param targetDegrees - angle the robot drives
     * @param power 
     * @return status
     */
    public int autoCrabDrive(double distance, double targetDegrees, double power) {

        double encoderCurrent = getAverageEncoder(); //Average of 4 wheels
        //double encoderCurrent = frontLeftWheel.getEncoderValue();

        //First time through initializes target values
        if(firstTime == true){
            firstTime = false;
            targetYaw = getYaw();
            encoderTarget = encoderCurrent + (ticksPerFoot * distance);
        }

        //Adjusts wheel angles
        double pidOutput;
        pidOutput = autoCrabDriveController.calculate(getYaw(), targetYaw); 
        System.out.println("Yaw: " + getYaw() + " pidoutput: " + pidOutput);
        teleopCrabDrive(targetDegrees + pidOutput, power); //Add pidOutput to targetDegrees if we want to use yaw

        //teleopSwerveDrive(targetDegrees, power, somepidoutput)

        //Checks if target distance has been reached, then ends function if so
        if (distance >= 0) {
            if(encoderCurrent >= encoderTarget){
                firstTime = true;
                teleopCrabDrive(0, 0);
                rotateController.reset();
                return Robot.DONE;
            } 
            else {
                return Robot.CONT;
            }
        } 
        else { //Distance < 0 
            if(encoderCurrent <= encoderTarget){
                firstTime = true;
                teleopCrabDrive(0, 0);
                rotateController.reset();
                return Robot.DONE;
            } 
            else {
                return Robot.CONT;
            }
        }

    }
    public int autoCrabDrive(double distance, double targetDegrees) { //Generic function for autoCrabDrive with default power of 0.6
        return autoCrabDrive(distance, targetDegrees, 0.6);
    }



    /**
     * Autonomous field drive:
     * drives a certain number of feet at a certain angle relative to the field
     * @param distance - the number of feet the robot drives
     * @param targetDegrees - angle the robot drives
     * @param power 
     * @return status
     */
    public int autoFieldDrive(double distance, double targetDegrees, double power){
        //autoDrive but 0 degrees is always the same direction, regardless of robot orientation
        double encoderCurrent = getAverageEncoder(); //Average of 4 wheels
        
        //First time through initializes target values
        if(firstTime == true){
            firstTime = false;
            encoderTarget = encoderCurrent + (ticksPerFoot * distance);
        }

        //Calculates how far wheels need to correct themselves to drive on correct yaw, then drives
        double pidOutput;
        pidOutput = rotateController.calculate(targetDegrees, getYaw()); 
		pidOutput = MathUtil.clamp(pidOutput, -0.25, 0.25);
        teleopCrabDrive(targetDegrees + pidOutput, power);

        //Checks if target distance has been reached, then ends function if so
        if (distance >= 0) {
            if(encoderCurrent >= encoderTarget){
                firstTime = true;
                teleopSwerve(0, 0, 0);
                rotateController.reset();
                return Robot.DONE;
            } 
            else {
                return Robot.CONT;
            }
        } 
        else { //Distance < 0 
            if(encoderCurrent <= encoderTarget){
                firstTime = true;
                teleopSwerve(0, 0, 0);
                rotateController.reset();
                return Robot.DONE;
            } 
            else {
                return Robot.CONT;
            }
        }
    }
    public int autoFieldDrive(double distance, double targetDegrees) { //Generic function for autoFieldDrive with default power of 0.6
        return autoFieldDrive(distance, targetDegrees, 0.6);
    }




    /**
     * TEST FUNCTIONS
     */
    public void disableMotors() {
        teleopSwerve(0.00, 0.00, 0.00);
    }

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

    public void resetEncoders(){
        frontRightWheel.resetEncoder();
        frontLeftWheel.resetEncoder();
        rearRightWheel.resetEncoder();
        rearLeftWheel.resetEncoder();
    }

} // End of the Drive Class