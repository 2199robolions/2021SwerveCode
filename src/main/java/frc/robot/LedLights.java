/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Spark;

/**
 * Add your docs here.
 */
public class LedLights {

	// VARIABLES
	private Spark ledController;

	// CONSTANTS
	private final int LED_PWM_CHANNEL = 0;

	// Constructor
	public LedLights() {
		ledController = new Spark(LED_PWM_CHANNEL);
	}

	/**
	 * DEFAULT
	 */
	public void defaultMode(String color) {
		switch (color) {
			case "Red":
				//This is for when we are one the red alliance 
				redAlliance();
				break;
			case "Blue":
				//This is for when we are one the blue alliance
				blueAlliance();
				break;
			default:
				//Sparkle blue on gold.
				ledController.set(.53);
				break;
		}
	}

	public void redAlliance(){
		// Heartbeat Red
		ledController.set(-0.25);
	}

	public void blueAlliance(){
		// Heartbeat Blue
		ledController.set(-0.23);
	}

	/**
	 * AUTO MODES
	 */
	public void autoMode() {
		// Solid color set to Aqua.
		ledController.set(.81);
	}

	public void autoModeFinished() {
		// Solid color set to Gold
		ledController.set(.67);
	}

	/**
	 * SHOOTER
	 */
	public void shooterReady() {
		// Heart Beat Red
		ledController.set(-.25);
	}

	public void onTarget() {
		// Solid color set to Green.
		ledController.set(.77);
	}

	/**
	 * LIMELIGHT ERROR CODES
	 */
	public void limelightFinished() {
		// Solid Green.
		ledController.set(.77);
	}

	public void limelightAdjusting() {
		//Solid Yellow
		ledController.set(.69);
	}

	public void limelightNoValidTarget() {
		// Solid Red.
		ledController.set(.61);
	}

} // End of the Ledligths Class