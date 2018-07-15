package org.usfirst.frc.team694.robot;

import org.usfirst.frc.team694.client.Client.Mode;

import edu.wpi.first.wpilibj.Timer;

public class Server {

	// All that we send to the client
	Mode currentMode = Mode.NORMAL;

	// What the bot gives us.
	// We use this data to determine what mode we're in
	Data ourData;

	public void update(Data data) {
		ourData.update(data);
	}

	public class Data {
		public double joyStickGas; // -1 to 1 % power
		public double joyStickTurn; // -1 to 1 rotation
		public double angle; // From navX;

		private double lastAngle;
		private double lastTime;
		private Mode mode;

		public Data(double joyStickGas, double joyStickTurn, double angle) {
			this.joyStickGas = joyStickGas;
			this.joyStickTurn = joyStickTurn;
			this.angle = angle;

			mode = Mode.NORMAL;
		}

		public void update(Data data) {
			this.lastAngle = angle;
			this.lastTime = Timer.getFPGATimestamp();

			this.joyStickGas = data.joyStickGas;
			this.joyStickTurn = data.joyStickTurn;
			this.angle = data.angle;

			mode = calculateMode();

		}
		
		public void sendToClient() {
			//TODO: Fill me!
		}

		private double getAngularVelocity() {
			double dt = Timer.getFPGATimestamp() - lastTime;
			double dv = angle - lastAngle;
			return dv / dt;
		}

		public Mode calculateMode() {
			if (mode == Mode.NORMAL) {
				if (joyStickGas > 0.8)
					return Mode.GASGASGAS;
				else
					return Mode.NORMAL;
			}
			// TODO: Test and Find a good threshold
			System.out.println("ANGULAR VELOCITY: " + getAngularVelocity());
			if (Math.abs(getAngularVelocity()) > -1) {// TODO: Find threshold
				return Mode.DEJAVU;
			}
			return Mode.NORMAL;
		}
	}
}
