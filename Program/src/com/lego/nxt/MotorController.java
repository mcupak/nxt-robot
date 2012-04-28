package com.lego.nxt;

import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.DCMotor;
import lejos.robotics.RegulatedMotor;

/**
 * Controller of the motors.
 * 
 * Makes the motors collaborate in the favor of the movement.
 * 
 * @author mcupak
 *
 */
public class MotorController
{
	private int speed=20;
	private static int STEERING_SPEED = 80;
	
	private static MotorController instance = null;

	// motor in the front
	private RegulatedMotor steeringMotor = null;

	// motors in the back:
	// left motor:
	private DCMotor motorB = null;
	// right motor:
	private DCMotor motorC = null;
	
	private boolean moving = false;

	/**
	 * direction 1 = right 0 = straight -1 = left
	 */
	private int direction = 0;

	protected MotorController() {
	}

	public static MotorController getInstance() {
		if (instance == null) {
			instance = new MotorController();
			instance.init();
		}
		return instance;
	}

	private void init() {
		MotorPort portA = MotorPort.getInstance(0);
		steeringMotor = new NXTRegulatedMotor(portA);
		steeringMotor.stop();
		MotorPort portB = MotorPort.getInstance(1);
		motorB = new NXTMotor(portB);
		motorB.stop();
		MotorPort portC = MotorPort.getInstance(2);
		motorC = new NXTMotor(portC);
		motorC.stop();
		steeringMotor.setSpeed(STEERING_SPEED);
		motorB.setPower(speed);
		motorC.setPower(speed);
	}

	public void forward() {
		motorB.backward();
		motorC.backward();
		moving = true;
	}

	public void backward() {
		motorB.forward();
		motorC.forward();
		moving = true;
	}

	public void stop() {
		//motorB.stop();
		//motorC.stop();
		motorB.flt();
		motorC.flt();
		moving = false;
	}

	/**
	 * Set direction.
	 * 
	 * @param d
	 *            -2 = full left -1 = half left 0 = straight 1 = half right 2 =
	 *            full right
	 */
	public void setDirection(int d) {
		if (d < -2) {
			d = -2;
		}
		if (d > 2) {
			d = 2;
		}
		int diff = d - direction;
		direction = d;
		steer(diff);
	}

	public Boolean isMoving() {
		//return Motor.B.isMoving();
		return moving;
	}

	private void steer(int q) {
		steeringMotor.rotate(q * (-15));
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
		// update?
		if (motorB != null && motorC != null) {
			motorB.setPower(speed);
			motorC.setPower(speed);
		}
	}
}
