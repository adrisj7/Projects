package util;

import com.ivan.xinput.XInputAxes;
import com.ivan.xinput.XInputButtons;
import com.ivan.xinput.XInputComponents;
import com.ivan.xinput.XInputDevice;
import com.ivan.xinput.XInputDevice14;
import com.ivan.xinput.exceptions.XInputNotLoadedException;

public class Gamepad {

	private XInputDevice device;

	private XInputComponents components;

	public Gamepad(int port) {

		try {
			if (XInputDevice14.isAvailable()) {
				device = XInputDevice14.getDeviceFor(port);
			} else if (XInputDevice.isAvailable()) {
				device = XInputDevice.getDeviceFor(port);
			}
		} catch (XInputNotLoadedException e) {
			System.out.println("Your goose is cooked");
			e.printStackTrace();
		}

		if (!device.isConnected()) {
			System.err.println("[Gamepad.java] BAD THING: Device at port " + port + " is NOT CONNECTED!");
			return;
		}
	}

	/**
	 * Call this to grab inputs
	 */
	public XInputComponents getComponents() {
		if (device.poll()) {
			components = device.getComponents();
		}
		if (components != null)
			return components;

		System.err.println("BAD STUFF: Components are null, this will probably crash!");
		return null;
	}

	public XInputAxes getAxes() {
	    return getComponents().getAxes();
	}
    public XInputButtons getButtons() {
        return getComponents().getButtons();
    }

	// Gamepad test
	public static void main(String[] args) throws XInputNotLoadedException {
		Gamepad pad = new Gamepad(0);
		while(true) {

			System.out.println("AXIS: " + pad.getComponents().getAxes().ry);

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
