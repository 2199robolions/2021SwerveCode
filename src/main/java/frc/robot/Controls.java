package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpiutil.math.MathUtil;

public class Controls {
    private boolean button3State = false;
    private boolean oldButton3State = false;
    private boolean fieldDrive = false;

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

    // Joystick Object Declaration & Instantiation
    private Joystick joystick = new Joystick(ControllerIDs.JOYSTICK.getId());
    
    // XboxController Object Declaration & Instantiation
    private XboxController xboxController = new XboxController(ControllerIDs.XBOXCONTROLLER.getId());

    public Controls() {
        //
    }

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
    public double getDrivePower(){
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
    public double getDriveX(){
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
    public double getDriveY(){
        double power = joystick.getY() * -1;
        double deadZone = 0.1;

        if ((power < deadZone) && (power > (deadZone * -1))) {
            return 0;
        }
        else {
            return power;
        }        
    }

    public boolean fieldDrive(){
        oldState = currState;
        currState = joystick.getRawButton(3);

        //If the button was just pressed
        if((currState == true) && (oldState == false)) {
            fieldDrive != fieldDrive; //Switch the fieldDrive value
        }
        return fieldDrive;
    }

} // End of the Controls class