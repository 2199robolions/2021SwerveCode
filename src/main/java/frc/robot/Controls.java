package frc.robot;

import edu.wpi.first.wpiutil.math.MathUtil;

import edu.wpi.first.wpilibj.Joystick;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;

public class Controls {
    
    //Singleton Method to insure that there is ever only one instance of Controls
    private static Controls instance = null;

    public static synchronized Controls getInstance() {
        if (instance == null) {
            instance = new Controls();
        }

        return instance;
    }

    /**
     * Enumerator for controller ID's
     */
    private enum ControllerIDs {
        JOYSTICK(1),
        XBOXCONTROLLER(0);
        
        private int id;

        // Each item in the enum will now have to be instantiated with a constructor with the integer id. Look few lines above, where JOYSTICK(int id) and XBOXCONTROLLER(int id) are. These are what the constructor is for.
        private ControllerIDs(int id) {
            this.id = id;
        }

        private int getId() {
            return this.id;
        }
    }

    // Joystick Object Declaration
    private Joystick joystick;
    
    // XboxController Object Declaration
    private XboxController xboxController;

    private Controls() {
        //Instance Creation
        joystick        = new Joystick(ControllerIDs.JOYSTICK.getId());
        xboxController  = new XboxController(ControllerIDs.XBOXCONTROLLER.getId());
    }

    /**
     * JOYSTICK METHODS
     */
    
     /**
     * 0 degrees is forward on the Joystick
     * this method returns values from -180 to +180
     * @return driveAngle
     */
    public double getDriveAngle() {
        double deadZone = 0.1;

        double x = joystick.getX();
        double y = joystick.getY() * -1;
        
        double rad = Math.atan2(x, y);
        double deg = Math.toDegrees(rad);
        //double = MathUtil.clamp(deg, 0, 360);

        // Drive Power is always positive
        double drivePower = getDrivePower();

        //System.out.println("Dr Pwr " + drivePower + " x " + x + " y " + y);
        
        if ((drivePower < deadZone) && (drivePower > -deadZone)) {
            return 0;
        }
        else {
            return deg;
        }
    }

    /**
     * Positive values are from clockwise rotation 
     * and negative values are from counter-clockwise
     * @return rotatePower
     */
    public double getRotatePower() {
        double deadZone = 0.2;

        double powerCubed;
        double power = joystick.getZ();

        if ((power < deadZone) && (power > (deadZone * -1))) {
            return 0;
        }
        else {
            //Cubing power because the rotate is SUPER sensitive
            powerCubed = Math.pow(power, 3); 
            
            return powerCubed;
        }        
    }

    /**
     * Gets the drive power
     * @return drivePower
     */
    public double getDrivePower() {
        double x = joystick.getX();
        double y = joystick.getY() * -1;
        
        double hyp = Math.sqrt(x*x + y*y);
        double hypClamp;
        //This will make it reach power 1 faster at a 45 degrees
        hypClamp = MathUtil.clamp(hyp, -1, 1);
        //hypClamp = hyp / Math.sqrt(2);
        
        return hypClamp;
    }

    /**
     * Gets the drive X
     * @return driveX
     */
    public double getDriveX() {
        double power = joystick.getX();
        double deadZone = 0.1;

        if ((power < deadZone) && (power > (deadZone * -1))) {
            return 0;
        }
        else {
            return power;
        }
    }

    /**
     * Gets the drive Y
     * @return driveY
     */
    public double getDriveY() {
        double power = joystick.getY() * -1;
        double deadZone = 0.1;

        if ((power < deadZone) && (power > (deadZone * -1))) {
            return 0;
        }
        else {
            return power;
        }        
    }

    /**
     * Joystick button 3
     * @return Whether or not field oriented drive should be activated
     */
    public boolean toggleFieldDrive() {
        boolean fieldDrive;
        fieldDrive = joystick.getRawButton(3);

        return fieldDrive;
    }

    /**
     * Joystick button ???
     * @return Whether or not the limelight should target
     */
    public boolean enableTargetLock() {
        boolean isPressed;
        isPressed = joystick.getRawButtonPressed(11); // This needs to become an actual button sonner or later
        
        return isPressed;
    }

    /**
     * Joystick trigger
     * @return Whether or not the shooter should fire
     */
    public boolean enableShooter() {
        boolean isPressed;
        isPressed = joystick.getTrigger();
        
        return isPressed;
    }

    /**
     * Joystick button 4
     * @return
     */
    public boolean enableTrenchShot() {
        return joystick.getRawButton(4);
    }

    /**
     * Joystick button 2
     * @return 
     */
    public boolean hailMary() {
        return joystick.getRawButton(2);
    }


    /**
     * These are all Functions of the Xbox controller
     */
    /**
     * Start Button Pressed
     * WHETER TO KILL ALL ACTIVE AUTO PROGRAMS!
     * @return startButtonPressed
     */
    public boolean autoKill() {
        return xboxController.getStartButtonPressed();
    }
    
    /**
     * Button A
     * @return buttonAPressed
     */
    //

    /**
     * Button B
     * @return buttonBPressed
     */
    //

    /**
     * Button X
     * @return buttonXPressed
     */
    //

    /**
     * Button Y
     * Deploys / Retracts the Grabber
     * @return buttonYPressed
     */
    public boolean grabberDeployRetract() {
        return xboxController.getYButtonPressed();
    }

    /**
     * DPad Inputs
     * Upper left, Up, and Upper right on the DPad returns forward
     * Lower left, Down, and Lower right on the DPad returns reverse
     * @return The direction the grabber should move
     */
    public Grabber.GrabberDirection getGrabberDir() {
        int dPad;
        dPad = xboxController.getPOV();

        if ( (dPad == 315) || (dPad == 0) || (dPad == 45) ) {
            return Grabber.GrabberDirection.REVERSE;
        }
        else if ( (dPad == 225) || (dPad == 180) || (dPad == 135) ) {
            return Grabber.GrabberDirection.FORWARD;
        }
        else {
            return Grabber.GrabberDirection.OFF;
        }
    }

    /**
     * Right Bumper Pressed
     * @return rightBumperPresed
     */
    public boolean climberAllArmsUp() {
        return xboxController.getBumper(Hand.kRight);
    }

    /**
     * Left Bumper Pressed
     * @return leftBumperPresed
     */
    public boolean climberTopArmDown() {
        return xboxController.getBumper(Hand.kLeft);
    }

    /**
     * Right trigger
     * @return power
     */
    public double getClimberPower() {
        double power;
        power = xboxController.getTriggerAxis(Hand.kRight);

        //trigger dead band
        if (power > 0.1) {
            return power;
        }
        else {
            return 0;
        }
    }

    /**
     * Left Trigger
     */
    //May become color wheel

    /**
     * XBox Controller Right Stick
     * @return Direction of the Ball Feeder Motor
     */
    public Shooter.BallFeederDirection ballFeederControl() {
        double xboxY;
        xboxY = xboxController.getY(Hand.kRight) * -1;

        //Trigger dead band
        if (xboxY > 0.1) {
            return Shooter.BallFeederDirection.FORWARD;
        }
        else if (xboxY < -0.1) {
            return Shooter.BallFeederDirection.REVERSE;
        }
        else {
            return Shooter.BallFeederDirection.OFF;
        }
    }

    /**
     * XBox Controller Left Stick
     */
    public Shooter.HoodMotorPosition hoodMotorControl() {
        double xboxY;
        xboxY = xboxController.getY(Hand.kLeft) * -1;

        if (xboxY >= 0.2) {
            return Shooter.HoodMotorPosition.LOW_SHOT;
        }
        else if (xboxY <= -0.2) {
            return Shooter.HoodMotorPosition.HIGH_SHOT;
        }
        else {
            return Shooter.HoodMotorPosition.AVERAGE_POSITION;
        }
    }

    /**
     * XBox Controller Right Stick Pressed
     */
    // Nothing so far

    /**
     * XBox Controller Left Stick Pressed
     */
    // Nothing so far

} // End of the Controls class