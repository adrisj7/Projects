package core;

import krpc.client.RPCException;
import krpc.client.services.SpaceCenter;

public class PIDController {

	public enum PIDTimeMode {
		TIME_TICKS,    // Based on how fast you update the data
		TIME_REAL,     // Based on system time
		TIME_KSP,      // Based on Kerbal Time
		TIME_SPECIFIED // Based on a given delta time
	}
	private double kP, kI, kD;
	private double integralMin, integralMax;

	private PIDTimeMode mode;

	private double target;
	private double prevDelta;

	private long prevTime;
	private double prevTimeSpaceCenter;

	private double delta, derivative, integral;

	public PIDController(double kP, double kI, double kD, double target, PIDTimeMode mode) {
		this.kP = kP;
		this.kI = kI;
		this.kD = kD;
		this.target = target;
		this.mode = mode;

		integralMin = Double.MIN_VALUE;
		integralMax = Double.MAX_VALUE;

		prevDelta = target;
		integral = derivative = delta = 0;
	}

	public PIDController(double kP, double kI, double kD, double kImin, double kImax, double target, PIDTimeMode mode) {
		this(kP, kI, kD, target, mode);

		this.integralMin = kImin;
		this.integralMax = kImax;
	}

	public void init() {
		if (mode == PIDTimeMode.TIME_REAL) {
			prevTime = System.currentTimeMillis();
		}
	}

	// Pass a target value, and the parameters are updated
	public void update(double value) {
		delta = value - target;

		switch(mode) {
			case TIME_TICKS:
				integral += delta;
				derivative = target - prevDelta;
				break;
			case TIME_REAL:
				long nowTime = System.currentTimeMillis();
				long dtime = nowTime - prevTime;

				integral += delta / (double)dtime;
				derivative = (target - prevDelta) / (double)dtime;

				prevTime = nowTime;
				break;
			case TIME_KSP:
				System.err.println("Incorrect usage of TIME_KSP update! Use the update function with 2 parameters, one with a SpaceCenter!");
				System.exit(0);				
				break;
			case TIME_SPECIFIED:
				System.err.println("Incorrect usage of TIME_SPECIFIED update! Use the update function with 2 parameters, both doubles!");
				System.exit(0);
				break;
		}

		if (integral < integralMin) integral = integralMin;
		if (integral > integralMax) integral = integralMax;

		prevDelta = target;
	}

	// Same as update, but specifying dtime
	public void update(double value, double dtime) {
		if (mode == PIDTimeMode.TIME_SPECIFIED) {
			delta = value - target;
			integral += delta / dtime;
			derivative = (delta - prevDelta) / dtime;
			prevDelta = target;
		} else {
			System.err.println("Incorrect usage of 2 variable dtime update! Must specify that we're in TIME_SPECIFIED mode!");
			System.exit(0);
		}

		if (integral < integralMin) integral = integralMin;
		if (integral > integralMax) integral = integralMax;
	}

	// Same as before, but using our KSP space center to keep track of time
	public void update(double value, SpaceCenter spaceCenter) throws RPCException {
		if (mode == PIDTimeMode.TIME_KSP) {
			double nowTime = spaceCenter.getUT();
			double dtime = nowTime - prevTimeSpaceCenter;
			if (prevTimeSpaceCenter == 0) {
				dtime = 0;
			}
			delta = value - target;
			if (dtime != 0) {
				integral += delta / dtime;
				derivative = (delta - prevDelta) / dtime;
			}
			prevTimeSpaceCenter = nowTime;
			prevDelta = delta;
		} else {
			System.err.println("Incorrect usage of 2 variable SpaceCenter update! Must specify that we're in TIME_KSP mode!");
			System.exit(0);
		}
		if (integral < integralMin) integral = integralMin;
		if (integral > integralMax) integral = integralMax;
	}

	// Get value of the pid loop
	public double getValue() {
		return kP * delta + kI * integral + kD * derivative;
	}

	// Reset our I accumulator, cause this thing goes wack
	public void resetIAccum() {
		integral = 0;
	}

	public void setTarget(double target) {
		this.target = target;
	}

	public void setPID(double kP, double kI, double kD) {
	    this.kP = kP;
	    this.kI = kI;
	    this.kD = kD;
	}

	public double getTarget() {
		return target;
	}

	public double tempGetDPart() {
		return delta;
	}
}
