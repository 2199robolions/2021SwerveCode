/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import frc.robot.Conveyer.ConveyerState;
import frc.robot.Grabber.GrabberDirection;
//import jdk.nashorn.internal.ir.BreakableNode;

public class Auto {
	//Variables
	private int          step;
	private long         startMs; 
	private boolean      firstTime = true;

	// Object creation
	private LedLights   led;
	private Wheels      wheels;
	private Shooter     shooter;
	private Conveyer    conveyer;
	private Grabber     grabber;
	private ColorWheel  colorWheel;

	private final int TEST_DELAY = 1000;
	private final int SHOOT_TIME = 5000;


	/**
	 * CONTRUCTOR
	 */
	public Auto (LedLights ledLights, Wheels wheels, Shooter shooter, Conveyer conveyer, Grabber grabber, ColorWheel colorwheel) {
		led             = ledLights;
		this.wheels     = wheels;
		this.shooter    = shooter;
		this.conveyer   = conveyer;
		this.grabber    = grabber;
		this.colorWheel = colorwheel;

		step = 1;
	}


	/** 
	 * For the 2021 AutoNav Challenge: Uses a different rotate function than the previous autoNav() function (see above), revolving around a point outside of the robot body.
	*/
	public int autoSlalom() {

		if (firstTime == true) {
			step = 1;
			firstTime = false;
		}

		int status = Robot.CONT;

		switch(step) {
			case 1:
				status = wheels.forward(1, 0);
				break;
			case 2:
				status = wheels.circle(-45, false, true, 0);
				break;
			case 3:
				status = wheels.forward(4.5, -45);
				break;
			case 4:
				status = wheels.circle(7, true, true, 0);
				break;
			case 5:
				status = wheels.forward(12.25, 7, -0.7);
				break;
			case 6:
				status = wheels.circle(60, true, true, 1);
				break;
			case 7:
				status = wheels.forward(2, 52);
				break;
			case 8:
				status = wheels.circle(115, false, true, 1);
				break;
			case 9:
				status = wheels.forward(1.5, 115);
				break;
			case 10:
				status = wheels.circle(-169.9, true, true, 1);
				break;
			case 11:
				status = wheels.forward(9.3, 169, -0.6);
				break;
			case 12:
				status = wheels.circle(-135, true, true, 1);
				break;
			case 13:
				status = wheels.forward(3.5, -135);
				break;
			default:
				firstTime = true;
				return Robot.DONE;
		}

		if ((status == Robot.DONE) || (status == Robot.FAIL)) {
			step = step + 1;
			System.out.println("Entering step: " + step);
		}

		return Robot.CONT;
	}

	//Slalom
	//13.3 seconds
	public int autoSlalomFullSpeed() {

		if (firstTime == true) {
			step = 1;
			firstTime = false;
		}

		int status = Robot.CONT;

		switch(step) {
			case 1:
				status = wheels.forwardFullSpeed(1, 0);
				break;
			case 2:
				status = wheels.circle(-45, false, true, 0);
				break;
			case 3:
				status = wheels.forwardFullSpeed(5.0, -45);
				break;
			case 4:
				status = wheels.circle(-3.5, true, true, 0);
				break;
			case 5:
				status = wheels.forwardFullSpeed(8.6, -3.5);
				break;
			case 6:
				status = wheels.circle(60, true, true, 0);
				break;
			case 7:
				status = wheels.forwardFullSpeed(2, 52);
				break;
			case 8:
				status = wheels.circle(115, false, true, 0);
				break;
			case 9:
				status = wheels.forwardFullSpeed(1.5, 115);
				break;
			case 10:
				status = wheels.circle(169.9, true, true, 0);
				break;
			case 11:
				status = wheels.forwardFullSpeed(10.4, -173);
				break;
			case 12:
				status = wheels.circle(-135, true, true, 0);
				break;
			case 13:
				status = wheels.forwardFullSpeed(3.5, -135);
				break;
			default:
				firstTime = true;
				return Robot.DONE;
		}

		if ((status == Robot.DONE) || (status == Robot.FAIL)) {
			step = step + 1;
			System.out.println("Entering step: " + step);
		}

		return Robot.CONT;
	}

	//Slalom
	//11.0 seconds
	public int autoSlalomSuperSpeed() {

		if (firstTime == true) {
			step = 1;
			firstTime = false;
		}

		int status = Robot.CONT;

		switch(step) {
			case 1:
				status = wheels.forwardFullSpeed(1, 0);
				break;
			case 2:
				status = wheels.circleFast(-45, false, true, 0);
				break;
			case 3:
				status = wheels.forwardFullSpeed(3.7, -45);
				break;
			case 4:
				status = wheels.circleFast(-10.0, true, true, 0);
				break;
			case 5:
				status = wheels.forwardFullSpeed(10.0, 0.0);
				break;
			case 6:
				status = wheels.circleFast(60, true, true, 0);
				break;
			case 7:
				status = wheels.forwardFullSpeed(2, 52);
				break;
			case 8:
				status = wheels.circleFast(115, false, true, 0);
				break;
			case 9:
				status = wheels.forwardFullSpeed(1.5, 115);
				break;
			case 10:
				status = wheels.circleFast(165, true, true, 0);
				break;
			case 11:
				status = wheels.forwardFullSpeed(10.4, 175);
				break;
			case 12:
				status = wheels.circle(-135, true, true, 0);
				break;
			case 13:
				status = wheels.forwardFullSpeed(3.5, -135);
				break;
			default:
				firstTime = true;
				return Robot.DONE;
		}

		if ((status == Robot.DONE) || (status == Robot.FAIL)) {
			step = step + 1;
			System.out.println("Entering step: " + step);
		}

		return Robot.CONT;
	}


	//Slalom
	//11.0 seconds
	public int autoSlalomSuperDuperSpeed() {

		if (firstTime == true) {
			step = 1;
			firstTime = false;
		}

		int status = Robot.CONT;

		switch(step) {
			case 1:
				status = wheels.forwardFullSpeed(0.75, 0, 0);
				break;
			case 2:
				status = wheels.circleSuperFast(-45, false, true, 0);
				break;
			case 3:
				status = wheels.forwardFullSpeed(3.5, -45, 1.5);
				break;
			case 4:
				status = wheels.circleSuperFast(-27.0, true, true, 0);
				break;
			case 5:
				status = wheels.forwardFullSpeed(10.6, 3.0, 1.5);
				break;
			case 6:
				status = wheels.circleSuperFast(40, true, true, 0);
				break;
			case 7:
				status = wheels.forwardFullSpeed(1.8, 52, 0);
				break;
			case 8:
				status = wheels.circleSuperFast(115, false, true, 0);
				break;
			case 9:
				status = wheels.forwardFullSpeed(0.75, 90, 0);
				break;
			case 10:
				status = wheels.circleSuperFast(150, true, true, 0);
				break;
			case 11:
				status = wheels.forwardFullSpeed(10.7, 175, 1.5);
				break;
			case 12:
				status = wheels.circleSuperFast(-150, true, true, 0);
				break;
			case 13:
				status = wheels.forwardFullSpeed(4.0, -150, 1.5);
				break;
			default:
				firstTime = true;
				return Robot.DONE;
		}

		if ((status == Robot.DONE) || (status == Robot.FAIL)) {
			step = step + 1;
			System.out.println("Entering step: " + step);
		}

		return Robot.CONT;
	}

	/**
	 * The auto program that we will be using for the May 20th Demo
	 * @param galacticSearchPath
	 * @return Robot Status
	 */
	public int may20thDemo() {

		if (firstTime == true) {
			step = 1;
			firstTime = false;
		}

		int status = Robot.CONT;

		switch(step){
			case 1:
				grabber.deployRetract();
				grabber.grabberDirection(GrabberDirection.FORWARD);
				status = Robot.DONE;
				break;
			case 2:
				status = wheels.forward(5, -5);
				break;
			case 3:
				conveyer.autoHorizontalControl();
				status = wheels.circleMedium(180, false, true, 0);
				break;
		/*	case 3:
				status = wheels.forward(5, 180);
				break;
			case 4:
				conveyer.autoHorizontalControl();
				status = wheels.circle(-50, true, true, 0);
				break;
			case 5:
				conveyer.autoHorizontalControl();
				status = wheels.forward(7, -50);
				break;*/


			default:
				firstTime = true;
				return Robot.DONE;
		}
		
		if ((status == Robot.DONE) || (status == Robot.FAIL)) {
			step ++;
			System.out.println("Entering step: " + step);
		}

		return Robot.CONT;
	}




	/*Barrel Race
	public int autoNavCircle() {

		if (firstTime == true) {
			step = 1;
			firstTime = false;
		}

		int status = Robot.CONT;

		switch(step) {
			case 1:
				status = wheels.forward(9.5, 0);
				break;
			case 2:
				status = wheels.circle(-5.0, true, true, 1);
				break;
			case 3:
				status = wheels.forward(7, -12.5);
				break;
			case 4:
				status = wheels.circle(45, false, true, 1);
				break;
			case 5:
				status = wheels.forward(7.25, 45);
				break;
			case 6:
				status = wheels.circle(-169, false, true, 1);
				break;
			case 7:
				status = wheels.forward(21.0, 169.9, -1.0);
				break;
			default:
				firstTime = true;
				return Robot.DONE;
		}

		if ((status == Robot.DONE) || (status == Robot.FAIL)) {
			step = step + 1;
			System.out.println("Entering step: " + step);
		}

		return Robot.CONT;
	}

	//Barrel Race
	//17.5 seconds
	public int autoBarrelFullSpeed() {

		if (firstTime == true) {
			step = 1;
			firstTime = false;
		}

		int status = Robot.CONT;

		switch(step) {
			case 1:
				status = wheels.forwardFullSpeed(9.5, 0);
				break;
			case 2:
				status = wheels.circle(-5.0, true, true, 1);
				break;
			case 3:
				status = wheels.forwardFullSpeed(7, -12.5);
				break;
			case 4:
				status = wheels.circle(45, false, true, 0);
				break;
			case 5:
				status = wheels.forwardFullSpeed(7.1, 45);
				break;
			case 6:
				status = wheels.circle(-169, false, true, 0);
				break;
			case 7:
				status = wheels.forward(17.0, 169.9, -1.0);
				break;
			default:
				firstTime = true;
				return Robot.DONE;
		}

		if ((status == Robot.DONE) || (status == Robot.FAIL)) {
			step = step + 1;
			System.out.println("Entering step: " + step);
		}

		return Robot.CONT;
	}
*/
	//Barrel Race
	//13.0 seconds
	public int autoBarrelSuperSpeed() {

		if (firstTime == true) {
			step = 1;
			firstTime = false;
		}

		int status = Robot.CONT;

		switch(step) {
			case 1:
				status = wheels.forwardFullSpeed(9.5, 0, 1.5); //Adding 1.5 ft param to decrease slowing down distance
				break;
			case 2:
				status = wheels.circleFast(-20.0, true, true, 0);
				break;
			case 3:
				status = wheels.forwardFullSpeed(7.9, -20.0, 1.5);
				break;
			case 4:
				status = wheels.circleFast(49, false, true, 0);
				break;
			case 5:
				status = wheels.forwardFullSpeed(6.75, 49, 1.5);//7.25
				break;
			case 6:
				status = wheels.circleFast(-170, false, true, 0);//-169.9
				break;
			case 7:
				status = wheels.forward(18.6, 175, -1.0); //169.9 yes they're opposite
				break;
			default:
				firstTime = true;
				return Robot.DONE;
		}

		if ((status == Robot.DONE) || (status == Robot.FAIL)) {
			step = step + 1;
			System.out.println("Entering step: " + step);
		}

		return Robot.CONT;
	}

	public int autoBarrelSuperDuperSpeed() {

		if (firstTime == true) {
			step = 1;
			firstTime = false;
		}

		int status = Robot.CONT;

		switch(step) {
			case 1:
				status = wheels.forwardFullSpeed(8.6, -2, 1.5); //Adding 1.5 ft param to decrease slowing down distance
				break;
			case 2:
				status = wheels.circleSuperFast(-40.0, true, true, 0);
				break;
			case 3:
				status = wheels.forwardFullSpeed(9.7, -4.5, 1.5);
				break;
			case 4:
				status = wheels.circleSuperFast(58, false, true, 0);
				break;
			case 5:
				status = wheels.forwardFullSpeed(8.2, 58, 1.5);//7.25
				break;
			case 6:
				status = wheels.circleSuperFast(-165, false, true, 0);//-169.9
				break;
			case 7:
				status = wheels.forward(18.6, -170, -1.0); //169.9 yes they're opposite
				break;
			default:
				firstTime = true;
				return Robot.DONE;
		}

		if ((status == Robot.DONE) || (status == Robot.FAIL)) {
			step = step + 1;
			System.out.println("Entering step: " + step);
		}

		return Robot.CONT;
	}

	
	


	//Bounce (16.4 seconds)
	public int autoBounce() {

		if (firstTime == true) {
			step = 1;
			firstTime = false;
		}

		int status = Robot.CONT;

		switch(step) {
			case 1:
				status = wheels.forward(1, 0);
				break;
			case 2:
				status = wheels.circle(-110.0, false, true, 0);
				break;
			case 3:
				status = wheels.forward(-9, -110);
				break;
			case 4:
				status = wheels.circle(105, false, false, 1);
				break;
			case 5:
				status = wheels.forward(-8.6, 105);
				break;
			case 6:
				status = wheels.forward(6.7, 70);
				break;
			case 7:
				status = wheels.circle(0, false, true, 0);
				break;
			case 8:
				status = wheels.forward(1, 0);
				break;
			case 9:
				status = wheels.circle(-80, false, true, 1);
				break;
			case 10:
				status = wheels.forward(5.35, -100);
				break;
			/*case 11:
				status = wheels.circle(-135, false, false, 1);
				break;*/
			case 11:
				status = wheels.forward(-4.75, -135);
				break;
			default:
				firstTime = true;
				return Robot.DONE;
		}

		if ((status == Robot.DONE) || (status == Robot.FAIL)) {
			step = step + 1;
			System.out.println("Entering step: " + step);
		}

		return Robot.CONT;
	}

	//Bounce (12.9 seconds)
	public int autoBounceFullSpeed() {

		if (firstTime == true) {
			step = 1;
			firstTime = false;
		}

		int status = Robot.CONT;

		switch(step) {
			case 1:
				status = wheels.forwardFullSpeed(1, 0);
				break;
			case 2:
				status = wheels.circle(-110.0, false, true, 0);
				break;
			case 3:
				status = wheels.forwardFullSpeed(-9.4, -109);
				break;
			case 4:
				status = wheels.circle(112, false, false, 0);
				break;
			case 5:
				status = wheels.forwardFullSpeed(-8.3, 118);
				break;
			case 6:
				status = wheels.forwardFullSpeed(6.2, 68);
				break;
			case 7:
				status = wheels.circle(0, false, true, 0);
				break;
			case 8:
				status = wheels.forwardFullSpeed(1, 0);
				break;
			case 9:
				status = wheels.circle(-80, false, true, 0);
				break;
			case 10:
				status = wheels.forwardFullSpeed(6.7, -67);
				break;
			case 11:
				status = wheels.forwardFullSpeed(-6.3, -127);
				break;
			/*case 12:
				grabber.grabberDirection(Grabber.GrabberDirection.FORWARD);
				grabber.deployRetract();
				break;*/
			default:
				firstTime = true;
				return Robot.DONE;
		}

		if ((status == Robot.DONE) || (status == Robot.FAIL)) {
			step = step + 1;
			System.out.println("Entering step: " + step);
		}

		return Robot.CONT;
	}


	//Bounce (11.2 seconds)
	public int autoBounceSuperSpeed() {

		if (firstTime == true) {
			step = 1;
			firstTime = false;
		}

		int status = Robot.CONT;

		switch(step) {
			case 1:
				status = wheels.forwardFullSpeed(1, 0);
				break;
			case 2:
				status = wheels.circleFast(-85.0, false, true, 0);
				break;
			case 3:
				status = wheels.forwardFullSpeed(-8.7, -110);
				break;
			case 4:
				status = wheels.circleSlow(120, false, false, 0);
				break;
			case 5:
				status = wheels.forwardFullSpeed(-9.2, 100);
				break;
			case 6:
				status = wheels.forwardFullSpeed(6.4, 65);
				break;
			case 7:
				status = wheels.circleFast(10, false, true, 0);
				break;
			case 8:
				status = wheels.forwardFullSpeed(1.5, 0);
				break;
			case 9:
				status = wheels.circleFast(-80, false, true, 0);
				break;
			case 10:
				status = wheels.forwardFullSpeed(7.0, -58);
				break;
			case 11:
				status = wheels.forwardFullSpeed(-8.0, -127);
				break;
			default:
				firstTime = true;
				return Robot.DONE;
		}

		if ((status == Robot.DONE) || (status == Robot.FAIL)) {
			step = step + 1;
			System.out.println("Entering step: " + step);
		}

		return Robot.CONT;
	}


	public int autoGalacticSearchARed() {

		if (firstTime == true) {
			step = 1;
			firstTime = false;
		}

		int status = Robot.CONT;

		switch(step) {
			case 1: //Start with grabber retracted
				grabber.deployRetract();
				grabber.grabberDirection(GrabberDirection.FORWARD);
				conveyer.autoHorizontalControl();
				status = Robot.DONE;
				break;
			case 2:
				status = wheels.forwardFullSpeed(6.9, 38);//5.5 old
				conveyer.autoHorizontalControl();
				break;
			case 3:
				status = wheels.circleFast(-80, false, true, 0);
				conveyer.autoHorizontalControl();
				break;
			case 4:
				status = wheels.forwardFullSpeed(4.5, -75);
				conveyer.autoHorizontalControl();
				break;
			case 5:
				status = wheels.circle(180, false, false, 0);//-150
				conveyer.manualHorizontalControl(ConveyerState.FORWARD);
				break;
			case 6:
				status = wheels.forwardFullSpeed(-12.25, 180);
				conveyer.manualHorizontalControl(ConveyerState.OFF);
				break;
			default:
				conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);
				firstTime = true;
				return Robot.DONE;
		}

		if ((status == Robot.DONE) || (status == Robot.FAIL)) {
			step = step + 1;
			System.out.println("Entering step: " + step);
		}

		return Robot.CONT;
	}


	public int autoGalacticSearchBRed() {

		if (firstTime == true) {
			step = 1;
			firstTime = false;
		}

		int status = Robot.CONT;

		switch(step) {
			case 1: //Start with grabber retracted
				grabber.deployRetract();
				grabber.grabberDirection(GrabberDirection.FORWARD);
				conveyer.autoHorizontalControl();
				status = Robot.DONE;
				break;
			case 2:
				status = wheels.forwardFullSpeed(3.0, 20);//5.5 old
				conveyer.autoHorizontalControl();
				break;
			case 3:
				status = wheels.circleFast(40, true, true, 0);
				conveyer.autoHorizontalControl();
				break;
			case 4:
				status = wheels.forwardFullSpeed(4.5, 40);
				conveyer.autoHorizontalControl();
				break;
			case 5:
				status = wheels.circleFast(-35, false, true, 0);
				conveyer.manualHorizontalControl(ConveyerState.FORWARD);
				break;
			case 6:
				status = wheels.forwardFullSpeed(4, -35);
				conveyer.manualHorizontalControl(ConveyerState.OFF);
				break;
			case 7:
				status = wheels.circleFast(-132, false, false, 0);
				conveyer.autoHorizontalControl();
				break;
			case 8:
				status = wheels.forwardFullSpeed(-14, 180);
				break;
			default:
				conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);
				firstTime = true;
				return Robot.DONE;
		}

		if ((status == Robot.DONE) || (status == Robot.FAIL)) {
			step = step + 1;
			System.out.println("Entering step: " + step);
		}

		return Robot.CONT;
	}


	public int autoGalacticSearchABlue() {

		if (firstTime == true) {
			step = 1;
			firstTime = false;
		}

		int status = Robot.CONT;

		switch(step) {
			case 1: //Start with grabber retracted
				grabber.deployRetract();
				grabber.grabberDirection(GrabberDirection.FORWARD);
				conveyer.autoHorizontalControl();
				status = Robot.DONE;
				break;
			case 2:
				status = wheels.forwardFullSpeed(9, 0);//5.5 old
				conveyer.autoHorizontalControl();
				break;
			case 3:
				status = wheels.circleFast(-65, false, true, 0);
				conveyer.autoHorizontalControl();
				break;
			case 4:
				status = wheels.forwardFullSpeed(4.5, -65);
				conveyer.autoHorizontalControl();
				break;
			case 5:
				status = wheels.circle(20, true, true, 0);//-150
				conveyer.manualHorizontalControl(ConveyerState.FORWARD);
				break;
			case 6:
				status = wheels.forwardFullSpeed(4, 20);
				conveyer.manualHorizontalControl(ConveyerState.OFF);
				break;
			case 7:
				grabber.deployRetract();
				status = Robot.DONE;
				break;
			case 8:
				conveyer.manualHorizontalControl(ConveyerState.FORWARD);
				status = wheels.forwardFullSpeed(5.0, 0);//4.5
				break;
			default:
				conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);
				firstTime = true;
				return Robot.DONE;
		}

		if ((status == Robot.DONE) || (status == Robot.FAIL)) {
			step = step + 1;
			System.out.println("Entering step: " + step);
		}

		return Robot.CONT;
	}

	public int autoGalacticSearchBBlue() {

		if (firstTime == true) {
			step = 1;
			firstTime = false;
		}

		int status = Robot.CONT;

		switch(step) {
			case 1: //Start with grabber retracted
				grabber.deployRetract();
				grabber.grabberDirection(GrabberDirection.FORWARD);
				conveyer.autoHorizontalControl();
				status = Robot.DONE;
				break;
			case 2:
				status = wheels.forwardFullSpeed(10, -19);
				conveyer.autoHorizontalControl();
				break;
			case 3:
				status = wheels.circleFast(-38, false, true, 0);
				conveyer.autoHorizontalControl();
				break;
			case 4:
				status = wheels.forwardFullSpeed(4.5, -38);
				conveyer.autoHorizontalControl();
				break;
			case 5:
				status = wheels.circle(25, true, true, 0);//-150
				conveyer.manualHorizontalControl(ConveyerState.FORWARD);
				break;
			case 6:
				status = wheels.forwardFullSpeed(6, 25);
				conveyer.manualHorizontalControl(ConveyerState.OFF);
				break;
			case 7:
				grabber.deployRetract();
				status = Robot.DONE;
				break;
			case 8:
				conveyer.manualHorizontalControl(ConveyerState.FORWARD);
				status = wheels.forwardFullSpeed(4.0, 15);//4.5
				break;
			default:
				conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);
				firstTime = true;
				return Robot.DONE;
		}

		if ((status == Robot.DONE) || (status == Robot.FAIL)) {
			step = step + 1;
			System.out.println("Entering step: " + step);
		}

		return Robot.CONT;
	}



	public int autoCircleTest() {

		if (firstTime == true) {
			step = 1;
			firstTime = false;
		}

		int status = Robot.CONT;

		switch(step) {
			case 1:
				status = wheels.circle(0, false, true, 1);
				break;
			default:
				firstTime = true;
				return Robot.DONE;
		}

		if ((status == Robot.DONE) || (status == Robot.FAIL)) {
			step = step + 1;
			System.out.println("Entering step: " + step);
		}

		return Robot.CONT;
	}

	
	
	/**
	 * left right or center of target simple shoot and move forward	
	 */
	public int leftRightCenterSimpleForwardAuto( int delay ) {
		int status = Robot.CONT;
		long currentMs = System.currentTimeMillis();
		long delayMs = delay * 1000;

		switch(step) {
			// Starts Auto Program
			case 1:
				led.autoMode();
				status = delay(delayMs);
				break;
			case 2:
				status = wheels.limelightPIDTargeting(Wheels.TargetPipeline.TEN_FOOT);
				break;
			case 3:
				startMs = System.currentTimeMillis();
				status = Robot.DONE;
				break;
			case 4:
				// Start Conveyer and Shooter
				shooter.manualShooterControl( Shooter.ShootLocation.TEN_FOOT );

				if (shooter.shooterReady() == true) {
					// Shooter at required RPM, Turn Conveyers On
					conveyer.manualHorizontalControl(Conveyer.ConveyerState.FORWARD);
					conveyer.manualVerticalControl(  Conveyer.ConveyerState.FORWARD);
				}
				else {
					// Shooter below required RPM, Turn Conveyers Off
					conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);
					conveyer.manualVerticalControl(  Conveyer.ConveyerState.OFF);
				}

				// Allow time for the shooter to Shoot
				if ((currentMs - startMs) > SHOOT_TIME ) {
					status = Robot.DONE;
				}
				break;
			case 5:
				shooter.manualShooterControl( Shooter.ShootLocation.OFF );
				conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);
				conveyer.manualVerticalControl(  Conveyer.ConveyerState.OFF);
				status = Robot.DONE;
				break;
			case 6:
				status = wheels.rotate(0.0);
				break;
			case 7:
				status = wheels.forward(3.0, 0.0);
				break;
			default:
				// Everything Off
				conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);
				conveyer.manualVerticalControl(  Conveyer.ConveyerState.OFF);
				shooter.manualShooterControl( Shooter.ShootLocation.OFF );

				// Set Step to 1
				step = 1;

				// Auto Program Finished
				led.autoModeFinished();

				return Robot.DONE;
		}

		if ((status == Robot.DONE)   || (status == Robot.FAIL)) {
			step = step + 1;
		}

		return Robot.CONT;
	}





	/**
	 * left of the field
	 */
	public int leftAuto( int delay ) {
		int status = Robot.CONT;
		long currentMs = System.currentTimeMillis();
		long delayMs = delay * 1000;

		switch(step) {
			// Starts Auto Program
			case 1:
				led.autoMode();
				status = delay(delayMs);
				break;
			case 2:
				// Rotate -5 degress to Assist Limelight
			 //   status = wheels.rotate( rotation );
				status = Robot.DONE;
				break;
			case 3:
				status = wheels.limelightPIDTargeting(Wheels.TargetPipeline.TEN_FOOT);
				break;
			case 4:
				startMs = System.currentTimeMillis();
				status = Robot.DONE;
				break;
			case 5:
				// Start Conveyer and Shooter
				shooter.manualShooterControl( Shooter.ShootLocation.TEN_FOOT );

				if (shooter.shooterReady() == true) {
					// Shooter at required RPM, Turn Conveyers On
					conveyer.manualHorizontalControl(Conveyer.ConveyerState.FORWARD);
					conveyer.manualVerticalControl(  Conveyer.ConveyerState.FORWARD);
				}
				else {
					// Shooter below required RPM, Turn Conveyers Off
					conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);
					conveyer.manualVerticalControl(  Conveyer.ConveyerState.OFF);
				}

				// Allow time for the shooter to Shoot
				if ((currentMs - startMs) > SHOOT_TIME ) {
					status = Robot.DONE;
				}
				break;
			case 6:
				shooter.manualShooterControl( Shooter.ShootLocation.OFF );
				conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);
				conveyer.manualVerticalControl(  Conveyer.ConveyerState.OFF);
				status = Robot.DONE;
				break;
			case 7:
				status = wheels.forward( -3.0 , 0.0 );
				break;
			case 8:
				status = wheels.rotate(-180.0);
				break;
			case 9:
				grabber.deployRetract();
				status = Robot.DONE;
				break;
			default:
				// Everything Off
				conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);
				conveyer.manualVerticalControl(  Conveyer.ConveyerState.OFF);
				shooter.manualShooterControl( Shooter.ShootLocation.OFF );

				// Set Step to 1
				step = 1;

				// Auto Program Finished
				led.autoModeFinished();

				return Robot.DONE;
		}

		if ((status == Robot.DONE)   || (status == Robot.FAIL)) {
			step = step + 1;
		}

		return Robot.CONT;
	}


   /**
	 * Center Auto
	 */
	public int centerAuto( int delay ) {
		int status = Robot.CONT;
		long currentMs = System.currentTimeMillis();
		long delayMs = delay * 1000;

		switch(step) {
			// Starts Auto Program
			case 1:
				led.autoMode();
				status = delay(delayMs);
				break;
			case 2:
				// Rotate -5 degress to Assist Limelight
			   // status = wheels.rotate( rotation );
				status = Robot.DONE;
				break;
			case 3:
				status = wheels.limelightPIDTargeting(Wheels.TargetPipeline.TEN_FOOT);
				break;
			case 4:
				startMs = System.currentTimeMillis();
				status = Robot.DONE;
				break;
			case 5:
				status = Robot.CONT;

				// Start Conveyer and Shooter
				shooter.manualShooterControl( Shooter.ShootLocation.TEN_FOOT );

				if (shooter.shooterReady() == true) {
					// Shooter at required RPM, Turn Conveyers On
					conveyer.manualHorizontalControl(Conveyer.ConveyerState.FORWARD);
					conveyer.manualVerticalControl(  Conveyer.ConveyerState.FORWARD);
				}
				else {
					// Shooter below required RPM, Turn Conveyers Off
					conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);
					conveyer.manualVerticalControl(  Conveyer.ConveyerState.OFF);
				}

				// Allow time for the shooter to Shoot
				if ( (currentMs - startMs) > SHOOT_TIME ) {
					status = Robot.DONE;
				}
				break;
			case 6:
				shooter.manualShooterControl( Shooter.ShootLocation.OFF );
				conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);
				conveyer.manualVerticalControl(  Conveyer.ConveyerState.OFF);
				// Back Up 5 Feet
				status = wheels.forward( -2.0 , 0.0 );
				break;
			case 7:
				status = wheels.rotate(-90.0);
				break;
			case 8:
				status = wheels.forward(-6.0, -90.0);
				break;
			case 9:
				status = wheels.rotate(-180.0);
				break;
			case 10:
				grabber.deployRetract();
				status = Robot.DONE;
				break;
			default:
				// Everything Off
				conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);
				conveyer.manualVerticalControl(  Conveyer.ConveyerState.OFF);
				shooter.manualShooterControl( Shooter.ShootLocation.OFF );

				// Set Step to 1
				step = 1;

				// Auto Program Finished
				led.autoModeFinished();

				return Robot.DONE;
		}

		if ((status == Robot.DONE)   || (status == Robot.FAIL)) {
			step = step + 1;
		}

		return Robot.CONT;
	}


	/**
	 * Right bumper is 2ft. from wall
	 */
	public int rightAuto( int delay ) {
		int status = Robot.CONT;
		long currentMs = System.currentTimeMillis();
		long delayMs = delay * 1000;

		switch(step) {
			// Starts Auto Program
			case 1:
				led.autoMode();
				status = delay(delayMs);
				break;
			case 2:
				// Rotate -5 degress to Assist Limelight
			 //   status = wheels.rotate( rotation );
				status = Robot.DONE;
				break;
			case 3:
				status = wheels.limelightPIDTargeting(Wheels.TargetPipeline.TEN_FOOT);
				break;
			case 4:
				startMs = System.currentTimeMillis();
				status = Robot.DONE;
				break;
			case 5:
				// Start Conveyer and Shooter
				shooter.manualShooterControl( Shooter.ShootLocation.TEN_FOOT );

				if (shooter.shooterReady() == true) {
					// Shooter at required RPM, Turn Conveyers On
					conveyer.manualHorizontalControl(Conveyer.ConveyerState.FORWARD);
					conveyer.manualVerticalControl(  Conveyer.ConveyerState.FORWARD);
				}
				else {
					// Shooter below required RPM, Turn Conveyers Off
					conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);
					conveyer.manualVerticalControl(  Conveyer.ConveyerState.OFF);
				}

				if ((currentMs - startMs) > SHOOT_TIME ) {
					// Allow time for the shooter to Shoot
					status = Robot.DONE;
				}
				break;
			case 6:
				shooter.manualShooterControl( Shooter.ShootLocation.OFF );
				conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);
				conveyer.manualVerticalControl(  Conveyer.ConveyerState.OFF);
				status = Robot.DONE;
				break;
			case 7:
				status = wheels.rotate(-45.0);
				break;
			case 8:
				// Back Up 5 Feet
				status = wheels.forward( -1.0 , -45.0 );
				break;
			case 9:
				status = wheels.rotate(-180.0);
				break;
			case 10:
				grabber.deployRetract();
				status = Robot.DONE;
				break;
			case 11:
				status = wheels.forward(3.0, -180.0);
				break;
			default:
				// Everything Off
				conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);
				conveyer.manualVerticalControl(  Conveyer.ConveyerState.OFF);
				shooter.manualShooterControl( Shooter.ShootLocation.OFF );

				// Set Step to 1
				step = 1;

				// Auto Program Finished
				led.autoModeFinished();

				return Robot.DONE;
		}

		if ((status == Robot.DONE)   || (status == Robot.FAIL)) {
			step = step + 1;
		}

		return Robot.CONT;
	}

	/**
	 * Deploy Climber Pistons
	 */
	public int climberDeploy(Climber climber) {
		int status = Robot.CONT;

		/**
		 * Fire Middle
		 * Fire Bottom
		 * Fire Top
		 */
		switch(step) {
			case 1:
				climber.bottomArmUp();

				status = Robot.DONE;
				break;
			case 2:
				status = delay(0);
				break;
			case 3:
				climber.middleArmUp();

				status = Robot.DONE;
				break;
			case 4:
				status = delay(0);
				break;
			case 5:
				climber.topArmUp();

				status = Robot.DONE;
				break;
			default:
				step = 1;

				return Robot.DONE;
		}

		if (status == Robot.DONE) {
			step++;
		}

		return Robot.CONT;
	}

	/**
	 * 
	 */
	public int shooterTest() {
		int status = Robot.CONT;
		int time = 5000;

		switch(step) {
			case 1:
				shooter.testShoooter(0.6);
				status = delay(time);
				break;
			case 2:
				shooter.testShoooter(0.65);
				status = delay(time);
				break;
			case 3:
				shooter.testShoooter(0.7);
				status = delay(time);
				break;
			case 4:
				shooter.testShoooter(0.75);
				status = delay(time);
				break;
			case 5:
				shooter.testShoooter(0.8);
				status = delay(time);
				break;
			case 6:
				shooter.testShoooter(0.85);
				status = delay(time);
				break;
			case 7:
				shooter.testShoooter(0.9);
				status = delay(time);
				break;
			case 8:
				shooter.testShoooter(0.95);
				status = delay(time);
				break;
			case 9:
				shooter.testShoooter(1.0);
				status = delay(time);
				break;
			default:
				step = 1;
				return Robot.DONE;
		}

		if (status == Robot.DONE) {
			step ++;
		}

		return Robot.CONT;
	}

	/**
	 * Test All Motors and Pistons Individualy
	 */
	public int testAll() {
		int status = Robot.CONT;

		switch(step) {
			case 1:
				// Deploy Grabber Motor
				grabber.deployRetract();

				status = Robot.DONE;
				break;
			case 2:
				// Turn Grabber On
				grabber.grabberDirection(Grabber.GrabberDirection.FORWARD);

				status = Robot.DONE;
				break;
			case 3:
				// Wait for 1 Second
				status = delay(TEST_DELAY);
				break;
			case 4:
				// Set Grabber to Off
				grabber.grabberDirection(Grabber.GrabberDirection.OFF);

				// Start Horizotal Conveyer
				conveyer.manualHorizontalControl(Conveyer.ConveyerState.FORWARD);

				status = Robot.DONE;
			case 5:
				// Wait for 1 Second
				status = delay(TEST_DELAY);
				break;
			case 6:
				// Stop the Horizontal Conveyer
				conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);

				// Start the Vertical Conveyer
				conveyer.manualVerticalControl( Conveyer.ConveyerState.FORWARD);

				status = Robot.DONE;
				break;
			case 7:
				// Wait for 1 Second
				status = delay(TEST_DELAY);
				break;
			case 8:
				// Turn Vertical Conveyer Off
				conveyer.manualVerticalControl(Conveyer.ConveyerState.OFF);

				// Turn Shooter Motor 1 On
				shooter.enableShooterMotor1();

				status = Robot.DONE;
				break;
			case 9:
				// Wait for 1 Second
				status = delay(TEST_DELAY);
				break;
			case 10:
				// Stop Shooter Motor 1
				shooter.stopShooterMotor1();

				// Start Shooter Motor 2
				shooter.enableShooterMotor2();

				status = Robot.DONE;
				break;
			case 11:
				// Wait for 1 Second
				status = delay(TEST_DELAY);
				break;
			case 12:
				// Stop Shooter Motor 2
				shooter.stopShooterMotor2();

				// Run Both Motors
				shooter.manualShooterControl( Shooter.ShootLocation.HAIL_MARY );

				status = Robot.DONE;
			case 13:
				// Wait for 1 Second
				status = delay(TEST_DELAY);
				break;
			case 14:
				// Stop Shooter Motors
				shooter.manualShooterControl( Shooter.ShootLocation.OFF );

				// Left Side Wheels
				wheels.enableDriveMotors("left");

				status = Robot.DONE;
				break;
			case 15:
				status = delay(TEST_DELAY);
				break;
			case 16:
				// Left Wheels Off
				wheels.stopDriveMotors("left");

				// Right Side Wheels
				wheels.enableDriveMotors("right");
				
				status = Robot.DONE;
				break;
			case 17:
				// Wait for 1 Second
				status = delay(TEST_DELAY);
				break;
			case 18:
				// Right Side Wheels Off
				wheels.stopDriveMotors("right");

				// Both Wheels
				wheels.controllerDrive( 1.00 , 0.00 );

				status = Robot.DONE;
				break;
			case 19:
				// Wait for 1 Second
				status = delay(TEST_DELAY);
				break;
			case 20:
				// Wheels Off
				wheels.controllerDrive( 0.00 , 0.00 );

				// Limelight On
				wheels.changeLimelightLED(Wheels.LIMELIGHT_ON);

				status = Robot.DONE;
				break;
			case 21:
				// Wait for Half a Second
				status = delay(TEST_DELAY / 2);
				break;
			case 22:
				// Limelight Off
				wheels.changeLimelightLED(Wheels.LIMELIGHT_OFF);

				// Color Wheel Deploy
				colorWheel.deployRetract();

				status = Robot.DONE;
				break;
			case 23:
				// Spin Color Wheel 4 times
				status = colorWheel.colorWheelSpin();
				break;
			case 24:
				// Shift SuperShifter Gears to High
				wheels.gearShift();

				status = Robot.DONE;
				break;
			case 25:
				// Wait for Half a Second
				status = delay(TEST_DELAY / 2);
				break;
			case 26:
				// Shift SuperShifter Gears to Low
				wheels.gearShift();

				status = Robot.DONE;
				break;
			default:
				step = 1;

				// Reset All Pistons
				grabber.deployRetract();
				colorWheel.deployRetract();
				conveyer.forwardingRetract();

				// Auto Mode Finished
				led.autoModeFinished();

				return Robot.DONE;
		}

		if (status == Robot.DONE) {
			step++;
		}

		return Robot.CONT;
	}

	/**
	 * Delaying Program  delay is in milliseconds
	 */
	private int delay(long delayMsec) {
		long currentMs = System.currentTimeMillis();

		if (firstTime == true) {
			firstTime = false;
			startMs = System.currentTimeMillis();
		}

		if ( (currentMs - startMs) > delayMsec) {
			firstTime = true;
			return Robot.DONE;
		}
		else  {
			return Robot.CONT;
		}
	}

	/***************************************************
	 * 
	 * Test section
	 * 
	 ***************************************************/
	public int testAuto(Wheels wheels) {
		int status = Robot.CONT;
	
		switch (step) {
			case 1:  
				status = wheels.forward(1.0, 0.0);
				break;
			case 2:
				status = wheels.rotate( 90.0 );
				break;
			case 3:
				status = wheels.forward( 1.0, 90.0 );
				break;
			case 4:
				status = wheels.rotate( 180.0 );
				break;
			case 5:
				status = wheels.forward( 1.0, 180.0);
				break;
			default: 
				step = 1;

				// Auto Program Finished
				led.autoModeFinished();

				return Robot.DONE;
		}
	
		if (status == Robot.DONE) {
			step++;
		}
	
		return Robot.CONT;
	}

	public int testRotate(Wheels wheels){
		int status = Robot.CONT;
	
		switch (step) {
			case 1:  
				status = wheels.rotate( 180.0 );
				break;
			default: 
				step = 1;

				// Auto Program Finished
				led.autoModeFinished();

				return Robot.DONE;
		}
	
		if (status == Robot.DONE) {
			step++;
		}
	
		return Robot.CONT;
	}

// Right side of the field facing the target
public int rightAutoOld(int delay) {
	int status = Robot.CONT;
	long currentMs = System.currentTimeMillis();
	long delayMs = delay * 1000;

	switch (step) {
			// Starts Auto Program
		case 1:
			led.autoMode();
			status = delay(delayMs);
			break;
		// Rotate -5 degrees
		case 2:  
			status = wheels.rotate( -5.0 );
			break;
		// Limelight targeting 
		case 3:
			wheels.changeLimelightLED(Wheels.LIMELIGHT_ON);

			status = wheels.limelightPIDTargeting(Wheels.TargetPipeline.TEN_FOOT);

			
			break;
		// Turn the shooter on
		case 4:
			wheels.changeLimelightLED(Wheels.LIMELIGHT_OFF);

			shooter.manualShooterControl( Shooter.ShootLocation.TEN_FOOT );

			if (shooter.shooterReady() == true) {
				// Shooter at required RPM, Turn Conveyers On
				conveyer.manualHorizontalControl(Conveyer.ConveyerState.FORWARD);
				conveyer.manualVerticalControl(  Conveyer.ConveyerState.FORWARD);
			}
			else {
				// Shooter below required RPM, Turn Conveyers Off
				conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);
				conveyer.manualVerticalControl(  Conveyer.ConveyerState.OFF);
			}

			if (((currentMs - startMs) + delayMs) > (4.5 * 1000) ) {
				// Allow time for the shooter to Shoot
				status = Robot.DONE;
			}

			break;
		case 5:
			shooter.manualShooterControl( Shooter.ShootLocation.OFF );

			status = Robot.DONE;
			break;
		case 6:
			status = wheels.rotate( -90.0 );
			break;
		case 7:
			status = wheels.rotate( -180.0 );
			break;
		case 8:
			grabber.deployRetract();
			grabber.grabberDirection(Grabber.GrabberDirection.FORWARD);

			conveyer.forwardingRetract();

			status = Robot.DONE;
			break;
		case 9 :
			status = wheels.forward( 14.0 , -180.0 );

			conveyer.autoHorizontalControl();
			conveyer.autoVerticalControl();
			break;
		case 10:
			grabber.grabberDirection(Grabber.GrabberDirection.OFF);
			
			status = wheels.forward( -14.0 , -180.0 );
			break;
		case 11:
			status = wheels.rotate( -90.0 );
			break;
		case 12:
			status = wheels.rotate(0);

			wheels.changeLimelightLED(Wheels.LIMELIGHT_ON);
			break;
		case 13:
			wheels.changeLimelightLED(Wheels.LIMELIGHT_ON);
			status = wheels.limelightPIDTargeting(Wheels.TargetPipeline.TEN_FOOT);

			break;
		case 14:
			startMs = System.currentTimeMillis();
			status = Robot.DONE;
			break;
		case 15:
			shooter.manualShooterControl( Shooter.ShootLocation.TEN_FOOT );
			if (shooter.shooterReady() == true) {
				// Shooter at required RPM, Turn Conveyers On
				conveyer.manualHorizontalControl(Conveyer.ConveyerState.FORWARD);
				conveyer.manualVerticalControl(  Conveyer.ConveyerState.FORWARD);
			}
			else {
				// Shooter below required RPM, Turn Conveyers Off
				conveyer.manualHorizontalControl(Conveyer.ConveyerState.OFF);
				conveyer.manualVerticalControl(  Conveyer.ConveyerState.OFF);
			}                

			if ((currentMs - startMs) > SHOOT_TIME ) {
				// Allow time for the shooter to Shoot
				status = Robot.DONE;
			}

			break;
		default:
			shooter.manualShooterControl( Shooter.ShootLocation.OFF );

			// Set Step to 1
			step = 1;

			// Auto Program Finished
			led.autoModeFinished();

			return Robot.DONE;
	}

	if ((status == Robot.DONE)  || ( status == Robot.FAIL)) {
		step++;
	}

	return Robot.CONT;
}

 
	/**
	 * Default Auto 
	 */
	public int defaultAuto(int delay) {
		int status = Robot.CONT;
		long delayMs = delay * 1000;

		switch (step) {
			// Starts Auto Program
			case 1: 
				led.autoMode();

				// Delay
				status = delay(delayMs);
				break;
			case 2:
				status = wheels.forward( -3.0 , 0.0 );
				break;
			default:
				// Set Step to 1
				step = 1;

				// Auto Program Finished
				led.autoModeFinished();

				return Robot.DONE;
		}

		if (status == Robot.DONE) {
			step++;
		}

		return Robot.CONT;
	}



} // End of Auto class