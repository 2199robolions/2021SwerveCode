package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;

public class Controls {

    private enum ControllerIDs {
        JOYSTICK(0), // TODO: Need actual values for these
        XBOXCONTROLLER(1); // TODO: Need actual values for these
        
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
    
}
