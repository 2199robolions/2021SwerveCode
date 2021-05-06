package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpiutil.math.MathUtil;

public class Controls {

    private enum ControllerIDs {
        JOYSTICK(1), // TODO: Need actual values for these
        XBOXCONTROLLER(0); // TODO: Need actual values for these
        
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
    }

    public double getJoystickZ() {
        return joystick.getZ();
    }

    /**
     * 0 degrees is forward on the Joystick
     * this method returns values from -180 to +180
     */
    public double getDriveAngle(){
        double x = joystick.getX();
        double y = joystick.getY() * -1;
        
        double rad = Math.atan2(x, y);
        double deg = Math.toDegrees(rad);
        //double degClamp = MathUtil.clamp(deg, -180, 180);

        double positiveDeadZone = 0.1;
        double negativeDeadZone = positiveDeadZone * -1;
        
        if ((x < positiveDeadZone) && (y < positiveDeadZone)){
            return 0;
        }
        else if ((x > negativeDeadZone) && (y > negativeDeadZone)) {
            return 0;
        }
        else {
            return deg;
        }
    }

    public double getDrivePower(){
        double x = joystick.getX();
        double y = joystick.getY() * -1;
        
        double hyp = Math.sqrt(x*x + y*y);
        double hypClamp;

        hypClamp = MathUtil.clamp(hyp, -1, 1);
        
        return hypClamp;
    }
    
}
